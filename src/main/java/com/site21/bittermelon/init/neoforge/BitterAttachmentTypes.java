package com.site21.bittermelon.init.neoforge;

import com.mojang.serialization.Codec;
import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.items.scps.scp377.FortuneInstance;
import com.site21.bittermelon.common.systems.atmosphere.data.AtmosChunkGrid;
import com.site21.bittermelon.common.systems.blockdamage.BlockDamageData;
import com.site21.bittermelon.common.systems.electronics.wiring.WireNetwork;
import com.site21.bittermelon.common.systems.medical.bodypart.HealthContainer;
import com.site21.bittermelon.common.systems.medical.legacy.medicalstats.MedicalStats;
import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import com.site21.bittermelon.init.custom.BodyParts;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class BitterAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Bittermelon.MOD_ID);

    public static final Supplier<AttachmentType<List<UUID>>> GERMS = ATTACHMENT_TYPES.register(
            "germs", () -> AttachmentType.builder(() -> List.<UUID>of()).serialize(Codec.list(UUIDUtil.CODEC).fieldOf("germs")).build()
    );

    public static final Supplier<AttachmentType<UUID>> ACTIVE_CHARACTER = ATTACHMENT_TYPES.register(
            "active_character", () -> AttachmentType.builder(() ->
                    new UUID(0, 0)).serialize(UUIDUtil.CODEC.fieldOf("active_character")).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<AtmosChunkGrid>> ATMOSPHERE = ATTACHMENT_TYPES.register(
            "atmosphere", () -> AttachmentType.builder(AtmosChunkGrid::new).serialize(AtmosChunkGrid.CODEC.fieldOf("atmosphere")).build()
    );

    public static final Supplier<AttachmentType<Integer>> ACTIVE_CHANNEL = ATTACHMENT_TYPES.register(
            "active_channel", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("active_channel")).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<Integer>> STEP_COUNTER = ATTACHMENT_TYPES.register(
            "step_counter", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("step_counter")).build()
    );

    public static final Supplier<AttachmentType<Integer>> STRESS = ATTACHMENT_TYPES.register(
            "stress", () -> AttachmentType.builder(() -> 0)
                    .serialize(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("stress"))
                    .sync(ByteBufCodecs.INT)
                    .build()
    );

    public static final Supplier<AttachmentType<Float>> STRESS_RELIEF = ATTACHMENT_TYPES.register(
            "stress_relief", () -> AttachmentType.builder(() -> 0f)
                    .serialize(Codec.FLOAT.fieldOf("stress_relief"))
                    .sync(ByteBufCodecs.FLOAT)
                    .build()
    );

    public static final Supplier<AttachmentType<Long>> LAST_TYPING_TIME = ATTACHMENT_TYPES.register(
            "last_typing_time", () -> AttachmentType.builder(() -> 0L).build()
    );

    public static final Supplier<AttachmentType<List<FortuneInstance>>> FORTUNE_INSTANCES = ATTACHMENT_TYPES.register(
            "fortune_instances", () -> AttachmentType.builder(() -> List.<FortuneInstance>of())
                    .serialize(Codec.list(FortuneInstance.CODEC).fieldOf("fortune_instances")).build()
    );

    public static final Supplier<AttachmentType<String>> LORE_OPENING = ATTACHMENT_TYPES.register(
            "lore_opening", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING.fieldOf("lore_opening")).build()
    );

    public static final Supplier<AttachmentType<BlockDamageData>> BLOCK_DAMAGE = ATTACHMENT_TYPES.register(
            "block_damage", () -> AttachmentType.builder(BlockDamageData::new)
                    .serialize(BlockDamageData.CODEC.fieldOf("block_damage"))
                    .sync(BlockDamageData.STREAM_CODEC)
                    .build()
    );

    public static final Supplier<AttachmentType<WireNetwork>> WIRE_NETWORK = ATTACHMENT_TYPES.register(
            "wire_network", () -> AttachmentType.builder(WireNetwork::new)
                    .serialize(WireNetwork.CODEC.fieldOf("wire_network"))
                    .sync(WireNetwork.STREAM_CODEC)
                    .build()
    );

    public static final Supplier<AttachmentType<Long>> LAST_SCREAM_TIME = ATTACHMENT_TYPES.register(
            "last_scream_time", () -> AttachmentType.builder(() -> 0L).build()
    );

    public static final Supplier<AttachmentType<UUID>> CARRIED_PASSENGER = ATTACHMENT_TYPES.register(
            "carried_passenger", () -> AttachmentType.builder(() -> new UUID(0, 0))
                    .serialize(UUIDUtil.CODEC.fieldOf("carried_passenger"))
                    .sync(UUIDUtil.STREAM_CODEC)
                    .build()
    );

    public static final Supplier<AttachmentType<MedicalStats>> MEDICAL_STATS = ATTACHMENT_TYPES.register(
            "medical_stats", () -> AttachmentType.builder(() -> MedicalStats.EMPTY)
                    .serialize(MedicalStats.CODEC.fieldOf("medical_stats"))
                    .sync(MedicalStats.STREAM_CODEC)
                    .build()
    );

    public static final Supplier<AttachmentType<SubstanceMixture>> STAINS = ATTACHMENT_TYPES.register(
            "stains", () -> AttachmentType.builder(() -> new SubstanceMixture())
                    .serialize(SubstanceMixture.CODEC.fieldOf("stains"))
                    .sync(SubstanceMixture.STREAM_CODEC)
                    .build()
    );

    public static final Supplier<AttachmentType<Boolean>> ENRAGED = ATTACHMENT_TYPES.register(
            "enraged", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("enraged"))
                    .sync(ByteBufCodecs.BOOL)
                    .build()
    );

    public static final Supplier<AttachmentType<Integer>> RAGE = ATTACHMENT_TYPES.register(
            "rage", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT.fieldOf("rage"))
                    .sync(ByteBufCodecs.INT)
                    .build()
    );

    public static final Supplier<AttachmentType<HealthContainer>> HEALTH_CONTAINER = ATTACHMENT_TYPES.register(
            "health_container", () -> AttachmentType.builder(() -> new HealthContainer(BodyParts.EMPTY.get().toInstance()))
                    .build()
    );
}
