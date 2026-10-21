package com.site21.bittermelon.common.systems.fluid.substance;

import com.site21.bittermelon.common.content.blocks.properties.BitterStateProperties;
import com.site21.bittermelon.init.neoforge.BitterParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
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
    public static final BooleanProperty BUBBLING = BitterStateProperties.BUBBLING;

    private static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 0.05, 1);
    private final List<FluidState> stateCache;
    private final SubstanceFluid fluid;

    public SubstanceFluidBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(LEVEL, 20)
                .setValue(LIT, false)
                .setValue(BUBBLING, false)
        );
        this.stateCache = new ArrayList<>();
        this.fluid = SUBSTANCE_FLUID.get();

        for (int i = 1; i <= 20; i++) {
            stateCache.add(fluid.defaultFluidState().setValue(LEVEL, i));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, LIT, BUBBLING);
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
        level.setBlock(pos, state.setValue(LEVEL, fluidState.getAmount()), 3);
        return true;
    }

    private void tick(Level level, BlockPos pos) {
        if (!level.getFluidTicks().hasScheduledTick(pos, fluid)) {
            level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            if (random.nextInt(24) == 0) {
                level.playLocalSound(
                        pos.getX() + 0.5f,
                        pos.getY() + 0.5f,
                        pos.getZ() + 0.5f,
                        SoundEvents.FIRE_AMBIENT,
                        SoundSource.BLOCKS,
                        1.0f + random.nextFloat(),
                        random.nextFloat() * 0.7f + 0.3f,
                        false
                );
            }

            for (int i = 0; i < 3; ++i) {
                level.addParticle(
                        ParticleTypes.LARGE_SMOKE,
                        pos.getX() + random.nextFloat(),
                        pos.getY() + random.nextFloat() * 0.5f + 0.5f,
                        pos.getZ() + random.nextFloat(),
                        0.0f,
                        0.0f,
                        0.0f
                );
            }
        }

        if (state.getValue(BUBBLING)) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            level.addAlwaysVisibleParticle(
                    BitterParticles.BUBBLE.get(),
                    x + 0.5,
                    y,
                    z + 0.5,
                    0.0,
                    0.04,
                    0.0
            );

            float fluidHeight = level.getFluidState(pos).getHeight(level, pos);

            level.addAlwaysVisibleParticle(
                    BitterParticles.BUBBLE.get(),
                    x + random.nextFloat(),
                    y + random.nextFloat() * fluidHeight,
                    z + random.nextFloat(),
                    0.0,
                    0.04,
                    0.0
            );

            if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE && level.getBlockState(pos.above()).isAir()) {
                // TODO: Should use gas color instead of fluid color
                level.addParticle(
                        new DustParticleOptions(fluidBE.getColor(), 2.0f),
                        pos.getX() + random.nextFloat(),
                        pos.getY() + 0.5 + fluidHeight,
                        pos.getZ() + random.nextFloat(),
                        10,
                        0.5,
                        1.0
                );
            }

            // TODO: Better sound
            level.playLocalSound(
                    x + 0.5f,
                    y + 0.5f,
                    z + 0.5f,
                    SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                    SoundSource.BLOCKS,
                    0.2f,
                    0.5f,
                    false
            );
        }
    }
}
