package net.hazen.hazennstuff.Item.Armor.Geckolib.GiornoGiovanna;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GiornoGiovannaArmorModel extends DefaultedEntityGeoModel<GiornoGiovannaArmorItem> {
   public GiornoGiovannaArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GiornoGiovannaArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/giorno_giovanna_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GiornoGiovannaArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/giorno_giovanna_armor.png");
   }

   public ResourceLocation getAnimationResource(GiornoGiovannaArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
