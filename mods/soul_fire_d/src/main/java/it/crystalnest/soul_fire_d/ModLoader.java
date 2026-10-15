package it.crystalnest.soul_fire_d;

import it.crystalnest.soul_fire_d.loot.LootRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus.Internal;

@Mod("soul_fire_d")
@Internal
public final class ModLoader {
   private static IEventBus bus;

   public ModLoader(IEventBus bus) {
      ModLoader.bus = bus;
      CommonModLoader.init();
      LootRegistry.register(bus);
   }

   public static IEventBus getBus() {
      return bus;
   }
}
