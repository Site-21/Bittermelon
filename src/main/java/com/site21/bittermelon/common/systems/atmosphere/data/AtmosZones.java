package com.site21.bittermelon.common.systems.atmosphere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.atmosphere.AtmosHandler;
import com.site21.bittermelon.common.systems.atmosphere.AtmosZone;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtmosZones extends SavedData {
    public static final SavedDataType<AtmosZones> TYPE;
    private final Map<Integer, AtmosZone> zones = new HashMap<>();
    private int nextId;
    private final LongLinkedOpenHashSet pending = new LongLinkedOpenHashSet();

    public AtmosZones(Map<Integer, AtmosZone> zones, int nextId) {
        this.zones.putAll(zones);
        this.nextId = nextId;
    }

    public AtmosZones() {
        this(new HashMap<>(), 0);
    }

    public static AtmosZones get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(TYPE);
        }
        throw new IllegalStateException("AtmosZones can only be accessed on the server side.");
    }

    public void tick(ServerLevel level) {
        int processed = 0;
        while (!pending.isEmpty() && processed < 64) {
            BlockPos pos = BlockPos.of(pending.removeFirstLong());
            AtmosHandler.updateAtmosphere(level, pos);
            processed++;
        }
    }

    public void addPending(BlockPos pos) {
        pending.add(pos.asLong());
    }

    public Map<Integer, AtmosZone> getZones() {
        return zones;
    }

    public AtmosZone getAtmosInstance(int id) {
        return zones.get(id);
    }

    public void addAtmosInstance(AtmosZone instance) {
        zones.put(instance.getId(), instance);
        setDirty();
    }

    public int getNextId() {
        return nextId++;
    }

    public void removeAtmosInstance(int uuid) {
        zones.remove(uuid);
        setDirty();
    }

    private static AtmosZones fromList(List<AtmosZone> list, int nextId) {
        Map<Integer, AtmosZone> map = new HashMap<>();
        for (AtmosZone zone : list) {
            map.put(zone.getId(), zone);
        }
        return new AtmosZones(map, nextId);
    }

    static {
        TYPE = new SavedDataType<>(
                Bittermelon.identifier("atmos_zones"),
                AtmosZones::new,
                RecordCodecBuilder.create(instance -> instance.group(
                        AtmosZone.CODEC.listOf()
                                .fieldOf("zones")
                                .forGetter(data -> List.copyOf(data.zones.values())),
                        Codec.INT
                                .fieldOf("next_id")
                                .forGetter(data -> data.nextId)
                ).apply(instance, AtmosZones::fromList)));
    }
}
