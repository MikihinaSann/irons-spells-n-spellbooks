package it.crystalnest.prometheus;

import it.crystalnest.prometheus.handler.FireResourceReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus.Internal;

@Mod("prometheus")
@Internal
public final class ModLoader {
   private static IEventBus bus;

   public ModLoader(IEventBus bus) {
      ModLoader.bus = bus;
      CommonModLoader.init();
      this.registerResourceLoader();
   }

   public static IEventBus getBus() {
      return bus;
   }

   private void registerResourceLoader() {
      NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.event.AddReloadListenerEvent.class, event -> event.addListener(new FireResourceReloadListener()));
      NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.event.OnDatapackSyncEvent.class, event -> FireResourceReloadListener.handle(event.getPlayer()));
   }
}
