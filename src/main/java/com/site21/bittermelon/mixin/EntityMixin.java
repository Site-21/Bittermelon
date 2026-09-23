package com.site21.bittermelon.mixin;

import com.site21.bittermelon.common.systems.ragdoll.RagdollEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

//    @Redirect(
//            method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z")
//    )
//    private boolean skipSerializeCheckWhenForced(EntityType<?> type, Entity vehicle, boolean force) {
//        return force || type.canSerialize();
//    }

    @Shadow
    public abstract @Nullable Entity getVehicle();

    @Inject(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private void bittermelon$eyeFromRagdollHead(float partialTickTime, CallbackInfoReturnable<Vec3> cir) {
        if (getVehicle() instanceof RagdollEntity ragdoll) {
            cir.setReturnValue(ragdoll.getHeadPosition(partialTickTime));
        }
    }

    @Inject(method = "getViewVector", at = @At("HEAD"), cancellable = true)
    private void bittermelon$viewFromRagdollHead(float a, CallbackInfoReturnable<Vec3> cir) {
        if (getVehicle() instanceof RagdollEntity ragdoll) {
            cir.setReturnValue(ragdoll.getHeadForward(a));
        }
    }
}
