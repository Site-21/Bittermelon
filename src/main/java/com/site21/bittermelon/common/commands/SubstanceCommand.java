package com.site21.bittermelon.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.site21.bittermelon.common.content.blocks.fluid.FluidBlockEntity;
import com.site21.bittermelon.common.content.items.substance.SubstanceContainerItem;
import com.site21.bittermelon.common.systems.atmosphere.AtmosHandler;
import com.site21.bittermelon.common.systems.atmosphere.AtmosInstance;
import com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluidBlockEntity;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.common.systems.substance.SubstanceStack;
import com.site21.bittermelon.init.neoforge.BitterFluids;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import static com.site21.bittermelon.common.systems.fluid.substance.SubstanceFluid.LEVEL;
import static com.site21.bittermelon.init.neoforge.BitterRegistries.SUBSTANCE_REGISTRY_KEY;

public class SubstanceCommand {

    public static final PermissionCheck PERMISSION_CHECK = new PermissionCheck.Require(Permissions.COMMANDS_ADMIN);

    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("substance")
                .requires(Commands.hasPermission(PERMISSION_CHECK))
                .then(fluidCommand(buildContext))
                .then(containerCommand(buildContext))
                .then(atmosCommand(buildContext))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> fluidCommand(CommandBuildContext buildContext) {
        return Commands.literal("fluid")
                .then(Commands.literal("add")
                        .then(substanceArg(buildContext)
                                .then(amountArg(SubstanceCommand::addSubstanceFluid))
                        )
                        .then(Commands.literal("temperature")
                                .then(temperatureArg(SubstanceCommand::addTemperatureFluid))
                        )
                )
                .then(Commands.literal("show")
                        .executes(context -> showFluidContents(context.getSource())));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> containerCommand(CommandBuildContext buildContext) {
        return Commands.literal("container")
                .then(Commands.literal("add")
                        .then(substanceArg(buildContext)
                                .then(Commands.literal("volume")
                                        .then(amountArg(SubstanceCommand::addSubstanceContainerVolume)))
                                .then(Commands.literal("amount")
                                        .then(amountArg(SubstanceCommand::addSubstanceContainerAmount)))
                        )
                )
                .then(Commands.literal("show")
                        .executes(context -> showContainerContents(context.getSource())));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> atmosCommand(CommandBuildContext buildContext) {
        return Commands.literal("atmos")
                .then(Commands.literal("add")
                        .then(substanceArg(buildContext)
                                .then(amountArg(SubstanceCommand::addSubstanceAtmos))
                        )
                )
                .then(Commands.literal("temperature")
                        .then(temperatureArg(SubstanceCommand::addTemperatureAtmos)))
                .then(Commands.literal("show")
                        .executes(context -> showAtmosContents(context.getSource())));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Holder.Reference<Substance>> substanceArg(CommandBuildContext buildContext) {
        return Commands.argument("substance", ResourceArgument.resource(buildContext, SUBSTANCE_REGISTRY_KEY));
    }

    private interface SubstanceAmountAction {
        int run(CommandSourceStack source, Holder<Substance> substance, int amount);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Integer> amountArg(SubstanceAmountAction action) {
        return Commands.argument("amount", IntegerArgumentType.integer())
                .executes(context -> action.run(
                        context.getSource(),
                        ResourceArgument.getResource(context, "substance", SUBSTANCE_REGISTRY_KEY),
                        IntegerArgumentType.getInteger(context, "amount")
                ));
    }

    private interface TemperatureAction {
        int run(CommandSourceStack source, float temperature);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Float> temperatureArg(TemperatureAction action) {
        return Commands.argument("temperature", FloatArgumentType.floatArg())
                .executes(context -> action.run(
                        context.getSource(),
                        FloatArgumentType.getFloat(context, "temperature")
                ));
    }

    private static int addSubstanceFluid(@NotNull CommandSourceStack source, Holder<Substance> substance, int amount) {
        Level level = source.getLevel();
        BlockPos pos = blockPosOf(source);

        if (!level.getFluidState(pos).is(BitterFluids.SUBSTANCE_FLUID.get())) {
            FluidState newState = BitterFluids.SUBSTANCE_FLUID.get().defaultFluidState().setValue(LEVEL, 1);
            level.setBlock(pos, newState.createLegacyBlock(), Block.UPDATE_CLIENTS);
        }

        if (!(level.getBlockEntity(pos) instanceof SubstanceFluidBlockEntity fluidBE)) {
            return 0;
        }

        SubstanceStack stack = new SubstanceStack(substance, amount);
        fluidBE.getMixture().updateSubstance(stack);
        source.sendSuccess(() -> Component.literal(String.format("Added %d %s to the puddle", amount, stack.getSubstance().getName())), true);
        source.sendSuccess(() -> Component.literal(fluidBE.getMixture().getContentsDescription()), true);
        return 1;
    }

    private static int addTemperatureFluid(@NotNull CommandSourceStack source, float temperature) {
        if (!(requireFluidEntity(source) instanceof FluidBlockEntity fluidBlockEntity)) {
            return 0;
        }

        // TODO: Temperature not yet implemented
        // fluidBlockEntity.setTemperature(temperature);
        // source.sendSuccess(() -> Component.literal("Temperature: " + fluidBlockEntity.getTemperature()), true);
        return 1;
    }

    private static int showFluidContents(@NotNull CommandSourceStack source) {
        if (!(requireFluidEntity(source) instanceof SubstanceFluidBlockEntity fluidBlockEntity)) {
            return 0;
        }

        source.sendSuccess(() -> Component.literal(fluidBlockEntity.getMixture().getContentsDescription()), true);
        source.sendSuccess(() -> Component.literal("Volume: " + fluidBlockEntity.getVolume()), true);
        float temperature = fluidBlockEntity.getTemperature();
        float celsius = temperature - 273.15f;
        source.sendSuccess(() -> Component.literal("Temperature: " + temperature + "K, " + celsius + "°C"), true);
        return 1;
    }

    private static BlockEntity requireFluidEntity(@NotNull CommandSourceStack source) {
        BlockEntity blockEntity = source.getLevel().getBlockEntity(blockPosOf(source));
        if (!(blockEntity instanceof FluidBlockEntity) && !(blockEntity instanceof SubstanceFluidBlockEntity)) {
            source.sendFailure(Component.literal("You must be standing on a puddle block to use this command."));
            return null;
        }
        return blockEntity;
    }

    private static int addSubstanceContainerAmount(@NotNull CommandSourceStack source, Holder<Substance> substance, int amount) {
        return updateHeldContainer(source, item -> new SubstanceStack(substance, amount));
    }

    private static int addSubstanceContainerVolume(@NotNull CommandSourceStack source, Holder<Substance> substance, int amount) {
        return updateHeldContainer(source, item -> {
            SubstanceStack stack = new SubstanceStack(substance, 0);
            stack.setVolume(amount);
            return stack;
        });
    }

    private static int updateHeldContainer(@NotNull CommandSourceStack source, java.util.function.Function<SubstanceContainerItem, SubstanceStack> stackFactory) {
        ItemStack heldStack = source.getPlayer().getMainHandItem();
        if (!(heldStack.getItem() instanceof SubstanceContainerItem item)) {
            source.sendFailure(Component.literal("You must be holding a substance container to use this command."));
            return 0;
        }

        item.updateSubstance(heldStack, stackFactory.apply(item));
        return 1;
    }

    private static int showContainerContents(@NotNull CommandSourceStack source) {
        // TODO: implement
        source.sendFailure(Component.literal("Not yet implemented."));
        return 0;
    }

    private static int addSubstanceAtmos(@NotNull CommandSourceStack source, Holder<Substance> substance, int amount) {
        SubstanceStack stack = new SubstanceStack(substance, amount);
        AtmosHandler.releaseGas(source.getLevel(), blockPosOf(source), stack);
        source.sendSuccess(() -> Component.literal(String.format("Added %d %s to the atmosphere",
                amount, stack.getSubstance().getName())), true);

        return showAtmosContents(source);
    }

    private static int addTemperatureAtmos(@NotNull CommandSourceStack source, float temperature) {
        AtmosInstance instance = requireAtmosInstance(source);
        if (instance == null) return 0;

        instance.setTemperature(temperature, source.getLevel());
        source.sendSuccess(() -> Component.literal("Set atmosphere temperature to: " + temperature), true);
        return 1;
    }

    private static int showAtmosContents(@NotNull CommandSourceStack source) {
        AtmosInstance instance = requireAtmosInstance(source);
        if (instance == null) return 0;

        source.sendSuccess(() -> Component.literal("Temperature: " + instance.getTemperature() + " K"), true);
        source.sendSuccess(() -> Component.literal("Pressure: " + instance.getPressure() + " kPa"), true);

        if (instance.getGases().isEmpty()) {
            source.sendSuccess(() -> Component.literal("No gases present"), true);
        } else {
            source.sendSuccess(() -> Component.literal("Gases:"), true);
            for (SubstanceStack gas : instance.getGases()) {
                source.sendSuccess(() -> Component.literal(" - " + gas.getSubstance().getName() + ": " + gas.getAmount()), true);
            }
        }

        return 1;
    }

    private static AtmosInstance requireAtmosInstance(@NotNull CommandSourceStack source) {
        AtmosInstance instance = AtmosHandler.getAtmosInstanceAt(source.getLevel(), blockPosOf(source));
        if (instance == null) {
            source.sendFailure(Component.literal("No atmosphere found at current position."));
        }
        return instance;
    }

    private static BlockPos blockPosOf(@NotNull CommandSourceStack source) {
        return BlockPos.containing(source.getPosition());
    }
}
