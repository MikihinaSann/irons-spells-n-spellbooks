package it.crystalnest.cobweb.platform.model;

public enum Platform {
   FABRIC,
   FORGE,
   NEOFORGE;

   @Override
   public String toString() {
      return this.name().toLowerCase();
   }
}
