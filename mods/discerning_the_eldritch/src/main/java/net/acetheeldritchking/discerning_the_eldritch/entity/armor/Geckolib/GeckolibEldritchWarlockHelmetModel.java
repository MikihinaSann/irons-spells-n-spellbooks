package net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib;

import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.GeckolibEldritchWarlockHelmetItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class GeckolibEldritchWarlockHelmetModel extends DefaultedItemGeoModel<GeckolibEldritchWarlockHelmetItem> implements GeckolibEldritchHelmetInterface {
   public GeckolibEldritchWarlockHelmetModel() {
      super(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", ""));
   }

   @Override
   public ResourceLocation getModelResource(GeckolibEldritchWarlockHelmetItem object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/eldritch_armor_helmet_geckolib.geo.json");
   }

   @Override
   public ResourceLocation getTextureResource(GeckolibEldritchWarlockHelmetItem object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/models/armor/geckolib/eldritch_armor_helmet.png");
   }

   @Override
   public ResourceLocation getAnimationResource(GeckolibEldritchWarlockHelmetItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/eldritch_armor_geckolib.animation.json");
   }
}
