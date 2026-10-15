package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.untold_behemoth;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class UntoldBehemothModel extends DefaultedEntityGeoModel<UntoldBehemothEntity> {
   public UntoldBehemothModel() {
      super(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "untold_behemoth"));
   }

   public ResourceLocation getModelResource(UntoldBehemothEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/untold_behemoth.geo.json");
   }

   public ResourceLocation getTextureResource(UntoldBehemothEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/untold_behemoth/untold_behemoth.png");
   }

   public ResourceLocation getAnimationResource(UntoldBehemothEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/untold_behemoth.animation.json");
   }

   public void setCustomAnimations(UntoldBehemothEntity animatable, long instanceId, AnimationState<UntoldBehemothEntity> animationState) {
      super.setCustomAnimations(animatable, instanceId, animationState);
   }
}
