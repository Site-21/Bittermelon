package com.site21.bittermelon.client.event;

import com.google.common.reflect.TypeToken;
import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.client.particles.BoomParticle;
import com.site21.bittermelon.client.particles.KapowParticle;
import com.site21.bittermelon.client.particles.PlasticParticle;
import com.site21.bittermelon.client.render.FogRenderer;
import com.site21.bittermelon.common.content.blocks.electronics.intercom.client.PhoneCordRenderer;
import com.site21.bittermelon.common.content.blocks.electronics.largeslidingdoor.client.LargeSlidingDoorRenderer;
import com.site21.bittermelon.common.content.blocks.electronics.slidingdoor.client.SlidingDoorRenderer;
import com.site21.bittermelon.common.content.blocks.electronics.television.MediaSheets;
import com.site21.bittermelon.common.content.blocks.electronics.television.client.TelevisionRenderer;
import com.site21.bittermelon.common.content.blocks.electronics.thermometer.client.ThermometerRenderer;
import com.site21.bittermelon.common.content.blocks.flamingo.FlamingoBlockRenderer;
import com.site21.bittermelon.common.content.blocks.wallwriting.client.WallWritingRenderer;
import com.site21.bittermelon.common.content.entities.cage.client.CageRenderer;
import com.site21.bittermelon.common.content.entities.chicken.client.ChickenRenderer;
import com.site21.bittermelon.common.content.entities.fluidprojectile.FluidProjectileRenderer;
import com.site21.bittermelon.common.content.entities.mimicplayer.Mimic;
import com.site21.bittermelon.common.content.entities.mimicplayer.client.MimicRenderer;
import com.site21.bittermelon.common.content.entities.ragdoll.RagdollUtil;
import com.site21.bittermelon.common.content.entities.ragdoll.client.RagdollRenderer;
import com.site21.bittermelon.common.content.entities.scp025fr.client.SCP025FRRenderer;
import com.site21.bittermelon.common.content.entities.scp131.client.SCP131Renderer;
import com.site21.bittermelon.common.content.entities.scp1507.client.SCP1507Renderer;
import com.site21.bittermelon.common.content.entities.scp250.client.SCP250Renderer;
import com.site21.bittermelon.common.content.entities.scp548.client.SCP548Renderer;
import com.site21.bittermelon.common.content.entities.scp650.client.SCP650Renderer;
import com.site21.bittermelon.common.content.entities.scp718.client.EyeballOnPlayerLayer;
import com.site21.bittermelon.common.content.entities.scp718.client.SCP718Renderer;
import com.site21.bittermelon.common.content.entities.scp815snake.client.SCP815BloodLayer;
import com.site21.bittermelon.common.content.entities.scp815snake.client.SCP815SnakeRenderer;
import com.site21.bittermelon.common.content.entities.scp939.client.SCP939Renderer;
import com.site21.bittermelon.common.content.entities.seamonkey.client.SeaMonkeyRenderer;
import com.site21.bittermelon.common.content.items.keycard.KeycardDecorator;
import com.site21.bittermelon.common.content.items.repairtool.RepairToolUseAnimation;
import com.site21.bittermelon.common.content.items.taser.TaserProjectileRenderer;
import com.site21.bittermelon.common.content.items.wire.client.WireFeatureRenderer;
import com.site21.bittermelon.common.content.items.wire.client.WireState;
import com.site21.bittermelon.common.systems.carry.CarryHandler;
import com.site21.bittermelon.common.systems.character.networking.UpdateCharacter;
import com.site21.bittermelon.common.systems.component.screwdriver.ScrewdriverUseAnimation;
import com.site21.bittermelon.common.systems.component.temperature.HeatDecorator;
import com.site21.bittermelon.common.systems.fluid.simple.ClientSimpleFluid;
import com.site21.bittermelon.common.systems.fluid.substance.client.ClientSubstanceFluid;
import com.site21.bittermelon.common.systems.fluid.substance.client.SubstanceFluidRenderer;
import com.site21.bittermelon.common.systems.fluid.substance.client.SubstanceTintSource;
import com.site21.bittermelon.common.systems.medical.anatomy.client.AnatomyDebugRenderer;
import com.site21.bittermelon.common.systems.medical.bodypart.LimbLayer;
import com.site21.bittermelon.common.systems.medical.bodypart.PartInstance;
import com.site21.bittermelon.common.systems.medical.legacy.client.AnatomyPictureInPictureRenderer;
import com.site21.bittermelon.common.systems.medical.legacy.client.compartmentrenderers.CompartmentRenderers;
import com.site21.bittermelon.common.systems.medical.legacy.client.tool.InstrumentWidgets;
import com.site21.bittermelon.common.systems.medical.legacy.networking.AddAndInsertCompartment;
import com.site21.bittermelon.common.systems.medical.legacy.networking.InsertCompartment;
import com.site21.bittermelon.common.systems.medical.legacy.networking.RemoveCompartment;
import com.site21.bittermelon.common.systems.medical.legacy.networking.UpdateCompartments;
import com.site21.bittermelon.common.systems.personnel.privilege.networking.*;
import com.site21.bittermelon.common.systems.personnel.registry.networking.AddPersonnelEntry;
import com.site21.bittermelon.common.systems.personnel.registry.networking.PersonnelClientPayloadHandler;
import com.site21.bittermelon.common.systems.personnel.registry.networking.RemovePersonnelEntry;
import com.site21.bittermelon.common.systems.personnel.registry.networking.UpdatePersonnelEntry;
import com.site21.bittermelon.common.systems.rage.client.RageRenderer;
import com.site21.bittermelon.datagen.property.*;
import com.site21.bittermelon.init.custom.BodyParts;
import com.site21.bittermelon.init.neoforge.*;
import com.site21.bittermelon.networking.client.ClientPayloadHandler;
import com.site21.bittermelon.networking.server.SetLastTypingTime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.site21.bittermelon.common.systems.medical.bodypart.client.BodyPartModels.registerBodyPartModel;
import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.HEALTH_CONTAINER;
import static com.site21.bittermelon.init.neoforge.BitterEntities.*;
import static com.site21.bittermelon.init.neoforge.BitterFluidTypes.SIMPLE_FLUID_TYPE;
import static com.site21.bittermelon.init.neoforge.BitterFluidTypes.SUBSTANCE_FLUID_TYPE;

