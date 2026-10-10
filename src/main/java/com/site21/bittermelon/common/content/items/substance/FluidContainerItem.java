package com.site21.bittermelon.common.content.items.substance;

import com.site21.bittermelon.common.systems.component.SubstanceContents;
import com.site21.bittermelon.common.systems.fluid.substance.MixtureOwner;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlock;
import com.site21.bittermelon.common.systems.substance.SubstanceContainer;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.init.neoforge.BitterDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static com.site21.bittermelon.init.neoforge.BitterBlocks.SUBSTANCE_FLUID;
import static com.site21.bittermelon.init.neoforge.BitterDataComponents.*;
import static net.minecraft.world.level.block.Block.UPDATE_ALL_IMMEDIATE;
import static net.minecraft.world.level.block.Block.UPDATE_NONE;

public class FluidContainerItem extends SubstanceContainerItem {
    public static final int MIN_TRANSFER_RATE = 1;
    private static final int DRINK_SPEED = 32;

    public FluidContainerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        tooltipAdder.accept(Component.literal("Contents: " + getSubstanceData(stack).getTotalVolume() + "/" + getCapacity(stack)));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        // TODO: Figure out a way to make use and useOn not overlap

        ItemStack itemInHand = player.getItemInHand(usedHand);
        ItemStack offhandItem = player.getOffhandItem();

        if (isContainerEmpty(itemInHand)) {
            playEmptySound(level, player.getOnPos());
            return InteractionResult.PASS;
        }

