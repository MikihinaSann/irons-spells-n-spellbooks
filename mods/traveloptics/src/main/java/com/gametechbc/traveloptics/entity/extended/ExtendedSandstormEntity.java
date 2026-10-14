package com.gametechbc.traveloptics.entity.extended;

import com.github.L_Ender.cataclysm.entity.effect.Sandstorm_Entity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExtendedSandstormEntity extends Sandstorm_Entity {
   public ExtendedSandstormEntity(EntityType<?> entityTypeIn, Level worldIn) {
      super(entityTypeIn, worldIn);
   }

   public ExtendedSandstormEntity(Level worldIn, double x, double y, double z, int lifespan, float offset, LivingEntity casterIn) {
      super(worldIn, x, y, z, lifespan, offset, casterIn);
      this.setOwner(casterIn);
      this.setLifespan(lifespan);
      this.setPos(x, y, z);
      this.setState(1);
      this.setOffset(offset);
      if (!worldIn.isClientSide) {
         this.setCasterID(casterIn.getId());
      }
   }

   public void customUpdateMotion() {
      Entity owner = this.getOwner();
      if (owner != null) {
         Vec3 center = owner.position().add(0.0, 0.0, 0.0);
         float radius = 6.0F;
         float speed = this.tickCount * 0.05F;
         float offset = this.getOffset();
         Vec3 orbit = new Vec3(center.x + Math.cos(speed + offset) * radius, center.y, center.z + Math.sin(speed + offset) * radius);
         this.moveTo(orbit);
      }
   }

   public void tick() {
      super.tick();
      this.customUpdateMotion();
   }
}
