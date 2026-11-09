package com.site21.bittermelon.common.content.entities.fluidprojectile;

import com.site21.bittermelon.common.events.CommonEvents;
import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.STAINS;
import static com.site21.bittermelon.init.neoforge.BitterEntities.FLUID_PROJECTILE;
import static com.site21.bittermelon.util.SubstanceUtil.spill;
import static net.neoforged.neoforge.event.EventHooks.onProjectileImpact;

public class FluidProjectile extends Projectile {
    private static final float MOMENTUM_LOSS = 0.99f;
    private SubstanceMixture mixture;

    public FluidProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public FluidProjectile(Level level, SubstanceMixture mixture) {
        super(FLUID_PROJECTILE.get(), level);
        this.mixture = mixture;
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 movement = getDeltaMovement();

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS && !onProjectileImpact(this, hitResult))
            hitTargetOrDeflectSelf(hitResult);

        double dx = getX() + movement.x;
        double dy = getY() + movement.y;
        double dz = getZ() + movement.z;

        updateRotation();
        setDeltaMovement(movement.scale(MOMENTUM_LOSS));
        applyGravity();
        setPos(dx, dy, dz);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        Vec3 movement = packet.getMovement();

        for (int i = 0; i < 7; i++) {
            double k = 0.4 + 0.1 * i;
            level().addParticle(ParticleTypes.SPIT, getX(), getY(), getZ(), movement.x * k, movement.y, movement.z * k);
        }

        setDeltaMovement(movement);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level().isClientSide()) return;

        spill(level(), result.getBlockPos().above(), mixture);
        playSplashSound();
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide()) return;

        if (result.getEntity() instanceof LivingEntity target) {
            SubstanceMixture stains = target.getData(STAINS);
            List<SubstanceStack> substances = mixture.spreadSubstancesByPercentage(0.5f);

            stains.transferSubstances(substances);
            spill(level(), target.blockPosition(), substances, mixture.getTemperature());

            playSplashSound();
            discard();
        }
    }

    private void playSplashSound() {
        level().playSound(null, blockPosition(), SoundEvents.BUCKET_EMPTY_FISH, getSoundSource(),
                0.1f, 0.8f + level().getRandom().nextFloat() * 0.4f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("mixture", SubstanceMixture.CODEC, mixture);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        mixture = input.read("mixture", SubstanceMixture.CODEC).orElse(new SubstanceMixture());
    }

    @Override
    protected double getDefaultGravity() {
        return 0.06f;
    }
}
