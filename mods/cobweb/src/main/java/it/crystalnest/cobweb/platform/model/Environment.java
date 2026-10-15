package it.crystalnest.cobweb.platform.model;

public enum Environment {
   DEVELOPMENT,
   PRODUCTION;

   @Override
   public String toString() {
      return this.name().toLowerCase();
   }
}
