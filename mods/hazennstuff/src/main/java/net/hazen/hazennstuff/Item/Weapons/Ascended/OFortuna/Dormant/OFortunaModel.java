package net.hazen.hazennstuff.Item.Weapons.Ascended.OFortuna.Dormant;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class OFortunaModel extends DefaultedItemGeoModel<OFortuna> {
   public OFortunaModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(OFortuna animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/o_fortuna.geo.json");
   }

   public ResourceLocation getTextureResource(OFortuna animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/o_fortuna.png");
   }

   public ResourceLocation getAnimationResource(OFortuna animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