@EventBusSubscriber(modid = Bittermelon.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {
    private static final Map<PlayerModelType, EntityRendererProvider<Mimic>> MIMIC_PROVIDERS = Map.of(
            PlayerModelType.WIDE, p_174098_ -> new MimicRenderer(p_174098_, false), PlayerModelType.SLIM, p_174096_ -> new MimicRenderer(p_174096_, true)
    );

    public static final ContextKey<Float> ENTITY_WIDTH = new ContextKey<>(
            Bittermelon.identifier("entity_width")
    );

    public static final ContextKey<Map<String, Boolean>> LIMB_VISIBILITY = new ContextKey<>(
            Bittermelon.identifier("limb_visibility")
    );

    public static final ContextKey<Boolean> EYEBALL_GROWTH = new ContextKey<>(
            Bittermelon.identifier("eyeball_growth")
    );

    public static final ContextKey<Map<BlockPos, Integer>> BLOCK_DAMAGE = new ContextKey<>(
            Bittermelon.identifier("block_damage")
    );

    public static final ContextKey<SleepTransforms.SleepTransform> SLEEP_TRANSFORM = new ContextKey<>(
            Bittermelon.identifier("sleep_transform")
    );

    public static final ContextKey<WireState> WIRE_STATE = new ContextKey<>(
            Bittermelon.identifier("wire_state")
    );

    public static final ContextKey<Boolean> BROKEN_JAW = new ContextKey<>(
            Bittermelon.identifier("broken_jaw")
    );

    public static final ContextKey<PartInstance> ROOT_PART = new ContextKey<>(
            Bittermelon.identifier("root_part")
    );

    public static final ContextKey<Boolean> RAGDOLLED = new ContextKey<>(
            Bittermelon.identifier("ragdolled")
    );

    @SubscribeEvent
    public static void fmlSetup(FMLClientSetupEvent event) {
//        ItemBlockRenderTypes.setRenderLayer(SUBSTANCE_FLUID.get(), ChunkSectionLayer.TRANSLUCENT);
        CompartmentRenderers.register();
        InstrumentWidgets.register();
        SleepTransforms.init();
        FogRenderer.register();
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerEntityRenderer(SCP_939.get(), SCP939Renderer::new);
        event.registerEntityRenderer(SCP_250.get(), SCP250Renderer::new);
        event.registerEntityRenderer(CHICKEN.get(), ChickenRenderer::new);
        event.registerEntityRenderer(THROWN_ITEM_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SCP_2398_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SCP_650.get(), SCP650Renderer::new);
        event.registerEntityRenderer(SCP_131.get(), SCP131Renderer::new);
        event.registerEntityRenderer(SCP_1507.get(), SCP1507Renderer::new);
        event.registerEntityRenderer(TASER_PROJECTILE.get(), TaserProjectileRenderer::new);
        event.registerEntityRenderer(SCP_548.get(), SCP548Renderer::new);
        event.registerEntityRenderer(SCP_815_SNAKE.get(), SCP815SnakeRenderer::new);
        event.registerEntityRenderer(CAGE.get(), CageRenderer::new);
        event.registerEntityRenderer(SEA_MONKEY.get(), SeaMonkeyRenderer::new);
        event.registerEntityRenderer(SCP_718.get(), SCP718Renderer::new);
        event.registerEntityRenderer(FLUID_PROJECTILE.get(), FluidProjectileRenderer::new);
        MIMIC_PROVIDERS.forEach(
                (_, provider) -> event.registerEntityRenderer(MIMIC.get(), provider)
        );
        event.registerEntityRenderer(SCP_025_FR.get(), SCP025FRRenderer::new);
        event.registerEntityRenderer(RAGDOLL.get(), RagdollRenderer::new);
        event.registerBlockEntityRenderer(BitterBlockEntities.THERMOMETER_BLOCK_ENTITY.get(), ThermometerRenderer::new);
        event.registerBlockEntityRenderer(BitterBlockEntities.INTERCOM_BLOCK_ENTITY.get(), PhoneCordRenderer::new);
        event.registerBlockEntityRenderer(BitterBlockEntities.LARGE_SLIDING_DOOR_BLOCK_ENTITY.get(), LargeSlidingDoorRenderer::new);
        event.registerBlockEntityRenderer(BitterBlockEntities.SLIDING_DOOR_BLOCK_ENTITY.get(), SlidingDoorRenderer::new);
        event.registerBlockEntityRenderer(BitterBlockEntities.WALL_WRITING_BLOCK_ENTITY.get(), WallWritingRenderer::new);
        event.registerBlockEntityRenderer(BitterBlockEntities.TELEVISION_BLOCK_ENTITY.get(), TelevisionRenderer::new);
        event.registerBlockEntityRenderer(BitterBlockEntities.PLASTIC_FLAMINGO_BLOCK_ENTITY.get(), FlamingoBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerRenderStateModifiers(@NotNull RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(
                new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {},
                (entity, state) -> state.setRenderData(SLEEP_TRANSFORM, SleepTransforms.get(entity.getType()))
        );

        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState) {
                renderState.setRenderData(WIRE_STATE, WireFeatureRenderer.extractWireState(avatar, renderState.partialTick));
            }
        });

        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState) {
                Entity carriedEntity = CarryHandler.getCarried(avatar);
                if (carriedEntity == null) {
                    renderState.setRenderData(ENTITY_WIDTH, 0f);
                    return;
                }

                float entityWidth = carriedEntity.getBbWidth();
                renderState.setRenderData(ENTITY_WIDTH, entityWidth);
            }
        });

        event.registerEntityModifier(
                new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {
                },
                (entity, state) -> {
//                    Map<String, Boolean> limbVisibility = null;
//                    if (entity.hasData(MEDICAL_STATS)) {
//                        limbVisibility = entity.getData(MEDICAL_STATS).getAnatomyModel().getBodyPartVisibility();
//                    }
//
//                    state.setRenderData(LIMB_VISIBILITY, limbVisibility);

                    if (entity.hasData(HEALTH_CONTAINER)) {
                        state.setRenderData(ROOT_PART, entity.getData(HEALTH_CONTAINER).getRoot());
                    }

                }
        );

        event.registerAvatarEntityModifier(
                new AvatarRenderStateModifier() {
                    @Override
                    public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState) {
                        MobEffectInstance eyeballEffect = avatar.getEffect(BitterMobEffects.EYEBALL_GROWTH);
                        boolean render = eyeballEffect != null && eyeballEffect.getAmplifier() > 0;
                        renderState.setRenderData(EYEBALL_GROWTH, render);
                    }
                }
        );

        event.registerAvatarEntityModifier(
                new AvatarRenderStateModifier() {
                    @Override
                    public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState) {
                        MobEffectInstance brokenJawEffect = avatar.getEffect(BitterMobEffects.BROKEN_JAW);
                        boolean render = brokenJawEffect != null;
                        renderState.setRenderData(BROKEN_JAW, render);
                    }
                }
        );

        event.registerEntityModifier(
                new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {},
                (entity, state) -> state.setRenderData(RAGDOLLED, RagdollUtil.isRagdolled(entity))
        );
    }

    @SubscribeEvent
    public static void registerRangeProperties(@NotNull RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "substance_volume"),
                SubstanceVolume.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.@NotNull ItemTintSources event) {
        event.register(
                Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "substance_color"),
                SubstanceColor.MAP_CODEC
        );

        event.register(
                Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "base_color"),
                BaseColor.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void registerSelectProperties(@NotNull RegisterSelectItemModelPropertyEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "pill_shape"),
                StackPillShape.TYPE
        );
    }

    @SubscribeEvent
    public static void registerConditionalProperties(@NotNull RegisterConditionalItemModelPropertyEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "smokable_lit"),
                SmokableLit.MAP_CODEC
        );

        event.register(
                Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "empty_377"),
                Empty377.MAP_CODEC
        );

        event.register(
                Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "open_1079"),
                Open1079.MAP_CODEC
        );

        event.register(
                Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "open_815"),
                Open815.MAP_CODEC
        );

        event.register(
                Bittermelon.identifier("on_cooldown"),
                OnCooldown.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void registerClientExtensions(@NotNull RegisterClientExtensionsEvent event) {
        event.registerItem(
                new ScrewdriverUseAnimation(),
                BitterItems.SCREWDRIVER
        );

        event.registerItem(
                new RepairToolUseAnimation(),
                BitterItems.REPAIR_TOOL
        );
    }

    @SubscribeEvent
    public static void onRenderInventorySlot(@NotNull RegisterItemDecorationsEvent event) {
        HeatDecorator heatDecorator = new HeatDecorator();
        for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
            event.register(item, heatDecorator);
        }

        event.register(BitterItems.KEYCARD, new KeycardDecorator());
    }

    @SubscribeEvent
    public static void onRegisterFluidTypeExtensions(@NotNull RegisterClientExtensionsEvent event) {
        event.registerFluidType(new ClientSubstanceFluid(), SUBSTANCE_FLUID_TYPE.get());
        event.registerFluidType(new ClientSimpleFluid(), SIMPLE_FLUID_TYPE.get());
    }

    @SubscribeEvent
    public static void registerAtlases(@NotNull RegisterTextureAtlasesEvent event) {
        event.register(new AtlasManager.AtlasConfig(MediaSheets.ATLAS_LOCATION, MediaSheets.ATLAS_INFO_LOCATION, true));
    }

    @SubscribeEvent
    public static void registerParticleProviders(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BitterParticles.PLASTIC.get(), PlasticParticle.Provider::new);
        event.registerSpriteSet(BitterParticles.KAPOW.get(), KapowParticle.Provider::new);
        event.registerSpriteSet(BitterParticles.BOOM.get(), BoomParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(@NotNull RegisterGuiLayersEvent event) {
        event.registerAbove(Identifier.withDefaultNamespace("camera_overlays"),
                Bittermelon.identifier("rage_overlay"), new RageRenderer());
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerModelType skin : event.getSkins()) {
            AvatarRenderer<AbstractClientPlayer> renderer = event.getPlayerRenderer(skin);
            if (renderer == null) return;
            renderer.addLayer(new EyeballOnPlayerLayer(renderer, event.getEntityModels()));
            renderer.addLayer(new SCP815BloodLayer(renderer));
            renderer.addLayer(new LimbLayer<>(renderer));
            PlayerModel model = renderer.getModel();
            registerBodyPartModel(BodyParts.HEAD, model.head);
            registerBodyPartModel(BodyParts.TORSO, model.body);
            registerBodyPartModel(BodyParts.RIGHT_ARM, model.rightArm);
            registerBodyPartModel(BodyParts.LEFT_ARM, model.leftArm);
            registerBodyPartModel(BodyParts.RIGHT_LEG, model.rightLeg);
            registerBodyPartModel(BodyParts.LEFT_LEG, model.leftLeg);
        }

        LivingEntityRenderer<?, ?, ?> skeletonRenderer = (LivingEntityRenderer<?, ?, ?>) event.getRenderer(EntityType.SKELETON);
        registerBodyPartModel(BodyParts.SKELETON_LEFT_ARM,
                skeletonRenderer.getModel().root().getChild("left_arm"),
                Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png"));

        LivingEntityRenderer<?, ?, ?> zombieRenderer = (LivingEntityRenderer<?, ?, ?>) event.getRenderer(EntityType.ZOMBIE);
        registerBodyPartModel(BodyParts.ZOMBIE_RIGHT_ARM,
                zombieRenderer.getModel().root().getChild("right_arm"),
                Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png"));
    }

    @SubscribeEvent
    public static void registerDebugRenderers(RegisterDebugRenderersEvent event) {
        event.register(new AnatomyDebugRenderer(Minecraft.getInstance()));
    }

    @SubscribeEvent
    public static void registerPictureInPictureRenderers(@NotNull RegisterPictureInPictureRenderersEvent event) {
        event.register(
                AnatomyPictureInPictureRenderer.RenderState.class,
                AnatomyPictureInPictureRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        FluidModel.Unbaked substanceModel = new FluidModel.Unbaked(
                new Material(Identifier.withDefaultNamespace("block/water_still")),
                new Material(Identifier.withDefaultNamespace("block/water_flow")),
                new Material(Identifier.withDefaultNamespace("block/water_overlay")),
                new SubstanceTintSource(),
                new SubstanceFluidRenderer()
        );

        event.register(substanceModel, BitterFluids.SUBSTANCE_FLUID);
    }

    @SubscribeEvent
    public static void registerClientPayloadHandlers(@NotNull RegisterClientPayloadHandlersEvent event) {
        event.register(
                SetLastTypingTime.TYPE,
                ClientPayloadHandler::setLastTypingTime
        );

        event.register(
                AddPrivilege.TYPE,
                PrivilegeClientPayloadHandler::handleAddPrivilege
        );

        event.register(
                RemovePrivilege.TYPE,
                PrivilegeClientPayloadHandler::handleRemovePrivilege
        );

        event.register(
                AddPrivilegeGroup.TYPE,
                PrivilegeClientPayloadHandler::handleAddPrivilegeGroup
        );

        event.register(
                RemovePrivilegeGroup.TYPE,
                PrivilegeClientPayloadHandler::handleRemovePrivilegeGroup
        );

        event.register(
                SetPrivilegeForEntry.TYPE,
                PrivilegeClientPayloadHandler::setPrivilegeForEntry
        );

        event.register(
                RemovePrivilegeForEntry.TYPE,
                PrivilegeClientPayloadHandler::removePrivilegeForEntry
        );

        event.register(
                SetPrivilegeForGroup.TYPE,
                PrivilegeClientPayloadHandler::setPrivilegeForGroup
        );

        event.register(
                RemovePrivilegeForGroup.TYPE,
                PrivilegeClientPayloadHandler::removePrivilegeForGroup
        );

        event.register(
                AddPersonnelEntry.TYPE,
                PersonnelClientPayloadHandler::addPersonnelEntry
        );

        event.register(
                RemovePersonnelEntry.TYPE,
                PersonnelClientPayloadHandler::removePersonnelEntry
        );

        event.register(
                UpdatePersonnelEntry.TYPE,
                PersonnelClientPayloadHandler::updatePersonnelEntry
        );

        event.register(
                UpdateCompartments.TYPE,
                UpdateCompartments::handle
        );

        event.register(
                InsertCompartment.TYPE,
                InsertCompartment::handle
        );

        event.register(
                AddAndInsertCompartment.TYPE,
                AddAndInsertCompartment::handle
        );

        event.register(
                RemoveCompartment.TYPE,
                RemoveCompartment::handle
        );

        event.register(
                UpdateCharacter.TYPE,
                UpdateCharacter::handle
        );
    }

    @SubscribeEvent
    public static void onConfigureRenderTarget(@NotNull ConfigureMainRenderTargetEvent event) {
        event.enableStencil();
    }
}
