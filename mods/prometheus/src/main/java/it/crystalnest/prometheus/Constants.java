package it.crystalnest.prometheus;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public final class Constants {
   public static final String MOD_ID = "prometheus";
   public static final String DDFIRES = "ddfires";
   public static final Logger LOGGER = LoggerFactory.getLogger("prometheus");

   private Constants() {
   }
}
