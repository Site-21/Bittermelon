package com.site21.bittermelon.common.content.blocks.electronics.thermostat;

import com.site21.bittermelon.common.systems.atmosphere.AtmosHandler;
import com.site21.bittermelon.common.systems.atmosphere.AtmosZone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ThermostatBlockEntity extends BlockEntity {
    private float temperature = 0;
    private float targetTemperature = 22;

    public ThermostatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void tick() {
        if (level == null) return;
        AtmosZone atmosZone = AtmosHandler.getZoneAt(level, worldPosition);
        if (atmosZone == null) {
            if (temperature != 22) {
                temperature = 22;
                setChanged();
            }
            return;
        }
        float temperature = atmosZone.getTemperature();

        if (temperature != this.temperature) {
            this.temperature = atmosZone.getTemperature();
            setChanged();
        }
    }
}
