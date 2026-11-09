package com.site21.bittermelon.util;

import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluid;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlockEntity;
import com.site21.bittermelon.common.systems.substance.SubstanceContainer;
import com.site21.bittermelon.common.systems.substance.SubstanceMixture;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.site21.bittermelon.init.neoforge.BitterFluids.SUBSTANCE_FLUID;

public final class SubstanceUtil {
    public static final float GAS_CONSTANT = 8.3144f; // 8.3144 L * kPa / K * mol

    /**
     * Get the total amount of substance in a list of SubstanceStacks.
     * @param substances the list of SubstanceStacks
     * @return the total amount of substance in moles
     */
    public static int getTotalVolume(@NotNull List<SubstanceStack> substances) {
        return substances.stream()
                .map(SubstanceStack::getVolume)
                .reduce(0, Integer::sum);
    }

    /**
     * Calculate the pressure of an ideal gas using the Ideal Gas Law: Point = nRT/V
     * @param substances the list of SubstanceStacks
     * @param volume the volume in liters
     * @param temperature the temperature in Kelvin
     * @return the pressure in kPa
     */
    public static float getPressure(List<SubstanceStack> substances, float volume, float temperature) {
        return SubstanceUtil.getTotalVolume(substances) * GAS_CONSTANT * temperature / volume;
    }

    /**
     * Spill the contents of a SubstanceContainer into the world at the given position, using a default temperature of 273.15 K.
     * @param level the level to spill the substances into
     * @param pos the position to spill the substances at
     * @param substances the list of substances to spill
     * @return true if the substances were successfully spilled, false otherwise
     */
    public static boolean spill(Level level, BlockPos pos, List<SubstanceStack> substances) {
        return spill(level, pos, substances, 273.15f);
    }

    /**
     * Spill the contents of a SubstanceContainer into the world at the given position.
     * @param level the level to spill the substances into
     * @param pos the position to spill the substances at
     * @param container the SubstanceContainer containing the substances to spill
     * @return true if the substances were successfully spilled, false otherwise
     */
    public static boolean spill(Level level, BlockPos pos, SubstanceContainer container) {
        return spill(level, pos, container.getSubstances(), container.getTemperature());
    }

