package com.site21.bittermelon.mixin;

import com.site21.bittermelon.common.systems.component.heavy.Heavy;
import com.site21.bittermelon.common.systems.component.heavy.JumpCooldown;
import com.site21.bittermelon.common.systems.component.heavy.WeightSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.site21.bittermelon.init.neoforge.BitterDataComponents.HEAVY;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements JumpCooldown {
    @Unique
    private int bittermelon$jumpReadyAt;
    @Unique
    private int bittermelon$jumpCooldownDuration;

    @Override
    public int bittermelon$getJumpCooldown() {
        return Math.max(0, bittermelon$jumpReadyAt - ((LivingEntity) (Object) this).tickCount);
    }

    @Override
    public int bittermelon$getJumpCooldownDuration() {
        return bittermelon$jumpCooldownDuration;
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void heavyJumpCooldown(CallbackInfo ci) {
        if (!((Object) this instanceof Player player) || player.isCreative() || player.isSpectator()) return;

        ItemStack held = player.getMainHandItem();
        if (!held.has(HEAVY)) return;

        if (player.tickCount < bittermelon$jumpReadyAt) {
            ci.cancel();
            return;
        }

        Heavy heavy = held.get(HEAVY);
        double penalty = heavy.basePenalty() + WeightSource.getPenalty(heavy.source(), held);
        bittermelon$jumpCooldownDuration = 20 + (int) (penalty * 40);
        bittermelon$jumpReadyAt = player.tickCount + bittermelon$jumpCooldownDuration;
    }
}
