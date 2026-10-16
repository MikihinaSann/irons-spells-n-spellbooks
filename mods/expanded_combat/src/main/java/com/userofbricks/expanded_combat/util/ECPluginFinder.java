package com.userofbricks.expanded_combat.util;

import com.userofbricks.expanded_combat.api.registry.ECPlugin;
import com.userofbricks.expanded_combat.api.registry.IExpandedCombatPlugin;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * NeoForge discovers {@link ECPlugin} classes via ModFileScanData annotation
 * scanning, which has no Fabric equivalent. On Fabric, plugin classes are
 * declared as {@code "expanded_combat:plugin"} entrypoints in each mod's
 * fabric.mod.json and instantiated by the loader.
 */
public class ECPluginFinder {
   public static final String PLUGIN_ENTRYPOINT = "expanded_combat:plugin";
   private static final Logger LOGGER = LogManager.getLogger();

   private ECPluginFinder() {
   }

   public static List<IExpandedCombatPlugin> getECPlugins() {
      List<IExpandedCombatPlugin> pluginList = getInstances();
      pluginList.sort(Comparator.comparingInt(IExpandedCombatPlugin::loadOrder));
      return pluginList;
   }

   private static List<IExpandedCombatPlugin> getInstances() {
      List<IExpandedCombatPlugin> instances = new ArrayList<>();

      for (EntrypointContainer<IExpandedCombatPlugin> container
            : FabricLoader.getInstance().getEntrypointContainers(PLUGIN_ENTRYPOINT, IExpandedCombatPlugin.class)) {
         IExpandedCombatPlugin instance;
         try {
            instance = container.getEntrypoint();
         } catch (Throwable t) {
            LOGGER.error("Failed to load EC plugin entrypoint from mod {}", container.getProvider().getMetadata().getId(), t);
            continue;
         }

         ECPlugin annotation = instance.getClass().getAnnotation(ECPlugin.class);
         boolean readyToLoad = true;
         if (annotation != null) {
            for (String modId : annotation.required()) {
               if (!ModList.get().isLoaded(modId)) {
                  readyToLoad = false;
                  LOGGER.debug("Mod {} is not loaded for EC Plugin Class {} so it was not loaded",
                        modId, instance.getClass().getName());
                  break;
               }
            }
         }

         if (readyToLoad) {
            instances.add(instance);
         }
      }

      return instances;
   }
}
