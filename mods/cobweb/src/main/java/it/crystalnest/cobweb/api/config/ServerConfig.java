package it.crystalnest.cobweb.api.config;

import it.crystalnest.cobweb.platform.Services;
import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import org.jetbrains.annotations.Nullable;

public abstract class ServerConfig extends CobwebConfig {
   protected ServerConfig(Builder builder) {
      super(builder);
   }

   protected static <T extends ServerConfig> T register(String modId, Function<Builder, T> constructor) {
      register(modId, ConfigType.SERVER, constructor);
      if (Services.CONFIG != null) {
         Services.CONFIG.registerServerConfig(getId(modId, ConfigType.SERVER), getSpec(modId));
         return getConfig(modId);
      } else {
         throw new NullPointerException("Make sure you are on NeoForge or the mod Forge Config API Port is installed!");
      }
   }

   @Nullable
   protected static <T extends ServerConfig> T getConfig(String modId) {
      return getConfig(modId, ConfigType.SERVER);
   }

   @Nullable
   protected static ModConfigSpec getSpec(String modId) {
      return getSpec(modId, ConfigType.SERVER);
   }
}
