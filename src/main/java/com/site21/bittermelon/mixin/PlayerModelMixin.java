package com.site21.bittermelon.mixin;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.player.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {
    @Inject(method = "createMesh", at = @At("RETURN"))
    private static void replaceArmsWithHands(
            CubeDeformation scale,
            boolean slim,
            CallbackInfoReturnable<MeshDefinition> cir
    ) {
        MeshDefinition mesh = cir.getReturnValue();
        PartDefinition root = mesh.getRoot();

        if (slim) {
            PartDefinition leftArm = root.addOrReplaceChild(
                    "left_arm",
                    CubeListBuilder.create()
                            .texOffs(32, 48)
                            .addBox(-1.0F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, scale),
                    PartPose.offset(5.0F, 2.0F, 0.0F)
            );
            leftArm.addOrReplaceChild(
                    "left_sleeve",
                    CubeListBuilder.create()
                            .texOffs(48, 48)
                            .addBox(-1.0F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, scale.extend(0.25F)),
                    PartPose.ZERO
            );
            leftArm.addOrReplaceChild(
                    "left_hand",
                    CubeListBuilder.create()
                            .texOffs(32, 48)
                            .addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale.extend(-0.001F)),
                    PartPose.ZERO
            );

            PartDefinition rightArm = root.addOrReplaceChild(
                    "right_arm",
                    CubeListBuilder.create()
                            .texOffs(40, 16)
                            .addBox(-2.0F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, scale),
                    PartPose.offset(-5.0F, 2.0F, 0.0F)
            );
            rightArm.addOrReplaceChild(
                    "right_sleeve",
                    CubeListBuilder.create()
                            .texOffs(40, 32)
                            .addBox(-2.0F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, scale.extend(0.25F)),
                    PartPose.ZERO
            );
            rightArm.addOrReplaceChild(
                    "right_hand",
                    CubeListBuilder.create()
                            .texOffs(40, 16)
                            .addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale.extend(-0.001F)),
                    PartPose.ZERO
            );

        } else {
            PartDefinition leftArm = root.addOrReplaceChild(
                    "left_arm",
                    CubeListBuilder.create()
                            .texOffs(32, 48)
                            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 9.0F, 4.0F, scale), // 9 instead of 12
                    PartPose.offset(5.0F, 2.0F, 0.0F)
            );
            leftArm.addOrReplaceChild(
                    "left_sleeve",
                    CubeListBuilder.create()
                            .texOffs(48, 48)
                            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 9.0F, 4.0F, scale.extend(0.25F)),
                    PartPose.ZERO
            );
            leftArm.addOrReplaceChild(
                    "left_hand",
                    CubeListBuilder.create()
                            .texOffs(32, 48)
                            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale.extend(-0.001F)),
                    PartPose.ZERO
            );

            PartDefinition rightArm = root.addOrReplaceChild(
                    "right_arm",
                    CubeListBuilder.create()
                            .texOffs(40, 16)
                            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 9.0F, 4.0F, scale),
                    PartPose.offset(-5.0F, 2.0F, 0.0F)
            );
            rightArm.addOrReplaceChild(
                    "right_sleeve",
                    CubeListBuilder.create()
                            .texOffs(40, 32)
                            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 9.0F, 4.0F, scale.extend(0.25F)),
                    PartPose.ZERO
            );
            rightArm.addOrReplaceChild(
                    "right_hand",
                    CubeListBuilder.create()
                            .texOffs(40, 16)
                            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale.extend(-0.001F)),
                    PartPose.ZERO
            );
        }
    }
}
