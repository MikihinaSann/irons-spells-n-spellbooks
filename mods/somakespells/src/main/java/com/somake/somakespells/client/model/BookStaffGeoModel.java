package com.somake.somakespells.client.model;

import com.somake.somakespells.item.AbstractAnimatedGrimoireItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BookStaffGeoModel extends GeoModel<AbstractAnimatedGrimoireItem> {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("somakespells", "geo/item/staff/book_staff.geo.json");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("somakespells", "animations/item/staff/book_staff.animation.json");

   public ResourceLocation getModelResource(AbstractAnimatedGrimoireItem animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(AbstractAnimatedGrimoireItem animatable) {
      return animatable.getGrimoireTexture();
   }

   public ResourceLocation getAnimationResource(AbstractAnimatedGrimoireItem animatable) {
      return ANIMATION;
   }
}
