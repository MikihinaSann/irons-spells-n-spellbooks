package com.gametechbc.traveloptics.entity.spells.gyro_slash;

import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.init.TOSpells;
import com.github.L_Ender.cataclysm.client.particle.Options.LightTrailParticleOptions;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class GyroSlashProjectile extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(GyroSlashProjectile.class, EntityDataSerializers.FLOAT);
   private static final double SPEED = 1.0;
   private static final int EXPIRE_TIME = 80;
   private final float maxRadius;
   public AABB oldBB;
   private int age;
   private float damage;
   private int effectAmplifier = 1;
   private int effectDuration = 40;
   public int animationTime;
   private List<Entity> victims;

   public GyroSlashProjectile(EntityType<? extends GyroSlashProjectile> entityType, Level level) {
      super(entityType, level);
      this.setRadius(0.6F);
      this.maxRadius = 3.0F;
      this.oldBB = this.getBoundingBox();
      this.victims = new ArrayList<>();
      this.setNoGravity(true);
   }

   public GyroSlashProjectile(EntityType<? extends GyroSlashProjectile> entityType, Level levelIn, LivingEntity shooter) {
      this(entityType, levelIn);
      this.setOwner(shooter);
      this.setYRot(shooter.getYRot());
      this.setXRot(shooter.getXRot());
   }

   public GyroSlashProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends GyroSlashProjectile>)TOEntities.GYRO_SLASH_PROJECTILE.get(), levelIn, shooter);
   }

   public void shoot(Vec3 rotation) {
      this.setDeltaMovement(rotation.scale(1.0));
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public int getEffectAmplifier() {
      return this.effectAmplifier;
   }

   public void setEffectAmplifier(int effectAmplifier) {
      this.effectAmplifier = effectAmplifier;
   }

   public int getEffectDuration() {
      return this.effectDuration;
   }

   public void setEffectDuration(int effectDuration) {
      this.effectDuration = effectDuration;
   }

   protected void defineSynchedData(Builder pBuilder) {
      pBuilder.define(DATA_RADIUS, 0.5F);
   }

   public void setRadius(float newRadius) {
      if (newRadius <= this.maxRadius && !this.level().isClientSide) {
         this.getEntityData().set(DATA_RADIUS, Mth.clamp(newRadius, 0.0F, this.maxRadius));
      }
   }

   public float getRadius() {
      return (Float)this.getEntityData().get(DATA_RADIUS);
   }

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   public void tick() {
      super.tick();
      if (++this.age > 80) {
         this.discard();
      } else {
         this.oldBB = this.getBoundingBox();
         this.setRadius(this.getRadius() + 0.12F);
         if (!this.level().isClientSide) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() == Type.BLOCK) {
               this.onHitBlock((BlockHitResult)hitresult);
            }

            for (Entity entity : this.level()
               .getEntities(this, this.getBoundingBox())
               .stream()
               .filter(target -> this.canHitEntity(target) && !this.victims.contains(target))
               .collect(Collectors.toSet())) {
               this.damageEntity(entity);
               if (entity instanceof ShieldPart || entity instanceof AbstractShieldEntity) {
                  this.discard();
                  return;
               }
            }
         }

         if (this.level().isClientSide) {
            float ran = 0.04F;
            float r = 0.7647059F + this.random.nextFloat() * ran * 1.5F;
            float g = 0.37254903F + this.random.nextFloat() * ran;
            float b = 0.011764706F + this.random.nextFloat() * ran;
            this.level()
               .addParticle(
                  new LightTrailParticleOptions(r, g, b, 2.5F, this.getBbHeight() / 2.0F, this.getId()), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0
               );
         }

         this.setPos(this.position().add(this.getDeltaMovement()));
         this.spawnParticles();
      }
   }

   public EntityDimensions getDimensions(Pose p_19721_) {
      this.getBoundingBox();
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
      if (DATA_RADIUS.equals(p_19729_)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_19729_);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   private void damageEntity(Entity entity) {
      if (!this.victims.contains(entity)) {
         DamageSources.applyDamage(entity, this.damage, TOSpells.GYRO_SLASH_SPELL.get().getDamageSource(this, this.getOwner()));
         if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(TOEffects.FLARE_VACUUM_EFFECT, this.effectDuration, this.effectAmplifier));
         }

         this.victims.add(entity);
      }
   }

   protected boolean canHitEntity(Entity entity) {
      return entity != this.getOwner() && super.canHitEntity(entity);
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.discard();
   }

   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("Damage", this.damage);
      pCompound.putInt("EffectAmplifier", this.effectAmplifier);
      pCompound.putInt("EffectDuration", this.effectDuration);
   }

   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.damage = pCompound.getFloat("Damage");
      this.effectAmplifier = pCompound.getInt("EffectAmplifier");
      this.effectDuration = pCompound.getInt("EffectDuration");
   }

   public void spawnParticles() {
      if (this.level().isClientSide) {
         float width = (float)this.getBoundingBox().getXsize();
         float step = 0.25F;
         float radians = (float) (Math.PI / 180.0) * this.getYRot();
         float speed = 0.1F;

         for (int i = 0; i < width / step; i++) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            double offset = step * (i - width / step / 2.0F);
            double rotX = offset * Math.cos(radians);
            double rotZ = -offset * Math.sin(radians);
            double dx = Math.random() * speed * 2.0 - speed;
            double dy = Math.random() * speed * 2.0 - speed;
            double dz = Math.random() * speed * 2.0 - speed;
            this.level().addParticle(ParticleHelper.EMBERS, false, x + rotX + dx, y + dy, z + rotZ + dz, dx, dy, dz);
         }
      }
   }
}
