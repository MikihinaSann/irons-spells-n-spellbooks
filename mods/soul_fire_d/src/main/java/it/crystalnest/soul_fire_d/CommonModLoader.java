package it.crystalnest.soul_fire_d;

import it.crystalnest.cobweb.api.pack.fixed.StaticDataPack;
import it.crystalnest.soul_fire_d.fire.FireRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack.Position;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class CommonModLoader {
   private CommonModLoader() {
   }

   public static void init() {
      FireRegistry.register();
      new StaticDataPack(ResourceLocation.fromNamespaceAndPath("soul_fire_d", "enchantments"), Position.TOP).register();
   }
}
