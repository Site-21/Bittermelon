package com.site21.bittermelon.common.systems.stumble;

import com.site21.bittermelon.common.systems.character.Character;
import com.site21.bittermelon.common.systems.character.CharacterManager;
import com.site21.bittermelon.common.systems.medical.legacy.medicalstats.MedicalStats;
import com.site21.bittermelon.common.systems.ragdoll.RagdollUtil;
import com.site21.bittermelon.init.neoforge.BitterMobEffects;
import com.site21.bittermelon.networking.client.SetForcedPose;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.MEDICAL_STATS;
import static com.site21.bittermelon.init.neoforge.BitterMobEffects.FALLEN;
import static com.site21.bittermelon.init.neoforge.BitterMobEffects.STUN;
import static com.site21.bittermelon.util.LocalMessageUtil.sendLocalMessage;

public class StumbleHandler {

    /**
     * Makes a living entity stumble with customizable duration and push direction.
     * Entities that are sleeping or swimming are ignored. Movement stats from character
     * data alter stumble duration, with less movement ability extending stumble duration.
     *
     * @param entity        the living entity to make stumble
     * @param length        base stumble duration in ticks
     * @param pushDirection direction to push the entity during stumble
     */
    public static void stumble(@NotNull LivingEntity entity, int length, Vec3 pushDirection) {
        if (entity.hasEffect(FALLEN)) return;
        if (entity.level().isClientSide()) return;

        MedicalStats medicalStats = entity.getData(MEDICAL_STATS);
        int movement = (int) medicalStats.getMovement();
        if (movement > 0) {
            length /= movement;
        } else {
            length = -1;
        }

        addStunEffect(entity, length);
        announceFall(entity);

        RagdollUtil.ragdoll(entity, pushDirection);
    }

    /**
     * Makes a living entity stumble with default duration based on entity type.
     * Players stumble for 40 ticks, other entities for 100 ticks.
     * Push direction is set to the entity's current look direction.
     *
     * @param entity the living entity to make stumble
     */
    public static void stumble(LivingEntity entity) {
        stumble(entity, entity instanceof Player ? 40 : 100, entity.getLookAngle());
    }

    /**
     * Makes a living entity stumble with default duration and custom push direction.
     * Players stumble for 40 ticks, other entities for 100 ticks.
     *
     * @param entity        the living entity to make stumble
     * @param pushDirection direction to push the entity during stumble
     */
    public static void stumble(LivingEntity entity, Vec3 pushDirection) {
        stumble(entity, entity instanceof Player ? 40 : 100, pushDirection);
    }

    private static void motion(@NotNull LivingEntity entity, @NotNull Vec3 pushDirection) {
        Vec3 normalizedPush = pushDirection.normalize();
        pushDirection.multiply(1, 0, 1);
        Vec3 lookVector = entity.getLookAngle();

        double dotProduct = normalizedPush.dot(lookVector);
        double multiplier = 1.2d * entity.getEyeHeight();
        entity.addDeltaMovement(pushDirection.multiply(multiplier, 0, multiplier));
        entity.hurtMarked = true;

        if (entity instanceof ServerPlayer player) {
            Pose fallPose = dotProduct > 0 ? Pose.SWIMMING : Pose.SLEEPING;

            player.setForcedPose(fallPose);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SetForcedPose(player.getUUID(), fallPose));

            if (fallPose == Pose.SLEEPING) {
                dropItem(player, 0.7);
            } else {
                dropItem(player, 0.3);
            }
        } else {
            entity.setPose(Pose.SLEEPING);
        }
    }

    private static void addStunEffect(@NotNull LivingEntity entity, int duration) {
        MobEffectInstance stumbleEffect = new MobEffectInstance(
                BitterMobEffects.STUN,
                duration,
                0,
                false,
                false
        );

        entity.addEffect(stumbleEffect);
    }

    private static void dropItem(@NotNull Player player, double chance) {
        if (player.getRandom().nextFloat() < chance) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty()) {
                player.drop(heldItem.copy(), true);
                heldItem.setCount(0);
            }
        }
    }

    private static void announceFall(@NotNull LivingEntity entity) {
        Character character = CharacterManager.get(entity.level()).getActiveCharacter(entity);
        if (character != null) {
            Component component = Component.literal(character.getName() + " falls to the ground.").withColor(character.getEmoteColor());
            sendLocalMessage(entity, 10, component);
        }
    }

    public static void attemptToRise(UUID uuid, @NotNull ServerLevel level) {
        Player player = level.getPlayerByUUID(uuid);
        if (player == null) return;

        if (!isStunned(player)) {
            assert player.getVehicle() != null;
            player.getVehicle().discard();
        }
    }

    public static boolean isStunned(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return false;
        return livingEntity.hasEffect(STUN);
    }

    public static boolean isStumbled(@NotNull Entity entity) {
        return RagdollUtil.isRagdolled(entity);
    }

    public static boolean canMove(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return false;
        return !livingEntity.hasEffect(STUN);
    }

    public static void clearStunned(@NotNull LivingEntity entity) {
        entity.removeEffect(STUN);
    }
}
