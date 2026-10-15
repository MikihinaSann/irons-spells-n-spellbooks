package net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.Dormant;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class IonicSplitterModel extends DefaultedItemGeoModel<IonicSplitterItem> {
   public IonicSplitterModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(IonicSplitterItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/ionic_splitter.geo.json");
   }

   public ResourceLocation getTextureResource(IonicSplitterItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/ionic_splitter_dormant.png");
   }

   public ResourceLocation getAnimationResource(IonicSplitterItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
