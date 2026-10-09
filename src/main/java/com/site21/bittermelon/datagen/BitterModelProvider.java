package com.site21.bittermelon.datagen;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.content.blocks.dirtyfloor.DirtyFloorBlock;
import com.site21.bittermelon.common.content.blocks.electronics.keycardreader.KeycardReaderBlock;
import com.site21.bittermelon.common.content.blocks.electronics.redstonedevice.RedstoneDeviceBlock;
import com.site21.bittermelon.common.content.blocks.electronics.television.StandingTelevisionBlock;
import com.site21.bittermelon.common.content.blocks.poster.SmallPosterBlock;
import com.site21.bittermelon.common.content.blocks.properties.Placement;
import com.site21.bittermelon.common.content.blocks.stickynote.StickyNoteBlock;
import com.site21.bittermelon.common.content.entities.scp718.SCP718BlisterBlock;
import com.site21.bittermelon.common.content.items.substance.pill.PillShape;
import com.site21.bittermelon.datagen.property.*;
import com.site21.bittermelon.init.neoforge.BitterBlocks;
import com.site21.bittermelon.init.neoforge.BitterItems;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.site21.bittermelon.datagen.BitterModelTemplates.*;
import static com.site21.bittermelon.init.neoforge.BitterBlocks.*;
import static com.site21.bittermelon.init.neoforge.BitterBlocks.LARGE_SLIDING_DOOR;
import static com.site21.bittermelon.init.neoforge.BitterItems.*;
import static com.site21.bittermelon.init.neoforge.BitterItems.KEYCARD_READER;
import static com.site21.bittermelon.init.neoforge.BitterItems.STICKY_NOTE;
import static net.minecraft.client.data.models.BlockModelGenerators.*;
import static net.minecraft.client.data.models.model.TextureMapping.getBlockTexture;

public class BitterModelProvider extends ModelProvider {

