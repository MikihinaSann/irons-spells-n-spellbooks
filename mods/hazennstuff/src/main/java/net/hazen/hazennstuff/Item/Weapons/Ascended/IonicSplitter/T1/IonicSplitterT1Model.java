package net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T1;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class IonicSplitterT1Model extends DefaultedItemGeoModel<IonicSplitterT1Item> {
   public IonicSplitterT1Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(IonicSplitterT1Item animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/ionic_splitter.geo.json");
   }

   public ResourceLocation getTextureResource(IonicSplitterT1Item animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/ionic_splitter_t1.png");
   }

   public ResourceLocation getAnimationResource(IonicSplitterT1Item animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
