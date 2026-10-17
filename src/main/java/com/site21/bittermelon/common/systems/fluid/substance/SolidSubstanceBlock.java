package com.site21.bittermelon.common.systems.fluid.substance;

import com.site21.bittermelon.common.content.blocks.properties.BitterStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class SolidSubstanceBlock extends Block implements EntityBlock {
    public static final IntegerProperty LEVEL = BitterStateProperties.LEVEL;
    public static final VoxelShape[] SHAPES = createShapes();

    public SolidSubstanceBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(LEVEL, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolidSubstanceBlockEntity(blockPos, blockState);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(LEVEL)];
    }

    public static VoxelShape[] createShapes() {
        VoxelShape[] shapes = new VoxelShape[21];
        for (int i = 0; i <= 20; i++) {
            double height = i / 20.0;
            shapes[i] = Block.box(0, 0, 0, 16, height * 16, 16);
        }
        return shapes;
    }
}
