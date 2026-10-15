package net.hazen.hazennstuff.Item.Weapons.Generic.NightsEdge;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class NightsEdgeModel extends DefaultedItemGeoModel<NightsEdgeItem> {
   public NightsEdgeModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(NightsEdgeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/nights_edge.geo.json");
   }

   public ResourceLocation getTextureResource(NightsEdgeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/nights_edge.png");
   }

   public ResourceLocation getAnimationResource(NightsEdgeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
