package net.hazen.hazennstuff.Item.Staves.WhiteLilyStaff;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WhiteLilyStaffModel extends DefaultedItemGeoModel<WhiteLilyStaff> {
   public WhiteLilyStaffModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(WhiteLilyStaff animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/white_lily_staff.geo.json");
   }

   public ResourceLocation getTextureResource(WhiteLilyStaff animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/white_lily_staff.png");
   }

   public ResourceLocation getAnimationResource(WhiteLilyStaff animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
