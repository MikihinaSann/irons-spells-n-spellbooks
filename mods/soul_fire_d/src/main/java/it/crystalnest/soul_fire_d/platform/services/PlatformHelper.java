package it.crystalnest.soul_fire_d.platform.services;

import it.crystalnest.cobweb.platform.model.Environment;
import it.crystalnest.cobweb.platform.model.Platform;

public interface PlatformHelper {
   Platform getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevEnv();

   default Environment getEnvironment() {
      return this.isDevEnv() ? Environment.DEVELOPMENT : Environment.PRODUCTION;
   }
}
