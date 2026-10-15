package it.crystalnest.cobweb.platform;

import it.crystalnest.cobweb.platform.model.Platform;
import it.crystalnest.cobweb.platform.services.PlatformHelper;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public final class NeoForgePlatformHelper implements PlatformHelper {
   @Override
   public Metadata createPackMetadata(Component description) {
      return new Metadata(description, PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of());
   }

   @Override
   public Platform getPlatformName() {
      return Platform.NEOFORGE;
   }

   @Override
   public boolean isModLoaded(String modId) {
      return ModList.get().isLoaded(modId);
   }

   @Override
   public String getModVersion(String modId) {
      return ((ModContainer)ModList.get().getModContainerById(modId).orElseThrow()).getModInfo().getVersion().toString();
   }

   @Override
   public Path getResourcePath(String modId, String path) {
      return ((ModContainer)ModList.get().getModContainerById(modId).orElseThrow()).getModInfo().getOwningFile().getFile().findResource(path);
   }

   @Override
   public boolean isDevEnv() {
      return !FMLLoader.isProduction();
   }
}
