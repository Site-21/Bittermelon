package com.site21.bittermelon.common.systems.atmosphere;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.common.systems.atmosphere.networking.UpdateAtmosBlocks;
import com.site21.bittermelon.common.systems.atmosphere.networking.UpdateAtmosGas;
import com.site21.bittermelon.common.systems.atmosphere.networking.UpdateAtmosTemperature;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.util.SubstanceUtil;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.site21.bittermelon.util.SubstanceUtil.GAS_CONSTANT;

public class AtmosInstance {
    private static final Codec<LongSet> LONG_SET_CODEC = Codec.LONG.listOf()
            .xmap(
                    LongOpenHashSet::new,
                    ArrayList::new
            );

    public static final Codec<AtmosInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("temperature").forGetter(AtmosInstance::getTemperature),
            SubstanceStack.CODEC.listOf().fieldOf("gases").forGetter(AtmosInstance::getGases),
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(AtmosInstance::getUUID),
            LONG_SET_CODEC.fieldOf("blocks").forGetter(AtmosInstance::getBlocks)
    ).apply(instance, AtmosInstance::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AtmosInstance> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT,
                    AtmosInstance::getTemperature,
                    SubstanceStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    AtmosInstance::getGases,
                    UUIDUtil.STREAM_CODEC,
                    AtmosInstance::getUUID,
                    ByteBufCodecs.collection(
                            LongOpenHashSet::new,
                            ByteBufCodecs.VAR_LONG
                    ),
                    AtmosInstance::getBlocks,
                    AtmosInstance::new
            );

    private final List<SubstanceStack> gases;
    private float temperature; // Kelvin
    private final UUID uuid;
    private final LongSet blocks;

    public AtmosInstance(float initialTemp, List<SubstanceStack> initialGasses, UUID uuid, LongSet blocks) {
        this.gases = new ArrayList<>(initialGasses);
        this.temperature = initialTemp;
        this.uuid = uuid;
        this.blocks = new LongOpenHashSet(blocks);
    }

    public AtmosInstance(float initialTemp, List<SubstanceStack> initialGasses, UUID uuid) {
        this(initialTemp, initialGasses, uuid, new LongOpenHashSet());
    }

    public AtmosInstance(float initialTemp, List<SubstanceStack> initialGasses) {
        this(initialTemp, initialGasses, UUID.randomUUID());
    }

    public AtmosInstance(float initialTemp) {
        this(initialTemp, new ArrayList<>(), UUID.randomUUID());
    }

    public float getTemperature() {
        return temperature;
    }

    public List<SubstanceStack> getGases() {
        return gases;
    }

    /**
     * Calculates the pressure of the atmosphere instance using the ideal gas law.
     *
     * @return The pressure in kPa.
     */
    public float getPressure() {
        if (blocks.isEmpty()) return 0;
        return SubstanceUtil.getTotalVolume(gases) * GAS_CONSTANT * temperature / (blocks.size() * 1000);
    }

    public UUID getUUID() {
        return uuid;
    }

    /**
     * Adds a block to this atmosphere instance and notifies all clients of the change.
     *
     * @param packedPos The packed position of the block to add.
     * @param level     The level in which the block is being added.
     */
    public void addBlock(Long packedPos, @NotNull Level level) {
        blocks.add(packedPos);
        if (!level.isClientSide()) {
            PacketDistributor.sendToAllPlayers(new UpdateAtmosBlocks(uuid, true, packedPos));
        }
    }

    /**
     * Removes a block from this atmosphere instance and notifies all clients of the change.
     *
     * @param packedPos The packed position of the block to remove.
     * @param level     The level in which the block is being removed.
     */
    public void removeBlock(Long packedPos, @NotNull Level level) {
        blocks.remove(packedPos);
        if (!level.isClientSide()) {
            PacketDistributor.sendToAllPlayers(new UpdateAtmosBlocks(uuid, false, packedPos));
        }
    }

    /**
     * Gets the set of blocks associated with this atmosphere instance.
     *
     * @return A LongSet of packed block positions.
     */
    public LongSet getBlocks() {
        return blocks;
    }

    /**
     * Sets the temperature of this atmosphere instance and notifies all clients of the change.
     *
     * @param temperature The new temperature in Kelvin.
     * @param level       The level in which the temperature is being set.
     */
    public void setTemperature(float temperature, @NotNull Level level) {
        this.temperature = temperature;
        if (!level.isClientSide()) {
            PacketDistributor.sendToAllPlayers(new UpdateAtmosTemperature(uuid, temperature));
        }
    }

    /**
     * Merges another atmosphere instance into this one, averaging temperatures and combining gases.
     * Notifies all clients of the changes.
     *
     * @param other The other atmosphere instance to merge.
     * @param level The level in which the merge is occurring.
     */
    public void merge(@NotNull AtmosInstance other, Level level) {
        setTemperature((temperature + other.getTemperature()) / 2, level);
        for (SubstanceStack gas : other.gases) {
            updateGas(gas, level);
        }
    }

    /**
     * Updates the amount of a specific gas in this atmosphere instance, adding it if not present.
     * Notifies all clients of the change.
     *
     * @param gas   The SubstanceStack representing the gas to update.
     * @param level The level in which the gas is being updated.
     */
    public void updateGas(SubstanceStack gas, Level level) {
        for (SubstanceStack stack : gases) {
            if (stack.canMergeWith(gas)) {
                stack.modifyAmount(gas.getAmount());
                if (!level.isClientSide()) {
                    PacketDistributor.sendToAllPlayers(new UpdateAtmosGas(uuid, stack));
                }
                return;
            }
        }

        gases.add(gas);
        if (!level.isClientSide()) {
            PacketDistributor.sendToAllPlayers(new UpdateAtmosGas(uuid, gas));
        }
    }

    /**
     * Checks if this atmosphere instance contains a specific gas.
     * @param gas The Substance to check for.
     * @return True if the gas is present, false otherwise.
     */
    public boolean containsGas(Substance gas) {
        return gases.stream().anyMatch(stack -> stack.getSubstance().equals(gas));
    }
}
