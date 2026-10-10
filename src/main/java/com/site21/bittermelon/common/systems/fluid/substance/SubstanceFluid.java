package com.site21.bittermelon.common.systems.fluid.substance;

import com.google.common.collect.Maps;
import com.site21.bittermelon.common.content.blocks.properties.BitterStateProperties;
import com.site21.bittermelon.common.systems.blockdamage.BlockDamageUtil;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.init.neoforge.BitterFluidTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.STAINS;
import static com.site21.bittermelon.init.neoforge.BitterBlocks.SUBSTANCE_FLUID;

public class SubstanceFluid extends Fluid {
    public static final IntegerProperty LEVEL = BitterStateProperties.LEVEL;
    public static final int FULL_BLOCK_VOLUME = 1000;
    private static final int SPREAD_THRESHOLD = 100;
    private static final int PRESSURE_THRESHOLD = 100;
    private static final float DOWNWARDS_SPREAD_RATIO = 0.85f;

    private final Map<FluidState, VoxelShape> shapes = Maps.newIdentityHashMap();
    private final Supplier<? extends BucketItem> bucket;

    public SubstanceFluid(Supplier<? extends BucketItem> bucket) {
        this.bucket = bucket;
        registerDefaultState(getStateDefinition().any().setValue(LEVEL, 1));
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected void tick(ServerLevel level, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (!(level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE)) return;

        Profiler.get().push("SubstanceFluidTick");

        List<SubstanceFluidBlockEntity> touched = new ArrayList<>(6);
        touched.add(fluidBE);
        try {
            if (fluidBE.getVolume() <= 0) {
                fluidBE.markDirty(level);
                return;
            }

            fluidBE.getMixture().tickReactions(level, pos);

            if (spreadDownwards(level, pos, fluidBE, touched)) return;

            int volume = fluidBE.getVolume();
            if (volume > SPREAD_THRESHOLD && !spreadHorizontally(level, pos, fluidBE, touched) && volume > FULL_BLOCK_VOLUME) {
                spreadUpwards(level, pos, fluidBE, touched);
            }
        } finally {
            for (SubstanceFluidBlockEntity be : touched) {
                be.update(level);
            }

            Profiler.get().pop();
        }
    }

    @Override
    protected void randomTick(ServerLevel level, BlockPos pos, FluidState state, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE) {
            exertPressure(fluidBE, level);
        }
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    private boolean spreadDownwards(Level level, BlockPos pos, SubstanceFluidBlockEntity fluidBE, List<SubstanceFluidBlockEntity> touched) {
        List<SubstanceStack> substances = fluidBE.getMixture().getSubstances();
        if (substances.isEmpty() || !canSpreadTo(level, pos.below())) return false;

        SubstanceFluidBlockEntity target = spreadTo(level, pos.below(), substances);
        if (target != null) touched.add(target);

        fluidBE.getMixture().setSubstances(List.of());
        return true;
    }

    private boolean spreadHorizontally(Level level, BlockPos pos, SubstanceFluidBlockEntity fluidBE, List<SubstanceFluidBlockEntity> touched) {
        Profiler.get().push("spreadHorizontally");

        List<BlockPos> spreadPositions = new ArrayList<>(4);
        getDownwardSpreadPositions(level, pos, fluidBE, spreadPositions);
        boolean downwardSpread = !spreadPositions.isEmpty();

        if (!downwardSpread) {
            getSpreadPositions(level, pos, fluidBE, spreadPositions);
            if (spreadPositions.isEmpty()) {
                Profiler.get().pop();
                return false;
            }
        }

        int spreadCount = spreadPositions.size();
        List<SubstanceStack> substancesToSpread = getSubstancesForSpread(fluidBE, spreadCount, downwardSpread);

        if (substancesToSpread.isEmpty()) {
            Profiler.get().pop();
            return false;
        }

        for (BlockPos spreadPos : spreadPositions) {
            SubstanceFluidBlockEntity target = spreadTo(level, spreadPos, substancesToSpread);
            if (target != null) touched.add(target);
        }

        fluidBE.getMixture().removeSubstances(substancesToSpread, spreadCount);
        equalizeSubstances(level, pos, fluidBE, touched);

        Profiler.get().pop();
        return true;
    }

    private List<SubstanceStack> getSubstancesForSpread(SubstanceFluidBlockEntity fluidBE, int spreadCount, boolean downwardSpread) {
        int sourceVolume = fluidBE.getVolume();
        int spreadVolume = downwardSpread ?
                Math.round(sourceVolume * DOWNWARDS_SPREAD_RATIO) / spreadCount :
                sourceVolume / (spreadCount + 1);

        return spreadSubstancesByVolume(
                fluidBE.getMixture().getSubstances(),
                spreadVolume,
                sourceVolume
        );
    }

    private void getDownwardSpreadPositions(Level level, BlockPos pos, SubstanceFluidBlockEntity fluidBE,
                                            List<BlockPos> positions) {
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            neighborPos.setWithOffset(pos, direction);
            if (level.getBlockState(neighborPos).canBeReplaced() && canSpreadTo(level, neighborPos.below())) {
                positions.add(neighborPos.immutable());
            }
        }
    }

