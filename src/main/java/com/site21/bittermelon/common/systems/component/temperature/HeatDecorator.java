package com.site21.bittermelon.common.systems.component.temperature;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import org.jetbrains.annotations.NotNull;

import static com.site21.bittermelon.init.neoforge.BitterDataComponents.BURN_TIME;
import static com.site21.bittermelon.init.neoforge.BitterDataComponents.TEMPERATURE;

public class HeatDecorator implements IItemDecorator {
    @Override
    public boolean render(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
        int temperature = stack.getOrDefault(TEMPERATURE, 0);
        if (temperature <= 273) return false;

        int minTemp = 273;
        int maxTemp = 506;
        float intensity = Mth.clamp((float) (temperature - minTemp) / (maxTemp - minTemp), 0f, 1f);

        int red = (int) (255 * intensity);
        int color = 0x55000000 | (red << 16);

        graphics.fill(RenderPipelines.GUI, x, y, x + 16, y + 16, color);

        if (stack.get(BURN_TIME) != null) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("textures/block/fire_0.png"), x, y, 0, 16, 16, 16, 16, 16);
        }

        return true;
    }
}
