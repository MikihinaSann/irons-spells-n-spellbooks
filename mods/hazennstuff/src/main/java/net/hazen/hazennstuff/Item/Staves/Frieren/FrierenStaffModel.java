package net.hazen.hazennstuff.Item.Staves.Frieren;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class FrierenStaffModel extends DefaultedItemGeoModel<FrierenStaff> {
   public FrierenStaffModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(FrierenStaff animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/frieren_staff.geo.json");
   }

   public ResourceLocation getTextureResource(FrierenStaff animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/frieren_staff.png");
   }

   public ResourceLocation getAnimationResource(FrierenStaff animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
