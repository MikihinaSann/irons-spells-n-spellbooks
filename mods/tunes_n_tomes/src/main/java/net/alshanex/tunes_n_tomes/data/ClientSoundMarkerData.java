package net.alshanex.tunes_n_tomes.data;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSoundMarkerData {
   private static int currentMarkers = 0;
   private static int maxMarkers = 4;

   public static void setMarkerInfo(int current, int max) {
      currentMarkers = current;
      maxMarkers = max;
   }

   public static int getCurrentMarkers() {
      return currentMarkers;
   }

   public static int getMaxMarkers() {
      return maxMarkers;
   }

   public static void reset() {
      currentMarkers = 0;
      maxMarkers = 4;
   }
}
