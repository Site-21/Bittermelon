package com.site21.bittermelon.datagen;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.init.neoforge.BitterSounds;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class BitterSoundDefinitionsProvider extends SoundDefinitionsProvider {
    public BitterSoundDefinitionsProvider(PackOutput output) {
        super(output, Bittermelon.MOD_ID);
    }

    private static SoundDefinition.Sound mod(String path) {
        return sound(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, path));
    }

    private static SoundDefinition.Sound vanilla(String path) {
        return sound(Identifier.withDefaultNamespace(path));
    }

    private void simple(Holder<SoundEvent> event, String... paths) {
        SoundDefinition def = definition();
        for (String path : paths) {
            def.with(mod(path));
        }
        add(event, def);
    }

    @Override
    public void registerSounds() {
        // Containers
        simple(BitterSounds.METAL_INVENTORY, "container/metal_inventory");
        simple(BitterSounds.TOOLBOX_OPEN, "container/toolbox_open");
        simple(BitterSounds.TOOLBOX_CLOSE, "container/toolbox_close");

        // Medical
        simple(BitterSounds.SCALPEL, "medical/scalpel");
        simple(BitterSounds.RETRACT, "medical/retract");
        simple(BitterSounds.CAUTERY, "medical/cautery");

        // Entity
        simple(BitterSounds.FALL, "entity/fall");
        simple(BitterSounds.BITE, "entity/bite");
        simple(BitterSounds.DRAG, "entity/drag");
        simple(BitterSounds.SLASH, "entity/slash");
        simple(BitterSounds.SMASH, "entity/smash");
        simple(BitterSounds.STAB, "entity/stab");
        simple(BitterSounds.WRESTLE, "entity/wrestle");
        simple(BitterSounds.FLAMINGO_HONK, "entity/flamingo_honk");
        simple(BitterSounds.GHOSTLY_EXHALE, "entity/scp939/ghostly_exhale");
        simple(BitterSounds.SCREAM, "entity/scp939/939_scream");
        simple(BitterSounds.MALE_COUGH,
                "entity/cough/male_cough1", "entity/cough/male_cough2", "entity/cough/male_cough3",
                "entity/cough/male_cough4", "entity/cough/male_cough5", "entity/cough/male_cough6");
        simple(BitterSounds.FEMALE_COUGH,
                "entity/cough/female_cough1", "entity/cough/female_cough2", "entity/cough/female_cough3",
                "entity/cough/female_cough4", "entity/cough/female_cough5", "entity/cough/female_cough6");

        // Misc
        simple(BitterSounds.LOW_IMPACT, "misc/low_impact");
        add(BitterSounds.SCANNER_BEEP, definition().with(mod("misc/scanner_beep").attenuationDistance(4)));
        simple(BitterSounds.BAT_IMPACT, "misc/bat_impact");
        simple(BitterSounds.SPARKS, "misc/sparks");
        simple(BitterSounds.SPLAT, "misc/splat");
        simple(BitterSounds.SPLATTER, "misc/splatter");
        simple(BitterSounds.HEART_BEAT, "misc/heart_beat");
        simple(BitterSounds.SLOW_BEAT, "misc/slow_beat");
        simple(BitterSounds.SLIP, "misc/slip");
        simple(BitterSounds.SNORT, "misc/snort");
        simple(BitterSounds.ZAP, "misc/zap");
        simple(BitterSounds.KNOCK, "misc/knock");

        // Machine
        simple(BitterSounds.BOOT_UP_TUNE, "machine/boot_up_tune");
        simple(BitterSounds.TERMINAL_HUM, "machine/terminal_hum");
        simple(BitterSounds.CONTAINMENT_ALERT, "machine/containment_alert");
        simple(BitterSounds.LARGE_SLIDING_DOOR_CLOSE, "machine/large_sliding_door_close");
        simple(BitterSounds.LARGE_SLIDING_DOOR_OPEN, "machine/large_sliding_door_open");
        simple(BitterSounds.LARGE_SLIDING_DOOR_STUCK, "machine/large_sliding_door_stuck");
        simple(BitterSounds.SLIDING_DOOR_OPEN, "machine/sliding_door_open");
        simple(BitterSounds.SLIDING_DOOR_CLOSE, "machine/sliding_door_close");
        simple(BitterSounds.DOOR_LOCK, "machine/door_lock");
        simple(BitterSounds.DOOR_UNLOCK, "machine/door_unlock");
        simple(BitterSounds.BREAKER_SWITCH, "machine/breaker_switch");
        simple(BitterSounds.COMPUTER_START, "machine/computer_start");
        simple(BitterSounds.COMPUTER_MID1, "machine/computer_mid1");
        simple(BitterSounds.COMPUTER_MID2, "machine/computer_mid2");
        simple(BitterSounds.COMPUTER_END, "machine/computer_end");
        simple(BitterSounds.MOUSE_CLICK, "machine/mouse_click");

        // Horror
        simple(BitterSounds.SCARE_1, "horror/scare_1");
        simple(BitterSounds.SCARE_2, "horror/scare_2");
        simple(BitterSounds.SCARE_3, "horror/scare_3");
        simple(BitterSounds.SCARE_4, "horror/scare_4");
        simple(BitterSounds.BELL_SCARE, "horror/bell_scare");

        // Item
        simple(BitterSounds.TASER, "item/taser");
        simple(BitterSounds.TASER_RELOAD, "item/taser_reload");
        simple(BitterSounds.TASER_SHOOT, "item/taser_shoot");
        simple(BitterSounds.SCREWDRIVER, "item/screwdriver");
        simple(BitterSounds.SCREWDRIVER_OPEN, "item/screwdriver_open");
        simple(BitterSounds.SCREWDRIVER_CLOSE, "item/screwdriver_close");
        simple(BitterSounds.WIRE_CUTTERS, "item/wire_cutters");
        add(BitterSounds.SCP_377_COOKIE_TAKE.value(), definition().with(vanilla("entity.item.pickup")));
        add(BitterSounds.SCP_377_EMPTY.value(), definition().with(vanilla("block.chest.close")));

        // Ambient
        add(BitterSounds.AMBIENT_NOISES.value(), definition()
                .with(mod("ambient/distant_noise"))
                .with(mod("ambient/movement_vents"))
                .with(mod("ambient/long_groaning"))
                .with(mod("ambient/groan"))
                .with(mod("ambient/distant_thud"))
                .with(mod("ambient/distant_noise"))
                .with(mod("ambient/distant_machinery"))
                .with(mod("ambient/creepy_thud"))
                .with(mod("ambient/computer_noises"))
                .with(mod("ambient/strange_noises"))
                .with(mod("ambient/low_hum"))
                .with(vanilla("animal1"))
                .with(vanilla("bass_whale1"))
                .with(vanilla("bass_whale2"))
                .with(vanilla("dark4")));
        simple(BitterSounds.FACILITY_AMBIENCE, "ambient/facility_ambience");
        simple(BitterSounds.PULSING, "ambient/pulsing");

        add(BitterSounds.BANJO.value(), definition().with(mod("music/banjo").stream()));

        // Block
        simple(BitterSounds.METAL_DRUM_ROLL, "block/metal_drum_roll_1", "block/metal_drum_roll_2", "block/metal_drum_roll_3");
        simple(BitterSounds.METAL_DRUM_FLIP, "block/metal_drum_flip");
        simple(BitterSounds.METAL_DRUM_GROAN, "block/metal_drum_groan_1", "block/metal_drum_groan_2", "block/metal_drum_groan_3");
    }
}