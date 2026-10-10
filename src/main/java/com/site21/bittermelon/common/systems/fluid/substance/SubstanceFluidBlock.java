package com.site21.bittermelon.common.systems.fluid.substance;

import com.site21.bittermelon.common.content.blocks.properties.BitterStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.site21.bittermelon.init.neoforge.BitterFluids.SUBSTANCE_FLUID;

public class SubstanceFluidBlock extends Block implements LiquidBlockContainer, EntityBlock {
    public static final IntegerProperty LEVEL = BitterStateProperties.LEVEL;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 0.05, 1);
    private final List<FluidState> stateCache;
    private final SubstanceFluid fluid;

    public SubstanceFluidBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(LEVEL, 20).setValue(LIT, false));
        this.stateCache = new ArrayList<>();
        this.fluid = SUBSTANCE_FLUID.get();

        for (int i = 1; i <= 20; i++) {
            stateCache.add(fluid.defaultFluidState().setValue(LEVEL, i));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, LIT);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SubstanceFluidBlockEntity(pos, state);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return stateCache.get(state.getValue(LEVEL) - 1);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(LIT) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(LIT) ? SHAPE : Shapes.empty();
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        tick(level, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        tick(level, pos);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity owner, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return true;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
//        level.setBlock(pos, state.setValue(LEVEL, Math.clamp(state.getValue(LEVEL) + fluidState.getValue(LEVEL), 1, 16)), 3);
        level.setBlock(pos, state.setValue(LEVEL, fluidState.getAmount()), 3);
        return true;
    }

    private void tick(Level level, BlockPos pos) {
        if (!level.getFluidTicks().hasScheduledTick(pos, fluid)) {
            level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
        }
    }
}
