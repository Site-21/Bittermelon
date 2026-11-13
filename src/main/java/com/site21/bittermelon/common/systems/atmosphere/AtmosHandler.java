package com.site21.bittermelon.common.systems.atmosphere;

import com.site21.bittermelon.common.systems.atmosphere.data.AtmosChunkGrid;
import com.site21.bittermelon.common.systems.atmosphere.data.AtmosZones;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.init.neoforge.BitterBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.site21.bittermelon.init.custom.Substances.NITROGEN;
import static com.site21.bittermelon.init.custom.Substances.OXYGEN;
import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.ATMOSPHERE;
import static com.site21.bittermelon.util.SubstanceUtil.GAS_CONSTANT;

public final class AtmosHandler {
    private static final Direction[] DIRECTIONS = Direction.values();

    /**
     * Private constructor to prevent instantiation.
     */
    public static @Nullable AtmosZone getZoneAt(@NotNull Level level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        AtmosChunkGrid data = chunk.getData(ATMOSPHERE.get());

        return AtmosZones.get(level).getAtmosInstance(data.get(pos));
    }

    public static int zoneId(Level level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        AtmosChunkGrid data = chunk.getData(ATMOSPHERE.get());

        return data.get(pos);
    }

    public static void onBlockStateChange(ServerLevel level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (!sealingChanged(oldState, newState)) return;
        AtmosZones.get(level).addPending(pos);
    }

    private static boolean sealingChanged(BlockState oldState, BlockState newState) {
        for (Direction face : DIRECTIONS) {
            if (faceOpen(oldState, face) != faceOpen(newState, face)) return true;
        }
        return false;
    }

    /**
     * Removes the given atmosphere instance from the level.
     *
     * @param level    The level from which to remove the atmosphere instance.
     * @param instance The atmosphere instance to remove.
     */
    public static void removeAtmosphere(@NotNull Level level, @NotNull AtmosZone instance) {
        AtmosZones.get(level).removeAtmosInstance(instance.getId());
    }

    /**
     * Releases the given gas at the specified position in the level.
     * If an atmosphere instance already exists at that position, it updates the gas in that instance.
     * Otherwise, it creates a new atmosphere instance with the given gas.
     *
     * @param level The level in which to release the gas.
     * @param pos   The position at which to release the gas.
     * @param gas   The gas to release.
     */
    public static void releaseGas(@NotNull ServerLevel level, BlockPos pos, @NotNull SubstanceStack gas) {
        if (level.isClientSide()) return;

        AtmosZone instance = getZoneAt(level, pos);
        if (instance != null) {
            gas.setAmount(gas.getAmount());
            instance.updateGas(gas, level);
        } else {
            addAtmosphere(level, pos, 0, List.of(gas));
        }
    }

    /**
     * Releases multiple gases at the specified position in the level.
     *
     * @param level The level in which to release the gases.
     * @param pos   The position at which to release the gases.
     * @param gases The list of gases to release.
     */
    public static void releaseGas(ServerLevel level, BlockPos pos, @NotNull List<SubstanceStack> gases) {
        for (SubstanceStack stack : gases) {
            releaseGas(level, pos, stack);
        }
    }

    /**
     * Adds a new atmosphere instance at the specified position in the level.
     *
     * @param level       The level in which to add the atmosphere instance.
     * @param startPos    The starting position for the atmosphere instance.
     * @param temperature The temperature of the atmosphere instance.
     * @param gasses      The list of gases in the atmosphere instance.
     */
    public static void addAtmosphere(@NotNull ServerLevel level, BlockPos startPos, float temperature, List<SubstanceStack> gasses) {
        AtmosZone newInstance = new AtmosZone(0, temperature, gasses);
        updateAtmosphere(level, startPos);
        AtmosZones.get(level).addAtmosInstance(newInstance);
    }

