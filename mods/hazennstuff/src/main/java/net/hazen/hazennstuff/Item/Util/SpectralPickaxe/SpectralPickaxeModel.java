package net.hazen.hazennstuff.Item.Util.SpectralPickaxe;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class SpectralPickaxeModel extends DefaultedItemGeoModel<SpectralPickaxeItem> {
   public SpectralPickaxeModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(SpectralPickaxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/tools/spectral_pickaxe.geo.json");
   }

   public ResourceLocation getTextureResource(SpectralPickaxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/tools/spectral_pickaxe.png");
   }

   public ResourceLocation getAnimationResource(SpectralPickaxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
