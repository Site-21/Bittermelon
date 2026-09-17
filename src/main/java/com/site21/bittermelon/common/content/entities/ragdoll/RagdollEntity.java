package com.site21.bittermelon.common.content.entities.ragdoll;

import com.github.stephengold.joltjni.Quat;
import com.github.stephengold.joltjni.RVec3;
import com.github.stephengold.joltjni.Vec3;
import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.entities.ragdoll.client.RagdollTransformation;
import com.site21.bittermelon.common.physics.PhysicsManager;
import com.site21.bittermelon.init.neoforge.BitterDataSerializers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

public class RagdollEntity extends Entity {
    private static final int PART_COUNT = 6;
    public Ragdoll ragdoll;
    public static final EntityDataAccessor<List<RagdollTransformation>> PART_TRANSFORMATIONS =
            SynchedEntityData.defineId(RagdollEntity.class, BitterDataSerializers.RAGDOLL_TRANSFORMATIONS.get());
    public static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(RagdollEntity.class, EntityDataSerializers.INT);
    private Vec3 pushDirection = new Vec3();
    private final RVec3[] prevPos = new RVec3[PART_COUNT];
    private final RVec3[] curPos = new RVec3[PART_COUNT];
    private final Quat[] prevRot = new Quat[PART_COUNT];
    private final Quat[] curRot = new Quat[PART_COUNT];
    private Entity owner;

    public RagdollEntity(EntityType<?> type, Level level) {
        super(type, level);
        if (!level.isClientSide()) {
            PhysicsManager.getPhysicsLevel(level.dimension()).addRagdoll(this);
        }

        for (int i = 0; i < PART_COUNT; i++) {
            prevPos[i] = new RVec3();
            curPos[i] = new RVec3();
            prevRot[i] = new Quat();
            curRot[i] = new Quat();
        }
    }

    public void addMotion(net.minecraft.world.phys.Vec3 motion) {
        this.pushDirection = new Vec3(motion.x + pushDirection.getX(), motion.y + pushDirection.getY(), motion.z + pushDirection.getZ());
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            List<RagdollTransformation> t = entityData.get(PART_TRANSFORMATIONS);
            for (int i = 0; i < PART_COUNT; i++) {
                prevPos[i].set(curPos[i]);
                curPos[i].set(t.get(i).pos);
                prevRot[i].set(curRot[i]);
                curRot[i].set(t.get(i).rot);
            }
            return;
        }

        if (ragdoll == null) {
            Vec3 startPos = new Vec3((float) getX(), (float) getY(), (float) getZ());
            ragdoll = new Ragdoll(PhysicsManager.getPhysicsLevel(level().dimension()).system(), startPos, getYRot());
        }

        ragdoll.addUniformVelocity(pushDirection);
        pushDirection = new Vec3();

        RVec3 torsoPos = ragdoll.getPart(1).getPosition();
        setPos(torsoPos.xx(), torsoPos.yy(), torsoPos.zz());

        List<RagdollTransformation> updated = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            RagdollTransformation t = new RagdollTransformation();
            t.update(ragdoll.getPart(i));
            updated.add(t);
        }

        entityData.set(PART_TRANSFORMATIONS, updated);
    }

    public RVec3 getPrevPos(int i) {
        return prevPos[i];
    }

    public RVec3 getCurPos(int i) {
        return curPos[i];
    }

    public Quat getPrevRot(int i) {
        return prevRot[i];
    }

    public Quat getCurRot(int i) {
        return curRot[i];
    }

    public List<RagdollTransformation> getPartTransformations() {
        return entityData.get(PART_TRANSFORMATIONS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        List<RagdollTransformation> initial = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            initial.add(new RagdollTransformation());
        }
        entityData.define(PART_TRANSFORMATIONS, initial);
        entityData.define(OWNER_ID, -1);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    public void clearOwner() {
        if (owner != null) {
            owner.stopRiding();
            owner = null;
        }
        entityData.set(OWNER_ID, -1);
    }

    public void clearOwnerNoOwnerUpdate() {
        owner = null;
        entityData.set(OWNER_ID, -1);
    }

    public void setOwner(Entity owner) {
        owner.startRiding(this);

        entityData.set(OWNER_ID, owner.getId());
        this.owner = owner;
    }

    public Entity getOwner() {
        if (owner == null && entityData.get(OWNER_ID) != -1) {
            Entity entity = level().getEntity(entityData.get(OWNER_ID));
            if (entity != null) {
                owner = entity;
            } else {
                Bittermelon.LOGGER.error("RagdollEntity has invalid owner ID: {}", entityData.get(OWNER_ID));
                entityData.set(OWNER_ID, -1);
            }
        }

        return owner;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        input.read("owner", UUIDUtil.CODEC).ifPresent(uuid -> {
            if (level() instanceof ServerLevel level) {
                Entity entity = level.getEntity(uuid);
                if (entity != null) {
                    setOwner(entity);
                }
            }
        });
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        if (owner != null) {
            output.store("owner", UUIDUtil.CODEC, owner.getUUID());
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, net.minecraft.world.phys.Vec3 location) {
        Entity owner = getOwner();
        return owner != null ? owner.interact(player, hand, location) : super.interact(player, hand, location);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (passenger == getOwner()) {
            clearOwnerNoOwnerUpdate();
        }
        super.removePassenger(passenger);
    }

    @Override
    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (!hasPassenger(passenger)) return;

        RVec3 torsoPos;
        if (level().isClientSide()) {
            torsoPos = curPos[1];
        } else if (ragdoll != null) {
            torsoPos = ragdoll.getPart(1).getPosition();
        } else {
            moveFunction.accept(passenger, getX(), getY(), getZ());
            return;
        }

        moveFunction.accept(passenger, torsoPos.xx(), torsoPos.yy(), torsoPos.zz());
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (ragdoll != null && !level().isClientSide()) {
            ragdoll.destroy();
            PhysicsManager.getPhysicsLevel(level()).removeRagdoll(this);
            ragdoll = null;
        }
    }
}
