package com.site21.bittermelon.common.systems.fluid.substance;

import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.site21.bittermelon.init.neoforge.BitterBlockEntities.SUBSTANCE_FLUID_BLOCK_ENTITY;
import static com.site21.bittermelon.init.neoforge.BitterFluids.SUBSTANCE_FLUID;
import static net.minecraft.world.level.block.Block.UPDATE_ALL;
import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;

public class SubstanceFluidBlockEntity extends BlockEntity implements MixtureOwner {
    private static final Direction[] FALLBACK_DIRECTIONS = new Direction[]{
            Direction.UP,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST
    };
    private SubstanceMixture mixture;
    private final Runnable mixtureChangedCallback = () -> {
        setChanged();
        updateFluidState();
    };

    public SubstanceFluidBlockEntity(BlockPos pos, BlockState blockState) {
        super(SUBSTANCE_FLUID_BLOCK_ENTITY.get(), pos, blockState);
        mixture = new SubstanceMixture(mixtureChangedCallback);
        mixture.setTemperature(500);
    }

    public void updateFluidState() {
        if (level == null) return;
        if (level.getBlockState(worldPosition).isAir()) return;

        Profiler.get().push("updateFluidState");

        int fluidLevel = Math.max(1, Mth.clamp(getVolume() / 50, 1, 19));
        int currentFluidLevel = level.getFluidState(worldPosition).getAmount();
        if (currentFluidLevel != 20 && currentFluidLevel != fluidLevel) {
            BlockState currentState = level.getBlockState(worldPosition);
            BlockState newState = currentState.setValue(SubstanceFluidBlock.LEVEL, fluidLevel);

            level.setBlock(worldPosition, newState, UPDATE_ALL);
        }

        level.scheduleTick(worldPosition, SUBSTANCE_FLUID.get(), SUBSTANCE_FLUID.get().getTickDelay(level));

        Profiler.get().pop();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level == null) return;
        displace(level, pos);
    }

    private void displace(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();

        List<BlockPos> targets = new ArrayList<>(4);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            currentPos.setWithOffset(pos, direction);
            if (level.getBlockState(currentPos).canBeReplaced()
                    && level.getBlockEntity(currentPos) instanceof SubstanceFluidBlockEntity be
                    && be.getVolume() < SubstanceFluid.FULL_BLOCK_VOLUME) {
                targets.add(currentPos.immutable());
            }
        }

        if (targets.isEmpty()) {
            for (Direction direction : FALLBACK_DIRECTIONS) {
                currentPos.setWithOffset(pos, direction);
                if (level.getBlockState(currentPos).canBeReplaced()) {
                    targets.add(currentPos.immutable());
                }
            }
        }

        if (targets.isEmpty()) return;

        List<SubstanceStack> substances = mixture.splitSubstances(targets.size(), 1);
        if (substances.isEmpty()) return;

        for (BlockPos target : targets) {
            SUBSTANCE_FLUID.get().spreadTo(level, target, substances);
        }
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);

        output.store("mixture", SubstanceMixture.CODEC, mixture);
        output.putInt("cachedColor", mixture.getColor());
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        input.read("mixture", SubstanceMixture.CODEC).ifPresent(loaded -> {
            int oldColor = this.mixture.getColor();
            loaded.setChangedCallback(mixtureChangedCallback);
            this.mixture = loaded;
            int newColor = loaded.getColor();

            if (level != null && level.isClientSide()) {
                if (oldColor != newColor) {
                    level.sendBlockUpdated(worldPosition, Blocks.AIR.defaultBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
                }
            }
        });
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), UPDATE_CLIENTS);
        }
    }

    @Override
    public SubstanceMixture getMixture() {
        return mixture;
    }
}
