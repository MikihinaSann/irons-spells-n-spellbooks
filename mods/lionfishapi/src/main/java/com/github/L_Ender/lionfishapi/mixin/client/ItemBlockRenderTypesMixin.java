package com.github.L_Ender.lionfishapi.mixin.client;

import com.github.L_Ender.lionfishapi.client.event.EventGetFluidRenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {
   @Inject(
      at = @At("TAIL"),
      remap = true,
      cancellable = true,
      method = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getRenderLayer(Lnet/minecraft/world/level/material/FluidState;)Lnet/minecraft/client/renderer/RenderType;"
   )
   private static void lionfish_getFluidRenderLayer(FluidState fluidState, CallbackInfoReturnable<RenderType> cir) {
      EventGetFluidRenderType event = new EventGetFluidRenderType(fluidState, (RenderType)cir.getReturnValue());
      NeoForge.EVENT_BUS.post(event);
      if (event.getResult() == EventGetFluidRenderType.Result.ALLOW) {
         cir.setReturnValue(event.getRenderType());
      }
   }
}
