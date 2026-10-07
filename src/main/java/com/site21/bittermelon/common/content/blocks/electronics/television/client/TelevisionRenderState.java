package com.site21.bittermelon.common.content.blocks.electronics.television.client;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class TelevisionRenderState extends BlockEntityRenderState {
    boolean powered;
    Transformation transformation;
    boolean standing;
    RenderType renderType;
    TextureAtlasSprite sprite;
}
