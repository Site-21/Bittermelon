package com.site21.bittermelon.common.systems.fluid.substance;

import com.google.common.collect.Maps;
import com.site21.bittermelon.common.content.blocks.properties.BitterStateProperties;
import com.site21.bittermelon.common.systems.blockdamage.BlockDamageUtil;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.common.systems.substance.SubstanceState;
import com.site21.bittermelon.init.neoforge.BitterBlockEntities;
import com.site21.bittermelon.init.neoforge.BitterFluidTypes;
import com.site21.bittermelon.init.neoforge.BitterFluids;
import com.site21.bittermelon.util.SubstanceUtil;
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
    public static final int SPREAD_THRESHOLD = 100;
    public static final int PRESSURE_THRESHOLD = 100;
    public static final float DOWNWARDS_SPREAD_RATIO = 0.85f;
    public static final float FREEZE_FRACTION = 0.6f;
    public static final float MELT_FRACTION = 0.4f;

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

            fluidBE.getMixture().exchangeHeat(298.15f, 1000, level.getGameTime());
            fluidBE.getMixture().tickReactions(level, pos);
            if (tryFreeze(level, pos, fluidBE)) return;

            NeighborInfo[] neighbors = getNeighbors(level, pos);

            if (spreadDownwards(level, fluidBE, neighbors[Direction.DOWN.ordinal()], touched)) return;

            int volume = fluidBE.getVolume();
            if (volume <= SPREAD_THRESHOLD) return;

            NeighborInfo aboveInfo = neighbors[Direction.UP.ordinal()];
            boolean canRise = volume >= FULL_BLOCK_VOLUME && canSpreadUpwards(aboveInfo);
            if (!spreadHorizontally(level, fluidBE, neighbors, canRise, touched) && volume > FULL_BLOCK_VOLUME) {
                spreadUpwards(level, fluidBE, aboveInfo, canRise, touched);
            }

            newEqualizeSubstances(level, pos, fluidBE, touched);
        } finally {
            for (SubstanceFluidBlockEntity be : touched) {
                be.update(level);
            }

            Profiler.get().pop();
        }
    }

    private NeighborInfo[] getNeighbors(Level level, BlockPos pos) {
        NeighborInfo[] neighbors = new NeighborInfo[6];
        for (Direction direction : Direction.values()) {
            neighbors[direction.ordinal()] = new NeighborInfo(level, pos.relative(direction));
        }
        return neighbors;
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

    private boolean spreadDownwards(Level level, SubstanceFluidBlockEntity fluidBE, NeighborInfo belowInfo, List<SubstanceFluidBlockEntity> touched) {
        List<SubstanceStack> substances = fluidBE.getMixture().getSubstances();
        if (substances.isEmpty()) return false;
        int volume = fluidBE.getVolume();

        if (!canSpreadTo(level, belowInfo)) return false;

        int spreadVolume = belowInfo.be() != null
                ? Math.min(volume, FULL_BLOCK_VOLUME - belowInfo.volume())
                : volume;

        List<SubstanceStack> spreadSubstances = SubstanceUtil.spreadSubstancesByVolume(
                substances,
                volume,
                spreadVolume
        );

        if (spreadSubstances.isEmpty()) return false;

        SubstanceFluidBlockEntity target = spreadTo(level, belowInfo, spreadSubstances, fluidBE.getMixture().getTemperature());
        if (target != null) touched.add(target);

        fluidBE.getMixture().removeSubstances(spreadSubstances);
        return volume == spreadVolume;
    }

    private boolean spreadHorizontally(Level level, SubstanceFluidBlockEntity fluidBE, NeighborInfo[] neighbors, boolean canRise, List<SubstanceFluidBlockEntity> touched) {
        Profiler.get().push("spreadHorizontally");

        boolean[] spreadable = getDownwardSpreadPositions(level, neighbors);
        boolean downwardSpread = spreadable != null;

        if (!downwardSpread) {
            spreadable = getSpreadPositions(level, fluidBE, neighbors, canRise);
            if (spreadable == null) {
                Profiler.get().pop();
                return false;
            }
        }

        int spreadCount = 0;
        for (boolean canSpread : spreadable) {
            if (canSpread) spreadCount++;
        }

        int volume = fluidBE.getVolume();
        SubstanceMixture mixture = fluidBE.getMixture();
        float temperature = mixture.getTemperature();
        List<SubstanceStack>[] spreadSubstances = new List[6];
//        boolean[] foam = new boolean[spreadCount];

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int i = direction.ordinal();
            if (!spreadable[i]) continue;

            NeighborInfo neighborInfo = neighbors[i];
            int spreadVolume = downwardSpread
                    ? Math.round(volume * DOWNWARDS_SPREAD_RATIO) / spreadCount
                    : (volume - neighborInfo.volume()) / (spreadCount + 1);

            List<SubstanceStack> substances = SubstanceUtil.spreadSubstancesByVolume(
                    mixture.getSubstances(),
                    volume,
                    spreadVolume
            );

            spreadSubstances[i] = substances;
//            foam[j] = spreadVolume >= 100;
        }

        boolean anySpread = false;
        RandomSource random = level.getRandom();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int i = direction.ordinal();
            if (!spreadable[i] || spreadSubstances[i].isEmpty()) continue;

            NeighborInfo neighborInfo = neighbors[i];
            SubstanceFluidBlockEntity target = spreadTo(level, neighborInfo, spreadSubstances[i], temperature);
            mixture.removeSubstancesNoUpdate(spreadSubstances[i], 1);
            if (target != null) touched.add(target);
            anySpread = true;

            // TODO: replace with proper foam particle
//            if (foam[j] && level instanceof ServerLevel serverLevel) {
//                serverLevel.sendParticles(
//                        ParticleTypes.DUST_PLUME,
//                        spreadPos.getX() + 0.5 * random.nextFloat(),
//                        spreadPos.getY() + 0.1 * random.nextFloat(),
//                        spreadPos.getZ() + 0.5 * random.nextFloat(),
//                        5,
//                        0,
//                        0,
//                        0,
//                        0.05
//                );
//            }
        }

        if (!anySpread) {
            Profiler.get().pop();
            return false;
        }

        mixture.refresh();
