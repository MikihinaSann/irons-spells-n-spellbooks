package net.hazen.hazennstuff.Item.Armor.Geckolib.MithrilBattlemage;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MithrilBattlemageArmorModel extends DefaultedEntityGeoModel<MithrilBattlemageArmorItem> {
   public MithrilBattlemageArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(MithrilBattlemageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/mithril_battlemage_armor.geo.json");
   }

   public ResourceLocation getTextureResource(MithrilBattlemageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/mithril_battlemage_armor.png");
   }

   public ResourceLocation getAnimationResource(MithrilBattlemageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
