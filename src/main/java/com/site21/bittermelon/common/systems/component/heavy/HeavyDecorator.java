package com.site21.bittermelon.common.systems.component.heavy;

import com.site21.bittermelon.Bittermelon;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

import static com.site21.bittermelon.init.neoforge.BitterDataComponents.HEAVY;

public class HeavyDecorator implements IItemDecorator {
    @Override
    public boolean render(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!stack.has(HEAVY)) return false;

        int size = 6;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                Bittermelon.identifier("textures/gui/heavy.png"),
                xOffset + 16 - size,
                yOffset + 16 - size,
                0, 0,
                size, size,
                16, 16,
                16, 16
        );

        return true;
    }
}
