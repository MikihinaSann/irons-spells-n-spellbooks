package it.crystalnest.cobweb.api.pack.fixed;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Position;

public final class StaticTexturePack extends StaticResourcePack {
   public StaticTexturePack(ResourceLocation location, PackSource source, Position position, boolean alwaysActive) {
      super(location, PackType.CLIENT_RESOURCES, source, position, alwaysActive);
   }

   public StaticTexturePack(ResourceLocation location, Position position) {
      super(location, PackType.CLIENT_RESOURCES, position);
   }

   @Override
   public String directory() {
      return "texturepack";
   }
}
