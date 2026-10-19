package com.site21.bittermelon.common.systems.chemistry;

import com.site21.bittermelon.common.systems.chemistry.effects.SynthesisEffect;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.init.custom.Substances;

import java.util.List;

public class Reactions {
    public static void register() {
        ReactionManager reactions = ReactionManager.getInstance();

        twoWay(reactions, Substances.ICE.get(), Substances.WATER.get(), 273.15f, 6f, 1);
        twoWay(reactions, Substances.WATER.get(), Substances.STEAM.get(), 373.15f, 40f, 1);
    }

    private static void twoWay(ReactionManager reactions, Substance low, Substance high, float point, float latentHeat, int rate) {
        reactions.register(convert(low, high, point, Float.POSITIVE_INFINITY, -latentHeat, rate));
        reactions.register(convert(high, low, 0f, point, latentHeat, rate));
    }

    private static Reaction convert(Substance from, Substance to, float minTemp, float maxTemp, float tempChange, int rate) {
        Reagent reagent = Reagent.builder()
                .addSubstanceRequirement(from)
                .proportion(1)
                .build();

        return new Reaction(
                List.of(reagent),
                List.of(),
                minTemp,
                maxTemp,
                tempChange,
                List.of(new SynthesisEffect().addProduct(to.builtInRegistryHolder(), 1)),
                List.of()
        );
    }
}
