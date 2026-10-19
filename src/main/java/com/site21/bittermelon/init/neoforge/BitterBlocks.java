package com.site21.bittermelon.init.neoforge;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.blocks.SeatBlock;
import com.site21.bittermelon.common.content.blocks.drum.DrumBlock;
import com.site21.bittermelon.common.content.blocks.burrow.BurrowBlock;
import com.site21.bittermelon.common.content.blocks.container.smallbox.SmallBox;
import com.site21.bittermelon.common.content.blocks.dirtyfloor.DirtyFloorBlock;
import com.site21.bittermelon.common.content.blocks.electronics.ATMBlock;
import com.site21.bittermelon.common.content.blocks.electronics.containmentalarm.ContainmentAlarm;
import com.site21.bittermelon.common.content.blocks.electronics.containmentpanel.ContainmentPanelBlock;
import com.site21.bittermelon.common.content.blocks.electronics.detonator.DetonatorBlock;
import com.site21.bittermelon.common.content.blocks.electronics.environmentsensor.EnvironmentSensor;
import com.site21.bittermelon.common.content.blocks.electronics.intercom.IntercomBlock;
import com.site21.bittermelon.common.content.blocks.electronics.keycardprinter.KeycardPrinter;
import com.site21.bittermelon.common.content.blocks.electronics.keycardreader.KeycardReaderBlock;
import com.site21.bittermelon.common.content.blocks.electronics.largeslidingdoor.LargeSlidingDoorBlock;
import com.site21.bittermelon.common.content.blocks.electronics.personnelterminal.PersonnelTerminalBlock;
import com.site21.bittermelon.common.content.blocks.electronics.redstonedevice.RedstoneDeviceBlock;
import com.site21.bittermelon.common.content.blocks.electronics.securedoor.KeycardReaderSecureDoorBlock;
import com.site21.bittermelon.common.content.blocks.electronics.securedoor.SecureDoorBlock;
import com.site21.bittermelon.common.content.blocks.electronics.slidingdoor.SlidingDoorBlock;
import com.site21.bittermelon.common.content.blocks.electronics.speaker.SpeakerBlock;
import com.site21.bittermelon.common.content.blocks.electronics.television.StandingTelevisionBlock;
import com.site21.bittermelon.common.content.blocks.electronics.television.WallTelevisionBlock;
import com.site21.bittermelon.common.content.blocks.electronics.thermometer.ThermometerBlock;
import com.site21.bittermelon.common.content.blocks.flamingo.FlamingoBlock;
import com.site21.bittermelon.common.content.blocks.lights.CageLampBlock;
import com.site21.bittermelon.common.content.blocks.lights.emergencyexitlight.EmergencyExitLampBlock;
import com.site21.bittermelon.common.content.blocks.poster.SmallPosterBlock;
import com.site21.bittermelon.common.content.blocks.powergrid.distributionboard.DistributionBoardBlock;
import com.site21.bittermelon.common.content.blocks.scp.scp151.SCP151Block;
import com.site21.bittermelon.common.content.blocks.scp.scp330.SCP330Block;
import com.site21.bittermelon.common.content.blocks.stickynote.StickyNoteBlock;
import com.site21.bittermelon.common.content.blocks.wallwriting.WallWritingBlock;
import com.site21.bittermelon.common.content.entities.cage.CageBlock;
import com.site21.bittermelon.common.content.entities.scp718.SCP718BlisterBlock;
import com.site21.bittermelon.common.systems.fluid.simple.SimpleFluidBlock;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlock;
import com.site21.bittermelon.common.systems.fluid.substance.SolidSubstanceBlock;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.ToIntFunction;

import static net.minecraft.world.level.block.SoundType.METAL;

