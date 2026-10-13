package io.redspace.ironslib.util;

import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class Utils {
   public static UUID uuidFromUndashed(String s) {
      if (s != null && s.length() == 32) {
         String dashed = s.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})", "$1-$2-$3-$4-$5");
         return UUID.fromString(dashed);
      } else {
         throw new IllegalArgumentException("Invalid UUID string");
      }
   }

   public static void spawnParticles(
      Level level, ParticleOptions particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force
   ) {
      level.getServer()
         .getPlayerList()
         .getPlayers()
         .forEach(player -> ((ServerLevel)level).sendParticles(player, particle, force, x, y, z, count, deltaX, deltaY, deltaZ, speed));
   }
}
