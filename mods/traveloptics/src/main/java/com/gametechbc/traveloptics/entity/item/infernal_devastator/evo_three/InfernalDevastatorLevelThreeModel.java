package com.gametechbc.traveloptics.entity.item.infernal_devastator.evo_three;

import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelThreeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class InfernalDevastatorLevelThreeModel extends GeoModel<InfernalDevastatorLevelThreeItem> {
   public InfernalDevastatorLevelThreeModel() {
      super();
   }

   public ResourceLocation getAnimationResource(InfernalDevastatorLevelThreeItem animatable) {
      return new ResourceLocation("traveloptics", "animations/infernal_devastator.animation.json");
   }

   public ResourceLocation getModelResource(InfernalDevastatorLevelThreeItem animatable) {
      return new ResourceLocation("traveloptics", "geo/infernal_devastator.geo.json");
   }

   public ResourceLocation getTextureResource(InfernalDevastatorLevelThreeItem animatable) {
      return new ResourceLocation("traveloptics", "textures/models/item/infernal_devastator.png");
   }
}
