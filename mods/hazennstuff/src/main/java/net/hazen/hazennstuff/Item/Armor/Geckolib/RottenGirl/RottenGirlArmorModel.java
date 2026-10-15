package net.hazen.hazennstuff.Item.Armor.Geckolib.RottenGirl;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RottenGirlArmorModel extends DefaultedEntityGeoModel<RottenGirlArmorItem> {
   public RottenGirlArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(RottenGirlArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/rotten_girl_armor.geo.json");
   }

   public ResourceLocation getTextureResource(RottenGirlArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/rotten_girl_armor.png");
   }

   public ResourceLocation getAnimationResource(RottenGirlArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
