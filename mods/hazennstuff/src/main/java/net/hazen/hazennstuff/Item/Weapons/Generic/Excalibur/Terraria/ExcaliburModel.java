package net.hazen.hazennstuff.Item.Weapons.Generic.Excalibur.Terraria;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ExcaliburModel extends DefaultedItemGeoModel<ExcaliburItem> {
   public ExcaliburModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ExcaliburItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/excalibur.geo.json");
   }

   public ResourceLocation getTextureResource(ExcaliburItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/excalibur.png");
   }

   public ResourceLocation getAnimationResource(ExcaliburItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
