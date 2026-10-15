package net.hazen.hazennstuff.Item.Armor.Geckolib.NecromancerArmor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class NecromancerArmorModel extends DefaultedEntityGeoModel<NecromancerArmorItem> {
   public NecromancerArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(NecromancerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/skeleton_robes.geo.json");
   }

   public ResourceLocation getTextureResource(NecromancerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/necromancer.png");
   }

   public ResourceLocation getAnimationResource(NecromancerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
