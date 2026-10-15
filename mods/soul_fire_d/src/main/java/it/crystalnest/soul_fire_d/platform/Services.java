package it.crystalnest.soul_fire_d.platform;

import it.crystalnest.soul_fire_d.Constants;
import it.crystalnest.soul_fire_d.platform.services.PlatformHelper;
import java.util.ServiceLoader;

public final class Services {
   public static final PlatformHelper PLATFORM = load(PlatformHelper.class);

   private Services() {
   }

   private static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }
}
