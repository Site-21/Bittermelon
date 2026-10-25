package com.site21.bittermelon.common.systems.component.heavy.client;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.component.heavy.JumpCooldown;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class JumpCooldownBarRenderer implements ContextualBarRenderer {
    private static final Identifier JUMP_BAR_BACKGROUND = Bittermelon.identifier("hud/progress_bar_background");
    private static final Identifier JUMP_BAR_COOLDOWN = Bittermelon.identifier("hud/stun_bar_progress");
    private final Minecraft minecraft;

    public JumpCooldownBarRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static boolean shouldShow(Player player) {
        return ((JumpCooldown) player).bittermelon$getJumpCooldown() > 0;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        JumpCooldown cooldown = (JumpCooldown) minecraft.player;
        int x = left(minecraft.getWindow());
        int y = top(minecraft.getWindow());

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, JUMP_BAR_BACKGROUND, x, y, 182, 5);

        int duration = cooldown.bittermelon$getJumpCooldownDuration();
        if (duration <= 0) return;

        float remaining = cooldown.bittermelon$getJumpCooldown() - deltaTracker.getGameTimeDeltaPartialTick(false);
        int width = Mth.lerpDiscrete(Mth.clamp(remaining / duration, 0, 1), 0, 182);
        if (width > 0) {
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    JUMP_BAR_COOLDOWN,
                    182,
                    5,
                    0,
                    0,
                    x,
                    y,
                    width,
                    5
            );
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
    }
}
