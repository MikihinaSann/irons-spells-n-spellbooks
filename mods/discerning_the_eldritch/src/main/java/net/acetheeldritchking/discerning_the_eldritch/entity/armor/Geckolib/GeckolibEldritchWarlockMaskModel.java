package net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib;

import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.GeckolibEldritchWarlockMaskItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class GeckolibEldritchWarlockMaskModel extends DefaultedItemGeoModel<GeckolibEldritchWarlockMaskItem> implements GeckolibEldritchMaskInterface {
   public GeckolibEldritchWarlockMaskModel() {
      super(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", ""));
   }

   @Override
   public ResourceLocation getModelResource(GeckolibEldritchWarlockMaskItem object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/eldritch_armor_mask_geckolib.geo.json");
   }

   @Override
   public ResourceLocation getTextureResource(GeckolibEldritchWarlockMaskItem object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/models/armor/geckolib/eldritch_warlock_mask.png");
   }

   @Override
   public ResourceLocation getAnimationResource(GeckolibEldritchWarlockMaskItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/eldritch_armor_geckolib.animation.json");
   }
}
