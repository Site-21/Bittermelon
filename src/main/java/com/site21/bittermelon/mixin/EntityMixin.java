package com.site21.bittermelon.mixin;

import com.site21.bittermelon.common.systems.fluid.substance.SolidSubstanceBlock;
import com.site21.bittermelon.common.systems.ragdoll.RagdollEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Redirect(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z")
    )
    private boolean bittermelon$skipSerializeCheckWhenForced(EntityType<?> type, Entity entityToRide, boolean force, boolean sendEventAndTriggers) {
        return force || type.canSerialize();
    }

    @Shadow
    public Optional<BlockPos> mainSupportingBlockPos;

    @Shadow
    public abstract @Nullable Entity getVehicle();

    @Shadow
    public abstract Level level();

    @Inject(method = "getBlockPosBelowThatAffectsMyMovement", at = @At("HEAD"), cancellable = true)
    private void bittermelon$solidSubstanceAffectsMovement(CallbackInfoReturnable<BlockPos> cir) {
        mainSupportingBlockPos.ifPresent(pos -> {
            if (level().getBlockState(pos).getBlock() instanceof SolidSubstanceBlock) {
                cir.setReturnValue(pos);
            }
        });
    }

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
