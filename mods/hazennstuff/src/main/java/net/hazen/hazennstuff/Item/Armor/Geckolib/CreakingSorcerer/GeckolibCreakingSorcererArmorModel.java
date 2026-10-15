package net.hazen.hazennstuff.Item.Armor.Geckolib.CreakingSorcerer;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibCreakingSorcererArmorModel extends DefaultedEntityGeoModel<GeckolibCreakingSorcererArmorItem> {
   public GeckolibCreakingSorcererArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibCreakingSorcererArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/creaking_sorcerer_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibCreakingSorcererArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/creaking_sorcerer_armor_geckolib.png");
   }

   public ResourceLocation getAnimationResource(GeckolibCreakingSorcererArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
