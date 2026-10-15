package net.hazen.hazennstuff.Item.Weapons.Ascended.OFortuna.T2;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class OFortunaT2Model extends DefaultedItemGeoModel<OFortunaT2> {
   public OFortunaT2Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(OFortunaT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/o_fortuna.geo.json");
   }

   public ResourceLocation getTextureResource(OFortunaT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/o_fortuna.png");
   }

   public ResourceLocation getAnimationResource(OFortunaT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
