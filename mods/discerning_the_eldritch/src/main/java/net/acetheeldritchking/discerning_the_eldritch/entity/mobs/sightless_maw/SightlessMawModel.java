package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.sightless_maw;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SightlessMawModel extends DefaultedEntityGeoModel<SightlessMawEntity> {
   public SightlessMawModel() {
      super(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "sightless_maw"));
   }

   public ResourceLocation getModelResource(SightlessMawEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/sightless_maw.geo.json");
   }

   public ResourceLocation getTextureResource(SightlessMawEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/sightless_maw/sightless_maw.png");
   }

   public ResourceLocation getAnimationResource(SightlessMawEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/sightless_maw.animation.json");
   }

   public void setCustomAnimations(SightlessMawEntity animatable, long instanceId, AnimationState<SightlessMawEntity> animationState) {
      super.setCustomAnimations(animatable, instanceId, animationState);
   }
}
