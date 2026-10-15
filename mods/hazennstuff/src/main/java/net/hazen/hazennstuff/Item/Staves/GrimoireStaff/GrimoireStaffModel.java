package net.hazen.hazennstuff.Item.Staves.GrimoireStaff;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class GrimoireStaffModel extends DefaultedItemGeoModel<GrimoireStaff> {
   public GrimoireStaffModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GrimoireStaff animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/grimoire_staff.geo.json");
   }

   public ResourceLocation getTextureResource(GrimoireStaff animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/grimoire_staff.png");
   }

   public ResourceLocation getAnimationResource(GrimoireStaff animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
