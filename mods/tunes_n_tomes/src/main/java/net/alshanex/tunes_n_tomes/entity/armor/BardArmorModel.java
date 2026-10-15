package net.alshanex.tunes_n_tomes.entity.armor;

import net.alshanex.tunes_n_tomes.item.armor.BardArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BardArmorModel extends GeoModel<BardArmorItem> {
   public ResourceLocation getModelResource(BardArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/bard_armor.geo.json");
   }

   public ResourceLocation getTextureResource(BardArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/models/armor/bard_armor.png");
   }

   public ResourceLocation getAnimationResource(BardArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