    /**
     * Spill a list of substances into the world at the given position.
     * If the block at the position can be replaced and there is no fluid present, it will be replaced with a SubstanceFluid block.
     * The substances will then be transferred to the SubstanceFluidBlockEntity at that position.
     * @param level the level to spill the substances into
     * @param pos the position to spill the substances at
     * @param substances the list of substances to spill
     * @param temperature the temperature of the substances
     * @return true if the substances were successfully spilled, false otherwise
     */
    public static boolean spill(Level level, BlockPos pos, List<SubstanceStack> substances, float temperature) {
        if (substances.isEmpty()) return false;

        SubstanceFluid fluid = SUBSTANCE_FLUID.get();

        if (level.getBlockState(pos).canBeReplaced() && level.getFluidState(pos).isEmpty()) {
            level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), Block.UPDATE_NONE);
        }

        return transferToSubstanceFluid(level, pos, substances, temperature);
    }

    /**
     * Replace the block at the given position with a SubstanceFluid block and transfer the given substances to it.
     * @param level the level to replace the block in
     * @param pos the position to replace with a SubstanceFluid block
     * @param container the SubstanceContainer containing the substances to transfer
     * @return true if the substances were successfully transferred, false otherwise
     */
    public static boolean replaceWithSubstanceFluid(Level level, BlockPos pos, SubstanceContainer container) {
        if (container.getSubstances().isEmpty()) return false;

        SubstanceFluid fluid = SUBSTANCE_FLUID.get();
        level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), Block.UPDATE_NONE);

        return transferToSubstanceFluid(level, pos, container.getSubstances(), container.getTemperature());
    }

    /**
     * Transfer the given substances to the SubstanceFluidBlockEntity at the given position, if it exists.
     * @param level the level to transfer the substances in
     * @param pos the position of the SubstanceFluidBlockEntity
     * @param substances the list of substances to transfer
     * @param temperature the temperature of the substances
     * @return true if the substances were successfully transferred, false otherwise
     */
    public static boolean transferToSubstanceFluid(Level level, BlockPos pos, List<SubstanceStack> substances, float temperature) {
        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity spreadBE) {
            SubstanceMixture mixture = spreadBE.getMixture();
            mixture.setTemperatureNoUpdate(temperatureForSpread(substances, temperature, mixture.getSubstances(), mixture.getTemperature()));
            mixture.transferSubstances(substances);
            return true;
        }

        return false;
    }

    /**
     * Calculate the resulting temperature when spreading substances from one list to another, based on their heat capacities and temperatures.
     * If either list has a heat capacity of 0, the temperature of the "from" substances is returned.
     * @param fromSubstances the list of substances being spread from
     * @param fromTemperature the temperature of the "from" substances
     * @param toSubstances the list of substances being spread to
     * @param toTemperature the temperature of the "to" substances
     * @return the resulting temperature after spreading the substances
     */
    public static float temperatureForSpread(List<SubstanceStack> fromSubstances, float fromTemperature, List<SubstanceStack> toSubstances, float toTemperature) {
        float fromHeatCapacity = getHeatCapacity(fromSubstances);
        float toHeatCapacity = getHeatCapacity(toSubstances);

        if (fromHeatCapacity <= 0 || toHeatCapacity <= 0) return fromTemperature;

        float totalHeatCapacity = fromHeatCapacity + toHeatCapacity;
        return (fromTemperature * fromHeatCapacity + toTemperature * toHeatCapacity) / totalHeatCapacity;
    }

    public static float getHeatCapacity(List<SubstanceStack> substances) {
        float capacity = 0;
        for (SubstanceStack stack : substances) {
            capacity += stack.getAmount() * stack.getSubstance().getMolarHeatCapacity();
        }
        return capacity;
    }

    /**
     * Spread a list of substances by volume, returning a new list of SubstanceStacks with the same substances but with amounts scaled by the ratio of spreadVolume to totalVolume.
     * @param substances the list of substances to spread
     * @param totalVolume the total volume of the substances
     * @param spreadVolume the volume to spread the substances to
     * @return a new list of SubstanceStacks with the same substances but with amounts scaled by the ratio of spreadVolume to totalVolume
     */
    public static List<SubstanceStack> spreadSubstancesByVolume(List<SubstanceStack> substances, int totalVolume, int spreadVolume) {
        if (totalVolume <= 0) return Collections.emptyList();

        List<SubstanceStack> spreadStacks = new ArrayList<>();
        float ratio = (float) spreadVolume / totalVolume;

        for (SubstanceStack stack : substances) {
            int amount = (int) (stack.getAmount() * ratio);

            if (amount > 0) {
                SubstanceStack spreadStack = stack.copy();
                spreadStack.setAmount(amount);
                spreadStacks.add(spreadStack);
            }
        }

        return spreadStacks;
    }

    /**
     * Spread a list of substances by percentage, returning a new list of SubstanceStacks with the same substances but with amounts scaled by the given percentage.
     * @param substances the list of substances to spread
     * @param totalVolume the total volume of the substances
     * @param percentage the percentage to spread the substances by
     * @return a new list of SubstanceStacks with the same substances but with amounts scaled by the given percentage
     */
    public static List<SubstanceStack> spreadSubstancesByPercentage(List<SubstanceStack> substances, int totalVolume, float percentage) {
        return spreadSubstancesByVolume(substances, totalVolume, (int) (totalVolume * percentage));
    }

    /**
     * Split a list of substances into a given number of stacks, pruning any stacks that would have an amount below the given threshold.
     * @param substances the list of substances to split
     * @param spreadCount the number of stacks to split the substances into
     * @param pruneThreshold the minimum amount for a stack to be included in the result
     * @return a new list of SubstanceStacks with the same substances but with amounts scaled by the ratio of 1/spreadCount, excluding any stacks below the pruneThreshold
     */
    public static List<SubstanceStack> splitSubstances(List<SubstanceStack> substances, int spreadCount, int pruneThreshold) {
        List<SubstanceStack> spreadStacks = new ArrayList<>();

        for (SubstanceStack stack : substances) {
            int amount = stack.getAmount() / spreadCount;

            if (amount > pruneThreshold) {
                SubstanceStack spreadStack = stack.copy();
                spreadStack.setAmount(amount);
                spreadStacks.add(spreadStack);
            }
        }

        return spreadStacks;
    }
}
