package com.gametechbc.traveloptics.entity.item.infernal_devastator.evo_one;

import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelOneItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class InfernalDevastatorLevelOneModel extends GeoModel<InfernalDevastatorLevelOneItem> {
   public InfernalDevastatorLevelOneModel() {
      super();
   }

   public ResourceLocation getAnimationResource(InfernalDevastatorLevelOneItem animatable) {
      return new ResourceLocation("traveloptics", "animations/infernal_devastator.animation.json");
   }

   public ResourceLocation getModelResource(InfernalDevastatorLevelOneItem animatable) {
      return new ResourceLocation("traveloptics", "geo/infernal_devastator.geo.json");
   }

   public ResourceLocation getTextureResource(InfernalDevastatorLevelOneItem animatable) {
      return new ResourceLocation("traveloptics", "textures/models/item/infernal_devastator.png");
   }
}
