package com.site21.bittermelon.common.content.entities.scp939;

import com.site21.bittermelon.common.content.entities.ragdoll.RagdollUtil;
import com.site21.bittermelon.common.content.entities.scp939.behavior.*;
import com.site21.bittermelon.common.content.entities.scp939.lure.LureSystem;
import com.site21.bittermelon.common.systems.ai.base.BitterMob;
import com.site21.bittermelon.common.systems.ai.base.Need;
import com.site21.bittermelon.common.systems.ai.base.NeedInstance;
import com.site21.bittermelon.common.systems.ai.behavior.attack.Pull;
import com.site21.bittermelon.common.systems.ai.behavior.attack.YankItem;
import com.site21.bittermelon.common.systems.ai.sensors.VisionConeSensor;
import com.site21.bittermelon.common.systems.ai.vibration.BitterAngerManagement;
import com.site21.bittermelon.common.systems.ai.vibration.BitterVibrationListener;
import com.site21.bittermelon.common.systems.character.Character;
import com.site21.bittermelon.init.neoforge.BitterActivity;
import com.site21.bittermelon.init.neoforge.BitterDataSerializers;
import com.site21.bittermelon.init.neoforge.BitterMemoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.ActivityBuilder;
import net.tslat.smartbrainlib.api.core.behaviour.base.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.LeapAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class SCP939 extends BitterMob<SCP939> {
    private static final int LISTENING_RADIUS = 16;
    private final DynamicGameEventListener<BitterVibrationListener<SCP939>> gameListener;
    private BitterAngerManagement angerManagement;
    private LureSystem lureSystem;
    private static final EntityDataAccessor<SCP939State> DATA_STATE = SynchedEntityData.defineId(SCP939.class,
            BitterDataSerializers.SCP_939_STATE.get());
    public final AnimationState listeningAnimationState = new AnimationState();
    public final AnimationState crawlAnimationState = new AnimationState();

    public SCP939(EntityType entityType, Level level) {
        super(entityType, level);
        PositionSource positionSource = new EntityPositionSource(this, getEyeHeight());
        BitterVibrationListener<SCP939> listener = new BitterVibrationListener<>(
                positionSource,
                LISTENING_RADIUS,
                SCP939::onVibration,
                SCP939::isFocusedListening,
                this
        );
        this.gameListener = new DynamicGameEventListener<>(listener);
        this.angerManagement = new BitterAngerManagement();
        this.lureSystem = new LureSystem();
    }

    @Override
    protected Character initializeCharacter() {
        return new Character(uuid, "SCP-939-" + getRandom().nextInt(1, 24));
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5)
                .add(Attributes.ATTACK_DAMAGE, 30.0)
                .add(Attributes.FOLLOW_RANGE, 128.0);
    }

    @Override
    protected Map<Need, NeedInstance> initializeNeeds() {
        return Map.of();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SmoothGroundNavigation(this, level);
    }

    @Override
    public List<? extends ExtendedSensor<?>> getSensors(SCP939 owner) {
        return List.of(
                new VisionConeSensor<>(this::getVisionConeAngle)
        );
    }

    @Override
    public Activity[] getActivityActivationPriority() {
        return new Activity[]{Activity.FIGHT, BitterActivity.LISTEN.get(), BitterActivity.HUNT.get(), Activity.IDLE};
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public ActivityBuilder<SCP939> getActivityGroupFor(Activity activity) {
        ActivityBuilder<SCP939> builder = ActivityBuilder.create(activity);
        if (activity.equals(BitterActivity.LISTEN.get())) {
            builder.behaviours((List) getListenBehaviours(this))
                    .behaviourPriorityBase(50)
                    .requireAndClearMemoriesOnUse(MemoryModuleType.DISTURBANCE_LOCATION);
        } else if (activity.equals(BitterActivity.HUNT.get())) {
            builder.behaviours((List) getHuntBehaviours(this))
                    .behaviourPriorityBase(50)
                    .addMemoryRequirement(BitterMemoryTypes.HUNTING.get(), MemoryStatus.VALUE_PRESENT);
        }

        return builder;
    }

    @Override
    public List<? extends BehaviorControl<?>> getAlwaysRunningBehaviours(SCP939 owner) {
        return List.of(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>(),
                new TargetOrRetaliate<>()
                        .canRetaliateAgainst((entity) -> !(entity instanceof SCP939))
        );
    }

    @Override
    public List<? extends BehaviorControl<?>> getIdleBehaviours(SCP939 owner) {
        return List.of(
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>()
                                .setRadius(getRandom().nextInt(1, 10)),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                )
        );
    }

    @Override
    public List<? extends BehaviorControl<?>> getFightingBehaviours(SCP939 owner) {
        return List.of(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>()
                        .speedModifier(1.4f),
                new OneRandomBehaviour<>(
                        new AnimatableMeleeAttack<>(10),
                        new LeapAtTarget<>(10)
                                .jumpStrength(1.2f)
                                .canAttack((entity, target) -> entity.distanceToSqr(target) > 3.0),
                        new YankItem<>(0),
                        new Pull<>(0)
                                .cooldownFor(40, 60)
                )
        );
    }

    public List<? extends BehaviorControl<?>> getHuntBehaviours(SCP939 ignoredOwner) {
        return List.of(
                new SweepArea()
                        .whenStopping(entity -> setDisturbanceLocationIfNone(entity, entity.blockPosition(), 100))
                        .runFor(300, 600),
                new AttemptLure()
        );
    }

    public List<? extends BehaviorControl<?>> getListenBehaviours(SCP939 ignoredOwner) {
        return List.of(
                new SetWalkToDisturbanceLocation<>(),
                new ReleaseGas(10),
                new Listen().noTimeout()
        );
    }

    @Override
    protected float getKnockback(Entity target, DamageSource damageSource) {
        float knockback = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (level() instanceof ServerLevel level) {
            knockback = EnchantmentHelper.modifyKnockback(level, getWeaponItem(), target, damageSource, knockback);
        }
        knockback *= 0.5f;

        float speed = (float) getKnownMovement().horizontalDistance();
        float t = Mth.clamp(speed / 0.03f, 0.0f, 1.0f);
        return knockback + Mth.lerp(t, 0.25f, 1.5f);
    }

    @Override
    public void causeExtraKnockback(Entity target, float knockback, Vec3 oldMovement) {
        super.causeExtraKnockback(target, knockback, oldMovement);
        if (knockback > 1.0f && target instanceof LivingEntity livingTarget) {
            float strength = knockback * 2.5f;
            Vec3 motion = new Vec3(Mth.sin(getYRot() * (float) (Math.PI / 180.0)), 0, -Mth.cos(getYRot() * (float) (Math.PI / 180.0))).scale(strength);
            RagdollUtil.ragdoll(livingTarget, motion);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_STATE, SCP939State.IDLE);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) return;

        boolean moving = walkAnimation.speed() > 0.01F;
        boolean listening = !moving && getState() == SCP939State.LISTENING;

        if (moving) crawlAnimationState.startIfStopped(tickCount); else crawlAnimationState.stop();
        if (listening) listeningAnimationState.startIfStopped(tickCount); else listeningAnimationState.stop();
    }

    public SCP939State getState() {
        return entityData.get(DATA_STATE);
    }

    public void setState(SCP939State state) {
        entityData.set(DATA_STATE, state);
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> action) {
        if (level() instanceof ServerLevel level) {
            action.accept(gameListener, level);
        }
    }

    public static void onVibration(SCP939 entity, ServerLevel level, BlockPos sourcePos, Holder<GameEvent> event, @Nullable Entity sourceEntity, double distance) {
        BitterAngerManagement angerManagement = entity.getAngerManagement();
        if (sourceEntity != null && !sourceEntity.isInvisible()) {
            if (sourceEntity instanceof SCP939) return;
            angerManagement.increaseAnger(sourceEntity, 10);
        }

        System.out.println("Received vibration! Current highest anger: " + angerManagement.getHighestAnger(level));

        if (angerManagement.getHighestAnger(level) < 80) {
//            BehaviorUtils.setWalkAndLookTargetMemories(entity, sourcePos, 1.0f, 2);
            setDisturbanceLocation(entity, sourcePos, 550);
            BrainUtil.setMemory(entity, BitterMemoryTypes.HUNTING.get(), true);
        }
    }

    private boolean isFocusedListening() {
        return getBrain().isActive(BitterActivity.LISTEN.get()) && getNavigation().isDone();
    }

    @Override
    public boolean dampensVibrations() {
        return true;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        if (tickCount % 20 == 0) {
            angerManagement.tick(level);
        }
    }

    public BitterAngerManagement getAngerManagement() {
        return angerManagement;
    }

    public boolean shouldAmnesticize() {
        if (level() instanceof ServerLevel level) {
            return angerManagement.getHighestAnger(level) > 50;
        }
        return false;
    }

    public LureSystem getLureSystem() {
        return lureSystem;
    }

    public double getVisionConeAngle() {
        return 90;
    }

    public static void setDisturbanceLocationIfNone(PathfinderMob entity, BlockPos pos, int time) {
        if (!entity.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION)) {
            setDisturbanceLocation(entity, pos, time);
        }
    }

    public static void setDisturbanceLocation(PathfinderMob entity, BlockPos pos, int time) {
        BrainUtil.setForgettableMemory(entity, MemoryModuleType.DISTURBANCE_LOCATION, pos, time);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("anger", BitterAngerManagement.CODEC, angerManagement);
        output.store("lures", LureSystem.CODEC, lureSystem);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        angerManagement = input.read("anger", BitterAngerManagement.CODEC).orElse(new BitterAngerManagement());
        lureSystem = input.read("lures", LureSystem.CODEC).orElse(new LureSystem());
    }
}