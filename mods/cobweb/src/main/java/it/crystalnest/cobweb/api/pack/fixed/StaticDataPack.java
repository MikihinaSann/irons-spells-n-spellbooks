package it.crystalnest.cobweb.api.pack.fixed;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Position;

public final class StaticDataPack extends StaticResourcePack {
   public StaticDataPack(ResourceLocation location, PackSource source, Position position, boolean alwaysActive) {
      super(location, PackType.SERVER_DATA, source, position, alwaysActive);
   }

   public StaticDataPack(ResourceLocation location, Position position) {
      super(location, PackType.SERVER_DATA, position);
   }

   @Override
   public String directory() {
      return "datapack";
   }
}
