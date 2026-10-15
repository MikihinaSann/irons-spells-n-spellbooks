package be.florens.expandability.api.forge;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingFluidCollisionEvent extends LivingEvent {
   private final FluidState fluidState;
   private boolean shouldCollide = false;

   public LivingFluidCollisionEvent(LivingEntity entity, FluidState fluidState) {
      super(entity);
      this.fluidState = fluidState;
   }

   public FluidState getFluidState() {
      return this.fluidState;
   }

   public boolean shouldCollide() {
      return this.shouldCollide;
   }

   public void setColliding(boolean shouldCollide) {
      this.shouldCollide = shouldCollide;
   }
}
