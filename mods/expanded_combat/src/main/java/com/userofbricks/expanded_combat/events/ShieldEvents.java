package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.init.DataMaps;
import com.userofbricks.expanded_combat.init.ECEnchantments;
import com.userofbricks.expanded_combat.init.PluginInit;
import com.userofbricks.expanded_combat.item.ArrowBlockWeaponItem;
import com.userofbricks.expanded_combat.item.ECShieldItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

public class ShieldEvents {
   @SubscribeEvent
   public static void ShieldBlockingEvent(LivingShieldBlockEvent event) {
      if (!ExpandedCombat.CONFIG.shieldProtectionConfig.EnableVanillaStyleShieldProtection
         && !(event.getEntity().getUseItem().getItem() instanceof ArrowBlockWeaponItem)) {
         ItemStack shieldItemStack = event.getEntity().getUseItem();
         float damageBlocked = 0.0F;
         float damageLeftToBlock = event.getOriginalBlockedDamage();
         if (ExpandedCombat.CONFIG.shieldProtectionConfig.EnableShieldBaseProtection) {
            damageBlocked += BaseShieldProtection(
               shieldItemStack, damageLeftToBlock, event.getEntity().level().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
            );
            damageLeftToBlock -= damageBlocked;
         }

         if (ExpandedCombat.CONFIG.shieldProtectionConfig.EnableShieldProtectionPercentage) {
            double damagePercent = ExpandedCombat.CONFIG.vanilla.defense.afterBasePercentReduction();
            if (shieldItemStack.getItem() instanceof ECShieldItem) {
               damagePercent = ECShieldItem.getPercentageProtection(shieldItemStack);
            } else if (shieldItemStack.getItemHolder().getData(DataMaps.SHIELD_MATERIALS) != null) {
               damagePercent = PluginInit.getShieldToMaterialPercentageProtection(shieldItemStack);
            }

            damageBlocked += (float)(damageLeftToBlock * damagePercent);
         }

         event.setBlockedDamage(damageBlocked);
      }
   }

   private static float BaseShieldProtection(ItemStack shieldItemStack, float damageLeftToBlock, Registry<Enchantment> enchantmentRegistry) {
      float damageBlocked = 0.0F;
      switch (ExpandedCombat.CONFIG.shieldProtectionConfig.shieldBaseProtectionType) {
         case PREDEFINED_AMMOUNT:
            double protectionAmount = ExpandedCombat.CONFIG.vanilla.defense.baseProtectionAmmount();
            if (shieldItemStack.getItem() instanceof ECShieldItem) {
               protectionAmount = ECShieldItem.getBaseProtection(shieldItemStack);
            } else if (shieldItemStack.getItemHolder().getData(DataMaps.SHIELD_MATERIALS) != null) {
               protectionAmount = PluginInit.getShieldToMaterialBaseProtection(shieldItemStack);
            }

            damageBlocked = (float)protectionAmount + shieldItemStack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.BLOCKING));
            break;
         case DURABILITY_PERCENTAGE:
            if (shieldItemStack.getMaxDamage() == 0) {
               damageBlocked = damageLeftToBlock;
            } else {
               float itemDamageLeft = Math.min(
                  (float)shieldItemStack.getMaxDamage(),
                  (float)shieldItemStack.getMaxDamage()
                     - shieldItemStack.getDamageValue()
                     + shieldItemStack.getMaxDamage()
                        * (shieldItemStack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.BLOCKING)) / 10.0F)
               );
               damageBlocked = damageLeftToBlock * (itemDamageLeft / shieldItemStack.getMaxDamage());
            }
            break;
         case INVERTED_DURABILITY_PERCENTAGE:
            if (shieldItemStack.getMaxDamage() != 0) {
               damageBlocked += damageLeftToBlock
                  * (
                     (
                           shieldItemStack.getDamageValue()
                              + shieldItemStack.getMaxDamage()
                                 * (shieldItemStack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.BLOCKING)) / 10.0F)
                        )
                        / shieldItemStack.getMaxDamage()
                  );
            }
      }

      return damageBlocked;
   }
}
