package com.site21.bittermelon.common.content.entities.ragdoll.client;

import com.github.stephengold.joltjni.Quat;
import com.github.stephengold.joltjni.RVec3;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerSkin;

public class RagdollRenderState extends EntityRenderState {
    public final RVec3[] partPositions = new RVec3[6];
    public final Quat[] partRotations = new Quat[6];
    public PlayerSkin skin = DefaultPlayerSkin.getDefaultSkin();

    public RagdollRenderState() {
        for (int i = 0; i < 6; i++) {
            partPositions[i] = new RVec3();
            partRotations[i] = new Quat();
        }
    }
}
