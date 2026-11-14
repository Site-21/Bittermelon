package com.site21.bittermelon.common.systems.atmosphere;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.util.SubstanceUtil;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.site21.bittermelon.util.SubstanceUtil.GAS_CONSTANT;

public class AtmosZone {
    public static final Codec<AtmosZone> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(AtmosZone::getId),
            Codec.FLOAT.fieldOf("temperature").forGetter(AtmosZone::getTemperature),
            SubstanceStack.CODEC.listOf().fieldOf("gases").forGetter(AtmosZone::getGases),
            Codec.INT.fieldOf("blocks").forGetter(AtmosZone::getBlocks)
    ).apply(instance, AtmosZone::new));

    private final int id;
    private final List<SubstanceStack> gases;
    private float temperature; // Kelvin
    private int blocks;

    public AtmosZone(int id, float initialTemp, List<SubstanceStack> initialGasses, int blocks) {
        this.id = id;
        this.gases = new ArrayList<>(initialGasses);
        this.temperature = initialTemp;
        this.blocks = blocks;
    }

    public AtmosZone(int id, float initialTemp, List<SubstanceStack> initialGasses) {
        this(id, initialTemp, initialGasses, 0);
    }

    public AtmosZone(int id, int blocks) {
        this(id, 293.15f, new ArrayList<>(), blocks);
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
        if (blocks == 0) return 0;
        return SubstanceUtil.getTotalAmount(gases) * GAS_CONSTANT * temperature / (blocks * 1000);
    }

    public int getId() {
        return id;
    }

    public void addBlock() {
        blocks++;
    }

    public void removeBlock() {
        blocks--;
    }

    public void removeBlocks(int count) {
        blocks -= count;
    }

    /**
     * Gets the set of blocks associated with this atmosphere instance.
     *
     * @return A LongSet of packed block positions.
     */
    public int getBlocks() {
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
    }

    /**
     * Merges another atmosphere instance into this one, averaging temperatures and combining gases.
     * Notifies all clients of the changes.
     *
     * @param other The other atmosphere instance to merge.
     * @param level The level in which the merge is occurring.
     */
    public void merge(@NotNull AtmosZone other, Level level) {
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
                return;
            }
        }

        gases.add(gas);
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
