package net.hazen.hazennstuff.Item.Weapons.Generic.Excalibur.ISSxTerraria;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ISSExcaliburModel extends DefaultedItemGeoModel<ISSExcaliburItem> {
   public ISSExcaliburModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ISSExcaliburItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/excalibur.geo.json");
   }

   public ResourceLocation getTextureResource(ISSExcaliburItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/excalibur_divine_gold.png");
   }

   public ResourceLocation getAnimationResource(ISSExcaliburItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
