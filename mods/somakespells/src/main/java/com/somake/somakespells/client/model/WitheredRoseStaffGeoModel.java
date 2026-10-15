package com.somake.somakespells.client.model;

import com.somake.somakespells.item.WitheredRoseStaffItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WitheredRoseStaffGeoModel extends GeoModel<WitheredRoseStaffItem> {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("somakespells", "geo/item/staff/withered_rose_staff.geo.json");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/item/staffs/withered_rose_staff.png");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "animations/item/staff/withered_rose_staff.animation.json"
   );

   public ResourceLocation getModelResource(WitheredRoseStaffItem animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(WitheredRoseStaffItem animatable) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(WitheredRoseStaffItem animatable) {
      return ANIMATION;
   }
}
