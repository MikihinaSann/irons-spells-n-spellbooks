package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class LightningFieldEntity extends Projectile {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(LightningFieldEntity.class, EntityDataSerializers.FLOAT);
   private float damage;
   private int duration;
   private AbstractSpell damageSpell;

   public LightningFieldEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public LightningFieldEntity(Level level, LivingEntity owner, float radius, int duration, float damage) {
      this((EntityType<? extends Projectile>)ModEntityTypes.LIGHTNING_FIELD.get(), level);
      this.setOwner(owner);
      this.setRadius(radius);
      this.duration = duration;
      this.damage = damage;
      this.setPos(owner != null ? owner.getX() : 0.0, owner != null ? owner.getY() : 0.0, owner != null ? owner.getZ() : 0.0);
   }

   public void setDamageSpell(AbstractSpell damageSpell) {
      this.damageSpell = damageSpell;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_RADIUS, 5.0F);
   }

   public void setRadius(float radius) {
      this.entityData.set(DATA_RADIUS, radius);
   }

   public float getRadius() {
      return (Float)this.entityData.get(DATA_RADIUS);
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         float radius = this.getRadius();
         if (this.tickCount > 5) {
            int particles = 4;
            float step = 360.0F / particles;
            float offset = this.tickCount * 5 % 360;

            for (int i = 0; i < particles; i++) {
               double theta = Math.toRadians(offset + i * step);
               double x = this.getX() + radius * Math.cos(theta);
               double z = this.getZ() + radius * Math.sin(theta);
               this.level().addParticle(ParticleHelper.ELECTRICITY, x, this.getY() + 0.1, z, 0.0, 0.0, 0.0);
            }
         }

         if (this.tickCount > 10) {
            for (int i = 0; i < 2; i++) {
               double r = radius * Math.sqrt(this.random.nextDouble());
               double theta = this.random.nextDouble() * 2.0 * Math.PI;
               double x = this.getX() + r * Math.cos(theta);
               double z = this.getZ() + r * Math.sin(theta);
               this.level().addParticle(ParticleHelper.ELECTRICITY, x, this.getY() + 0.1, z, 0.0, 0.0, 0.0);
            }
         }
      } else {
         if (this.tickCount == 1) {
            float radiusx = this.getRadius();
            Vector3f center = new Vector3f(0.5F, 0.8F, 1.0F);
            MagicManager.spawnParticles(
               this.level(), new BlastwaveParticleOptions(center, radiusx), this.getX(), this.getY() + 0.15F, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
            );
         }

         if (this.tickCount > this.duration) {
            this.discard();
            return;
         }

         if (this.tickCount % 10 == 0) {
            float radiusx = this.getRadius();

            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radiusx, 2.0, radiusx))) {
               if (entity != this.getOwner() && entity.distanceToSqr(this) < radiusx * radiusx) {
                  Entity owner = this.getOwner();
                  AbstractSpell spell = this.damageSpell != null ? this.damageSpell : (AbstractSpell)ModSpells.LIGHTNING_FIELD.get();
                  if (DamageSources.applyDamage(entity, this.damage, spell.getDamageSource(this, (Entity)(owner != null ? owner : this)))) {
                     entity.addEffect(new MobEffectInstance(ModMobEffects.ELECTROCUTED, 30, 0));
                  }

                  if (this.level() instanceof ServerLevel serverLevel) {
                     Vec3 end = entity.getBoundingBox().getCenter();
                     serverLevel.sendParticles(new ZapParticleOption(end), entity.getX(), entity.getY(), entity.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
                  }
               }
            }
         }
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putFloat("Radius", this.getRadius());
      tag.putInt("Age", this.tickCount);
      tag.putInt("Duration", this.duration);
      tag.putFloat("Damage", this.damage);
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      this.setRadius(tag.getFloat("Radius"));
      this.tickCount = tag.getInt("Age");
      this.duration = tag.getInt("Duration");
      this.damage = tag.getFloat("Damage");
   }
}
