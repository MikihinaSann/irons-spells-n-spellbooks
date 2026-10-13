package net.veroxuniverse.crystal_chronicles.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BaseFluidType extends FluidType {
   private final ResourceLocation stillTexture;
   private final ResourceLocation flowingTexture;
   private final Vector3f fogColor;

   public BaseFluidType(ResourceLocation stillTexture, ResourceLocation flowingTexture, Vector3f fogColor, Properties properties) {
      super(properties);
      this.stillTexture = stillTexture;
      this.flowingTexture = flowingTexture;
      this.fogColor = fogColor;
   }

   public IClientFluidTypeExtensions getClientFluidTypeExtensions() {
      return new IClientFluidTypeExtensions() {
         @Override
         public ResourceLocation getStillTexture() {
            return BaseFluidType.this.stillTexture;
         }

         @Override
         public ResourceLocation getFlowingTexture() {
            return BaseFluidType.this.flowingTexture;
         }

         @NotNull
         public Vector3f modifyFogColor(
            Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor
         ) {
            return BaseFluidType.this.fogColor;
         }

         public void modifyFogRender(
            Camera camera, FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape
         ) {
            RenderSystem.setShaderFogStart(0.5F);
            RenderSystem.setShaderFogEnd(1.0F);
         }
      };
   }
}
