package net.neoforged.neoforge.common.world;

import net.minecraft.world.level.biome.Biome.ClimateSettings;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;

public class ClimateSettingsBuilder {
   private boolean hasPrecipitation;
   private float temperature;
   private TemperatureModifier temperatureModifier;
   private float downfall;

   public static ClimateSettingsBuilder copyOf(ClimateSettings settings) {
      return create(settings.hasPrecipitation(), settings.temperature(), settings.temperatureModifier(), settings.downfall());
   }

   public static ClimateSettingsBuilder create(boolean hasPrecipitation, float temperature, TemperatureModifier temperatureModifier, float downfall) {
      return new ClimateSettingsBuilder(hasPrecipitation, temperature, temperatureModifier, downfall);
   }

   private ClimateSettingsBuilder(boolean hasPrecipitation, float temperature, TemperatureModifier temperatureModifier, float downfall) {
      this.hasPrecipitation = hasPrecipitation;
      this.temperature = temperature;
      this.temperatureModifier = temperatureModifier;
      this.downfall = downfall;
   }

   public ClimateSettings build() {
      return new ClimateSettings(this.hasPrecipitation, this.temperature, this.temperatureModifier, this.downfall);
   }

   public boolean hasPrecipitation() {
      return this.hasPrecipitation;
   }

   public void setHasPrecipitation(boolean hasPrecipitation) {
      this.hasPrecipitation = hasPrecipitation;
   }

   public float getTemperature() {
      return this.temperature;
   }

   public void setTemperature(float temperature) {
      this.temperature = temperature;
   }

   public TemperatureModifier getTemperatureModifier() {
      return this.temperatureModifier;
   }

   public void setTemperatureModifier(TemperatureModifier temperatureModifier) {
      this.temperatureModifier = temperatureModifier;
   }

   public float getDownfall() {
      return this.downfall;
   }

   public void setDownfall(float downfall) {
      this.downfall = downfall;
   }
}
