package com.site21.bittermelon.init.neoforge;

import com.mojang.serialization.MapCodec;
import com.site21.bittermelon.Bittermelon;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BitterParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Bittermelon.MOD_ID);

    public static final Supplier<ParticleType<ColorParticleOption>> PLASTIC = PARTICLES.register("plastic",
            () -> new ParticleType<>(false) {
        @Override
        public @NotNull MapCodec<ColorParticleOption> codec() {
            return ColorParticleOption.codec((ParticleType<ColorParticleOption>) this);
        }

        @Override
        public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleOption> streamCodec() {
            return ColorParticleOption.streamCodec((ParticleType<ColorParticleOption>) this);
        }
    });

    public static final Supplier<ParticleType<ColorParticleOption>> FOAM = PARTICLES.register("foam",
            () -> new ParticleType<>(false) {
        @Override
        public @NotNull MapCodec<ColorParticleOption> codec() {
            return ColorParticleOption.codec((ParticleType<ColorParticleOption>) this);
        }

        @Override
        public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleOption> streamCodec() {
            return ColorParticleOption.streamCodec((ParticleType<ColorParticleOption>) this);
        }
    });

    public static final Supplier<SimpleParticleType> KAPOW = PARTICLES.register("kapow",
            () -> new SimpleParticleType(false));

    public static final Supplier<SimpleParticleType> BOOM = PARTICLES.register("boom",
            () -> new SimpleParticleType(false));

    public static final Supplier<SimpleParticleType> BUBBLE = PARTICLES.register("bubble",
            () -> new SimpleParticleType(false));
}
