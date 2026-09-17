package com.site21.bittermelon.common.events;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.items.clownhammer.ClownHammer;
import com.site21.bittermelon.common.content.items.scps.scp377.FortuneHandler;
import com.site21.bittermelon.common.physics.PhysicsManager;
import com.site21.bittermelon.common.systems.atmosphere.AtmosHandler;
import com.site21.bittermelon.common.systems.atmosphere.data.AtmosInstancesData;
import com.site21.bittermelon.common.systems.atmosphere.networking.AtmosChunkUpdate;
import com.site21.bittermelon.common.systems.blockdamage.BlockDamageUtil;
import com.site21.bittermelon.common.systems.carry.CarryHandler;
import com.site21.bittermelon.common.systems.character.Character;
import com.site21.bittermelon.common.systems.character.CharacterManager;
import com.site21.bittermelon.common.systems.character.networking.SyncActiveCharacter;
import com.site21.bittermelon.common.systems.character.networking.SyncCharacters;
import com.site21.bittermelon.common.systems.character.skills.SkillUpdater;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluid;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlockEntity;
import com.site21.bittermelon.common.systems.medical.wound.HitCalculator;
import com.site21.bittermelon.common.systems.rage.RageHandler;
import com.site21.bittermelon.common.systems.stress.StressHandler;
import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.common.systems.telecomms.intercom.IntercomManager;
import com.site21.bittermelon.common.systems.telecomms.intercom.networking.SyncIntercomList;
import com.site21.bittermelon.init.neoforge.BitterMobEffects;
import com.site21.bittermelon.networking.server.SetLastTypingTime;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.site21.bittermelon.common.content.blocks.dirtyfloor.DirtyBlocksHandler.tickDirtying;
import static com.site21.bittermelon.init.custom.Anatomies.HUMAN;
import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.*;
import static com.site21.bittermelon.init.neoforge.BitterFluids.SUBSTANCE_FLUID;
import static com.site21.bittermelon.init.neoforge.BitterMobEffects.DROWNING;

@EventBusSubscriber(modid = Bittermelon.MOD_ID)
public class CommonEvents {
    private static final int STAIN_TICK_INTERVAL = 20;

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.@NotNull Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        FortuneHandler.onEntityTick(entity);

        if (entity instanceof LivingEntity livingEntity) {
            CarryHandler.tickCarrying(livingEntity);
            tickStains(level, livingEntity);

            if (level instanceof ServerLevel serverLevel) {
                tickDirtying(serverLevel, livingEntity);
            }

            Character character = CharacterManager.get(entity.level()).getActiveCharacter(entity);
            if (character != null) {
                tickCharacter(livingEntity, character);
            }
        }

//        if (entity instanceof Avatar || entity instanceof Mimic) {
//            if (!entity.hasData(HEALTH_CONTAINER)) {
//                entity.setData(HEALTH_CONTAINER, new HumanDefinition().createHealthContainer(entity));
//            }
//        }

