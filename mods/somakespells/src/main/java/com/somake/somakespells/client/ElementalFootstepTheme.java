package com.somake.somakespells.client;

public enum ElementalFootstepTheme {
   LIGHTNING(70, 170, 255),
   BLOOD(140, 30, 45),
   HOLY(255, 215, 110),
   AQUA(80, 195, 255),
   EVOCATION(175, 175, 195),
   ELDRITCH(100, 60, 170),
   ENDER(150, 70, 190),
   FIRE(255, 95, 40),
   SOUL_FIRE(90, 235, 255),
   INFERNAL_FIRE(120, 255, 90),
   GEO(125, 84, 48),
   ICE(150, 235, 255),
   NATURE(85, 190, 85),
   SOUND(195, 195, 215),
   SPIRIT(120, 245, 220),
   SYMMETRY(255, 235, 90);

   private final int red;
   private final int green;
   private final int blue;

   private ElementalFootstepTheme(int red, int green, int blue) {
      this.red = red;
      this.green = green;
      this.blue = blue;
   }

   public int red() {
      return this.red;
   }

   public int green() {
      return this.green;
   }

   public int blue() {
      return this.blue;
   }
}
