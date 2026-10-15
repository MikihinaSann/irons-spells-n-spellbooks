package it.crystalnest.cobweb.platform.services;

import it.crystalnest.cobweb.platform.model.Environment;
import it.crystalnest.cobweb.platform.model.Platform;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack.Metadata;

public interface PlatformHelper {
   Metadata createPackMetadata(Component var1);

   Platform getPlatformName();

   boolean isModLoaded(String var1);

   String getModVersion(String var1) throws NoSuchElementException;

   Path getResourcePath(String var1, String var2) throws NoSuchElementException;

   boolean isDevEnv();

   default Environment getEnvironment() {
      return this.isDevEnv() ? Environment.DEVELOPMENT : Environment.PRODUCTION;
   }
}
