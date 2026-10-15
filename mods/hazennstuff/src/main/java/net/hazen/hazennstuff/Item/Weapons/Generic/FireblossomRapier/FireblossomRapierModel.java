package net.hazen.hazennstuff.Item.Weapons.Generic.FireblossomRapier;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class FireblossomRapierModel extends DefaultedItemGeoModel<FireblossomRapierItem> {
   public FireblossomRapierModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(FireblossomRapierItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/fireblossom_rapier.geo.json");
   }

   public ResourceLocation getTextureResource(FireblossomRapierItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/fireblossom_rapier.png");
   }

   public ResourceLocation getAnimationResource(FireblossomRapierItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
