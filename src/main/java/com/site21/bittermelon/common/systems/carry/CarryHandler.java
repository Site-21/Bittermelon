package com.site21.bittermelon.common.systems.carry;

import com.site21.bittermelon.init.neoforge.BitterAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CarryHandler {

    public static void tickCarrying(@NotNull LivingEntity entity) {
        if (entity.isVehicle() && !(entity instanceof PlayerRideable)) {
            float totalVolume = 0;

            for (Entity passenger : entity.getPassengers()) {
                EntityDimensions dimensions = passenger.getDimensions(passenger.getPose());
                totalVolume += dimensions.width() * dimensions.height();
            }

            if (totalVolume < 1.0f) return;

            int amplifier = Math.min((int) (totalVolume / 2), 6);

            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 2, amplifier, true,
                    false, true));
        }
    }

    /**
     * Allows the carrier to pick up the target entity as a passenger.
     *
     * @param carrier The entity that will carry the target.
     * @param target  The entity to be picked up.
     * @return True if the target was successfully picked up, false otherwise.
     */
    public static boolean pickUpEntity(LivingEntity carrier, @NotNull LivingEntity target) {
        if (!target.isAlive()) return false;

        if (!target.startRiding(carrier, true, true)) return false;
        carrier.setData(BitterAttachmentTypes.CARRIED_PASSENGER, target.getUUID());
        return true;
    }

    /**
     * Handles the player attempting to pick up an entity.
     *
     * @param player The player attempting to pick up the entity.
     * @param target The entity to be picked up.
     * @return True if the entity was successfully picked up, false otherwise.
     */
    public static boolean playerPickUpEntity(@NotNull Player player, @NotNull LivingEntity target) {
        if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) return false;

        if (player.isShiftKeyDown()) {
            return pickUpEntity(player, target);
        }

        return false;
    }

    /**
     * Places the carriedId entity at the specified block position or throws it if out of range.
     *
     * @param player    The player placing the entity.
     * @param pos       The block position to place the entity at.
     * @param hitResult The block hit result for precise placement.
     * @return True if the entity was successfully placed, false otherwise.
     */
    public static boolean placeEntity(Player player, BlockPos pos, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown()) return false;

        Entity carriedEntity = getCarried(player);
        if (carriedEntity == null) return false;

        carriedEntity.stopRiding();

        if (player.isWithinBlockInteractionRange(pos, -player.blockInteractionRange() / 2)) {
            carriedEntity.setPos(hitResult.getLocation());
            player.removeData(BitterAttachmentTypes.CARRIED_PASSENGER);
        } else if (player.level().isClientSide()) {
            ClientPacketDistributor.sendToServer(new ThrowCarriedEntity(player.getUUID(), carriedEntity.getUUID()));
        }

        return true;
    }

    /**
     * Retrieves the entity currently being carriedId by the carrier.
     *
     * @param carrier The entity carrying another entity.
     * @return The carriedId entity, or null if none is being carriedId.
     */
    public static @Nullable Entity getCarried(LivingEntity carrier) {
        UUID carriedUUID = carrier.getData(BitterAttachmentTypes.CARRIED_PASSENGER);
        for (Entity passenger : carrier.getPassengers()) {
            if (passenger.getUUID().equals(carriedUUID)) {
                return passenger;
            }
        }

        carrier.removeData(BitterAttachmentTypes.CARRIED_PASSENGER);
        return null;
    }
}