public class BitterBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Bittermelon.MOD_ID);

    @Contract(pure = true)
    private static @NotNull ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (state) -> (Boolean) state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    public static final DeferredBlock<SmallBox> SMALL_CARDBOARD_BOX = BLOCKS.registerBlock(
            "small_cardboard_box",
            properties -> new SmallBox(properties
                    .destroyTime(0.5f)
                    .sound(SoundType.WOOL)
                    .noOcclusion())
    );

    public static final DeferredBlock<ATMBlock> ATM = BLOCKS.registerBlock(
            "atm",
            properties -> new ATMBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<ContainmentPanelBlock> CONTAINMENT_PANEL = BLOCKS.registerBlock(
            "containment_panel",
            properties -> new ContainmentPanelBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<DirtyFloorBlock> DIRTY_FLOOR = BLOCKS.registerBlock(
            "dirty_floor",
            properties -> new DirtyFloorBlock(properties
                    .noOcclusion()
                    .noCollision()
                    .destroyTime(-1)
                    .replaceable()
                    .noLootTable()
            ));

    public static final DeferredBlock<ThermometerBlock> THERMOMETER = BLOCKS.registerBlock(
            "thermometer",
            properties -> new ThermometerBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<IntercomBlock> INTERCOM = BLOCKS.registerBlock(
            "intercom",
            properties -> new IntercomBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<EnvironmentSensor> ENVIRONMENT_SENSOR = BLOCKS.registerBlock(
            "environment_sensor",
            properties -> new EnvironmentSensor(properties.noOcclusion())
    );

    public static final DeferredBlock<ContainmentAlarm> CONTAINMENT_ALARM = BLOCKS.registerBlock(
            "containment_alarm",
            properties -> new ContainmentAlarm(properties.noOcclusion())
    );

    public static final DeferredBlock<DetonatorBlock> DETONATOR = BLOCKS.registerBlock(
            "detonator",
            properties -> new DetonatorBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<SpeakerBlock> SPEAKER = BLOCKS.registerBlock(
            "speaker",
            properties -> new SpeakerBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<SecureDoorBlock> SECURE_DOOR = BLOCKS.registerBlock(
            "secure_door",
            properties -> new SecureDoorBlock(
                    BlockSetType.IRON,
                    properties
                            .noOcclusion()
                            .destroyTime(5)
            )
    );

    public static final DeferredBlock<KeycardReaderSecureDoorBlock> KEYCARD_READER_SECURE_DOOR = BLOCKS.registerBlock(
            "keycard_reader_secure_door",
            properties -> new KeycardReaderSecureDoorBlock(
                    BlockSetType.IRON,
                    properties
                            .noOcclusion()
                            .destroyTime(5)
            )


    );

    public static final DeferredBlock<LargeSlidingDoorBlock> LARGE_SLIDING_DOOR = BLOCKS.registerBlock(
            "large_sliding_door",
            LargeSlidingDoorBlock::new
    );

    public static final DeferredBlock<SmallPosterBlock> YELLOW_INSPECTION_POSTER = BLOCKS.registerBlock(
            "yellow_inspection_poster",
            SmallPosterBlock::new
    );

    public static final DeferredBlock<DistributionBoardBlock> DISTRIBUTION_BOARD = BLOCKS.registerBlock(
            "distribution_board",
            DistributionBoardBlock::new
    );

    public static final DeferredBlock<SCP151Block> SCP_151 = BLOCKS.registerBlock(
            "scp_151",
            SCP151Block::new
    );

    public static final DeferredBlock<PersonnelTerminalBlock> PERSONNEL_TERMINAL = BLOCKS.registerBlock(
            "personnel_terminal_block",
            PersonnelTerminalBlock::new
    );

    public static final DeferredBlock<KeycardPrinter> KEYCARD_PRINTER = BLOCKS.registerBlock(
            "keycard_printer",
            KeycardPrinter::new
    );

    public static final DeferredBlock<SlidingDoorBlock> WINDOWED_SLIDING_DOOR = BLOCKS.registerBlock(
            "windowed_sliding_door",
            properties -> new SlidingDoorBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<WallWritingBlock> WALL_WRITING = BLOCKS.registerBlock(
            "wall_writing",
            properties -> new WallWritingBlock(properties
                    .noCollision()
                    .replaceable()
                    .destroyTime(-1)
                    .sound(SoundType.EMPTY)
                    .noTerrainParticles()
                    .noLootTable()
            )
    );

    public static final DeferredBlock<StickyNoteBlock> STICKY_NOTE = BLOCKS.registerBlock(
            "sticky_note",
            properties -> new StickyNoteBlock(properties
                    .noOcclusion()
                    .noCollision()
                    .sound(new SoundType(1.0f, 1.0f, SoundEvents.BOOK_PUT, SoundEvents.BOOK_PUT, SoundEvents.BOOK_PUT, SoundEvents.BOOK_PUT, SoundEvents.BOOK_PUT))
            )
    );

    public static final DeferredBlock<KeycardReaderBlock> KEYCARD_READER = BLOCKS.registerBlock(
            "keycard_reader",
            properties -> new KeycardReaderBlock(properties
                    .sound(METAL)
                    .destroyTime(1.5f)
            )
    );

    public static final DeferredBlock<RedstoneDeviceBlock> REDSTONE_DEVICE = BLOCKS.registerBlock(
            "redstone_device",
            properties -> new RedstoneDeviceBlock(properties
                    .sound(METAL)
                    .destroyTime(1.5f)
            )
    );

    public static final DeferredBlock<SubstanceFluidBlock> SUBSTANCE_FLUID = BLOCKS.registerBlock(
            "substance_fluid_block",
            properties -> new SubstanceFluidBlock(properties
                    .mapColor(MapColor.WATER)
                    .replaceable()
                    .noCollision()
                    .strength(100.0F)
                    .pushReaction(PushReaction.DESTROY)
                    .noLootTable()
                    .liquid()
                    .sound(SoundType.EMPTY)
            )
    );

    public static final DeferredBlock<SolidSubstanceBlock> FROZEN_SUBSTANCE = BLOCKS.registerBlock(
            "frozen_substance",
            properties -> new SolidSubstanceBlock(properties
                    .mapColor(MapColor.ICE)
                    .strength(0.5f)
                    .sound(SoundType.GLASS)
                    .friction(0.98f)
                    .speedFactor(1.05f)
                    .noOcclusion()
                    .noLootTable()
            )
    );

    public static final DeferredBlock<SimpleFluidBlock> SIMPLE_FLUID_BLOCK = BLOCKS.registerBlock(
            "simple_fluid_block",
            properties -> new SimpleFluidBlock(properties.mapColor(MapColor.WATER).noLootTable())
    );

    public static final DeferredBlock<EmergencyExitLampBlock> EMERGENCY_EXIT_LAMP = BLOCKS.registerBlock(
            "emergency_exit_lamp",
            properties -> new EmergencyExitLampBlock(properties
                    .lightLevel(litBlockEmission(10))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> RED_CAGE_LAMP = BLOCKS.registerBlock(
            "red_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> BLUE_CAGE_LAMP = BLOCKS.registerBlock(
            "blue_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> GREEN_CAGE_LAMP = BLOCKS.registerBlock(
            "green_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> ORANGE_CAGE_LAMP = BLOCKS.registerBlock(
            "orange_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> YELLOW_CAGE_LAMP = BLOCKS.registerBlock(
            "yellow_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> PURPLE_CAGE_LAMP = BLOCKS.registerBlock(
            "purple_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> LIME_CAGE_LAMP = BLOCKS.registerBlock(
            "lime_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> PINK_CAGE_LAMP = BLOCKS.registerBlock(
            "pink_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> MAGENTA_CAGE_LAMP = BLOCKS.registerBlock(
            "magenta_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> CYAN_CAGE_LAMP = BLOCKS.registerBlock(
            "cyan_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> LIGHT_BLUE_CAGE_LAMP = BLOCKS.registerBlock(
            "light_blue_cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<CageLampBlock> CAGE_LAMP = BLOCKS.registerBlock(
            "cage_lamp",
            properties -> new CageLampBlock(properties
                    .lightLevel(litBlockEmission(14))
                    .destroyTime(1.5f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<StandingTelevisionBlock> LIGHT_GRAY_TELEVISION = BLOCKS.registerBlock(
            "light_gray_television",
            properties -> new StandingTelevisionBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<WallTelevisionBlock> LIGHT_GRAY_WALL_TELEVISION = BLOCKS.registerBlock(
            "light_gray_wall_television",
            properties -> new WallTelevisionBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<FlamingoBlock> PLASTIC_FLAMINGO = BLOCKS.registerBlock(
            "plastic_flamingo",
            properties -> new FlamingoBlock(properties.noOcclusion())
    );

    public static final DeferredBlock<SCP330Block> SCP_330 = BLOCKS.registerBlock(
            "scp_330",
            SCP330Block::new
    );

    public static final DeferredBlock<BurrowBlock> BURROW = BLOCKS.registerBlock(
            "burrow",
            properties -> new BurrowBlock(properties
                    .destroyTime(0.5f)
                    .sound(SoundType.GRAVEL)
                    .noLootTable()
            )
    );

    public static final DeferredBlock<CageBlock> CAGE = BLOCKS.registerBlock(
            "cage",
            properties -> new CageBlock(properties
                    .noOcclusion()
                    .destroyTime(2.0f)
                    .sound(METAL)
            )
    );

    public static final DeferredBlock<SCP718BlisterBlock> EYEBALL_BLISTER = BLOCKS.registerBlock(
            "eyeball_blister",
            properties -> new SCP718BlisterBlock(properties
                    .destroyTime(0.5f)
                    .sound(SoundType.SLIME_BLOCK)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .dynamicShape()
            )
    );

    public static final DeferredBlock<SeatBlock> BLACK_WOODEN_SEAT = BLOCKS.registerBlock(
            "black_wooden_seat",
            properties -> new SeatBlock(properties
                    .destroyTime(1.5f)
                    .sound(SoundType.WOOD)
            )
    );

    public static final DeferredBlock<DrumBlock> METAL_DRUM = BLOCKS.registerBlock(
            "metal_drum",
            properties -> new DrumBlock(properties
                    .destroyTime(2.5f)
                    .sound(new SoundType(
                            0.3f,
                            1.5f,
                            SoundEvents.ANVIL_BREAK,
                            SoundEvents.ANVIL_STEP,
                            BitterSounds.METAL_DRUM_FLIP.value(),
                            SoundEvents.ANVIL_HIT,
                            BitterSounds.METAL_DRUM_FLIP.value()
                    ))
            )
    );

    public static final DeferredBlock<SolidSubstanceBlock> RED_ICE = BLOCKS.registerBlock(
            "red_ice",
            properties -> new SolidSubstanceBlock(properties
                    .mapColor(MapColor.COLOR_RED)
                    .strength(0.5f)
                    .sound(SoundType.GLASS)
                    .friction(0.98f)
                    .speedFactor(1.05f)
                    .noOcclusion()
                    .noLootTable()
            )
    );
}