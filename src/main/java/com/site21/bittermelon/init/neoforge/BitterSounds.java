package com.site21.bittermelon.init.neoforge;

import com.site21.bittermelon.Bittermelon;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BitterSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister
            .create(BuiltInRegistries.SOUND_EVENT, Bittermelon.MOD_ID);

    public static final Holder<SoundEvent> TOOLBOX_OPEN = SOUND_EVENTS.register("container.toolbox_open",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> TOOLBOX_CLOSE = SOUND_EVENTS.register("container.toolbox_close",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> METAL_INVENTORY = SOUND_EVENTS.register("container.metal_inventory",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> GHOSTLY_EXHALE = SOUND_EVENTS.register("entity.scp939.ghostly_exhale",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCALPEL = SOUND_EVENTS.register("medical.scalpel",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> RETRACT = SOUND_EVENTS.register("medical.retract",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> CAUTERY = SOUND_EVENTS.register("medical.cautery",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> FALL = SOUND_EVENTS.register("entity.fall",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> WRESTLE = SOUND_EVENTS.register("entity.wrestle",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> STAB = SOUND_EVENTS.register("entity.stab",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SMASH = SOUND_EVENTS.register("entity.smash",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SLASH = SOUND_EVENTS.register("entity.slash",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCREAM = SOUND_EVENTS.register("entity.scp939.939_scream",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BITE = SOUND_EVENTS.register("entity.bite",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> DRAG = SOUND_EVENTS.register("entity.drag",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> LOW_IMPACT = SOUND_EVENTS.register("misc.low_impact",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCANNER_BEEP = SOUND_EVENTS.register("misc.scanner_beep",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BOOT_UP_TUNE = SOUND_EVENTS.register("machine.boot_up_tune",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> TERMINAL_HUM = SOUND_EVENTS.register("machine.terminal_hum",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> CONTAINMENT_ALERT = SOUND_EVENTS.register("machine.containment_alert",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BAT_IMPACT = SOUND_EVENTS.register("misc.bat_impact",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> LARGE_SLIDING_DOOR_CLOSE = SOUND_EVENTS
            .register("machine.large_sliding_door_close", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> LARGE_SLIDING_DOOR_OPEN = SOUND_EVENTS
            .register("machine.large_sliding_door_open", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> LARGE_SLIDING_DOOR_STUCK = SOUND_EVENTS
            .register("machine.large_sliding_door_stuck", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCARE_1 = SOUND_EVENTS.register("horror.scare_1",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCARE_2 = SOUND_EVENTS.register("horror.scare_2",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCARE_3 = SOUND_EVENTS.register("horror.scare_3",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCARE_4 = SOUND_EVENTS.register("horror.scare_4",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SPARKS = SOUND_EVENTS.register("misc.sparks",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MALE_COUGH = SOUND_EVENTS.register("entity.cough.male_cough",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> FEMALE_COUGH = SOUND_EVENTS.register("entity.cough.female_cough",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SPLAT = SOUND_EVENTS.register("misc.splat",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> HEART_BEAT = SOUND_EVENTS.register("misc.heart_beat",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BELL_SCARE = SOUND_EVENTS.register("horror.bell_scare",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SPLATTER = SOUND_EVENTS.register("misc.splatter",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> TASER = SOUND_EVENTS.register("item.taser",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> TASER_RELOAD = SOUND_EVENTS.register("item.taser_reload",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> TASER_SHOOT = SOUND_EVENTS.register("item.taser_shoot",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SLIP = SOUND_EVENTS.register("misc.slip",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SOGGY = SOUND_EVENTS.register("misc.soggy",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SNORT = SOUND_EVENTS.register("misc.snort",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> COMPUTER_END = SOUND_EVENTS.register("machine.computer_end",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> COMPUTER_MID1 = SOUND_EVENTS.register("machine.computer_mid1",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> COMPUTER_MID2 = SOUND_EVENTS.register("machine.computer_mid2",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> COMPUTER_START = SOUND_EVENTS.register("machine.computer_start",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MOUSE_CLICK = SOUND_EVENTS.register("machine.mouse_click",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> AMBIENT_NOISES = SOUND_EVENTS.register("ambient.ambient_noises",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> FACILITY_AMBIENCE = SOUND_EVENTS.register("ambient.facility_ambience",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> PULSING = SOUND_EVENTS.register("ambient.pulsing",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BANJO = SOUND_EVENTS.register("music.banjo",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BREAKER_SWITCH = SOUND_EVENTS.register("machine.breaker_switch",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCREWDRIVER = SOUND_EVENTS.register("item.screwdriver",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> DOOR_UNLOCK = SOUND_EVENTS.register("machine.door_unlock",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> DOOR_LOCK = SOUND_EVENTS.register("machine.door_lock",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SLIDING_DOOR_OPEN = SOUND_EVENTS.register("machine.sliding_door_open",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SLIDING_DOOR_CLOSE = SOUND_EVENTS.register("machine.sliding_door_close",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> WIRE_CUTTERS = SOUND_EVENTS.register("item.wire_cutters",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCREWDRIVER_OPEN = SOUND_EVENTS.register("item.screwdriver_open",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCREWDRIVER_CLOSE = SOUND_EVENTS.register("item.screwdriver_close",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> ZAP = SOUND_EVENTS.register("misc.zap",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> KNOCK = SOUND_EVENTS.register("misc.knock",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCP_377_COOKIE_TAKE = SOUND_EVENTS.register("item.scp_377_cookie_take",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SCP_377_EMPTY = SOUND_EVENTS.register("item.scp_377_empty",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SLOW_BEAT = SOUND_EVENTS.register("misc.slow_beat",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> FLAMINGO_HONK = SOUND_EVENTS.register("entity.flamingo_honk",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> METAL_DRUM_ROLL = SOUND_EVENTS.register("block.metal_drum_roll",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> METAL_DRUM_FLIP = SOUND_EVENTS.register("block.metal_drum_flip",
            SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> METAL_DRUM_GROAN = SOUND_EVENTS.register("block.metal_drum_groan",
            SoundEvent::createVariableRangeEvent);
}
