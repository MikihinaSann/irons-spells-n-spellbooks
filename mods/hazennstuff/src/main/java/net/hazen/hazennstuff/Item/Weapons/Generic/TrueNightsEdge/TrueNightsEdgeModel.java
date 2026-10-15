package net.hazen.hazennstuff.Item.Weapons.Generic.TrueNightsEdge;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class TrueNightsEdgeModel extends DefaultedItemGeoModel<TrueNightsEdgeItem> {
   public TrueNightsEdgeModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(TrueNightsEdgeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/true_nights_edge.geo.json");
   }

   public ResourceLocation getTextureResource(TrueNightsEdgeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/true_nights_edge.png");
   }

   public ResourceLocation getAnimationResource(TrueNightsEdgeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
