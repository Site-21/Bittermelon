package com.site21.bittermelon.common.systems.stumble.client;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.stumble.StumbleHandler;
import com.site21.bittermelon.init.neoforge.BitterMobEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import static com.site21.bittermelon.common.systems.stumble.client.RiseKeyHandler.TICKS_REQUIRED;

public class RiseBarRenderer implements ContextualBarRenderer {
    private static final Identifier PROGRESS_BAR_BACKGROUND = Bittermelon.identifier("hud/progress_bar_background");
    private static final Identifier PROGRESS_BAR_PROGRESS = Bittermelon.identifier("hud/progress_bar_progress");
    private static final Identifier STUN_BAR_PROGRESS = Bittermelon.identifier("hud/stun_bar_progress");

    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final int MAX_STUN_TIME = 40;
    private final Minecraft minecraft;

    public RiseBarRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static boolean shouldShow(Player player) {
        return StumbleHandler.isStumbled(player) && (RiseKeyHandler.isKeyPressed() || StumbleHandler.isStunned(player));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Player player = minecraft.player;
        if (player == null) return;

        int x = left(minecraft.getWindow());
        int y = top(minecraft.getWindow());

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_BAR_BACKGROUND, x, y, BAR_WIDTH, BAR_HEIGHT);

        if (RiseKeyHandler.isKeyPressed()) {
            float ticksHeld = RiseKeyHandler.getTicksHeld();
            if (ticksHeld <= 0) return;

            int width = Math.min((int) ((ticksHeld / (TICKS_REQUIRED - 2)) * BAR_WIDTH), BAR_WIDTH);
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    PROGRESS_BAR_PROGRESS,
                    BAR_WIDTH,
                    BAR_HEIGHT,
                    0,
                    0,
                    x,
                    y,
                    width,
                    BAR_HEIGHT
            );
        } else {
            MobEffectInstance stunEffect = player.getEffect(BitterMobEffects.STUN);
            if (stunEffect == null) return;

            int width = Math.min((int) ((stunEffect.getDuration() / (float) MAX_STUN_TIME) * BAR_WIDTH), BAR_WIDTH);
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    STUN_BAR_PROGRESS,
                    BAR_WIDTH,
                    BAR_HEIGHT,
                    0,
                    0,
                    x,
                    y,
                    width,
                    BAR_HEIGHT
            );
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
    }
}
