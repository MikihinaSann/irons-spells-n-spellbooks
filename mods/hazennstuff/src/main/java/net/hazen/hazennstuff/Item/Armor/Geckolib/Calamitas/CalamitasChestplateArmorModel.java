package net.hazen.hazennstuff.Item.Armor.Geckolib.Calamitas;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CalamitasChestplateArmorModel extends DefaultedEntityGeoModel<CalamitasChestplateArmorItem> {
   public CalamitasChestplateArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(CalamitasChestplateArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/calamitas_armor.geo.json");
   }

   public ResourceLocation getTextureResource(CalamitasChestplateArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/calamitas_armor.png");
   }

   public ResourceLocation getAnimationResource(CalamitasChestplateArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/calamitas_armor.animation.json");
   }
}
