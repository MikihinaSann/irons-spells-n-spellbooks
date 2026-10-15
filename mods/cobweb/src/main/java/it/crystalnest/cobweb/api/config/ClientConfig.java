package it.crystalnest.cobweb.api.config;

import it.crystalnest.cobweb.platform.Services;
import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import org.jetbrains.annotations.Nullable;

public abstract class ClientConfig extends CobwebConfig {
   protected ClientConfig(Builder builder) {
      super(builder);
   }

   protected static <T extends ClientConfig> T register(String modId, Function<Builder, T> constructor) {
      register(modId, ConfigType.CLIENT, constructor);
      if (Services.CONFIG != null) {
         Services.CONFIG.registerClientConfig(getId(modId, ConfigType.CLIENT), getSpec(modId));
         return getConfig(modId);
      } else {
         throw new NullPointerException("Make sure you are on NeoForge or the mod Forge Config API Port is installed!");
      }
   }

   @Nullable
   protected static <T extends ClientConfig> T getConfig(String modId) {
      return getConfig(modId, ConfigType.CLIENT);
   }

   @Nullable
   protected static ModConfigSpec getSpec(String modId) {
      return getSpec(modId, ConfigType.CLIENT);
   }
}
