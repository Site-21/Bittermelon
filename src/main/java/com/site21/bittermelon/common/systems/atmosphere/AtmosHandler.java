package com.site21.bittermelon.common.systems.atmosphere;

import com.site21.bittermelon.common.systems.atmosphere.data.AtmosChunkGrid;
import com.site21.bittermelon.common.systems.atmosphere.data.AtmosZones;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.ATMOSPHERE;

public final class AtmosHandler {
    private static final Direction[] DIRECTIONS = Direction.values();

    /** Private constructor to prevent instantiation. */
    public static @Nullable AtmosZone getAtmosInstanceAt(@NotNull Level level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        AtmosChunkGrid data = chunk.getData(ATMOSPHERE.get());

        return null;
    }

    /**
     * Updates the atmosphere at the given position and its adjacent blocks.
     * @param level The level in which to update the atmosphere.
     * @param pos The position at which to update the atmosphere.
     */
    public static void updateAtmosphereAt(@NotNull Level level, @NotNull BlockPos pos) {
        if (level.isClientSide()) return;

        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
        Set<AtmosZone> updatedInstances = new HashSet<>();

        for (Direction direction : DIRECTIONS) {
            mutableBlockPos.move(direction);

            if (getAtmosInstanceAt(level, mutableBlockPos) instanceof AtmosZone atmosZone
                    && !updatedInstances.contains(atmosZone)) {
                updateAtmosphere(level, mutableBlockPos.immutable(), atmosZone);
                updatedInstances.add(atmosZone);
            }
        }
    }

    /**
     * Removes the given atmosphere instance from the level.
     * @param level The level from which to remove the atmosphere instance.
     * @param instance The atmosphere instance to remove.
     */
    public static void removeAtmosphere(@NotNull Level level, @NotNull AtmosZone instance) {
        AtmosZones.get(level).removeAtmosInstance(instance.getId());
    }

    /**
     * Releases the given gas at the specified position in the level.
     * If an atmosphere instance already exists at that position, it updates the gas in that instance.
     * Otherwise, it creates a new atmosphere instance with the given gas.
     * @param level The level in which to release the gas.
     * @param pos The position at which to release the gas.
     * @param gas The gas to release.
     */
    public static void releaseGas(@NotNull Level level, BlockPos pos, @NotNull SubstanceStack gas) {
        if (level.isClientSide()) return;

        AtmosZone instance = getAtmosInstanceAt(level, pos);
        if (instance != null) {
            gas.setAmount(gas.getAmount());
            instance.updateGas(gas, level);
        } else {
            addAtmosphere(level, pos, gas.getTemperature(), List.of(gas));
        }
    }

    /**
     * Releases multiple gases at the specified position in the level.
     * @param level The level in which to release the gases.
     * @param pos The position at which to release the gases.
     * @param gases The list of gases to release.
     */
    public static void releaseGas(Level level, BlockPos pos, @NotNull List<SubstanceStack> gases) {
        for (SubstanceStack stack : gases) {
            releaseGas(level, pos, stack);
        }
    }

    /**
     * Adds a new atmosphere instance at the specified position in the level.
     * @param level The level in which to add the atmosphere instance.
     * @param startPos The starting position for the atmosphere instance.
     * @param temperature The temperature of the atmosphere instance.
     * @param gasses The list of gases in the atmosphere instance.
     */
    public static void addAtmosphere(@NotNull Level level, BlockPos startPos, float temperature, List<SubstanceStack> gasses) {
        if (level.isClientSide()) return;

        AtmosZone newInstance = new AtmosZone(0, temperature, gasses);
        updateAtmosphere(level, startPos, newInstance);
        AtmosZones.get(level).addAtmosInstance(newInstance);
    }

    /**
     * Updates the atmosphere starting from the specified position using a flood fill algorithm.
     * @param level The level in which to update the atmosphere.
     * @param startPos The starting position for the flood fill.
     * @param atmosZone The atmosphere instance to update.
     */
    public static void updateAtmosphere(@NotNull Level level, BlockPos startPos, AtmosZone atmosZone) {
        if (level.canSeeSky(startPos)) return;
        Set<BlockPos> floodFill = FloodFill.run(level, startPos, 1000);
        updateAtmosphere(level, atmosZone, floodFill);
    }