    private void getSpreadPositions(Level level, BlockPos pos, SubstanceFluidBlockEntity fluidBE, List<BlockPos> positions) {
        List<BlockPos> backupPositions = new ArrayList<>(4);
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            neighborPos.setWithOffset(pos, direction);
            if (level.getBlockState(neighborPos).canBeReplaced()) {
                positions.add(neighborPos.immutable());
            } else if (canSpreadHorizontally(level, neighborPos, fluidBE)) {
                backupPositions.add(neighborPos.immutable());
            }
        }

        if (positions.isEmpty()) {
            positions.addAll(backupPositions);
        }
    }

    private void spreadUpwards(Level level, BlockPos pos, SubstanceFluidBlockEntity fluidBE, List<SubstanceFluidBlockEntity> touched) {
        int spreadVolume = fluidBE.getVolume() - FULL_BLOCK_VOLUME;
        if (spreadVolume <= 0) return;

        BlockPos abovePos = pos.above();

        if (level.getFluidState(abovePos).is(this) || level.getBlockState(abovePos).canBeReplaced()) {
            List<SubstanceStack> substancesToSpread = spreadSubstancesByVolume(fluidBE.getMixture().getSubstances(), spreadVolume,
                    fluidBE.getVolume());
            if (substancesToSpread.isEmpty()) return;

            SubstanceFluidBlockEntity target = spreadTo(level, abovePos, substancesToSpread);
            if (target != null) touched.add(target);

            for (SubstanceStack spreadStack : substancesToSpread) {
                fluidBE.getMixture().removeSubstance(spreadStack, spreadStack.getAmount());
            }
        }
    }

    private List<SubstanceStack> spreadSubstancesByVolume(List<SubstanceStack> substances, int transferVolume, int totalVolume) {
        List<SubstanceStack> spreadStacks = new ArrayList<>();
        float ratio = (float) transferVolume / totalVolume;

        for (SubstanceStack stack : substances) {
            int amount = (int) (stack.getAmount() * ratio);

            if (amount > 0) {
                SubstanceStack spreadStack = stack.copy();
                spreadStack.setAmount(amount);
                spreadStacks.add(spreadStack);
            }
        }

        return spreadStacks;
    }

    public boolean canSpreadTo(Level level, BlockPos pos) {
        // Check if the neighbor fluid state is empty or same type
        FluidState neighborFluidState = level.getFluidState(pos);
        if (!neighborFluidState.isEmpty() && !neighborFluidState.is(this)) return false;

        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity neighborBE) {
            return neighborBE.getVolume() < FULL_BLOCK_VOLUME;
        }

        // Check if the block can be replaced by this fluid
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getBlock() instanceof LiquidBlockContainer liquidBlockContainer) {
            return liquidBlockContainer.canPlaceLiquid(null, level, pos, blockState, this);
        }

        return blockState.canBeReplaced();
    }

    private boolean canSpreadHorizontally(Level level, BlockPos pos, SubstanceFluidBlockEntity fluidBE) {
        FluidState neighborFluidState = level.getFluidState(pos);
        if (!neighborFluidState.isEmpty() && !neighborFluidState.is(this)) return false;

        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity neighborBE) {
//            return fluidBE.getVolume() - neighborBE.getVolume() >= 50;
            return fluidBE.getVolume() > neighborBE.getVolume();
        }

        // Check if the block can be replaced by this fluid
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getBlock() instanceof LiquidBlockContainer liquidBlockContainer) {
            return liquidBlockContainer.canPlaceLiquid(null, level, pos, blockState, this);
        }

        return blockState.canBeReplaced();
    }

    private void equalizeSubstances(Level level, BlockPos pos, SubstanceFluidBlockEntity fluidBE, List<SubstanceFluidBlockEntity> touched) {
        Profiler.get().push("equalizeSubstances");

        List<SubstanceFluidBlockEntity> fluids = new ArrayList<>(5);
        fluids.add(fluidBE);
        int sourceVolume = fluidBE.getVolume();
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            neighborPos.setWithOffset(pos, direction);
            if (level.getBlockEntity(neighborPos) instanceof SubstanceFluidBlockEntity neighborBE) {
                if (Math.abs(neighborBE.getVolume() - sourceVolume) > 50) {
                    fluids.add(neighborBE);
                }
            }
        }

        if (fluids.size() < 2) {
            Profiler.get().pop();
            return;
        }

        touched.addAll(fluids);
        int fluidCount = fluids.size();

        Map<Substance, Integer> totalsByType = new HashMap<>();
        for (SubstanceFluidBlockEntity be : fluids) {
            for (SubstanceStack stack : be.getMixture().getSubstances()) {
                totalsByType.merge(stack.getSubstance(), stack.getAmount(), Integer::sum);
            }
        }

        for (int i = 0; i < fluidCount; i++) {
            SubstanceFluidBlockEntity be = fluids.get(i);
            List<SubstanceStack> newStacks = new ArrayList<>(totalsByType.size());

            for (var entry : totalsByType.entrySet()) {
                int totalAmount = entry.getValue();
                int amount = totalAmount / fluidCount;

                // Prevents truncation but will infinitely try to equalize
//                int remainder = totalAmount % fluidCount;
//                int amount = baseAmount + (i < remainder ? 1 : 0);

                if (amount > 0) {
                    SubstanceStack newStack = entry.getKey().toStack();
                    if (newStack != null) {
                        newStack.setAmount(amount);
                        newStacks.add(newStack);
                    }
                }
            }

            be.getMixture().setSubstances(newStacks);
        }


        //            if (level instanceof ServerLevel serverLevel) {
