package com.site21.bittermelon.init.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import com.site21.bittermelon.Bittermelon;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Bittermelon.MOD_ID, value = Dist.CLIENT)
public class BitterKeyBindings {
    public static final KeyMapping.Category BITTERMELON_CATEGORY = new KeyMapping.Category(Bittermelon.identifier("category"));

    public static final Lazy<KeyMapping> HEALTH_SCREEN_KEY = Lazy.of(() ->
            new KeyMapping(
                    "Health Screen",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_H,
                    BITTERMELON_CATEGORY
            )
    );

    public static final Lazy<KeyMapping> THROW_ITEM_KEY = Lazy.of(() ->
            new KeyMapping(
                    "Throw Item",
                    KeyConflictContext.IN_GAME,
                    KeyModifier.ALT,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_Q,
                    BITTERMELON_CATEGORY
            )
    );

    public static final Lazy<KeyMapping> CHARACTER_KEY = Lazy.of(() ->
            new KeyMapping(
                    "Character",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_C,
                    BITTERMELON_CATEGORY
            )
    );

    public static final Lazy<KeyMapping> RAGDOLL_KEY = Lazy.of(() ->
            new KeyMapping(
                    "Ragdoll",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    BITTERMELON_CATEGORY
            )
    );

    @SubscribeEvent
    public static void register(@NotNull RegisterKeyMappingsEvent event) {
        event.registerCategory(BITTERMELON_CATEGORY);
        event.register(HEALTH_SCREEN_KEY.get());
        event.register(THROW_ITEM_KEY.get());
        event.register(CHARACTER_KEY.get());
        event.register(RAGDOLL_KEY.get());
    }
}
