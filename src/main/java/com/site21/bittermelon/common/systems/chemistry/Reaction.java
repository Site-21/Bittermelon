package com.site21.bittermelon.common.systems.chemistry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.common.systems.substance.SubstanceContainer;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Reaction(
        List<Reagent> reagents,
        List<ReactionCondition> conditions,
        int minTemperature,
        int temperatureChange,
        List<ReactionEffect> effects,
        List<ReactionEffect> finishEffects
) {
    public static final Codec<Reaction> CODEC;

    public boolean canOccur(SubstanceContainer substanceContainer, float temperature, Level level, BlockPos pos) {
        if (temperature < minTemperature) return false;

        for (ReactionCondition condition : conditions) {
            if (!condition.test(substanceContainer, level, pos)) {
                return false;
            }
        }

        for (Reagent reagent : reagents) {
            boolean found = false;
            for (SubstanceStack stack : substanceContainer.getSubstances()) {
                if (reagent.matches(stack)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        return true;
    }

    public int getReactionRate(SubstanceContainer substanceContainer, Level level, BlockPos pos) {
        return 100;
    }

    public boolean react(SubstanceContainer substanceContainer, Level level, BlockPos pos) {
        Map<SubstanceStack, Reagent> reactants = new HashMap<>();
        int scaledAmount = getReactionRate(substanceContainer, level, pos);

        for (Reagent reagent : reagents) {
            boolean found = false;
            for (SubstanceStack stack : substanceContainer.getSubstances()) {
                if (reagent.matches(stack)) {
                    reactants.put(stack, reagent);
                    if (reagent.proportion() > 0) {
                        scaledAmount = Math.min(scaledAmount, stack.getAmount() / reagent.proportion());
                    }
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        if (scaledAmount <= 0) return false;

        for (var entry : reactants.entrySet()) {
            substanceContainer.removeSubstanceNoUpdate(entry.getKey(), scaledAmount * entry.getValue().proportion());
        }

        for (ReactionEffect effect : effects) {
            effect.apply(substanceContainer, level, pos, scaledAmount);
        }

        substanceContainer.refresh();

        boolean fullyConsumed = true;
        for (var entry : reactants.entrySet()) {
            if (entry.getKey().getAmount() > 0) {
                fullyConsumed = false;
                break;
            }
        }

        if (fullyConsumed) {
            finish(substanceContainer, level, pos);
        }

        return true;
    }

    public void finish(SubstanceContainer substanceContainer, Level level, BlockPos pos) {
        for (ReactionEffect effect : finishEffects) {
            effect.apply(substanceContainer, level, pos, 0);
        }
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Reagent.CODEC.listOf()
                        .fieldOf("reagents")
                        .forGetter(Reaction::reagents),
                ReactionCondition.CODEC.listOf()
                        .fieldOf("conditions")
                        .forGetter(Reaction::conditions),
                Codec.INT
                        .fieldOf("min_temperature")
                        .forGetter(Reaction::minTemperature),
                Codec.INT
                        .fieldOf("temperature_change")
                        .forGetter(Reaction::temperatureChange),
                ReactionEffect.CODEC.listOf()
                        .fieldOf("effects")
                        .forGetter(Reaction::effects),
                ReactionEffect.CODEC.listOf()
                        .fieldOf("finish_effects")
                        .forGetter(Reaction::finishEffects)
        ).apply(instance, Reaction::new));
    }
}
