package com.site21.bittermelon.common.systems.stumble;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.client.render.screenshake.StartScreenshake;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import static com.site21.bittermelon.init.neoforge.BitterSounds.FALL;

public class FallenEffect extends MobEffect {
    public FallenEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
        addAttributeModifier(
                Attributes.JUMP_STRENGTH,
                Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "effect.fallen"),
                -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectStarted(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new StartScreenshake(70, 10));
        }
        entity.level().playSound(null, entity.getOnPos(), FALL.value(), SoundSource.PLAYERS);
    }

    @Override
    public boolean applyEffectTick(@NotNull ServerLevel level, @NotNull LivingEntity entity, int amplifier) {
//        if (!isStunned(entity) && !(entity instanceof Player)) {
//            if (entity.getPose() != Pose.STANDING) {
//                entity.setPose(Pose.STANDING);
//            }
//            entity.removeEffect(FALLEN);
//        }

        return true;
    }
}
