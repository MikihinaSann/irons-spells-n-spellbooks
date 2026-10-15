package net.hazen.hazennstuff.Item.Armor.Geckolib.Calamitas;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CalamitasArmorModel extends DefaultedEntityGeoModel<CalamitasArmorItem> {
   public CalamitasArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(CalamitasArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/calamitas_armor.geo.json");
   }

   public ResourceLocation getTextureResource(CalamitasArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/calamitas_armor.png");
   }

   public ResourceLocation getAnimationResource(CalamitasArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/calamitas_armor.animation.json");
   }
}
