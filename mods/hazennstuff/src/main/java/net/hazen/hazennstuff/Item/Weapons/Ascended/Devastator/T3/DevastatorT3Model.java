package net.hazen.hazennstuff.Item.Weapons.Ascended.Devastator.T3;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DevastatorT3Model extends DefaultedItemGeoModel<DevastatorT3> {
   public DevastatorT3Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(DevastatorT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/devastator.geo.json");
   }

   public ResourceLocation getTextureResource(DevastatorT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/devastator.png");
   }

   public ResourceLocation getAnimationResource(DevastatorT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
