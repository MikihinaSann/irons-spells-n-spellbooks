package com.gametechbc.traveloptics.entity.spells;

import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.init.TOSpells;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class AshenBreathProjectile extends AbstractConeProjectile {
   public AshenBreathProjectile(EntityType<? extends AbstractConeProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public AshenBreathProjectile(Level level, LivingEntity entity) {
      super((EntityType)TOEntities.ASHEN_BREATH_PROJECTILE.get(), level, entity);
   }

   public void spawnParticles() {
      Entity owner = this.getOwner();
      if (this.level().isClientSide && owner != null) {
         Vec3 rotation = owner.getLookAngle().normalize();
         Vec3 pos = owner.position().add(rotation.scale(1.6));
         double x = pos.x;
         double y = pos.y + owner.getEyeHeight() * 0.9F;
         double z = pos.z;
         double speed = this.random.nextDouble() * 0.4 + 0.45;

         for (int i = 0; i < 24; i++) {
            double offset = 0.25;
            double ox = Math.random() * 2.0 * offset - offset;
            double oy = Math.random() * 2.0 * offset - offset;
            double oz = Math.random() * 2.0 * offset - offset;
            double angularness = 0.8;
            Vec3 randomVec = new Vec3(
                  Math.random() * 2.0 * angularness - angularness,
                  Math.random() * 2.0 * angularness - angularness,
                  Math.random() * 2.0 * angularness - angularness
               )
               .normalize();
            Vec3 result = rotation.scale(3.0).add(randomVec).normalize().scale(speed);
            this.level()
               .addParticle(
                  (ParticleOptions)(this.random.nextFloat() < 0.97F ? ParticleTypes.SMOKE : ParticleHelper.FIRE),
                  x + ox,
                  y + oy,
                  z + oz,
                  result.x,
                  result.y,
                  result.z
               );
         }
      }
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      Entity entity = entityHitResult.getEntity();
      if (DamageSources.applyDamage(entity, this.damage, TOSpells.ASHEN_BREATH_SPELL.get().getDamageSource(this, this.getOwner()))
         && entity instanceof LivingEntity livingEntity) {
         livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
      }
   }
}
