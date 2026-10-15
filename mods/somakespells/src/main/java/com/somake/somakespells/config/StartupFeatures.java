package com.somake.somakespells.config;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

public final class StartupFeatures {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Path COMMON_CONFIG = FMLPaths.CONFIGDIR.get().resolve("somakespells").resolve("general").resolve("common.toml");
   private static final Pattern SACRED_PHOENIX_PATTERN = Pattern.compile("^\\s*enableSacredPhoenixBlessing\\s*=\\s*(true|false)\\s*(?:#.*)?$", 2);
   private static final boolean SACRED_PHOENIX_BLESSING_ENABLED = readSacredPhoenixBlessing();

   private StartupFeatures() {
   }

   public static boolean isSacredPhoenixBlessingEnabled() {
      return SACRED_PHOENIX_BLESSING_ENABLED;
   }

   private static boolean readSacredPhoenixBlessing() {
      if (!Files.isRegularFile(COMMON_CONFIG)) {
         LOGGER.info(
            "Sacred Phoenix Blessing startup registration is disabled (config file not created yet). It can be enabled after the first launch in {}.",
            COMMON_CONFIG
         );
         return false;
      } else {
         try {
            for (String line : Files.readAllLines(COMMON_CONFIG)) {
               Matcher matcher = SACRED_PHOENIX_PATTERN.matcher(line);
               if (matcher.matches()) {
                  boolean enabled = Boolean.parseBoolean(matcher.group(1));
                  LOGGER.info("Sacred Phoenix Blessing startup registration is {}.", enabled ? "enabled" : "disabled");
                  return enabled;
               }
            }

            LOGGER.info("Sacred Phoenix Blessing startup registration is disabled (option not found in {}).", COMMON_CONFIG);
         } catch (IOException var4) {
            LOGGER.error("Failed to read startup feature flags from {}. Sacred Phoenix Blessing will remain disabled.", COMMON_CONFIG, var4);
         }

         return false;
      }
   }
}
