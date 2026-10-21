package com.site21.bittermelon.util;

import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluid;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlockEntity;
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
     * Spill a list of substances into the world at the given position.
     * If the block at the position can be replaced and there is no fluid present, it will be replaced with a SubstanceFluid block.
     * The substances will then be transferred to the SubstanceFluidBlockEntity at that position.
     * @param level the level to spill the substances into
     * @param pos the position to spill the substances at
     * @param substances the list of substances to spill
     * @return true if the substances were successfully spilled, false otherwise
     */
    public static boolean spill(Level level, BlockPos pos, List<SubstanceStack> substances) {
        if (substances.isEmpty()) return false;

        SubstanceFluid fluid = SUBSTANCE_FLUID.get();

        if (level.getBlockState(pos).canBeReplaced() && level.getFluidState(pos).isEmpty()) {
            level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), Block.UPDATE_NONE);
        }

        return transferToSubstanceFluid(level, pos, substances);
    }

    /**
     * Replace the block at the given position with a SubstanceFluid block and transfer the given substances to it.
     * @param level the level to replace the block in
     * @param pos the position to replace with a SubstanceFluid block
     * @param substances the list of substances to transfer
     * @return true if the substances were successfully transferred, false otherwise
     */
    public static boolean replaceWithSubstanceFluid(Level level, BlockPos pos, List<SubstanceStack> substances) {
        if (substances.isEmpty()) return false;

        SubstanceFluid fluid = SUBSTANCE_FLUID.get();
        level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), Block.UPDATE_NONE);

        return transferToSubstanceFluid(level, pos, substances);
    }

    /**
     * Transfer the given substances to the SubstanceFluidBlockEntity at the given position, if it exists.
     * @param level the level to transfer the substances in
     * @param pos the position of the SubstanceFluidBlockEntity
     * @param substances the list of substances to transfer
     * @return true if the substances were successfully transferred, false otherwise
     */
    public static boolean transferToSubstanceFluid(Level level, BlockPos pos, List<SubstanceStack> substances) {
        if (level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity spreadBE) {
            spreadBE.getMixture().transferSubstances(substances);
            return true;
        }

        return false;
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
