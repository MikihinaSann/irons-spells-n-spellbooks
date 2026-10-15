package net.hazen.hazennstuff.Item.Armor.Geckolib.AlchemistSupreme;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AlchemistSupremeArmorModel extends DefaultedEntityGeoModel<AlchemistSupremeArmorItem> {
   public AlchemistSupremeArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(AlchemistSupremeArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/alchemist_supreme_armor.geo.json");
   }

   public ResourceLocation getTextureResource(AlchemistSupremeArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/alchemist_supreme_armor.png");
   }

   public ResourceLocation getAnimationResource(AlchemistSupremeArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
