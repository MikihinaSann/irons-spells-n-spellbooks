package net.alshanex.tunes_n_tomes.event;

import net.alshanex.tunes_n_tomes.registry.TAttributeRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(
   modid = "tunes_n_tomes",
   bus = Bus.MOD
)
public class ModEvents {
   @SubscribeEvent
   public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
      e.getTypes().forEach(entity -> TAttributeRegistry.ATTRIBUTES.getEntries().forEach(attribute -> e.add(entity, attribute)));
   }
}
