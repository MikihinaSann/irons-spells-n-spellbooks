package com.userofbricks.expanded_combat.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.userofbricks.expanded_combat.commands.CommandIncreaseCharge;
import com.userofbricks.expanded_combat.commands.CommandSetAddedHearts;
import com.userofbricks.expanded_combat.commands.CommandSetStolenHearts;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> commands = dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("expanded_combat")
                  .then(CommandIncreaseCharge.register(dispatcher)))
               .then(CommandSetStolenHearts.register(dispatcher)))
            .then(CommandSetAddedHearts.register(dispatcher))
      );
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("EC").redirect(commands));
   }
}
