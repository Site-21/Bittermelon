package com.site21.bittermelon.common.systems.fluid.substance.client;

import com.site21.bittermelon.common.systems.fluid.substance.SolidSubstanceBlockEntity;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SolidSubstanceTintSource implements BlockTintSource {
    @Override
    public int color(BlockState blockState) {
        return 0;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SolidSubstanceBlockEntity be) {
            return be.getMixture().getColor();
        }

        return color(state);
    }
}
