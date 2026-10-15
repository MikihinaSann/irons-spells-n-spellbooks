package com.somake.somakespells.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.somake.somakespells.client.RedSoulWingTransform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class RedSoulWingCommand {
   private static final SuggestionProvider<CommandSourceStack> STATE_SUGGESTIONS = (context, builder) -> {
      for (RedSoulWingTransform.WingState state : RedSoulWingTransform.WingState.values()) {
         builder.suggest(state.id());
      }

      return builder.buildFuture();
   };

   private RedSoulWingCommand() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                       "red_soul_wings"
                                    )
                                    .then(
                                       Commands.literal("pos")
                                          .then(
                                             Commands.argument("x", FloatArgumentType.floatArg(-10.0F, 10.0F))
                                                .then(
                                                   Commands.argument("y", FloatArgumentType.floatArg(-10.0F, 10.0F))
                                                      .then(
                                                         Commands.argument("z", FloatArgumentType.floatArg(-10.0F, 10.0F))
                                                            .executes(
                                                               context -> {
                                                                  RedSoulWingTransform.setPosition(
                                                                     FloatArgumentType.getFloat(context, "x"),
                                                                     FloatArgumentType.getFloat(context, "y"),
                                                                     FloatArgumentType.getFloat(context, "z")
                                                                  );
                                                                  return show((CommandSourceStack)context.getSource());
                                                               }
                                                            )
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.literal("rot")
                                       .then(
                                          Commands.argument("x", FloatArgumentType.floatArg(-360.0F, 360.0F))
                                             .then(
                                                Commands.argument("y", FloatArgumentType.floatArg(-360.0F, 360.0F))
                                                   .then(
                                                      Commands.argument("z", FloatArgumentType.floatArg(-360.0F, 360.0F))
                                                         .executes(
                                                            context -> {
                                                               RedSoulWingTransform.setRotation(
                                                                  FloatArgumentType.getFloat(context, "x"),
                                                                  FloatArgumentType.getFloat(context, "y"),
                                                                  FloatArgumentType.getFloat(context, "z")
                                                               );
                                                               return show((CommandSourceStack)context.getSource());
                                                            }
                                                         )
                                                   )
                                             )
                                       )
                                 ))
                              .then(Commands.literal("scale").then(Commands.argument("value", FloatArgumentType.floatArg(0.05F, 10.0F)).executes(context -> {
                                 RedSoulWingTransform.setScale(FloatArgumentType.getFloat(context, "value"));
                                 return show((CommandSourceStack)context.getSource());
                              }))))
                           .then(
                              Commands.literal("preview")
                                 .then(
                                    Commands.argument("state", StringArgumentType.word())
                                       .suggests(STATE_SUGGESTIONS)
                                       .executes(
                                          context -> {
                                             RedSoulWingTransform.setPreviewState(
                                                parseState((CommandSourceStack)context.getSource(), StringArgumentType.getString(context, "state"), true)
                                             );
                                             return show((CommandSourceStack)context.getSource());
                                          }
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("pose")
                              .then(
                                 Commands.argument("state", StringArgumentType.word())
                                    .suggests(STATE_SUGGESTIONS)
                                    .then(
                                       Commands.argument("x", FloatArgumentType.floatArg(-360.0F, 360.0F))
                                          .then(
                                             Commands.argument("y", FloatArgumentType.floatArg(-360.0F, 360.0F))
                                                .then(
                                                   Commands.argument("z", FloatArgumentType.floatArg(-360.0F, 360.0F))
                                                      .then(
                                                         Commands.argument("edge_y", FloatArgumentType.floatArg(-360.0F, 360.0F))
                                                            .executes(
                                                               context -> {
                                                                  RedSoulWingTransform.WingState state = parseState(
                                                                     (CommandSourceStack)context.getSource(),
                                                                     StringArgumentType.getString(context, "state"),
                                                                     false
                                                                  );
                                                                  RedSoulWingTransform.setPoseDegrees(
                                                                     state,
                                                                     FloatArgumentType.getFloat(context, "x"),
                                                                     FloatArgumentType.getFloat(context, "y"),
                                                                     FloatArgumentType.getFloat(context, "z"),
                                                                     FloatArgumentType.getFloat(context, "edge_y")
                                                                  );
                                                                  RedSoulWingTransform.setPreviewState(state);
                                                                  return showPose((CommandSourceStack)context.getSource(), state);
                                                               }
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("show_pose")
                           .then(
                              Commands.argument("state", StringArgumentType.word())
                                 .suggests(STATE_SUGGESTIONS)
                                 .executes(
                                    context -> showPose(
                                       (CommandSourceStack)context.getSource(),
                                       parseState((CommandSourceStack)context.getSource(), StringArgumentType.getString(context, "state"), false)
                                    )
                                 )
                           )
                     ))
                  .then(
                     Commands.literal("reset_pose")
                        .then(
                           Commands.argument("state", StringArgumentType.word())
                              .suggests(STATE_SUGGESTIONS)
                              .executes(
                                 context -> {
                                    RedSoulWingTransform.WingState state = parseState(
                                       (CommandSourceStack)context.getSource(), StringArgumentType.getString(context, "state"), false
                                    );
                                    RedSoulWingTransform.resetPose(state);
                                    return showPose((CommandSourceStack)context.getSource(), state);
                                 }
                              )
                        )
                  ))
               .then(Commands.literal("show").executes(context -> show((CommandSourceStack)context.getSource()))))
            .then(Commands.literal("reset").executes(context -> {
               RedSoulWingTransform.reset();
               return show((CommandSourceStack)context.getSource());
            }))
      );
   }

   private static int show(CommandSourceStack source) {
      source.sendSuccess(() -> Component.literal("Red Soul wings: " + RedSoulWingTransform.describe()), false);
      return 1;
   }

   private static int showPose(CommandSourceStack source, RedSoulWingTransform.WingState state) {
      source.sendSuccess(() -> Component.literal("Red Soul wings: " + RedSoulWingTransform.describePose(state)), false);
      return 1;
   }

   private static RedSoulWingTransform.WingState parseState(CommandSourceStack source, String value, boolean allowAuto) {
      for (RedSoulWingTransform.WingState state : RedSoulWingTransform.WingState.values()) {
         if (state.id().equalsIgnoreCase(value) && (allowAuto || state != RedSoulWingTransform.WingState.AUTO)) {
            return state;
         }
      }

      source.sendFailure(Component.literal("Estado inválido: " + value));
      return allowAuto ? RedSoulWingTransform.WingState.AUTO : RedSoulWingTransform.WingState.IDLE;
   }
}