//                serverLevel.sendParticles(ParticleTypes.BUBBLE, be.getBlockPos().getX() + 0.5, be.getBlockPos().getY() + 1,
//                        be.getBlockPos().getZ() + 0.5, 1, 0, 0, 0, 0.1);
//            }

        Profiler.get().pop();
    }

    protected @Nullable SubstanceFluidBlockEntity spreadTo(Level level, BlockPos pos, List<SubstanceStack> substances) {
        if (!level.getFluidState(pos).is(this)) {
            level.setBlock(pos, createLegacyBlock(defaultFluidState().setValue(LEVEL, 1)), Block.UPDATE_NONE);
            playFlowSound(level, pos, level.getRandom());
        }
        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity be) {
            be.getMixture().transferSubstances(substances);
            return be;
        }
        return null;
    }

    private void exertPressure(SubstanceFluidBlockEntity fluidBE, LevelAccessor level) {
        int pressure = fluidBE.getMixture().getPressure();
        if (pressure >= PRESSURE_THRESHOLD) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = fluidBE.getBlockPos().relative(direction);
                if (level.getBlockState(neighborPos).canBeReplaced()) continue;

                BlockDamageUtil.addDamage(level, neighborPos, pressure / 100);
            }
        }
    }

    protected void playFlowSound(Level level, BlockPos pos, RandomSource random) {
        level.playSound(
                null,
                pos,
                SoundEvents.WATER_AMBIENT,
                SoundSource.AMBIENT,
                random.nextFloat() * 0.25F + 0.75F,
                random.nextFloat() + 0.5F
        );
    }

    @Override
    protected void entityInside(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier) {
        super.entityInside(level, pos, entity, effectApplier);

        if (level.isClientSide()) return;
        if (level.getGameTime() % 20 != 0) return;
        if (entity.getDeltaMovement().lengthSqr() == 0) return;
        if (!(level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE)) return;

        int fluidVolume = fluidBE.getVolume();

        SubstanceMixture stains = entity.getData(STAINS);
        int stainHeadroom = 100 - stains.getVolume();
        if (stainHeadroom <= 0) return;

        float contactFactor = entity.isSprinting() ? 0.2f : 0.1f;
        int absorb = Math.min(stainHeadroom, (int) (fluidVolume * contactFactor));
        if (absorb <= 0) return;

        List<SubstanceStack> absorbed = spreadSubstancesByVolume(fluidBE.getMixture().getSubstances(), absorb, fluidVolume);
        if (!absorbed.isEmpty()) {
            fluidBE.getMixture().removeSubstances(absorbed);
            stains.transferSubstances(absorbed);
        }
    }

    @Override
    public Item getBucket() {
        return bucket.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos,
                                        Fluid fluid, Direction direction) {
        return true;
    }

    @Override
    protected Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState state) {
        double flowX = 0.0;
        double flowZ = 0.0;
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

//        for (Direction direction : Direction.Plane.HORIZONTAL) {
//            Direction opposite = direction.getOpposite();
//
//            // adjacent
//            neighborPos.setWithOffset(pos, direction);
//            FluidState adjacentFluid = level.getFluidState(neighborPos);
//            BlockState adjacentBlock = level.getBlockState(neighborPos);
//            boolean occupied = adjacentFluid.getAmount() > 1 && !adjacentBlock.canBeReplaced();
//
//            // opposite
//            neighborPos.setWithOffset(pos, opposite);
//            FluidState oppositeFluid = level.getFluidState(neighborPos);
//            boolean oppositeUnoccupied = oppositeFluid.is(this)
//                    ? oppositeFluid.getAmount() < 2
//                    : level.getBlockState(neighborPos).canBeReplaced();
//
//            if (occupied && oppositeUnoccupied) {
//                flowX += opposite.getStepX();
//                flowZ += opposite.getStepZ();
//            }
//        }

        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE) {
            if (fluidBE.getVolume() > SPREAD_THRESHOLD) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    neighborPos.setWithOffset(pos, direction);
                    if (level.getBlockState(neighborPos).canBeReplaced()) {
                        flowX += direction.getStepX();
                        flowZ += direction.getStepZ();
                    }
                }
            }
        }

        if (flowX * flowX + flowZ * flowZ < 0.01) {
            return Vec3.ZERO;
        }

        Vec3 flowDirection = new Vec3(flowX, 0.0, flowZ);

        return flowDirection.normalize();
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getHeight(FluidState state, BlockGetter level, BlockPos pos) {
        return state.getType().isSame(level.getFluidState(pos.above()).getType()) ? 1f : getOwnHeight(state);
    }

    @Override
    public float getOwnHeight(FluidState state) {
        return state.getAmount() / 20.0f;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return SUBSTANCE_FLUID.get().defaultBlockState().setValue(BitterStateProperties.LEVEL, state.getValue(LEVEL));
    }

    @Override
    public boolean isSource(FluidState state) {
        return true;
    }

    @Override
    public int getAmount(FluidState state) {
        return state.getValue(LEVEL);
    }

    public int getBlockLevel(int volume) {
        return Math.clamp(volume / 50, 1, 20);
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos) {
        return shapes.computeIfAbsent(state, (fluidState) -> Shapes.box(0.0, 0.0, 0.0, 1.0,
                fluidState.getHeight(level, pos), 1.0));
    }

    @Override
    public FluidType getFluidType() {
        return BitterFluidTypes.SUBSTANCE_FLUID_TYPE.get();
    }
}
