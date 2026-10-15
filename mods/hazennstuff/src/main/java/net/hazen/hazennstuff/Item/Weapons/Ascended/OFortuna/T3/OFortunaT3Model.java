package net.hazen.hazennstuff.Item.Weapons.Ascended.OFortuna.T3;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class OFortunaT3Model extends DefaultedItemGeoModel<OFortunaT3> {
   public OFortunaT3Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(OFortunaT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/o_fortuna.geo.json");
   }

   public ResourceLocation getTextureResource(OFortunaT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/o_fortuna.png");
   }

   public ResourceLocation getAnimationResource(OFortunaT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
