package com.site21.bittermelon.common.systems.fluid.substance.client;

import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlockEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public class ClientSubstanceFluid implements IClientFluidTypeExtensions {
    private static final float FOG_END = 6.0f;

    @Override
    public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
        if (level.getBlockEntity(BlockPos.containing(camera.position())) instanceof SubstanceFluidBlockEntity fluidBE) {
            fluidFogColor.set(ARGB.vector4fFromARGB32(fluidBE.getColor()));
        }
    }

    @Override
    public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
        fogData.environmentalEnd = FOG_END;
        fogData.skyEnd = FOG_END;
        fogData.cloudEnd = FOG_END;
    }
}
