package net.warphan.iss_magicfromtheeast.entity.armor;

import net.minecraft.resources.ResourceLocation;
import net.warphan.iss_magicfromtheeast.item.armor.JiangshiHatItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class JiangshiHatModel extends DefaultedItemGeoModel<JiangshiHatItem> {
   public JiangshiHatModel() {
      super(new ResourceLocation("iss_magicfromtheeast", ""));
   }

   public ResourceLocation getModelResource(JiangshiHatItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "geo/jiangshi_hat.geo.json");
   }

   public ResourceLocation getTextureResource(JiangshiHatItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "textures/models/armor/jiangshi_hat.png");
   }

   public ResourceLocation getAnimationResource(JiangshiHatItem animatable) {
      return new ResourceLocation("iss_magicfromtheeast", "animations/master_armor_animation.json");
   }
}
