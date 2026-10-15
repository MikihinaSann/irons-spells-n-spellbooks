package net.hazen.hazennstuff.Item.Weapons.Generic.ThornChakram;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ThornChakramModel extends DefaultedItemGeoModel<ThornChakramItem> {
   public ThornChakramModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ThornChakramItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/thorn_chakram.geo.json");
   }

   public ResourceLocation getTextureResource(ThornChakramItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/thorn_chakram.png");
   }

   public ResourceLocation getAnimationResource(ThornChakramItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
