package com.site21.bittermelon.common.content.fluids.scp009;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;

public class SCP009Fluid extends WaterFluid {

    @Override
    public boolean isSource(FluidState fluidState) {
        return false;
    }

    @Override
    public int getAmount(FluidState fluidState) {
        return 0;
    }
}
