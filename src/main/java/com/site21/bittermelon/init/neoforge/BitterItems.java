package com.site21.bittermelon.init.neoforge;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.items.*;
import com.site21.bittermelon.common.content.items.clownhammer.ClownHammer;
import com.site21.bittermelon.common.content.items.gun.Handgun;
import com.site21.bittermelon.common.content.items.handheldsysteminterface.HandheldSystemInterface;
import com.site21.bittermelon.common.content.items.laserdesignator.LaserDesignatorItem;
import com.site21.bittermelon.common.content.items.medical.tools.SyringeItem;
import com.site21.bittermelon.common.content.items.mop.MopItem;
import com.site21.bittermelon.common.content.items.repairtool.RepairToolItem;
import com.site21.bittermelon.common.content.items.scps.SCP005Item;
import com.site21.bittermelon.common.content.items.scps.SCP109Item;
import com.site21.bittermelon.common.content.items.scps.scp1079.SCP1079CandyItem;
import com.site21.bittermelon.common.content.items.scps.scp1079.SCP1079Item;
import com.site21.bittermelon.common.content.items.scps.scp377.SCP3771Item;
import com.site21.bittermelon.common.content.items.scps.scp377.SCP377CookieItem;
import com.site21.bittermelon.common.content.items.scps.scp377.SCP377Item;
import com.site21.bittermelon.common.content.items.scps.scp815.SCP815Item;
import com.site21.bittermelon.common.content.items.scps.scp815.SCP815SnakeHandItem;
import com.site21.bittermelon.common.content.items.screwdriver.ScrewdriverItem;
import com.site21.bittermelon.common.content.items.smokable.SmokableItem;
import com.site21.bittermelon.common.content.items.substance.FluidContainerItem;
import com.site21.bittermelon.common.content.items.substance.GasContainerItem;
import com.site21.bittermelon.common.content.items.substance.GlassFluidContainerItem;
import com.site21.bittermelon.common.content.items.substance.PowderedSubstanceItem;
import com.site21.bittermelon.common.content.items.substance.pill.PillItem;
import com.site21.bittermelon.common.content.items.substance.pill.PillShape;
import com.site21.bittermelon.common.content.items.taser.TaserItem;
import com.site21.bittermelon.common.content.items.wire.WireItem;
import com.site21.bittermelon.common.content.items.wirecutters.WireCuttersItem;
import com.site21.bittermelon.common.content.items.writablepaper.WritablePaper;
import com.site21.bittermelon.common.content.items.writingutensils.ChalkItem;
import com.site21.bittermelon.common.content.items.writingutensils.HighlighterItem;
import com.site21.bittermelon.common.systems.component.Smokable;
import com.site21.bittermelon.common.systems.component.medical.Retractor;
import com.site21.bittermelon.common.systems.component.medical.Scalpel;
import com.site21.bittermelon.common.systems.component.medical.Suture;
import com.site21.bittermelon.common.systems.component.screwdriver.Screwdriver;
import com.site21.bittermelon.common.systems.component.temperature.HeatBehavior;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static com.site21.bittermelon.init.neoforge.BitterDataComponents.*;

