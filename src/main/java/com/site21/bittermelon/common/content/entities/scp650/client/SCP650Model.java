package com.site21.bittermelon.common.content.entities.scp650.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SCP650Model extends EntityModel<SCP650RenderState> {

    public SCP650Model(@NotNull ModelPart root) {
        super(root);
    }

    public static @NotNull LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 8).addBox(-5.0F, -12.0F, -1.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -12.0F, 0.0F));

        PartDefinition stomach = torso.addOrReplaceChild("stomach", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -4.0F, 0.05F, 6.0F, 4.0F, 4.0F, new CubeDeformation(-0.2F, 0.0F, -0.25F)), PartPose.offset(-1.0F, -4.0F, -1.0F));

        PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -5.0F, -1.5F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -13.0F, 1.0F));

        PartDefinition neck = head.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_arm = torso.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(24, 30).addBox(-0.9F, -0.1F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.9F, -11.9F, 1.0F));

        PartDefinition upper_left_arm = left_arm.addOrReplaceChild("upper_left_arm", CubeListBuilder.create().texOffs(8, 26).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(1.1F, 1.9F, 0.0F));

        PartDefinition lower_left_arm = upper_left_arm.addOrReplaceChild("lower_left_arm", CubeListBuilder.create().texOffs(0, 34).addBox(-1.0F, -1.1667F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(8, 34).addBox(-1.0F, 0.3333F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.1F))
                .texOffs(0, 42).mirror().addBox(-1.0F, 4.8333F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)).mirror(false), PartPose.offset(0.0F, 5.1667F, 0.0F));

        PartDefinition right_arm = torso.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 26).mirror().addBox(-2.1F, -0.1F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)).mirror(false), PartPose.offset(-4.9F, -11.9F, 1.0F));

        PartDefinition upper_right_arm = right_arm.addOrReplaceChild("upper_right_arm", CubeListBuilder.create().texOffs(16, 26).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offset(-1.1F, 1.9F, 0.0F));

        PartDefinition lower_right_arm = upper_right_arm.addOrReplaceChild("lower_right_arm", CubeListBuilder.create().texOffs(0, 30).mirror().addBox(-1.0F, -1.1667F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)).mirror(false)
                .texOffs(16, 34).mirror().addBox(-1.0F, 0.3333F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.1F)).mirror(false)
                .texOffs(0, 38).addBox(-1.0F, 4.8333F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 5.1667F, 0.0F));

        PartDefinition hip = body.addOrReplaceChild("hip", CubeListBuilder.create().texOffs(20, 18).addBox(-3.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.0F, 1.0F));

        PartDefinition left_leg = hip.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(20, 10).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(24, 34).addBox(-1.0F, 4.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(2.0F, 2.0F, 0.0F));

        PartDefinition lower_left_leg = left_leg.addOrReplaceChild("lower_left_leg", CubeListBuilder.create().texOffs(24, 3).mirror().addBox(-1.0F, 0.0667F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.1F)).mirror(false)
                .texOffs(24, 39).addBox(-1.0F, 2.8667F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 6.4333F, 0.0F));

        PartDefinition right_leg = hip.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(28, 10).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.01F)).mirror(false)
                .texOffs(32, 34).mirror().addBox(-1.0F, 4.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(-0.2F)).mirror(false), PartPose.offset(-2.0F, 2.0F, 0.0F));

        PartDefinition lower_right_leg = right_leg.addOrReplaceChild("lower_right_leg", CubeListBuilder.create().texOffs(16, 3).addBox(-1.0F, 0.0667F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.1F))
                .texOffs(32, 39).addBox(-1.0F, 2.8667F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 6.4333F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull SCP650RenderState renderState) {
        super.setupAnim(renderState);

        renderState.pose.bake(root()).applyStatic();
    }
}