//        equalizeSubstances(level, pos, fluidBE, canRise, touched);

        Profiler.get().pop();
        return true;
    }

    private boolean[] getDownwardSpreadPositions(Level level, NeighborInfo[] neighbors) {
        boolean[] spreadable = new boolean[6];
        boolean anySpreadable = false;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            NeighborInfo neighborInfo = neighbors[direction.ordinal()];
            if (!isEmpty(neighborInfo.state())) {
                spreadable[direction.ordinal()] = false;
                continue;
            }

            if (canSpreadTo(level, neighborInfo.pos().below())) {
                spreadable[direction.ordinal()] = true;
                anySpreadable = true;
            } else {
                spreadable[direction.ordinal()] = false;
            }
        }

        return anySpreadable ? spreadable : null;
    }

    private boolean[] getSpreadPositions(Level level, SubstanceFluidBlockEntity fluidBE, NeighborInfo[] neighbors, boolean canRise) {
        boolean[] spreadable = new boolean[6];
        boolean[] backup = new boolean[6];

        boolean needsBackup = true;
        boolean anySpreadable = false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            NeighborInfo neighborInfo = neighbors[direction.ordinal()];

            if (isEmpty(neighborInfo.state())) {
                spreadable[direction.ordinal()] = true;
                needsBackup = false;
                anySpreadable = true;
                continue;
            } else if (canSpreadHorizontally(level, neighborInfo, fluidBE, canRise)) {
                backup[direction.ordinal()] = true;
                anySpreadable = true;
            } else {
                backup[direction.ordinal()] = false;
            }

            spreadable[direction.ordinal()] = false;
        }

        return anySpreadable
                ? needsBackup
                  ? backup
                  : spreadable
                : null;
    }

    private void spreadUpwards(Level level, SubstanceFluidBlockEntity fluidBE, NeighborInfo aboveInfo, boolean canRise, List<SubstanceFluidBlockEntity> touched) {
        int volume = fluidBE.getVolume();
        int spreadVolume = volume - FULL_BLOCK_VOLUME;
        if (spreadVolume <= 0) return;

        if (canRise) {
            List<SubstanceStack> substancesToSpread = SubstanceUtil.spreadSubstancesByVolume(
                    fluidBE.getMixture().getSubstances(),
                    volume,
                    spreadVolume
            );

            if (substancesToSpread.isEmpty()) return;

            SubstanceFluidBlockEntity target = spreadTo(level, aboveInfo, substancesToSpread, fluidBE.getMixture().getTemperature());
            if (target != null) touched.add(target);

            for (SubstanceStack spreadStack : substancesToSpread) {
                fluidBE.getMixture().removeSubstance(spreadStack, spreadStack.getAmount());
            }
        }
    }

    private boolean canSpreadUpwards(NeighborInfo aboveInfo) {
        return aboveInfo.state().getFluidState().is(this) || aboveInfo.state().canBeReplaced();
    }

    private boolean shouldRiseInstead(boolean canRise, int sourceVolume, int neighborVolume) {
        return canRise && sourceVolume >= FULL_BLOCK_VOLUME && neighborVolume >= FULL_BLOCK_VOLUME;
    }

    private boolean canSpreadTo(Level level, BlockPos pos) {
        return canSpreadTo(level, new NeighborInfo(level, pos));
    }

    private boolean canSpreadTo(Level level, NeighborInfo info) {
        // Check if the neighbor fluid state is empty or same type
        FluidState neighborFluidState = info.state().getFluidState();
        if (!neighborFluidState.isEmpty() && !neighborFluidState.is(this)) return false;

        if (info.be() != null && info.volume() < FULL_BLOCK_VOLUME) return true;

        // Check if the block can be replaced by this fluid
        if (info.state().getBlock() instanceof LiquidBlockContainer liquidBlockContainer) {
            return liquidBlockContainer.canPlaceLiquid(null, level, info.pos(), info.state(), this);
        }

        return isEmpty(info.state());
    }

    private boolean canSpreadHorizontally(Level level, NeighborInfo neighborInfo, SubstanceFluidBlockEntity fluidBE, boolean canRise) {
        FluidState neighborFluidState = neighborInfo.state().getFluidState();
        if (!neighborFluidState.isEmpty() && !neighborFluidState.is(this)) return false;

        if (neighborFluidState.is(this)) {
            if (shouldRiseInstead(canRise, fluidBE.getVolume(), neighborInfo.volume())) return false;
//            return fluidBE.getVolume() > neighborBE.getVolume();
            return fluidBE.getVolume() - neighborInfo.volume() >= 10;
        }

        // Check if the block can be replaced by this fluid
        if (neighborInfo.state().getBlock() instanceof LiquidBlockContainer liquidBlockContainer) {
            return liquidBlockContainer.canPlaceLiquid(null, level, neighborInfo.pos(), neighborInfo.state(), this);
        }

        return isEmpty(neighborInfo.state());
    }

    private void newEqualizeSubstances(Level level, BlockPos pos, SubstanceFluidBlockEntity fluidBE, List<SubstanceFluidBlockEntity> touched) {
        Profiler.get().push("equalizeSubstances");
        List<SubstanceFluidBlockEntity> fluids = new ArrayList<>(4);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            if (level.getBlockEntity(neighborPos) instanceof SubstanceFluidBlockEntity neighborBE) {
                fluids.add(neighborBE);
            }
        }

        if (fluids.isEmpty()) {
            Profiler.get().pop();
            return;
        }

        SubstanceMixture mainMixture = fluidBE.getMixture();
        int mainVolume = mainMixture.getVolume();
        float mainTemperature = mainMixture.getTemperature();

        int count = fluids.size();
        List<SubstanceStack>[] in = new List[count];
        List<SubstanceStack>[] out = new List[count];

        for (int i = 0; i < count; i++) {
            SubstanceMixture mixture = fluids.get(i).getMixture();
            int volume = mixture.getVolume();
            int slice = Math.round(Math.min(mainVolume, volume) * 0.4f / count);
            in[i] = SubstanceUtil.spreadSubstancesByVolume(mixture.getSubstances(), volume, slice);
            out[i] = SubstanceUtil.spreadSubstancesByVolume(mainMixture.getSubstances(), mainVolume, slice);
        }

        float keptCapacity = mainMixture.getHeatCapacity();
        float gainedCapacity = 0;
        float gainedHeat = 0;
        boolean changed = false;
        for (int i = 0; i < count; i++) {
            if (in[i].isEmpty() || out[i].isEmpty()) continue;

            SubstanceFluidBlockEntity be = fluids.get(i);
            SubstanceMixture mixture = be.getMixture();

            float neighborTemperature = mixture.getTemperature();
            boolean sameTemperature = Math.abs(mainTemperature - neighborTemperature) < 0.01f;
            if (sameTemperature && SubstanceUtil.sameSubstances(in[i], out[i], 1)) continue;

            float inCapacity = SubstanceUtil.getHeatCapacity(in[i]);
            float outCapacity = SubstanceUtil.getHeatCapacity(out[i]);
            float neighborKept = mixture.getHeatCapacity() - inCapacity;
            mixture.updateSubstancesNoUpdate(out[i]);
            mixture.removeSubstancesNoUpdate(in[i], 1);
            mainMixture.updateSubstancesNoUpdate(in[i]);
            mainMixture.removeSubstancesNoUpdate(out[i], 1);

            float neighborTotal = neighborKept + outCapacity;
            if (neighborTotal > 0) {
                mixture.setTemperatureNoUpdate((neighborKept * neighborTemperature + outCapacity * mainTemperature) / neighborTotal);
            }

            keptCapacity -= outCapacity;
            gainedCapacity += inCapacity;
            gainedHeat += inCapacity * neighborTemperature;
            mixture.refresh();
            touched.add(be);
            changed = true;
        }

        if (!changed) {
            Profiler.get().pop();
            return;
        }

        float mainTotal = keptCapacity + gainedCapacity;
        if (mainTotal > 0) {
            mainMixture.setTemperatureNoUpdate((keptCapacity * mainTemperature + gainedHeat) / mainTotal);
        }
        mainMixture.refresh();

        Profiler.get().pop();
    }

    private void equalizeSubstances(Level level, BlockPos pos, SubstanceFluidBlockEntity fluidBE, boolean canRise, List<SubstanceFluidBlockEntity> touched) {
        Profiler.get().push("equalizeSubstances");

        List<SubstanceFluidBlockEntity> fluids = new ArrayList<>(5);
        fluids.add(fluidBE);
        int sourceVolume = fluidBE.getVolume();
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            neighborPos.setWithOffset(pos, direction);
            if (level.getBlockEntity(neighborPos) instanceof SubstanceFluidBlockEntity neighborBE) {
                int neighborVolume = neighborBE.getVolume();
                if (!shouldRiseInstead(canRise, sourceVolume, neighborVolume) && Math.abs(neighborVolume - sourceVolume) > 50) {
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
        float totalHeat = 0;
        float totalHeatCapacity = 0;
        for (SubstanceFluidBlockEntity be : fluids) {
            SubstanceMixture mixture = be.getMixture();
            for (SubstanceStack stack : mixture.getSubstances()) {
                totalsByType.merge(stack.getSubstance(), stack.getAmount(), Integer::sum);
            }
            float heatCapacity = mixture.getHeatCapacity();
            totalHeat += mixture.getTemperature() * heatCapacity;
            totalHeatCapacity += heatCapacity;
        }
        float equalizedTemperature = totalHeatCapacity > 0 ? totalHeat / totalHeatCapacity : fluidBE.getMixture().getTemperature();

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

            be.getMixture().setTemperatureNoUpdate(equalizedTemperature);
            be.getMixture().setSubstances(newStacks);
        }


//                    if (level instanceof ServerLevel serverLevel) {
//                serverLevel.sendParticles(ParticleTypes.BUBBLE, fluidBE.getBlockPos().getX() + 0.5, fluidBE.getBlockPos().getY() + 1,
//                        fluidBE.getBlockPos().getZ() + 0.5, 1, 0, 0, 0, 0.1);
//            }

        Profiler.get().pop();
    }

    protected void spreadTo(Level level, BlockPos pos, List<SubstanceStack> substances, float temperature) {
        NeighborInfo info = new NeighborInfo(level, pos);
        spreadTo(level, info, substances, temperature);
    }

    private @Nullable SubstanceFluidBlockEntity spreadTo(Level level, NeighborInfo neighborInfo, List<SubstanceStack> substances, float temperature) {
        if (!neighborInfo.state().getFluidState().is(this)) {
            level.setBlock(neighborInfo.pos(), createLegacyBlock(defaultFluidState().setValue(LEVEL, 1)), Block.UPDATE_NONE);
            playFlowSound(level, neighborInfo.pos(), level.getRandom());
        }

        SubstanceFluidBlockEntity be = neighborInfo.be() == null
                ? level.getBlockEntity(neighborInfo.pos(), BitterBlockEntities.SUBSTANCE_FLUID_BLOCK_ENTITY.get()).orElse(null)
                : neighborInfo.be();

        if (be == null) return null;

        SubstanceMixture mixture = be.getMixture();

        float newTemperature = SubstanceUtil.temperatureForSpread(
                substances,
                temperature,
                mixture.getSubstances(),
                mixture.getTemperature()
        );

        mixture.setTemperatureNoUpdate(newTemperature);
        mixture.transferSubstances(substances);

        return be;
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

        List<SubstanceStack> absorbed = SubstanceUtil.spreadSubstancesByVolume(fluidBE.getMixture().getSubstances(), fluidVolume, absorb);
        if (!absorbed.isEmpty()) {
            fluidBE.getMixture().removeSubstances(absorbed);
            stains.transferSubstances(absorbed);
        }
    }

    @Override
    protected Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState state) {
        return new Vec3(0, 0, 0);
    }

    private boolean tryFreeze(ServerLevel level, BlockPos pos, SubstanceFluidBlockEntity fluidBE) {
        SubstanceMixture mixture = fluidBE.getMixture();
        int volume = fluidBE.getVolume();

        // TODO: Alternatively, we could first verify if there is a solid substance in the mixture, perhaps keeping a list of solid substances
        if (mixture.getSolidFraction() < 1.0) return false;

        List<SubstanceStack> solidSubstances = new ArrayList<>();
        List<SubstanceStack> nonSolidSubstances = new ArrayList<>();
        float temperature = mixture.getTemperature();

        for (SubstanceStack stack : mixture.getSubstances()) {
            Substance substance = stack.getSubstance();
            if (substance.getState() == SubstanceState.SOLID) {
                solidSubstances.add(stack);
            } else {
                nonSolidSubstances.add(stack);
            }
        }

        int blockLevel = getBlockLevel(volume);

        if (canSpreadTo(level, pos.above()) && blockLevel == 20) {
            SubstanceUtil.spill(level, pos.above(), nonSolidSubstances, temperature);
        } else {
            solidSubstances.addAll(nonSolidSubstances);
//            List<BlockPos> spreadPositions = new ArrayList<>(4);
//            for (Direction direction : Direction.Plane.HORIZONTAL) {
//                BlockPos neighborPos = pos.relative(direction);
//                if (canSpreadTo(level, neighborPos)) {
//                    spreadPositions.add(neighborPos);
//                }
//            }
//
//            if (!spreadPositions.isEmpty()) {
//                int spreadCount = spreadPositions.size();
//                List<SubstanceStack> spreadStacks = SubstanceUtil.splitSubstances(nonSolidSubstances, spreadCount, 0);
//                for (BlockPos spreadPos : spreadPositions) {
//                    SubstanceUtil.spill(level, spreadPos, spreadStacks);
//                }
//            } else {
//                solidSubstances.addAll(nonSolidSubstances);
//            }
        }

        fluidBE.beginConversion();
        // TODO: Perhaps better way to choose different block based on substance
        solidSubstances.sort((a, b) -> Integer.compare(b.getVolume(), a.getVolume()));
        BlockState solidState = solidSubstances.getFirst().getSubstance().getSolidBlock().defaultBlockState();
        level.setBlock(pos, solidState.setValue(SolidSubstanceBlock.LEVEL, blockLevel), Block.UPDATE_ALL);

        if (level.getBlockEntity(pos) instanceof SolidSubstanceBlockEntity solid) {
            solid.getMixture().setSubstances(solidSubstances);
            solid.getMixture().setTemperature(temperature);
            solid.setChanged();
        }

        return true;
    }

    private boolean isEmpty(BlockState blockState) {
        return !blockState.getFluidState().is(this) && blockState.canBeReplaced();
    }

    @Override
    public Item getBucket() {
        return bucket.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return true;
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

    private record NeighborInfo(BlockPos pos, BlockState state, @Nullable SubstanceFluidBlockEntity be, int volume) {
        public NeighborInfo(Level level, BlockPos pos) {
            BlockState state = level.getBlockState(pos);
            SubstanceFluidBlockEntity be = null;
            int volume = 0;
            if (state.getFluidState().is(BitterFluids.SUBSTANCE_FLUID.get())) {
                be = level.getBlockEntity(pos, BitterBlockEntities.SUBSTANCE_FLUID_BLOCK_ENTITY.get()).orElseThrow();
                volume = be.getVolume();
            }

            this(pos, state, be, volume);
        }
    }
}
