package net.hazen.hazennstuff.Item.Armor.Geckolib.Maverick;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MaverickArmorModel extends DefaultedEntityGeoModel<MaverickArmorItem> {
   public MaverickArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(MaverickArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/maverick_armor.geo.json");
   }

   public ResourceLocation getTextureResource(MaverickArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/maverick_armor.png");
   }

   public ResourceLocation getAnimationResource(MaverickArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/maverick_armor.animation.json");
   }
}
