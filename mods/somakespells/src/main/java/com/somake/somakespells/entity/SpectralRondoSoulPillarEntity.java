package com.somake.somakespells.entity;

import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulPillarEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SpectralRondoSoulPillarEntity extends SoulPillarEntity implements IronSpellDamageEntity {
   public SpectralRondoSoulPillarEntity(
      Level level, double x, double y, double z, float rotation, int warmupDelay, LivingEntity owner, int lifeTicks, boolean red
   ) {
      super(level, x, y, z, rotation, warmupDelay, owner, lifeTicks, 0.0F, red);
   }

   @Override
   public boolean somake$applySpellDamage(LivingEntity target, float ignoredAmount) {
      return false;
   }

   public boolean shouldBeSaved() {
      return false;
   }
}
