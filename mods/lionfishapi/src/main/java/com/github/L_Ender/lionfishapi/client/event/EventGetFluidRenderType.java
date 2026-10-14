package com.github.L_Ender.lionfishapi.client.event;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;

@OnlyIn(Dist.CLIENT)
public class EventGetFluidRenderType extends Event {
   private FluidState fluidState;
   private RenderType renderType;
   private EventGetFluidRenderType.Result result = EventGetFluidRenderType.Result.DEFAULT;

   public EventGetFluidRenderType(FluidState fluidState, RenderType renderType) {
      this.fluidState = fluidState;
      this.renderType = renderType;
   }

   public FluidState getFluidState() {
      return this.fluidState;
   }

   public RenderType getRenderType() {
      return this.renderType;
   }

   public void setRenderType(RenderType renderType) {
      this.renderType = renderType;
   }

   public void setResult(EventGetFluidRenderType.Result result) {
      this.result = result;
   }

   public EventGetFluidRenderType.Result getResult() {
      return this.result;
   }

   public enum Result {
      ALLOW,
      DEFAULT,
      DENY;
   }
}
