package com.site21.bittermelon.common.systems.fluid.substance.client;

import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlockEntity;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.fluid.FluidTintSource;

public class SubstanceFluidTintSource implements FluidTintSource {
    @Override
    public int color(FluidState state) {
        return 0xFF3F76E4;
    }

    @Override
    public int color(BlockState state) {
        return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE) {
            return fluidBE.getColor();
        }

        return FluidTintSource.super.colorInWorld(state, level, pos);
    }

    @Override
    public int colorInWorld(FluidState fluidState, BlockState blockState, BlockAndTintGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE) {
            return fluidBE.getColor();
        }

        return FluidTintSource.super.colorInWorld(fluidState, blockState, level, pos);
    }
}
