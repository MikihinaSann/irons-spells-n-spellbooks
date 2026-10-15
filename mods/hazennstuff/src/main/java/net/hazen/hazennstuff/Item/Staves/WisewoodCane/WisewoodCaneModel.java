package net.hazen.hazennstuff.Item.Staves.WisewoodCane;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WisewoodCaneModel extends DefaultedItemGeoModel<WisewoodCane> {
   public WisewoodCaneModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(WisewoodCane animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/wisewood_cane.geo.json");
   }

   public ResourceLocation getTextureResource(WisewoodCane animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/wisewood_cane.png");
   }

   public ResourceLocation getAnimationResource(WisewoodCane animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
