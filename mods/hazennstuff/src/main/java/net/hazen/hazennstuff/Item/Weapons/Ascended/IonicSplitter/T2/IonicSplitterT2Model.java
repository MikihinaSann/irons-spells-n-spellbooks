package net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T2;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class IonicSplitterT2Model extends DefaultedItemGeoModel<IonicSplitterT2Item> {
   public IonicSplitterT2Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(IonicSplitterT2Item animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/ionic_splitter.geo.json");
   }

   public ResourceLocation getTextureResource(IonicSplitterT2Item animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/ionic_splitter_t2.png");
   }

   public ResourceLocation getAnimationResource(IonicSplitterT2Item animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
