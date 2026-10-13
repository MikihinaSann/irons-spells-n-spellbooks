package io.redspace.ironslib.transmog.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class GenericArmorModel<T extends Item & GeoItem> extends DefaultedItemGeoModel<T> {
   private final ResourceLocation model;
   private final ResourceLocation texture;
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("irons_lib", "animations/wizard_armor_animation.json");

   public GenericArmorModel(String modid, String name) {
      this(
         ResourceLocation.fromNamespaceAndPath(modid, String.format("geo/%s_armor.geo.json", name)),
         ResourceLocation.fromNamespaceAndPath(modid, String.format("textures/models/armor/%s.png", name))
      );
   }

   public GenericArmorModel(ResourceLocation model, ResourceLocation texture) {
      super(ResourceLocation.fromNamespaceAndPath(model.getNamespace(), ""));
      this.model = model;
      this.texture = texture;
   }

   public GenericArmorModel(String name) {
      this("irons_lib", name);
   }

   public ResourceLocation getModelResource(T animatable) {
      return this.model;
   }

   public ResourceLocation getTextureResource(T animatable) {
      return this.texture;
   }

   public ResourceLocation getAnimationResource(T animatable) {
      return ANIMATION;
   }
}
