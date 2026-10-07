package com.site21.bittermelon.common.content.blocks.barrel.client;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class DrumBlockRenderState extends BlockEntityRenderState {
    public Direction facing;
    public float fillLevel;
    public int color;
    public TextureAtlasSprite sprite;
    public float rotation;
    public @Nullable MovingBlockRenderState block;
    public Direction moveDirection;
}
