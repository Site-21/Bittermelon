package com.site21.bittermelon.common.content.entities.scp939.behavior;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryCondition;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.base.ExtendedBehaviour;
import net.tslat.smartbrainlib.library.object.MemoryTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class SweepArea extends ExtendedBehaviour<PathfinderMob> {
    public static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder()
            .usesMemories(MemoryModuleType.WALK_TARGET);

    private static final int SEARCH_RADIUS = 2;
    private static final int CELL_SIZE = 4;
    private static final int OFFSET_RESET_INTERVAL = 2400;
    private static final int MAX_CANDIDATES = 21;

    private static final Long2LongOpenHashMap VISIT_GRID = new Long2LongOpenHashMap();
    private static final Long2IntOpenHashMap ACTIVITY_GRID = new Long2IntOpenHashMap();
    private long lastOffsetReset = 0;
    private int offsetX = 0;
    private int offsetZ = 0;

    @Override
    public Set<MemoryCondition<?, ?>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean shouldKeepRunning(PathfinderMob entity) {
        return true;
    }

    @Override
    protected void tick(PathfinderMob entity) {
        long key = gridKey(entity.blockPosition());
        VISIT_GRID.put(key, entity.level().getGameTime());
        if (!entity.getNavigation().isDone()) return;

        Path path = pickTarget(entity);
        if (path == null) return;
        BlockPos targetPos = path.getTarget();

        if (entity.level() instanceof ServerLevel level) {
//            level.sendParticles(
//                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
//                targetPos.getX() + 0.5,
//                targetPos.getY() + 0.5,
//                targetPos.getZ() + 0.5,
//                1,
//                0, 0, 0, 0
//            );
        }

        entity.getNavigation().moveTo(path, 0.8);
        entity.getLookControl().setLookAt(Vec3.atCenterOf(targetPos));
    }

    private Path pickTarget(PathfinderMob entity) {
        long now = entity.level().getGameTime();

        if (now - lastOffsetReset > OFFSET_RESET_INTERVAL) {
            offsetX = entity.getRandom().nextInt(CELL_SIZE);
            offsetZ = entity.getRandom().nextInt(CELL_SIZE);
            lastOffsetReset = now;
        }

        BlockPos origin = entity.blockPosition();
        Vec3 heading = entity.getDeltaMovement();

        List<Candidate> candidates = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int cx = -SEARCH_RADIUS; cx <= SEARCH_RADIUS; cx++) {
            for (int cz = -SEARCH_RADIUS; cz <= SEARCH_RADIUS; cz++) {
                if (candidates.size() >= MAX_CANDIDATES) break;
                int x = origin.getX() + cx * CELL_SIZE + offsetX;
                int z = origin.getZ() + cz * CELL_SIZE + offsetZ;
                pos.set(x, origin.getY(), z);
                if (pos.equals(origin)) continue;

                if (entity.level().getBlockState(pos).isCollisionShapeFullBlock(entity.level(), pos)) continue;
                candidates.add(new Candidate(pos.immutable(), scoreCell(pos, now, heading, origin)));

                if (entity.level() instanceof ServerLevel level) {
//                    level.sendParticles(
//                            ParticleTypes.FLAME,
//                            pos.getX() + 0.5,
//                            pos.getY() + 0.5,
//                            pos.getZ() + 0.5,
//                            1,
//                            0, 0, 0, 0
//                    );
                }
            }
        }

        candidates.sort(Comparator.comparingDouble(Candidate::score).reversed());
        System.out.println("Checking " + candidates.size() + " candidates");

        for (int i = 0; i < Math.min(MAX_CANDIDATES, candidates.size()); i++) {
            Path path = entity.getNavigation().createPath(candidates.get(i).pos(), 0);
            if (path != null && path.canReach()) {
                System.out.println("Found path on attempt: " + i);
                return path;
            }
        }

        return null;
    }

    private float scoreCell(BlockPos blockPos, long currentTime, Vec3 heading, BlockPos pos) {
        if (blockPos.distSqr(pos) <= 1) {
            return 0;
        }

        long gridKey = gridKey(blockPos);
        long lastVisitTime = VISIT_GRID.getOrDefault(gridKey, 0L);
        long timeSinceLastVisit = currentTime - lastVisitTime;

        int activityLevel = ACTIVITY_GRID.getOrDefault(gridKey, 0);

        float momentum = 0;
        if (heading.lengthSqr() > 0.01) {
            Vec3 toCell = new Vec3(blockPos.getX() - pos.getX(), 0, blockPos.getZ() - pos.getZ()).normalize();
            momentum = (float) heading.dot(toCell) * 100;
        }

        float distancePenalty = (float) blockPos.distSqr(pos) * 10;

        return timeSinceLastVisit + activityLevel + momentum - distancePenalty;
    }

    private static long gridKey(BlockPos pos) {
        int x = Math.floorDiv(pos.getX(), CELL_SIZE);
        int z = Math.floorDiv(pos.getZ(), CELL_SIZE);
        return (x & 0xFFFFFFFFL) | ((long) z << 32);
    }

    public static void addActivity(BlockPos pos, int amount) {
        long key = gridKey(pos);
        int currentActivity = ACTIVITY_GRID.getOrDefault(key, 0);
        ACTIVITY_GRID.put(key, currentActivity + amount);
    }

    private record Candidate(BlockPos pos, float score) {}
}
