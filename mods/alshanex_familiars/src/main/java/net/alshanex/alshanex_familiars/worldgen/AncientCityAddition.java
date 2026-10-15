package net.alshanex.alshanex_familiars.worldgen;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber
public class AncientCityAddition {
   private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(
      Registries.PROCESSOR_LIST, new ResourceLocation("minecraft", "empty")
   );

   private static void addBuildingToPool(
      Registry<StructureTemplatePool> templatePoolRegistry,
      Registry<StructureProcessorList> processorListRegistry,
      ResourceLocation poolRL,
      String nbtPieceRL,
      int weight
   ) {
      Holder<StructureProcessorList> processorList = processorListRegistry.getHolderOrThrow(ProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION);
      StructureTemplatePool pool = (StructureTemplatePool)templatePoolRegistry.get(poolRL);
      if (pool != null) {
         SinglePoolElement piece = (SinglePoolElement)SinglePoolElement.single(nbtPieceRL, processorList).apply(Projection.RIGID);

         for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
         }

         List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.rawTemplates);
         listOfPieceEntries.add(new Pair(piece, weight));
         pool.rawTemplates = listOfPieceEntries;
      }
   }

   @SubscribeEvent
   public static void addSummonerLabToAncientCity(ServerAboutToStartEvent event) {
      Registry<StructureTemplatePool> templatePoolRegistry = (Registry<StructureTemplatePool>)event.getServer()
         .registryAccess()
         .registry(Registries.TEMPLATE_POOL)
         .orElseThrow();
      Registry<StructureProcessorList> processorListRegistry = (Registry<StructureProcessorList>)event.getServer()
         .registryAccess()
         .registry(Registries.PROCESSOR_LIST)
         .orElseThrow();
      int weight = 2;
      if (weight > 0) {
         addBuildingToPool(
            templatePoolRegistry, processorListRegistry, ResourceLocation.parse("minecraft:ancient_city/structures"), "alshanex_familiars:summoner_lab", weight
         );
      }
   }
}
