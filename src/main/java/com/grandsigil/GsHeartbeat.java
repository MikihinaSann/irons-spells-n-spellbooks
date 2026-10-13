package com.grandsigil;

import java.util.Map.Entry;
import net.minecraft.client.Minecraft;

public final class GsHeartbeat {
   private static final long SILENCE = 6000000000L;
   private static final long COOLDOWN = 60000000000L;
   private static volatile long beat;
   private static volatile long reported;
   private static volatile boolean watching;

   private GsHeartbeat() {
   }

   public static void beat() {
      beat = System.nanoTime();
      if (!watching) {
         start();
      }
   }

   private static synchronized void start() {
      if (!watching) {
         watching = true;
         Thread watcher = new Thread(GsHeartbeat::watch, "grandsigil-heartbeat");
         watcher.setDaemon(true);
         watcher.setPriority(1);
         watcher.start();
      }
   }

   private static void watch() {
      while (true) {
         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
            return;
         }

         long last = beat;
         long now = System.nanoTime();
         if (last != 0L && now - last >= 6000000000L && now - reported >= 60000000000L) {
            if (!owed()) {
               beat = now;
            } else {
               reported = now;
               dump(now - last);
            }
         }
      }
   }

   private static boolean owed() {
      try {
         Minecraft minecraft = Minecraft.getInstance();
         return minecraft != null && minecraft.level != null && minecraft.isWindowActive();
      } catch (Throwable var1) {
         return true;
      }
   }

   private static void dump(long silent) {
      StringBuilder out = new StringBuilder(4096);
      out.append("\n================ grandsigil: the game has not drawn a frame in ")
         .append(silent / 1000000L)
         .append(" ms ================\n")
         .append("Every thread in the process follows. The one that is stuck is almost\n")
         .append("certainly \"Render thread\" or \"Server thread\".\n");

      for (Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
         Thread thread = entry.getKey();
         String name = thread.getName();
         boolean worth = name.contains("Render") || name.contains("Server") || name.contains("Client") || name.contains("main");
         if (worth) {
            out.append("\n--- ").append(name).append("  [").append(thread.getState()).append("]\n");

            for (StackTraceElement frame : entry.getValue()) {
               out.append("      at ").append(frame).append('\n');
               if (frame.getClassName().startsWith("com.grandsigil")) {
                  out.append("        ^^^^ grandsigil\n");
               }
            }
         }
      }

      out.append("\n=========================================================================\n");
      GrandSigil.LOGGER.error(out.toString());
   }
}
