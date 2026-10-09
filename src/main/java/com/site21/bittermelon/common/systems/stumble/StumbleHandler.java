package com.site21.bittermelon.common.systems.stumble;

import com.site21.bittermelon.common.systems.character.Character;
import com.site21.bittermelon.common.systems.character.CharacterManager;
import com.site21.bittermelon.common.systems.medical.legacy.medicalstats.MedicalStats;
import com.site21.bittermelon.common.systems.ragdoll.RagdollUtil;
import com.site21.bittermelon.init.neoforge.BitterEntityTags;
import com.site21.bittermelon.init.neoforge.BitterMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.MEDICAL_STATS;
import static com.site21.bittermelon.init.neoforge.BitterMobEffects.FALLEN;
import static com.site21.bittermelon.init.neoforge.BitterMobEffects.STUN;
import static com.site21.bittermelon.util.LocalMessageUtil.sendLocalMessage;

public class StumbleHandler {
    private static final float DROP_CHANCE = 0.5f;

    /**
     * Makes a living entity stumble with customizable duration and push direction.
     * Entities that are sleeping or swimming are ignored. Movement stats from character
     * data alter stumble duration, with less movement ability extending stumble duration.
     *
     * @param entity        the living entity to make stumble
     * @param length        base stumble duration in ticks
     * @param pushDirection direction to push the entity during stumble
     */
    public static void stumble(LivingEntity entity, int length, Vec3 pushDirection) {
        if (entity.level().isClientSide()) return;
        if (entity.hasEffect(FALLEN)) return;

        MedicalStats medicalStats = entity.getData(MEDICAL_STATS);
        int movement = (int) medicalStats.getMovement();
        if (movement > 0) {
            length /= movement;
        } else {
            length = -1;
        }

        addStunEffect(entity, length);

        if (entity.is(BitterEntityTags.RAGDOLLABLE)) {
            RagdollUtil.ragdoll(entity, pushDirection);

            if (entity instanceof Player player) {
                dropItem(player);
            }
        } else {
            entity.addEffect(new MobEffectInstance(FALLEN, MobEffectInstance.INFINITE_DURATION, 0, false, false));
            motion(entity, pushDirection);
        }

        announceFall(entity);
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

    private static void motion(LivingEntity entity, Vec3 pushDirection) {
        pushDirection.multiply(1, 0, 1);
        double multiplier = 1.2 * entity.getEyeHeight();
        entity.addDeltaMovement(pushDirection.multiply(multiplier, 0, multiplier));
        entity.hurtMarked = true;
        entity.setPose(Pose.SLEEPING);
    }

    private static void addStunEffect(LivingEntity entity, int duration) {
        MobEffectInstance stumbleEffect = new MobEffectInstance(
                BitterMobEffects.STUN,
                duration,
                0,
                false,
                false
        );

        entity.addEffect(stumbleEffect);
    }

    private static void dropItem(Player player) {
        if (player.getRandom().nextFloat() < DROP_CHANCE) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty()) {
                player.drop(heldItem.copy(), true);
                heldItem.setCount(0);
            }
        }
    }

    private static void announceFall(LivingEntity entity) {
        Character character = CharacterManager.get(entity.level()).getActiveCharacter(entity);
        if (character != null) {
            Component component = Component.literal(character.getName() + " falls to the ground.").withColor(character.getEmoteColor());
            sendLocalMessage(entity, 10, component);
        }
    }

    public static void attemptToRise(UUID uuid, ServerLevel level) {
        Player player = level.getPlayerByUUID(uuid);
        if (player == null) return;

        if (!isStunned(player) && player.getVehicle() != null) {
            player.getVehicle().discard();
        }
    }

    public static boolean isStunned(LivingEntity entity) {
        return entity.hasEffect(STUN);
    }

    public static boolean isStumbled(LivingEntity entity) {
        return entity.is(BitterEntityTags.RAGDOLLABLE) ?
                RagdollUtil.isRagdolled(entity) :
                entity.hasEffect(FALLEN);
    }

    public static boolean canMove(LivingEntity entity) {
        return !entity.hasEffect(STUN);
    }

    public static void clearStunned(LivingEntity entity) {
        entity.removeEffect(STUN);
    }
}
