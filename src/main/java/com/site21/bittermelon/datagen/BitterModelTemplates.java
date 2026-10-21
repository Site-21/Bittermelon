package com.site21.bittermelon.datagen;

import com.site21.bittermelon.Bittermelon;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public class BitterModelTemplates {
    public static final TextureSlot CUSHION = TextureSlot.create("cushion");
    public static final TextureSlot FRAME = TextureSlot.create("frame");
    public static final ModelTemplate[] LEVELS = new ModelTemplate[20];

    public static final ModelTemplate SLIDING_DOOR_BOTTOM_LEFT = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/sliding_door_bottom_left")),
            Optional.of("_bottom_left"),
            TextureSlot.TOP, TextureSlot.BOTTOM
    );

    public static final ModelTemplate SLIDING_DOOR_BOTTOM_RIGHT = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/sliding_door_bottom_right")),
            Optional.of("_bottom_right"),
            TextureSlot.TOP, TextureSlot.BOTTOM
    );

    public static final ModelTemplate SLIDING_DOOR_TOP_LEFT = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/sliding_door_top_left")),
            Optional.of("_top_left"),
            TextureSlot.TOP, TextureSlot.BOTTOM
    );

    public static final ModelTemplate SLIDING_DOOR_TOP_RIGHT = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/sliding_door_top_right")),
            Optional.of("_top_right"),
            TextureSlot.TOP, TextureSlot.BOTTOM
    );

    public static final ModelTemplate BUTTON_LIKE = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/button")),
            Optional.of(""),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate SMALL_POSTER_LEFT = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/small_poster_left")),
            Optional.of("_left"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate SMALL_POSTER_RIGHT = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/small_poster_right")),
            Optional.of("_right"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate PAINTING_SIDE = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/painting_side")),
            Optional.of("_side"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate PAINTING_TOP = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/painting_top")),
            Optional.of("_top"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate PAINTING_BOTTOM = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/painting_bottom")),
            Optional.of("_bottom"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate TRAPDOOR_SHAPED_SIDE = new ModelTemplate(
            Optional.of(Identifier.withDefaultNamespace("block/template_orientable_trapdoor_open")),
            Optional.of("_side"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate TRAPDOOR_SHAPED_TOP = new ModelTemplate(
            Optional.of(Identifier.withDefaultNamespace("block/template_orientable_trapdoor_top")),
            Optional.of("_top"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate TRAPDOOR_SHAPED_BOTTOM = new ModelTemplate(
            Optional.of(Identifier.withDefaultNamespace("block/template_orientable_trapdoor_bottom")),
            Optional.of("_bottom"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate SMOKABLE = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "item/smokable")),
            Optional.empty(),
            TextureSlot.LAYER0
    );

    public static final ModelTemplate FLAT_GUN = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "item/flat_gun")),
            Optional.empty(),
            TextureSlot.LAYER0
    );

    public static final ModelTemplate CAGE_LAMP_SIDE_ON = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/cage_lamp_side")),
            Optional.of("_side_on"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate CAGE_LAMP_SIDE_OFF = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/cage_lamp_side")),
            Optional.of("_side_off"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate CAGE_LAMP_TOP_ON = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/cage_lamp_top")),
            Optional.of("_top_on"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate CAGE_LAMP_TOP_OFF = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/cage_lamp_top")),
            Optional.of("_top_off"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate CAGE_LAMP_BOTTOM_ON = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/cage_lamp_bottom")),
            Optional.of("_bottom_on"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate CAGE_LAMP_BOTTOM_OFF = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/cage_lamp_bottom")),
            Optional.of("_bottom_off"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate TELEVISION = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/television")),
            Optional.empty(),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate TELEVISION_WALL = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/television_wall")),
            Optional.of("_wall"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate TELEVISION_225 = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/television_225")),
            Optional.of("_225"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate TELEVISION_45 = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/television_45")),
            Optional.of("_45"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate TELEVISION_675 = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/television_675")),
            Optional.of("_675"),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate FIRE = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/fire")),
            Optional.of("_fire"),
            TextureSlot.FIRE
    );

    public static final ModelTemplate EYEBALL_BLISTER = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/eyeball_blister")),
            Optional.empty(),
            TextureSlot.CROSS
    );

    public static final ModelTemplate SEAT_TOP = new ModelTemplate(
            Optional.of(Bittermelon.identifier("block/seat_top")),
            Optional.of("_top"),
            CUSHION, FRAME
    );

    public static final ModelTemplate SEAT_LEG_NE = new ModelTemplate(
            Optional.of(Bittermelon.identifier("block/seat_leg_ne")),
            Optional.of("_leg_ne"),
            FRAME
    );

    public static final ModelTemplate SEAT_LEG_NW = new ModelTemplate(
            Optional.of(Bittermelon.identifier("block/seat_leg_nw")),
            Optional.of("_leg_nw"),
            FRAME
    );

    public static final ModelTemplate SEAT_LEG_SE = new ModelTemplate(
            Optional.of(Bittermelon.identifier("block/seat_leg_se")),
            Optional.of("_leg_se"),
            FRAME
    );

    public static final ModelTemplate SEAT_LEG_SW = new ModelTemplate(
            Optional.of(Bittermelon.identifier("block/seat_leg_sw")),
            Optional.of("_leg_sw"),
            FRAME
    );

    public static final ModelTemplate DRUM = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/drum")),
            Optional.empty(),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate DRUM_OPEN = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/drum_open")),
            Optional.of("_open"),
            TextureSlot.TEXTURE
    );

    static {
        for (int n = 0; n < 20; n++) {
            int level = n + 1;
            LEVELS[n] = new ModelTemplate(
                    Optional.of(Bittermelon.identifier("block/level_" + level)),
                    Optional.of("_level_" + level),
                    TextureSlot.TEXTURE
            );
        }
    }
}
