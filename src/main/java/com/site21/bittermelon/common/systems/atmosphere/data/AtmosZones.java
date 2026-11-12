package com.site21.bittermelon.common.systems.atmosphere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.atmosphere.AtmosZone;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;

public class AtmosZones extends SavedData {
    public static final SavedDataType<AtmosZones> TYPE;
    private final Map<Integer, AtmosZone> zones = new HashMap<>();
    private int nextId;

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

    static {
        TYPE = new SavedDataType<>(
                Bittermelon.identifier("atmos_zones"),
                AtmosZones::new,
                RecordCodecBuilder.create(instance -> instance.group(
                        Codec.unboundedMap(Codec.INT, AtmosZone.CODEC)
                                .fieldOf("zones")
                                .forGetter(data -> data.zones),
                        Codec.INT
                                .fieldOf("next_id")
                                .forGetter(data -> data.nextId)
                ).apply(instance, AtmosZones::new)));
    }
}
