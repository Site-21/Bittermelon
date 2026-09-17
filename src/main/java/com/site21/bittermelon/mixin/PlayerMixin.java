package com.site21.bittermelon.mixin;

import com.site21.bittermelon.common.content.entities.ragdoll.RagdollUtil;
import com.site21.bittermelon.common.systems.carry.CarryHandler;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    public PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    // TODO: This is very bad mixin practice. Will be incompatible with other mods that modify this method.
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, @NotNull EntityDimensions dimensions, float partialTick) {
        Entity carriedPassenger = CarryHandler.getCarried(this);

        if (entity == carriedPassenger) {
            float yOffset = dimensions.height() / 2f - entity.getDimensions(getPose()).height() / 2;

            if (getPose() == Pose.CROUCHING) {
                yOffset = dimensions.height() * 0.85f;
            } else if (getPose() == Pose.SWIMMING || getPose() == Pose.FALL_FLYING) {
                yOffset = dimensions.height() * 0.4f;
            }

            double horizontalOffset = 0.55;

            double yawRad = Math.toRadians(yBodyRot);
            double xOffset = -Math.sin(yawRad) * horizontalOffset;
            double zOffset = Math.cos(yawRad) * horizontalOffset;

            return new Vec3(xOffset, yOffset, zOffset);
        }

        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick);
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);

        Entity carriedPassenger = CarryHandler.getCarried(this);
        if (passenger == carriedPassenger) {
            passenger.setYRot(this.getYRot());
            if (passenger instanceof LivingEntity living) {
                living.setYBodyRot(this.yBodyRot);
            }
        }
    }

    @Inject(method = "wantsToStopRiding()Z", at = @At("TAIL"), cancellable = true)
    private void wantsToStopRiding(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() && !RagdollUtil.isRagdolled(this));
    }
}