    /**
     * Updates the atmosphere starting from the specified position using a flood fill algorithm.
     *
     * @param level     The level in which to update the atmosphere.
     * @param pos       The starting position for the flood fill.
     */
    public static void updateAtmosphere(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos) || level.canSeeSky(pos)) return;

        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();
        AtmosZone largestZone = null;

        for (Direction dir : Direction.values()) {
            neighbor.setWithOffset(pos, dir);
            if (!level.isLoaded(neighbor)) continue;

            int zoneId = zoneId(level, neighbor);
            if (zoneId != 0) {
                AtmosZone neighborZone = AtmosZones.get(level).getAtmosInstance(zoneId);
                if (neighborZone != null && (largestZone == null || neighborZone.getBlocks() > largestZone.getBlocks())) {
                    largestZone = neighborZone;
                }
            }
        }

        if (largestZone != null) {
            largestZone.addBlock();
            LevelChunk chunk = level.getChunkAt(pos);
            chunk.getData(ATMOSPHERE.get()).set(pos, largestZone.getId());
            return;
        }

        tryCreateZone(level, pos);
    }

    private static void tryCreateZone(ServerLevel level, BlockPos pos) {
        Set<BlockPos> blocks = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> contacts = new HashSet<>();
        boolean hasOpenCell = false;

        blocks.add(pos);
        queue.add(pos);

        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
        while (!queue.isEmpty()) {
            BlockPos cur = queue.poll();
            if (level.canSeeSky(cur)) return;
            BlockState state = level.getBlockState(cur);
            if (isFullyOpen(state)) hasOpenCell = true;

            for (Direction dir : Direction.values()) {
                mutPos.setWithOffset(cur, dir);

                if (!level.isLoaded(mutPos)) return;
                if (!connects(level, state, mutPos, dir)) continue;
                if (zoneId(level, mutPos) != 0) {
                    contacts.add(mutPos.immutable());
                    continue;
                }

                BlockPos next = mutPos.immutable();
                if (blocks.add(next)) {
                    if (blocks.size() > 8192) return;
                    queue.add(next);
                }
            }
        }
        if (!hasOpenCell) return;

        float temperature = temperature(level, pos);
        List<SubstanceStack> gases = airGases(temperature, blocks.size());

        AtmosZones zones = AtmosZones.get(level);
        AtmosZone zone = new AtmosZone(zones.getNextId(), temperature, gases, blocks.size());
        zones.addAtmosInstance(zone);

        for (BlockPos block : blocks) {
            LevelChunk chunk = level.getChunkAt(block);
            chunk.getData(ATMOSPHERE.get()).set(block, zone.getId());
        }
    }

    private static boolean isFullyOpen(BlockState state) {
        for (Direction face : DIRECTIONS) {
            if (!faceOpen(state, face)) return false;
        }
        return true;
    }

    private static boolean connects(Level level, BlockState state, BlockPos neighbor, Direction direction) {
        return faceOpen(state, direction) && faceOpen(level.getBlockState(neighbor), direction.getOpposite());
    }

    private static boolean faceOpen(BlockState state, Direction face) {
        if (state.isAir() || state.is(BitterBlockTags.PASSES_ATMOS)) return true;
        if (state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) return false;

        VoxelShape shape = state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        if (shape.isEmpty()) return true;

        // TODO: maybe this should be cached
        Direction.Axis axis = face.getAxis();
        VoxelShape projected = Shapes.empty();
        for (AABB box : shape.toAabbs()) {
            projected = Shapes.or(projected, Shapes.create(
                    axis == Direction.Axis.X ? 0 : box.minX,
                    axis == Direction.Axis.Y ? 0 : box.minY,
                    axis == Direction.Axis.Z ? 0 : box.minZ,
                    axis == Direction.Axis.X ? 1 : box.maxX,
                    axis == Direction.Axis.Y ? 1 : box.maxY,
                    axis == Direction.Axis.Z ? 1 : box.maxZ)
            );
        }

        return !Block.isFaceFull(projected, face);
    }

    private static float temperature(Level level, BlockPos pos) {
        return 273.15f + 20 * level.getBiome(pos).value().getBaseTemperature();
    }

    private static List<SubstanceStack> airGases(float temperature, int blocks) {
        float molesPerBlock = 101.325f * 1000 / (GAS_CONSTANT * temperature);
        return List.of(
                new SubstanceStack(NITROGEN.get(), (int) (molesPerBlock * 0.79f * blocks)),
                new SubstanceStack(OXYGEN.get(), (int) (molesPerBlock * 0.21f * blocks))
        );
    }
}
