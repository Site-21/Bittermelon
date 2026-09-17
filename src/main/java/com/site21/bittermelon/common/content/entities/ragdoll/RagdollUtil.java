package com.site21.bittermelon.common.content.entities.ragdoll;

import com.site21.bittermelon.init.neoforge.BitterEntities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class RagdollUtil {
    public static void ragdollPlayer(ServerPlayer player) {
        ragdollPlayer(player, player.getDeltaMovement());
    }

    public static void ragdollPlayer(ServerPlayer player, Vec3 motion) {
        RagdollEntity ragdoll = spawnRagdoll(player.level(), player.position(), motion);
        player.setCamera(ragdoll);
        player.setGameMode(GameType.SPECTATOR);
    }

    public static void ragdollWithDiscard(LivingEntity entity) {
        ragdollWithDiscard(entity, entity.getDeltaMovement());
    }

    public static void ragdollWithDiscard(LivingEntity entity, Vec3 motion) {
        spawnRagdoll(entity.level(), entity.position(), motion);
        entity.discard();
    }

    public static RagdollEntity spawnRagdoll(Level level, Vec3 pos, Vec3 velocity) {
        RagdollEntity ragdoll = BitterEntities.RAGDOLL.get().create(level, EntitySpawnReason.EVENT);
        assert ragdoll != null;
        ragdoll.setPos(pos.x, pos.y + 1, pos.z);
        ragdoll.addMotion(velocity);
        level.addFreshEntity(ragdoll);
        return ragdoll;
    }
}
