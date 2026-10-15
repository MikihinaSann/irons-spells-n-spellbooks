package net.alshanex.alshanex_familiars.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public class FrozenEntityRenderType extends RenderType {
   private static final ResourceLocation ICE_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/ice.png");

   public FrozenEntityRenderType(
      String nameIn,
      VertexFormat formatIn,
      Mode drawModeIn,
      int bufferSizeIn,
      boolean useDelegateIn,
      boolean needsSortingIn,
      Runnable setupTaskIn,
      Runnable clearTaskIn
   ) {
      super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
   }

   public static RenderType getFrozenEntityRenderType(float x, float y) {
      TextureStateShard textureState = new TextureStateShard(ICE_TEXTURE, false, false);
      CompositeState rendertype = CompositeState.builder()
         .setShaderState(RenderType.RENDERTYPE_ENTITY_CUTOUT_SHADER)
         .setTextureState(textureState)
         .setTransparencyState(NO_TRANSPARENCY)
         .setLightmapState(LIGHTMAP)
         .setOverlayState(OVERLAY)
         .createCompositeState(true);
      return create("frozen_entity_type", DefaultVertexFormat.NEW_ENTITY, Mode.QUADS, 256, false, true, rendertype);
   }
}
