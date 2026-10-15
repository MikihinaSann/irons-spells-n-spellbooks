package net.alshanex.alshanex_familiars.entity.armor;

import net.alshanex.alshanex_familiars.item.armor.BardArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BardArmorModel extends GeoModel<BardArmorItem> {
   public ResourceLocation getModelResource(BardArmorItem object) {
      return new ResourceLocation("alshanex_familiars", "geo/bard_armor.geo.json");
   }

   public ResourceLocation getTextureResource(BardArmorItem object) {
      return new ResourceLocation("alshanex_familiars", "textures/models/armor/bard_armor.png");
   }

   public ResourceLocation getAnimationResource(BardArmorItem animatable) {
      return new ResourceLocation("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
