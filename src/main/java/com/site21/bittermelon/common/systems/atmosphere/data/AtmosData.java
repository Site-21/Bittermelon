package com.site21.bittermelon.common.systems.atmosphere.data;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.atmosphere.AtmosZone;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AtmosData extends SavedData {
    public static final SavedDataType<AtmosData> TYPE;
    private static AtmosData clientInstance;
    private final Map<Integer, AtmosZone> atmosInstances = new ConcurrentHashMap<>();

    public static @NotNull AtmosData get(@NotNull Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(TYPE);
        }
        return getClient();
    }

    
    private static AtmosData getClient() {
        if (clientInstance == null) {
            clientInstance = new AtmosData();
        }
        return clientInstance;
    }

    
    public static void clearClientData() {
        if (clientInstance != null) {
            clientInstance.atmosInstances.clear();
        }
    }

    public Map<Integer, AtmosZone> getAtmosInstances() {
        return atmosInstances;
    }

    public AtmosZone getAtmosInstance(UUID uuid) {
        return atmosInstances.get(uuid);
    }

    public void addAtmosInstance(AtmosZone instance) {
        atmosInstances.put(instance.getId(), instance);
        setDirty();
    }

    public void removeAtmosInstance(int uuid) {
        atmosInstances.remove(uuid);
        setDirty();
    }
    static {
        TYPE = new SavedDataType<>(
                Bittermelon.identifier("atmos_instances"),
                AtmosData::new,
                RecordCodecBuilder.create(instance -> instance.group(
                                AtmosZone.CODEC.listOf().fieldOf("instances")
                                        .forGetter(data -> new ArrayList<>(data.atmosInstances.values())))
                        .apply(instance, instances -> {
                            AtmosData data = new AtmosData();
                            for (AtmosZone atmosZone : instances) {
                                data.atmosInstances.put(atmosZone.getId(), atmosZone);
                            }
                            return data;
                        })));
    }
}
