package com.site21.bittermelon.common.content.blocks.electronics.television.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import com.site21.bittermelon.common.content.blocks.electronics.television.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.WallAndGroundTransformations;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class TelevisionRenderer implements BlockEntityRenderer<TelevisionBlockEntity, TelevisionRenderState> {
    public static final WallAndGroundTransformations<Transformation> TRANSFORMATIONS = new WallAndGroundTransformations<>(
            TelevisionRenderer::createWallTransformation, TelevisionRenderer::createGroundTransformation, 16
    );

    public TelevisionRenderer(BlockEntityRendererProvider.@NotNull Context context) {
    }

    @Override
    public void extractRenderState(TelevisionBlockEntity blockEntity, TelevisionRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.powered = blockState.getValue(TelevisionBlock.POWERED);
        if (blockState.getBlock() instanceof StandingTelevisionBlock) {
            state.transformation = TRANSFORMATIONS.freeTransformations(blockState.getValue(StandingTelevisionBlock.ROTATION));
            state.standing = true;
        } else {
            state.transformation = TRANSFORMATIONS.wallTransformation(blockState.getValue(WallTelevisionBlock.FACING));
            state.standing = false;
        }

        SpriteId spriteId = MediaSheets.getMaterial(blockEntity.getMedia());
        state.renderType = spriteId.renderType(RenderTypes::entitySolid);
        state.sprite = Minecraft.getInstance().getAtlasManager().get(spriteId);
    }

    @Override
    public TelevisionRenderState createRenderState() {
        return new TelevisionRenderState();
    }

    @Override
    public void submit(TelevisionRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.powered) return;

        float yOffset = state.standing ? 0.125f : 0.128f;
        collector.submitCustomGeometry(
                poseStack,
                state.renderType,
                (pose, buffer) -> submitTelevision(state, pose, buffer, yOffset)
        );
    }

    private void submitTelevision(TelevisionRenderState state, PoseStack.Pose pose, VertexConsumer buffer, float yOffset) {
        pose.mulPose(state.transformation);

        Vector3f normal = pose.normal().transform(new Vector3f(0, 0, -1));
        float size = 0.33f;
        float u0 = state.sprite.getU0();
        float u1 = state.sprite.getU1();
        float v0 = state.sprite.getV0();
        float v1 = state.sprite.getV1();

        addVertex(buffer, pose, -size, -size + yOffset, 0, u0, v1, normal);
        addVertex(buffer, pose, size, -size + yOffset, 0, u1, v1, normal);
        addVertex(buffer, pose, size, size, 0, u1, v0, normal);
        addVertex(buffer, pose, -size, size, 0, u0, v0, normal);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, Vector3f normal) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(pose, normal.x, normal.y, normal.z);
    }

    private static Transformation createWallTransformation(Direction direction) {
        return new Transformation(
                new Matrix4f()
                        .translation(0.5f, 0.5f, 0.5f)
                        .rotate(Axis.YP.rotationDegrees(-direction.toYRot()))
                        .translate(0, 0, 0.251f)
        );
    }

    private static Transformation createGroundTransformation(int rotation) {
        return new Transformation(
                new Matrix4f()
                        .translation(0.5f, 0.5f, 0.5f)
                        .rotate(Axis.YP.rotationDegrees(-rotation * 22.5f + 180f))
                        .translate(0.0f, -0.125f, 0.381f)
        );
    }
}
