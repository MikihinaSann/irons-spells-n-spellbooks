package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Shaders {
   private static final String[] IRIS = new String[]{"net.irisshaders.iris.api.v0.IrisApi", "net.coderbot.iris.api.v0.IrisApi"};
   private static final String OPTIFINE = "net.optifine.shaders.Shaders";
   private static Object irisApi;
   private static Method irisInUse;
   private static Field optifineLoaded;
   private static boolean looked;
   private static final long FRESH = 500000000L;
   private static boolean cached;
   private static boolean asked;
   private static long askedAt;

   private Shaders() {
   }

   public static boolean packInUse() {
      long now = System.nanoTime();
      if (asked && now - askedAt < 500000000L) {
         return cached;
      } else {
         asked = true;
         askedAt = now;
         cached = ask();
         return cached;
      }
   }

   private static boolean ask() {
      if (!looked) {
         look();
      }

      try {
         if (irisInUse != null) {
            return Boolean.TRUE.equals(irisInUse.invoke(irisApi));
         }

         if (optifineLoaded != null) {
            return optifineLoaded.getBoolean(null);
         }
      } catch (Throwable var1) {
         irisInUse = null;
         irisApi = null;
         optifineLoaded = null;
         GrandSigil.LOGGER.warn("Could not ask whether a shader pack is loaded; assuming not, for the rest of the session", var1);
      }

      return false;
   }

   private static synchronized void look() {
      if (!looked) {
         looked = true;

         for (String name : IRIS) {
            try {
               Class<?> type = Class.forName(name, false, Shaders.class.getClassLoader());
               Object instance = type.getMethod("getInstance").invoke(null);
               if (instance != null) {
                  irisApi = instance;
                  irisInUse = type.getMethod("isShaderPackInUse");
                  GrandSigil.LOGGER.info("Iris found ({}); the screen pass will stand down while a pack is loaded", name);
                  return;
               }
            } catch (Throwable var7) {
            }
         }

         try {
            Class<?> type = Class.forName("net.optifine.shaders.Shaders", false, Shaders.class.getClassLoader());
            optifineLoaded = type.getField("shaderPackLoaded");
            GrandSigil.LOGGER.info("OptiFine found; the screen pass will stand down while a pack is loaded");
         } catch (Throwable var6) {
         }
      }
   }
}
