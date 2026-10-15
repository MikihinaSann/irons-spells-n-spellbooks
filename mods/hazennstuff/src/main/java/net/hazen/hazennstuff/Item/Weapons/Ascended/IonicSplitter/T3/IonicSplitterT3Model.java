package net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T3;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class IonicSplitterT3Model extends DefaultedItemGeoModel<IonicSplitterT3Item> {
   public IonicSplitterT3Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(IonicSplitterT3Item animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/ionic_splitter.geo.json");
   }

   public ResourceLocation getTextureResource(IonicSplitterT3Item animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/ionic_splitter_t3.png");
   }

   public ResourceLocation getAnimationResource(IonicSplitterT3Item animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
