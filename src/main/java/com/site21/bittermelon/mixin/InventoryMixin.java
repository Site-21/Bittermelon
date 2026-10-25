package com.site21.bittermelon.mixin;

import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.site21.bittermelon.init.neoforge.BitterDataComponents.HEAVY;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow
    public abstract int getSelectedSlot();

    @Shadow
    public abstract ItemStack getSelectedItem();

    @Shadow
    @Final
    public Player player;

    @Shadow
    public abstract ItemStack getItem(int slot);

    @Shadow
    public abstract ItemStack removeItem(int slot, int count);

    @Shadow
    public abstract int getContainerSize();

    @Shadow
    public abstract boolean add(int slot, ItemStack itemStack);

    @Shadow
    @Final
    private EntityEquipment equipment;

    @Inject(method = "setSelectedSlot", at = @At("HEAD"))
    private void dropHeavyOnNewSelected(int selected, CallbackInfo ci) {
        if (player.isCreative()) return;

        int currentSelected = getSelectedSlot();
        ItemStack currentItem = getSelectedItem();
        if (currentItem == null) return;

        if (currentItem.has(HEAVY) && selected != currentSelected) {
            player.drop(currentItem.copy(), true);
            removeItem(currentSelected, currentItem.getCount());
        }
    }

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void restrictHeavy(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (player.isCreative() || !itemStack.has(HEAVY)) return;

        int selected = getSelectedSlot();
        if (!getItem(selected).isEmpty() || bittermelon$hasOtherHeavy(selected) || !equipment.get(EquipmentSlot.OFFHAND).isEmpty()) {
            cir.setReturnValue(false);
        } else if (slot != selected) {
            cir.setReturnValue(add(selected, itemStack));
        }
    }

    @Unique
    private boolean bittermelon$hasOtherHeavy(int except) {
        for (int i = 0; i < getContainerSize(); i++) {
            if (i != except && getItem(i).has(HEAVY)) return true;
        }
        return false;
    }
}
