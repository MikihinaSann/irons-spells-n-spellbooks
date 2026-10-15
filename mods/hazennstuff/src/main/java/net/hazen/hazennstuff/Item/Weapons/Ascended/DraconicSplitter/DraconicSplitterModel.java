package net.hazen.hazennstuff.Item.Weapons.Ascended.DraconicSplitter;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DraconicSplitterModel extends DefaultedItemGeoModel<DraconicSplitterItem> {
   public DraconicSplitterModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(DraconicSplitterItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/draconic_splitter.geo.json");
   }

   public ResourceLocation getTextureResource(DraconicSplitterItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/draconic_splitter.png");
   }

   public ResourceLocation getAnimationResource(DraconicSplitterItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
