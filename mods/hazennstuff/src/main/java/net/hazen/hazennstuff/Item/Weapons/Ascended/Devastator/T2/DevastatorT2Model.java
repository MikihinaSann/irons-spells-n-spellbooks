package net.hazen.hazennstuff.Item.Weapons.Ascended.Devastator.T2;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DevastatorT2Model extends DefaultedItemGeoModel<DevastatorT2> {
   public DevastatorT2Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(DevastatorT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/devastator.geo.json");
   }

   public ResourceLocation getTextureResource(DevastatorT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/devastator.png");
   }

   public ResourceLocation getAnimationResource(DevastatorT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
