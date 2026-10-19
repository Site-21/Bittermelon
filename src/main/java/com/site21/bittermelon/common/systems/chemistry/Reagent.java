package com.site21.bittermelon.common.systems.chemistry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.common.systems.substance.Nature;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record Reagent(
        int proportion,
        int order,
        EnumMap<Nature, Float> natureRequirements,
        Set<Holder<Substance>> substanceRequirements
) {
    public static final Codec<Reagent> CODEC;

    boolean matches(SubstanceStack stack) {
        Substance substance = stack.getSubstance();

        for (Holder<Substance> requiredSubstance : substanceRequirements) {
            if (requiredSubstance.value().equals(substance)) return true;
        }

        if (natureRequirements.isEmpty() && !substanceRequirements.isEmpty()) return false;

        EnumMap<Nature, Float> nature = substance.getNature();
        for (var entry : natureRequirements.entrySet()) {
            if (nature.getOrDefault(entry.getKey(), -1f) < entry.getValue()) return false;
        }

        return true;
    }

    public List<Holder<Substance>> getSubstanceRequirements() {
        return substanceRequirements.stream().toList();
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull Reagent deserialize(int proportion, int order, Map<Nature, Float> natureRequirements,
                                               List<Holder<Substance>> substanceRequirements) {
        EnumMap<Nature, Float> natures = new EnumMap<>(Nature.class);
        natures.putAll(natureRequirements);
        return new Reagent(proportion, order, natures, new HashSet<>(substanceRequirements));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int proportion = 1;
        private int order = 0;
        private final EnumMap<Nature, Float> natureRequirements = new EnumMap<>(Nature.class);
        private final Set<Holder<Substance>> substanceRequirements = new HashSet<>();

        public Builder proportion(int proportion) {
            this.proportion = proportion;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder addNatureRequirement(Nature nature, float amount) {
            natureRequirements.put(nature, amount);
            return this;
        }

        public Builder addSubstanceRequirement(Substance substance) {
            substanceRequirements.add(substance.builtInRegistryHolder());
            return this;
        }

        public Reagent build() {
            return new Reagent(proportion, order, natureRequirements, substanceRequirements);
        }
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT
                        .fieldOf("proportion")
                        .forGetter(Reagent::proportion),
                Codec.INT
                        .fieldOf("order")
                        .forGetter(Reagent::order),
                Codec.unboundedMap(Nature.CODEC, Codec.FLOAT)
                        .fieldOf("nature_requirements")
                        .forGetter(Reagent::natureRequirements),
                Substance.CODEC.listOf()
                        .fieldOf("substance_requirements")
                        .forGetter(Reagent::getSubstanceRequirements)
        ).apply(instance, Reagent::deserialize));
    }
}