public class BitterItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Bittermelon.MOD_ID);

    // Block Items
    public static final DeferredItem<BlockItem> SMALL_CARDBOARD_BOX = ITEMS.registerSimpleBlockItem(BitterBlocks.SMALL_CARDBOARD_BOX);
    public static final DeferredItem<BlockItem> ATM = ITEMS.registerSimpleBlockItem(BitterBlocks.ATM);
    public static final DeferredItem<BlockItem> CONTAINMENT_PANEL = ITEMS.registerSimpleBlockItem(BitterBlocks.CONTAINMENT_PANEL);
    public static final DeferredItem<BlockItem> THERMOMETER = ITEMS.registerSimpleBlockItem(BitterBlocks.THERMOMETER);
    public static final DeferredItem<BlockItem> INTERCOM = ITEMS.registerSimpleBlockItem(BitterBlocks.INTERCOM);
    public static final DeferredItem<BlockItem> ENVIRONMENT_SENSOR = ITEMS.registerSimpleBlockItem(BitterBlocks.ENVIRONMENT_SENSOR);
    public static final DeferredItem<BlockItem> CONTAINMENT_ALARM = ITEMS.registerSimpleBlockItem(BitterBlocks.CONTAINMENT_ALARM);
    public static final DeferredItem<BlockItem> DETONATOR = ITEMS.registerSimpleBlockItem(BitterBlocks.DETONATOR);
    public static final DeferredItem<BlockItem> SPEAKER = ITEMS.registerSimpleBlockItem(BitterBlocks.SPEAKER);
    public static final DeferredItem<BlockItem> SECURE_DOOR = ITEMS.registerSimpleBlockItem(BitterBlocks.SECURE_DOOR);
    public static final DeferredItem<BlockItem> KEYCARD_READER_SECURE_DOOR = ITEMS.registerSimpleBlockItem(BitterBlocks.KEYCARD_READER_SECURE_DOOR);
    public static final DeferredItem<BlockItem> LARGE_SLIDING_DOOR = ITEMS.registerSimpleBlockItem(BitterBlocks.LARGE_SLIDING_DOOR);
    public static final DeferredItem<BlockItem> YELLOW_INSPECTION_POSTER = ITEMS.registerSimpleBlockItem(BitterBlocks.YELLOW_INSPECTION_POSTER);
    public static final DeferredItem<BlockItem> DISTRIBUTION_BOARD = ITEMS.registerSimpleBlockItem(BitterBlocks.DISTRIBUTION_BOARD);

    public static final DeferredItem<BlockItem> SCP_151 = ITEMS.registerSimpleBlockItem(
            BitterBlocks.SCP_151,
            () -> new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(
                                    Component.literal("The Painting")
                                            .withStyle(ChatFormatting.ITALIC)
                                            .withStyle(ChatFormatting.GRAY),
                                    Component.literal("Euclid")
                                            .withStyle(ChatFormatting.GOLD)
                            ))
                    )
    );

    public static final DeferredItem<BlockItem> PERSONNEL_TERMINAL = ITEMS.registerSimpleBlockItem(BitterBlocks.PERSONNEL_TERMINAL);
    public static final DeferredItem<BlockItem> KEYCARD_PRINTER = ITEMS.registerSimpleBlockItem(BitterBlocks.KEYCARD_PRINTER);
    public static final DeferredItem<BlockItem> WINDOWED_SLIDING_DOOR = ITEMS.registerSimpleBlockItem(BitterBlocks.WINDOWED_SLIDING_DOOR);
    public static final DeferredItem<BlockItem> KEYCARD_READER = ITEMS.registerSimpleBlockItem(BitterBlocks.KEYCARD_READER);
    public static final DeferredItem<BlockItem> REDSTONE_DEVICE = ITEMS.registerSimpleBlockItem(BitterBlocks.REDSTONE_DEVICE);
    public static final DeferredItem<BlockItem> EMERGENCY_EXIT_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.EMERGENCY_EXIT_LAMP);
    public static final DeferredItem<BlockItem> RED_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.RED_CAGE_LAMP);
    public static final DeferredItem<BlockItem> BLUE_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.BLUE_CAGE_LAMP);
    public static final DeferredItem<BlockItem> GREEN_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.GREEN_CAGE_LAMP);
    public static final DeferredItem<BlockItem> ORANGE_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.ORANGE_CAGE_LAMP);
    public static final DeferredItem<BlockItem> YELLOW_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.YELLOW_CAGE_LAMP);
    public static final DeferredItem<BlockItem> PURPLE_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.PURPLE_CAGE_LAMP);
    public static final DeferredItem<BlockItem> LIME_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.LIME_CAGE_LAMP);
    public static final DeferredItem<BlockItem> PINK_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.PINK_CAGE_LAMP);
    public static final DeferredItem<BlockItem> MAGENTA_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.MAGENTA_CAGE_LAMP);
    public static final DeferredItem<BlockItem> CYAN_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.CYAN_CAGE_LAMP);
    public static final DeferredItem<BlockItem> LIGHT_BLUE_CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.LIGHT_BLUE_CAGE_LAMP);
    public static final DeferredItem<BlockItem> CAGE_LAMP = ITEMS.registerSimpleBlockItem(BitterBlocks.CAGE_LAMP);
    public static final DeferredItem<BlockItem> LIGHT_GRAY_TELEVISION = ITEMS.register("light_gray_television", registryName ->
            new StandingAndWallBlockItem(BitterBlocks.LIGHT_GRAY_TELEVISION.get(), BitterBlocks.LIGHT_GRAY_WALL_TELEVISION.get(), Direction.DOWN,
                    new Item.Properties().setId(ResourceKey.create(Registries.ITEM, registryName))));
    public static final DeferredItem<BlockItem> PLASTIC_FLAMINGO = ITEMS.registerSimpleBlockItem(BitterBlocks.PLASTIC_FLAMINGO);

    public static final DeferredItem<BlockItem> SCP_330 = ITEMS.registerSimpleBlockItem(
            BitterBlocks.SCP_330,
            () -> new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(
                                    Component.literal("Take Only Two")
                                            .withStyle(ChatFormatting.ITALIC)
                                            .withStyle(ChatFormatting.GRAY),
                                    Component.literal("Safe")
                                            .withStyle(ChatFormatting.GREEN)
                            ))
                    )
    );

    public static final DeferredItem<BlockItem> CAGE = ITEMS.registerSimpleBlockItem(BitterBlocks.CAGE);
    public static final DeferredItem<BlockItem> EYEBALL_BLISTER = ITEMS.registerSimpleBlockItem(BitterBlocks.EYEBALL_BLISTER);
    public static final DeferredItem<BlockItem> BLACK_WOODEN_SEAT = ITEMS.registerSimpleBlockItem(BitterBlocks.BLACK_WOODEN_SEAT);
    public static final DeferredItem<BlockItem> METAL_BARREL = ITEMS.registerSimpleBlockItem(BitterBlocks.METAL_DRUM);

    public static final DeferredItem<FluidContainerItem> BEER_BOTTLE = ITEMS.register("beer_bottle", registryName ->
            new GlassFluidContainerItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(VOLUME, 75)));

    public static final DeferredItem<FluidContainerItem> WHISKEY_BOTTLE = ITEMS.register("whiskey_bottle", registryName ->
            new FluidContainerItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(VOLUME, 100)));

    public static final DeferredItem<SCP109Item> SCP_109 = ITEMS.register("scp_109", registryName ->
            new SCP109Item(new Item.Properties()
                    .stacksTo(1)
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(HIDE_BAR, true)
                    .component(VOLUME, 0)
                    .component(DataComponents.LORE, new ItemLore(List.of(Component.literal("Infinite Canteen").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY), Component.literal("Euclid").withStyle(ChatFormatting.GOLD)))
                    )));

    public static final DeferredItem<SmokableItem> CIGARETTE = ITEMS.register("cigarette", registryName ->
            new SmokableItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(SMOKABLE, Smokable.DEFAULT)
                    .equippable(EquipmentSlot.HEAD)
                    .component(VOLUME, 20)));


    //SPAWN EGGS
    public static final DeferredItem<Item> SCP_650_SPAWN_EGG = ITEMS.registerItem(
            "scp_650_spawn_egg",
            properties -> new SpawnEggItem(properties
                    .spawnEgg(BitterEntities.SCP_650.get())
                    .stacksTo(1)
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(
                                    Component.literal("Startling Statue")
                                            .withStyle(ChatFormatting.ITALIC)
                                            .withStyle(ChatFormatting.GRAY),
                                    Component.literal("Euclid")
                                            .withStyle(ChatFormatting.GOLD)
                            ))
                    ))
    );

    public static final DeferredItem<Item> SCP_131_SPAWN_EGG = ITEMS.registerItem(
            "scp_131_spawn_egg",
            properties -> new SpawnEggItem(properties
                    .spawnEgg(BitterEntities.SCP_131.get())
                    .stacksTo(1)
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(
                                    Component.literal("The Eye Pods")
                                            .withStyle(ChatFormatting.ITALIC)
                                            .withStyle(ChatFormatting.GRAY),
                                    Component.literal("Safe")
                                            .withStyle(ChatFormatting.GREEN)
                            ))
                    ))
    );

    public static final DeferredItem<Item> SCP_548_SPAWN_EGG = ITEMS.registerItem(
            "scp_548_spawn_egg",
            properties -> new SpawnEggItem(properties
                    .spawnEgg(BitterEntities.SCP_548.get())
                    .stacksTo(1)
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(
                                    Component.literal("Ice Spider")
                                            .withStyle(ChatFormatting.ITALIC)
                                            .withStyle(ChatFormatting.GRAY),
                                    Component.literal("Euclid")
                                            .withStyle(ChatFormatting.GOLD)
                            ))
                    ))
    );

    public static final DeferredItem<Item> SCP_939_SPAWN_EGG = ITEMS.registerItem(
            "scp_939_spawn_egg",
            properties -> new SpawnEggItem(properties
                    .spawnEgg(BitterEntities.SCP_939.get())
                    .stacksTo(1)
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(
                                    Component.literal("With Many Voices")
                                            .withStyle(ChatFormatting.ITALIC)
                                            .withStyle(ChatFormatting.GRAY),
                                    Component.literal("Keter")
                                            .withStyle(ChatFormatting.RED)
                            ))
                    ))
    );

    public static final DeferredItem<Item> SCP_1507_SPAWN_EGG = ITEMS.registerItem(
            "scp_1507_spawn_egg",
            properties ->
                    new SpawnEggItem(properties
                            .spawnEgg(BitterEntities.SCP_1507.get())
                            .stacksTo(1)
                            .component(DataComponents.LORE, new ItemLore(
                                    List.of(
                                            Component.literal("Pink Flamingos")
                                                    .withStyle(ChatFormatting.ITALIC)
                                                    .withStyle(ChatFormatting.GRAY),
                                            Component.literal("Euclid")
                                                    .withStyle(ChatFormatting.GOLD)
                                    ))
                            ))
    );

    public static final DeferredItem<Item> SCP_718_SPAWN_EGG = ITEMS.registerItem(
            "scp_718_spawn_egg",
            properties ->
                    new SpawnEggItem(properties
                            .spawnEgg(BitterEntities.SCP_718.get())
                            .stacksTo(1)
                            .component(DataComponents.LORE, new ItemLore(
                                    List.of(
                                            Component.literal("Eyeball")
                                                    .withStyle(ChatFormatting.ITALIC)
                                                    .withStyle(ChatFormatting.GRAY),
                                            Component.literal("Keter")
                                                    .withStyle(ChatFormatting.RED)
                                    ))
                            ))
    );

    public static final DeferredItem<Item> SCP_025_FR_SPAWN_EGG = ITEMS.registerItem(
            "scp_025_fr_spawn_egg",
            properties ->
                    new SpawnEggItem(properties
                            .spawnEgg(BitterEntities.SCP_025_FR.get())
                            .stacksTo(1)
                            .component(DataComponents.LORE, new ItemLore(
                                    List.of(
                                            Component.literal("Deadly Silence")
                                                    .withStyle(ChatFormatting.ITALIC)
                                                    .withStyle(ChatFormatting.GRAY),
                                            Component.literal("Euclid")
                                                    .withStyle(ChatFormatting.GOLD)
                                    ))
                            ))
    );

    public static final DeferredItem<Item> SCP_018 = ITEMS.register("scp_018", registryName ->
            new Item(new Item.Properties()
                    .stacksTo(1)
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(ENERGY_LOSS_ON_BOUNCE, 1.5f)
                    .component(MAX_BOUNCES, 10000)
                    .component(DataComponents.LORE, new ItemLore(List.of(Component.literal("Super Ball").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY), Component.literal("Euclid").withStyle(ChatFormatting.GOLD)))
                    )));


    public static final DeferredItem<Item> SCP_005 = ITEMS.register("scp_005", registryName ->
            new SCP005Item(new Item.Properties()
                    .stacksTo(1)
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(DataComponents.LORE, new ItemLore(List.of(Component.literal("The Skeleton Key").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY), Component.literal("Safe").withStyle(ChatFormatting.GREEN))
                    ))));

    public static final DeferredItem<SyringeItem> SYRINGE = ITEMS.register("syringe", registryName ->
            new SyringeItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(VOLUME, 10)));

    public static final DeferredItem<MopItem> MOP = ITEMS.register("mop", registryName ->
            new MopItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(VOLUME, 50)
                    .component(MAX_TRANSFER_RATE, 20)));

    public static final DeferredItem<PowderedSubstanceItem> POWDER = ITEMS.register("powder", registryName ->
            new PowderedSubstanceItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(VOLUME, 20)));

    public static final DeferredItem<PillItem> PILL = ITEMS.register("pill", registryName ->
            new PillItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(PILL_SHAPE, PillShape.ROUND)
                    .component(DataComponents.DYED_COLOR, new DyedItemColor(0xFFFFFF))
                    .component(VOLUME, 20)));


    public static final DeferredItem<Item> SCALPEL = ITEMS.register("scalpel", registryName ->
            new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .stacksTo(1)
                    .component(BitterDataComponents.SCALPEL, new Scalpel(1.0f))
            ));

    public static final DeferredItem<Item> HEMOSTAT = ITEMS.registerSimpleItem(
            "hemostat",
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> RETRACTOR = ITEMS.register("retractor", registryName ->
            new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .stacksTo(1)
                    .component(BitterDataComponents.RETRACTOR, new Retractor())
            ));

    public static final DeferredItem<Item> CAUTERY = ITEMS.registerSimpleItem(
            "cautery",
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> KIDNEY = ITEMS.registerSimpleItem(
            "kidney",
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> LIVER = ITEMS.registerSimpleItem(
            "liver",
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> STOMACH = ITEMS.registerSimpleItem(
            "stomach",
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> GALLBLADDER = ITEMS.registerSimpleItem(
            "gallbladder",
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> PELVIS = ITEMS.registerSimpleItem(
            "pelvis",
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BLADDER = ITEMS.registerSimpleItem(
            "bladder",
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> SURGICAL_SPONGE = ITEMS.registerSimpleItem(
            "surgical_sponge",
            () -> new Item.Properties().stacksTo(3));

    public static final DeferredItem<Item> BANDAGE = ITEMS.registerSimpleItem(
            "bandage",
            () -> new Item.Properties().stacksTo(3));

    public static final DeferredItem<Item> GLASS_SHARD = ITEMS.registerSimpleItem(
            "glass_shard",
            () -> new Item.Properties().stacksTo(8));

    public static final DeferredItem<LaserDesignatorItem> LASER_DESIGNATOR = ITEMS.registerItem(
            "laser_designator",
            properties -> new LaserDesignatorItem(properties.stacksTo(1))
    );

    public static final DeferredItem<GasContainerItem> GAS_CYLINDER = ITEMS.registerItem("gas_cylinder", GasContainerItem::new);

    public static final DeferredItem<IntercomPhoneItem> INTERCOM_PHONE = ITEMS.registerItem("intercom_phone", IntercomPhoneItem::new);

    public static final DeferredItem<Item> NETWORK_CABLE = ITEMS.registerSimpleItem("network_cable");

    public static final DeferredItem<WireItem> WIRE = ITEMS.registerItem("wire", WireItem::new);

    public static final DeferredItem<Item> COLON = ITEMS.registerSimpleItem(
            "colon",
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> HELLO_KITTY_CELLPHONE = ITEMS.registerSimpleItem("hello_kitty_cellphone");

    public static final DeferredItem<Item> SCP_2398 = ITEMS.registerSimpleItem(
            "scp_2398",
            () -> new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.LORE, new ItemLore(List.of(
                            Component.literal("Home Run Bat")
                                    .withStyle(ChatFormatting.ITALIC)
                                    .withStyle(ChatFormatting.GRAY),
                            Component.literal("Safe")
                                    .withStyle(ChatFormatting.GREEN))
                    ))
    );

    public static final DeferredItem<Item> BASEBALL = ITEMS.registerSimpleItem("baseball");

    public static final DeferredItem<TestHeatedItem> CIGARETTE_BUTT = ITEMS.register("cigarette_butt", registryName ->
            new TestHeatedItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(DataComponents.CONSUMABLE, Consumable.builder()
                            .consumeSeconds(0.5f)
                            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0)))
                            .build())
                    .component(HEAT_BEHAVIOR, HeatBehavior.DEFAULT)));

    public static final DeferredItem<Item> KEYCARD = ITEMS.registerSimpleItem("keycard");

    public static final DeferredItem<SCP815SnakeHandItem> SCP_815_SNAKE_HAND = ITEMS.registerItem(
            "scp_815_snake_hand", SCP815SnakeHandItem::new);

    public static final DeferredItem<WritablePaper> WRITABLE_PAPER = ITEMS.registerItem("paper", WritablePaper::new);

    public static final DeferredItem<Item> TASER_CARTRIDGE = ITEMS.registerSimpleItem("taser_cartridge");

    public static final DeferredItem<TaserItem> TASER = ITEMS.registerItem(
            "taser",
            properties -> new TaserItem(properties.stacksTo(1))
    );

    public static final DeferredItem<Item> BODY_PART = ITEMS.registerSimpleItem("body_part");

    public static final DeferredItem<Item> BITTERMELON = ITEMS.register("bittermelon",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .food(new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationModifier(0.1f)
                            .build())
                    .component(DataComponents.CONSUMABLE, Consumable.builder()
                            .onConsume(new ApplyStatusEffectsConsumeEffect(
                                    new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.8f))
                            .build())));

    public static final DeferredItem<SCP377Item> SCP_377 = ITEMS.registerItem("scp_377",
            properties -> new SCP377Item(
                    properties.stacksTo(1)
                            .component(DataComponents.LORE, new ItemLore(List.of(Component.literal("Accurate Fortune Cookies").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY), Component.literal("Safe").withStyle(ChatFormatting.GREEN)))
                            )));

    public static final DeferredItem<SCP3771Item> SCP_377_1 = ITEMS.registerItem("scp_377_1", SCP3771Item::new);

    public static final DeferredItem<SCP377CookieItem> FORTUNE_COOKIE = ITEMS.registerItem("fortune_cookie", SCP377CookieItem::new);

    public static final DeferredItem<Item> CRACKED_FORTUNE_COOKIE = ITEMS.registerSimpleItem("cracked_fortune_cookie",
            () -> new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(1)
                            .saturationModifier(1)
                            .alwaysEdible()
                            .build()
                    )
    );

    public static final DeferredItem<HandheldSystemInterface> HANDHELD_SYSTEM_INTERFACE = ITEMS.registerItem("handheld_system_interface", HandheldSystemInterface::new);

    public static final DeferredItem<ScrewdriverItem> SCREWDRIVER = ITEMS.register("screwdriver", registryName ->
            new ScrewdriverItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .component(BitterDataComponents.SCREWDRIVER, Screwdriver.DEFAULT)));

    public static final DeferredItem<ChalkItem> CHALK = ITEMS.register("chalk", registryName ->
            new ChalkItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .setNoCombineRepair()
                    .component(
                            // TODO: find a way to make particles the right color in context
                            DataComponents.CONSUMABLE,
                            Consumable.builder().build()
                    ).component(
                            DataComponents.CAN_PLACE_ON,
                            new AdventureModePredicate(List.of(
                                    new BlockPredicate(
                                            Optional.of(HolderSet.direct(BitterBlocks.WALL_WRITING)),
                                            Optional.empty(),
                                            Optional.empty(),
                                            DataComponentMatchers.ANY)))
                    ).component(
                            DataComponents.TOOLTIP_DISPLAY,
                            new TooltipDisplay(false, new LinkedHashSet<>(List.of(DataComponents.CAN_PLACE_ON)))
                    )
                    .durability(64)));

    public static final DeferredItem<HighlighterItem> HIGHLIGHTER = ITEMS.registerItem(
            "highlighter",
            properties -> new HighlighterItem(properties
                    .setNoCombineRepair()
                    .component(
                            DataComponents.CAN_PLACE_ON,
                            new AdventureModePredicate(List.of(
                                    new BlockPredicate(
                                            Optional.of(HolderSet.direct(BitterBlocks.WALL_WRITING)),
                                            Optional.empty(),
                                            Optional.empty(),
                                            DataComponentMatchers.ANY)))
                    ).component(
                            DataComponents.TOOLTIP_DISPLAY,
                            new TooltipDisplay(false, new LinkedHashSet<>(List.of(DataComponents.CAN_PLACE_ON)))
                    )
                    .durability(64)
            )
    );

    public static final DeferredItem<StickyNote> STICKY_NOTE = ITEMS.registerItem("sticky_note",
            props -> new StickyNote(BitterBlocks.STICKY_NOTE.get(), props.stacksTo(16))
    );

    public static final DeferredItem<Item> PEN = ITEMS.registerSimpleItem("pen");

    public static final DeferredItem<WireCuttersItem> WIRE_CUTTERS = ITEMS.registerItem("wire_cutters", WireCuttersItem::new);

    public static final DeferredItem<BucketItem> SUBSTANCE_FLUID_BUCKET = ITEMS.registerItem("substance_fluid_bucket", registryName ->
            new BucketItem(BitterFluids.SUBSTANCE_FLUID.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName.effectiveModel()))
                    .stacksTo(1)));

    public static final DeferredItem<BucketItem> SIMPLE_FLUID_BUCKET = ITEMS.registerItem("simple_fluid_bucket", registryName ->
            new BucketItem(BitterFluids.SIMPLE_FLUID.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName.effectiveModel()))
                    .stacksTo(1)));

    public static final DeferredItem<Item> SUTURE = ITEMS.register("suture", registryName ->
            new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .stacksTo(1)
                    .component(BitterDataComponents.SUTURE, new Suture(1.0f))
            ));

    public static final DeferredItem<RepairToolItem> REPAIR_TOOL = ITEMS.register("repair_tool", registryName ->
            new RepairToolItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .stacksTo(1)
            ));

    public static final DeferredItem<DebugWire> DEBUG_WIRE = ITEMS.register("debug_wire", registryName ->
            new DebugWire(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .stacksTo(1)
            ));

    public static final DeferredItem<Item> SEA_MONKEY_BUCKET = ITEMS.register("sea_monkey_bucket", registryName ->
            new MobBucketItem(BitterEntities.SEA_MONKEY.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_TADPOLE, new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .stacksTo(1)
                    .component(VOLUME, 50)
            ));

    public static final DeferredItem<SCP1079Item> SCP_1079 = ITEMS.registerItem("scp_1079",
            properties -> new SCP1079Item(
                    properties.stacksTo(1)
                            .component(DataComponents.LORE, new ItemLore(List.of(Component.literal("Dr. Wondertainment's Bubblebath Bonbons!").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.LIGHT_PURPLE), Component.literal("[WARNING] Only one sweet is to be taken per sitting. Not suitable for children under 3 years").withStyle(ChatFormatting.DARK_GRAY)))
                            )));

    public static final DeferredItem<SCP1079CandyItem> SCP_1079_CANDY = ITEMS.register(
            "scp_1079_candy",
            registryName -> new SCP1079CandyItem(
                    new Item.Properties()
                            .food(new FoodProperties.Builder()
                                    .nutrition(1)
                                    .saturationModifier(1)
                                    .alwaysEdible()
                                    .build())
                            .setId(ResourceKey.create(Registries.ITEM, registryName))
                            .stacksTo(64)
            )
    );

    public static final DeferredItem<SCP815Item> SCP_815 = ITEMS.registerItem("scp_815",
            properties -> new SCP815Item(
                    properties.stacksTo(1)
                            .component(DataComponents.LORE, new ItemLore(List.of(
                                    Component.literal("[Label] Fancy's Salted Mixed Nuts")
                                            .withStyle(ChatFormatting.ITALIC)
                                            .withStyle(ChatFormatting.DARK_GRAY)
                            )))
            ));

    public static final DeferredItem<Item> METAL_ROD = ITEMS.registerSimpleItem("metal_rod");

    public static final DeferredItem<Item> PINK_PLASTIC_SCRAP = ITEMS.registerSimpleItem("pink_plastic_scrap");

    public static final DeferredItem<Handgun> HANDGUN = ITEMS.registerItem("handgun", Handgun::new);

    public static final DeferredItem<ClownHammer> CLOWN_HAMMER = ITEMS.registerItem("clown_hammer", properties ->
            new ClownHammer(properties.attributes(
                    ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 10000000, AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build())));

}