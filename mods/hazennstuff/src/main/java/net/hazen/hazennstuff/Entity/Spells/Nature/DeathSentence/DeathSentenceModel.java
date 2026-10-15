package net.hazen.hazennstuff.Entity.Spells.Nature.DeathSentence;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DeathSentenceModel extends GeoModel<DeathSentence> {
   public ResourceLocation getModelResource(DeathSentence animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/death_sentence.geo.json");
   }

   public ResourceLocation getTextureResource(DeathSentence animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/death_sentence.png");
   }

   public ResourceLocation getAnimationResource(DeathSentence animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/spark.animation.json");
   }
}
