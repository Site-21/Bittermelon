package com.site21.bittermelon.common.systems.atmosphere.data;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class AtmosChunkGrid {
    private static final int SECTION_SIZE = 4096;
    public static final Codec<AtmosChunkGrid> CODEC = AtmosSection.CODEC.listOf()
            .xmap(AtmosChunkGrid::fromSections, AtmosChunkGrid::toSections);

    private final Int2ObjectMap<int[]> sections = new Int2ObjectOpenHashMap<>();
    private final Int2IntOpenHashMap counts = new Int2IntOpenHashMap();

    private static AtmosChunkGrid fromSections(List<AtmosSection> saved) {
        AtmosChunkGrid data = new AtmosChunkGrid();

        // RLE decoding
        for (AtmosSection section : saved) {
            int[] runs = section.runs();
            int[] ids = new int[SECTION_SIZE];
            int index = 0;
            int count = 0;

            // reads value and number of times it repeats and fills the ids accordingly
            for (int i = 0; i + 1 < runs.length && index < SECTION_SIZE; i += 2) {
                int value = runs[i];
                int run = Math.min(runs[i + 1], SECTION_SIZE - index);
                for (int j = 0; j < run; j++) {
                    ids[index++] = value;
                }
                if (value != 0) count += run;
            }

            if (count > 0) {
                data.sections.put(section.y(), ids);
                data.counts.put(section.y(), count);
            }
        }

        return data;
    }

    private List<AtmosSection> toSections() {
        List<AtmosSection> saved = new ArrayList<>(sections.size());

        // RLE encoding
        for (Int2ObjectMap.Entry<int[]> entry : sections.int2ObjectEntrySet()) {
            int[] ids = entry.getValue();
            IntArrayList runs = new IntArrayList();

            int value = ids[0];
            int run = 1;

            // stores the value and the number of times it repeats consecutively
            for (int i = 1; i < SECTION_SIZE; i++) {
                if (ids[i] == value) {
                    run++;
                } else {
                    runs.add(value);
                    runs.add(run);
                    value = ids[i];
                    run = 1;
                }
            }
            runs.add(value);
            runs.add(run);

            saved.add(new AtmosSection(entry.getIntKey(), runs.toIntArray()));
        }

        return saved;
    }

    public int[] getSection(int y) {
        return sections.get(y >> 4);
    }

    public int get(BlockPos pos) {
        int[] section = getSection(pos.getY());
        return section == null ? 0 : section[index(pos)];
    }

    public boolean set(BlockPos pos, int id) {
        int sectionY = pos.getY() >> 4;
        int[] section = sections.get(sectionY);
        if (section == null) {
            if (id == 0) return false; // nothing to remove
            section = new int[4096];
            sections.put(sectionY, section);
        }

        int index = index(pos);
        int old = section[index];
        if (old == id) return false;
        section[index] = id;

        if (old == 0) {
            counts.addTo(sectionY, 1); // empty slot filled
        } else if (id == 0) {
            if (counts.addTo(sectionY, -1) == 1) { // section is now empty
                sections.remove(sectionY);
                counts.remove(sectionY);
            }
        }

        return true;
    }

    public int index(BlockPos pos) {
        return pos.getX() & 15 | (pos.getY() & 15) << 4 | (pos.getZ() & 15) << 8;
    }
}
