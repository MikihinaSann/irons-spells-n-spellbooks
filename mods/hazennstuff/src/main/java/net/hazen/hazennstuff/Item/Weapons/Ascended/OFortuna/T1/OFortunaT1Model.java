package net.hazen.hazennstuff.Item.Weapons.Ascended.OFortuna.T1;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class OFortunaT1Model extends DefaultedItemGeoModel<OFortunaT1> {
   public OFortunaT1Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(OFortunaT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/o_fortuna.geo.json");
   }

   public ResourceLocation getTextureResource(OFortunaT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/o_fortuna.png");
   }

   public ResourceLocation getAnimationResource(OFortunaT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
