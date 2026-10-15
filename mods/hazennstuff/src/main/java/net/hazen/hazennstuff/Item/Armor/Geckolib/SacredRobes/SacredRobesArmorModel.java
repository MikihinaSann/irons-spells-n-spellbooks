package net.hazen.hazennstuff.Item.Armor.Geckolib.SacredRobes;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SacredRobesArmorModel extends DefaultedEntityGeoModel<SacredRobesArmorItem> {
   public SacredRobesArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(SacredRobesArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/sacred_robes_armor.geo.json");
   }

   public ResourceLocation getTextureResource(SacredRobesArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/sacred_robes_armor.png");
   }

   public ResourceLocation getAnimationResource(SacredRobesArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
