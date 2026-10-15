package net.hazen.hazennstuff.Item.Staves.DivineScepter;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DivineScepterModel extends DefaultedItemGeoModel<DivineScepter> {
   public DivineScepterModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(DivineScepter animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/divine_scepter.geo.json");
   }

   public ResourceLocation getTextureResource(DivineScepter animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/divine_scepter.png");
   }

   public ResourceLocation getAnimationResource(DivineScepter animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
