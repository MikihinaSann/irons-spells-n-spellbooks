package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.somake.somakespells.network.ElementalFieryExplosionPacket;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.FireOrbsSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class SoulfireOrbEntity extends AbstractMagicProjectile {
   private static final int MODE_FALLING = 0;
   private static final int MODE_FRONT_ORBIT = 1;
   private static final int MODE_SKY_WAITING = 2;
   private static final double FRONT_DISTANCE = 2.35;
   private static final double FRONT_SPACING = 1.05;
   private static final double FRONT_HEIGHT_FACTOR = 0.72;
   private static final double FRONT_BOB_AMPLITUDE = 0.08;
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(SoulfireOrbEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(SoulfireOrbEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_ORB_MODE = SynchedEntityData.defineId(SoulfireOrbEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_FRONT_SLOT = SynchedEntityData.defineId(SoulfireOrbEntity.class, EntityDataSerializers.INT);
   private Vec3 skyAnchor = Vec3.ZERO;
   private int transferDelay = -1;
   private int releaseDelay = -1;

   public SoulfireOrbEntity(EntityType<? extends AbstractMagicProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public SoulfireOrbEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends AbstractMagicProjectile>)ModEntityTypes.SOULFIRE_ORB.get(), level);
      this.setOwner(owner);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_IS_SOUL_FIRE, false);
      builder.define(DATA_IS_INFERNAL_FIRE, false);
      builder.define(DATA_ORB_MODE, 0);
      builder.define(DATA_FRONT_SLOT, 0);
   }

   public void setIsSoulFire(boolean isSoulFire) {
      this.entityData.set(DATA_IS_SOUL_FIRE, isSoulFire);
   }

   public boolean isSoulFire() {
      return (Boolean)this.entityData.get(DATA_IS_SOUL_FIRE);
   }

   public void setIsInfernalFire(boolean isInfernalFire) {
      this.entityData.set(DATA_IS_INFERNAL_FIRE, isInfernalFire);
   }

   public boolean isInfernalFire() {
      return (Boolean)this.entityData.get(DATA_IS_INFERNAL_FIRE);
   }

   public void prepareFrontOrbit(int frontSlot) {
      this.setFrontSlot(frontSlot);
      this.setOrbMode(1);
      this.transferDelay = -1;
      this.releaseDelay = -1;
      Vec3 frontPosition = this.computeFrontPosition();
      this.setPos(frontPosition.x, frontPosition.y, frontPosition.z);
      this.setDeltaMovement(Vec3.ZERO);
   }

   public void scheduleTransferToSky(Vec3 targetPosition, int transferDelay, int releaseDelay) {
      this.skyAnchor = targetPosition;
      this.transferDelay = Math.max(transferDelay, 0);
      this.releaseDelay = Math.max(releaseDelay, 0);
      if (this.transferDelay == 0) {
         this.transferDelay = -1;
         this.moveToSkyAnchor();
      }
   }

   public void transferToSky(Vec3 targetPosition) {
      this.skyAnchor = targetPosition;
      this.transferDelay = -1;
      this.releaseDelay = -1;
      this.moveToSkyAnchor();
   }

   public void releaseAfter(int releaseDelay) {
      if (this.getOrbMode() == 2) {
         this.releaseDelay = Math.max(releaseDelay, 0);
         if (this.releaseDelay == 0) {
            this.releaseDelay = -1;
            this.releaseFromSky();
         }
      }
   }

   public void playSummonSound() {
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLAZE_SHOOT, this.getSoundSource(), 0.8F, 1.2F + this.random.nextFloat() * 0.15F);
   }

   public void playSummonEffects() {
      if (this.level() instanceof ServerLevel serverLevel) {
         this.spawnTransitionCircle(serverLevel, this.getX(), this.getY() + 0.08, this.getZ(), false);
         this.spawnTransitionBurst(serverLevel, this.getX(), this.getY() + 0.1, this.getZ(), false);
      }
   }

   public boolean isReleased() {
      return this.getOrbMode() == 0;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.updateSpellState();
      }
   }

   public void trailParticles() {
      if (this.level().isClientSide) {
         Vec3 vec3 = this.getDeltaMovement();
         ParticleOptions particle = this.getTrailParticle();
         this.level().addParticle(particle, this.getX(), this.getY() + 0.5, this.getZ(), -vec3.x * 0.5, -vec3.y * 0.5, -vec3.z * 0.5);
      }
   }

   public void impactParticles(double x, double y, double z) {
      if (this.level() instanceof ServerLevel serverLevel) {
         ParticleOptions particle = this.getTrailParticle();
         ParticleOptions burstParticle = this.getBurstParticle();
         serverLevel.sendParticles(particle, x, y, z, 80, 1.5, 1.5, 1.5, 0.2);
         serverLevel.sendParticles(burstParticle, x, y + 0.02, z, 2, 0.12, 0.12, 0.12, 0.0);
         MagicManager.spawnParticles(serverLevel, this.getBurstFlameParticle(), x, y + 0.06, z, 10, 0.28, 0.18, 0.28, 0.08, true);
         serverLevel.sendParticles(ParticleTypes.SMOKE, x, y + 0.06, z, 8, 0.3, 0.14, 0.3, 0.035);
      }
   }

   public float getSpeed() {
      return 1.5F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundEvents.GENERIC_EXPLODE);
   }

   protected void onHit(HitResult hitResult) {
      if (this.isReleased()) {
         Vec3 impactPos = hitResult.getLocation();
         super.onHit(hitResult);
         if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel)this.level();
            float explosionRadius = 3.5F;
            this.spawnEnhancedImpactEffects(serverLevel, impactPos, explosionRadius);
            this.level()
               .getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(explosionRadius))
               .forEach(
                  entity -> {
                     if (entity != this.getOwner() && entity.distanceToSqr(impactPos.x, impactPos.y, impactPos.z) < explosionRadius * explosionRadius) {
                        entity.invulnerableTime = 0;
                        Entity owner = this.getOwner();
                        DamageSources.applyDamage(
                           entity, this.getDamage(), ((FireOrbsSpell)ModSpells.FIRE_ORBS.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
                        );
                     }
                  }
               );
            this.discard();
         }
      }
   }

   private void spawnEnhancedImpactEffects(ServerLevel serverLevel, Vec3 impactPos, float explosionRadius) {
      float[] ringColor = this.getRingColor();
      PacketDistributor.sendToPlayersTrackingEntity(
         this,
         new ElementalFieryExplosionPacket(impactPos.subtract(this.getDeltaMovement().scale(0.35)), explosionRadius, this.getExplosionElementMode()),
         new CustomPacketPayload[0]
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(ringColor[0], ringColor[1], ringColor[2], explosionRadius + 0.6F),
         impactPos.x,
         impactPos.y + 0.05,
         impactPos.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      serverLevel.sendParticles(this.getImpactExplosionParticle(), impactPos.x, impactPos.y + 0.02, impactPos.z, 1, 0.0, 0.0, 0.0, 0.0);
      MagicManager.spawnParticles(serverLevel, this.getBurstFlameParticle(), impactPos.x, impactPos.y + 0.08, impactPos.z, 18, 0.45, 0.2, 0.45, 0.12, true);
      serverLevel.sendParticles(this.getLavaParticle(), impactPos.x, impactPos.y + 0.05, impactPos.z, 6, 0.35, 0.08, 0.35, 0.0);
      serverLevel.sendParticles(ParticleTypes.SMOKE, impactPos.x, impactPos.y + 0.1, impactPos.z, 12, 0.45, 0.15, 0.45, 0.045);
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setIsSoulFire(compound.getBoolean("IsSoulFire"));
      this.setIsInfernalFire(compound.getBoolean("IsInfernalFire"));
      this.skyAnchor = new Vec3(compound.getDouble("SkyAnchorX"), compound.getDouble("SkyAnchorY"), compound.getDouble("SkyAnchorZ"));
      this.transferDelay = compound.getInt("TransferDelay");
      this.releaseDelay = compound.getInt("ReleaseDelay");
      this.setFrontSlot(compound.getInt("FrontSlot"));
      this.setOrbMode(compound.getInt("OrbMode"));
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("IsSoulFire", this.isSoulFire());
      compound.putBoolean("IsInfernalFire", this.isInfernalFire());
      compound.putDouble("SkyAnchorX", this.skyAnchor.x);
      compound.putDouble("SkyAnchorY", this.skyAnchor.y);
      compound.putDouble("SkyAnchorZ", this.skyAnchor.z);
      compound.putInt("TransferDelay", this.transferDelay);
      compound.putInt("ReleaseDelay", this.releaseDelay);
      compound.putInt("FrontSlot", this.getFrontSlot());
      compound.putInt("OrbMode", this.getOrbMode());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }

   private void updateSpellState() {
      int mode = this.getOrbMode();
      if (mode == 1) {
         LivingEntity owner = this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null;
         if (owner != null && owner.isAlive()) {
            Vec3 frontPosition = this.computeFrontPosition();
            this.setPos(frontPosition.x, frontPosition.y, frontPosition.z);
            this.setDeltaMovement(Vec3.ZERO);
            if (this.transferDelay > 0) {
               this.transferDelay--;
            } else if (this.transferDelay == 0) {
               this.transferDelay = -1;
               this.moveToSkyAnchor();
            }
         } else {
            this.discard();
         }
      } else {
         if (mode == 2) {
            this.setPos(this.skyAnchor.x, this.skyAnchor.y, this.skyAnchor.z);
            this.setDeltaMovement(Vec3.ZERO);
            if (this.releaseDelay > 0) {
               this.releaseDelay--;
            } else if (this.releaseDelay == 0) {
               this.releaseDelay = -1;
               this.releaseFromSky();
            }
         }
      }
   }

   private void moveToSkyAnchor() {
      if (this.level() instanceof ServerLevel serverLevel) {
         this.spawnTransitionCircle(serverLevel, this.getX(), this.getY() + 0.08, this.getZ(), true);
         this.spawnTransitionBurst(serverLevel, this.getX(), this.getY() + 0.1, this.getZ(), true);
      }

      this.level()
         .playSound(
            null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 0.45F, 1.45F + this.random.nextFloat() * 0.15F
         );
      this.setOrbMode(2);
      this.setPos(this.skyAnchor.x, this.skyAnchor.y, this.skyAnchor.z);
      this.setDeltaMovement(Vec3.ZERO);
      this.level()
         .playSound(
            null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, this.getSoundSource(), 0.85F, 1.0F + this.random.nextFloat() * 0.2F
         );
      if (this.level() instanceof ServerLevel serverLevel) {
         this.spawnTransitionCircle(serverLevel, this.getX(), this.getY() + 0.08, this.getZ(), false);
         this.spawnTransitionBurst(serverLevel, this.getX(), this.getY() + 0.1, this.getZ(), false);
      }
   }

   private void releaseFromSky() {
      this.setOrbMode(0);
      this.setPos(this.skyAnchor.x, this.skyAnchor.y, this.skyAnchor.z);
      this.shoot(new Vec3(0.0, -1.0, 0.0));
   }

   private Vec3 computeFrontPosition() {
      LivingEntity owner = this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null;
      if (owner == null) {
         return this.position();
      } else {
         Vec3 look = owner.getLookAngle();
         Vec3 flatLook = new Vec3(look.x, 0.0, look.z);
         if (flatLook.lengthSqr() < 1.0E-4) {
            float radians = owner.getYRot() * (float) (Math.PI / 180.0);
            flatLook = new Vec3(-Math.sin(radians), 0.0, Math.cos(radians));
         }

         flatLook = flatLook.normalize();
         Vec3 side = new Vec3(-flatLook.z, 0.0, flatLook.x);
         double sideOffset = (this.getFrontSlot() - 2) * 1.05;
         double bobOffset = Math.sin((this.tickCount + this.getFrontSlot() * 4) * 0.25) * 0.08;
         return owner.position().add(0.0, owner.getBbHeight() * 0.72 + bobOffset, 0.0).add(flatLook.scale(2.35)).add(side.scale(sideOffset));
      }
   }

   private void setOrbMode(int mode) {
      this.entityData.set(DATA_ORB_MODE, mode);
      boolean released = mode == 0;
      this.setNoGravity(!released);
      this.noPhysics = !released;
   }

   private int getOrbMode() {
      return (Integer)this.entityData.get(DATA_ORB_MODE);
   }

   private void setFrontSlot(int frontSlot) {
      this.entityData.set(DATA_FRONT_SLOT, frontSlot);
   }

   private int getFrontSlot() {
      return (Integer)this.entityData.get(DATA_FRONT_SLOT);
   }

   private void spawnTransitionCircle(ServerLevel serverLevel, double x, double y, double z, boolean disappearing) {
      float[] ringColor = this.getRingColor();
      float scale = disappearing ? 7.0F : 9.0F;
      serverLevel.sendParticles(
         new RingParticleOptions(
            0.0F,
            (float) (Math.PI / 2),
            disappearing ? 14 : 16,
            (int)(ringColor[0] * 255.0F),
            (int)(ringColor[1] * 255.0F),
            (int)(ringColor[2] * 255.0F),
            0.9F,
            scale,
            false,
            0
         ),
         x,
         y,
         z,
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(ringColor[0], ringColor[1], ringColor[2], disappearing ? 0.65F : 0.85F),
         x,
         y,
         z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
   }

   private void spawnTransitionBurst(ServerLevel serverLevel, double x, double y, double z, boolean disappearing) {
      ParticleOptions flameParticle = this.getBurstFlameParticle();
      ParticleOptions burstParticle = this.getBurstParticle();
      ParticleOptions lavaParticle = this.getLavaParticle();
      double spread = disappearing ? 0.18 : 0.24;
      double speed = disappearing ? 0.035 : 0.06;
      MagicManager.spawnParticles(serverLevel, flameParticle, x, y, z, disappearing ? 10 : 16, spread, 0.12, spread, speed, true);
      MagicManager.spawnParticles(serverLevel, burstParticle, x, y + 0.02, z, 1, 0.0, 0.0, 0.0, 0.0, true);
      serverLevel.sendParticles(ParticleTypes.SMOKE, x, y + 0.04, z, disappearing ? 6 : 4, spread * 0.7, 0.08, spread * 0.7, disappearing ? 0.03 : 0.015);
      serverLevel.sendParticles(lavaParticle, x, y + 0.02, z, disappearing ? 2 : 4, spread * 0.55, 0.05, spread * 0.55, 0.0);
   }

   private float[] getRingColor() {
      if (this.isSoulFire()) {
         return new float[]{0.0F, 0.8F, 1.0F};
      } else {
         return this.isInfernalFire() ? new float[]{0.47F, 0.7F, 0.14F} : new float[]{1.0F, 0.4F, 0.0F};
      }
   }

   private ParticleOptions getTrailParticle() {
      if (this.isSoulFire()) {
         return (ParticleOptions)ModParticles.SOUL_FIRE.get();
      } else {
         return (ParticleOptions)(this.isInfernalFire()
            ? ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FIRE.get())
            : (ParticleOptions)ModParticles.FIRE.get());
      }
   }

   private ParticleOptions getBurstFlameParticle() {
      if (this.isSoulFire()) {
         return (ParticleOptions)ModParticles.SOUL_FLAME_ANIM_FIRE.get();
      } else {
         return (ParticleOptions)(this.isInfernalFire()
            ? ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FLAME_ANIM_FIRE.get())
            : (ParticleOptions)ModParticles.FLAME_ANIM_FIRE.get());
      }
   }

   private ParticleOptions getLavaParticle() {
      if (this.isSoulFire()) {
         return (ParticleOptions)ModParticles.SOUL_FIRE_LAVA.get();
      } else {
         return this.isInfernalFire() ? ModParticles.infernalFireLavaOr((SimpleParticleType)ModParticles.SOUL_FIRE_LAVA.get()) : ParticleTypes.LAVA;
      }
   }

   private ParticleOptions getBurstParticle() {
      if (this.isSoulFire()) {
         return (ParticleOptions)ModParticles.SOUL_FIRE_ORB_SMALL_EXPLODE.get();
      } else {
         return (ParticleOptions)(this.isInfernalFire()
            ? ModParticles.infernalFireOrbSmallExplodeOr((SimpleParticleType)ModParticles.FIRE_ORB_SMALL_EXPLODE.get())
            : (ParticleOptions)ModParticles.FIRE_ORB_SMALL_EXPLODE.get());
      }
   }

   private ParticleOptions getImpactExplosionParticle() {
      if (this.isSoulFire()) {
         return (ParticleOptions)ModParticle.IGNIS_SOUL_EXPLODE.get();
      } else {
         return (ParticleOptions)(this.isInfernalFire()
            ? ModParticles.infernalFireExplodeOr((SimpleParticleType)ModParticle.IGNIS_EXPLODE.get())
            : (ParticleOptions)ModParticle.IGNIS_EXPLODE.get());
      }
   }

   private int getExplosionElementMode() {
      if (this.isSoulFire()) {
         return 1;
      } else {
         return this.isInfernalFire() ? 2 : 0;
      }
   }
}
