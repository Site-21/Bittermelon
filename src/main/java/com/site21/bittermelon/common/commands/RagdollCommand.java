package com.site21.bittermelon.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.site21.bittermelon.common.systems.ragdoll.RagdollUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RagdollCommand {
    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ragdoll")
                        .executes(RagdollCommand::ragdollSelf)
                .then(Commands.argument("target", EntityArgument.entities())
                        .executes(RagdollCommand::ragdoll))
                .then(Commands.literal("clear")
                        .executes(RagdollCommand::clearSelf)
                        .then(Commands.argument("target", EntityArgument.entities())
                                .executes(RagdollCommand::clear)))
        );
    }

    private static int ragdollSelf(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = context.getSource().getEntityOrException();
        if (!(target instanceof LivingEntity entity)) return 0;
        RagdollUtil.ragdoll(entity);
        return 1;
    }

    private static int ragdoll(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");

        for (Entity target : targets) {
            if (target instanceof LivingEntity entity) {
                RagdollUtil.ragdoll(entity);
            }
        }

        return targets.size();
    }

    private static int clearSelf(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = context.getSource().getEntityOrException();

        if (target instanceof LivingEntity entity) {
            RagdollUtil.clearRagdoll(entity);
        }

        return 1;
    }

    private static int clear(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");

        for (Entity target : targets) {
            if (target instanceof LivingEntity entity) {
                RagdollUtil.clearRagdoll(entity);
            }
        }

        return targets.size();
    }
}

