package com.site21.bittermelon.client.event;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.character.networking.OpenCharacterScreenC2S;
import com.site21.bittermelon.common.systems.medical.legacy.client.HealthScreen;
import com.site21.bittermelon.common.systems.ragdoll.networking.RagdollPlayer;
import com.site21.bittermelon.common.systems.throwing.ThrowItemPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import static com.site21.bittermelon.init.neoforge.BitterKeyBindings.*;

@EventBusSubscriber(modid = Bittermelon.MOD_ID, value = Dist.CLIENT)
public class KeyEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        assert player != null;

        if (THROW_ITEM_KEY.get().consumeClick()) {
            player.swing(InteractionHand.MAIN_HAND);
            ClientPacketDistributor.sendToServer(new ThrowItemPacket(player.getUUID()));
        } else if (HEALTH_SCREEN_KEY.get().consumeClick()) {
            HealthScreen.openHealthScreen();
        } else if (CHARACTER_KEY.get().consumeClick()) {
            ClientPacketDistributor.sendToServer(new OpenCharacterScreenC2S(player.getUUID()));
        } else if (RAGDOLL_KEY.get().consumeClick()) {
            ClientPacketDistributor.sendToServer(new RagdollPlayer(player.getUUID()));
        }
    }
}
