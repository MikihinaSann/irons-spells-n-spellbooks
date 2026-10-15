package net.hazen.hazennstuff.Item.Weapons.Generic.Dawnmaker;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DawnmakerModel extends DefaultedItemGeoModel<DawnmakerItem> {
   public DawnmakerModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(DawnmakerItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/flame_reaver.geo.json");
   }

   public ResourceLocation getTextureResource(DawnmakerItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/flame_reaver.png");
   }

   public ResourceLocation getAnimationResource(DawnmakerItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
