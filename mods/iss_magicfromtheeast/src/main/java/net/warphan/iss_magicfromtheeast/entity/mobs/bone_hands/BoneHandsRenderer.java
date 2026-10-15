package net.warphan.iss_magicfromtheeast.entity.mobs.bone_hands;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BoneHandsRenderer extends GeoEntityRenderer<BoneHandsEntity> {
   public static final ResourceLocation textureLocation = new ResourceLocation("iss_magicfromtheeast", "textures/entity/bone_hand.png");

   public BoneHandsRenderer(Context renderManager) {
      super(renderManager, new BoneHandsModel());
      this.shadowRadius = 0.0F;
   }

   public ResourceLocation getTextureLocation(BoneHandsEntity animatable) {
      return textureLocation;
   }

   protected float getDeathMaxRotation(BoneHandsEntity degree) {
      return 0.0F;
   }
}
