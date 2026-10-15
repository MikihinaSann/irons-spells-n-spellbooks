package net.hazen.hazennstuff.Item.Weapons.Generic.Malice;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class MaliceModel extends DefaultedItemGeoModel<MaliceItem> {
   public MaliceModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(MaliceItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/malice.geo.json");
   }

   public ResourceLocation getTextureResource(MaliceItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/malice.png");
   }

   public ResourceLocation getAnimationResource(MaliceItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
