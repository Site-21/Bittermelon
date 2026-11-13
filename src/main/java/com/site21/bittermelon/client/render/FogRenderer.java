package com.site21.bittermelon.client.render;

import com.site21.bittermelon.init.neoforge.BitterMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class FogRenderer {
    private static final List<Supplier<Fog>> SOURCES = new ArrayList<>();

    public static void register() {
//        SOURCES.add(AtmosFog::getAtmosFog);
        SOURCES.add(FogRenderer::getAmnesiaFog);
    }

   public static Fog getAmnesiaFog() {
       Minecraft mc = Minecraft.getInstance();
       if (mc.player == null || !mc.player.hasEffect(BitterMobEffects.AMNESIA)) return null;
       int amplifier = Objects.requireNonNull(mc.player.getEffect(BitterMobEffects.AMNESIA)).getAmplifier();
       return new Fog(0, 30f + (Math.max(0, 100f - amplifier * 10f)), 0x555555);
   }

    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Fog fog = combine();
        if (fog == null) return;
        event.setNearPlaneDistance(fog.near());
        event.setFarPlaneDistance(fog.far());
    }

    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Fog fog = combine();
        if (fog == null) return;
        event.setRed(ARGB.red(fog.color()) / 255f);
        event.setGreen(ARGB.green(fog.color()) / 255f);
        event.setBlue(ARGB.blue(fog.color()) / 255f);
    }

    private static Fog combine() {
        float near = 0, far = 0, r = 0, g = 0, b = 0;
        int n = 0;

        for (Supplier<Fog> source : SOURCES) {
            Fog fog = source.get();
            if (fog == null) continue;

            near += fog.near();
            far += fog.far();
            r += ARGB.red(fog.color());
            g += ARGB.green(fog.color());
            b += ARGB.blue(fog.color());
            n++;
        }

        if (n == 0) return null;
        return new Fog(near / n, far / n, ARGB.color(255, Math.round(r / n), Math.round(g / n), Math.round(b / n)));
    }

    public record Fog(float near, float far, int color) {}
}
