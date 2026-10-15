package net.hazen.hazennstuff.Item.Armor.Geckolib.SynthesizerV;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SynthesizerVArmorModel extends DefaultedEntityGeoModel<SynthesizerVArmorItem> {
   public SynthesizerVArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(SynthesizerVArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/teto_kasane_synthesizer_v_armor.geo.json");
   }

   public ResourceLocation getTextureResource(SynthesizerVArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/teto_kasane_synthesizer_v_armor.png");
   }

   public ResourceLocation getAnimationResource(SynthesizerVArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
