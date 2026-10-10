package com.site21.bittermelon.common.systems.fluid.substance;

import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
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
import static net.minecraft.world.level.block.Block.*;

public class SubstanceFluidBlockEntity extends BlockEntity implements MixtureOwner {
    private static final SubstanceFluid fluid = SUBSTANCE_FLUID.get();
    private static final Direction[] FALLBACK_DIRECTIONS = new Direction[]{
            Direction.UP,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST
    };
    private SubstanceMixture mixture;
    private final Runnable mixtureChangedCallback = this::onMixtureChanged;
    private boolean dirty;
    private int lastSyncedColor;
    private int syncedColor;

    public SubstanceFluidBlockEntity(BlockPos pos, BlockState blockState) {
        super(SUBSTANCE_FLUID_BLOCK_ENTITY.get(), pos, blockState);
        mixture = new SubstanceMixture(mixtureChangedCallback);
        mixture.setTemperature(500);
    }

    public void onMixtureChanged() {
        if (level instanceof ServerLevel serverLevel) {
            markDirty(serverLevel);
        }
    }

    public void markDirty(ServerLevel level) {
        if (isRemoved()) return;
        dirty = true;
        ensureTicking(level);
    }

    private void ensureTicking(ServerLevel level) {
        if (!level.getFluidTicks().hasScheduledTick(worldPosition, fluid)) {
            level.scheduleTick(worldPosition, fluid, fluid.getTickDelay(level));
        }
    }

    public void update(ServerLevel level) {
        if (!dirty) return;
        dirty = false;

        int volume = getVolume();
        if (volume <= 0) {
            level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
            return;
        }

        BlockState state = getBlockState();

        int targetLevel = fluid.getBlockLevel(volume);
        if (targetLevel != state.getValue(SubstanceFluidBlock.LEVEL)) {
            level.setBlock(worldPosition, state.setValue(SubstanceFluidBlock.LEVEL, targetLevel), UPDATE_CLIENTS);
        }

        int color = mixture.getColor();
        if (color != lastSyncedColor) {
            lastSyncedColor = color;
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }

        setChanged();
        ensureTicking(level);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) return;
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
            fluid.spreadTo(level, target, substances);
        }
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.store("mixture", SubstanceMixture.CODEC, mixture);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        input.read("mixture", SubstanceMixture.CODEC).ifPresent(loaded -> {
            mixture.setSubstances(loaded.getSubstances());
            mixture.setTemperature(loaded.getTemperature());
            lastSyncedColor = mixture.getColor();
        });

        if (level != null && level.isClientSide()) {
            int newColor = input.getIntOr("color", syncedColor);
            if (newColor != syncedColor) {
                syncedColor = newColor;
                level.sendBlockUpdated(worldPosition, Blocks.AIR.defaultBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
            }
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", mixture.getColor());
        return tag;
    }

    @Override
    public int getColor() {
        return level != null && level.isClientSide() ? syncedColor : mixture.getColor();
    }

    @Override
    public SubstanceMixture getMixture() {
        return mixture;
    }
}
