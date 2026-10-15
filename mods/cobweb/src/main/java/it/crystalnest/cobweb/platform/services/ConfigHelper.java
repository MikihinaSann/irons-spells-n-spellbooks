package it.crystalnest.cobweb.platform.services;

import net.neoforged.neoforge.common.ModConfigSpec;

public interface ConfigHelper {
   void registerCommonConfig(String var1, ModConfigSpec var2);

   void registerClientConfig(String var1, ModConfigSpec var2);

   void registerServerConfig(String var1, ModConfigSpec var2);
}
