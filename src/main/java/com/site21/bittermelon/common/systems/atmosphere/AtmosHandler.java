package com.site21.bittermelon.common.systems.atmosphere;

import com.site21.bittermelon.common.systems.atmosphere.data.AtmosChunkGrid;
import com.site21.bittermelon.common.systems.atmosphere.data.AtmosZones;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.init.neoforge.BitterBlockTags;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
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

        return AtmosZones.get(level).getZone(data.get(pos));
    }

    public static int zoneId(Level level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        AtmosChunkGrid data = chunk.getData(ATMOSPHERE.get());

        return data.get(pos);
    }

    public static void setZoneId(Level level, BlockPos pos, int zoneId) {
        LevelChunk chunk = level.getChunkAt(pos);
        AtmosChunkGrid data = chunk.getData(ATMOSPHERE.get());

        data.set(pos, zoneId);
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
        AtmosZones.get(level).removeZone(instance.getId());
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
        int zoneId = zoneId(level, pos);
        if (zoneId != 0 && !isFullyOpen(level.getBlockState(pos))) {
            removeAtmos(level, pos, neighbor, zoneId);
        } else {
            expandAtmos(level, pos, neighbor);
        }
    }

    private static void removeAtmos(ServerLevel level, BlockPos pos, BlockPos.MutableBlockPos mutPos, int zoneId) {
        AtmosZone zone = AtmosZones.get(level).getZone(zoneId);
        zone.removeBlock();
        setZoneId(level, pos, 0);

        List<BlockPos> formerNeighbors = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            mutPos.setWithOffset(pos, dir);
            if (!level.isLoaded(mutPos)) continue;

            int neighborZoneId = zoneId(level, mutPos);
            if (neighborZoneId == zoneId) {
                formerNeighbors.add(mutPos.immutable());
            }
        }

        trySplitZone(level, formerNeighbors, zone, zoneId);
    }

    private static void expandAtmos(ServerLevel level, BlockPos pos, BlockPos.MutableBlockPos mutPos) {
        AtmosZone largestZone = null;

        for (Direction dir : Direction.values()) {
            mutPos.setWithOffset(pos, dir);
            if (!level.isLoaded(mutPos)) continue;

            // TODO: Dissolve zones if one of neighbors has id 0
            int zoneId = zoneId(level, mutPos);
            if (zoneId != 0) {
                AtmosZone neighborZone = AtmosZones.get(level).getZone(zoneId);
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
        LongOpenHashSet blocks = new LongOpenHashSet();
        Queue<BlockPos> queue = new ArrayDeque<>();
        LongOpenHashSet contacts = new LongOpenHashSet();
        boolean hasOpenCell = false;

        blocks.add(pos.asLong());
        queue.add(pos);

        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
        while (!queue.isEmpty()) {
            BlockPos cur = queue.poll();
            if (level.canSeeSky(cur)) return;
            BlockState state = level.getBlockState(cur);
            if (isFullyOpen(state)) hasOpenCell = true;

            for (Direction dir : Direction.values()) {
                if (!faceOpen(state, dir)) continue;
                mutPos.setWithOffset(cur, dir);

                if (!level.isLoaded(mutPos)) return;
                if (!faceOpen(level.getBlockState(mutPos), dir.getOpposite())) continue;
                if (zoneId(level, mutPos) != 0) {
                    contacts.add(mutPos.asLong());
                    continue;
                }

                if (blocks.add(mutPos.asLong())) {
                    if (blocks.size() > 8192) return;
                    queue.add(mutPos.immutable());
                }
            }
        }
        if (!hasOpenCell) return;
        createZone(level, blocks, pos);
    }

    private static boolean isFullyOpen(BlockState state) {
        for (Direction face : DIRECTIONS) {
            if (!faceOpen(state, face)) return false;
        }
        return true;
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
        return List.of(new SubstanceStack(NITROGEN.get(), (int) (molesPerBlock * 0.79f * blocks)), new SubstanceStack(OXYGEN.get(), (int) (molesPerBlock * 0.21f * blocks)));
    }

    private static void trySplitZone(ServerLevel level, List<BlockPos> neighbors, AtmosZone zone, int zoneId) {
        Long2IntOpenHashMap visited = new Long2IntOpenHashMap();
        visited.defaultReturnValue(-1);
        List<LongOpenHashSet> blocks = new ArrayList<>();
        List<Queue<BlockPos>> queues = new ArrayList<>();
        int active = 0;

        for (int i = 0; i < neighbors.size(); i++) {
            BlockPos start = neighbors.get(i);
            long key = start.asLong();
            visited.put(key, i);

            LongOpenHashSet blockSet = new LongOpenHashSet();
            blockSet.add(key);
            blocks.add(blockSet);

            Queue<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);
            queues.add(queue);

            active++;
        }

        while (active > 1) {
            for (int i = 0; i < queues.size() && active > 1; i++) {
                Queue<BlockPos> queue = queues.get(i);
                if (queue == null) continue;

                BlockPos cur = queue.poll();
                if (cur == null) {
                    zone.removeBlocks(blocks.get(i).size());
                    createZone(level, blocks.get(i), neighbors.get(i));
                    queues.set(i, null);
                    active--;
                    continue;
                }

                for (Direction dir : Direction.values()) {
                    BlockPos next = cur.relative(dir);
                    if (!level.isLoaded(next) || zoneId(level, next) != zoneId) continue;

                    long key = next.asLong();
                    int owner = visited.get(key);

                    if (owner == -1) {
                        visited.put(key, i);
                        blocks.get(i).add(key);
                        queue.add(next);
                    } else if (owner != i) {
                        int from = blocks.get(i).size() < blocks.get(owner).size() ? i : owner;
                        int to = (from == i) ? owner : i;

                        for (long block : blocks.get(from)) {
                            visited.put(block, to);
                        }

                        blocks.get(to).addAll(blocks.get(from));
                        queues.get(to).addAll(queues.get(from));
                        blocks.set(from, null);
                        queues.set(from, null);
                        active--;

                        if (from == i) {
                            queues.get(to).add(cur);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void createZone(ServerLevel level, LongOpenHashSet blocks, BlockPos origin) {
        if (blocks.isEmpty()) return;

        float temperature = temperature(level, origin);
        List<SubstanceStack> gases = airGases(temperature, blocks.size());

        AtmosZones zones = AtmosZones.get(level);
        AtmosZone zone = new AtmosZone(zones.getNextId(), temperature, gases, blocks.size());
        zones.addAtmosInstance(zone);

        for (long blockLong : blocks) {
            BlockPos block = BlockPos.of(blockLong);
            LevelChunk chunk = level.getChunkAt(block);
            chunk.getData(ATMOSPHERE.get()).set(block, zone.getId());
        }
    }
}
