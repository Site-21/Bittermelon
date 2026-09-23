package com.site21.bittermelon.common.systems.ragdoll.networking;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.ragdoll.RagdollUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RagdollPlayer(UUID playerUUID) implements CustomPacketPayload {
    public static final Type<RagdollPlayer> TYPE = new Type<>(Bittermelon.identifier("ragdoll_player"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, RagdollPlayer> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            RagdollPlayer::playerUUID,
            RagdollPlayer::new
    );

    public void handle(IPayloadContext ctx) {
        Level level = ctx.player().level();
        if (level.getPlayerByUUID(playerUUID) instanceof Player player) {
            RagdollUtil.ragdoll(player);
        }
    }
}