        // If the player is holding a fluid container in the offhand, try to transfer substances into it
        if (usedHand == InteractionHand.MAIN_HAND
                && offhandItem.getItem() instanceof FluidContainerItem
                && itemInHand.getOrDefault(CAN_SPILL, true)) {

            if (!level.isClientSide() && player.isShiftKeyDown()) {
                transferSubstancesToContainer(itemInHand, offhandItem, level, player);
            }
        } else {
            // Otherwise, handle drinking or toggling spill state

            BlockHitResult blockHit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
            boolean isLookingAtBlock = blockHit.getType() == HitResult.Type.BLOCK;
            BlockState blockState = level.getBlockState(blockHit.getBlockPos());

            // If the item has a lid, toggle spill state when sneaking and not looking at a block
//            if (player.isShiftKeyDown() && hasLid && !isLookingAtBlock) {
            if (player.isShiftKeyDown() && !isLookingAtBlock) {
                boolean currentSpillState = itemInHand.getOrDefault(CAN_SPILL, false);
                itemInHand.set(CAN_SPILL, !currentSpillState);
                // TODO: Lid sound
            } else if (itemInHand.getOrDefault(CAN_SPILL, true) && !blockState.is(SUBSTANCE_FLUID)) {
                // If the item can spill and the player isn't looking at a fluid block, start drinking

                if (level.isClientSide()) {
                    playDrinkSound(level, player.getOnPos());
                }

                return ItemUtils.startUsingInstantly(level, player, usedHand);
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();

        if (!stack.getOrDefault(CAN_SPILL, true)) return InteractionResult.FAIL;

        if (!level.isClientSide() && player != null) {
            // If the container isn't empty and the player is sneaking, try to spill
            if (!isContainerEmpty(stack)) {
                if (player.isShiftKeyDown()) {
                    return handleSpillAction(clickedPos, level, player, stack);
                }
            }

            // If the container isn't full and the clicked block is a fluid block, try to fill from it
            if (level.getBlockState(clickedPos).getBlock() instanceof SubstanceFluidBlock) {
                transferSubstancesFromBlock(clickedPos, level, stack);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    protected InteractionResult handleSpillAction(@NotNull BlockPos clickedOnPos, @NotNull Level level, Player player, ItemStack stack) {
        BlockPos spillPos = clickedOnPos.above();
        BlockState existingState = level.getBlockState(spillPos);
        BlockState clickedOnState = level.getBlockState(clickedOnPos);

        // Try to spill above the clicked block first, then on the clicked block if that fails
       if (clickedOnState.canBeReplaced()) {
           if (!(clickedOnState.getBlock() instanceof SubstanceFluidBlock)) {
               level.setBlock(clickedOnPos, SUBSTANCE_FLUID.get().defaultBlockState(), UPDATE_ALL_IMMEDIATE);
           }
            transferSubstancesToBlock(clickedOnPos, level, stack, getLimitedTransferRate(stack));
        } else if (level.getBlockEntity(clickedOnPos) instanceof MixtureOwner) {
            transferSubstancesToBlock(clickedOnPos, level, stack, getLimitedTransferRate(stack));
        } else if (existingState.canBeReplaced()) {
            level.setBlock(spillPos, SUBSTANCE_FLUID.get().defaultBlockState(), UPDATE_ALL_IMMEDIATE);
            transferSubstancesToBlock(spillPos, level, stack, getLimitedTransferRate(stack));
        } else {
            player.sendSystemMessage(Component.literal("Can't spill here!").withStyle(ChatFormatting.RED));
            return InteractionResult.PASS;
        }

        return InteractionResult.SUCCESS;
    }

    protected void transferSubstancesToBlock(BlockPos pos, @NotNull Level level, ItemStack stack, int volume) {
        if (level.getBlockEntity(pos) instanceof MixtureOwner container) {
            transferSubstances(stack, getTotalVolume(stack), volume,
                    (substance, amount) -> container.getMixture().updateSubstance(substance));
            playEmptySound(level, pos);
        }
    }

    protected void transferSubstancesFromBlock(BlockPos pos, @NotNull Level level, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof MixtureOwner container) {
            int availableCapacity = getCapacity(stack) - getTotalVolume(stack);
            int transferRate = Math.min(getTransferRate(stack), availableCapacity);

            List<SubstanceStack> transferredSubstances = container.getMixture().transferSubstancesByVolume(transferRate);
            SubstanceContents.Mutable mutableData = getMutableSubstanceData(stack);

            for (SubstanceStack substance : transferredSubstances) {
                mutableData.updateSubstance(substance);
            }

            setSubstanceDataFromMutable(stack, mutableData);
            playFillSound(level, pos);
        }
    }

    protected void transferSubstancesToContainer(ItemStack sourceStack, ItemStack targetStack, Level level, Player player) {
        int totalSourceVolume = getTotalVolume(sourceStack);
        int transferRate = getLimitedTransferRate(sourceStack);
        int spaceAvailable = getCapacity(targetStack) - getTotalVolume(targetStack);

        if (totalSourceVolume <= 0 || spaceAvailable <= 0) return;

        int totalTransferVolume = Math.min(transferRate, Math.min(totalSourceVolume, spaceAvailable));

        transferSubstances(sourceStack, totalSourceVolume, totalTransferVolume,
                (substance, amount) -> updateTargetContainer(targetStack, substance, amount));

        if (totalTransferVolume > 0) {
            playFillSound(level, player.getOnPos());
        }
    }
    
    protected void transferSubstances(ItemStack sourceStack, int totalVolume, int transferRate, SubstanceTransferHandler handler) {
        if (totalVolume <= 0) return;

        SubstanceContents.Mutable mutableData = getMutableSubstanceData(sourceStack);
        Iterator<SubstanceStack> iterator = mutableData.substances.iterator();

        while (iterator.hasNext()) {
            SubstanceStack substance = iterator.next();
            if (substance == null) continue;

            int proportion = substance.getVolume() / totalVolume;
            int transferVolume = Math.min(transferRate * proportion, substance.getVolume());

            if (transferVolume > 0) {
                SubstanceStack transferredSubstance = substance.copy();
                transferredSubstance.setVolume(transferVolume);

                handler.handle(transferredSubstance, transferVolume);
                substance.modifyVolume(-transferVolume);

                if (substance.getVolume() <= 0.001f) {
                    iterator.remove();
                }
            }
        }

        setSubstanceDataFromMutable(sourceStack, mutableData);
    }

    private void updateTargetContainer(ItemStack targetStack, SubstanceStack substance, float amount) {
        SubstanceContents.Mutable targetData = getMutableSubstanceData(targetStack);
        targetData.substances.add(substance);
        setSubstanceDataFromMutable(targetStack, targetData);
    }

    public static int getTransferRate(@NotNull ItemStack stack) {
        return stack.getOrDefault(BitterDataComponents.TRANSFER_RATE.get(), MIN_TRANSFER_RATE);
    }

    public static int getMaxTransferRate(@NotNull ItemStack stack) {
        return stack.getOrDefault(MAX_TRANSFER_RATE, 100);
    }

    public int getLimitedTransferRate(@NotNull ItemStack stack) {
        return Math.min(stack.getOrDefault(BitterDataComponents.TRANSFER_RATE.get(), MIN_TRANSFER_RATE), getTotalVolume(stack));
    }

    public static void setTransferRate(@NotNull ItemStack stack, int rate) {
        if (stack.getItem() instanceof FluidContainerItem fluidContainerItem) {
            stack.set(BitterDataComponents.TRANSFER_RATE.get(), Mth.clamp(rate, MIN_TRANSFER_RATE, getMaxTransferRate(stack)));
        }
    }

    protected void playEmptySound(@NotNull Level level, BlockPos pos) {
        level.playSound(null, pos,
                SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.5F, 1.5F);
    }

    protected void playFillSound(@NotNull Level level, BlockPos pos) {
        level.playSound(null, pos,
                SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 0.5F, 1.0F);
    }

    protected void playDrinkSound(@NotNull Level level, BlockPos pos) {
        level.playSound(null, pos,
                SoundEvents.GENERIC_DRINK.value(), SoundSource.PLAYERS, 0.5F,
                level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    protected void playBurpSound(@NotNull Level level, BlockPos pos) {
        level.playSound(null, pos,
                SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F,
                level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return DRINK_SPEED + getTransferRate(stack);
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(getFlavorMessageComponent(stack));
            }
        }

        stack = consumeSubstances(stack, getLimitedTransferRate(stack), entity);

        playBurpSound(level, entity.getOnPos());

        return stack;
    }

    @Override
    public boolean onEntityItemUpdate(@NotNull ItemStack stack, @NotNull ItemEntity entity) {
        if (getTotalVolume(stack) <= 0) return false;

        Level level = entity.level();
        if (!level.isClientSide() && !entity.isNoGravity() && entity.onGround()) {
            if (!stack.getOrDefault(HAS_LANDED.get(), false)) {
                stack.set(HAS_LANDED.get(), true);
                if (stack.getOrDefault(CAN_SPILL, true)) {
                    spill(stack, level, entity.blockPosition(), (int) (getMaxTransferRate(stack) * entity.getRandom().nextFloat()));
                }
            }
        }
        return false;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (stack.getOrDefault(HAS_LANDED.get(), false)) {
            stack.set(HAS_LANDED.get(), false);
        }
    }

    public void spill(ItemStack stack, @NotNull Level level, BlockPos pos, int volume) {
        if (volume <= 0) return;
        BlockState existingState = level.getBlockState(pos);

        if (existingState.getBlock() instanceof SubstanceFluidBlock) {
            transferSubstancesToBlock(pos, level, stack, volume);
        } else if (existingState.canBeReplaced()) {
            level.setBlock(pos, SUBSTANCE_FLUID.get().defaultBlockState(), UPDATE_NONE);
            transferSubstancesToBlock(pos, level, stack, volume);
        }
    }

    @FunctionalInterface
    protected interface SubstanceTransferHandler {
        void handle(SubstanceStack substance, int amount);
    }
}
