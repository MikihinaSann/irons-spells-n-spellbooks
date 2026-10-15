package net.alshanex.tunes_n_tomes.setup;

import net.alshanex.tunes_n_tomes.entity.rhapsody.BlueBirdEntity;
import net.alshanex.tunes_n_tomes.entity.segno_marker.SegnoMarkerEntity;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(
   modid = "tunes_n_tomes",
   bus = Bus.MOD
)
public class CommonSetup {
   @SubscribeEvent
   public static void onAttributeCreate(EntityAttributeCreationEvent event) {
      event.put((EntityType)TEntityRegistry.BLUE_BIRD.get(), BlueBirdEntity.prepareAttributes().build());
      event.put((EntityType)TEntityRegistry.SEGNO_MARKER.get(), SegnoMarkerEntity.prepareAttributes().build());
   }
}
