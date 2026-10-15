package it.crystalnest.prometheus.platform;

import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.platform.services.NetworkHelper;
import it.crystalnest.prometheus.platform.services.PlatformHelper;
import java.util.ServiceLoader;

public final class Services {
   public static final PlatformHelper PLATFORM = load(PlatformHelper.class);
   public static final NetworkHelper NETWORK = load(NetworkHelper.class);

   private Services() {
   }

   private static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }
}
