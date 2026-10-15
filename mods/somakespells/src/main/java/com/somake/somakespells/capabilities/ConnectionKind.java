package com.somake.somakespells.capabilities;

public enum ConnectionKind {
   BLESSED,
   CURSED,
   SHACKLED,
   GUARDIAN;

   public boolean isHealing() {
      return this == BLESSED;
   }

   public boolean isDamage() {
      return this == CURSED;
   }

   public boolean isRestraining() {
      return this == SHACKLED;
   }

   public boolean isProtective() {
      return this == GUARDIAN;
   }
}
