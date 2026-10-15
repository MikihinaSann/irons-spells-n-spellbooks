package com.somake.somakespells.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.somake.somakespells.client.renderer.blockentity.UpgradeForgeBlockEntityRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class DevBookCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                           "dev_book"
                        )
                        .then(
                           Commands.literal("pos")
                              .then(
                                 Commands.argument("x", FloatArgumentType.floatArg())
                                    .then(
                                       Commands.argument("y", FloatArgumentType.floatArg())
                                          .then(Commands.argument("z", FloatArgumentType.floatArg()).executes(context -> {
                                             float x = FloatArgumentType.getFloat(context, "x");
                                             float y = FloatArgumentType.getFloat(context, "y");
                                             float z = FloatArgumentType.getFloat(context, "z");
                                             UpgradeForgeBlockEntityRenderer.setBookTranslation(x, y, z);
                                             sendFeedback(
                                                (CommandSourceStack)context.getSource(), "Posição", UpgradeForgeBlockEntityRenderer.describeTransform()
                                             );
                                             return 1;
                                          }))
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("rot")
                           .then(
                              Commands.argument("x", FloatArgumentType.floatArg())
                                 .then(Commands.argument("y", FloatArgumentType.floatArg()).executes(context -> {
                                    float x = FloatArgumentType.getFloat(context, "x");
                                    float y = FloatArgumentType.getFloat(context, "y");
                                    UpgradeForgeBlockEntityRenderer.setBookRotation(x, y);
                                    sendFeedback((CommandSourceStack)context.getSource(), "Rotação", UpgradeForgeBlockEntityRenderer.describeTransform());
                                    return 1;
                                 }))
                           )
                     ))
                  .then(Commands.literal("scale").then(Commands.argument("value", FloatArgumentType.floatArg(0.01F)).executes(context -> {
                     float value = FloatArgumentType.getFloat(context, "value");
                     UpgradeForgeBlockEntityRenderer.setBookScale(value);
                     sendFeedback((CommandSourceStack)context.getSource(), "Escala", UpgradeForgeBlockEntityRenderer.describeTransform());
                     return 1;
                  }))))
               .then(Commands.literal("show").executes(context -> {
                  sendFeedback((CommandSourceStack)context.getSource(), "Transform atual", UpgradeForgeBlockEntityRenderer.describeTransform());
                  return 1;
               })))
            .then(Commands.literal("reset").executes(context -> {
               UpgradeForgeBlockEntityRenderer.resetBookTransform();
               sendFeedback((CommandSourceStack)context.getSource(), "Reset", UpgradeForgeBlockEntityRenderer.describeTransform());
               return 1;
            }))
      );
   }

   private static void sendFeedback(CommandSourceStack source, String label, String value) {
      if (source.getEntity() instanceof LocalPlayer player) {
         player.displayClientMessage(Component.literal(label + ": " + value), false);
      }
   }
}
