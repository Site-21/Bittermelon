package com.site21.bittermelon.common.content.entities;

import com.site21.bittermelon.common.content.items.scps.scp2398.SCP2398Item;
import com.site21.bittermelon.common.systems.ragdoll.RagdollEntity;
import com.site21.bittermelon.common.systems.ragdoll.RagdollUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static com.site21.bittermelon.init.neoforge.BitterDataComponents.*;
import static com.site21.bittermelon.init.neoforge.BitterEntities.THROWN_ITEM_PROJECTILE;
import static com.site21.bittermelon.init.neoforge.BitterItems.SCP_2398;
import static net.minecraft.world.item.Items.SNOWBALL;

public class ThrownItemProjectile extends ThrowableItemProjectile {
    private static final double BASE_GRAVITY = 0.03;
    private static final double MAX_VELOCITY = 2;
    private static final double BREAK_GLASS_VELOCITY = 1.5;
    private static final double BREAK_DOOR_VELOCITY = 2.5;

    private int bounceCount = 0;
    private final int maxBounces;
    private final double energyLossOnBounce;

    public ThrownItemProjectile(EntityType<? extends ThrownItemProjectile> entityType, Level level) {
        super(entityType, level);
        energyLossOnBounce = 0.7;
        maxBounces = 50;
    }

    public ThrownItemProjectile(double x, double y, double z, Level level, ItemStack item) {
        super(THROWN_ITEM_PROJECTILE.get(), x, y, z, level, item);
        energyLossOnBounce = item.getOrDefault(ENERGY_LOSS_ON_BOUNCE, 0.7f);
        maxBounces = item.getOrDefault(MAX_BOUNCES, 50);
    }

