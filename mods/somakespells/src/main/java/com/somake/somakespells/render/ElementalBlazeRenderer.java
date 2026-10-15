package com.somake.somakespells.render;

import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

public class ElementalBlazeRenderer<T extends Blaze> extends BlazeRenderer {
   private final ResourceLocation texture;

   public ElementalBlazeRenderer(Context context, ResourceLocation texture) {
      super(context);
      this.texture = texture;
   }

   public ResourceLocation getTextureLocation(Blaze entity) {
      return this.texture;
   }
}
