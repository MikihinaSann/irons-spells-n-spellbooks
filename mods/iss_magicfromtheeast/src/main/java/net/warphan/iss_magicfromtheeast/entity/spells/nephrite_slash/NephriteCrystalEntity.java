package net.warphan.iss_magicfromtheeast.entity.spells.nephrite_slash;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import net.warphan.iss_magicfromtheeast.util.MFTEParticleHelper;

public class NephriteCrystalEntity extends AoeEntity {
   private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.defineId(NephriteCrystalEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_WAIT_TIME = SynchedEntityData.defineId(NephriteCrystalEntity.class, EntityDataSerializers.INT);
   public static final int RISE_TIME = 4;
   public static final int REST_TIME = 40;
   private static final int LOWER_TIME = 30;
   private final List<Entity> victims = new ArrayList<>();

   public NephriteCrystalEntity(EntityType<? extends AoeEntity> entityType, Level level) {
      super(entityType, level);
   }

   public NephriteCrystalEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends AoeEntity>)MFTEEntityRegistries.NEPHRITE_CRYSTAL.get(), level);
      this.setOwner(owner);
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_SIZE, 1.0F);
      pBuilder.define(DATA_WAIT_TIME, 8);
   }

   public float getCrystalSize() {
      return (Float)this.entityData.get(DATA_SIZE);
   }

   public void setCrystalSize(float f) {
      this.entityData.set(DATA_SIZE, f);
      this.refreshDimensions();
   }

   public int getWaitTime() {
      return (Integer)this.entityData.get(DATA_WAIT_TIME);
   }

   public void setWaitTime(int i) {
      this.entityData.set(DATA_WAIT_TIME, i);
   }

   public float getPositionOffset(float partialTick) {
      float f = this.tickCount + partialTick;
      int waitTime = this.getWaitTime();
      if (f < waitTime) {
         return -1.0F;
      } else if (f < waitTime + 4) {
         f = (f - waitTime) / 4.0F;
         return Mth.sin(f * (float) Math.PI) / (float) Math.PI + f - 1.0F;
      } else if (f < waitTime + 4 + 40) {
         return 0.0F;
      } else {
         f = Mth.clamp((f - (waitTime + 4 + 40)) / 30.0F, 0.0F, 1.0F) + 1.0F;
         return -(Mth.sin(f * (float) Math.PI) / (float) Math.PI + f - 1.0F);
      }
   }

   public void tick() {
      this.refreshDimensions();
      int waitTime = this.getWaitTime();
      if (this.tickCount == waitTime) {
         if (!this.level.isClientSide) {
            float f = this.getCrystalSize();
            if (!this.isSilent()) {
               this.level
                  .playSound(
                     null,
                     this.blockPosition(),
                     (SoundEvent)MFTESoundRegistries.JADE_EMERGE.get(),
                     SoundSource.NEUTRAL,
                     1.25F * this.getCrystalSize(),
                     Mth.randomBetweenInclusive(Utils.random, 6, 12) * 0.1F
                  );
            }

            MagicManager.spawnParticles(
               this.level,
               MFTEParticleHelper.JADE_SHATTER,
               this.getX(),
               this.level
                     .clip(new ClipContext(this.position().add(0.0, 2.0, 0.0), this.position(), Block.COLLIDER, Fluid.NONE, CollisionContext.empty()))
                     .getLocation()
                     .y()
                  + 0.1,
               this.getZ(),
               (int)(3.0F * f * f),
               0.1 * f,
               0.1 * f,
               0.1F * f,
               0.12 * f,
               false
            );
            MagicManager.spawnParticles(
               this.level,
               ParticleTypes.GLOW,
               this.getX(),
               this.level
                     .clip(new ClipContext(this.position().add(0.0, 2.0, 0.0), this.position(), Block.COLLIDER, Fluid.NONE, CollisionContext.empty()))
                     .getLocation()
                     .y()
                  + 0.1,
               this.getZ(),
               (int)(2.0F * f * f),
               0.1 * f,
               0.1 * f,
               0.1F * f,
               0.08 * f,
               false
            );
         }
      } else if (this.tickCount > waitTime && this.tickCount < waitTime + 4) {
         AABB damager = this.getBoundingBox();
         damager.setMaxY(this.getY() + damager.getYsize() * (this.getPositionOffset(0.0F) + 1.0F));

         for (Entity entity : this.level
            .getEntities(this, damager)
            .stream()
            .filter(target -> this.canHitEntity(target) && !this.victims.contains(target))
            .collect(Collectors.toSet())) {
            if (DamageSources.applyDamage(entity, this.damage, MFTESpellRegistries.NEPHRITE_SLASH_SPELL.get().getDamageSource(this, this.getOwner()))) {
               entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, this.getCrystalSize() * 0.3, 0.0));
               entity.hurtMarked = true;
            }

            this.victims.add(entity);
            if (entity instanceof ShieldPart || entity instanceof AbstractShieldEntity) {
               this.discard();
               return;
            }
         }
      } else if (this.tickCount > waitTime + 4 + 40 + 30) {
         this.discard();
      }
   }

   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("waitTime", this.getWaitTime());
   }

   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setWaitTime(pCompound.getInt("waitTime"));
   }

   public void applyEffect(LivingEntity target) {
   }

   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getCrystalSize() * 0.3F, this.getCrystalSize() * 0.7F * (this.getPositionOffset(1.0F) + 1.0F));
   }

   public boolean canBeCollidedWith() {
      return true;
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
