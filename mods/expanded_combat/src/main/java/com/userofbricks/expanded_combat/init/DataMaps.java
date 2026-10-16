package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.data_components.ShieldMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(
   modid = "expanded_combat",
   bus = Bus.MOD
)
public class DataMaps {
   public static final DataMapType<Item, Material> SHIELD_INGREDIENT_MAP = DataMapType.builder(
         ExpandedCombat.modLoc("ingredient_to_shield_material"), Registries.ITEM, Material.CODEC
      )
      .synced(Material.CODEC, true)
      .build();
   public static final DataMapType<Item, ShieldMaterials> SHIELD_MATERIALS = DataMapType.builder(
         ExpandedCombat.modLoc("shield_materials"), Registries.ITEM, ShieldMaterials.CODEC
      )
      .synced(ShieldMaterials.CODEC, false)
      .build();

   @SubscribeEvent
   public static void registerDataMaps(RegisterDataMapTypesEvent event) {
      event.register(SHIELD_INGREDIENT_MAP);
      event.register(SHIELD_MATERIALS);
   }
}
