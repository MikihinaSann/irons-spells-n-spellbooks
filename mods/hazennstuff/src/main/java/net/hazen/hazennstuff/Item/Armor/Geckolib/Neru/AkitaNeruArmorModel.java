package net.hazen.hazennstuff.Item.Armor.Geckolib.Neru;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AkitaNeruArmorModel extends DefaultedEntityGeoModel<AkitaNeruArmorItem> {
   public AkitaNeruArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(AkitaNeruArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/neru_armor.geo.json");
   }

   public ResourceLocation getTextureResource(AkitaNeruArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/neru_armor.png");
   }

   public ResourceLocation getAnimationResource(AkitaNeruArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
