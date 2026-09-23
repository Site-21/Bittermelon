package com.site21.bittermelon.common.systems.stumble.client;

import com.site21.bittermelon.common.systems.ragdoll.RagdollUtil;
import com.site21.bittermelon.common.systems.stumble.StumbleHandler;
import com.site21.bittermelon.common.systems.stumble.networking.AttemptToRise;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.UUID;

public class RiseKeyHandler {
    private static int ticksHeld = 0;
    public static final int TICKS_REQUIRED = 25;
    private static boolean keyPressed = false;

    public static void tick() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        UUID uuid = player.getUUID();
        if (RagdollUtil.isRagdolled(player)) {
            if (StumbleHandler.isStunned(player)) return;

            if (Minecraft.getInstance().options.keyJump.isDown()) {
                keyPressed = true;
                ticksHeld++;
                if (ticksHeld >= TICKS_REQUIRED) {
                    ClientPacketDistributor.sendToServer(new AttemptToRise(uuid));
                    keyPressed = false;
                    ticksHeld = 0;
                }
            } else {
                ticksHeld = 0;
                keyPressed = false;
            }
        }
    }

    public static int getTicksHeld() {
        return ticksHeld;
    }

    public static boolean isKeyPressed() {
        return keyPressed;
    }
}