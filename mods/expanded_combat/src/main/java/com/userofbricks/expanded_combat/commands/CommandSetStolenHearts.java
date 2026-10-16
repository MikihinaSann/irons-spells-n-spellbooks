package com.userofbricks.expanded_combat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.userofbricks.expanded_combat.datagen.LangStrings;
import com.userofbricks.expanded_combat.init.DataAttachments;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CommandSetStolenHearts {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(
      p_137029_ -> Component.translatable(LangStrings.stolenHeartsNotValidEntity, new Object[]{p_137029_})
   );
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(
      Component.translatable(LangStrings.stolenHeartsNothingHappened)
   );

   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
      return ((LiteralArgumentBuilder)Commands.literal("SetStolenHearts").requires(cs -> cs.hasPermission(1)))
         .then(
            Commands.argument("targets", EntityArgument.entities())
               .then(
                  Commands.argument("amount", IntegerArgumentType.integer())
                     .executes(
                        commandSourceStackSource -> setStolenHearts(
                           (CommandSourceStack)commandSourceStackSource.getSource(),
                           EntityArgument.getEntities(commandSourceStackSource, "targets"),
                           (Integer)commandSourceStackSource.getArgument("amount", Integer.class)
                        )
                     )
               )
         );
   }

   private static int setStolenHearts(CommandSourceStack source, Collection<? extends Entity> targets, Integer amount) throws CommandSyntaxException {
      int i = 0;

      for (Entity entity : targets) {
         if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setData(DataAttachments.STOLEN_HEALTH.get(), amount);
            i++;
         } else if (targets.size() == 1) {
            throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
         }
      }

      if (i == 0) {
         throw ERROR_NOTHING_HAPPENED.create();
      } else {
         if (targets.size() == 1) {
            source.sendSuccess(
               () -> Component.translatable(LangStrings.stolenHeartsSuccess, new Object[]{amount, targets.iterator().next().getDisplayName()}), true
            );
         } else {
            source.sendSuccess(() -> Component.translatable(LangStrings.stolenHeartsSucceses, new Object[]{amount, targets.size()}), true);
         }

         return i;
      }
   }
}
