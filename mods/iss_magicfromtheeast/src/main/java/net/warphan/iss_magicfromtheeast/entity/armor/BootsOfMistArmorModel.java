package net.warphan.iss_magicfromtheeast.entity.armor;

import net.minecraft.resources.ResourceLocation;
import net.warphan.iss_magicfromtheeast.item.armor.BootsOfMistArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class BootsOfMistArmorModel extends DefaultedItemGeoModel<BootsOfMistArmorItem> {
   public BootsOfMistArmorModel() {
      super(new ResourceLocation("iss_magicfromtheeast", ""));
   }

   public ResourceLocation getModelResource(BootsOfMistArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "geo/mist_boots.geo.json");
   }

   public ResourceLocation getTextureResource(BootsOfMistArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "textures/models/armor/mist_boots.png");
   }

   public ResourceLocation getAnimationResource(BootsOfMistArmorItem animatable) {
      return new ResourceLocation("iss_magicfromtheeast", "animations/master_armor_animation.json");
   }
}
