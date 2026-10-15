package net.acetheeldritchking.aces_spell_utils.entity.armor;

import net.acetheeldritchking.aces_spell_utils.items.example.ExampleWarlockArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ExampleWarlockMaskModel extends DefaultedItemGeoModel<ExampleWarlockArmorItem> {
   public ExampleWarlockMaskModel() {
      super(ResourceLocation.fromNamespaceAndPath("aces_spell_utils", ""));
   }

   public ResourceLocation getModelResource(ExampleWarlockArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("aces_spell_utils", "geo/evil_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ExampleWarlockArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("aces_spell_utils", "textures/models/armor/evil_armor.png");
   }

   public ResourceLocation getAnimationResource(ExampleWarlockArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
