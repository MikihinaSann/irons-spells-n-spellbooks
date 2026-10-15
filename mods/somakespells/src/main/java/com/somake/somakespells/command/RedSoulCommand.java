package com.somake.somakespells.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.somake.somakespells.entity.RedEchoEntity;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class RedSoulCommand {
   private RedSoulCommand() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("somake").requires(source -> source.hasPermission(2)))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("red_soul")
                           .executes(
                              context -> toggle(
                                 (CommandSourceStack)context.getSource(), List.of(((CommandSourceStack)context.getSource()).getPlayerOrException())
                              )
                           ))
                        .then(
                           Commands.literal("on")
                              .executes(
                                 context -> start(
                                    (CommandSourceStack)context.getSource(), List.of(((CommandSourceStack)context.getSource()).getPlayerOrException())
                                 )
                              )
                        ))
                     .then(
                        Commands.literal("off")
                           .executes(
                              context -> cancel(
                                 (CommandSourceStack)context.getSource(), List.of(((CommandSourceStack)context.getSource()).getPlayerOrException())
                              )
                           )
                     ))
                  .then(
                     ((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players())
                              .executes(context -> start((CommandSourceStack)context.getSource(), EntityArgument.getPlayers(context, "targets"))))
                           .then(
                              Commands.literal("on")
                                 .executes(context -> start((CommandSourceStack)context.getSource(), EntityArgument.getPlayers(context, "targets")))
                           ))
                        .then(
                           Commands.literal("off")
                              .executes(context -> cancel((CommandSourceStack)context.getSource(), EntityArgument.getPlayers(context, "targets")))
                        )
                  )
            )
      );
   }

   private static int toggle(CommandSourceStack source, Collection<ServerPlayer> players) {
      return start(source, players);
   }

   private static int start(CommandSourceStack source, Collection<ServerPlayer> players) {
      int started = 0;

      for (ServerPlayer player : players) {
         if (RedEchoEntity.startDuel(player)) {
            started++;
         }
      }

      int count = started;
      source.sendSuccess(() -> Component.literal("Duelo Red Echo iniciado para " + count + " jogador(es)."), true);
      return players.size();
   }

   private static int cancel(CommandSourceStack source, Collection<ServerPlayer> players) {
      int canceled = 0;

      for (ServerPlayer player : players) {
         if (RedEchoEntity.cancelDuel(player, true)) {
            canceled++;
         }
      }

      int count = canceled;
      source.sendSuccess(() -> Component.literal("Duelo Red Echo cancelado para " + count + " jogador(es)."), true);
      return players.size();
   }
}
