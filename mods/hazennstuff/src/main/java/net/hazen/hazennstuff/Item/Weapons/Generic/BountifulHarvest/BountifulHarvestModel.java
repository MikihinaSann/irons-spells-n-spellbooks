package net.hazen.hazennstuff.Item.Weapons.Generic.BountifulHarvest;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class BountifulHarvestModel extends DefaultedItemGeoModel<BountifulHarvestItem> {
   public BountifulHarvestModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(BountifulHarvestItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/bountiful_harvest.geo.json");
   }

   public ResourceLocation getTextureResource(BountifulHarvestItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/bountiful_harvest.png");
   }

   public ResourceLocation getAnimationResource(BountifulHarvestItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
