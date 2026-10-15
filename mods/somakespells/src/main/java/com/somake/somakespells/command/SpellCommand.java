package com.somake.somakespells.command;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.somake.somakespells.capabilities.PlayerSpellMastery;
import com.somake.somakespells.config.Config;
import com.somake.somakespells.network.SyncSpellMasteryPacket;
import com.somake.somakespells.registries.ModAttachments;
import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import io.redspace.ironsspellbooks.util.UpgradeUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpellCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      if ((Boolean)Config.ENABLE_SPELL_LOCK_SYSTEM.get()) {
         dispatcher.register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                              "somake"
                           )
                           .requires(source -> source.hasPermission(2)))
                        .then(
                           Commands.literal("learn")
                              .then(
                                 Commands.argument("target", EntityArgument.player())
                                    .then(
                                       Commands.argument("spell", ResourceLocationArgument.id())
                                          .suggests(SpellCommand::suggestSpells)
                                          .then(Commands.argument("level", IntegerArgumentType.integer(0, 100)).executes(SpellCommand::learnSpell))
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("forget")
                           .then(
                              Commands.argument("target", EntityArgument.player())
                                 .then(
                                    Commands.argument("spell", ResourceLocationArgument.id())
                                       .suggests(SpellCommand::suggestSpells)
                                       .executes(SpellCommand::forgetSpell)
                                 )
                           )
                     ))
                  .then(
                     Commands.literal("upgrade")
                        .then(
                           Commands.argument("target", EntityArgument.player())
                              .then(
                                 Commands.argument("type", ResourceKeyArgument.key(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY))
                                    .then(
                                       ((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer(1))
                                             .executes(SpellCommand::upgradeItem))
                                          .then(Commands.argument("slot", StringArgumentType.word()).executes(SpellCommand::upgradeItemWithSlot))
                                    )
                              )
                        )
                  ))
               .then(
                  Commands.literal("downgrade")
                     .then(
                        Commands.argument("target", EntityArgument.player())
                           .then(
                              Commands.argument("type", ResourceKeyArgument.key(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY))
                                 .then(
                                    ((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer(1)).executes(SpellCommand::downgradeItem))
                                       .then(Commands.argument("slot", StringArgumentType.word()).executes(SpellCommand::downgradeItemWithSlot))
                                 )
                           )
                     )
               )
         );
      }
   }

   private static CompletableFuture<Suggestions> suggestSpells(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggestResource(SpellRegistry.getEnabledSpells().stream().map(AbstractSpell::getSpellResource), builder);
   }

   private static int learnSpell(CommandContext<CommandSourceStack> context) {
      try {
         ServerPlayer player = EntityArgument.getPlayer(context, "target");
         ResourceLocation spellId = ResourceLocationArgument.getId(context, "spell");
         AbstractSpell spell = SpellRegistry.getSpell(spellId);
         if (spell == SpellRegistry.none()) {
            ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Spell not found: " + spellId));
            return 0;
         } else {
            int level = IntegerArgumentType.getInteger(context, "level");
            PlayerSpellMastery mastery = (PlayerSpellMastery)player.getData(ModAttachments.PLAYER_SPELL_MASTERY);
            mastery.setUnlockedLevel(spellId.toString(), level);
            PacketDistributor.sendToPlayer(player, new SyncSpellMasteryPacket(mastery.getAllUnlockedSpells()), new CustomPacketPayload[0]);
            ((CommandSourceStack)context.getSource())
               .sendSuccess(() -> Component.literal("Set spell " + spellId + " level to " + level + " for " + player.getName().getString()), true);
            return 1;
         }
      } catch (Exception var6) {
         ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Error executing command: " + var6.getMessage()));
         return 0;
      }
   }

   private static int forgetSpell(CommandContext<CommandSourceStack> context) {
      try {
         ServerPlayer player = EntityArgument.getPlayer(context, "target");
         ResourceLocation spellId = ResourceLocationArgument.getId(context, "spell");
         AbstractSpell spell = SpellRegistry.getSpell(spellId);
         if (spell == SpellRegistry.none()) {
            ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Spell not found: " + spellId));
            return 0;
         } else {
            PlayerSpellMastery mastery = (PlayerSpellMastery)player.getData(ModAttachments.PLAYER_SPELL_MASTERY);
            mastery.setUnlockedLevel(spellId.toString(), 0);
            PacketDistributor.sendToPlayer(player, new SyncSpellMasteryPacket(mastery.getAllUnlockedSpells()), new CustomPacketPayload[0]);
            ((CommandSourceStack)context.getSource())
               .sendSuccess(() -> Component.literal("Removed spell " + spellId + " from " + player.getName().getString()), true);
            return 1;
         }
      } catch (Exception var5) {
         ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Error executing command: " + var5.getMessage()));
         return 0;
      }
   }

   private static int upgradeItem(CommandContext<CommandSourceStack> context) {
      return upgradeItemInternal(context, null);
   }

   private static int upgradeItemWithSlot(CommandContext<CommandSourceStack> context) {
      return upgradeItemInternal(context, StringArgumentType.getString(context, "slot"));
   }

   private static int upgradeItemInternal(CommandContext<CommandSourceStack> context, String slotArg) {
      try {
         ServerPlayer player = EntityArgument.getPlayer(context, "target");
         ItemStack stack = player.getMainHandItem();
         if (stack.isEmpty()) {
            ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Target is not holding an item"));
            return 0;
         } else {
            ResourceKey<UpgradeOrbType> resourcekey = (ResourceKey<UpgradeOrbType>)context.getArgument("type", ResourceKey.class);
            int amount = IntegerArgumentType.getInteger(context, "amount");
            UpgradeData upgradeData = UpgradeData.getUpgradeData(stack);
            String slot;
            if (slotArg != null) {
               slot = slotArg;
            } else if (upgradeData != UpgradeData.NONE) {
               slot = upgradeData.getUpgradedSlot();
            } else {
               slot = UpgradeUtils.getRelevantEquipmentSlot(stack);
            }

            Holder<UpgradeOrbType> upgradeTypeHolder = (Holder<UpgradeOrbType>)UpgradeOrbTypeRegistry.upgradeTypeRegistry(
                  ((CommandSourceStack)context.getSource()).registryAccess()
               )
               .getHolder(resourcekey)
               .orElseThrow();
            boolean forceSlot = slotArg != null && (upgradeData == UpgradeData.NONE || !upgradeData.getUpgradedSlot().equals(slotArg));
            if (forceSlot && upgradeData != UpgradeData.NONE) {
               Map<Holder<UpgradeOrbType>, Integer> upgrades = new HashMap<>(upgradeData.upgrades());
               int currentCount = upgrades.getOrDefault(upgradeTypeHolder, 0);
               upgrades.put(upgradeTypeHolder, currentCount + amount);
               UpgradeData newUpgradeData = new UpgradeData(ImmutableMap.copyOf(upgrades), slot);
               UpgradeData.set(stack, newUpgradeData);
            } else {
               for (int i = 0; i < amount; i++) {
                  UpgradeData.set(stack, UpgradeData.getUpgradeData(stack).addUpgrade(stack, upgradeTypeHolder, slot));
               }
            }

            ((CommandSourceStack)context.getSource())
               .sendSuccess(
                  () -> Component.literal(
                     "Upgraded item for " + player.getName().getString() + " with " + resourcekey.location() + " x" + amount + " (slot: " + slot + ")"
                  ),
                  true
               );
            return amount;
         }
      } catch (Exception var13) {
         ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Error executing upgrade: " + var13.getMessage()));
         return 0;
      }
   }

   private static int downgradeItem(CommandContext<CommandSourceStack> context) {
      return downgradeItemInternal(context, null);
   }

   private static int downgradeItemWithSlot(CommandContext<CommandSourceStack> context) {
      return downgradeItemInternal(context, StringArgumentType.getString(context, "slot"));
   }

   private static int downgradeItemInternal(CommandContext<CommandSourceStack> context, String slotArg) {
      try {
         ServerPlayer player = EntityArgument.getPlayer(context, "target");
         ItemStack stack = player.getMainHandItem();
         if (stack.isEmpty()) {
            ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Target is not holding an item"));
            return 0;
         } else {
            ResourceKey<UpgradeOrbType> resourcekey = (ResourceKey<UpgradeOrbType>)context.getArgument("type", ResourceKey.class);
            int amount = IntegerArgumentType.getInteger(context, "amount");
            Holder<UpgradeOrbType> upgradeTypeHolder = (Holder<UpgradeOrbType>)UpgradeOrbTypeRegistry.upgradeTypeRegistry(
                  ((CommandSourceStack)context.getSource()).registryAccess()
               )
               .getHolder(resourcekey)
               .orElseThrow();
            UpgradeData upgradeData = UpgradeData.getUpgradeData(stack);
            Map<Holder<UpgradeOrbType>, Integer> upgrades = upgradeData.upgrades();
            if (!upgrades.containsKey(upgradeTypeHolder)) {
               ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Item does not have upgrade: " + resourcekey.location()));
               return 0;
            } else {
               int currentCount = upgrades.get(upgradeTypeHolder);
               int newCount = Math.max(0, currentCount - amount);
               Builder<Holder<UpgradeOrbType>, Integer> builder = ImmutableMap.builder();
               if (newCount > 0) {
                  builder.put(upgradeTypeHolder, newCount);
               }

               for (Entry<Holder<UpgradeOrbType>, Integer> entry : upgrades.entrySet()) {
                  if (!entry.getKey().equals(upgradeTypeHolder)) {
                     builder.put(entry.getKey(), entry.getValue());
                  }
               }

               String targetSlot = slotArg != null ? slotArg : upgradeData.getUpgradedSlot();
               UpgradeData newUpgradeData = new UpgradeData(builder.build(), targetSlot);
               UpgradeData.set(stack, newUpgradeData);
               ((CommandSourceStack)context.getSource())
                  .sendSuccess(
                     () -> Component.literal(
                        "Downgraded item for "
                           + player.getName().getString()
                           + " with "
                           + resourcekey.location()
                           + " -"
                           + amount
                           + " (slot: "
                           + targetSlot
                           + ")"
                     ),
                     true
                  );
               return amount;
            }
         }
      } catch (Exception var14) {
         ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Error executing downgrade: " + var14.getMessage()));
         return 0;
      }
   }
}
