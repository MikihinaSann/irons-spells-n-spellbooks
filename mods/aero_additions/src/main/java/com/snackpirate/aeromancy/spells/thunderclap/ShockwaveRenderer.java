package com.snackpirate.aeromancy.spells.thunderclap;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class ShockwaveRenderer extends EntityRenderer<ShockwaveEntity> {
   protected ShockwaveRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(ShockwaveEntity shockwaveEntity) {
      return null;
   }
}
