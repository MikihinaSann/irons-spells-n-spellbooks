package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.registry.IExpandedCombatPlugin;
import com.userofbricks.expanded_combat.api.registry.RegistrationHandler;
import com.userofbricks.expanded_combat.api.registry.ShieldMaterialUseTick;
import com.userofbricks.expanded_combat.api.weapon_type.WeaponType;
import com.userofbricks.expanded_combat.data_components.ShieldMaterials;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PluginInit {
   public static List<ShieldMaterialUseTick> shieldMaterialUseTickList = new ArrayList<>();
   public static Map<ResourceLocation, Material> materials = new HashMap<>();
   public static Map<ResourceLocation, WeaponType> weaponTypes = new HashMap<>();

   public static void loadClass() {
      for (IExpandedCombatPlugin plugin : ExpandedCombat.PLUGINS) {
         plugin.registerMaterials(new RegistrationHandler());
         shieldMaterialUseTickList.addAll(plugin.registerShieldMaterialUseTick());
      }
   }

   @Nullable
   public static ShieldMaterialUseTick getShieldUseTickEntry(Material material) {
      for (ShieldMaterialUseTick shieldMaterialUseTick : shieldMaterialUseTickList) {
         if (material == shieldMaterialUseTick.material()) {
            return shieldMaterialUseTick;
         }
      }

      return null;
   }

   public static double getShieldToMaterialBaseProtection(ItemStack stack) {
      ShieldMaterials materials = (ShieldMaterials)stack.get(ItemDataComponents.SHIELD_MATERIALS);
      if (materials == null) {
         return ExpandedCombat.CONFIG.vanilla.defense.baseProtectionAmmount();
      } else {
         double ul = materials.ULMaterial().defense().baseProtectionAmmount() / 5.0F;
         double ur = materials.URMaterial().defense().baseProtectionAmmount() / 5.0F;
         double dl = materials.DLMaterial().defense().baseProtectionAmmount() / 5.0F;
         double dr = materials.DRMaterial().defense().baseProtectionAmmount() / 5.0F;
         double m = materials.MMaterial().defense().baseProtectionAmmount() / 5.0F;
         return ul + ur + dl + dr + m;
      }
   }

   public static double getShieldToMaterialPercentageProtection(ItemStack stack) {
      ShieldMaterials materials = (ShieldMaterials)stack.get(ItemDataComponents.SHIELD_MATERIALS);
      if (materials == null) {
         return ExpandedCombat.CONFIG.vanilla.defense.baseProtectionAmmount();
      } else {
         double ul = materials.ULMaterial().defense().afterBasePercentReduction() / 5.0F;
         double ur = materials.URMaterial().defense().afterBasePercentReduction() / 5.0F;
         double dl = materials.DLMaterial().defense().afterBasePercentReduction() / 5.0F;
         double dr = materials.DRMaterial().defense().afterBasePercentReduction() / 5.0F;
         double m = materials.MMaterial().defense().afterBasePercentReduction() / 5.0F;
         return ul + ur + dl + dr + m;
      }
   }
}
