package net.warphan.iss_magicfromtheeast.entity.armor;

import net.minecraft.resources.ResourceLocation;
import net.warphan.iss_magicfromtheeast.item.armor.TaoistArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class TaoistArmorModel extends DefaultedItemGeoModel<TaoistArmorItem> {
   public TaoistArmorModel() {
      super(new ResourceLocation("iss_magicfromtheeast", ""));
   }

   public ResourceLocation getModelResource(TaoistArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "geo/taoist_armor.geo.json");
   }

   public ResourceLocation getTextureResource(TaoistArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "textures/models/armor/taoist.png");
   }

   public ResourceLocation getAnimationResource(TaoistArmorItem animatable) {
      return new ResourceLocation("iss_magicfromtheeast", "animations/master_armor_animation.json");
   }
}
