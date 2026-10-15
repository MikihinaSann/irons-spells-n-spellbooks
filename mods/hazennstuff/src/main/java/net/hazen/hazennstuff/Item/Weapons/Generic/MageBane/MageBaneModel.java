package net.hazen.hazennstuff.Item.Weapons.Generic.MageBane;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class MageBaneModel extends DefaultedItemGeoModel<MageBaneItem> {
   public MageBaneModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(MageBaneItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/mage_bane.geo.json");
   }

   public ResourceLocation getTextureResource(MageBaneItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/mage_bane.png");
   }

   public ResourceLocation getAnimationResource(MageBaneItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
