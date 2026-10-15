package it.crystalnest.cobweb.platform;

import it.crystalnest.cobweb.Constants;
import it.crystalnest.cobweb.platform.model.Platform;
import it.crystalnest.cobweb.platform.services.ConfigHelper;
import it.crystalnest.cobweb.platform.services.PlatformHelper;
import it.crystalnest.cobweb.platform.services.RegistryHelper;
import java.util.ServiceLoader;
import org.jetbrains.annotations.Nullable;

public final class Services {
   public static final PlatformHelper PLATFORM = load(PlatformHelper.class);
   public static final RegistryHelper<?> REGISTRY = load(RegistryHelper.class);
   @Nullable
   public static final ConfigHelper CONFIG = PLATFORM.getPlatformName() != Platform.NEOFORGE && !PLATFORM.isModLoaded("forgeconfigapiport")
      ? null
      : load(ConfigHelper.class);

   private Services() {
   }

   private static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }
}
