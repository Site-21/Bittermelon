package com.site21.bittermelon.common.content.blocks.electronics.environmentsensor;

import com.site21.bittermelon.common.systems.atmosphere.AtmosHandler;
import com.site21.bittermelon.common.systems.atmosphere.AtmosZone;
import com.site21.bittermelon.common.systems.electronics.ElectronicDevice;
import com.site21.bittermelon.common.content.blocks.electronics.ElectronicBlockEntity;
import com.site21.bittermelon.common.systems.electronics.wiring.OutputPort;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.site21.bittermelon.init.neoforge.BitterBlockEntities.ENVIRONMENT_SENSOR_BLOCK_ENTITY;

public class EnvironmentSensorBlockEntity extends ElectronicBlockEntity implements ElectronicDevice {
    private static final float FALLBACK_TEMPERATURE = 22;
    private static final float FALLBACK_PRESSURE = 101.325f;
    private final Map<String, OutputPort> outputPorts = new HashMap<>();
    private float temperature = 0; // Kelvin
    private float pressure = 0; // kPa
    private final Set<SubstanceStack> gases = new HashSet<>();

    public EnvironmentSensorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ENVIRONMENT_SENSOR_BLOCK_ENTITY.get(), pos, blockState);
        initializePorts();
    }

    private void initializePorts() {
        OutputPort TEMPERATURE = new OutputPort("temperature", this::getTemperature, worldPosition);
        OutputPort PRESSURE = new OutputPort("pressure", this::getPressure, worldPosition);

        outputPorts.put(TEMPERATURE.id, TEMPERATURE);
        outputPorts.put(PRESSURE.id, PRESSURE);
    }

    public void tick() {
        if (level == null) return;
        AtmosZone atmosZone = AtmosHandler.getAtmosInstanceAt(level, worldPosition.above());
        // TODO: Remove above once shape is fixed
        if (atmosZone == null) {
            if (temperature != FALLBACK_TEMPERATURE || pressure != FALLBACK_PRESSURE) {
                temperature = FALLBACK_TEMPERATURE;
                pressure = FALLBACK_PRESSURE;
                setChanged();
            }
            return;
        }
        float temperature = atmosZone.getTemperature();
        float pressure = atmosZone.getPressure();
        boolean changed = false;

        if (temperature != this.temperature) {
            this.temperature = temperature;
            changed = true;
        }

        if (pressure != this.pressure) {
            this.pressure = pressure;
            changed = true;
        }

        if (!gases.containsAll(atmosZone.getGases())) {
            gases.addAll(atmosZone.getGases());
            changed = true;
        }

        if (changed) setChanged();

        for (OutputPort outputPort : outputPorts.values()) {
            outputPort.update(level);
        }
    }

    @Override
    public Map<String, OutputPort> getOutputPorts() {
        return outputPorts;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getPressure() {
        return pressure;
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);

        output.putFloat("temperature", temperature);
        output.putFloat("pressure", pressure);

        saveOutputPorts(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);

        temperature = input.getFloatOr("temperature", 0.0f);
        pressure = input.getFloatOr("pressure", 0.0f);

        loadOutputPorts(input);
    }
}