    /**
     * Updates the atmosphere instance with the given set of blocks.
     * Merges with existing instances if necessary and redistributes gases.
     * @param level The level in which to update the atmosphere.
     * @param atmosZone The atmosphere instance to update.
     * @param floodFill The set of blocks to include in the atmosphere instance.
     */
    public static void updateAtmosphere(@NotNull Level level, @NotNull AtmosZone atmosZone, @NotNull Set<BlockPos> floodFill) {
//        if (level.isClientSide()) return;
//        Set<UUID> mergedInstances = new HashSet<>();
//
//        // Convert floodFill BlockPos to LongSet for easier comparison
//        LongSet newBlocks = new LongOpenHashSet();
//        for (BlockPos pos : floodFill) {
//            newBlocks.add(pos.asLong());
//        }
//
//        // Identify disconnected blocks that are no longer part of the flood fill
//        LongSet disconnectedBlocks = new LongOpenHashSet(atmosZone.getBlocks());
//        disconnectedBlocks.removeAll(newBlocks);
//
//        // If there are disconnected blocks, create a new AtmosInstance for them
//        if (!disconnectedBlocks.isEmpty()) {
//            AtmosZone newInstance = new AtmosZone(
//                    atmosZone.getTemperature(),
//                    // Distribute gases proportionally based on the number of blocks
//                    atmosZone.getGases().stream()
//                            .map(gas -> {
//                                SubstanceStack copy = gas.copy();
//                                copy.setAmount(gas.getAmount() * disconnectedBlocks.size() / atmosZone.getBlocks().size());
//                                return copy;
//                            })
//                            .toList()
//            );
//
//            // Reduce gases in the original instance proportionally
//            int remainingRatio = newBlocks.size() / atmosZone.getBlocks().size();
//            for (SubstanceStack gas : atmosZone.getGases()) {
//                gas.setAmount(gas.getAmount() * remainingRatio);
//            }
//
//            // Assign disconnected blocks to the new instance
//            for (long packedPos : disconnectedBlocks) {
//                BlockPos pos = BlockPos.of(packedPos);
//                LevelChunk chunk = level.getChunkAt(pos);
//                AtmosChunkData data = chunk.getData(ATMOSPHERE.get());
//                data.addAtmosBlock(pos, newInstance.getId());
//                newInstance.addBlock(packedPos, level);
//            }
//
//            AtmosInstancesData.get(level).addAtmosInstance(newInstance);
//        }
//
//        // Clear current blocks and reassign based on flood fill
//        atmosZone.getBlocks().clear();
//
//        for (BlockPos pos : floodFill) {
//            LevelChunk chunk = level.getChunkAt(pos);
//            AtmosChunkData data = chunk.getData(ATMOSPHERE.get());
//
//            // Merge with existing instance if present
//            AtmosZone existing = getAtmosInstanceAt(level, pos);
//            if (existing != null && !mergedInstances.contains(existing.getId()) && existing != atmosZone) {
//                atmosZone.merge(existing, level);
//                mergedInstances.add(existing.getId());
//                removeAtmosphere(level, existing);
//            }
//            data.addAtmosBlock(pos, atmosZone.getId());
//            atmosZone.addBlock(pos.asLong(), level);
//        }
//
//        // Sync the updated instance
//        AtmosInstancesData.get(level).syncInstance(atmosZone);
    }

    public static void onBlockUpdate(ServerLevel level, BlockPos pos) {
//        if (level.getBlockState(pos).is(BitterBlockTags.PASSES_ATMOS)) return;
//
//        LevelChunk chunk = level.getChunkAt(pos);
//        AtmosChunkData data = chunk.getData(ATMOSPHERE.get());
//
//        if (!level.getBlockState(pos).canBeReplaced() && !level.getBlockState(pos).is(BitterBlockTags.PASSES_ATMOS)) {
//            if (AtmosHandler.getAtmosInstanceAt(level, pos) != null) {
//                AtmosHandler.getAtmosInstanceAt(level, pos).removeBlock(pos.asLong(), level);
//            }
//            data.removeAtmosBlock(pos);
//        }
//
//        // TODO: Proper updating for doors
//        AtmosHandler.updateAtmosphereAt(level, pos);
    }
}
