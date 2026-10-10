package com.site21.bittermelon.common.content.items.scps.scp815;

import com.site21.bittermelon.common.content.entities.scp815snake.SCP815Snake;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlock;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlockEntity;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.init.custom.Substances;
import com.site21.bittermelon.init.neoforge.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;


public class SCP815Item extends Item {

    private static final long RESEAL_TICKS = 15 * 20L;

    private static final Component MSG_OPEN = Component.literal("You open the can.")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);

    private static final BlockParticleOption BLOOD_PARTICLE = new BlockParticleOption(
            ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState()
    );

    public SCP815Item(Properties properties) {
        super(properties);
    }


    // For Berry robots to open the can on the nearest player
    public static void openCanOnNearestPlayer(Level level, Vec3 origin, double range) {
        if (level.isClientSide()) return;
        Player nearest = level.getNearestPlayer(origin.x, origin.y, origin.z, range, false);
        if (nearest == null) return;
        ItemStack dummy = new ItemStack(BitterItems.SCP_815.get());
        dummy.set(BitterDataComponents.OPEN_TIME.get(), null);
        openCan(dummy, nearest, level);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player,
                                          @NotNull InteractionHand usedHand) {
        if (level.isClientSide()) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(usedHand);
        if (isOpen(stack, level)) return InteractionResult.PASS;

        openCan(stack, player, level);
        return InteractionResult.SUCCESS;
    }

    private boolean isOpen(ItemStack stack, Level level) {
        Long openedAt = stack.get(BitterDataComponents.OPEN_TIME.get());
        return openedAt != null && (level.getGameTime() - openedAt) < RESEAL_TICKS;
    }

    private static void openCan(ItemStack stack, Player player, Level level) {
        stack.set(BitterDataComponents.OPEN_TIME.get(), level.getGameTime());
        player.sendSystemMessage(MSG_OPEN);
        player.getCooldowns().addCooldown(stack, 10);
        spawnSnakes(player, level);
        spawnBloodParticles(player, level);
        long t = level.getGameTime() + 10;
        NeoForge.EVENT_BUS.addListener(new Consumer<LevelTickEvent.Post>() {
            public void accept(LevelTickEvent.Post e) {
                if (e.getLevel() != level || level.getGameTime() < t) return;
                spawnBloodPuddle(player, level);
                NeoForge.EVENT_BUS.unregister(this);
            }
        });
        player.addEffect(new MobEffectInstance(BitterMobEffects.BLEEDING, 200000, 1, false, false));
        player.addEffect(new MobEffectInstance(BitterMobEffects.BROKEN_JAW, 200000, 0, false, false));
    }

    private static void spawnSnakes(Player player, Level level) {
        Vec3 eyes = player.getEyePosition();
        Vec3 mouth = new Vec3(eyes.x, eyes.y - 0.06, eyes.z);
        Vec3 look = player.getLookAngle();

        for (int i = 0; i < 4; i++) {
            SCP815Snake snake = new SCP815Snake(BitterEntities.SCP_815_SNAKE.get(), level);
            snake.setPos(
                    mouth.x + look.x * 0.2 + (level.getRandom().nextDouble() - 0.5) * 0.2,
                    mouth.y,
                    mouth.z + look.z * 0.2 + (level.getRandom().nextDouble() - 0.5) * 0.2
            );
            snake.setDeltaMovement(
                    look.x * 0.3 + (level.getRandom().nextDouble() - 0.5) * 0.15,
                    -0.3 - level.getRandom().nextDouble() * 0.2,
                    look.z * 0.3 + (level.getRandom().nextDouble() - 0.5) * 0.15
            );
            snake.setInvulnerable(true);
            snake.setSilent(true);
            level.addFreshEntity(snake);
        }
    }

    private static void spawnBloodParticles(Player player, Level level) {
        ServerLevel serverLevel = (ServerLevel) level;
        Vec3 origin = player.getEyePosition();
        Vec3 look = player.getLookAngle();

        for (int i = 0; i < 400; i++) {
            serverLevel.sendParticles(BLOOD_PARTICLE,
                    origin.x, origin.y, origin.z, 0,
                    look.x * 0.5 + level.getRandom().nextGaussian() * 0.15,
                    look.y * 0.2 + level.getRandom().nextGaussian() * 0.15,
                    look.z * 0.5 + level.getRandom().nextGaussian() * 0.15,
                    1.0
            );
        }
    }

    private static void spawnBloodPuddle(Player player, Level level) {
        Vec3 look = player.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0, look.z).normalize();
        double cx = player.getX() + forward.x;
        double cz = player.getZ() + forward.z;

        for (int[] o : new int[][]{{0,0},{1,0},{-1,0},{0,1},{0,-1}}) {
            double wx = cx + forward.z * o[0] + forward.x * o[1];
            double wz = cz - forward.x * o[0] + forward.z * o[1];

            for (int y = (int) player.getY() + 1; y >= (int) player.getY() - 5; y--) {
                BlockPos check = BlockPos.containing(wx, y, wz);
                BlockPos above = check.above();
                if (!level.getBlockState(check).canBeReplaced() && level.getBlockState(above).canBeReplaced()) {
                    level.setBlockAndUpdate(above, BitterBlocks.SUBSTANCE_FLUID.get().defaultBlockState().setValue(SubstanceFluidBlock.LEVEL, 1));
                    if (level.getBlockEntity(above) instanceof SubstanceFluidBlockEntity be) {
                        be.getMixture().updateSubstance(new SubstanceStack(Substances.KOOL_AID.get(), 50));
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
        Long openedAt = stack.get(BitterDataComponents.OPEN_TIME.get());
        if (openedAt != null && (level.getGameTime() - openedAt) >= RESEAL_TICKS)
            stack.remove(BitterDataComponents.OPEN_TIME.get());
    }
}