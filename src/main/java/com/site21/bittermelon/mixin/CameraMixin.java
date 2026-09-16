package com.site21.bittermelon.mixin;

import com.github.stephengold.joltjni.RVec3;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.site21.bittermelon.common.content.entities.ragdoll.RagdollEntity;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class CameraMixin {
    @WrapOperation(
            method = "alignWithEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V")
    )
    private void bittermelon$offsetCamera(Camera camera, double x, double y, double z, Operation<Void> original,
                                          @Local(argsOnly = true) float partialTicks) {
        if (!(camera.entity().getVehicle() instanceof RagdollEntity ragdollEntity)) {
            original.call(camera, x, y, z);
            return;
        }

        RVec3 prevPos = ragdollEntity.getPrevPos(0);
        RVec3 curPos = ragdollEntity.getCurPos(0);

        Vec3 offset = new Vec3(
                Mth.lerp(partialTicks, prevPos.xx(), curPos.xx()),
                Mth.lerp(partialTicks, prevPos.yy(), curPos.yy()),
                Mth.lerp(partialTicks, prevPos.zz(), curPos.zz())
        );

        original.call(camera, offset.x, offset.y, offset.z);
    }
}
