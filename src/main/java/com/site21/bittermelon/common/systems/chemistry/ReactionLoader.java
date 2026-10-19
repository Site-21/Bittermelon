package com.site21.bittermelon.common.systems.chemistry;

import com.site21.bittermelon.Bittermelon;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class ReactionLoader extends SimpleJsonResourceReloadListener<Reaction> {
    public static final ResourceKey<Registry<Reaction>> REACTION_REGISTRY_KEY = ResourceKey.createRegistryKey(Bittermelon.identifier("reactions"));

    public ReactionLoader(HolderLookup.Provider provider) {
        super(provider, Reaction.CODEC, REACTION_REGISTRY_KEY);
    }

    @Override
    protected void apply(Map<Identifier, Reaction> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        ReactionManager manager = ReactionManager.getInstance();
        manager.clear();
        Reactions.register();

        object.forEach((location, reaction) -> {
                    validate(location, reaction);
                    manager.register(reaction);
                }
        );

        Bittermelon.LOGGER.info("Loaded {} reactions", object.size());
    }

    private void validate(Identifier location, Reaction reaction) {
        if (reaction.reagents().isEmpty()) {
            throw new IllegalStateException("Reaction " + location + " must have at least one reagent");
        }
    }
}
