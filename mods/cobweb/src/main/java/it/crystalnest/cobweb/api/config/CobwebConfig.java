package it.crystalnest.cobweb.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public abstract class CobwebConfig {
   private static final Map<String, Pair<? extends CobwebConfig, ModConfigSpec>> CONFIGS = new HashMap<>();
   private static final Pair<? extends CobwebConfig, ModConfigSpec> NULL_CONFIG = Pair.of(null, null);

   protected CobwebConfig(Builder builder) {
      this.define(builder);
      builder.build();
   }

   protected static <T extends CobwebConfig> void register(String modId, ConfigType type, Function<Builder, T> constructor) {
      CONFIGS.put(getId(modId, type), new Builder().configure(constructor));
   }

   @Nullable
   protected static <T extends CobwebConfig> T getConfig(String modId, ConfigType type) {
      return (T)(CONFIGS.containsKey(getId(modId, type)) ? CONFIGS.get(getId(modId, type)).getLeft() : null);
   }

   @Nullable
   protected static ModConfigSpec getSpec(String modId, ConfigType type) {
      return (ModConfigSpec)CONFIGS.getOrDefault(getId(modId, type), NULL_CONFIG).getRight();
   }

   protected static String getId(String modId, ConfigType type) {
      return modId + "_" + type;
   }

   public void register() {
   }

   protected abstract void define(Builder var1);

   protected boolean stringListValidator(Object element) {
      return element instanceof String string && !string.isBlank();
   }
}
