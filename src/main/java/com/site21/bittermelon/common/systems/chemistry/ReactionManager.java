package com.site21.bittermelon.common.systems.chemistry;

import com.site21.bittermelon.common.systems.substance.Nature;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.common.systems.substance.SubstanceContainer;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.Level;

import java.util.*;

public class ReactionManager {
    private static ReactionManager instance;
    private final EnumMap<Nature, Set<Reaction>> natureToReactions = new EnumMap<>(Nature.class);
    private final Map<Substance, Set<Reaction>> substanceToReactions = new HashMap<>();

    public static ReactionManager getInstance() {
        if (instance == null) instance = new ReactionManager();
        return instance;
    }

    public void register(Reaction reaction) {
        for (Reagent reagent : reaction.reagents()) {
            for (var entry : reagent.natureRequirements().entrySet()) {
                natureToReactions
                        .computeIfAbsent(entry.getKey(), _ -> new HashSet<>())
                        .add(reaction);
            }

            for (Holder<Substance> holder : reagent.substanceRequirements()) {
                substanceToReactions
                        .computeIfAbsent(holder.value(), _ -> new HashSet<>())
                        .add(reaction);
            }
        }
    }

    public void clear() {
        natureToReactions.clear();
        substanceToReactions.clear();
    }

    public Set<Reaction> findMatch(SubstanceContainer substanceContainer, Level level, BlockPos pos) {
        Profiler.get().push("findMatch");

        Set<Reaction> candidates = new HashSet<>();

        for (Nature nature : substanceContainer.getNatures()) {
            Set<Reaction> indexed = natureToReactions.get(nature);
            if (indexed != null) candidates.addAll(indexed);
        }

        for (SubstanceStack stack : substanceContainer.getSubstances()) {
            Set<Reaction> indexed = substanceToReactions.get(stack.getSubstance());
            if (indexed != null) candidates.addAll(indexed);
        }

        Set<Reaction> result = new HashSet<>();
        for (Reaction reaction : candidates) {
            if (reaction.canOccur(substanceContainer, substanceContainer.getTemperature(), level, pos)) result.add(reaction);
        }

        Profiler.get().pop();
        return result;
    }
}
