package com.site21.bittermelon.common.content.items.balloonhammer;

import com.site21.bittermelon.init.neoforge.BitterParticles;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.core.particles.ParticleOptions;

import java.util.List;
import java.util.function.Supplier;

public class BalloonHammer extends Item {
    private interface Effect {
        void play(ServerLevel level, LivingEntity target, LivingEntity attacker);
    }

    private static final List<Effect> EFFECTS = List.of(
            particle(BitterParticles.BOOM),
            particle(BitterParticles.KAPOW),
            BalloonHammer::firework
    );

    public BalloonHammer(Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        removeEntity(target, attacker);
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() == InteractionHand.MAIN_HAND
                && event.getEntity().getMainHandItem().getItem() instanceof BalloonHammer
                && event.getTarget() instanceof LivingEntity target
                && target.isAlive()
                && !event.getLevel().isClientSide()) {
            event.setCanceled(true);
            removeEntity(target, event.getEntity());
        }
    }

    private static void removeEntity(LivingEntity target, LivingEntity attacker) {
        if (target.level() instanceof ServerLevel level) {
            EFFECTS.get(level.getRandom().nextInt(EFFECTS.size())).play(level, target, attacker);
            target.discard();
        }
    }

    private static Effect particle(Supplier<? extends ParticleOptions> type) {
        return (level, target, attacker) ->
                level.sendParticles(type.get(), target.getX(), target.getY() + 2.0, target.getZ(), 1, 0, 0, 0, 0);
    }

    private static void firework(ServerLevel level, LivingEntity target, LivingEntity attacker) {
        Vec3 pos = attacker.getEyePosition().add(attacker.getLookAngle().scale(2.5));

        FireworkExplosion explosion = new FireworkExplosion(
                FireworkExplosion.Shape.LARGE_BALL,
                IntList.of(0xFF69B4, 0x1E90FF),
                IntList.of(0xFFB6C1, 0x87CEFA),
                true,
                true
        );
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        stack.set(DataComponents.FIREWORKS, new Fireworks(1, List.of(explosion)));

        FireworkRocketEntity rocket = new FireworkRocketEntity(level, pos.x, pos.y, pos.z, stack);
        level.addFreshEntity(rocket);
        level.broadcastEntityEvent(rocket, (byte) 17);
        rocket.discard();
    }
}