package com.grandsigil;

public final class GsPresence {
   public static final int SIGIL = 0;
   public static final int SUN = 1;
   public static final int BOLT = 2;
   public static final int TREE = 3;
   public static final int GATE = 4;
   public static final int GLACIER = 5;
   public static final int GAZE = 6;
   public static final int ARENA = 7;
   public static final int GRIP = 8;
   public static final int FIELD = 9;
   public static final int RECKONING = 10;
   public static final int COFFIN = 11;
   public static final int ARSENAL = 12;
   public static final int BLADE = 13;
   public static final int PHOENIX = 14;
   public static final int THUNDER_DRAGON = 15;
   public static final int BLOOM = 16;
   public static final int ICE_AGE = 17;
   public static final int THREADS = 18;
   public static final int CLEAVE = 19;
   private static final int KINDS = 20;
   private static final int[] LIVE = new int[20];

   private GsPresence() {
   }

   public static void enter(int kind) {
      synchronized (LIVE) {
         LIVE[kind]++;
      }
   }

   public static void leave(int kind) {
      synchronized (LIVE) {
         if (LIVE[kind] > 0) {
            LIVE[kind]--;
         }
      }
   }

   public static boolean any(int kind) {
      synchronized (LIVE) {
         return LIVE[kind] > 0;
      }
   }

   public static boolean anyAtAll() {
      synchronized (LIVE) {
         for (int live : LIVE) {
            if (live > 0) {
               return true;
            }
         }

         return false;
      }
   }
}
