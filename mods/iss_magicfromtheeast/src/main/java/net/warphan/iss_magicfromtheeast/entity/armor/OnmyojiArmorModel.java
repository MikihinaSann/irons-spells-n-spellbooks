package net.warphan.iss_magicfromtheeast.entity.armor;

import net.minecraft.resources.ResourceLocation;
import net.warphan.iss_magicfromtheeast.item.armor.OnmyojiArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class OnmyojiArmorModel extends DefaultedItemGeoModel<OnmyojiArmorItem> {
   public OnmyojiArmorModel() {
      super(new ResourceLocation("iss_magicfromtheeast", ""));
   }

   public ResourceLocation getModelResource(OnmyojiArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "geo/onmyoji_armor.geo.json");
   }

   public ResourceLocation getTextureResource(OnmyojiArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "textures/models/armor/onmyoji.png");
   }

   public ResourceLocation getAnimationResource(OnmyojiArmorItem animatable) {
      return new ResourceLocation("iss_magicfromtheeast", "animations/master_armor_animation.json");
   }
}
