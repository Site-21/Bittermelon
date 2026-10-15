package com.site21.bittermelon.common.systems.component.heavy;

import com.mojang.serialization.Codec;
import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import net.minecraft.world.item.ItemStack;

import static com.site21.bittermelon.init.neoforge.BitterDataComponents.SUBSTANCE_MIXTURE;

public enum WeightSource {
    NONE,
    FLUID;

    private static final double REFERENCE_MASS_KG = 500.0;
    private static final double MAX_PENALTY = 0.8;

    public static final Codec<WeightSource> CODEC = Codec.STRING.xmap(WeightSource::valueOf, WeightSource::name);

    public static double getPenalty(WeightSource culprit, ItemStack item) {
        return switch (culprit) {
            case NONE -> 0;
            case FLUID -> getFluidPenalty(item);
        };
    }

    private static double getFluidPenalty(ItemStack item) {
        SubstanceMixture mixture = item.get(SUBSTANCE_MIXTURE);
        if (mixture == null) return 0;

        double massKg = mixture.getMass() / 1000.0;
        return Math.min(massKg / REFERENCE_MASS_KG, 1.0) * MAX_PENALTY;
    }
}
