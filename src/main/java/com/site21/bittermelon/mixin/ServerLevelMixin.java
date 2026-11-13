package com.site21.bittermelon.mixin;

import com.site21.bittermelon.common.systems.atmosphere.AtmosHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "updatePOIOnBlockStateChange", at = @At("HEAD"))
    private void bittermelon$onBlockStateChange(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        AtmosHandler.onBlockStateChange((ServerLevel) (Object) this, pos, oldState, newState);
    }
}
