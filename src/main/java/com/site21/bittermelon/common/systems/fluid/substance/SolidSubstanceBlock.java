package com.site21.bittermelon.common.systems.fluid.substance;

import com.site21.bittermelon.common.content.blocks.properties.BitterStateProperties;
import com.site21.bittermelon.common.systems.blockdamage.BlockDamageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;
import org.jspecify.annotations.Nullable;

public class SolidSubstanceBlock extends Block implements EntityBlock {
    public static final IntegerProperty LEVEL = BitterStateProperties.LEVEL;
    public static final VoxelShape[] SHAPES = createShapes();
    private static final int MAX_BREAKABLE_LEVEL = 5;
    private static final float MAX_CRACK_CHANCE = 0.2f;
    private static final float MIN_CRACK_CHANCE = 0.005f;

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

    @Override
    protected boolean skipRendering(BlockState state, BlockState neighbor, Direction direction) {
        if (!neighbor.is(this)) return super.skipRendering(state, neighbor, direction);
        int level = state.getValue(LEVEL);
        int neighborLevel = neighbor.getValue(LEVEL);
        return switch (direction) {
            case UP -> level == 20;
            case DOWN -> neighborLevel == 20;
            default -> neighborLevel >= level;
        };
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState onState, Entity entity) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(entity instanceof Player) && !EventHooks.canEntityGrief(serverLevel, entity)) return;

        int layer = onState.getValue(LEVEL);
        if (level.getFluidState(pos.below()).isEmpty() || layer > MAX_BREAKABLE_LEVEL) return;

        float carefulMultiplier = entity.isSteppingCarefully() ? 0.5f : 1.0f;
        float chance = Mth.clampedMap(layer, 1, MAX_BREAKABLE_LEVEL, MAX_CRACK_CHANCE, MIN_CRACK_CHANCE) * carefulMultiplier;
        if (level.getRandom().nextFloat() < chance) {
            BlockDamageUtil.addDamage(serverLevel, pos, 10);
            level.playSound(
                    null,
                    pos,
                    SoundEvents.TURTLE_EGG_CRACK,
                    SoundSource.BLOCKS,
                    0.7f,
                    1.0f + level.getRandom().nextFloat() * 0.2f
            );
        }

        super.stepOn(level, pos, onState, entity);
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
