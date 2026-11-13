package com.site21.bittermelon.common.systems.substance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SubstanceStack implements DataComponentHolder, MutableDataComponentHolder {
    public static final Codec<SubstanceStack> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, SubstanceStack> STREAM_CODEC;

    public static final SubstanceStack EMPTY = new SubstanceStack(null);
    private final Substance substance;
    private int amount; // in moles
    private final PatchedDataComponentMap components;

    public SubstanceStack(Substance substance, int amount, PatchedDataComponentMap components) {
        this.substance = substance;
        this.amount = amount;
        this.components = components;
    }

    public SubstanceStack(Substance substance, int amount) {
        this(substance, amount, new PatchedDataComponentMap(substance.components()));
    }

    public SubstanceStack(Holder<Substance> tag, int amount) {
        this(tag.value(), amount);
    }

    public SubstanceStack(Holder<Substance> tag, int amount, DataComponentPatch components) {
        this(tag.value(), amount, PatchedDataComponentMap.fromPatch(tag.value().components(), components));
    }

    public SubstanceStack(Void unused) {
        substance = null;
        components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
    }

    public Substance getSubstance() {
        return substance;
    }

    public Holder<Substance> getSubstanceHolder() {
        return this.getSubstance().builtInRegistryHolder();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void modifyAmount(int delta) {
        amount = Math.max(0, amount + delta);
    }

    public int getVolume() {
        return Mth.ceil(amount * substance.getMolarVolume() / 1000);
    }

    public void setVolume(int volume) {
        amount = (int) (volume / substance.getMolarVolume() * 1000);
    }

    public void modifyVolume(int delta) {
        setVolume(Math.max(0, getVolume() + delta));
    }

    /**
     * Checks if this SubstanceStack is empty (amount is zero or less).
     * @return True if the SubstanceStack is empty, false otherwise.
     */
    public boolean isEmpty() {
        return amount <= 0;
    }

    /**
     * Creates a copy of this SubstanceStack. If the stack is empty, returns SubstanceStack.EMPTY.
     * @return A copy of this SubstanceStack.
     */
    public SubstanceStack copy() {
        return new SubstanceStack(getSubstance(), getAmount(), components.copy());

        // TODO: Uncomment when we figure out why substancestacks are empty
//        if (this.isEmpty()) {
//            return EMPTY;
//        } else {
//            return new SubstanceStack(getSubstance(), getAmount(), getTemperature(), components.copy());
//        }
    }

    /**
     * Checks if this SubstanceStack can be merged with another SubstanceStack.
     * Two SubstanceStacks can be merged if they have the same substance and the same components.
     * @param other The other SubstanceStack to check against.
     * @return True if the two SubstanceStacks can be merged, false otherwise.
     */
    public boolean canMergeWith(SubstanceStack other) {
        if (this == other) {
            return true;
        } else {
            return Objects.equals(components, other.components) && substance == other.getSubstance();
        }
    }

    /**
     * Checks if two SubstanceStacks are exactly the same, including amount, temperature, substance, and components.
     * @param stack The first SubstanceStack to compare.
     * @param other The second SubstanceStack to compare.
     * @return True if the two SubstanceStacks are exactly the same, false otherwise.
     */
    public static boolean matches(SubstanceStack stack, SubstanceStack other) {
        if (stack == other) {
            return true;
        } else {
            return stack.amount == other.amount && isSameSubstanceSameComponents(stack, other);
        }
    }

    /**
     * Checks if two SubstanceStacks have the same substance and components, ignoring amount and temperature.
     * @param stack The first SubstanceStack to compare.
     * @param other The second SubstanceStack to compare.
     * @return True if the two SubstanceStacks have the same substance and components, false otherwise.
     */
    public static boolean isSameSubstanceSameComponents(SubstanceStack stack, SubstanceStack other) {
        if (stack == other) {
            return true;
        } else {
            return Objects.equals(stack.components, other.components) && stack.substance == other.substance;
        }
    }

    /**
     * Checks if two lists of SubstanceStacks are exactly the same, including order, amount, temperature, substance, and components.
     * @param list The first list of SubstanceStacks to compare.
     * @param other The second list of SubstanceStacks to compare.
     * @return True if the two lists of SubstanceStacks are exactly the same, false otherwise.
     */
    public static boolean listMatches(@NotNull List<SubstanceStack> list, @NotNull List<SubstanceStack> other) {
        if (list.size() != other.size()) {
            return false;
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (!matches(list.get(i), other.get(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Generates a hash code for a list of SubstanceStacks, considering substance and components only.
     * @param list The list of SubstanceStacks to hash.
     * @return The hash code of the list.
     */
    public static int hashStackList(@NotNull List<SubstanceStack> list) {
        int i = 0;

        for (SubstanceStack stack : list) {
            i = i * 31 + hashSubstanceAndComponents(stack);
        }

        return i;
    }

    /**
     * Generates a hash code for a SubstanceStack, considering substance and components only.
     * @param stack The SubstanceStack to hash.
     * @return The hash code of the SubstanceStack.
     */
    public static int hashSubstanceAndComponents(@javax.annotation.Nullable SubstanceStack stack) {
        if (stack != null) {
            int i = 31 + stack.getSubstance().hashCode();
            return 31 * i + stack.getComponents().hashCode();
        } else {
            return 0;
        }
    }

    @Override
    public <T> @Nullable T set(@NotNull DataComponentType<T> component, @Nullable T value) {
        return components.set(component, value);
    }

    @Override
    public <T> @Nullable T remove(@NotNull DataComponentType<? extends T> component) {
        return components.remove(component);
    }

    @Override
    public void applyComponents(@NotNull DataComponentPatch patch) {
        components.applyPatch(patch);
    }

    @Override
    public void applyComponents(@NotNull DataComponentMap components) {
        this.components.setAll(this.components);
    }

    @Override
    public @NotNull DataComponentMap getComponents() {
        return isEmpty() ? DataComponentMap.EMPTY : components;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Substance.CODEC
                        .fieldOf("substance")
                        .forGetter(SubstanceStack::getSubstanceHolder),
                ExtraCodecs.NON_NEGATIVE_INT
                        .fieldOf("amount")
                        .forGetter(SubstanceStack::getAmount),
                DataComponentPatch.CODEC
                        .optionalFieldOf("components", DataComponentPatch.EMPTY)
                        .forGetter(stack -> stack.components.asPatch())
        ).apply(instance, SubstanceStack::new));

        STREAM_CODEC = StreamCodec.composite(
                Substance.STREAM_CODEC, SubstanceStack::getSubstanceHolder,
                ByteBufCodecs.INT, SubstanceStack::getAmount,
                DataComponentPatch.STREAM_CODEC, stack -> stack.components.asPatch(),
                SubstanceStack::new
        );
    }
}
