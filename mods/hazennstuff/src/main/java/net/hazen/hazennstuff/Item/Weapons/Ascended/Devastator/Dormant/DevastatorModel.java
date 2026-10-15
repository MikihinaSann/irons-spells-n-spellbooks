package net.hazen.hazennstuff.Item.Weapons.Ascended.Devastator.Dormant;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DevastatorModel extends DefaultedItemGeoModel<Devastator> {
   public DevastatorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(Devastator animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/devastator.geo.json");
   }

   public ResourceLocation getTextureResource(Devastator animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/devastator.png");
   }

   public ResourceLocation getAnimationResource(Devastator animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