        if (entity instanceof Player player) {
            if (!player.hasData(MEDICAL_STATS)) {
                player.setData(MEDICAL_STATS, HUMAN.get().toInstance(player));
            } else {
                player.getData(MEDICAL_STATS).tick(player);
            }

            StressHandler.tickStress(level, player);
            RageHandler.tick(level, player);
            updateTypingTime(player);
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (serverLevel.tickRateManager().runsNormally()) {
                PhysicsManager.updatePhysicsLevel(event.getLevel());
            }
        }
    }

    private static void tickCharacter(LivingEntity entity, Character character) {
        SkillUpdater.tickSkills(entity, character);
    }

    private static void updateTypingTime(Player player) {
        if (player.level().isClientSide()) return;

        if (player.getExistingDataOrNull(LAST_TYPING_TIME) != null) {
            long lastTypingTime = player.getData(LAST_TYPING_TIME);
            long timeSinceTyping = System.currentTimeMillis() - lastTypingTime;

            if (timeSinceTyping > 5000) {
                player.removeData(LAST_TYPING_TIME);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SetLastTypingTime(player.getUUID(), -1));
            }
        }
    }

    private static void tickStains(Level level, LivingEntity entity) {
        BlockPos pos = entity.blockPosition();

        if (level.isClientSide()) return;

        if (entity.hasData(STAINS) && level.getGameTime() % STAIN_TICK_INTERVAL == 0) {
            SubstanceMixture stains = entity.getData(STAINS);
            stains.tickReactions(level, pos);

            for (SubstanceStack stack : stains.getSubstances()) {
                stack.getSubstance().onContact(stack, entity);
            }

            if (stains.getVolume() > 10 && level.getRandom().nextFloat() > 0.1f) {
                List<SubstanceStack> drippedSubstances = stains.spreadSubstancesByVolume(10);
                if (drip(level, pos, drippedSubstances)) {
                    stains.removeSubstances(drippedSubstances);
                    level.playSound(null, pos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER,
                            SoundSource.AMBIENT, 1.0f, 0.8f + level.getRandom().nextFloat() * 0.4f);
                }
            }
        }
    }

    public static boolean drip(@NotNull Level level, BlockPos pos, List<SubstanceStack> substances) {
        SubstanceFluid fluid = SUBSTANCE_FLUID.get();

        if (level.getBlockState(pos).canBeReplaced() && level.getFluidState(pos).isEmpty()) {
            level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), Block.UPDATE_ALL);
        }

        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity spreadBE) {
            spreadBE.transferSubstances(substances);
            return true;
        }

        return false;
    }

    @SubscribeEvent
    public static void onBreakBlock(BreakBlockEvent event) {
        if (event.isCanceled()) return;
        BlockDamageUtil.clearDamage(event.getLevel(), event.getPos());

        if (!(event.getLevel() instanceof Level level) || level.isClientSide()) return;
        PhysicsManager.getPhysicsLevel(level).markDirty(event.getPos());
    }

    @SubscribeEvent
    public static void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide()) return;
        PhysicsManager.getPhysicsLevel(level).markDirty(event.getPos());
    }

    @SubscribeEvent
    public static void onMultiPlaceBlock(BlockEvent.EntityMultiPlaceEvent event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide()) return;
        PhysicsManager.getPhysicsLevel(level).markDirty(event.getPos());
    }

    @SubscribeEvent
    public static void onClownHammerBreak(BreakBlockEvent event) {
        if (event.getPlayer().getMainHandItem().getItem() instanceof ClownHammer) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.@NotNull EntityInteract event) {
        if (CarryHandler.playerPickUpEntity(event.getEntity(), event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.@NotNull RightClickBlock event) {
        if (CarryHandler.placeEntity(event.getEntity(), event.getPos(), event.getHitVec())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(@NotNull LivingDeathEvent event) {
        // TODO: Save dead NPC characters so they can be restored or looked back on
        LivingEntity entity = event.getEntity();

        // Clean up NPCs
        if (!entity.level().isClientSide()) {
            if (!(entity instanceof Player)) {
                CharacterManager characterManager = CharacterManager.get(entity.level());
                List<Character> characters = characterManager.getCharactersByEntityUUID(entity.getUUID());
                List<Character> charactersToRemove = new ArrayList<>(characters);
                for (Character character : charactersToRemove) {
                    characterManager.removeCharacter(character.getId());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent.Pre event) {
        HitCalculator.damageEntity(event);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Level level = player.level();

            AtmosInstancesData.get(level).syncToClient();
            PacketDistributor.sendToPlayer(player, new SyncIntercomList(IntercomManager.get(level).getIntercomIDs()));

            CharacterManager characterManager = CharacterManager.get(level);
            PacketDistributor.sendToPlayer(player, new SyncCharacters(characterManager.getCharacters()));

            Character activeCharacter = characterManager.getActiveCharacter(player);
            if (activeCharacter != null) {
                PacketDistributor.sendToPlayer(player, new SyncActiveCharacter(activeCharacter.getId()));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingBreathe(@NotNull LivingBreatheEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.hasEffect(DROWNING)) {
            if (player.getEffect(DROWNING).getAmplifier() > 9) {
                event.setCanBreathe(false);
            }
            event.setRefillAirAmount(0);
        }
    }

    @SubscribeEvent
    public static void onChunkSent(ChunkWatchEvent.@NotNull Sent event) {
        // Sync the atmosphere data for the chunk to the player when they start tracking it
        PacketDistributor.sendToPlayer(event.getPlayer(),
                new AtmosChunkUpdate(event.getPos(), event.getChunk().getData(ATMOSPHERE.get())));
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (levelAccessor.isClientSide() || !(levelAccessor instanceof Level level)) return;
        ChunkPos pos = event.getChunk().getPos();
        PhysicsManager.getPhysicsLevel(level.dimension()).unloadChunk(pos);
    }

    @SubscribeEvent
    public static void onBlockNotifyNeighbors(BlockEvent.NeighborNotifyEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            AtmosHandler.onBlockUpdate(serverLevel, event.getPos());
        }
    }

    @SubscribeEvent
    public static void onRemoveEffect(MobEffectEvent.Remove event) {
        if (event.getEffect().is(BitterMobEffects.AMNESIA) && event.getEntity() instanceof ServerPlayer player) {
            Component text = Component.literal("You struggle to remember what just happened.").withStyle(ChatFormatting.RED);
            player.sendSystemMessage(text, true);
            player.sendSystemMessage(text);
        }
    }
}
