package net.hazen.hazennstuff.Item.Armor.Geckolib.MyceliumGuardian;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MyceliumGuardianArmorModel extends DefaultedEntityGeoModel<MyceliumGuardianArmorItem> {
   public MyceliumGuardianArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(MyceliumGuardianArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/mycelium_guardian_armor.geo.json");
   }

   public ResourceLocation getTextureResource(MyceliumGuardianArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/mycelium_guardian_armor_geckolib.png");
   }

   public ResourceLocation getAnimationResource(MyceliumGuardianArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
