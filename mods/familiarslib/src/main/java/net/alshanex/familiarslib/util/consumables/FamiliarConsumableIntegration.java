package net.alshanex.familiarslib.util.consumables;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.registry.ComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FamiliarConsumableIntegration {
   private static final Map<UUID, FamiliarConsumableSystem.ConsumableData> consumableDataCache = new ConcurrentHashMap<>();
   private static final String CONSUMABLE_NBT_KEY = "FamiliarConsumableData";

   public static FamiliarConsumableSystem.ConsumableData getConsumableData(AbstractSpellCastingPet familiar) {
      UUID familiarId = familiar.getUUID();
      FamiliarConsumableSystem.ConsumableData cachedData = consumableDataCache.get(familiarId);
      if (cachedData != null) {
         return cachedData;
      } else {
         FamiliarConsumableSystem.ConsumableData data = loadFromEntityNBT(familiar);
         consumableDataCache.put(familiarId, data);
         return data;
      }
   }

   private static FamiliarConsumableSystem.ConsumableData loadFromEntityNBT(AbstractSpellCastingPet familiar) {
      if (familiar.getPersistentData().contains("FamiliarConsumableData")) {
         CompoundTag consumableNBT = familiar.getPersistentData().getCompound("FamiliarConsumableData");
         return FamiliarConsumableSystem.loadConsumableDataFromNBT(consumableNBT);
      } else {
         return new FamiliarConsumableSystem.ConsumableData();
      }
   }

   private static void saveToEntityNBT(AbstractSpellCastingPet familiar, FamiliarConsumableSystem.ConsumableData data) {
      UUID familiarId = familiar.getUUID();
      consumableDataCache.put(familiarId, data);
      CompoundTag consumableNBT = new CompoundTag();
      FamiliarConsumableSystem.saveConsumableDataToNBT(data, consumableNBT);
      familiar.getPersistentData().put("FamiliarConsumableData", consumableNBT);
      FamiliarsLib.LOGGER.debug("Saved consumable data to entity NBT for familiar {}: {}", familiarId, data.toString());
   }

   private static void updateConsumableData(AbstractSpellCastingPet familiar, FamiliarConsumableSystem.ConsumableData data) {
      saveToEntityNBT(familiar, data);
   }

   public static FamiliarConsumableComponent getConsumableComponent(ItemStack itemStack) {
      return (FamiliarConsumableComponent)itemStack.get(ComponentRegistry.FAMILIAR_CONSUMABLE.get());
   }

   public static boolean isConsumableItem(ItemStack itemStack) {
      return itemStack.has(ComponentRegistry.FAMILIAR_CONSUMABLE.get());
   }

   public static ItemStack makeConsumableItem(ItemStack itemStack, FamiliarConsumableSystem.ConsumableType type, int tier) {
      FamiliarConsumableComponent component = new FamiliarConsumableComponent(type, tier);
      itemStack.set(ComponentRegistry.FAMILIAR_CONSUMABLE.get(), component);
      return itemStack;
   }

   public static InteractionResult handleConsumableInteraction(AbstractSpellCastingPet familiar, Player player, ItemStack itemStack) {
      FamiliarConsumableComponent component = getConsumableComponent(itemStack);
      if (component == null) {
         return InteractionResult.PASS;
      } else if (!familiar.level().isClientSide) {
         FamiliarConsumableSystem.ConsumableData data = getConsumableData(familiar);
         int currentValue = data.getValue(component.type());
         int maxAllowed = component.getLimit();
         if (currentValue >= maxAllowed) {
            if (player instanceof ServerPlayer serverPlayer) {
               serverPlayer.connection
                  .send(
                     new ClientboundSetActionBarTextPacket(
                        Component.translatable(
                              "message.familiarslib.consumable.max_reached", new Object[]{Component.translatable(getTypeTranslationKey(component.type()))}
                           )
                           .withStyle(ChatFormatting.RED)
                     )
                  );
            }

            return InteractionResult.FAIL;
         } else {
            int maxUsableTier = getMaxUsableTier(data, component.type());
            if (component.tier() > maxUsableTier) {
               if (player instanceof ServerPlayer serverPlayer) {
                  serverPlayer.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable("message.familiarslib.consumable.tier_too_high", new Object[]{component.tier(), maxUsableTier})
                              .withStyle(ChatFormatting.RED)
                        )
                     );
               }

               return InteractionResult.FAIL;
            } else {
               float currentHealth = familiar.getHealth();
               boolean isHealthConsumable = component.type() == FamiliarConsumableSystem.ConsumableType.HEALTH;
               int bonus = component.getBonus();
               int newValue = Math.min(currentValue + bonus, maxAllowed);
               data.setValue(component.type(), newValue);
               FamiliarsLib.LOGGER
                  .debug("Applying consumable {} to familiar {}: {} -> {}", new Object[]{component.type(), familiar.getUUID(), currentValue, newValue});
               updateConsumableData(familiar, data);
               FamiliarConsumableSystem.applyAttributeModifiers(familiar, data);
               if (isHealthConsumable) {
                  familiar.setHealth(familiar.getMaxHealth());
                  FamiliarsLib.LOGGER.debug("Applied health consumable to familiar {}: healed to full health {}", familiar.getUUID(), familiar.getMaxHealth());
               }

               itemStack.shrink(1);
               String unit = getUnitSuffix(component.type());
               if (player instanceof ServerPlayer serverPlayer) {
                  serverPlayer.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable(
                                 "message.familiarslib.consumable.success",
                                 new Object[]{Component.translatable(getTypeTranslationKey(component.type())), newValue + unit}
                              )
                              .withStyle(ChatFormatting.GREEN)
                        )
                     );
               }

               spawnConsumableEffects(familiar);
               return InteractionResult.SUCCESS;
            }
         }
      } else {
         return InteractionResult.CONSUME;
      }
   }

   private static int getMaxUsableTier(FamiliarConsumableSystem.ConsumableData data, FamiliarConsumableSystem.ConsumableType type) {
      int currentValue = data.getValue(type);

      for (int tier = type.getMaxTier(); tier >= 1; tier--) {
         if (currentValue < type.getTierLimit(tier)) {
            return tier;
         }
      }

      return 0;
   }

   private static String getTypeTranslationKey(FamiliarConsumableSystem.ConsumableType type) {
      return switch (type) {
         case ARMOR -> "consumable.type.armor";
         case HEALTH -> "consumable.type.health";
         case SPELL_POWER -> "consumable.type.spell_power";
         case SPELL_RESIST -> "consumable.type.spell_resist";
         case ENRAGED -> "consumable.type.enraged";
         case BLOCKING -> "consumable.type.blocking";
      };
   }

   private static String getUnitSuffix(FamiliarConsumableSystem.ConsumableType type) {
      return switch (type) {
         case ARMOR -> " armor";
         case HEALTH -> "%";
         case SPELL_POWER -> "%";
         case SPELL_RESIST -> "%";
         case ENRAGED -> " stacks";
         case BLOCKING -> "";
      };
   }

   private static void spawnConsumableEffects(AbstractSpellCastingPet familiar) {
   }

   public static void saveConsumableData(AbstractSpellCastingPet familiar, CompoundTag compound) {
      UUID familiarId = familiar.getUUID();
      FamiliarConsumableSystem.ConsumableData data = getConsumableData(familiar);
      FamiliarConsumableSystem.saveConsumableDataToNBT(data, compound);
      FamiliarsLib.LOGGER.debug("Saving consumable data to NBT for familiar {}: {}", familiarId, data.toString());
   }

   public static void loadConsumableData(AbstractSpellCastingPet familiar, CompoundTag compound) {
      UUID familiarId = familiar.getUUID();
      FamiliarConsumableSystem.ConsumableData data = FamiliarConsumableSystem.loadConsumableDataFromNBT(compound);
      saveToEntityNBT(familiar, data);
      FamiliarsLib.LOGGER.debug("Loading consumable data from NBT for familiar {}: {}", familiarId, data.toString());
   }

   public static void removeConsumableModifiers(AbstractSpellCastingPet familiar) {
      FamiliarConsumableSystem.removeAllConsumableModifiers(familiar);
      clearCachedData(familiar);
   }

   public static void applyConsumableModifiers(AbstractSpellCastingPet familiar) {
      FamiliarConsumableSystem.ConsumableData data = getConsumableData(familiar);
      FamiliarConsumableSystem.applyAttributeModifiers(familiar, data);
      FamiliarsLib.LOGGER.debug("Applied consumable modifiers for familiar {}: {}", familiar.getUUID(), data.toString());
   }

   public static int getCurrentBonus(AbstractSpellCastingPet familiar, FamiliarConsumableSystem.ConsumableType type) {
      FamiliarConsumableSystem.ConsumableData data = getConsumableData(familiar);
      return data.getValue(type);
   }

   public static int getMaxUsableTier(AbstractSpellCastingPet familiar, FamiliarConsumableSystem.ConsumableType type) {
      FamiliarConsumableSystem.ConsumableData data = getConsumableData(familiar);
      return getMaxUsableTier(data, type);
   }

   public static void clearCachedData(AbstractSpellCastingPet familiar) {
      consumableDataCache.remove(familiar.getUUID());
   }

   public static boolean canFamiliarBlock(AbstractSpellCastingPet familiar) {
      FamiliarConsumableSystem.ConsumableData data = getConsumableData(familiar);
      return data.getValue(FamiliarConsumableSystem.ConsumableType.BLOCKING) > 0;
   }

   public static int getEnragedStacks(AbstractSpellCastingPet familiar) {
      FamiliarConsumableSystem.ConsumableData data = getConsumableData(familiar);
      return data.getValue(FamiliarConsumableSystem.ConsumableType.ENRAGED);
   }

   public static void migrateLegacyData(
      AbstractSpellCastingPet familiar, int legacyHealthStacks, int legacyArmorStacks, int legacyEnragedStacks, boolean legacyCanBlock
   ) {
      FamiliarConsumableSystem.ConsumableData data = getConsumableData(familiar);
      FamiliarConsumableSystem.removeLegacyModifiers(familiar);
      float currentHealth = familiar.getHealth();
      if (legacyHealthStacks > 0) {
         int healthPercentage = legacyHealthStacks * 10;
         healthPercentage = Math.min(healthPercentage, FamiliarConsumableSystem.ConsumableType.HEALTH.getMaxLimit());
         data.setValue(FamiliarConsumableSystem.ConsumableType.HEALTH, healthPercentage);
         FamiliarsLib.LOGGER.debug("Migrated {} legacy health stacks to {}% health bonus", legacyHealthStacks, healthPercentage);
      }

      if (legacyArmorStacks > 0) {
         int armorPoints = Math.min(legacyArmorStacks, FamiliarConsumableSystem.ConsumableType.ARMOR.getMaxLimit());
         data.setValue(FamiliarConsumableSystem.ConsumableType.ARMOR, armorPoints);
         FamiliarsLib.LOGGER.debug("Migrated {} legacy armor stacks to {} armor points", legacyArmorStacks, armorPoints);
      }

      if (legacyEnragedStacks > 0) {
         int enragedStacks = Math.min(legacyEnragedStacks, FamiliarConsumableSystem.ConsumableType.ENRAGED.getMaxLimit());
         data.setValue(FamiliarConsumableSystem.ConsumableType.ENRAGED, enragedStacks);
         FamiliarsLib.LOGGER.debug("Migrated {} legacy enraged stacks", enragedStacks);
      }

      if (legacyCanBlock) {
         data.setValue(FamiliarConsumableSystem.ConsumableType.BLOCKING, 1);
         FamiliarsLib.LOGGER.debug("Migrated legacy blocking ability");
      }

      updateConsumableData(familiar, data);
      FamiliarConsumableSystem.applyAttributeModifiers(familiar, data);
      float newMaxHealth = familiar.getMaxHealth();
      float restoredHealth = Math.min(currentHealth, newMaxHealth);
      familiar.setHealth(restoredHealth);
      FamiliarsLib.LOGGER
         .debug(
            "Completed legacy data migration for familiar {}: {} (health: {}/{})",
            new Object[]{familiar.getUUID(), data.toString(), familiar.getHealth(), familiar.getMaxHealth()}
         );
   }
}
