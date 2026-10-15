package net.hazen.hazennstuff.Item.Armor.Geckolib.SacredRobes.Ascended;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AscendedSacredRobesArmorModel extends DefaultedEntityGeoModel<AscendedSacredRobesArmorItem> {
   public AscendedSacredRobesArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(AscendedSacredRobesArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/sacred_robes_armor.geo.json");
   }

   public ResourceLocation getTextureResource(AscendedSacredRobesArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/sacred_robes_armor.png");
   }

   public ResourceLocation getAnimationResource(AscendedSacredRobesArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
