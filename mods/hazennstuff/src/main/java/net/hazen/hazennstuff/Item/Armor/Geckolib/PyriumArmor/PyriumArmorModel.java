package net.hazen.hazennstuff.Item.Armor.Geckolib.PyriumArmor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PyriumArmorModel extends DefaultedEntityGeoModel<PyriumArmorItem> {
   public PyriumArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(PyriumArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/pyrium_armor.geo.json");
   }

   public ResourceLocation getTextureResource(PyriumArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/pyrium_armor.png");
   }

   public ResourceLocation getAnimationResource(PyriumArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
