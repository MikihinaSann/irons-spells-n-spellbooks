package net.hazen.hazennstuff.Item.Weapons.Generic.VampireKnives;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class VampireKnivesModel extends DefaultedItemGeoModel<VampireKnivesItem> {
   public VampireKnivesModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(VampireKnivesItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/vampire_knives.geo.json");
   }

   public ResourceLocation getTextureResource(VampireKnivesItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/vampire_knives.png");
   }

   public ResourceLocation getAnimationResource(VampireKnivesItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
