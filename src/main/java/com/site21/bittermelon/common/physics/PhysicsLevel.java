package com.site21.bittermelon.common.physics;

import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.enumerate.EActivation;
import com.github.stephengold.joltjni.enumerate.EMotionType;
import com.site21.bittermelon.common.systems.ragdoll.RagdollEntity;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.geckolib.GeckoLibConstants.LOGGER;
import static com.site21.bittermelon.common.physics.PhysicsManager.*;

public class PhysicsLevel {
    private static final int MAX_REBUILDS_PER_TICK = 8;
    private final PhysicsSystem system;
    private final Set<RagdollEntity> ragdolls = new HashSet<>();
    private final Long2IntOpenHashMap sectionToBodyId = new Long2IntOpenHashMap();
    private final LongOpenHashSet dirtySections = new LongOpenHashSet();
    private final boolean[] solid = new boolean[4096];
    private final boolean[] consumed = new boolean[4096];
    private boolean optimizeBP;

    public PhysicsLevel(PhysicsSystem system) {
        this.system = system;
        sectionToBodyId.defaultReturnValue(-1);
    }

    public PhysicsSystem system() {
        return system;
    }

    public void markDirty(BlockPos pos) {
        int sx = pos.getX() >> 4;
        int sy = pos.getY() >> 4;
        int sz = pos.getZ() >> 4;
        dirtySections.add(SectionPos.asLong(sx, sy, sz));
    }

    public void unloadChunk(ChunkPos pos) {
        LongOpenHashSet toRemove = new LongOpenHashSet();
        for (long key : sectionToBodyId.keySet()) {
            if (SectionPos.x(key) == pos.x() && SectionPos.z(key) == pos.z()) {
                toRemove.add(key);
            }
        }
        removeSections(toRemove);
    }

    public void update(Level level) {
        rebuildDirty(level);
        updateSections(level);
        maybeOptimizeBroadPhase(level.getGameTime());

        system.update(0.05f, 1, TEMP_ALLOCATOR, JOB_SYSTEM);
    }

    private void maybeOptimizeBroadPhase(long time) {
        if (!optimizeBP) return;
        if (time % 100 != 0) return;
        if (!dirtySections.isEmpty()) return;

        system.optimizeBroadPhase();
        optimizeBP = false;
    }

    private void updateSections(Level level) {
        LongOpenHashSet desired = new LongOpenHashSet();

        for (RagdollEntity ragdoll : ragdolls) {
            desired.addAll(sectionsNear(ragdoll.blockPosition()));
        }

        LongOpenHashSet toRemove = new LongOpenHashSet(sectionToBodyId.keySet());
        toRemove.removeAll(desired);

        LongOpenHashSet toAdd = new LongOpenHashSet(desired);
        toAdd.removeAll(sectionToBodyId.keySet());

        if (toRemove.isEmpty() && toAdd.isEmpty()) {
            return;
        }

        removeSections(toRemove);
        addSections(level, toAdd);
    }

