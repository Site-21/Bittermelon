package com.site21.bittermelon.client.particles;

import com.site21.bittermelon.common.systems.fluid.substance.MixtureOwner;
import com.site21.bittermelon.init.neoforge.BitterFluidTags;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class BubbleParticle extends SingleQuadParticle {
    private static final float VELOCITY_SCALE = 0.2f;
    private static final float VELOCITY_JITTER = 0.02f;
    private static final float BASE_LIFETIME = 40.0f;

    public BubbleParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, TextureAtlasSprite sprite) {
        super(level, x, y, z, sprite);
        this.gravity = -0.125f;
        this.friction = 0.85f;
        this.setSize(0.02f, 0.02f);
        this.quadSize *= random.nextFloat() * 0.6f + 0.2f;
        this.xd = initialVelocity(xa);
        this.yd = initialVelocity(ya);
        this.zd = initialVelocity(za);
        this.lifetime = (int) (BASE_LIFETIME / (random.nextFloat() * 0.8f + 0.2f));
        BlockPos pos = BlockPos.containing(x, y, z);
        if (level.getBlockEntity(pos) instanceof MixtureOwner owner) {
            int color = owner.getColor();
            setColor(
                    ((color >> 16) & 0xFF) / 255.0f,
                    ((color >> 8) & 0xFF) / 255.0f,
                    (color & 0xFF) / 255.0f
            );
        }
    }

    private double initialVelocity(double base) {
        return base * VELOCITY_SCALE + (random.nextFloat() * 2.0f - 1.0f) * VELOCITY_JITTER;
    }

    public void tick() {
        super.tick();
        if (!removed && !level.getFluidState(BlockPos.containing(x, y, z)).is(BitterFluidTags.SUBSTANCE)) {
            remove();
        }
    }

    public SingleQuadParticle.Layer getLayer() {
        return Layer.OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new BubbleParticle(level, x, y, z, xAux, yAux, zAux, sprite.get(random));
        }
    }
}
