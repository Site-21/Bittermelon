package com.site21.bittermelon.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin {

//    @Redirect(
//            method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z")
//    )
//    private boolean skipSerializeCheckWhenForced(EntityType<?> type, Entity vehicle, boolean force) {
//        return force || type.canSerialize();
//    }

    @Shadow
    public abstract Level level();
}
