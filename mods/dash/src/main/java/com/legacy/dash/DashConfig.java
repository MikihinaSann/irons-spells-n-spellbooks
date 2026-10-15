package com.legacy.dash;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class DashConfig {
   public static final ModConfigSpec SERVER_SPEC;
   public static final DashConfig.ServerConfig SERVER;

   public static boolean useDurability() {
      return (Boolean)SERVER.useDurability.get();
   }

   static {
      Pair<DashConfig.ServerConfig, ModConfigSpec> serverPair = new Builder().configure(DashConfig.ServerConfig::new);
      SERVER = (DashConfig.ServerConfig)serverPair.getLeft();
      SERVER_SPEC = (ModConfigSpec)serverPair.getRight();
   }

   private static class ServerConfig {
      public final ConfigValue<Boolean> useDurability;

      public ServerConfig(Builder builder) {
         builder.comment("Dash Server/World Config");
         builder.push("Enchantment");
         this.useDurability = builder.comment("\n Cause damage to boots when dashing. (Affected by Unbreaking)").define("useDurability", false);
         builder.pop();
      }
   }
}
