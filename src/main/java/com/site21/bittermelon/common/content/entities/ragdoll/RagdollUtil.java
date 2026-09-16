package com.site21.bittermelon.common.content.entities.ragdoll;

import com.site21.bittermelon.init.neoforge.BitterEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class RagdollUtil {
    public static void ragdoll(LivingEntity entity) {
        ragdoll(entity, entity.getDeltaMovement());
    }

    public static void ragdoll(LivingEntity entity, Vec3 motion) {
        spawnRagdoll(entity, entity.level(), entity.position(), motion);
    }

    public static void clearRagdoll(LivingEntity entity) {
        if (entity.getVehicle() instanceof RagdollEntity ragdoll) {
            ragdoll.discard();
        }
    }

    public static void spawnRagdoll(LivingEntity owner, Level level, Vec3 pos, Vec3 velocity) {
        if (isRagdolled(owner)) return;
        RagdollEntity ragdoll = BitterEntities.RAGDOLL.get().create(level, EntitySpawnReason.EVENT);
        assert ragdoll != null;
        ragdoll.setPos(pos.x, pos.y + 1, pos.z);
        ragdoll.addMotion(velocity);
        ragdoll.setOwner(owner);
        level.addFreshEntity(ragdoll);
    }

    public static boolean isRagdolled(Entity entity) {
        return entity.getVehicle() instanceof RagdollEntity ragdoll && ragdoll.getOwner() == entity;
    }
}
