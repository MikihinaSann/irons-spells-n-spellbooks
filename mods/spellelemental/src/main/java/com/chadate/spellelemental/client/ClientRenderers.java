package com.chadate.spellelemental.client;

import com.chadate.spellelemental.client.render.SwordStandRenderer;
import com.chadate.spellelemental.register.ModBlockEntities;
import com.chadate.spellelemental.register.ModFluid;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   modid = "spellelemental",
   value = {Dist.CLIENT}
)
public class ClientRenderers {
   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(() -> {
         BlockEntityRenderers.register(ModBlockEntities.SWORD_STAND.get(), SwordStandRenderer::new);
         net.neoforged.neoforge.client.extensions.common.NeoForgeFluidRenderLayers.setRenderLayer((Fluid)ModFluid.STELLAR_ESSENCE.get(), RenderType.translucent());
         net.neoforged.neoforge.client.extensions.common.NeoForgeFluidRenderLayers.setRenderLayer((Fluid)ModFluid.FIRE_SWORD_OIL.get(), RenderType.translucent());
         net.neoforged.neoforge.client.extensions.common.NeoForgeFluidRenderLayers.setRenderLayer((Fluid)ModFluid.ICE_SWORD_OIL.get(), RenderType.translucent());
         net.neoforged.neoforge.client.extensions.common.NeoForgeFluidRenderLayers.setRenderLayer((Fluid)ModFluid.LIGHTNING_SWORD_OIL.get(), RenderType.translucent());
         net.neoforged.neoforge.client.extensions.common.NeoForgeFluidRenderLayers.setRenderLayer((Fluid)ModFluid.BLOOD_SWORD_OIL.get(), RenderType.translucent());
         net.neoforged.neoforge.client.extensions.common.NeoForgeFluidRenderLayers.setRenderLayer((Fluid)ModFluid.NATURE_SWORD_OIL.get(), RenderType.translucent());
      });
   }

   @SubscribeEvent
   public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
      final ResourceLocation WATER_STILL = ResourceLocation.withDefaultNamespace("block/water_still");
      final ResourceLocation WATER_FLOW = ResourceLocation.withDefaultNamespace("block/water_flow");
      event.registerFluidType(new IClientFluidTypeExtensions() {
         @NotNull
         public ResourceLocation getStillTexture() {
            return WATER_STILL;
         }

         @NotNull
         public ResourceLocation getFlowingTexture() {
            return WATER_FLOW;
         }

         public int getTintColor() {
            return -9807155;
         }
      }, new FluidType[]{(FluidType)ModFluid.STELLAR_ESSENCE_TYPE.get()});
      event.registerFluidType(new IClientFluidTypeExtensions() {
         @NotNull
         public ResourceLocation getStillTexture() {
            return WATER_STILL;
         }

         @NotNull
         public ResourceLocation getFlowingTexture() {
            return WATER_FLOW;
         }

         public int getTintColor() {
            return -47872;
         }
      }, new FluidType[]{(FluidType)ModFluid.FIRE_SWORD_OIL_TYPE.get()});
      event.registerFluidType(new IClientFluidTypeExtensions() {
         @NotNull
         public ResourceLocation getStillTexture() {
            return WATER_STILL;
         }

         @NotNull
         public ResourceLocation getFlowingTexture() {
            return WATER_FLOW;
         }

         public int getTintColor() {
            return -7876885;
         }
      }, new FluidType[]{(FluidType)ModFluid.ICE_SWORD_OIL_TYPE.get()});
      event.registerFluidType(new IClientFluidTypeExtensions() {
         @NotNull
         public ResourceLocation getStillTexture() {
            return WATER_STILL;
         }

         @NotNull
         public ResourceLocation getFlowingTexture() {
            return WATER_FLOW;
         }

         public int getTintColor() {
            return -14774017;
         }
      }, new FluidType[]{(FluidType)ModFluid.LIGHTNING_SWORD_OIL_TYPE.get()});
      event.registerFluidType(new IClientFluidTypeExtensions() {
         @NotNull
         public ResourceLocation getStillTexture() {
            return WATER_STILL;
         }

         @NotNull
         public ResourceLocation getFlowingTexture() {
            return WATER_FLOW;
         }

         public int getTintColor() {
            return -7667712;
         }
      }, new FluidType[]{(FluidType)ModFluid.BLOOD_SWORD_OIL_TYPE.get()});
      event.registerFluidType(new IClientFluidTypeExtensions() {
         @NotNull
         public ResourceLocation getStillTexture() {
            return WATER_STILL;
         }

         @NotNull
         public ResourceLocation getFlowingTexture() {
            return WATER_FLOW;
         }

         public int getTintColor() {
            return -13447886;
         }
      }, new FluidType[]{(FluidType)ModFluid.NATURE_TYPE.get()});
   }
}
