package com.site21.bittermelon.common.content.entities.ragdoll.client;

import com.github.stephengold.joltjni.Quat;
import com.github.stephengold.joltjni.RVec3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.site21.bittermelon.common.content.entities.ragdoll.RagdollEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class RagdollRenderer extends EntityRenderer<RagdollEntity, RagdollRenderState> {
    private final ModelPart head, torso, leftArm, rightArm, leftLeg, rightLeg;
    private final Quaternionf quatA = new Quaternionf();
    private final Quaternionf quatB = new Quaternionf();

    public RagdollRenderer(EntityRendererProvider.Context context) {
        super(context);
        PlayerModel model = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
        head = model.head;
        torso = model.body;
        leftArm = model.leftArm;
        rightArm = model.rightArm;
        leftLeg = model.leftLeg;
        rightLeg = model.rightLeg;
    }

    @Override
    public RagdollRenderState createRenderState() {
        return new RagdollRenderState();
    }

    @Override
    public void extractRenderState(RagdollEntity entity, RagdollRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        Vec3 entityPos = entity.getPosition(partialTicks);
        for (int i = 0; i < 6; i++) {
            RVec3 prev = entity.getPrevPos(i);
            RVec3 cur = entity.getCurPos(i);

            state.partPositions[i].set(
                    Mth.lerp(partialTicks, prev.xx(), cur.xx()) - entityPos.x,
                    Mth.lerp(partialTicks, prev.yy(), cur.yy()) - entityPos.y,
                    Mth.lerp(partialTicks, prev.zz(), cur.zz()) - entityPos.z
            );

            Quat prevRot = entity.getPrevRot(i);
            Quat curRot = entity.getCurRot(i);
            quatA.set(prevRot.getX(), prevRot.getY(), prevRot.getZ(), prevRot.getW());
            quatB.set(curRot.getX(), curRot.getY(), curRot.getZ(), curRot.getW());
            quatA.slerp(quatB, partialTicks);
            state.partRotations[i].set(quatA.x, quatA.y, quatA.z, quatA.w);
        }
    }

    @Override
    public void submit(RagdollRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        submitPart(state, 0, head, poseStack, collector);
        submitPart(state, 1, torso, poseStack, collector);
        submitPart(state, 2, leftArm, poseStack, collector);
        submitPart(state, 3, rightArm, poseStack, collector);
        submitPart(state, 4, leftLeg, poseStack, collector);
        submitPart(state, 5, rightLeg, poseStack, collector);

    }

    private void submitPart(RagdollRenderState state, int i, ModelPart part, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();

        RVec3 pos = state.partPositions[i];
        poseStack.translate(pos.xx(), pos.yy(), pos.zz());

        Quat r = state.partRotations[i];
        quatA.set(r.getX(), r.getY(), r.getZ(), r.getW());
        poseStack.mulPose(quatA);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));

        poseStack.translate(-part.x / 16.0f, -part.y / 16.0f, -part.z / 16.0f);

        float offsetY = switch (i) {
            case 0 -> 4.0f / 16.0f;
            case 1, 4, 5 -> -6.0f / 16.0f;
            case 2, 3 -> -4.0f / 16.0f;
            default -> 0.0f;
        };
        poseStack.translate(0.0f, offsetY, 0.0f);

        collector.submitModelPart(part, poseStack, RenderTypes.entityCutout(DefaultPlayerSkin.getDefaultTexture()), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }
}
