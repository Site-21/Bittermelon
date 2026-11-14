package com.site21.bittermelon.common.systems.atmosphere;

import com.site21.bittermelon.common.systems.atmosphere.data.AtmosChunkGrid;
import com.site21.bittermelon.common.systems.atmosphere.data.AtmosZones;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.init.neoforge.BitterBlockTags;
import com.site21.bittermelon.util.SubstanceUtil;
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
import net.minecraft.world.level.levelgen.Heightmap;
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

    public static @Nullable AtmosZone getZoneAt(Level level, BlockPos pos) {
        AtmosZones zones = AtmosZones.get(level);
        int id = zoneId(level, pos);
        if (id != 0) return zones.getZone(id);

        BlockState state = level.getBlockState(pos);
        if (!openAnywhere(state)) return null;

        VoxelShape shape = state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
        for (Direction dir : DIRECTIONS) {
            if (Block.isFaceFull(shape.getFaceShape(dir), dir)) continue;
            mutPos.setWithOffset(pos, dir);
            if (!level.isLoaded(mutPos)) continue;

            int neighborId = zoneId(level, mutPos);
            if (neighborId != 0 && faceOpen(level.getBlockState(mutPos), dir.getOpposite())) {
                return zones.getZone(neighborId);
            }
        }

        return null;
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
        chunk.markUnsaved();
    }

    public static void onBlockStateChange(ServerLevel level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (!level.getServer().isSameThread()) return;
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
     * @param level The level in which to update the atmosphere.
     * @param pos   The starting position for the flood fill.
     */
    public static void updateAtmosphere(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos)) return;
        BlockState state = level.getBlockState(pos);
        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
        int oldId = zoneId(level, pos);
        if (oldId != 0) {
            removeAtmos(level, pos, mutPos, oldId);
        }

        // first, expand the atmosphere to the largest neighboring zone if one exists
        expandAtmos(level, pos, mutPos, state);

        // if the block is not part of a zone still, try to create a new zone
        if (zoneId(level, pos) == 0) {
            tryCreateZone(level, pos);
        }

        for (Direction dir : DIRECTIONS) {
            // creates zones in a newly sealed area
            mutPos.setWithOffset(pos, dir);
            if (!level.isLoaded(mutPos)) continue;
            if (zoneId(level, mutPos) != 0) continue;
            tryCreateZone(level, mutPos.immutable());
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

    private static void expandAtmos(ServerLevel level, BlockPos pos, BlockPos.MutableBlockPos mutPos, BlockState state) {
        AtmosZone largestZone = null;

        for (Direction dir : Direction.values()) {
            if (!faceOpen(state, dir)) continue;
            mutPos.setWithOffset(pos, dir);
            if (!level.isLoaded(mutPos)) continue;
            if (!faceOpen(level.getBlockState(mutPos), dir.getOpposite())) continue;

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
            chunk.markUnsaved();
        }
    }

    private static void tryCreateZone(ServerLevel level, BlockPos pos) {
        if (!openAnywhere(level.getBlockState(pos))) return;

        LongOpenHashSet blocks = new LongOpenHashSet();
        Queue<BlockPos> queue = new ArrayDeque<>();
        LongOpenHashSet contacts = new LongOpenHashSet();
        boolean hasOpenCell = false;

        blocks.add(pos.asLong());
        queue.add(pos);

        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
        while (!queue.isEmpty()) {
            BlockPos cur = queue.poll();
            if (isUnderOpenSky(level, cur)) return;

            BlockState state = level.getBlockState(cur);
            // this prevents creating pockets in partly open areas like doors
            if (fullyOpen(state)) hasOpenCell = true;

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

        float temperature = temperature(level, pos);
        List<SubstanceStack> gases = airGases(temperature, blocks.size());
        createZone(level, blocks, gases, temperature);
    }

    private static boolean fullyOpen(BlockState state) {
        for (Direction face : DIRECTIONS) {
            if (!faceOpen(state, face)) return false;
        }
        return true;
    }

    private static boolean openAnywhere(BlockState state) {
        for (Direction face : DIRECTIONS) {
            if (faceOpen(state, face)) return true;
        }
        return false;
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
                new SubstanceStack(NITROGEN.get(), Math.round(molesPerBlock * 0.79f * blocks)),
                new SubstanceStack(OXYGEN.get(), Math.round(molesPerBlock * 0.21f * blocks))
        );
    }

    @SuppressWarnings("unchecked")
    private static void trySplitZone(ServerLevel level, List<BlockPos> neighbors, AtmosZone zone, int zoneId) {
        Long2IntOpenHashMap visited = new Long2IntOpenHashMap();
        visited.defaultReturnValue(-1);
        LongOpenHashSet[] blocks = new LongOpenHashSet[neighbors.size()];
        Queue<BlockPos>[] queues = new Queue[neighbors.size()];
        boolean[] unloaded = new boolean[neighbors.size()];
        int active = 0;
        float lostShare = 0;
        int lostSize = 0;

        for (int i = 0; i < neighbors.size(); i++) {
            BlockPos start = neighbors.get(i);
            long key = start.asLong();
            visited.put(key, i);

            LongOpenHashSet blockSet = new LongOpenHashSet();
            blockSet.add(key);
            blocks[i] = blockSet;

            Queue<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);
            queues[i] = queue;

            active++;
        }

        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
        while (active > 1) {
            // process each neighbor in sequence and merge them if they collide
            for (int i = 0; i < queues.length && active > 1; i++) {
                Queue<BlockPos> queue = queues[i];
                if (queue == null) continue;

                BlockPos cur = queue.poll();
                // if a neighbor has no more blocks, it means that it is a split zone
                if (cur == null) {
                    // it's not guaranteed that an unloaded chunk is split, so we just assume that it is not
                    if (!unloaded[i]) {
                        lostSize += blocks[i].size();
                        float share = blocks[i].size() / (float) zone.getBlocks();
                        lostShare += share;
                        List<SubstanceStack> gases = SubstanceUtil.spreadSubstancesByPercentage(zone.getGases(), zone.getBlocks(), share);
                        createZone(level, blocks[i], gases, zone.getTemperature());
                    }
                    queues[i] = null;
                    active--;
                    continue;
                }

                BlockState state = level.getBlockState(cur);
                for (Direction dir : Direction.values()) {
                    if (!faceOpen(state, dir)) continue;
                    mutPos.setWithOffset(cur, dir);

                    if (!level.isLoaded(mutPos)) {
                        unloaded[i] = true;
                        continue;
                    }
                    if (zoneId(level, mutPos) != zoneId) continue;
                    if (!faceOpen(level.getBlockState(mutPos), dir.getOpposite())) continue;

                    long key = mutPos.asLong();
                    int owner = visited.get(key);

                    if (owner == -1) {
                        // unvisited block
                        visited.put(key, i);
                        blocks[i].add(key);
                        queue.add(mutPos.immutable());
                    } else if (owner != i) {
                        // visited block from another neighbor, merge the smaller neighbor into the larger one
                        int from = blocks[i].size() < blocks[owner].size() ? i : owner;
                        int to = (from == i) ? owner : i;

                        for (long block : blocks[from]) {
                            visited.put(block, to);
                        }

                        blocks[to].addAll(blocks[from]);
                        queues[to].addAll(queues[from]);
                        blocks[from] = null;
                        queues[from] = null;
                        active--;

                        unloaded[to] = unloaded[to] || unloaded[from];

                        if (from == i) {
                            queues[to].add(cur);
                            break;
                        }
                    }
                }
            }
        }

        zone.removeBlocks(lostSize);
        zone.multiplyGases(1 - lostShare);
    }

    private static void createZone(ServerLevel level, LongOpenHashSet blocks, List<SubstanceStack> gases, float temperature) {
        if (blocks.isEmpty()) return;

        AtmosZones zones = AtmosZones.get(level);
        AtmosZone zone = new AtmosZone(zones.getNextId(), temperature, gases, blocks.size());
        zones.addAtmosInstance(zone);

        for (long blockLong : blocks) {
            BlockPos block = BlockPos.of(blockLong);
            setZoneId(level, block, zone.getId());
        }
    }

    private static boolean isUnderOpenSky(Level level, BlockPos pos) {
        return pos.getY() >= level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
    }
}
