package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CustodiaCaeliSigilEntity extends Entity {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(CustodiaCaeliSigilEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(CustodiaCaeliSigilEntity.class, EntityDataSerializers.INT);
   private static final int BUFF_INTERVAL_TICKS = 10;
   private static final double MAX_LAYER_SCALE = 1.14;
   private static final double MAX_PULSE_SCALE = 1.04;
   private static final double CULL_PADDING = 0.75;
   private static final int PREPARE_DURATION_TICKS = 16;
   private int effectDuration;
   private int effectAmplifier;

   public CustodiaCaeliSigilEntity(EntityType<? extends CustodiaCaeliSigilEntity> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public CustodiaCaeliSigilEntity(Level level, LivingEntity owner, float radius, int duration, int effectDuration, int effectAmplifier) {
      this(level, owner.getX(), owner.getBoundingBox().minY + 0.02, owner.getZ(), owner, radius, duration, effectDuration, effectAmplifier);
   }

   public CustodiaCaeliSigilEntity(
      Level level, double x, double y, double z, LivingEntity owner, float radius, int duration, int effectDuration, int effectAmplifier
   ) {
      this((EntityType<? extends CustodiaCaeliSigilEntity>)ModEntityTypes.CUSTODIA_CAELI_SIGIL.get(), level);
      this.setPos(x, y, z);
      this.setRadius(radius);
      this.setDuration(duration);
      this.effectDuration = effectDuration;
      this.effectAmplifier = effectAmplifier;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_RADIUS, 6.0F);
      builder.define(DATA_DURATION, 360);
   }

   public float getRadius() {
      return (Float)this.entityData.get(DATA_RADIUS);
   }

   public void setRadius(float radius) {
      this.entityData.set(DATA_RADIUS, radius);
   }

   public int getDuration() {
      return (Integer)this.entityData.get(DATA_DURATION);
   }

   public void setDuration(int duration) {
      this.entityData.set(DATA_DURATION, duration);
   }

   public float getProgress(float partialTick) {
      return Math.min(1.0F, (this.tickCount + partialTick) / Math.max(1, this.getDuration()));
   }

   private double getRenderCullRadius() {
      double maxRenderedHalfSize = this.getRadius() * 1.14 * 1.04;
      return maxRenderedHalfSize * Math.sqrt(2.0) + 0.75;
   }

   public AABB getBoundingBoxForCulling() {
      double cullRadius = this.getRenderCullRadius();
      return new AABB(
         this.getX() - cullRadius, this.getY() - 1.0, this.getZ() - cullRadius, this.getX() + cullRadius, this.getY() + 1.5, this.getZ() + cullRadius
      );
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      double maxDistance = Math.max(64.0, this.getRenderCullRadius() * 64.0);
      return distance < maxDistance * maxDistance;
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         this.spawnClientWisps();
      } else if (this.level() instanceof ServerLevel serverLevel) {
         if (this.tickCount == 1) {
            this.spawnPreparationWisps(serverLevel);
         }

         if (this.tickCount % 2 == 0) {
            this.spawnAmbientWisps(serverLevel);
         }

         if (this.tickCount == 16) {
            this.spawnInitialBurst(serverLevel);
         }

         if (this.tickCount >= 16 && (this.tickCount == 16 || this.tickCount % 10 == 0)) {
            this.applyBlessing();
         }

         if (this.tickCount >= this.getDuration()) {
            this.discard();
         }
      }
   }

   private void spawnClientWisps() {
      float radius = this.getRadius();
      if (this.tickCount % 2 == 0) {
         double angle = Math.toRadians(this.tickCount * 12.0F % 360.0F);
         double x = this.getX() + Math.cos(angle) * radius * 0.72;
         double z = this.getZ() + Math.sin(angle) * radius * 0.72;
         this.level().addParticle(ParticleHelper.WISP, x, this.getY() + 0.08, z, 0.0, 0.01, 0.0);
         this.level().addParticle(ParticleHelper.WISP, this.getX(), this.getY() + 0.12, this.getZ(), 0.0, 0.015, 0.0);
      }
   }

   private void spawnPreparationWisps(ServerLevel serverLevel) {
      float radius = this.getRadius();

      for (int i = 0; i < 48; i++) {
         double angle = Math.toRadians(7.5 * i);
         double x = this.getX() + Math.cos(angle) * radius;
         double z = this.getZ() + Math.sin(angle) * radius;
         double mx = (serverLevel.random.nextDouble() - 0.5) * 0.04;
         double my = 0.01 + serverLevel.random.nextDouble() * 0.03;
         double mz = (serverLevel.random.nextDouble() - 0.5) * 0.04;
         serverLevel.sendParticles(ParticleHelper.WISP, x, this.getY() + 0.1, z, 1, mx, my, mz, 0.0);
      }
   }

   private void spawnInitialBurst(ServerLevel serverLevel) {
      float radius = this.getRadius();
      MagicManager.spawnParticles(
         serverLevel, new BlastwaveParticleOptions(1.0F, 0.92F, 0.36F, radius), this.getX(), this.getY() + 0.04, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(1.0F, 0.98F, 0.82F, radius * 0.72F),
         this.getX(),
         this.getY() + 0.08,
         this.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         serverLevel, ParticleHelper.WISP, this.getX(), this.getY() + 0.15, this.getZ(), 36, radius * 0.25, 0.08, radius * 0.25, 0.08, true
      );
   }

   private void spawnAmbientWisps(ServerLevel serverLevel) {
      float radius = this.getRadius();
      float baseRotation = this.tickCount * 8.0F;

      for (int i = 0; i < 8; i++) {
         double angle = Math.toRadians(baseRotation + i * 45.0F);
         double orbitRadius = i % 2 == 0 ? radius * 0.94 : radius * 0.68;
         double x = this.getX() + Math.cos(angle) * orbitRadius;
         double z = this.getZ() + Math.sin(angle) * orbitRadius;
         serverLevel.sendParticles(ParticleHelper.WISP, x, this.getY() + 0.06, z, 1, 0.015, 0.015, 0.015, 0.0);
         if (this.tickCount % 4 == 0) {
            serverLevel.sendParticles(ParticleHelper.WISP, x, this.getY() + 0.1, z, 1, 0.02, 0.03, 0.02, 0.0);
         }
      }

      serverLevel.sendParticles(ParticleHelper.WISP, this.getX(), this.getY() + 0.12, this.getZ(), 4, radius * 0.18, 0.03, radius * 0.18, 0.0);
      if (this.tickCount % 20 == 0) {
         MagicManager.spawnParticles(
            serverLevel,
            new BlastwaveParticleOptions(1.0F, 0.9F, 0.28F, radius * 0.96F),
            this.getX(),
            this.getY() + 0.03,
            this.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         MagicManager.spawnParticles(
            serverLevel, ParticleHelper.WISP, this.getX(), this.getY() + 0.1, this.getZ(), 12, radius * 0.12, 0.04, radius * 0.12, 0.03, true
         );
      }
   }

   private void applyBlessing() {
      float radius = this.getRadius();
      AABB area = this.getBoundingBox().inflate(radius, 2.0, radius);
      List<Player> players = this.level().getEntitiesOfClass(Player.class, area);
      double maxDistanceSqr = radius * radius;

      for (Player player : players) {
         if (player.isAlive() && !player.isSpectator() && !(player.distanceToSqr(this.getX(), player.getY(), this.getZ()) > maxDistanceSqr)) {
            player.addEffect(new MobEffectInstance(ModMobEffects.CUSTODIA_CAELI_BLESSING, this.effectDuration, this.effectAmplifier, false, true, true));
         }
      }
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      this.setRadius(tag.getFloat("Radius"));
      this.setDuration(tag.getInt("Duration"));
      this.effectDuration = tag.getInt("EffectDuration");
      this.effectAmplifier = tag.getInt("EffectAmplifier");
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putFloat("Radius", this.getRadius());
      tag.putInt("Duration", this.getDuration());
      tag.putInt("EffectDuration", this.effectDuration);
      tag.putInt("EffectAmplifier", this.effectAmplifier);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
