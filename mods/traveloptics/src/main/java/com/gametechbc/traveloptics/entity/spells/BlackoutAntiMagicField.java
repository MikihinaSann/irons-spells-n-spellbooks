package com.gametechbc.traveloptics.entity.spells;

import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.util.TODamageTypes;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class BlackoutAntiMagicField extends AoeEntity {
   private DamageSource movementDamageSource;
   private final Map<LivingEntity, Vec3> entityLastPositions = new HashMap<>();
   private float damage = 2.0F;

   public BlackoutAntiMagicField(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public BlackoutAntiMagicField(Level level) {
      this((EntityType<? extends Projectile>)TOEntities.BLACKOUT_ANTI_MAGIC_FIELD.get(), level);
   }

   public void applyEffect(LivingEntity target) {
      target.addEffect(new MobEffectInstance(TOEffects.BLACKOUT, 60, 0, false, false, false));
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.tickCount % 20 == 0) {
         MagicManager.spawnParticles(
            this.level(),
            new BlastwaveParticleOptions(new Vector3f(0.1F, 0.1F, 0.1F), this.getRadius()),
            this.getX(),
            this.getY() + 0.165F,
            this.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );

         for (LivingEntity entity : this.getEntitiesInsideRadius(LivingEntity.class)) {
            if (entity != this.getOwner()) {
               Vec3 currentPos = entity.position();
               Vec3 lastPos = this.entityLastPositions.getOrDefault(entity, currentPos);
               if (!currentPos.equals(lastPos)) {
                  if (this.movementDamageSource == null) {
                     this.movementDamageSource = new DamageSource(DamageSources.getHolderFromResource(entity, TODamageTypes.BLACKOUT), this, this.getOwner());
                  }

                  DamageSources.ignoreNextKnockback(entity);
                  entity.hurt(this.movementDamageSource, this.damage);
               }

               this.entityLastPositions.put(entity, currentPos);
            }
         }

         this.entityLastPositions.keySet().removeIf(entityx -> !this.isInsideRadius(entityx.position()));
      }
   }

   public float getParticleCount() {
      return 1.2F * this.getRadius();
   }

   protected float particleYOffset() {
      return 0.25F;
   }

   protected float getParticleSpeedModifier() {
      return 1.4F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.of(ParticleTypes.SMOKE);
   }

   private <T extends Entity> List<T> getEntitiesInsideRadius(Class<T> entityType) {
      return this.level().getEntitiesOfClass(entityType, this.getBoundingBox());
   }

   private boolean isInsideRadius(Vec3 position) {
      return position.distanceTo(this.position()) <= this.getRadius();
   }

   public float getDamage() {
      return this.damage;
   }

   public void setMovementDamage(float damage) {
      this.damage = damage;
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putFloat("Damage", this.damage);
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("Damage")) {
         this.damage = tag.getFloat("Damage");
      }
   }
}
