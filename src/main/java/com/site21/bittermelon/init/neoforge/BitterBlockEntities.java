package com.site21.bittermelon.init.neoforge;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.blocks.drum.DrumBlockEntity;
import com.site21.bittermelon.common.content.blocks.burrow.BurrowBlockEntity;
import com.site21.bittermelon.common.content.blocks.container.smallbox.BoxBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.containmentalarm.ContainmentAlarmBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.containmentpanel.ContainmentPanelBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.detonator.DetonatorBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.environmentsensor.EnvironmentSensorBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.intercom.IntercomBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.keycardreader.KeycardReaderBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.largeslidingdoor.LargeSlidingDoorBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.personnelterminal.PersonnelTerminalBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.redstonedevice.RedstoneDeviceBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.securedoor.KeycardReaderSecureDoorBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.securedoor.SecureDoorBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.slidingdoor.SlidingDoorBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.speaker.SpeakerBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.television.TelevisionBlockEntity;
import com.site21.bittermelon.common.content.blocks.electronics.thermometer.ThermometerBlockEntity;
import com.site21.bittermelon.common.content.blocks.flamingo.FlamingoBlockEntity;
import com.site21.bittermelon.common.content.blocks.lights.emergencyexitlight.EmergencyExitLampBlockEntity;
import com.site21.bittermelon.common.content.blocks.powergrid.distributionboard.DistributionBoardBlockEntity;
import com.site21.bittermelon.common.content.blocks.scp.scp151.SCP151BlockEntity;
import com.site21.bittermelon.common.content.blocks.stickynote.StickyNoteBlockEntity;
import com.site21.bittermelon.common.content.blocks.wallwriting.WallWritingBlockEntity;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlockEntity;
import com.site21.bittermelon.common.systems.fluid.substance.SolidSubstanceBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.site21.bittermelon.init.neoforge.BitterBlocks.*;

