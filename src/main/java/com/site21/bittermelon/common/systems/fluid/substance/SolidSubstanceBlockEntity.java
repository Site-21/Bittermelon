package com.site21.bittermelon.common.systems.fluid.substance;

import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import static com.site21.bittermelon.init.neoforge.BitterBlockEntities.SOLID_SUBSTANCE_BLOCK_ENTITY;

public class SolidSubstanceBlockEntity extends BlockEntity implements MixtureOwner {
    private final SubstanceMixture mixture;
    private int syncedColor;

    public SolidSubstanceBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(SOLID_SUBSTANCE_BLOCK_ENTITY.get(), worldPosition, blockState);
        mixture = new SubstanceMixture();
    }

    public SubstanceMixture getMixture() {
        return mixture;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("mixture", SubstanceMixture.CODEC, mixture);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("mixture", SubstanceMixture.CODEC).ifPresent(loaded -> {
            mixture.setSubstances(loaded.getSubstances());
            mixture.setTemperature(loaded.getTemperature());
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
}
