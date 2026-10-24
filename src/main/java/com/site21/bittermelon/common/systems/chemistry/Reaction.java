package com.site21.bittermelon.common.systems.chemistry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.common.systems.substance.SubstanceContainer;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Reaction(
        List<Reagent> reagents,
        List<ReactionCondition> conditions,
        float minTemperature,
        float maxTemperature,
        float temperatureChange,
        float rate,
        List<ReactionEffect> effects,
        List<ReactionEffect> finishEffects
) {
    public static final Codec<Reaction> CODEC;
    private static final float KILOJOULES_TO_JOULES = 1000;
    private static final int UNBOUNDED_EXTENT = 100;

    public boolean canOccur(SubstanceContainer substanceContainer, float temperature, Level level, BlockPos pos) {
        if (temperature < minTemperature || temperature >= maxTemperature) return false;

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

    public int getReactionRate(SubstanceContainer substanceContainer, Level level, BlockPos pos, int maxExtent) {
        if (maxExtent == Integer.MAX_VALUE) return UNBOUNDED_EXTENT;
        return Math.min(maxExtent, Mth.ceil(maxExtent * rate));
    }

    private int limitByHeat(int extent, float capacity, float temperature) {
        if (temperatureChange == 0 || capacity <= 0) return extent;

        float headroom;
        if (temperatureChange > 0) {
            if (maxTemperature == Float.MAX_VALUE) return extent;
            headroom = maxTemperature - temperature;
        } else {
            headroom = temperature - minTemperature;
        }

        float affordable = headroom * capacity / (Math.abs(temperatureChange) * KILOJOULES_TO_JOULES);
        return Math.min(extent, Mth.floor(affordable));
    }

    public boolean react(SubstanceContainer substanceContainer, Level level, BlockPos pos) {
        if (!canOccur(substanceContainer, substanceContainer.getTemperature(), level, pos)) return false;
        Map<SubstanceStack, Reagent> reactants = new HashMap<>();
        int maxExtent = Integer.MAX_VALUE;

        for (Reagent reagent : reagents) {
            boolean found = false;
            for (SubstanceStack stack : substanceContainer.getSubstances()) {
                if (reagent.matches(stack)) {
                    reactants.put(stack, reagent);
                    if (reagent.proportion() > 0) {
                        maxExtent = Math.min(maxExtent, stack.getAmount() / reagent.proportion());
                    }
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        float capacity = substanceContainer.getHeatCapacity();
        int scaledAmount = limitByHeat(
                getReactionRate(substanceContainer, level, pos, maxExtent),
                capacity,
                substanceContainer.getTemperature());

        if (scaledAmount <= 0) return false;

        for (var entry : reactants.entrySet()) {
            substanceContainer.removeSubstanceNoUpdate(entry.getKey(), scaledAmount * entry.getValue().proportion());
        }

        for (ReactionEffect effect : effects) {
            effect.apply(substanceContainer, level, pos, scaledAmount);
        }

        if (temperatureChange != 0 && capacity > 0) {
            substanceContainer.modifyTemperatureNoUpdate(temperatureChange * KILOJOULES_TO_JOULES * scaledAmount / capacity);
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
                        .optionalFieldOf("conditions", List.of())
                        .forGetter(Reaction::conditions),
                Codec.FLOAT
                        .optionalFieldOf("min_temperature", 0.0f)
                        .forGetter(Reaction::minTemperature),
                Codec.FLOAT
                        .optionalFieldOf("max_temperature", Float.MAX_VALUE)
                        .forGetter(Reaction::maxTemperature),
                Codec.FLOAT
                        .optionalFieldOf("temperature_change", 0.0f)
                        .forGetter(Reaction::temperatureChange),
                Codec.FLOAT
                        .optionalFieldOf("rate", 1.0f)
                        .forGetter(Reaction::rate),
                ReactionEffect.CODEC.listOf()
                        .optionalFieldOf("effects", List.of())
                        .forGetter(Reaction::effects),
                ReactionEffect.CODEC.listOf()
                        .optionalFieldOf("finish_effects", List.of())
                        .forGetter(Reaction::finishEffects)
        ).apply(instance, Reaction::new));
    }
}
