package com.site21.bittermelon.common.content.blocks.barrel.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.site21.bittermelon.common.content.blocks.barrel.DrumBlock;
import com.site21.bittermelon.common.content.blocks.barrel.DrumBlockEntity;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluid;
import com.site21.bittermelon.init.neoforge.BitterFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class DrumBlockRenderer implements BlockEntityRenderer<DrumBlockEntity, DrumBlockRenderState> {

    public DrumBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public DrumBlockRenderState createRenderState() {
        return new DrumBlockRenderState();
    }

    @Override
    public void extractRenderState(DrumBlockEntity blockEntity, DrumBlockRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(DrumBlock.FACING);
        state.fillLevel = Math.min(1.0f, blockEntity.getMixture().getVolume() / (float) SubstanceFluid.FULL_BLOCK_VOLUME);
        state.color = blockEntity.getMixture().getColor();
        state.sprite = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(BitterFluids.SUBSTANCE_FLUID.get().defaultFluidState()).stillMaterial().sprite();

        Direction moveDirection = blockEntity.getMoveDirection();
        BlockState blockState = blockEntity.getBlockState();
        if (blockEntity.getLevel() instanceof ClientLevel level && moveDirection != null && blockState.getValue(DrumBlock.ROLLING)) {
            state.rotation = blockEntity.getRotationProgress(partialTicks);
            BlockPos pos = blockEntity.getBlockPos().relative(moveDirection).above(); // a bit weird to get position above, but otherwise the lighting will look dark
            state.block = createMovingBlock(pos, blockState, level.getBiome(pos), level);
            state.moveDirection = moveDirection;
        }
    }

    private static MovingBlockRenderState createMovingBlock(BlockPos pos, BlockState blockState, Holder<Biome> biome, ClientLevel level) {
        MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
        movingBlockRenderState.randomSeedPos = pos;
        movingBlockRenderState.blockPos = pos;
        movingBlockRenderState.blockState = blockState;
        movingBlockRenderState.biome = biome;
        movingBlockRenderState.cardinalLighting = level.cardinalLighting();
        movingBlockRenderState.lightEngine = level.getLightEngine();
        return movingBlockRenderState;
    }

    @Override
    public void submit(DrumBlockRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.block != null) {
            Direction dir = state.moveDirection;
            float angle = 90f * state.rotation * dir.getAxisDirection().getStep();

            Quaternionf rot = switch (dir.getAxis()) {
                case X -> Axis.ZP.rotationDegrees(-angle);
                case Z -> Axis.XP.rotationDegrees(angle);
                default -> new Quaternionf();
            };

            double px = dir == Direction.EAST ? 1 : 0;
            double pz = dir == Direction.SOUTH ? 1 : 0;

            poseStack.pushPose();
            poseStack.translate(px, 0, pz);
            poseStack.mulPose(rot);
            poseStack.translate(-px, 0, -pz);
            collector.submitMovingBlock(poseStack, state.block);
            submitFluid(state, poseStack, collector);
            poseStack.popPose();
            return;
        }

        submitFluid(state, poseStack, collector);
    }

    private void submitFluid(DrumBlockRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.fillLevel <= 0) return;
        collector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, buffer) -> {
            float min = 0.1f;
            float max = 0.9f;
            float y0 = 0.125f;
            float y1 = y0 + state.fillLevel * 0.75f;
            float u0 = state.sprite.getU0();
            float u1 = state.sprite.getU1();
            float v0 = state.sprite.getV0();
            float v1 = state.sprite.getV1();
            int color = state.color;
            int light = state.lightCoords;

            vertex(pose, buffer, min, y1, min, u0, v0, color, light, 0, 1, 0);
            vertex(pose, buffer, min, y1, max, u0, v1, color, light, 0, 1, 0);
            vertex(pose, buffer, max, y1, max, u1, v1, color, light, 0, 1, 0);
            vertex(pose, buffer, max, y1, min, u1, v0, color, light, 0, 1, 0);

            float vTop = v1 - (v1 - v0) * state.fillLevel;
            switch (state.facing) {
                case NORTH -> side(pose, buffer, max, min, min, min, y0, y1, u0, u1, v1, vTop, color, light, 0, 0, -1);
                case SOUTH -> side(pose, buffer, min, max, max, max, y0, y1, u0, u1, v1, vTop, color, light, 0, 0, 1);
                case WEST -> side(pose, buffer, min, min, min, max, y0, y1, u0, u1, v1, vTop, color, light, -1, 0, 0);
                case EAST -> side(pose, buffer, max, max, max, min, y0, y1, u0, u1, v1, vTop, color, light, 1, 0, 0);
            }
        });
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, float x, float y, float z, float u,
                                   float v, int color, int light, float nx, float ny, float nz) {
        buffer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }

    private void side(PoseStack.Pose pose, VertexConsumer buffer,
                      float x0, float z0, float x1, float z1,
                      float y0, float y1, float u0, float u1, float vBottom, float vTop,
                      int color, int light, float nx, float ny, float nz) {
        vertex(pose, buffer, x0, y0, z0, u0, vBottom, color, light, nx, ny, nz);
        vertex(pose, buffer, x1, y0, z1, u1, vBottom, color, light, nx, ny, nz);
        vertex(pose, buffer, x1, y1, z1, u1, vTop, color, light, nx, ny, nz);
        vertex(pose, buffer, x0, y1, z0, u0, vTop, color, light, nx, ny, nz);
    }
}
