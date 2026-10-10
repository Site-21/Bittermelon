package com.site21.bittermelon.common.content.items.scps;

import com.site21.bittermelon.common.content.items.substance.FluidContainerItem;
import com.site21.bittermelon.common.systems.fluid.substance.MixtureOwner;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.site21.bittermelon.init.custom.Substances.WATER;
import static com.site21.bittermelon.init.neoforge.BitterDataComponents.CAN_SPILL;

public class SCP109Item extends FluidContainerItem {
    public SCP109Item(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        updateSubstance(stack, new SubstanceStack(WATER.get(), getMaxTransferRate(stack)));
        return super.use(level, player, usedHand);
    }

    @Override
    public boolean onEntityItemUpdate(@NotNull ItemStack stack, @NotNull ItemEntity entity) {
        Level level = entity.level();
        if (level.isClientSide()) return false;
        if (level.getGameTime() % 100 == 0) {
            if (stack.getOrDefault(CAN_SPILL, true)) {
                spill(stack, level, entity.blockPosition(), (int) (getMaxTransferRate(stack) * entity.getRandom().nextFloat()));
                entity.playSound(SoundEvents.WATER_AMBIENT, 0.3f, 2);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void transferSubstancesToBlock(BlockPos pos, Level level, ItemStack stack, int volume) {
        if (level.getBlockEntity(pos) instanceof MixtureOwner container) {
            SubstanceStack substanceStack = WATER.get().toStack();
            substanceStack.setVolume(volume);
            container.getMixture().updateSubstance(substanceStack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Contents: ∞/∞"));
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level level) {
        return Integer.MAX_VALUE;
    }
}
