package net.warphan.iss_magicfromtheeast.entity.armor;

import net.minecraft.resources.ResourceLocation;
import net.warphan.iss_magicfromtheeast.item.armor.ElementalCommanderArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ElementalCommanderArmorModel extends DefaultedItemGeoModel<ElementalCommanderArmorItem> {
   public ElementalCommanderArmorModel() {
      super(new ResourceLocation("iss_magicfromtheeast", ""));
   }

   public ResourceLocation getModelResource(ElementalCommanderArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "geo/elemental_commander_chestplate.geo.json");
   }

   public ResourceLocation getTextureResource(ElementalCommanderArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "textures/models/armor/elemental_commander.png");
   }

   public ResourceLocation getAnimationResource(ElementalCommanderArmorItem animatable) {
      return new ResourceLocation("iss_magicfromtheeast", "animations/master_armor_animation.json");
   }
}
