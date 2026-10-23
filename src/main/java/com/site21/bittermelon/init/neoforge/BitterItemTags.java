package com.site21.bittermelon.init.neoforge;

import com.site21.bittermelon.Bittermelon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;


public class BitterItemTags {
    public static final TagKey<Item> BASEBALL = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "baseball")
    );

    public static final TagKey<Item> SCP = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "scp")
    );

    public static final TagKey<Item> LIGHTER = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "lighter")
    );

    public static final TagKey<Item> WIRE_CUTTER = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "wire_cutter")
    );

    public static final TagKey<Item> INVENTORY_EXCLUDED = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "inventory_excluded")
    );
}
