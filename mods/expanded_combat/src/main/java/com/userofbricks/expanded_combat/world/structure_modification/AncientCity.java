package com.userofbricks.expanded_combat.world.structure_modification;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber(
   modid = "expanded_combat"
)
public class AncientCity {
   private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(
      Registries.PROCESSOR_LIST, ResourceLocation.fromNamespaceAndPath("minecraft", "empty")
   );
   private static final ResourceKey<StructureProcessorList> ANCIENT_CITY_PROCESSOR_LIST_KEY = ResourceKey.create(
      Registries.PROCESSOR_LIST, ResourceLocation.fromNamespaceAndPath("expanded_combat", "weaponry_vault_degradation")
   );

   private static void addBuildingToPool(
      Registry<StructureTemplatePool> templatePoolRegistry,
      Registry<StructureProcessorList> processorListRegistry,
      ResourceLocation poolRL,
      String nbtPieceRL,
      int weight
   ) {
      Holder<StructureProcessorList> ancientCityProcessorList = processorListRegistry.getHolderOrThrow(ANCIENT_CITY_PROCESSOR_LIST_KEY);
      StructureTemplatePool pool = (StructureTemplatePool)templatePoolRegistry.get(poolRL);
      if (pool != null) {
         SinglePoolElement piece = (SinglePoolElement)SinglePoolElement.legacy(nbtPieceRL, ancientCityProcessorList).apply(Projection.RIGID);

         for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
         }
      }
   }

   @SubscribeEvent
   public static void addNewBuilding(ServerAboutToStartEvent event) {
      Registry<StructureTemplatePool> templatePoolRegistry = (Registry<StructureTemplatePool>)event.getServer()
         .registryAccess()
         .registry(Registries.TEMPLATE_POOL)
         .orElseThrow();
      Registry<StructureProcessorList> processorListRegistry = (Registry<StructureProcessorList>)event.getServer()
         .registryAccess()
         .registry(Registries.PROCESSOR_LIST)
         .orElseThrow();
      addBuildingToPool(
         templatePoolRegistry,
         processorListRegistry,
         ResourceLocation.fromNamespaceAndPath("minecraft", "ancient_city/structures"),
         "expanded_combat:ancient_city/structures/weaponry_barracks",
         1
      );
   }
}
