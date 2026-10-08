package com.site21.bittermelon.common.systems.substance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.common.systems.chemistry.Reaction;
import com.site21.bittermelon.common.systems.chemistry.ReactionManager;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluid;
import com.site21.bittermelon.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SubstanceMixture implements SubstanceContainer {
    public static final Codec<SubstanceMixture> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, SubstanceMixture> STREAM_CODEC;

    private final List<SubstanceStack> substances;
    private final List<Reaction> activeReactions;
    private final List<Reaction> cachedReactions;
    private boolean reactionsDirty = false;
    private boolean searchDirty = false;
    private float temperature; // in Kelvin
    private Runnable onChanged = () -> {};

    private int cachedVolume = -1;
    private int cachedColor = -1;
    private int cachedViscosity = -1;

    public SubstanceMixture(List<SubstanceStack> substances, float temperature) {
        this.substances = new ArrayList<>(substances);
        activeReactions = new ArrayList<>();
        cachedReactions = new ArrayList<>();
        this.temperature = temperature;
    }

    public SubstanceMixture(Runnable onChanged) {
        this(new ArrayList<>(), 273.15f);
        this.onChanged = onChanged;
    }

    public SubstanceMixture() {
        this(new ArrayList<>(), 273.15f);
    }

    public void setChangedCallback(Runnable callback) {
        this.onChanged = callback;
    }

    public void markReactionsDirty() {
        reactionsDirty = true;
    }

    public void markSearchDirty() {
        searchDirty = true;
    }

    public void tickReactions(Level level, BlockPos pos) {
        if (searchDirty) {
            searchReactions(this, level, pos);
            searchDirty = false;
            reactionsDirty = false;
        } else if (reactionsDirty) {
            refreshReactions();
            reactionsDirty = false;
        }

        Iterator<Reaction> iterator = activeReactions.iterator();
        while (iterator.hasNext()) {
            Reaction reaction = iterator.next();
            if (!reaction.react(this, level, pos)) {
                cachedReactions.add(reaction);
                iterator.remove();
            }
        }
    }

    /**
     * Reactions are cached when they fail to react, which can happen if their conditions are no longer met.
     * This method moves all cached reactions back to the active list, allowing them to be re-evaluated in the next tick.
     * This is important to ensure that reactions can occur again if the conditions become favorable,
     * without needing to be re-searched.
     */
    public void refreshReactions() {
        activeReactions.addAll(cachedReactions);
        cachedReactions.clear();
    }

    public void searchReactions(SubstanceContainer container, Level level, BlockPos pos) {
        activeReactions.clear();
        cachedReactions.clear();
        if (substances.isEmpty()) return;

        activeReactions.addAll(ReactionManager.getInstance().findMatch(container, level, pos));
    }

    @Override
    public void refresh() {
        cachedVolume = -1;
        cachedColor = -1;
        cachedViscosity = -1;
        onChanged.run();
    }

    public List<SubstanceStack> getSubstances() {
        return substances;
    }

    public void setSubstances(List<SubstanceStack> newSubstances) {
        substances.clear();
        substances.addAll(newSubstances);
        refresh();
        markSearchDirty();
    }

    public void updateSubstance(SubstanceStack substance) {
        for (SubstanceStack stack : substances) {
            if (stack.canMergeWith(substance)) {
                stack.modifyAmount(substance.getAmount());
                refresh();
                markReactionsDirty();
                return;
            }
        }
        substances.add(substance);
        refresh();
        markSearchDirty();
    }

    @Override
    public void updateSubstanceNoUpdate(SubstanceStack substance) {
        for (SubstanceStack stack : substances) {
            if (stack.canMergeWith(substance)) {
                stack.modifyAmount(substance.getAmount());
                markReactionsDirty();
                return;
            }
        }
        substances.add(substance);
        markSearchDirty();
    }

    public void transferSubstances(@NotNull List<SubstanceStack> newSubstances) {
        for (SubstanceStack stack : newSubstances) {
            updateSubstanceNoUpdate(stack.copy());
        }
        refresh();
    }

    public void removeSubstance(SubstanceStack substance, int amount) {
        for (SubstanceStack stack : substances) {
            if (substance.canMergeWith(stack)) {
                stack.modifyAmount(-amount);
                if (stack.getAmount() <= 0) {
                    substances.remove(stack);
                    markSearchDirty();
                }
                refresh();
                return;
            }
        }
    }

    @Override
    public void removeSubstanceNoUpdate(SubstanceStack substance, int amount) {
        for (SubstanceStack stack : substances) {
            if (substance.canMergeWith(stack)) {
                stack.modifyAmount(-amount);
                if (stack.getAmount() <= 0) {
                    substances.remove(stack);
                }
                return;
            }
        }
    }

    public void removeSubstances(@NotNull List<SubstanceStack> toRemove) {
        removeSubstances(toRemove, 1);
    }

    public void removeSubstances(List<SubstanceStack> toRemove, int multiplier) {
        for (SubstanceStack stack : toRemove) {
            removeSubstanceNoUpdate(stack, stack.getAmount() * multiplier);
        }
        refresh();
    }

    public void mergeSubstances(@NotNull List<SubstanceStack> newSubstances, SubstanceStack stack) {
        for (SubstanceStack substance : newSubstances) {
            if (stack.canMergeWith(substance)) {
                substance.modifyVolume(stack.getVolume());
                return;
            }
        }
        newSubstances.add(stack);
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void modifyTemperature(float delta) {
        temperature = Math.max(0, temperature + delta);
    }

    public int getVolume() {
        if (cachedVolume == -1) {
            cachedVolume = 0;
            for (SubstanceStack stack : substances) {
                cachedVolume += stack.getVolume();
            }
        }
        return cachedVolume;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getColor() {
        if (cachedColor == -1) {
            if (substances.isEmpty()) {
                cachedColor = Substance.DEFAULT_COLOR;
            } else {
                Map<Integer, Integer> colors = new HashMap<>(substances.size());
                for (SubstanceStack stack : substances) {
                    colors.put(stack.getSubstance().getColor(), stack.getVolume());
                }
                cachedColor = ColorUtil.mixColors(colors);
            }
        }
        return cachedColor;
    }

    public int getViscosity() {
        if (cachedViscosity == -1) {
            int weightedSum = 0;
            int totalVolume = 0;
            for (SubstanceStack stack : substances) {
                int volume = stack.getVolume();
                int viscosity = stack.getSubstance().getViscosity();
                weightedSum += viscosity * volume;
                totalVolume += volume;
            }
            if (totalVolume <= 0) {
                cachedViscosity = 1000;
            } else {
                cachedViscosity = weightedSum / totalVolume;
            }
        }
        return cachedViscosity;
    }

    public String getContentsDescription() {
        if (substances.isEmpty()) {
            return "Empty";
        }
        return substances.stream()
                .map(entry -> String.format("%s: %d", entry.getSubstance().getName(), entry.getAmount()))
                .collect(Collectors.joining(", "));
    }

    /**
     * Calculates the pressure based on the fluid's volume.
     * The pressure is defined as the difference between the full block volume and the current volume of the fluid.
     *
     * @return The calculated pressure value.
     */
    public int getPressure() {
        return getVolume() - SubstanceFluid.FULL_BLOCK_VOLUME;
    }

    public List<SubstanceStack> spreadSubstancesByVolume(int transferVolume) {
        float currentVolume = getVolume();

        if (currentVolume <= 0) {
            return Collections.emptyList();
        }

        List<SubstanceStack> spreadStacks = new ArrayList<>();
        float ratio = (float) transferVolume / currentVolume;

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

    public List<SubstanceStack> spreadSubstancesByPercentage(float percentage) {
        return spreadSubstancesByVolume((int) (getVolume() * percentage));
    }

    public List<SubstanceStack> splitSubstances(int spreadCount, int pruneThreshold) {
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

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SubstanceStack.CODEC.listOf().fieldOf("substances").forGetter(SubstanceMixture::getSubstances),
                Codec.FLOAT.fieldOf("temperature").forGetter(SubstanceMixture::getTemperature)
        ).apply(instance, SubstanceMixture::new));

        STREAM_CODEC = StreamCodec.composite(
                SubstanceStack.STREAM_CODEC.apply(ByteBufCodecs.list()), SubstanceMixture::getSubstances,
                ByteBufCodecs.FLOAT, SubstanceMixture::getTemperature,
                SubstanceMixture::new
        );
    }
}
