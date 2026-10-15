package net.hazen.hazennstuff.Item.Armor.Geckolib.Iron431;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class Iron431ArmorModel extends DefaultedEntityGeoModel<Iron431ArmorItem> {
   public Iron431ArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(Iron431ArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/irons_armor.geo.json");
   }

   public ResourceLocation getTextureResource(Iron431ArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/irons_armor.png");
   }

   public ResourceLocation getAnimationResource(Iron431ArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