    public ThrownItemProjectile(LivingEntity owner, Level level, ItemStack item) {
        super(THROWN_ITEM_PROJECTILE.get(), owner, level, item);
        energyLossOnBounce = item.getOrDefault(ENERGY_LOSS_ON_BOUNCE, 0.7f);
        maxBounces = item.getOrDefault(MAX_BOUNCES, 50);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return SNOWBALL;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;

        Vec3 currentPos = position();
        Vec3 velocity = getDeltaMovement();
        Vec3 nextPos = currentPos.add(velocity);

        // Ray trace from current position to next position
        BlockHitResult hitResult = level().clip(
                new ClipContext(
                        currentPos,
                        nextPos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        this
                )
        );

        // If we would hit a block, trigger the collision early
        if (hitResult.getType() != HitResult.Type.MISS) {
            // Move to just before the collision point
            Vec3 hitPos = hitResult.getLocation();
            Vec3 direction = nextPos.subtract(currentPos).normalize();
            Vec3 safePos = hitPos.subtract(direction.scale(0.1)); // Back up slightly

            setPos(safePos);
            onHitBlock(hitResult);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        if (!(level() instanceof ServerLevel level)) return;

        boolean shouldBounceBack = handleBlockInteraction(result);

        // Bounce sound
        level().playSound(null, result.getBlockPos(), SoundEvents.STONE_FALL, SoundSource.PLAYERS, 2, 1);

        if (bounceCount < maxBounces && shouldBounceBack) {
            Vec3 newVelocity = getNewVelocity(result);

            // Ensure above minimum velocity threshold
            if (newVelocity.length() > 0.25) {
                setDeltaMovement(newVelocity);
                bounceCount++;
                return; // Continue bouncing
            }
        }

        spawnAtLocation(level, getItem());

        level.broadcastEntityEvent(this, (byte) 3);
        discard();
    }

    private @NotNull Vec3 getNewVelocity(@NotNull BlockHitResult result) {
        // Reflection Formula {w = v - 2 * (v∙n) * n}

        // v
        Vec3 velocity = new Vec3(this.getDeltaMovement().toVector3f());
        // n
        Vec3 normal = new Vec3(result.getDirection().getStepX(), result.getDirection().getStepY(), result.getDirection().getStepZ());
        // v∙n
        double dot = velocity.dot(normal);
        // w
        Vec3 newVelocity = new Vec3(
                velocity.x - 2 * dot * normal.x,
                velocity.y - 2 * dot * normal.y,
                velocity.z - 2 * dot * normal.z
        );

        // Energy loss
        newVelocity = newVelocity.scale(energyLossOnBounce);

        if (newVelocity.length() > MAX_VELOCITY) {
            newVelocity = newVelocity.normalize().scale(MAX_VELOCITY);
        }

        // Ensure minimum y velocity
        // Check if vertical bounce is too small and what face of the block we're hitting
        if (Math.abs(newVelocity.y) < 0.2 && result.getDirection().getStepY() != 0) {
            // For floor collision, Math.signum will make it bounce upwards and vice versa for ceiling collision.
            newVelocity = new Vec3(newVelocity.x, 0.2 * Math.signum(result.getDirection().getStepY()), newVelocity.z);
        }

        return newVelocity;
    }

    private boolean handleBlockInteraction(@NotNull BlockHitResult result) {
        // Returns if the projectile should bounce back
        BlockPos pos = result.getBlockPos();
        BlockState state = level().getBlockState(pos);

        if ((state.is(Tags.Blocks.GLASS_BLOCKS) || state.is(Tags.Blocks.GLASS_PANES)) && getDeltaMovement().length() >= BREAK_GLASS_VELOCITY) {
            level().destroyBlock(pos, false, this);
            return true;
        } else if (state.getBlock() instanceof DoorBlock && getDeltaMovement().length() >= BREAK_DOOR_VELOCITY) {
            level().destroyBlock(pos, true, this);
            return true;
        } else if (state.getBlock() instanceof BellBlock block) {
            block.attemptToRing(level(), pos, result.getDirection());
        } else if (state.getBlock() instanceof ButtonBlock block) {
            block.press(state, level(), pos, null);
        }

        return true;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();

        if (level() instanceof ServerLevel level) {
            // v
            Vec3 velocity = new Vec3(this.getDeltaMovement().toVector3f());
            // n
            Vec3 normal = new Vec3(getDirection().getStepX(), getDirection().getStepY(), getDirection().getStepZ());
            // v∙n
            double dot = velocity.dot(normal);
            // w
            Vec3 newVelocity = new Vec3(
                    velocity.x - 2 * dot * normal.x,
                    velocity.y - 2 * dot * normal.y,
                    velocity.z - 2 * dot * normal.z
            );

            // Energy loss
            newVelocity.scale(energyLossOnBounce);

            // Ensure minimum y velocity
            // Check if vertical bounce is too small and what face of the block we're hitting
            if (Math.abs(newVelocity.y) < 0.2 && getMotionDirection().getStepY() != 0) {
                // For floor collision, Math.signum will make it bounce upwards and vice versa for ceiling collision.
                newVelocity = new Vec3(newVelocity.x, 0.2 * Math.signum(getMotionDirection().getStepY()), newVelocity.z);
            }

            setDeltaMovement(newVelocity);

            float dmg = 1;
            dmg *= getItem().getCount();
            dmg /= getItem().getMaxStackSize() / 4f;
            entity.hurtServer(level, damageSources().thrown(this, getOwner()), dmg);

            if (velocity.length() > 0.5) {
                Vec3 force = entity.position().subtract(position()).normalize().scale(20);
                if (entity instanceof LivingEntity living) {
                    RagdollUtil.ragdoll(living, force);
                } else if (entity instanceof RagdollEntity ragdoll) {
                    ragdoll.addMotion(force);
                }
            }
        }

        if (getItem().getItem() instanceof SCP2398Item) {
            level().broadcastEntityEvent(this, (byte) 3);
            discard();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return BASE_GRAVITY + getItem().getOrDefault(WEIGHT, 0.0f) / 100.0f;
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float amount) {
        if (source.is(DamageTypes.EXPLOSION) ||
                source.is(DamageTypes.PLAYER_EXPLOSION) ||
                source.is(DamageTypes.BAD_RESPAWN_POINT)) {
            return false;
        }

        if (source.getEntity() instanceof Player player) {
            Vec3 hitDirection = player.getLookAngle();

            double hitStrength = 0.8 + (player.isSprinting() ? 0.3 : 0) + (player.getMainHandItem().is(SCP_2398.get()) ? 1.0f : 0);

            setDeltaMovement(
                    hitDirection.x * hitStrength,
                    hitDirection.y * hitStrength,
                    hitDirection.z * hitStrength
            );

            level.playSound(null, blockPosition(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.PLAYERS,
                    0.8F, 0.8F + random.nextFloat() * 0.4F);
        }

        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (level().isClientSide()) return InteractionResult.SUCCESS;

        player.addItem(getItem());
        discard();
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isPickable() {
        return true;
    }
}

