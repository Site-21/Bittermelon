package com.site21.bittermelon.common.content.items.substance;

import com.site21.bittermelon.common.content.items.base.BitterItem;
import com.site21.bittermelon.common.systems.component.SubstanceContents;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.site21.bittermelon.init.neoforge.BitterDataComponents.*;

public class SubstanceContainerItem extends BitterItem {
    public SubstanceContainerItem(Properties properties) {
        super(properties);
    }

    public SubstanceContents getSubstanceData(@NotNull ItemStack stack) {
        return stack.getOrDefault(SUBSTANCE_CONTENTS.get(), SubstanceContents.EMPTY);
    }

    public void setSubstanceDataFromMutable(@NotNull ItemStack stack, SubstanceContents.@NotNull Mutable mutableData) {
        stack.set(SUBSTANCE_CONTENTS.get(), mutableData.toImmutable());
        updateVisuals(stack);
    }

    public SubstanceContents.Mutable getMutableSubstanceData(@NotNull ItemStack stack) {
        SubstanceContents substanceContents = stack.getOrDefault(SUBSTANCE_CONTENTS.get(), SubstanceContents.EMPTY);
        return substanceContents.toMutable();
    }

    public List<SubstanceStack> getContents(ItemStack stack) {
        return getSubstanceData(stack).substances();
    }

    public int getTotalAmount(ItemStack stack) {
        return getSubstanceData(stack).getTotalAmount();
    }

    public int getTotalVolume(ItemStack stack) {
        return getSubstanceData(stack).getTotalVolume();
    }

    /**
     * Updates the substance in the item stack by adding or modifying the given substance stack.
     * @param itemStack the item stack to update
     * @param substanceStack the substance stack to add or modify
     */
    public void updateSubstance(ItemStack itemStack, SubstanceStack substanceStack) {
        if (substanceStack != null) {
            SubstanceContents.Mutable mutableData = getMutableSubstanceData(itemStack);
            mutableData.updateSubstance(substanceStack);
            setSubstanceDataFromMutable(itemStack, mutableData);
        }
    }

    public int getCapacity(@NotNull ItemStack stack) {
        return stack.getOrDefault(VOLUME, 0);
    }

    public boolean isContainerEmpty(ItemStack stack) {
        return getContents(stack).isEmpty();
    }

    /**
     * Consumes substances from the container based on the consume rate and the proportion of each substance.
     * @param stack The ItemStack representing the substance container.
     * @param consumeRate The total amount to consume from the container.
     * @param entity The LivingEntity consuming the substances.
     * @return The updated ItemStack after consumption.
     */
    public ItemStack consumeSubstances(ItemStack stack, int consumeRate, LivingEntity entity) {
        int totalAmount = getTotalVolume(stack);
        SubstanceContents.Mutable mutableData = getMutableSubstanceData(stack);
        Iterator<SubstanceStack> iterator = mutableData.substances.iterator();

        while (iterator.hasNext()) {
            SubstanceStack substance = iterator.next();

            int proportion = totalAmount > 0 ? substance.getVolume() / totalAmount : 0;
            int consumeAmount = Math.min(consumeRate * proportion, substance.getVolume());

            SubstanceStack consumedSubstance = substance.copy();
            consumedSubstance.setVolume(consumeAmount);
            consumedSubstance.getSubstance().onConsume(consumedSubstance, entity);

            substance.modifyVolume(-consumeAmount);

            if (substance.getVolume() <= consumeAmount) {
                iterator.remove();
            }
        }

        setSubstanceDataFromMutable(stack, mutableData);
        return stack;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !stack.getOrDefault(BAR_HIDDEN, false) && getTotalVolume(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return getSubstanceData(stack).getTotalVolume() * MAX_BAR_WIDTH / getCapacity(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fillPercentage = (float) getSubstanceData(stack).getTotalVolume() / getCapacity(stack);
        if (fillPercentage < 0.5f) {
            return 0xFF0000 | (Math.round(510 * fillPercentage) << 8); // Red to Yellow
        } else {
            return 0x00FF00 | (Math.round(510 * (1 - fillPercentage)) << 16);  // Yellow to Green
        }
    }

    protected Component getFlavorMessageComponent(ItemStack stack) {
        return getSensoryMessageComponent(
                stack,
                "Tastes",
                "Tastes like...",
                "Tastes like a noticeable blend of",
                "notes.",
                "Tastes strongly",
                Substance::getFlavor);
    }

    protected Component getSmellMessageComponent(ItemStack stack) {
        return getSensoryMessageComponent(
                stack,
                "Smells",
                "Smells...",
                "Smells noticeably",
                "",
                "Smells strongly",
                Substance::getSmell);
    }

    private Component getSensoryMessageComponent(ItemStack stack, String singleVerb, String multipleMain, String mediumPrefix, String mediumSuffix, String strongPrefix, Function<Substance, String> propertyGetter) {
        List<SubstanceStack> substances = getContents(stack);
        int totalVolume = getSubstanceData(stack).getTotalVolume();

        if (totalVolume == 0 || substances.isEmpty()) {
            return Component.empty();
        }

        if (substances.size() == 1) {
            String property = propertyGetter.apply(substances.getFirst().getSubstance());
            if (property.isEmpty()) return Component.empty();

            String description = singleVerb + " " + property + ".";
            return Component.literal(description).withStyle(ChatFormatting.GREEN);
        }

        MutableComponent mainComponent = Component.literal(multipleMain).withStyle(ChatFormatting.GREEN);

        String hoverText = substances.stream()
                .filter(substance -> {
                    String property = propertyGetter.apply(substance.getSubstance());
                    return property != null && !property.trim().isEmpty();
                })
                .map(substance -> {
                    int volume = substance.getVolume();
                    int percentageAmount = volume / totalVolume * 100;
                    String property = propertyGetter.apply(substance.getSubstance());

                    if (percentageAmount <= 35) {
                        return "Has a faint hint of " + property + " tones.";
                    } else if (percentageAmount <= 65) {
                        return mediumPrefix + " " + property + " " + mediumSuffix;
                    } else {
                        return strongPrefix + " " + property + ".";
                    }
                })
                .collect(Collectors.joining("\n"));

        return mainComponent.setStyle(mainComponent.getStyle().withHoverEvent(
                new HoverEvent.ShowText(Component.literal(hoverText))));
    }

    private void updateVisuals(@NotNull ItemStack stack) {
        stack.set(LAST_UPDATED.get(), System.currentTimeMillis());
    }
}
