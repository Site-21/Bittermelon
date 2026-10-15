package com.site21.bittermelon.common.systems.component.heavy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Heavy(double basePenalty, WeightSource source) {
    public static final Codec<Heavy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("base_penalty").forGetter(Heavy::basePenalty),
            WeightSource.CODEC.fieldOf("source").forGetter(Heavy::source)
    ).apply(instance, Heavy::new));

    public Heavy(double basePenalty) {
        this(basePenalty, WeightSource.NONE);
    }
}
