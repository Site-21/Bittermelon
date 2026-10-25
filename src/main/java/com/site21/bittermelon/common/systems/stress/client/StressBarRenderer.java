package com.site21.bittermelon.common.systems.stress.client;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.stress.StressUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.STRESS;
import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.STRESS_RELIEF;

public class StressBarRenderer implements ContextualBarRenderer {
    private static final Identifier STRESS_BAR_BACKGROUND = Bittermelon.identifier("hud/stress_bar_background");
    private static final Identifier STRESS_BAR_PROGRESS = Bittermelon.identifier("hud/stress_bar_progress");
    private final Minecraft minecraft;

    public StressBarRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static boolean shouldShow(Player player) {
        return player.getData(STRESS) > 0;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        LocalPlayer player = minecraft.player;
        int x = left(minecraft.getWindow());
        int y = top(minecraft.getWindow());
        int stress = player.getData(STRESS);
        int level = stress / 100;
        int width;

        if (level >= StressUtil.MAX_STRESS || stress % 100 == 0) {
            width = 182;
        } else {
            width = (stress % 100) * 182 / 100;
        }

        int shakeOffset = 0;
        if (level >= 1) {
            float time = (System.currentTimeMillis() % 1000) / 1000f;
            shakeOffset = (int) (Math.sin(time * Math.PI * 10) * level / 2);
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STRESS_BAR_BACKGROUND, x + shakeOffset, y, 182, 5);

        if (width > 0) {
            int color = ARGB.color(1 - player.getData(STRESS_RELIEF), 0xFFFFFF);

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    getStressBar(level),
                    182, 5,
                    0, 0,
                    x + shakeOffset, y,
                    width, 5,
                    color
            );
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        LocalPlayer player = minecraft.player;
        int level = player.getData(STRESS) / 100;
        Component component = Component.literal("!".repeat(level));
        int x = (graphics.guiWidth() - minecraft.font.width(component)) / 2;
        int y = graphics.guiHeight() - 24 - 9 - 2;
        int color = ARGB.color(1 - player.getData(STRESS_RELIEF), 0xFFFFFF);
        graphics.text(minecraft.font, component, x, y, color, true);
    }

    @Contract("_ -> new")
    private @NotNull Identifier getStressBar(int level) {
        return level < 1 ? STRESS_BAR_PROGRESS : Bittermelon.identifier("hud/stress_bar_progress_" + level);
    }
}
