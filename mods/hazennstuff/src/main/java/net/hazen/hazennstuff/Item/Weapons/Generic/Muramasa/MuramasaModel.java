package net.hazen.hazennstuff.Item.Weapons.Generic.Muramasa;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class MuramasaModel extends DefaultedItemGeoModel<MuramasaItem> {
   public MuramasaModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(MuramasaItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/muramasa.geo.json");
   }

   public ResourceLocation getTextureResource(MuramasaItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/muramasa.png");
   }

   public ResourceLocation getAnimationResource(MuramasaItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
