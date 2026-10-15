package net.hazen.hazennstuff.Item.Armor.Geckolib.PermafrostPrince;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PermafrostPrinceArmorModel extends DefaultedEntityGeoModel<PermafrostPrinceArmorItem> {
   public PermafrostPrinceArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(PermafrostPrinceArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/permafrost_prince_armor.geo.json");
   }

   public ResourceLocation getTextureResource(PermafrostPrinceArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/permafrost_prince_armor.png");
   }

   public ResourceLocation getAnimationResource(PermafrostPrinceArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
