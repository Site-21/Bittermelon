package com.site21.bittermelon.init.neoforge;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.entities.ThrownItemProjectile;
import com.site21.bittermelon.common.content.entities.cage.Cage;
import com.site21.bittermelon.common.content.entities.chicken.Chicken;
import com.site21.bittermelon.common.content.entities.fluidprojectile.FluidProjectile;
import com.site21.bittermelon.common.content.entities.mimicplayer.Mimic;
import com.site21.bittermelon.common.content.entities.ragdoll.RagdollEntity;
import com.site21.bittermelon.common.content.entities.scp025fr.SCP025FR;
import com.site21.bittermelon.common.content.entities.scp131.SCP131;
import com.site21.bittermelon.common.content.entities.scp1507.SCP1507;
import com.site21.bittermelon.common.content.entities.scp250.SCP250;
import com.site21.bittermelon.common.content.entities.scp548.SCP548;
import com.site21.bittermelon.common.content.entities.scp650.SCP650;
import com.site21.bittermelon.common.content.entities.scp718.SCP718;
import com.site21.bittermelon.common.content.entities.scp815snake.SCP815Snake;
import com.site21.bittermelon.common.content.entities.scp939.SCP939;
import com.site21.bittermelon.common.content.entities.seamonkey.SeaMonkey;
import com.site21.bittermelon.common.content.items.scps.scp2398.SCP2398ProjectileItem;
import com.site21.bittermelon.common.content.items.taser.TaserProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class BitterEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(Bittermelon.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownItemProjectile>> THROWN_ITEM_PROJECTILE = ENTITY_TYPES.register("thrown_item_projectile",
            () -> EntityType.Builder.<ThrownItemProjectile>of(ThrownItemProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .noLootTable()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "thrown_item_projectile"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP2398ProjectileItem>> SCP_2398_PROJECTILE = ENTITY_TYPES.register("scp_2398_projectile",
            () -> EntityType.Builder.<SCP2398ProjectileItem>of(SCP2398ProjectileItem::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .noLootTable()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "scp_2398_projectile"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP939>> SCP_939 = ENTITY_TYPES.register("scp_939",
            () -> EntityType.Builder.of(SCP939::new, MobCategory.MONSTER)
                    .sized(0.9f, 0.9f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "scp_939"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP250>> SCP_250 = ENTITY_TYPES.register("scp_250",
            () -> EntityType.Builder.of(SCP250::new, MobCategory.MONSTER)
                    .sized(1.0f, 3.0f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "scp_250"))));

    public static final DeferredHolder<EntityType<?>, EntityType<Chicken>> CHICKEN = ENTITY_TYPES.register("chicken",
            () -> EntityType.Builder.of(Chicken::new, MobCategory.AMBIENT)
                    .sized(0.3f, 0.3f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "chicken"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP650>> SCP_650 = ENTITY_TYPES.register("scp_650",
            () -> EntityType.Builder.of(SCP650::new, MobCategory.MONSTER)
                    .sized(0.7f, 1.7f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "scp_650"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP131>> SCP_131 = ENTITY_TYPES.register("scp_131",
            () -> EntityType.Builder.of(SCP131::new, MobCategory.AMBIENT)
                    .sized(0.3f, 0.3f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "scp_131"))));

    public static final DeferredHolder<EntityType<?>, EntityType<TaserProjectile>> TASER_PROJECTILE = ENTITY_TYPES.register("taser_projectile",
            () -> EntityType.Builder.<TaserProjectile>of(TaserProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .noLootTable()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "taser_projectile"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP1507>> SCP_1507 = ENTITY_TYPES.register("scp_1507",
            () -> EntityType.Builder.of(SCP1507::new, MobCategory.MONSTER)
                    .sized(0.5f, 0.7f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "scp_1507"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP548>> SCP_548 = ENTITY_TYPES.register("scp_548",
            () -> EntityType.Builder.of(SCP548::new, MobCategory.MONSTER)
                    .sized(0.2f, 0.1f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "scp_548"))));

    public static final DeferredHolder<EntityType<?>, EntityType<Cage>> CAGE = ENTITY_TYPES.register("cage",
            () -> EntityType.Builder.of(Cage::new, MobCategory.MISC)
                    .noLootTable()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "cage"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SeaMonkey>> SEA_MONKEY = ENTITY_TYPES.register("sea_monkey",
            () -> EntityType.Builder.of(SeaMonkey::new, MobCategory.WATER_AMBIENT)
                    .sized(0.3f, 0.3f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "sea_monkey"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP718>> SCP_718 = ENTITY_TYPES.register("scp_718",
            () -> EntityType.Builder.of(SCP718::new, MobCategory.MONSTER)
                    .sized(0.3f, 1.8f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Bittermelon.identifier("scp_718"))));

    public static final DeferredHolder<EntityType<?>, EntityType<FluidProjectile>> FLUID_PROJECTILE = ENTITY_TYPES.register("fluid_projectile",
            () -> EntityType.Builder.<FluidProjectile>of(FluidProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .noLootTable()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Bittermelon.identifier("fluid_projectile"))));

    public static final DeferredHolder<EntityType<?>, EntityType<Mimic>> MIMIC = ENTITY_TYPES.register("mimic",
            () -> EntityType.Builder.<Mimic>of(Mimic::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .noLootTable()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Bittermelon.identifier("mimic"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP025FR>> SCP_025_FR = ENTITY_TYPES.register("scp_025_fr",
            () -> EntityType.Builder.of(SCP025FR::new, MobCategory.MONSTER)
                    .sized(0.6f, 0.3f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Bittermelon.identifier("scp_025_fr"))));

    public static final DeferredHolder<EntityType<?>, EntityType<SCP815Snake>> SCP_815_SNAKE = ENTITY_TYPES.register("scp_815_snake",
            () -> EntityType.Builder.of(SCP815Snake::new, MobCategory.MISC)
                    .sized(0.5f, 0.2f)
                    .noLootTable()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "scp_815_snake"))));

    public static final DeferredHolder<EntityType<?>, EntityType<RagdollEntity>> RAGDOLL = ENTITY_TYPES.register("ragdoll",
            () -> EntityType.Builder.of(RagdollEntity::new, MobCategory.MISC)
                    .sized(0.6f, 0.5f)
                    .noLootTable()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Bittermelon.identifier("ragdoll"))));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        eventBus.addListener(BitterEntities::registerAttributes);
    }

    private static void registerAttributes(@NotNull EntityAttributeCreationEvent event) {
        event.put(SCP_939.get(), SCP939.createAttributes().build());
        event.put(SCP_250.get(), SCP939.createAttributes().build());
        event.put(CHICKEN.get(), Chicken.createAttributes().build());
        event.put(SCP_650.get(), SCP650.createAttributes().build());
        event.put(SCP_131.get(), SCP131.createAttributes().build());
        event.put(SCP_1507.get(), SCP1507.createAttributes().build());
        event.put(SCP_548.get(), SCP548.createAttributes().build());
        event.put(SEA_MONKEY.get(), SeaMonkey.createAttributes().build());
        event.put(SCP_718.get(), SCP718.createAttributes().build());
        event.put(MIMIC.get(), Mimic.createAttributes().build());
        event.put(SCP_025_FR.get(), SCP025FR.createAttributes().build());
        event.put(SCP_815_SNAKE.get(), SCP815Snake.createLivingAttributes().build());
    }
}