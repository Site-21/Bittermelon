package com.site21.bittermelon.common.systems.fluid.substance;

import com.site21.bittermelon.init.neoforge.BitterFluidTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class SubstanceFluidType extends FluidType {
    public SubstanceFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public double motionScale(@NotNull Entity entity) {
        // TODO: Doesn't work


        return super.motionScale(entity);
    }

    @Override
    public boolean canSwim(@NotNull Entity entity) {
        if (entity.getFluidHeight(BitterFluidTypes.SUBSTANCE_FLUID_TYPE.get()) <= 0.4) {
            return false;
        }

        return super.canSwim(entity);
    }

    @Override
    public int getViscosity(FluidState state, BlockAndLightGetter getter, BlockPos pos) {
        if (getter.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE) {
            return fluidBE.getViscosity();
        }

        return super.getViscosity(state, getter, pos);
    }
}
