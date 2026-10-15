package net.hazen.hazennstuff.Item.Armor.Geckolib.FireblossomBattlemage;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibFireblossomBattlemageArmorModel extends DefaultedEntityGeoModel<GeckolibFireblossomBattlemageArmorItem> {
   public GeckolibFireblossomBattlemageArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibFireblossomBattlemageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/fireblossom_battlemage_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibFireblossomBattlemageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/fireblossom_battlemage_crowned_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibFireblossomBattlemageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/fireblossom_battlemage_armor.animation.json");
   }
}
