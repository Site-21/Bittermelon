package com.site21.bittermelon.client.event;

import com.github.stephengold.joltjni.Quat;
import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.client.render.FogRenderer;
import com.site21.bittermelon.client.render.ShaderManager;
import com.site21.bittermelon.client.render.TypingIndicatorRenderer;
import com.site21.bittermelon.common.content.blocks.electronics.intercom.client.PhoneTipRenderer;
import com.site21.bittermelon.common.content.entities.ragdoll.RagdollEntity;
import com.site21.bittermelon.common.content.items.wire.client.WireFeatureRenderer;
import com.site21.bittermelon.common.content.items.wire.client.WireOverlayExtractor;
import com.site21.bittermelon.common.systems.atmosphere.data.AtmosInstancesData;
import com.site21.bittermelon.common.systems.blockdamage.client.BlockDamageExtractor;
import com.site21.bittermelon.common.systems.carry.CarryHandler;
import com.site21.bittermelon.common.systems.carry.CarryRenderer;
import com.site21.bittermelon.common.systems.carry.ThrowCarriedEntity;
import com.site21.bittermelon.common.systems.character.CharacterManager;
import com.site21.bittermelon.common.systems.economy.bank.AccountRegistry;
import com.site21.bittermelon.common.systems.personnel.privilege.PrivilegeManager;
import com.site21.bittermelon.common.systems.personnel.registry.PersonnelRegistry;
import com.site21.bittermelon.common.systems.rage.client.ClientRageHandler;
import com.site21.bittermelon.common.systems.stumble.client.RiseKeyHandler;
import com.site21.bittermelon.common.systems.stumble.client.RiseProgressBar;
import com.site21.bittermelon.common.systems.telecomms.intercom.IntercomManager;
import com.site21.bittermelon.init.neoforge.BitterAttachmentTypes;
import com.site21.bittermelon.init.neoforge.BitterMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@EventBusSubscriber(modid = Bittermelon.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        AccountRegistry.clearClientData();
        CharacterManager.clearClientData();
        PersonnelRegistry.clearClientData();
        AtmosInstancesData.clearClientData();
        IntercomManager.clearClientData();
        PrivilegeManager.clearClientData();
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post<?> event) {
        WireFeatureRenderer.submitWire(event.getRenderState(), event.getSubmitNodeCollector(), event.getPoseStack());
    }

    @SubscribeEvent
    public static void onExtractLevelRenderState(ExtractLevelRenderStateEvent event) {
        BlockDamageExtractor.extractBlockDamageRenderStates(event.getRenderState(), event.getLevelRenderer(),
                event.getFrustum(), event.getLevel());
    }

    @SubscribeEvent
    public static void submitCustomGeometry(SubmitCustomGeometryEvent event) {
        CarryRenderer.renderCarriedEntity(event.getLevelRenderState().cameraRenderState, event.getPoseStack(), event.getSubmitNodeCollector());
    }

    @SubscribeEvent
    public static void onRenderHands(RenderHandEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player.hasData(BitterAttachmentTypes.CARRIED_PASSENGER)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderOverlayPre(RenderGuiLayerEvent.@NotNull Pre event) {
        if (event.getName() == VanillaGuiLayers.PLAYER_HEALTH || event.getName() == VanillaGuiLayers.FOOD_LEVEL) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderOverlayPost(RenderGuiLayerEvent.@NotNull Post event) {
        RiseProgressBar.extract(event.getGuiGraphics());
        PhoneTipRenderer.extractPhoneTip(event.getGuiGraphics());
        WireOverlayExtractor.extractWiringOverlay(event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.@NotNull Post<?, ?, ?> event) {
        if (event.getRenderer().getModel() instanceof HumanoidModel<?> model) {
            float renderWidth = event.getRenderState().getRenderDataOrDefault(ClientSetup.ENTITY_WIDTH, 0.0f);

            if (renderWidth > 0f) {
                float widthFactor = Math.min(renderWidth / 0.5f, 2.0f);
                float x = -1.0f;
                float z = 0.005f * widthFactor;
                model.rightArm.xRot = x;
                model.leftArm.xRot = x;
                model.rightArm.zRot = z;
                model.leftArm.zRot = -z;
            }
        }
    }

    @SubscribeEvent
    public static void onFogRender(ViewportEvent.RenderFog event) {
        FogRenderer.onRenderFog(event);
    }

    @SubscribeEvent
    public static void onComputeFogColors(ViewportEvent.ComputeFogColor event) {
        FogRenderer.onFogColor(event);
    }

    @SubscribeEvent
    public static void onCanRenderNameTag(@NotNull RenderNameTagEvent.CanRender event) {
        if (!TypingIndicatorRenderer.canRender(event.getEntity())) {
            event.setCanRender(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void onDoRenderNameTag(@NotNull RenderNameTagEvent.DoRender event) {
        if (Minecraft.getInstance().options.hideGui) return;

        TypingIndicatorRenderer.renderTypingIcon(
                event.getPoseStack(),
                event.getSubmitNodeCollector(),
                event.getEntityRenderState().lightCoords
        );
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        RiseKeyHandler.tick();

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player != null) {
            ClientRageHandler.tick(minecraft, player);
        }
    }

    @SubscribeEvent
    public static void onClientTickPre(ClientTickEvent.Pre event) {
        ShaderManager.updatePostEffects();
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.@NotNull RightClickEmpty event) {
        Player player = event.getEntity();

        if (player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND) {
            Entity carriedEntity = CarryHandler.getCarried(player);
            if (carriedEntity == null) return;

            carriedEntity.stopRiding();
            ClientPacketDistributor.sendToServer(new ThrowCarriedEntity(player.getUUID(), carriedEntity.getUUID()));
        }
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (!(event.getCamera().entity().getVehicle() instanceof RagdollEntity ragdoll)) return;

        Quat prevRot = ragdoll.getPrevRot(0);
        Quat curRot = ragdoll.getCurRot(0);
        Quaternionf q0 = new Quaternionf(prevRot.getX(), prevRot.getY(), prevRot.getZ(), prevRot.getW());
        Quaternionf q1 = new Quaternionf(curRot.getX(), curRot.getY(), curRot.getZ(), curRot.getW());

        Quaternionf rot = q0.slerp(q1, (float) event.getPartialTick()).normalize();
        Vector3f euler = rot.getEulerAnglesYXZ(new Vector3f());
        event.setPitch((float) Math.toDegrees(euler.x));
        event.setYaw((float) -Math.toDegrees(euler.y));
        event.setRoll((float) Math.toDegrees(euler.z));
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player == null || !player.hasEffect(BitterMobEffects.AMNESIA)) return;

        event.getToolTip().clear();
        event.getToolTip().add(Component.literal("???").withStyle(ChatFormatting.DARK_GRAY));
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.SELECTED_ITEM_NAME)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(BitterMobEffects.AMNESIA)) {
            event.setCanceled(true);
        }
    }
}
