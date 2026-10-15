package net.alshanex.tunes_n_tomes.entity.armor;

import net.alshanex.tunes_n_tomes.item.armor.MelodicMasteryArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MelodicMasteryArmorModel extends GeoModel<MelodicMasteryArmorItem> {
   public ResourceLocation getModelResource(MelodicMasteryArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/melodic_mastery_armor.geo.json");
   }

   public ResourceLocation getTextureResource(MelodicMasteryArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/models/armor/melodic_mastery_armor.png");
   }

   public ResourceLocation getAnimationResource(MelodicMasteryArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
