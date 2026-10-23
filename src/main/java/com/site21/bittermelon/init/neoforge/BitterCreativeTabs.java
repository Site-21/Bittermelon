package com.site21.bittermelon.init.neoforge;

import com.site21.bittermelon.Bittermelon;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.site21.bittermelon.init.neoforge.BitterItems.*;
import static net.minecraft.core.component.DataComponents.BASE_COLOR;
import static net.minecraft.core.component.DataComponents.ITEM_NAME;

public class BitterCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Bittermelon.MOD_ID);

    public static final Supplier<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () ->
            CreativeModeTab.builder()
                    .title(Component.literal("Bittermelon"))
                    .icon(() -> new ItemStack(BitterItems.BITTERMELON.get()))
                    .displayItems((_, output) -> {
                        for (DeferredHolder<Item, ? extends Item> item : BitterItems.ITEMS.getEntries()) {
                            if (item.is(BitterItemTags.INVENTORY_EXCLUDED)) continue;
                            if (item.is(BitterItemTags.SCP)) continue;
                            output.accept(item.get());
                        }

                        for (DyeColor color : DyeColor.values()) {
                            output.accept(createDyedItem(CHALK.toStack(), color, "Chalk"));
                            output.accept(createDyedItem(HIGHLIGHTER.toStack(), color, "Highlighter"));
                        }
                    })
                    .build()
    );

    public static final Supplier<CreativeModeTab> SCP_TAB = CREATIVE_MODE_TABS.register("scp_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.literal("SCPs"))
                    .icon(() -> new ItemStack(SCP_377.get()))
                    .displayItems((parameters, output) -> {
                        for (DeferredHolder<Item, ? extends Item> item : BitterItems.ITEMS.getEntries()) {
                            if (item.is(BitterItemTags.SCP)) {
                                output.accept(item.get());
                            }
                        }
                    })
                    .build()
    );

    @Contract("_, _, _ -> param1")
    private static @NotNull ItemStack createDyedItem(@NotNull ItemStack stack, DyeColor color, String nameSuffix) {
        stack.set(BASE_COLOR, color);
        stack.set(ITEM_NAME, Component.literal(WordUtils.capitalize(color.getName().replace("_", " ")) + " " + nameSuffix));
        return stack;
    }
}
