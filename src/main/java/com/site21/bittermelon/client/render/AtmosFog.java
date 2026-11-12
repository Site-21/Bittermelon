package com.site21.bittermelon.client.render;

import com.site21.bittermelon.common.systems.atmosphere.AtmosHandler;
import com.site21.bittermelon.common.systems.atmosphere.AtmosZone;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.site21.bittermelon.util.ColorUtil.mixColors;

public class AtmosFog {
    private static final float MAX_FOG_DENSITY = 0.01f; // Maximum fog density (0.0 - 1.0)
    private static final float DENSITY_THRESHOLD = 100.0f; // Gas amount at which max density is reached
    private static final float BASE_FOG_DISTANCE = 0.25f; // Base fog distance

    public static FogRenderer.Fog getAtmosFog() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return null;

        AtmosZone instance = AtmosHandler.getAtmosInstanceAt(mc.level, mc.player.getOnPos().above());
        if (instance == null) return null;

        float fogDensity = calculateFogDensity(instance);
        float fogStart = BASE_FOG_DISTANCE * (1 - fogDensity);

        Map<Integer, Integer> colors = new HashMap<>();
        for (SubstanceStack stack : instance.getGases()) {
            colors.put(stack.getSubstance().getColor(), stack.getAmount());
        }
        int color = mixColors(colors);

        return new FogRenderer.Fog(fogStart, 2, color);
    }

    private static float calculateFogDensity(@NotNull AtmosZone instance) {
        float weightedAmount = 0;

        for (SubstanceStack stack : instance.getGases()) {
            float amount = stack.getAmount();
            float transparency = 0.9f;
            weightedAmount += amount * (1 - transparency);
        }

        return Math.min(weightedAmount / DENSITY_THRESHOLD, MAX_FOG_DENSITY);
    }
}
