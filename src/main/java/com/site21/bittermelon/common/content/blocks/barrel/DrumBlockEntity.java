package com.site21.bittermelon.common.content.blocks.barrel;

import com.site21.bittermelon.common.systems.fluid.substance.MixtureOwner;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluid;
import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import com.site21.bittermelon.init.neoforge.BitterBlockEntities;
import com.site21.bittermelon.init.neoforge.BitterSounds;
import com.site21.bittermelon.util.SubstanceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.site21.bittermelon.common.content.blocks.barrel.DrumBlock.*;
import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;

public class DrumBlockEntity extends BlockEntity implements MixtureOwner {
    private static final float ROT_SPEED = 0.1f;
    private static final float ROT_SPEED_MIN = 0.05f;
    private static final int PRESSURE_THRESHOLD = 100;
    private static final float EXPLOSION_THRESHOLD = 0.4f;
    private static final int PRESSURE_INTERVAL = 100;

    private SubstanceMixture mixture;
    private final Runnable mixtureChangedCallback = this::setChanged;
    private Direction moveDirection;
    private float rot = 1.0f;
    private float rot0 = 1.0f;

    public DrumBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BitterBlockEntities.DRUM_BLOCK_ENTITY.get(), worldPosition, blockState);
        mixture = new SubstanceMixture();
        mixture.setChangedCallback(mixtureChangedCallback);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        tickAnimation();

        if (animationFinished() && moveDirection != null) {
            move(level, pos, state);
        }

        if (level.getGameTime() % 20 != 0) return;

        if (mixture != null) {
            mixture.tickReactions(level, pos);
        }

        potentiallyExplode(level, pos, state);
    }

    private void potentiallyExplode(Level level, BlockPos pos, BlockState state) {
        int pressure = mixture.getPressure();
        if (pressure > PRESSURE_THRESHOLD) {
            if (level.getGameTime() % PRESSURE_INTERVAL == 0) {
                level.playSound(
                        null,
                        pos,
                        BitterSounds.METAL_DRUM_GROAN.value(),
                        SoundSource.BLOCKS,
                        0.25f,
                        0.9f + 0.1f * level.getRandom().nextFloat()
                );

                float pressureRatio = (pressure - PRESSURE_THRESHOLD) / 1000.0f;
                if (level.getRandom().nextFloat() < pressureRatio) {
                    Vec3 explosionPos;
                    if (pressureRatio >= EXPLOSION_THRESHOLD) {
                        SubstanceUtil.replaceWithSubstanceFluid(level, pos, mixture.getSubstances());
                        level.addDestroyBlockEffect(pos, state);
                        explosionPos = Vec3.atCenterOf(pos);
                    } else {
                        Direction facing = state.getValue(DrumBlock.FACING);
                        Direction newFacing = facing == Direction.DOWN
                                ? Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom())
                                : facing;
                        level.setBlock(pos, state.setValue(OPEN, true).setValue(FACING, newFacing), Block.UPDATE_ALL);
                        explosionPos = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(state.getValue(DrumBlock.FACING).getUnitVec3i()).scale(0.5));
                    }

                    level.explode(
                            null,
                            explosionPos.x, explosionPos.y, explosionPos.z,
                            1.0f,
                            false,
                            Level.ExplosionInteraction.BLOCK
                    );
                }
            }
        }
    }

    private void move(Level level, BlockPos pos, BlockState state) {
        BlockPos newPos = pos.relative(moveDirection);
        Direction oldFacing = state.getValue(DrumBlock.FACING);
        Direction newFacing = rollFacing(state.getValue(DrumBlock.FACING), moveDirection);

        BlockState newState = state
                .setValue(ROLLING, false)
                .setValue(DrumBlock.FACING, newFacing);
        level.setBlock(newPos, newState, Block.UPDATE_ALL);

        if (oldFacing.getAxis() != newFacing.getAxis()) {
            level.playSound(
                    null,
                    pos,
                    BitterSounds.METAL_DRUM_FLIP.value(),
                    SoundSource.BLOCKS,
                    0.5f,
                    1.0f + (1.0f - getMixture().getVolume() / (float) SubstanceFluid.FULL_BLOCK_VOLUME) * 0.2f
            );
        }

        if (level.getBlockEntity(newPos) instanceof DrumBlockEntity movedBarrel) {
            movedBarrel.setMixture(getMixture());
        }

        level.removeBlock(pos, false);
    }

    private static Direction rollFacing(Direction facing, Direction dir) {
        if (facing == dir) return Direction.DOWN;
        if (facing == dir.getOpposite()) return Direction.UP;
        if (facing == Direction.UP) return dir;
        if (facing == Direction.DOWN) return dir.getOpposite();
        return facing;
    }

    public void tickAnimation() {
        if (!animationFinished() && moveDirection != null) {
            rot0 = rot;
            float speed = ROT_SPEED * (1.0f - mixture.getVolume() / (float) SubstanceFluid.FULL_BLOCK_VOLUME);
            rot += Math.max(ROT_SPEED_MIN, ROT_SPEED_MIN + speed);
        }
    }

    public boolean animationFinished() {
        return rot >= 1.0f;
    }

    public float getRotationProgress(float partialTick) {
        if (animationFinished()) return 1.0f;
        return Mth.lerp(partialTick, rot0, rot);
    }

    public void setMoveDirection(Direction moveDirection) {
        this.moveDirection = moveDirection;
        rot = 0.0f;
        rot0 = 0.0f;
    }

    public Direction getMoveDirection() {
        return moveDirection;
    }

    public SubstanceMixture getMixture() {
        return mixture;
    }

    public void setMixture(SubstanceMixture mixture) {
        this.mixture = mixture;
        mixture.setChangedCallback(mixtureChangedCallback);
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("mixture", SubstanceMixture.CODEC, mixture);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("mixture", SubstanceMixture.CODEC).ifPresent(loaded -> {
            int oldColor = this.mixture.getColor();
            loaded.setChangedCallback(mixtureChangedCallback);
            this.mixture = loaded;
            int newColor = loaded.getColor();

            if (level != null && level.isClientSide()) {
                if (oldColor != newColor) {
                    level.sendBlockUpdated(worldPosition, Blocks.AIR.defaultBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
                }
            }
        });
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            // Ensure overflow is handled when mixture volume increases
            if (getBlockState().getValue(OPEN) && !level.isClientSide()) {
                if (getVolume() > SubstanceFluid.FULL_BLOCK_VOLUME) {
                    level.scheduleTick(worldPosition, getBlockState().getBlock(), TICK_DELAY);
                }
            }

            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), UPDATE_CLIENTS);
        }
    }
}
