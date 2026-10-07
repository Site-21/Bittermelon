package com.site21.bittermelon.common.content.blocks.electronics.television;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.site21.bittermelon.init.custom.Medias.COLOR_TEST;
import static com.site21.bittermelon.init.custom.Medias.MANAPHY;
import static com.site21.bittermelon.init.neoforge.BitterBlockEntities.TELEVISION_BLOCK_ENTITY;

public class TelevisionBlockEntity extends BlockEntity {
    private Holder<Media> media;

    public TelevisionBlockEntity(BlockPos pos, BlockState blockState) {
        super(TELEVISION_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

    }

    public Holder<Media> getMedia() {
        return COLOR_TEST;
    }
}
