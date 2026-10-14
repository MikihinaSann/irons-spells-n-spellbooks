package com.gametechbc.traveloptics.entity.spells.gyro_slash;

import com.gametechbc.traveloptics.init.TOEntities;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class GyroSlashVisual extends AoeEntity {
   private static final EntityDataAccessor<Boolean> DATA_MIRRORED = SynchedEntityData.defineId(GyroSlashVisual.class, EntityDataSerializers.BOOLEAN);
   LivingEntity target;
   public final int ticksPerFrame = 2;
   public final int deathTime = 8;

   public GyroSlashVisual(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public GyroSlashVisual(Level level, boolean mirrored) {
      this((EntityType<? extends Projectile>)TOEntities.GYRO_SLASH_VISUAL.get(), level);
      if (mirrored) {
         this.getEntityData().set(DATA_MIRRORED, true);
      }
   }

   public void applyEffect(LivingEntity target) {
   }

   public void tick() {
      if (!this.firstTick) {
         this.firstTick = true;
      }

      if (this.tickCount >= 8) {
         this.discard();
      }
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_MIRRORED, false);
   }

   public boolean isMirrored() {
      return (Boolean)this.getEntityData().get(DATA_MIRRORED);
   }

   public boolean shouldBeSaved() {
      return false;
   }

   public void refreshDimensions() {
   }

   public void ambientParticles() {
   }

   public float getParticleCount() {
      return 0.0F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
   }
}
