package com.gametechbc.traveloptics.entity.item.infernal_devastator.evo_two;

import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelTwoItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class InfernalDevastatorLevelTwoModel extends GeoModel<InfernalDevastatorLevelTwoItem> {
   public InfernalDevastatorLevelTwoModel() {
      super();
   }

   public ResourceLocation getAnimationResource(InfernalDevastatorLevelTwoItem animatable) {
      return new ResourceLocation("traveloptics", "animations/infernal_devastator.animation.json");
   }

   public ResourceLocation getModelResource(InfernalDevastatorLevelTwoItem animatable) {
      return new ResourceLocation("traveloptics", "geo/infernal_devastator.geo.json");
   }

   public ResourceLocation getTextureResource(InfernalDevastatorLevelTwoItem animatable) {
      return new ResourceLocation("traveloptics", "textures/models/item/infernal_devastator.png");
   }
}
