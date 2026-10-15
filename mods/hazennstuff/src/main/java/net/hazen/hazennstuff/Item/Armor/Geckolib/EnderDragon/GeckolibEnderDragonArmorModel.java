package net.hazen.hazennstuff.Item.Armor.Geckolib.EnderDragon;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibEnderDragonArmorModel extends DefaultedEntityGeoModel<GeckolibEnderDragonArmorItem> {
   public GeckolibEnderDragonArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibEnderDragonArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/ender_dragon_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibEnderDragonArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/ender_dragon_armor_geckolib.png");
   }

   public ResourceLocation getAnimationResource(GeckolibEnderDragonArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
