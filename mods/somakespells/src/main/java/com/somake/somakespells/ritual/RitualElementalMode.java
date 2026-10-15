package com.somake.somakespells.ritual;

public enum RitualElementalMode {
   NORMAL("fire"),
   SOUL_FIRE("soul_fire"),
   INFERNAL_FIRE("infernal_fire");

   private final String id;

   private RitualElementalMode(String id) {
      this.id = id;
   }

   public String id() {
      return this.id;
   }

   public boolean isEmpowered() {
      return this != NORMAL;
   }

   public static RitualElementalMode fromId(String id) {
      for (RitualElementalMode mode : values()) {
         if (mode.id.equalsIgnoreCase(id)) {
            return mode;
         }
      }

      return NORMAL;
   }

   public static RitualElementalMode fromOrdinal(int ordinal) {
      RitualElementalMode[] values = values();
      return ordinal >= 0 && ordinal < values.length ? values[ordinal] : NORMAL;
   }
}
