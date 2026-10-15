package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.somake.somakespells.particle.CustomStormParticleOptions;
import com.somake.somakespells.particle.OrbitingWaterParticleOptions;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.aqua.SeaSerpentJetSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SeaSerpentJetEntity extends Entity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private float damage;
   private LivingEntity caster;
   private UUID casterId;
   private final List<LivingEntity> hitEntities = new ArrayList<>();

   public SeaSerpentJetEntity(EntityType<?> type, Level level) {
      super(type, level);
   }

   public SeaSerpentJetEntity(Level level, double x, double y, double z, float yaw, float damage, LivingEntity caster) {
      this((EntityType<?>)ModEntityTypes.SEA_SERPENT_JET.get(), level);
      this.setPos(x, y, z);
      this.setYRot(yaw);
      this.damage = damage;
      this.caster = caster;
      if (caster != null) {
         this.casterId = caster.getUUID();
      }
   }

   public void tick() {
      super.tick();
      if (this.tickCount > 32) {
         this.discard();
      }

      if (!this.level().isClientSide) {
         if (this.tickCount == 5 && this.level() instanceof ServerLevel serverLevel) {
            ScreenShake_Entity.ScreenShake(serverLevel, this.position(), 20.0F, 0.2F, 0, 20);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.PLAYERS, 2.0F, 0.8F);
            WaterSplashEntity splash = new WaterSplashEntity((EntityType<?>)ModEntityTypes.WATER_SPLASH.get(), this.level());
            splash.moveTo(this.getX(), this.getY(), this.getZ());
            splash.setYRot(this.getYRot());
            splash.setAnimationState(1);
            this.level().addFreshEntity(splash);
            serverLevel.sendParticles(
               new CustomStormParticleOptions(0.4F, 1.0F, 1.5F, 2.0F, 3.0F, this.getId(), 0.0F),
               this.getX(),
               this.getY() + 1.0,
               this.getZ(),
               2,
               0.5,
               0.5,
               0.5,
               0.1
            );
            serverLevel.sendParticles(
               new OrbitingWaterParticleOptions(1.0F, 1.0F, 1.0F, 1.5F, 2.5F, this.getId(), 0.0F),
               this.getX(),
               this.getY() + 1.0,
               this.getZ(),
               16,
               1.0,
               0.5,
               1.0,
               0.1
            );
            this.spawnSideWater(serverLevel, 10);
         }

         if (this.tickCount == 24 && this.level() instanceof ServerLevel serverLevel) {
            ScreenShake_Entity.ScreenShake(serverLevel, this.position(), 20.0F, 0.2F, 0, 20);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.PLAYERS, 2.0F, 0.8F);
            WaterSplashEntity splash = new WaterSplashEntity((EntityType<?>)ModEntityTypes.WATER_SPLASH.get(), this.level());
            splash.moveTo(this.getX(), this.getY(), this.getZ());
            splash.setYRot(this.getYRot());
            splash.setAnimationState(1);
            this.level().addFreshEntity(splash);
            this.spawnSideWater(serverLevel, 14);
         }

         if (this.tickCount > 5 && this.tickCount < 25) {
            if (this.tickCount % 3 == 0 && this.level() instanceof ServerLevel serverLevel) {
               this.spawnSideWater(serverLevel, 3);
            }

            AABB box = this.getBoundingBox().inflate(1.5, 3.0, 1.5);

            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, box)) {
               if (entity != this.caster && !this.hitEntities.contains(entity) && (this.caster == null || !entity.isAlliedTo(this.caster))) {
                  boolean hurt = DamageSources.applyDamage(
                     entity,
                     this.damage,
                     ((SeaSerpentJetSpell)ModSpells.SEA_SERPENT_JET.get()).getDamageSource(this, (Entity)(this.caster != null ? this.caster : this))
                  );
                  if (hurt) {
                     entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 1.0, 0.0));
                     this.hitEntities.add(entity);
                  }
               }
            }
         }
      }
   }

   private void spawnSideWater(ServerLevel serverLevel, int count) {
      double angle = Math.toRadians(this.getYRot());
      double sideX = Math.cos(angle);
      double sideZ = Math.sin(angle);
      double forwardX = -Math.sin(angle);
      double forwardZ = Math.cos(angle);

      for (int i = 0; i < count; i++) {
         double side = (i % 2 == 0 ? 1.0 : -1.0) * (0.45 + this.random.nextDouble() * 0.55);
         double forward = (this.random.nextDouble() - 0.5) * 0.7;
         double x = this.getX() + sideX * side + forwardX * forward;
         double z = this.getZ() + sideZ * side + forwardZ * forward;
         double y = this.getY() + 0.35 + this.random.nextDouble() * 0.75;
         double velocityX = sideX * Math.copySign(0.12 + this.random.nextDouble() * 0.18, side);
         double velocityZ = sideZ * Math.copySign(0.12 + this.random.nextDouble() * 0.18, side);
         double velocityY = 0.08 + this.random.nextDouble() * 0.16;
         serverLevel.sendParticles(ParticleTypes.SPLASH, x, y, z, 0, velocityX, velocityY, velocityZ, 1.0);
         if (i % 2 == 0) {
            serverLevel.sendParticles(ParticleTypes.FALLING_WATER, x, y + 0.1, z, 0, velocityX * 0.7, velocityY * 0.6, velocityZ * 0.7, 1.0);
         }
      }
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.hasUUID("Caster")) {
         this.casterId = compound.getUUID("Caster");
      }

      this.damage = compound.getFloat("Damage");
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      if (this.casterId != null) {
         compound.putUUID("Caster", this.casterId);
      }

      compound.putFloat("Damage", this.damage);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
      return new ClientboundAddEntityPacket(this, entity);
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   private PlayState predicate(AnimationState<SeaSerpentJetEntity> event) {
      return event.setAndContinue(RawAnimation.begin().thenPlay("animation.storm_serpent.flame_jet"));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
