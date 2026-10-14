package com.gametechbc.traveloptics.entity.spells;

import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.init.TOSounds;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ShockwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EnsnareEntity extends AoeEntity {
   private int tickCounter = 0;
   private static int rootDuration = 100;

   public EnsnareEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.setCircular();
   }

   public EnsnareEntity(Level level) {
      this((EntityType<? extends Projectile>)TOEntities.ENSNARE.get(), level);
   }

   public void setRootDuration(int duration) {
      rootDuration = duration;
   }

   public int getRootDuration() {
      return rootDuration;
   }

   public void tick() {
      super.tick();
      this.tickCounter++;
      if (!this.level().isClientSide) {
         if (this.tickCounter == 1) {
            this.rootChangingEffects();
         }

         if (this.tickCounter == 60) {
            this.rootEntities();
            this.rootTriggerEffects();
         }
      }
   }

   private void rootChangingEffects() {
      if (this.level() instanceof ServerLevel serverLevel) {
         MagicManager.spawnParticles(
            serverLevel,
            new ShockwaveParticleOptions(((SchoolType)SchoolRegistry.NATURE.get()).getTargetingColor(), -4.0F, true),
            this.getX(),
            this.getY(),
            this.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         this.playSound((SoundEvent)TOSounds.ENSNARE_PREPARE.get(), 1.0F, 1.0F);
      }
   }

   private void rootTriggerEffects() {
      if (this.level() instanceof ServerLevel serverLevel) {
         MagicManager.spawnParticles(
            serverLevel,
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.NATURE.get()).getTargetingColor(), this.getRadius()),
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
         CameraShakeManager.addCameraShake(new CameraShakeData(15, this.position(), this.getRadius() + 6.0F));
         this.playSound((SoundEvent)TOSounds.ENSNARE_TRIGGER.get(), 1.0F, 1.0F);
      }
   }

   private void rootEntities() {
      if (this.level() instanceof ServerLevel serverLevel) {
         LivingEntity owner = this.getOwner() instanceof LivingEntity ? (LivingEntity)this.getOwner() : null;

         for (LivingEntity target : serverLevel.getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox(),
            entity -> entity != null && !entity.getType().is(ModTags.CANT_ROOT) && !entity.equals(owner) && (owner == null || !this.isAlly(owner, entity))
         )) {
            RootEntity rootEntity = new RootEntity(serverLevel, target);
            rootEntity.setDuration(rootDuration);
            rootEntity.setTarget(target);
            rootEntity.moveTo(target.position());
            serverLevel.addFreshEntity(rootEntity);
            target.stopRiding();
            target.startRiding(rootEntity, true);
         }
      }
   }

   private boolean isAlly(LivingEntity owner, LivingEntity target) {
      return owner.getTeam() != null && owner.getTeam().isAlliedTo(target.getTeam());
   }

   public void applyEffect(LivingEntity target) {
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
      return Optional.of(ParticleHelper.ACID);
   }

   protected boolean canHitTargetForGroundContext(LivingEntity target) {
      return true;
   }

   protected Vec3 getInflation() {
      return new Vec3(0.0, 5.0, 0.0);
   }

   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 3.0F);
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("TickCounter", this.tickCounter);
      tag.putInt("RootDuration", rootDuration);
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("TickCounter")) {
         this.tickCounter = tag.getInt("TickCounter");
      }

      if (tag.contains("RootDuration")) {
         rootDuration = tag.getInt("RootDuration");
      }
   }
}
