package com.site21.bittermelon.common.systems.ragdoll.client;

import com.github.stephengold.joltjni.Body;
import com.github.stephengold.joltjni.Quat;
import com.github.stephengold.joltjni.RVec3;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class RagdollTransformation {
    public static final StreamCodec<ByteBuf, RagdollTransformation> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public RagdollTransformation decode(ByteBuf input) {
            RagdollTransformation transformation = new RagdollTransformation();
            transformation.pos.set(input.readDouble(), input.readDouble(), input.readDouble());
            transformation.rot.set(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
            return transformation;
        }

        @Override
        public void encode(ByteBuf output, RagdollTransformation value) {
            output.writeDouble(value.pos.xx());
            output.writeDouble(value.pos.yy());
            output.writeDouble(value.pos.zz());
            output.writeFloat(value.rot.getX());
            output.writeFloat(value.rot.getY());
            output.writeFloat(value.rot.getZ());
            output.writeFloat(value.rot.getW());
        }
    };

    public final RVec3 pos = new RVec3();
    public final Quat rot = new Quat();

    public void update(Body body) {
        pos.set(body.getPosition());
        rot.set(body.getRotation());
    }
}