public class BitterBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Bittermelon.MOD_ID);

    public static final Supplier<BlockEntityType<BoxBlockEntity>> BOX_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "box_block_entity",
            () -> new BlockEntityType<>(BoxBlockEntity::new, false, SMALL_CARDBOARD_BOX.get()));

    public static final Supplier<BlockEntityType<ContainmentPanelBlockEntity>> CONTAINMENT_PANEL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "containment_panel_block_entity",
            () -> new BlockEntityType<>(ContainmentPanelBlockEntity::new, false, CONTAINMENT_PANEL.get()));

    public static final Supplier<BlockEntityType<PersonnelTerminalBlockEntity>> PERSONNEL_TERMINAL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "personnel_terminal_block_entity",
            () -> new BlockEntityType<>(PersonnelTerminalBlockEntity::new, false, PERSONNEL_TERMINAL.get()));

    public static final Supplier<BlockEntityType<KeycardReaderBlockEntity>> KEYCARD_READER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "keycard_reader_block_entity",
            () -> new BlockEntityType<>(KeycardReaderBlockEntity::new, false, KEYCARD_READER.get()));

    public static final Supplier<BlockEntityType<ThermometerBlockEntity>> THERMOMETER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "thermometer_block_entity",
            () -> new BlockEntityType<>(ThermometerBlockEntity::new, false, THERMOMETER.get()));

    public static final Supplier<BlockEntityType<EnvironmentSensorBlockEntity>> ENVIRONMENT_SENSOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "environment_sensor_block_entity",
            () -> new BlockEntityType<>(EnvironmentSensorBlockEntity::new, false, ENVIRONMENT_SENSOR.get()));

    public static final Supplier<BlockEntityType<IntercomBlockEntity>> INTERCOM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "intercom_block_entity",
            () -> new BlockEntityType<>(IntercomBlockEntity::new, false, INTERCOM.get()));

    public static final Supplier<BlockEntityType<SpeakerBlockEntity>> SPEAKER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "speaker_block_entity",
            () -> new BlockEntityType<>(SpeakerBlockEntity::new, false, SPEAKER.get()));

    public static final Supplier<BlockEntityType<ContainmentAlarmBlockEntity>> CONTAINMENT_ALARM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "containment_alarm_block_entity",
            () -> new BlockEntityType<>(ContainmentAlarmBlockEntity::new, false, CONTAINMENT_ALARM.get()));

    public static final Supplier<BlockEntityType<DetonatorBlockEntity>> DETONATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "detonator_block_entity",
            () -> new BlockEntityType<>(DetonatorBlockEntity::new, false, DETONATOR.get()));

    public static final Supplier<BlockEntityType<SecureDoorBlockEntity>> SECURE_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "secure_door_block_entity",
            () -> new BlockEntityType<>(SecureDoorBlockEntity::new, false, SECURE_DOOR.get()));

    public static final Supplier<BlockEntityType<KeycardReaderSecureDoorBlockEntity>> KEYCARD_READER_SECURE_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "keycard_reader_secure_door_block_entity",
            () -> new BlockEntityType<>(KeycardReaderSecureDoorBlockEntity::new, false, KEYCARD_READER_SECURE_DOOR.get()));

    public static final Supplier<BlockEntityType<SlidingDoorBlockEntity>> SLIDING_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "sliding_door_block_entity",
            () -> new BlockEntityType<>(SlidingDoorBlockEntity::new, false, WINDOWED_SLIDING_DOOR.get()));

    public static final Supplier<BlockEntityType<LargeSlidingDoorBlockEntity>> LARGE_SLIDING_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "large_sliding_door_block_entity",
            () -> new BlockEntityType<>(LargeSlidingDoorBlockEntity::new, false, LARGE_SLIDING_DOOR.get()));

    public static final Supplier<BlockEntityType<DistributionBoardBlockEntity>> DISTRIBUTION_BOARD_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "distribution_board_block_entity",
            () -> new BlockEntityType<>(DistributionBoardBlockEntity::new, false, DISTRIBUTION_BOARD.get()));

    public static final Supplier<BlockEntityType<RedstoneDeviceBlockEntity>> REDSTONE_DEVICE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "redstone_device_block_entity",
            () -> new BlockEntityType<>(RedstoneDeviceBlockEntity::new, false, REDSTONE_DEVICE.get()));

    public static final Supplier<BlockEntityType<SCP151BlockEntity>> SCP151_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "scp_151_block_entity",
            () -> new BlockEntityType<>(SCP151BlockEntity::new, false, SCP_151.get()));

    public static final Supplier<BlockEntityType<WallWritingBlockEntity>> WALL_WRITING_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "wall_writing_block_entity",
            () -> new BlockEntityType<>(WallWritingBlockEntity::new, false, WALL_WRITING.get()));

    public static final Supplier<BlockEntityType<StickyNoteBlockEntity>> STICKY_NOTE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "sticky_note_block_entity",
            () -> new BlockEntityType<>(StickyNoteBlockEntity::new, false, STICKY_NOTE.get()));

    public static final Supplier<BlockEntityType<SubstanceFluidBlockEntity>> SUBSTANCE_FLUID_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "substance_fluid_block_entity",
            () -> new BlockEntityType<>(SubstanceFluidBlockEntity::new, false, SUBSTANCE_FLUID.get()));

    public static final Supplier<BlockEntityType<EmergencyExitLampBlockEntity>> EMERGENCY_EXIT_LAMP_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "emergency_exit_lamp_block_entity",
            () -> new BlockEntityType<>(EmergencyExitLampBlockEntity::new, false, EMERGENCY_EXIT_LAMP.get()));

    public static final Supplier<BlockEntityType<TelevisionBlockEntity>> TELEVISION_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "television_block_entity",
            () -> new BlockEntityType<>(TelevisionBlockEntity::new, false, LIGHT_GRAY_TELEVISION.get(), LIGHT_GRAY_WALL_TELEVISION.get()));

    public static final Supplier<BlockEntityType<FlamingoBlockEntity>> PLASTIC_FLAMINGO_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "plastic_flamingo_block_entity",
            () -> new BlockEntityType<>(FlamingoBlockEntity::new, false, PLASTIC_FLAMINGO.get()));

    public static final Supplier<BlockEntityType<BurrowBlockEntity>> BURROW_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "burrow_block_entity",
            () -> new BlockEntityType<>(BurrowBlockEntity::new, false, BURROW.get()));

    public static final Supplier<BlockEntityType<DrumBlockEntity>> DRUM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "drum_block_entity",
            () -> new BlockEntityType<>(DrumBlockEntity::new, false, METAL_DRUM.get()));

    public static final Supplier<BlockEntityType<SolidSubstanceBlockEntity>> SOLID_SUBSTANCE_BLOCK_ENTITY = BitterBlockEntities.BLOCK_ENTITY_TYPES.register(
            "solid_substance_block_entity",
            () -> new BlockEntityType<>(SolidSubstanceBlockEntity::new, false, SOLID_SUBSTANCE.get(), RED_ICE.get()));
}

