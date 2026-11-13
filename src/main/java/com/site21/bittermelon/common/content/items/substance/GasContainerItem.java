package com.site21.bittermelon.common.content.items.substance;

import com.site21.bittermelon.common.systems.atmosphere.AtmosHandler;
import com.site21.bittermelon.common.systems.atmosphere.AtmosZone;
import com.site21.bittermelon.common.systems.component.SubstanceContents;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.util.SubstanceUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

import static com.site21.bittermelon.init.custom.Substances.OXYGEN;
import static com.site21.bittermelon.init.neoforge.BitterDataComponents.*;

public class GasContainerItem extends SubstanceContainerItem {
    public GasContainerItem(Properties properties) {
        super(properties);
    }

    public int getReleasePressure(@NotNull ItemStack stack) {
        return stack.getOrDefault(RELEASE_PRESSURE.get(), 0);
    }

    public void setReleasePressure(@NotNull ItemStack stack, int releasePressure) {
        stack.set(RELEASE_PRESSURE.get(), Mth.clamp(releasePressure, 0, getMaxReleasePressure(stack)));
    }

    public int getMaxReleasePressure(ItemStack stack) {
        return 10;
    }


    public float getMaxPressure(@NotNull ItemStack stack) {
        return stack.getOrDefault(MAX_PRESSURE, 0.0f);
    }

    public float getPressure(ItemStack stack) {
        return SubstanceUtil.getPressure(getSubstanceData(stack).substances(), getMaxPressure(stack), stack.getOrDefault(TEMPERATURE, 293));
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.min((int) ((long) getPressure(stack) * MAX_BAR_WIDTH / getMaxPressure(stack)), MAX_BAR_WIDTH);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float pressure = getPressure(stack);
        float fillPercentage = pressure / getMaxPressure(stack);

        if (pressure > getMaxPressure(stack)) {
            // Purple/magenta color
            return 0xFF00FF;
        }

        if (fillPercentage < 0.5f) {
            return 0xFF0000 | (Math.round(510 * fillPercentage) << 8); // Red to Yellow
        } else {
            return 0x00FF00 | (Math.round(510 * (1 - fillPercentage)) << 16); // Yellow to Green
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (level.isClientSide()) return;

        if (getPressure(stack) > getMaxPressure(stack) * 1.10f) {
            level.explode(entity, entity.getX(), entity.getY(), entity.getZ(),
                    2.0f,
                    Level.ExplosionInteraction.TNT);

            AtmosHandler.releaseGas(level, entity.getOnPos(), getContents(stack));
            stack.setCount(0);
        }

        if (getReleasePressure(stack) > 0 && getTotalAmount(stack) > 0) {
            transferGas(stack, level, entity);
        }
    }

    public void transferGas(ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity) {
        if (level.isClientSide()) return;

        AtmosZone atmosZone = AtmosHandler.getZoneAt(level, entity.getOnPos());
        if (atmosZone == null) {
            AtmosHandler.addAtmosphere(level, entity.getOnPos(), 293.15f, List.of(new SubstanceStack(OXYGEN.get(), 1)));
            atmosZone = AtmosHandler.getZoneAt(level, entity.getOnPos());
        }

        if (atmosZone == null || atmosZone.getPressure() <= 0 || getReleasePressure(stack) < atmosZone.getPressure()) return;

        int totalVolume = getTotalVolume(stack);
        int transferVolume = (int) (((getReleasePressure(stack) * totalVolume) / atmosZone.getPressure()) / 100);

        // TODO: Propulsion when transfer volume is too high

        SubstanceContents.Mutable mutableData = getMutableSubstanceData(stack);
        Iterator<SubstanceStack> iterator = mutableData.substances.iterator();

        while (iterator.hasNext()) {
            SubstanceStack substance = iterator.next();
            if (substance == null) continue;

            int proportion = totalVolume > 0 ? substance.getVolume() / totalVolume : 0;
            int proportionalTransferVolume = Math.min(transferVolume * proportion, substance.getVolume());

            if (proportionalTransferVolume > 0) {
                SubstanceStack transferredSubstance = substance.copy();
                transferredSubstance.setVolume(proportionalTransferVolume);
                atmosZone.updateGas(transferredSubstance, entity.level());

                substance.modifyVolume(-proportionalTransferVolume);
                if (substance.getVolume() <= 0.001f) {
                    iterator.remove();
                }
            }
        }

        setSubstanceDataFromMutable(stack, mutableData);
    }
}