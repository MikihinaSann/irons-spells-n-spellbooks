package com.somake.somakespells.item;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class MirroredEdgeItem extends BaseAttributeSwordItem {
   public MirroredEdgeItem(Properties properties) {
      super(
         Tiers.IRON,
         14,
         -3.0F,
         properties,
         MagicFromTheEastCompat.getSymmetrySpellPowerAttributeOrNull(),
         ResourceLocation.fromNamespaceAndPath("somakespells", "mirrored_edge_modifier"),
         0.15,
         Operation.ADD_MULTIPLIED_BASE
      );
   }
}
