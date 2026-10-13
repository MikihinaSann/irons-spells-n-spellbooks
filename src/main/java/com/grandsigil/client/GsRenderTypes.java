package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public final class GsRenderTypes extends RenderType {
   private static final VertexFormat POSITION_COLOR_TEX = VertexFormat.builder()
      .add("Position", VertexFormatElement.POSITION)
      .add("Color", VertexFormatElement.COLOR)
      .add("UV0", VertexFormatElement.UV0)
      .build();
   private static final ResourceLocation WHITE = GrandSigil.id("textures/entity/white.png");
   private static final ShaderStateShard TEXTURED = new ShaderStateShard(GameRenderer::getPositionTexColorShader);
   private static final GsRenderTypes.Pair GLOW = new GsRenderTypes.Pair(
      create(
         "grandsigil_glow",
         DefaultVertexFormat.POSITION_TEX_COLOR,
         Mode.QUADS,
         2048,
         false,
         false,
         CompositeState.builder()
            .setShaderState(TEXTURED)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      ),
      create(
         "grandsigil_glow_lit",
         POSITION_COLOR_TEX,
         Mode.QUADS,
         2048,
         false,
         false,
         CompositeState.builder()
            .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      )
   );
   private static final GsRenderTypes.Pair STONE = new GsRenderTypes.Pair(
      create(
         "grandsigil_stone",
         DefaultVertexFormat.POSITION_TEX_COLOR,
         Mode.QUADS,
         8192,
         false,
         false,
         CompositeState.builder()
            .setShaderState(TEXTURED)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .createCompositeState(false)
      ),
      create(
         "grandsigil_stone_lit",
         POSITION_COLOR_TEX,
         Mode.QUADS,
         8192,
         false,
         false,
         CompositeState.builder()
            .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .createCompositeState(false)
      )
   );
   private static final GsRenderTypes.Pair GLASS = new GsRenderTypes.Pair(
      create(
         "grandsigil_glass",
         DefaultVertexFormat.POSITION_TEX_COLOR,
         Mode.QUADS,
         4096,
         false,
         false,
         CompositeState.builder()
            .setShaderState(TEXTURED)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      ),
      create(
         "grandsigil_glass_lit",
         POSITION_COLOR_TEX,
         Mode.QUADS,
         4096,
         false,
         false,
         CompositeState.builder()
            .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      )
   );
   private static final GsRenderTypes.Pair FLOOR = new GsRenderTypes.Pair(
      create(
         "grandsigil_floor",
         DefaultVertexFormat.POSITION_TEX_COLOR,
         Mode.QUADS,
         2048,
         false,
         false,
         CompositeState.builder()
            .setShaderState(TEXTURED)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .setLayeringState(POLYGON_OFFSET_LAYERING)
            .createCompositeState(false)
      ),
      create(
         "grandsigil_floor_lit",
         POSITION_COLOR_TEX,
         Mode.QUADS,
         2048,
         false,
         false,
         CompositeState.builder()
            .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .setLayeringState(POLYGON_OFFSET_LAYERING)
            .createCompositeState(false)
      )
   );
   private static final GsRenderTypes.Pair SKY = new GsRenderTypes.Pair(
      create(
         "grandsigil_sky",
         DefaultVertexFormat.POSITION_TEX_COLOR,
         Mode.QUADS,
         4096,
         false,
         false,
         CompositeState.builder()
            .setShaderState(TEXTURED)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(NO_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      ),
      create(
         "grandsigil_sky_lit",
         POSITION_COLOR_TEX,
         Mode.QUADS,
         4096,
         false,
         false,
         CompositeState.builder()
            .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      )
   );
   private static final GsRenderTypes.Pair VEIL = new GsRenderTypes.Pair(
      create(
         "grandsigil_veil",
         DefaultVertexFormat.POSITION_TEX_COLOR,
         Mode.QUADS,
         4096,
         false,
         false,
         CompositeState.builder()
            .setShaderState(TEXTURED)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(NO_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      ),
      create(
         "grandsigil_veil_lit",
         POSITION_COLOR_TEX,
         Mode.QUADS,
         4096,
         false,
         false,
         CompositeState.builder()
            .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
            .setTextureState(new TextureStateShard(WHITE, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      )
   );
   private static final GsRenderTypes.Family PATCH = new GsRenderTypes.Family(
      texture -> create(
            "grandsigil_patch",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            Mode.QUADS,
            1024,
            false,
            false,
            CompositeState.builder()
               .setShaderState(TEXTURED)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(NO_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         ),
      texture -> create(
            "grandsigil_patch_lit",
            POSITION_COLOR_TEX,
            Mode.QUADS,
            1024,
            false,
            false,
            CompositeState.builder()
               .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(NO_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         )
   );
   private static final GsRenderTypes.Family CELESTIAL = new GsRenderTypes.Family(
      texture -> create(
            "grandsigil_celestial",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            Mode.QUADS,
            1024,
            false,
            false,
            CompositeState.builder()
               .setShaderState(TEXTURED)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(ADDITIVE_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(NO_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         ),
      texture -> create(
            "grandsigil_celestial_lit",
            POSITION_COLOR_TEX,
            Mode.QUADS,
            1024,
            false,
            false,
            CompositeState.builder()
               .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(ADDITIVE_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(NO_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         )
   );
   private static final GsRenderTypes.Family INK = new GsRenderTypes.Family(
      texture -> create(
            "grandsigil_ink",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            Mode.QUADS,
            1024,
            false,
            false,
            CompositeState.builder()
               .setShaderState(TEXTURED)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         ),
      texture -> create(
            "grandsigil_ink_lit",
            POSITION_COLOR_TEX,
            Mode.QUADS,
            1024,
            false,
            false,
            CompositeState.builder()
               .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         )
   );
   private static final GsRenderTypes.Family MATTER = new GsRenderTypes.Family(
      texture -> create(
            "grandsigil_matter",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            Mode.QUADS,
            8192,
            false,
            false,
            CompositeState.builder()
               .setShaderState(TEXTURED)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         ),
      texture -> create(
            "grandsigil_matter_lit",
            POSITION_COLOR_TEX,
            Mode.QUADS,
            8192,
            false,
            false,
            CompositeState.builder()
               .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         )
   );
   private static final GsRenderTypes.Family SCULPT = new GsRenderTypes.Family(
      texture -> create(
            "grandsigil_sculpt",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            Mode.QUADS,
            8192,
            false,
            false,
            CompositeState.builder()
               .setShaderState(TEXTURED)
               .setTextureState(new TextureStateShard(texture, true, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_DEPTH_WRITE)
               .createCompositeState(false)
         ),
      texture -> create(
            "grandsigil_sculpt_lit",
            POSITION_COLOR_TEX,
            Mode.QUADS,
            8192,
            false,
            false,
            CompositeState.builder()
               .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
               .setTextureState(new TextureStateShard(texture, true, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_DEPTH_WRITE)
               .createCompositeState(false)
         )
   );
   private static final GsRenderTypes.Family GLOW_TEX = new GsRenderTypes.Family(
      texture -> create(
            "grandsigil_glow_tex",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            Mode.QUADS,
            2048,
            false,
            false,
            CompositeState.builder()
               .setShaderState(TEXTURED)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(ADDITIVE_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         ),
      texture -> create(
            "grandsigil_glow_tex_lit",
            POSITION_COLOR_TEX,
            Mode.QUADS,
            2048,
            false,
            false,
            CompositeState.builder()
               .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(ADDITIVE_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         )
   );
   private static final GsRenderTypes.Family FLOOR_TEX = new GsRenderTypes.Family(
      texture -> create(
            "grandsigil_floor_tex",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            Mode.QUADS,
            2048,
            false,
            false,
            CompositeState.builder()
               .setShaderState(TEXTURED)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(ADDITIVE_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .setLayeringState(POLYGON_OFFSET_LAYERING)
               .createCompositeState(false)
         ),
      texture -> create(
            "grandsigil_floor_tex_lit",
            POSITION_COLOR_TEX,
            Mode.QUADS,
            2048,
            false,
            false,
            CompositeState.builder()
               .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
               .setTextureState(new TextureStateShard(texture, false, false))
               .setTransparencyState(ADDITIVE_TRANSPARENCY)
               .setCullState(NO_CULL)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .setLayeringState(POLYGON_OFFSET_LAYERING)
               .createCompositeState(false)
         )
   );

   private GsRenderTypes(
      String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState
   ) {
      super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
      throw new UnsupportedOperationException("held only for its inherited access");
   }

   public static RenderType glow() {
      return GLOW.get();
   }

   public static RenderType floor() {
      return FLOOR.get();
   }

   public static RenderType glass() {
      return GLASS.get();
   }

   public static RenderType stone() {
      return STONE.get();
   }

   public static RenderType sky() {
      return SKY.get();
   }

   public static RenderType veil() {
      return VEIL.get();
   }

   public static RenderType patch(ResourceLocation texture) {
      return PATCH.get(texture);
   }

   public static RenderType celestial(ResourceLocation texture) {
      return CELESTIAL.get(texture);
   }

   public static RenderType ink(ResourceLocation texture) {
      return INK.get(texture);
   }

   public static RenderType matter(ResourceLocation texture) {
      return MATTER.get(texture);
   }

   public static RenderType sculpt(ResourceLocation texture) {
      return SCULPT.get(texture);
   }

   public static RenderType glow(ResourceLocation texture) {
      return GLOW_TEX.get(texture);
   }

   public static RenderType floor(ResourceLocation texture) {
      return FLOOR_TEX.get(texture);
   }

   private static final class Family {
      private final Function<ResourceLocation, RenderType> plain;
      private final Function<ResourceLocation, RenderType> lit;

      Family(Function<ResourceLocation, RenderType> plain, Function<ResourceLocation, RenderType> lit) {
         this.plain = Util.memoize(plain);
         this.lit = Util.memoize(lit);
      }

      RenderType get(ResourceLocation texture) {
         return (Shaders.packInUse() ? this.lit : this.plain).apply(texture);
      }
   }

   private static final class Pair {
      private final RenderType plain;
      private final RenderType lit;

      Pair(RenderType plain, RenderType lit) {
         this.plain = plain;
         this.lit = lit;
      }

      RenderType get() {
         return Shaders.packInUse() ? this.lit : this.plain;
      }
   }
}
