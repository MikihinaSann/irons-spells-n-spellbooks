package net.hazen.hazennstuff.Item.Weapons.Ascended.Devastator.T1;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DevastatorT1Model extends DefaultedItemGeoModel<DevastatorT1> {
   public DevastatorT1Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(DevastatorT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/devastator.geo.json");
   }

   public ResourceLocation getTextureResource(DevastatorT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/devastator.png");
   }

   public ResourceLocation getAnimationResource(DevastatorT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
