package com.site21.bittermelon.common.systems.fluid.substance;

import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static com.site21.bittermelon.init.neoforge.BitterBlockEntities.SOLID_SUBSTANCE_BLOCK_ENTITY;

public class SolidSubstanceBlockEntity extends BlockEntity implements MixtureOwner {
    private final SubstanceMixture mixture;

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
    }
}