    private LongOpenHashSet sectionsNear(BlockPos pos) {
        LongOpenHashSet result = new LongOpenHashSet();
        int sx = pos.getX() >> 4;
        int sy = pos.getY() >> 4;
        int sz = pos.getZ() >> 4;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    result.add(SectionPos.asLong(sx + dx, sy + dy, sz + dz));
                }
            }
        }
        return result;
    }

    private void addSections(Level level, LongOpenHashSet toAdd) {
        for (long key : toAdd) {
            int sx = SectionPos.x(key);
            int sy = SectionPos.y(key);
            int sz = SectionPos.z(key);

            LevelChunk chunk = level.getChunkSource().getChunkNow(sx, sz);
            if (chunk == null) continue;

            int sectionIndex = level.getSectionIndexFromSectionY(sy);
            if (sectionIndex < 0 || sectionIndex >= chunk.getSectionsCount()) continue;

            int originX = sx << 4;
            int originY = sy << 4;
            int originZ = sz << 4;

            ShapeRefC shape = buildSectionShape(level, chunk, originX, originY, originZ);
            sectionToBodyId.put(key, shape == null ? -1 : createSectionBody(shape, originX, originY, originZ));

            dirtySections.remove(key);
            optimizeBP = true;
        }
    }

    private void removeSections(LongOpenHashSet toRemove) {
        if (toRemove.isEmpty()) return;

        int[] ids = new int[toRemove.size()];
        int i = 0;
        for (long key : toRemove) {
            int bodyId = sectionToBodyId.remove(key);
            if (bodyId != sectionToBodyId.defaultReturnValue()) {
                ids[i++] = bodyId;
            }
        }
        if (i == 0) return;
        if (i != ids.length) ids = Arrays.copyOf(ids, i);

        BodyInterface bi = system.getBodyInterface();
        BodyIdArray idArray = new BodyIdArray(ids);
        bi.removeBodies(idArray, ids.length);
        bi.destroyBodies(idArray, ids.length);
        optimizeBP = true;
    }

    private void rebuildDirty(Level level) {
        if (dirtySections.isEmpty()) return;

        BodyInterface bi = system.getBodyInterface();
        LongIterator it = dirtySections.iterator();
        int rebuilds = MAX_REBUILDS_PER_TICK;

        while (it.hasNext() && rebuilds > 0) {
            long key = it.nextLong();

            if (!sectionToBodyId.containsKey(key)) {
                it.remove();
                continue;
            }

            int sx = SectionPos.x(key);
            int sy = SectionPos.y(key);
            int sz = SectionPos.z(key);

            LevelChunk chunk = level.getChunkSource().getChunkNow(sx, sz);
            if (chunk == null) {
                it.remove();
                continue;
            }

            rebuilds--;
            it.remove();

            int originX = sx << 4;
            int originY = sy << 4;
            int originZ = sz << 4;

            int oldId = sectionToBodyId.get(key);
            ShapeRefC shape = buildSectionShape(level, chunk, originX, originY, originZ);

            if (shape == null) {
                if (oldId != -1) {
                    bi.removeBody(oldId);
                    bi.destroyBody(oldId);
                }
                sectionToBodyId.put(key, -1);
            } else if (oldId != -1) {
                bi.setShape(oldId, shape, false, EActivation.DontActivate);
            } else {
                sectionToBodyId.put(key, createSectionBody(shape, originX, originY, originZ));
            }

            wakeBodiesNear(originX, originY, originZ);
            optimizeBP = true;
        }
    }

    private void wakeBodiesNear(int originX, int originY, int originZ) {
        AaBox box = new AaBox(
                new Vec3(originX - 1f, originY - 1f, originZ - 1f),
                new Vec3(originX + 17f, originY + 17f, originZ + 17f)
        );
        system.getBodyInterface().activateBodiesInAaBox(box, new BroadPhaseLayerFilter(), new ObjectLayerFilter());
    }

    private int createSectionBody(ShapeRefC shapeRef, int originX, int originY, int originZ) {
        BodyCreationSettings bcs = new BodyCreationSettings();
        bcs.setShape(shapeRef);
        bcs.setMotionType(EMotionType.Static);
        bcs.setObjectLayer(NON_MOVING);
        bcs.setFriction(0.9f);
        bcs.setRestitution(0.0f);
        bcs.setPosition(originX, originY, originZ);

        BodyInterface bi = system.getBodyInterface();
        Body body;
        try {
            body = bi.createBody(bcs);
        } catch (Exception e) {
            LOGGER.error("Failed to create body for section {} {} {}: {}", originX, originY, originZ, e.getMessage());
            return -1;
        }

        bi.addBody(body, EActivation.DontActivate);
        return body.getId();
    }

    private ShapeRefC buildSectionShape(Level level, LevelChunk chunk, int originX, int originY, int originZ) {
        int sectionIndex = level.getSectionIndex(originY);
        LevelChunkSection section = chunk.getSection(sectionIndex);
        if (section.hasOnlyAir()) return null;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        fillSolidMask(level, section, pos, originX, originY, originZ, solid);
        Arrays.fill(consumed, false);

        StaticCompoundShapeSettings compound = new StaticCompoundShapeSettings();
        boolean any = false;

        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                int x = 0;
                while (x < 16) {
                    int idx = idx(x, y, z);
                    if (consumed[idx] || !solid[idx]) {
                        x++;
                        continue;
                    }

                    int runX = 1;
                    while (x + runX < 16 && !consumed[idx + runX] && solid[idx + runX]) {
                        runX++;
                    }

                    int runZ = 1;
                    outerZ:
                    while (z + runZ < 16) {
                        int rowIdx = idx + (runZ << 4);
                        for (int dx = 0; dx < runX; dx++) {
                            if (consumed[rowIdx + dx] || !solid[rowIdx + dx]) break outerZ;
                        }
                        runZ++;
                    }

                    int runY = 1;
                    outerY:
                    while (y + runY < 16) {
                        int layerIdx = idx + (runY << 8);
                        for (int dz = 0; dz < runZ; dz++) {
                            int rowIdx = layerIdx + (dz << 4);
                            for (int dx = 0; dx < runX; dx++) {
                                if (consumed[rowIdx + dx] || !solid[rowIdx + dx]) break outerY;
                            }
                        }
                        runY++;
                    }

                    for (int dy = 0; dy < runY; dy++) {
                        for (int dz = 0; dz < runZ; dz++) {
                            int rowIdx = idx + (dy << 8) + (dz << 4);
                            for (int dx = 0; dx < runX; dx++) {
                                consumed[rowIdx + dx] = true;
                            }
                        }
                    }

                    Vec3 halfExtent = new Vec3(runX / 2f, runY / 2f, runZ / 2f);
                    Vec3 center = new Vec3(
                            x + halfExtent.getX(),
                            y + halfExtent.getY(),
                            z + halfExtent.getZ());
                    compound.addShape(center, new Quat(), new BoxShape(halfExtent));
                    any = true;

                    x += runX;
                }
            }
        }

        any |= addIrregularShapes(level, section, pos, originX, originY, originZ, consumed, compound);

        if (!any) return null;

        try (ShapeResult result = compound.create()) {
            if (result.hasError()) {
                LOGGER.warn("Failed to build chunk shape at {} {} {}: {}", originX, originY, originZ, result.getError());
                return null;
            }
            return result.get();
        }
    }

    private void fillSolidMask(Level level, LevelChunkSection section, BlockPos.MutableBlockPos pos,
                               int originX, int originY, int originZ, boolean[] solid) {
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    solid[idx(x, y, z)] = isFull(level, section, pos, originX, originY, originZ, x, y, z);
                }
            }
        }
    }

    private static int idx(int x, int y, int z) {
        return (y << 8) | (z << 4) | x;
    }

    private boolean addIrregularShapes(Level level, LevelChunkSection section, BlockPos.MutableBlockPos pos,
                                       int originX, int originY, int originZ, boolean[] consumed,
                                       StaticCompoundShapeSettings compound) {
        boolean any = false;
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    if (consumed[idx(x, y, z)]) continue;
                    BlockState state = section.getBlockState(x, y, z);
                    if (state.isAir()) continue;

                    pos.set(originX + x, originY + y, originZ + z);
                    VoxelShape shape = state.getCollisionShape(level, pos);
                    if (shape.isEmpty()) continue;

                    for (AABB box : shape.toAabbs()) {
                        Vec3 half = new Vec3(box.getXsize() / 2, box.getYsize() / 2, box.getZsize() / 2);
                        if (half.getX() <= 0 || half.getY() <= 0 || half.getZ() <= 0) continue;
                        Vec3 center = new Vec3(
                                x + (float) (box.minX + half.getX()),
                                y + (float) (box.minY + half.getY()),
                                z + (float) (box.minZ + half.getZ()));
                        compound.addShape(center, new Quat(), new BoxShape(half));
                        any = true;
                    }
                }
            }
        }
        return any;
    }

    private boolean isFull(Level level, LevelChunkSection section, BlockPos.MutableBlockPos pos, int originX, int originY,
                           int originZ, int x, int y, int z) {
        pos.set(originX + x, originY + y, originZ + z);
        BlockState state = section.getBlockState(x, y, z);
        return state.isCollisionShapeFullBlock(level, pos);
    }

    public void addRagdoll(RagdollEntity ragdoll) {
        ragdolls.add(ragdoll);
    }

    public void removeRagdoll(RagdollEntity ragdoll) {
        ragdolls.remove(ragdoll);
    }
}
