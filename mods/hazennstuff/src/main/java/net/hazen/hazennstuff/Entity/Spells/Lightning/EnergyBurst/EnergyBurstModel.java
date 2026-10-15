package net.hazen.hazennstuff.Entity.Spells.Lightning.EnergyBurst;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EnergyBurstModel extends GeoModel<EnergyBurst> {
   public ResourceLocation getModelResource(EnergyBurst animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/energy_burst.geo.json");
   }

   public ResourceLocation getTextureResource(EnergyBurst animatable) {
      int frameCount = 4;
      int frameDuration = 1;
      int frame = animatable.tickCount / frameDuration % frameCount;
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/spark_" + frame + ".png");
   }

   public ResourceLocation getAnimationResource(EnergyBurst animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/spark.animation.json");
   }
}
