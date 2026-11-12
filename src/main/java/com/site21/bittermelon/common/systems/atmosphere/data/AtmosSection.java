package com.site21.bittermelon.common.systems.atmosphere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.stream.IntStream;

public record AtmosSection(int y, int[] runs) {
    public static Codec<AtmosSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT
                    .fieldOf("y")
                    .forGetter(AtmosSection::y),
            Codec.INT_STREAM
                    .fieldOf("runs")
                    .xmap(IntStream::toArray, IntStream::of)
                    .forGetter(AtmosSection::runs)
    ).apply(instance, AtmosSection::new));
}
