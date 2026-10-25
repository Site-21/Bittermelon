package com.site21.bittermelon.mixin;

import com.site21.bittermelon.common.content.entities.mimicplayer.Mimic;
import com.site21.bittermelon.common.systems.component.heavy.client.JumpCooldownBarRenderer;
import com.site21.bittermelon.common.systems.stress.client.StressBarRenderer;
import com.site21.bittermelon.common.systems.stumble.client.RiseBarRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Mutable
    @Shadow
    @Final
    private Map<Gui.ContextualInfo, Supplier<ContextualBarRenderer>> contextualInfoBarRenderers;

    @Unique
    private static final Gui.ContextualInfo bittermelon$STRESS = Enum.valueOf(Gui.ContextualInfo.class, "STRESS");
    @Unique
    private static final Gui.ContextualInfo bittermelon$JUMP_COOLDOWN = Enum.valueOf(Gui.ContextualInfo.class, "JUMP_COOLDOWN");
    @Unique
    private static final Gui.ContextualInfo bittermelon$RISE = Enum.valueOf(Gui.ContextualInfo.class, "RISE");

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;contextualInfoBarRenderers:Ljava/util/Map;", shift = At.Shift.AFTER))
    private void onInit(Minecraft minecraft, CallbackInfo ci) {
        contextualInfoBarRenderers = new HashMap<>(contextualInfoBarRenderers);
        contextualInfoBarRenderers.put(bittermelon$STRESS, () -> new StressBarRenderer(minecraft));
        contextualInfoBarRenderers.put(bittermelon$JUMP_COOLDOWN, () -> new JumpCooldownBarRenderer(minecraft));
        contextualInfoBarRenderers.put(bittermelon$RISE, () -> new RiseBarRenderer(minecraft));
    }

    @Inject(method = "nextContextualInfoState", at = @At("HEAD"), cancellable = true)
    private void onNextContextualInfoState(CallbackInfoReturnable<Gui.ContextualInfo> cir) {
        if (minecraft.player == null) return;

        if (RiseBarRenderer.shouldShow(minecraft.player)) {
            cir.setReturnValue(bittermelon$RISE);
        } else if (JumpCooldownBarRenderer.shouldShow(minecraft.player)) {
            cir.setReturnValue(bittermelon$JUMP_COOLDOWN);
        } else if (StressBarRenderer.shouldShow(minecraft.player)) {
            cir.setReturnValue(bittermelon$STRESS);
        }
    }

    @Inject(
            method = "getCameraPlayer",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onGetCameraPlayer(CallbackInfoReturnable<Player> cir) {
        if (minecraft.getCameraEntity() instanceof Mimic mimic && mimic.getPlayer() != null) {
            cir.setReturnValue(mimic.getPlayer());
        }
    }
}