    public BitterModelProvider(PackOutput output) {
        super(output, Bittermelon.MOD_ID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(BitterBlocks.SMALL_CARDBOARD_BOX.get());
        blockModels.createTrivialCube(BitterBlocks.ATM.get());
        createIndentedSmallBlock(blockModels, BitterBlocks.CONTAINMENT_PANEL.get());
        blockModels.createDoor(BitterBlocks.SECURE_DOOR.get());
        blockModels.createDoor(BitterBlocks.KEYCARD_READER_SECURE_DOOR.get());
        blockModels.registerSimpleFlatItemModel(LARGE_SLIDING_DOOR.asItem());
        blockModels.createParticleOnlyBlock(LARGE_SLIDING_DOOR.get());
        blockModels.registerSimpleFlatItemModel(BitterBlocks.PLASTIC_FLAMINGO.asItem());
        blockModels.createParticleOnlyBlock(BitterBlocks.PLASTIC_FLAMINGO.get());
        createSlidingDoor(blockModels, BitterBlocks.WINDOWED_SLIDING_DOOR.get());
        createTrapdoorShaped(blockModels, BitterBlocks.DISTRIBUTION_BOARD.get());
        blockModels.registerSimpleFlatItemModel(BitterBlocks.DISTRIBUTION_BOARD.get());
        createDirtyFloor(blockModels);
        createButtonLike(blockModels, BitterBlocks.THERMOMETER.get());
        createIndentedSmallBlock(blockModels, BitterBlocks.INTERCOM.get());
        blockModels.createTrivialCube(BitterBlocks.ENVIRONMENT_SENSOR.get());
        blockModels.createTrivialCube(BitterBlocks.CONTAINMENT_ALARM.get());
        blockModels.createTrivialCube(BitterBlocks.DETONATOR.get());
        blockModels.createTrivialCube(BitterBlocks.SPEAKER.get());
        createSmallPoster(blockModels, BitterBlocks.YELLOW_INSPECTION_POSTER.get());
        createPainting(blockModels, BitterBlocks.SCP_151.get());
        blockModels.createTrivialCube(BitterBlocks.PERSONNEL_TERMINAL.get());
        blockModels.createTrivialCube(BitterBlocks.KEYCARD_PRINTER.get());
        blockModels.createAirLikeBlock(BitterBlocks.WALL_WRITING.get(), new Material(Identifier.withDefaultNamespace("white_concrete_powder")));
        createStickyNote(blockModels);
        createKeycardReader(blockModels);
        createRedstoneDevice(blockModels, BitterBlocks.REDSTONE_DEVICE.get(), TexturedModel.ORIENTABLE);
        createCageLamp(blockModels, BitterBlocks.EMERGENCY_EXIT_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.RED_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.GREEN_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.BLUE_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.ORANGE_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.YELLOW_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.PURPLE_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.LIME_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.PINK_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.MAGENTA_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.CYAN_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.LIGHT_BLUE_CAGE_LAMP.get());
        createDefaultCageLamp(blockModels, BitterBlocks.CAGE_LAMP.get());
        createFluid(blockModels, SUBSTANCE_FLUID.get());
        blockModels.createParticleOnlyBlock(SIMPLE_FLUID_BLOCK.get());
        createTelevision(blockModels, BitterBlocks.LIGHT_GRAY_TELEVISION.get(), BitterBlocks.LIGHT_GRAY_WALL_TELEVISION.get());
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BitterBlocks.SCP_330.get(), plainVariant(modLocation("block/scp_330")))
                        .with(ROTATION_HORIZONTAL_FACING)
        );
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BURROW.get(), plainVariant(modLocation("block/burrow")))
        );
        blockModels.createTrivialCube(BitterBlocks.CAGE.get());
        createEyeballBlister(blockModels, BitterBlocks.EYEBALL_BLISTER.get());
        createWoodenSeat(blockModels, BitterBlocks.BLACK_WOODEN_SEAT.get(), "black");
        createDrum(blockModels, BitterBlocks.METAL_DRUM.get());
        itemModels.generateFlatItem(BitterBlocks.METAL_DRUM.get().asItem(), ModelTemplates.FLAT_ITEM);

        // SubstanceFluid Containers
        itemModels.generateFlatItem(BEER_BOTTLE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WHISKEY_BOTTLE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SCP_109.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SUBSTANCE_FLUID_BUCKET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SIMPLE_FLUID_BUCKET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SEA_MONKEY_BUCKET.get(), ModelTemplates.FLAT_ITEM);

        // Medical Items
        itemModels.generateFlatItem(SYRINGE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(SCALPEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(HEMOSTAT.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(RETRACTOR.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(CAUTERY.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(SURGICAL_SPONGE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BANDAGE.get(), ModelTemplates.FLAT_ITEM);

        // Body Parts
        itemModels.generateFlatItem(KIDNEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(LIVER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(STOMACH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(GALLBLADDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(PELVIS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BLADDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(COLON.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BODY_PART.get(), ModelTemplates.FLAT_ITEM);

        // SCPs
        itemModels.generateFlatItem(SCP_005.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SCP_018.get(), ModelTemplates.FLAT_ITEM);
        generateSCP377(itemModels);
        itemModels.generateFlatItem(SCP_377_1.get(), ModelTemplates.FLAT_ITEM);
        generate3D2DItem(itemModels, SCP_2398.get());
        generateSCP1079(itemModels);
        itemModels.generateFlatItem(SCP_1079_CANDY.get(), ModelTemplates.FLAT_ITEM);
        generateSCP815(itemModels);
        itemModels.generateFlatItem(SCP_815_SNAKE_HAND.get(), ModelTemplates.FLAT_ITEM);
        generateHandgun(itemModels);

        // SCP Spawn Eggs
        itemModels.generateFlatItem(BitterItems.SCP_131_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BitterItems.SCP_548_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BitterItems.SCP_650_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BitterItems.SCP_939_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BitterItems.SCP_1507_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SCP_718_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SCP_025_FR_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);

        // Tools and Equipment
        itemModels.generateFlatItem(LASER_DESIGNATOR.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(SCREWDRIVER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(WIRE_CUTTERS.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generate3D2DItem(itemModels, MOP.get());
        generate3D2DItem(itemModels, TASER.get());
        generate3D2DItem(itemModels, CLOWN_HAMMER.get());


        // Writing Utensils
        itemModels.generateFlatItem(PEN.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generateBaseColorItem(itemModels, CHALK.get(), DyeColor.WHITE);
        generateHighlighterItem(itemModels, HIGHLIGHTER.get());

        // Consumables
        generateSmokableItem(itemModels, CIGARETTE.get());
        itemModels.generateFlatItem(CIGARETTE_BUTT.get(), ModelTemplates.FLAT_ITEM);
        generatePowderItem(itemModels);
        generatePillItem(itemModels, PILL.get());

        itemModels.generateFlatItem(FORTUNE_COOKIE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(CRACKED_FORTUNE_COOKIE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BITTERMELON.get(), ModelTemplates.FLAT_ITEM);

        // Miscellaneous Items
        itemModels.generateFlatItem(GLASS_SHARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(GAS_CYLINDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(INTERCOM_PHONE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(NETWORK_CABLE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WIRE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(HELLO_KITTY_CELLPHONE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BASEBALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(KEYCARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WRITABLE_PAPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(TASER_CARTRIDGE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(HANDHELD_SYSTEM_INTERFACE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SUTURE.get(), ModelTemplates.FLAT_ITEM);
        generate3D2DItem(itemModels, REPAIR_TOOL.get());
        itemModels.generateFlatItem(DEBUG_WIRE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(METAL_ROD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(PINK_PLASTIC_SCRAP.get(), ModelTemplates.FLAT_ITEM);
    }

    public void generateSCP1079(@NotNull ItemModelGenerators itemModels) {
        ItemModel.Unbaked model = ItemModelUtils.plainModel(itemModels.createFlatItemModel(SCP_1079.get(), ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked open = ItemModelUtils.plainModel(
                itemModels.createFlatItemModel(SCP_1079.get(), "_open", ModelTemplates.FLAT_ITEM));

        itemModels.itemModelOutput.accept(
                SCP_1079.get(),
                new ConditionalItemModel.Unbaked(
                        Optional.empty(),
                        new Open1079(),
                        open,
                        model
                )
        );
    }

    public void generateSCP815(@NotNull ItemModelGenerators itemModels) {
        ItemModel.Unbaked model = ItemModelUtils.plainModel(itemModels.createFlatItemModel(SCP_815.get(), ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked open = ItemModelUtils.plainModel(
                itemModels.createFlatItemModel(SCP_815.get(), "_open", ModelTemplates.FLAT_ITEM));

        itemModels.itemModelOutput.accept(
                SCP_815.get(),
                new ConditionalItemModel.Unbaked(
                        Optional.empty(),
                        new Open815(),
                        open,
                        model
                )
        );
    }

    public void generateClownHammer(@NotNull ItemModelGenerators itemModels) {
        ItemModel.Unbaked model = ItemModelUtils.plainModel(
                itemModels.createFlatItemModel(BitterItems.CLOWN_HAMMER.get(), ModelTemplates.FLAT_ITEM));

        itemModels.itemModelOutput.accept(BitterItems.CLOWN_HAMMER.get(), model);
    }

    public void generateHandgun(@NotNull ItemModelGenerators itemModels) {
        ItemModel.Unbaked model = ItemModelUtils.plainModel(itemModels.createFlatItemModel(HANDGUN.get(), FLAT_GUN));
        ItemModel.Unbaked open = ItemModelUtils.plainModel(
                itemModels.createFlatItemModel(HANDGUN.get(), "_shooting", FLAT_GUN));

        itemModels.itemModelOutput.accept(
                HANDGUN.get(),
                new ConditionalItemModel.Unbaked(
                        Optional.empty(),
                        new OnCooldown(),
                        open,
                        model
                )
        );
    }

    public void createSlidingDoor(@NotNull BlockModelGenerators blockModels, Block doorBlock) {
        TextureMapping textureMapping = TextureMapping.door(doorBlock);

        MultiVariant bottomLeft = plainVariant(SLIDING_DOOR_BOTTOM_LEFT.create(doorBlock, textureMapping, blockModels.modelOutput));
        MultiVariant bottomRight = plainVariant(SLIDING_DOOR_BOTTOM_RIGHT.create(doorBlock, textureMapping, blockModels.modelOutput));
        MultiVariant topLeft = plainVariant(SLIDING_DOOR_TOP_LEFT.create(doorBlock, textureMapping, blockModels.modelOutput));
        MultiVariant topRight = plainVariant(SLIDING_DOOR_TOP_RIGHT.create(doorBlock, textureMapping, blockModels.modelOutput));

        blockModels.registerSimpleFlatItemModel(doorBlock.asItem());

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(doorBlock).with(
                                PropertyDispatch.initial(
                                        BlockStateProperties.DOOR_HINGE,
                                        BlockStateProperties.OPEN,
                                        BlockStateProperties.DOUBLE_BLOCK_HALF
                                ).generate((hinge, _, half) -> {
                                    if (half == DoubleBlockHalf.LOWER) {
                                        return hinge == DoorHingeSide.LEFT ? bottomLeft : bottomRight;
                                    } else {
                                        return hinge == DoorHingeSide.LEFT ? topLeft : topRight;
                                    }
                                }))
                        .with(ROTATION_HORIZONTAL_FACING)
        );
    }

    public void createDirtyFloor(@NotNull BlockModelGenerators blockModels) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(DIRTY_FLOOR.get()).with(
                                PropertyDispatch.initial(DirtyFloorBlock.DIRTINESS).generate((dirtiness) ->
                                        plainVariant(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/dirty_floor_" + dirtiness))))
                        .with(ROTATION_HORIZONTAL_FACING));
    }

    public void createButtonLike(@NotNull BlockModelGenerators blockModels, Block block) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block,
                                plainVariant(BUTTON_LIKE.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput)))
                        .with(ROTATION_HORIZONTAL_FACING));
    }

    public void createIndentedSmallBlock(@NotNull BlockModelGenerators blockModels, Block block) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block,
                                plainVariant(Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/indented_small_block")))
                        .with(ROTATION_HORIZONTAL_FACING));
    }

    public void createKeycardReader(@NotNull BlockModelGenerators blockModels) {
        blockModels.registerSimpleItemModel(KEYCARD_READER.get().asItem(), modLocation("item/keycard_reader_left"));
        MultiVariant leftVariant = plainVariant(modLocation("block/keycard_reader_left"));
        MultiVariant rightVariant = plainVariant(modLocation("block/keycard_reader_right"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BitterBlocks.KEYCARD_READER.get())
                        .with(PropertyDispatch.initial(KeycardReaderBlock.PLACEMENT)
                                .select(Placement.LEFT, leftVariant)
                                .select(Placement.RIGHT, rightVariant))
                        .with(ROTATION_HORIZONTAL_FACING));
    }

    public void createRedstoneDevice(@NotNull BlockModelGenerators blockModels, Block block, TexturedModel.@NotNull Provider modelProvider) {
        MultiVariant offVariant = plainVariant(modelProvider.create(block, blockModels.modelOutput));
        Material material = getBlockTexture(block, "_front_on");
        MultiVariant onVariant = plainVariant(modelProvider.get(block)
                .updateTextures(mapping -> mapping.put(TextureSlot.FRONT, material))
                .createWithSuffix(block, "_on", blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(createBooleanModelDispatch(RedstoneDeviceBlock.POWERED, onVariant, offVariant))
                        .with(ROTATION_HORIZONTAL_FACING));
    }

    public void createSmallPoster(@NotNull BlockModelGenerators blockModels, @NotNull Block block) {
        blockModels.registerSimpleFlatItemModel(block.asItem());
        MultiVariant leftVariant = plainVariant(SMALL_POSTER_LEFT.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));
        MultiVariant rightVariant = plainVariant(SMALL_POSTER_RIGHT.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(SmallPosterBlock.PLACEMENT)
                                .select(Placement.LEFT, leftVariant)
                                .select(Placement.RIGHT, rightVariant))
                        .with(ROTATION_HORIZONTAL_FACING));
    }

    public void createPainting(@NotNull BlockModelGenerators blockModels, @NotNull Block block) {
        blockModels.registerSimpleFlatItemModel(block.asItem());
        MultiVariant sideVariant = plainVariant(PAINTING_SIDE.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));
        MultiVariant topVariant = plainVariant(PAINTING_TOP.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));
        MultiVariant bottomVariant = plainVariant(PAINTING_BOTTOM.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BlockStateProperties.ATTACH_FACE)
                                .select(AttachFace.WALL, sideVariant)
                                .select(AttachFace.CEILING, topVariant)
                                .select(AttachFace.FLOOR, bottomVariant))
                        .with(ROTATION_HORIZONTAL_FACING)
        );
    }

    public void createTrapdoorShaped(@NotNull BlockModelGenerators blockModels, Block block) {
        MultiVariant sideVariant = plainVariant(TRAPDOOR_SHAPED_SIDE.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));
        MultiVariant topVariant = plainVariant(TRAPDOOR_SHAPED_TOP.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));
        MultiVariant bottomVariant = plainVariant(TRAPDOOR_SHAPED_BOTTOM.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BlockStateProperties.ATTACH_FACE)
                                .select(AttachFace.WALL, sideVariant)
                                .select(AttachFace.CEILING, topVariant)
                                .select(AttachFace.FLOOR, bottomVariant))
                        .with(ROTATION_HORIZONTAL_FACING)
        );
    }

    public void createStickyNote(@NotNull BlockModelGenerators blockModels) {
        blockModels.registerSimpleFlatItemModel(STICKY_NOTE.get().asItem());
        MultiPartGenerator multiPartGenerator = MultiPartGenerator.multiPart(BitterBlocks.STICKY_NOTE.get());

        // Wall variants
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != Direction.Axis.Y) {
                for (int i = 0; i <= 15; i++) {
                    boolean topLeft = (i & 8) != 0;
                    boolean topRight = (i & 4) != 0;
                    boolean bottomLeft = (i & 2) != 0;
                    boolean bottomRight = (i & 1) != 0;

                    String modelName = "sticky_note_" + String.format("%04d", Integer.parseInt(Integer.toBinaryString(i)));
                    Identifier modelLocation = Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/sticky_note/" + modelName);

                    multiPartGenerator.with(
                            BlockModelGenerators.condition()
                                    .term(StickyNoteBlock.TOP_LEFT, topLeft)
                                    .term(StickyNoteBlock.TOP_RIGHT, topRight)
                                    .term(StickyNoteBlock.BOTTOM_LEFT, bottomLeft)
                                    .term(StickyNoteBlock.BOTTOM_RIGHT, bottomRight)
                                    .term(BlockStateProperties.ATTACH_FACE, AttachFace.WALL)
                                    .term(BlockStateProperties.HORIZONTAL_FACING, direction),
                            plainVariant(modelLocation).with(getRotationMutator(direction))
                    );
                }
            }
        }

        // Floor variants
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != Direction.Axis.Y) {
                for (int i = 0; i <= 15; i++) {
                    boolean topLeft = (i & 8) != 0;
                    boolean topRight = (i & 4) != 0;
                    boolean bottomLeft = (i & 2) != 0;
                    boolean bottomRight = (i & 1) != 0;

                    String modelName = "sticky_note_floor_" + String.format("%04d", Integer.parseInt(Integer.toBinaryString(i)));
                    Identifier modelLocation = Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/sticky_note/" + modelName);

                    multiPartGenerator.with(
                            BlockModelGenerators.condition()
                                    .term(StickyNoteBlock.TOP_LEFT, topLeft)
                                    .term(StickyNoteBlock.TOP_RIGHT, topRight)
                                    .term(StickyNoteBlock.BOTTOM_LEFT, bottomLeft)
                                    .term(StickyNoteBlock.BOTTOM_RIGHT, bottomRight)
                                    .term(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR)
                                    .term(BlockStateProperties.HORIZONTAL_FACING, direction),
                            plainVariant(modelLocation).with(getRotationMutator(direction))
                    );
                }
            }
        }

        // Ceiling variants
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != Direction.Axis.Y) {

                for (int i = 0; i <= 15; i++) {
                    boolean topLeft = (i & 8) != 0;
                    boolean topRight = (i & 4) != 0;
                    boolean bottomLeft = (i & 2) != 0;
                    boolean bottomRight = (i & 1) != 0;

                    String modelName = "sticky_note_ceiling_" + String.format("%04d", Integer.parseInt(Integer.toBinaryString(i)));
                    Identifier modelLocation = Identifier.fromNamespaceAndPath(Bittermelon.MOD_ID, "block/sticky_note/" + modelName);

                    multiPartGenerator.with(
                            BlockModelGenerators.condition()
                                    .term(StickyNoteBlock.TOP_LEFT, topLeft)
                                    .term(StickyNoteBlock.TOP_RIGHT, topRight)
                                    .term(StickyNoteBlock.BOTTOM_LEFT, bottomLeft)
                                    .term(StickyNoteBlock.BOTTOM_RIGHT, bottomRight)
                                    .term(BlockStateProperties.ATTACH_FACE, AttachFace.CEILING)
                                    .term(BlockStateProperties.HORIZONTAL_FACING, direction),
                            plainVariant(modelLocation).with(getRotationMutator(direction))
                    );
                }
            }
        }

        blockModels.blockStateOutput.accept(multiPartGenerator);
    }

    public void generate3D2DItem(@NotNull ItemModelGenerators itemModels, Item item) {
        ItemModel.Unbaked inventory = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked holding = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_in_hand"));
        itemModels.itemModelOutput.accept(item, ItemModelGenerators.createFlatModelDispatch(inventory, holding));
    }

    public void generateTintedSubstanceItem(@NotNull ItemModelGenerators itemModels, Item item) {
        Identifier model = itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, new SubstanceColor()));
    }

    public void generatePowderItem(@NotNull ItemModelGenerators itemModels) {
        itemModels.itemModelOutput.accept(
                POWDER.get(),
                new RangeSelectItemModel.Unbaked(
                        Optional.empty(),
                        new SubstanceVolume(),
                        1,
                        List.of(
                                new RangeSelectItemModel.Entry(
                                        0,
                                        ItemModelUtils.tintedModel(
                                                ModelTemplates.FLAT_ITEM.create(
                                                        modLocation("item/powder_1"),
                                                        TextureMapping.layer0(new Material(modLocation("item/powder_1"))),
                                                        itemModels.modelOutput
                                                ),
                                                new SubstanceColor()
                                        )
                                ),
                                new RangeSelectItemModel.Entry(
                                        5,
                                        ItemModelUtils.tintedModel(
                                                ModelTemplates.FLAT_ITEM.create(
                                                        modLocation("item/powder_2"),
                                                        TextureMapping.layer0(new Material(modLocation("item/powder_2"))),
                                                        itemModels.modelOutput
                                                ),
                                                new SubstanceColor()
                                        )
                                ),
                                new RangeSelectItemModel.Entry(
                                        10,
                                        ItemModelUtils.tintedModel(
                                                ModelTemplates.FLAT_ITEM.create(
                                                        modLocation("item/powder_3"),
                                                        TextureMapping.layer0(new Material(modLocation("item/powder_3"))),
                                                        itemModels.modelOutput
                                                ),
                                                new SubstanceColor()
                                        )
                                ),
                                new RangeSelectItemModel.Entry(
                                        15,
                                        ItemModelUtils.tintedModel(mcLocation("item/sugar"), new SubstanceColor())
                                )
                        ),
                        Optional.of(
                                ItemModelUtils.plainModel(mcLocation("item/sugar"))
                        )
                )
        );
    }

    public void generatePillItem(@NotNull ItemModelGenerators itemModels, Item item) {
        itemModels.itemModelOutput.accept(
                item,
                new SelectItemModel.Unbaked(
                        Optional.empty(),
                        new SelectItemModel.UnbakedSwitch<>(
                                new StackPillShape(),
                                List.of(
                                        new SelectItemModel.SwitchCase<>(
                                                List.of(PillShape.ROUND),
                                                ItemModelUtils.tintedModel(
                                                        ModelTemplates.FLAT_ITEM.create(
                                                                modLocation("item/pill_round"),
                                                                TextureMapping.layer0(new Material(modLocation("item/pill_round"))),
                                                                itemModels.modelOutput
                                                        ),
                                                        new Dye(0xFFFFFF)
                                                )
                                        ),
                                        new SelectItemModel.SwitchCase<>(
                                                List.of(PillShape.CAPSULE),
                                                ItemModelUtils.tintedModel(itemModels.generateLayeredItem(
                                                                modLocation("item/pill_capsule"),
                                                                new Material(modLocation("item/pill_capsule")),
                                                                new Material(modLocation("item/pill_capsule_overlay"))
                                                        ),
                                                        new Dye(0xFFFFFF),
                                                        ItemModelUtils.constantTint(-1))
                                        ),
                                        new SelectItemModel.SwitchCase<>(
                                                List.of(PillShape.OVAL),
                                                ItemModelUtils.tintedModel(
                                                        ModelTemplates.FLAT_ITEM.create(
                                                                modLocation("item/pill_oval"),
                                                                TextureMapping.layer0(new Material(modLocation("item/pill_oval"))),
                                                                itemModels.modelOutput
                                                        ),
                                                        new Dye(0xFFFFFF)
                                                )
                                        )
                                )
                        ),
                        Optional.of(ItemModelUtils.plainModel(modLocation("item/pill_round"))))
        );
    }

    public void generateBaseColorItem(@NotNull ItemModelGenerators itemModels, Item item, DyeColor defaultColor) {
        itemModels.itemModelOutput.accept(
                item,
                ItemModelUtils.tintedModel(
                        itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM),
                        new BaseColor(defaultColor)
                )
        );
    }

    public void generateHighlighterItem(@NotNull ItemModelGenerators itemModels, Item item) {
        itemModels.itemModelOutput.accept(
                item,
                ItemModelUtils.tintedModel(
                        itemModels.generateLayeredItem(
                                item,
                                new Material(modLocation("item/highlighter_tip")),
                                new Material(modLocation("item/highlighter"))
                        ),
                        ItemModelUtils.constantTint(-1),
                        new BaseColor(DyeColor.WHITE)
                )
        );
    }

    public void generateSmokableItem(@NotNull ItemModelGenerators itemModels, Item item) {
        ItemModel.Unbaked model = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, SMOKABLE));
        ItemModel.Unbaked litModel = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_lit", SMOKABLE));

        itemModels.itemModelOutput.accept(
                item,
                new ConditionalItemModel.Unbaked(
                        Optional.empty(),
                        new SmokableLit(),
                        litModel,
                        model
                )
        );
    }

    public void generateSCP377(@NotNull ItemModelGenerators itemModels) {
        ItemModel.Unbaked model = ItemModelUtils.plainModel(itemModels.createFlatItemModel(SCP_377.get(), ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked emptyModel = ItemModelUtils.plainModel(
                itemModels.createFlatItemModel(SCP_377.get(), "_empty", ModelTemplates.FLAT_ITEM));

        itemModels.itemModelOutput.accept(
                SCP_377.get(),
                new ConditionalItemModel.Unbaked(
                        Optional.empty(),
                        new Empty377(),
                        model,
                        emptyModel
                )
        );
    }

    public void createCageLamp(@NotNull BlockModelGenerators blockModels, Block block) {
        TextureMapping offTextureMapping = TextureMapping.defaultTexture(block);
        Material onTexture = getBlockTexture(block, "_on");
        TextureMapping onTextureMapping = offTextureMapping.copyAndUpdate(TextureSlot.TEXTURE, onTexture);

        createCageLamp(blockModels, block, offTextureMapping, onTextureMapping);
    }

    public void createDefaultCageLamp(@NotNull BlockModelGenerators blockModels, Block block) {
        TextureMapping offTextureMapping = TextureMapping.defaultTexture(new Material(modLocation("block/cage_lamp")));
        Material onTexture = getBlockTexture(block, "_on");
        TextureMapping onTextureMapping = offTextureMapping.copyAndUpdate(TextureSlot.TEXTURE, onTexture);

        createCageLamp(blockModels, block, offTextureMapping, onTextureMapping);
    }

    public void createCageLamp(@NotNull BlockModelGenerators blockModels, Block block, TextureMapping offMapping, TextureMapping onMapping) {
        MultiVariant sideOn = plainVariant(CAGE_LAMP_SIDE_ON.create(block, onMapping, blockModels.modelOutput));
        MultiVariant sideOff = plainVariant(CAGE_LAMP_SIDE_OFF.create(block, offMapping, blockModels.modelOutput));
        MultiVariant topOn = plainVariant(CAGE_LAMP_TOP_ON.create(block, onMapping, blockModels.modelOutput));
        MultiVariant topOff = plainVariant(CAGE_LAMP_TOP_OFF.create(block, offMapping, blockModels.modelOutput));
        MultiVariant bottomOn = plainVariant(CAGE_LAMP_BOTTOM_ON.create(block, onMapping, blockModels.modelOutput));
        MultiVariant bottomOff = plainVariant(CAGE_LAMP_BOTTOM_OFF.create(block, offMapping, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BlockStateProperties.ATTACH_FACE, BlockStateProperties.LIT)
                                .select(AttachFace.WALL, true, sideOn)
                                .select(AttachFace.WALL, false, sideOff)
                                .select(AttachFace.CEILING, true, topOn)
                                .select(AttachFace.CEILING, false, topOff)
                                .select(AttachFace.FLOOR, true, bottomOn)
                                .select(AttachFace.FLOOR, false, bottomOff))
                        .with(ROTATION_HORIZONTAL_FACING)
        );

        blockModels.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block, "_bottom_on"));
    }

    public void createTelevision(BlockModelGenerators blockModels, Block standingBlock, Block wallBlock) {
        TextureMapping textureMapping = TextureMapping.defaultTexture(standingBlock);
        createStandingTelevision(blockModels, standingBlock, textureMapping);
        createWallTelevision(blockModels, wallBlock, textureMapping);
    }

    public void createStandingTelevision(@NotNull BlockModelGenerators blockModels, @NotNull Block block, TextureMapping textureMapping) {
        MultiVariant normalVariant = plainVariant(TELEVISION.create(block, textureMapping, blockModels.modelOutput));
        MultiVariant angle225Variant = plainVariant(TELEVISION_225.create(block, textureMapping, blockModels.modelOutput));
        MultiVariant angle45Variant = plainVariant(TELEVISION_45.create(block, textureMapping, blockModels.modelOutput));
        MultiVariant angle675Variant = plainVariant(TELEVISION_675.create(block, textureMapping, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(StandingTelevisionBlock.ROTATION)
                                .select(0, normalVariant)
                                .select(1, angle225Variant)
                                .select(2, angle45Variant)
                                .select(3, angle675Variant)
                                .select(4, normalVariant.with(Y_ROT_90))
                                .select(5, angle225Variant.with(Y_ROT_90))
                                .select(6, angle45Variant.with(Y_ROT_90))
                                .select(7, angle675Variant.with(Y_ROT_90))
                                .select(8, normalVariant.with(Y_ROT_180))
                                .select(9, angle225Variant.with(Y_ROT_180))
                                .select(10, angle45Variant.with(Y_ROT_180))
                                .select(11, angle675Variant.with(Y_ROT_180))
                                .select(12, normalVariant.with(Y_ROT_270))
                                .select(13, angle225Variant.with(Y_ROT_270))
                                .select(14, angle45Variant.with(Y_ROT_270))
                                .select(15, angle675Variant.with(Y_ROT_270)))
        );

        blockModels.registerSimpleItemModel(block.asItem(), ModelLocationUtils.getModelLocation(block, "_45"));
    }

    public void createWallTelevision(@NotNull BlockModelGenerators blockModels, @NotNull Block block, TextureMapping textureMapping) {
        MultiVariant wallVariant = plainVariant(TELEVISION_WALL.create(block, textureMapping, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING)
                                .select(Direction.NORTH, wallVariant)
                                .select(Direction.EAST, wallVariant.with(Y_ROT_90))
                                .select(Direction.SOUTH, wallVariant.with(Y_ROT_180))
                                .select(Direction.WEST, wallVariant.with(Y_ROT_270)))
        );
    }

    public void createFluid(@NotNull BlockModelGenerators blockModels, @NotNull Block block) {
        MultiVariant none = blockModels.createParticleOnlyBlockModel(block, block);

        TextureMapping fireMapping = new TextureMapping().put(TextureSlot.FIRE, new Material(Identifier.withDefaultNamespace("block/fire_0")));
        MultiVariant full = plainVariant(FIRE.create(block, fireMapping, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BlockStateProperties.LIT)
                                .select(false, none)
                                .select(true, full)
                        ));
    }

    public void createEyeballBlister(BlockModelGenerators blockModels, Block block) {
        blockModels.registerSimpleItemModel(block, Bittermelon.identifier("block/eyeball_blister_2"));
        MultiVariant age0 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_0",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_0"))), blockModels.modelOutput));
        MultiVariant age1 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_1",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_1"))), blockModels.modelOutput));
        MultiVariant age2 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_2",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_2"))), blockModels.modelOutput));
        MultiVariant age3 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_3",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_3"))), blockModels.modelOutput));
        MultiVariant age4 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_4",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_4"))), blockModels.modelOutput));
        MultiVariant age5 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_5",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_5"))), blockModels.modelOutput));
        MultiVariant age6 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_6",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_6"))), blockModels.modelOutput));
        MultiVariant age7 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_7",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_7"))), blockModels.modelOutput));
        MultiVariant age8 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_8",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_8"))), blockModels.modelOutput));
        MultiVariant age9 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_9",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_9"))), blockModels.modelOutput));
        MultiVariant age10 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_10",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_10"))), blockModels.modelOutput));
        MultiVariant age11 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_11",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_11"))), blockModels.modelOutput));
        MultiVariant age12 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_12",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_12"))), blockModels.modelOutput));
        MultiVariant age13 = plainVariant(BitterModelTemplates.EYEBALL_BLISTER.createWithSuffix(block, "_13",
                TextureMapping.cross(new Material(Bittermelon.identifier("block/eyeball_blister_13"))), blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(SCP718BlisterBlock.AGE)
                                .select(0, age0)
                                .select(1, age1)
                                .select(2, age2)
                                .select(3, age3)
                                .select(4, age4)
                                .select(5, age5)
                                .select(6, age6)
                                .select(7, age7)
                                .select(8, age8)
                                .select(9, age9)
                                .select(10, age10)
                                .select(11, age11)
                                .select(12, age12)
                                .select(13, age13)
                        ));
    }

    public void createWoodenSeat(BlockModelGenerators blockModels, Block block, String color) {
        createSeat(
                blockModels,
                block,
                new Material(Bittermelon.identifier("block/" + color + "_seat_cushion")),
                new Material(Bittermelon.identifier("block/wooden_seat_frame"))
        );
    }

    public void createSeat(BlockModelGenerators blockModels, Block block, Material cushion, Material frame) {
        TextureMapping textureMapping = new TextureMapping().put(CUSHION, cushion).put(FRAME, frame);

        // TODO: Reuse models across blocks

        MultiVariant seatTop = plainVariant(SEAT_TOP.create(block, textureMapping, blockModels.modelOutput));
        MultiVariant legNW = plainVariant(SEAT_LEG_NW.create(block, textureMapping, blockModels.modelOutput));
        MultiVariant legNE = plainVariant(SEAT_LEG_NE.create(block, textureMapping, blockModels.modelOutput));
        MultiVariant legSW = plainVariant(SEAT_LEG_SW.create(block, textureMapping, blockModels.modelOutput));
        MultiVariant legSE = plainVariant(SEAT_LEG_SE.create(block, textureMapping, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiPartGenerator.multiPart(block)
                        .with(
                                condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
                                seatTop
                        )
                        .with(
                                condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
                                seatTop.with(Y_ROT_90)
                        )
                        .with(
                                condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
                                seatTop.with(Y_ROT_180)
                        )
                        .with(
                                condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
                                seatTop.with(Y_ROT_270)
                        )
                        .with(
                                condition()
                                        .term(BlockStateProperties.NORTH, false)
                                        .term(BlockStateProperties.WEST, false),
                                legNW
                        )
                        .with(
                                condition()
                                        .term(BlockStateProperties.NORTH, false)
                                        .term(BlockStateProperties.EAST, false),
                                legNE
                        )
                        .with(
                                condition()
                                        .term(BlockStateProperties.SOUTH, false)
                                        .term(BlockStateProperties.WEST, false),
                                legSW
                        )
                        .with(
                                condition()
                                        .term(BlockStateProperties.SOUTH, false)
                                        .term(BlockStateProperties.EAST, false),
                                legSE
                        )
        );

        blockModels.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block));
    }

    public void createDrumOld(@NotNull BlockModelGenerators blockModels, Block block) {
        Material side = getBlockTexture(block, "_side");
        Material bottom = getBlockTexture(block, "_bottom");

        TextureMapping closedMapping = new TextureMapping()
                .put(TextureSlot.TOP, getBlockTexture(block, "_top"))
                .put(TextureSlot.SIDE, side)
                .put(TextureSlot.BOTTOM, bottom);
        TextureMapping openMapping = new TextureMapping()
                .put(TextureSlot.TOP, getBlockTexture(block, "_top_open"))
                .put(TextureSlot.SIDE, side)
                .put(TextureSlot.BOTTOM, bottom);

        MultiVariant closed = plainVariant(
                ModelTemplates.CUBE_BOTTOM_TOP.create(block, closedMapping, blockModels.modelOutput));
        MultiVariant open = plainVariant(
                ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(block, "_open", openMapping, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BarrelBlock.FACING, BarrelBlock.OPEN)
                                .generate((facing, isOpen) -> {
                                    MultiVariant base = isOpen ? open : closed;
                                    return switch (facing) {
                                        case DOWN -> base.with(X_ROT_180);
                                        case NORTH -> base.with(X_ROT_90);
                                        case EAST -> base.with(X_ROT_90).with(Y_ROT_90);
                                        case SOUTH -> base.with(X_ROT_90).with(Y_ROT_180);
                                        case WEST -> base.with(X_ROT_90).with(Y_ROT_270);
                                        default -> base;
                                    };
                                }))
        );

        blockModels.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block));
    }

    public void createDrum(@NotNull BlockModelGenerators blockModels, Block block) {
        MultiVariant closed = plainVariant(DRUM.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));
        MultiVariant open = plainVariant(DRUM_OPEN.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BarrelBlock.FACING, BarrelBlock.OPEN)
                                .generate((facing, isOpen) -> {
                                    MultiVariant base = isOpen ? open : closed;
                                    return switch (facing) {
                                        case DOWN -> base.with(X_ROT_180);
                                        case NORTH -> base.with(X_ROT_90);
                                        case EAST -> base.with(X_ROT_90).with(Y_ROT_90);
                                        case SOUTH -> base.with(X_ROT_90).with(Y_ROT_180);
                                        case WEST -> base.with(X_ROT_90).with(Y_ROT_270);
                                        default -> base;
                                    };
                                }))
        );
    }

    @Contract(pure = true)
    private VariantMutator getRotationMutator(@NotNull Direction direction) {
        return switch (direction) {
            case EAST -> Y_ROT_90;
            case SOUTH -> Y_ROT_180;
            case WEST -> Y_ROT_270;
            default -> NOP;
        };
    }
}
