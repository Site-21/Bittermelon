package com.site21.bittermelon.common.events;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.items.scps.scp2398.SCP2398Item;
import com.site21.bittermelon.common.systems.blockdamage.BlockDamageUtil;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlockEntity;
import com.site21.bittermelon.common.systems.ragdoll.RagdollEntity;
import com.site21.bittermelon.common.systems.ragdoll.RagdollUtil;
import com.site21.bittermelon.common.systems.syncsound.SyncSoundEvent;
import com.site21.bittermelon.common.systems.syncsound.SyncSoundType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Bittermelon.MOD_ID)
public class ExplosionHandler {
    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.@NotNull Detonate event) {
        Explosion explosion = event.getExplosion();
        List<BlockPos> affectedBlocks = new ArrayList<>(event.getAffectedBlocks());
        event.getAffectedBlocks().clear();
        Level level = event.getLevel();

        for (BlockPos pos : affectedBlocks) {
            if (!level.getFluidState(pos).isEmpty()) {
                affectFluid(level, pos, explosion.center(), explosion.radius());
            }

            if (level.getBlockState(pos).isAir()) continue;

            int breakProgress = calculateBreakProgress(explosion.center(), pos, explosion.radius());
            BlockDamageUtil.addDamage(level, pos, breakProgress);
        }

        List<Entity> affectedEntities = event.getAffectedEntities();
        affectedEntities.removeIf(entity -> {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getItem();
                return stack.getItem() instanceof SCP2398Item;
            }
            return false;
        });

        for (Entity entity : affectedEntities) {
            Vec3 force = entity.position().subtract(explosion.center()).normalize().scale(explosion.radius() * 5);
            if (entity instanceof LivingEntity living) {
                RagdollUtil.ragdoll(living, force);
            } else if (entity instanceof RagdollEntity ragdoll) {
                ragdoll.addMotion(force);
            }
        }

        if (!event.getLevel().isClientSide()) {
            NeoForge.EVENT_BUS.post(new SyncSoundEvent(level,
                    new BlockPos(
                            (int) explosion.center().x,
                            (int) explosion.center().y,
                            (int) explosion.center().z),
                    SyncSoundType.AMBIENT,
                    Component.literal("(explosion)")
                            .withColor(0xFF808080)
                            .withStyle(ChatFormatting.ITALIC),
                    (int) explosion.radius() * 4,
                    SoundEvents.GENERIC_EXPLODE.value()));
        }
    }

    private static int calculateBreakProgress(@NotNull Vec3 center, BlockPos pos, float radius) {
        double distance = Math.sqrt(center.distanceToSqr(Vec3.atCenterOf(pos)));
        double breakProgress = Math.max(0, 1.0 - (distance / radius));

        return (int) Math.min(100, breakProgress * 100);
    }

    private static void affectFluid(Level level, BlockPos pos, Vec3 center, float radius) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        // TODO: Add fluid pushing away from explosion center

        // TODO: Better looking particles
        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE) {
            serverLevel.sendParticles(
                    new DustParticleOptions(fluidBE.getColor(), 5.0f),
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    10,
                    0.5, 1.0, 0.5,
                    2
            );
        }
    }
}
