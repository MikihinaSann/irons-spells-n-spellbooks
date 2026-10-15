package com.somake.somakespells.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T> {
   public EmptyRenderer(Context pContext) {
      super(pContext);
   }

   public ResourceLocation getTextureLocation(T pEntity) {
      return null;
   }
}
