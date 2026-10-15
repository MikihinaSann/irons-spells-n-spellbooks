package net.hazen.hazennstuff.Item.Armor.Geckolib.FireblossomBattlemage;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibFireblossomBattlemageCrownedArmorModel extends DefaultedEntityGeoModel<GeckolibFireblossomBattlemageCrownedArmorItem> {
   public GeckolibFireblossomBattlemageCrownedArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibFireblossomBattlemageCrownedArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/fireblossom_battlemage_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibFireblossomBattlemageCrownedArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/fireblossom_battlemage_crowned_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibFireblossomBattlemageCrownedArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/fireblossom_battlemage_armor.animation.json");
   }
}
