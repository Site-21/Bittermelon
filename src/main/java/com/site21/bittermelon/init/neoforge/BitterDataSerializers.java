package com.site21.bittermelon.init.neoforge;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.entities.cage.client.BlockInfo;
import com.site21.bittermelon.common.content.entities.scp939.SCP939State;
import com.site21.bittermelon.common.systems.ragdoll.client.RagdollTransformation;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BitterDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Bittermelon.MOD_ID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<List<BlockInfo>>> BLOCK_INFO = ENTITY_DATA_SERIALIZERS.register(
            "blocks",
            () -> new EntityDataSerializer<>() {
                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, List<BlockInfo>> codec() {
                    return BlockInfo.STREAM_CODEC.apply(ByteBufCodecs.list());
                }

                @Override
                public List<BlockInfo> copy(List<BlockInfo> value) {
                    return new ArrayList<>(value);
                }
            }
    );

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<UUID>> UUID = ENTITY_DATA_SERIALIZERS.register(
            "uuid",
            () -> new EntityDataSerializer<>() {
                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, UUID> codec() {
                    return UUIDUtil.STREAM_CODEC;
                }

                @Override
                public UUID copy(UUID value) {
                    return value;
                }
            }
    );

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<SCP939State>> SCP_939_STATE = ENTITY_DATA_SERIALIZERS.register(
            "scp_939_state",
            () -> new EntityDataSerializer<>() {
                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, SCP939State> codec() {
                    return SCP939State.STREAM_CODEC;
                }

                @Override
                public SCP939State copy(SCP939State value) {
                    return value;
                }
            }
    );

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<List<RagdollTransformation>>> RAGDOLL_TRANSFORMATIONS = ENTITY_DATA_SERIALIZERS.register(
            "ragdoll_transformations",
            () -> new EntityDataSerializer<>() {
                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, List<RagdollTransformation>> codec() {
                    return RagdollTransformation.STREAM_CODEC.apply(ByteBufCodecs.list());
                }

                @Override
                public List<RagdollTransformation> copy(List<RagdollTransformation> value) {
                    return new ArrayList<>(value);
                }
            }
    );
}
