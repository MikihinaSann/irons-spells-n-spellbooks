package net.hazen.hazennstuff.Item.Staves.BlossomOfTheEnchantress;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class BlossomOfTheEnchantressModel extends DefaultedItemGeoModel<BlossomOfTheEnchantress> {
   public BlossomOfTheEnchantressModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(BlossomOfTheEnchantress animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/blossom_of_the_enchantress.geo.json");
   }

   public ResourceLocation getTextureResource(BlossomOfTheEnchantress animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/blossom_of_the_enchantress.png");
   }

   public ResourceLocation getAnimationResource(BlossomOfTheEnchantress animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
