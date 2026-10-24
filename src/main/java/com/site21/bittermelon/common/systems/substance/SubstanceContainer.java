package com.site21.bittermelon.common.systems.substance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public interface SubstanceContainer {
    void updateSubstance(SubstanceStack stack);

    void updateSubstanceNoUpdate(SubstanceStack stack);

    void removeSubstance(SubstanceStack stack, int amount);

    void removeSubstanceNoUpdate(SubstanceStack stack, int amount);

    void refresh();

    int getVolume();

    float getTemperature();

    void setTemperature(float temperature);

    void modifyTemperature(float delta);

    void modifyTemperatureNoUpdate(float delta);

    Collection<SubstanceStack> getSubstances();

    default EnumSet<Nature> getNatures() {
        EnumSet<Nature> natures = EnumSet.noneOf(Nature.class);
        for (SubstanceStack stack : getSubstances()) {
            natures.addAll(stack.getSubstance().getNature().keySet());
        }
        return natures;
    }

    default List<SubstanceStack> transferSubstancesByVolume(int volume) {
        List<SubstanceStack> transferred = new ArrayList<>();
        int actualVolume = Math.min(volume, getVolume());
        float ratio = (float) actualVolume / getVolume();
        for (SubstanceStack stack : getSubstances()) {
            int amountToTransfer = (int) (stack.getAmount() * ratio);
            if (amountToTransfer > 0) {
                SubstanceStack newStack = stack.copy();
                newStack.setAmount(amountToTransfer);
                transferred.add(newStack);
                removeSubstanceNoUpdate(stack, amountToTransfer);
            }
        }

        refresh();
        return transferred;
    }

    /**
     * Calculates the total heat capacity of the substances in the container.
     * @return The total heat capacity in J/K.
     */
    default float getHeatCapacity() {
        float capacity = 0;
        for (SubstanceStack stack : getSubstances()) {
            capacity += stack.getAmount() * stack.getSubstance().getMolarHeatCapacity();
        }
        return capacity;
    }
}
