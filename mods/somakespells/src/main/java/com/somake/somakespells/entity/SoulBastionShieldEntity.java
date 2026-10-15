package com.somake.somakespells.entity;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.SoulBastionSpell;
import com.somake.somakespells.util.RedSoulHelper;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SoulBastionShieldEntity extends Entity {
   private static final EntityDataAccessor<Float> DEST_X = SynchedEntityData.defineId(SoulBastionShieldEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DEST_Y = SynchedEntityData.defineId(SoulBastionShieldEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DEST_Z = SynchedEntityData.defineId(SoulBastionShieldEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(SoulBastionShieldEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> OUTWARD = SynchedEntityData.defineId(SoulBastionShieldEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> RED = SynchedEntityData.defineId(SoulBastionShieldEntity.class, EntityDataSerializers.BOOLEAN);
   private static final int MAX_LIFE = 55;
   private LivingEntity owner;
   private UUID ownerId;

   public SoulBastionShieldEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SoulBastionShieldEntity(Level level, LivingEntity owner, Vec3 spawn, Vec3 destination, float damage, boolean outward, boolean redSoul) {
      this((EntityType<?>)ModEntityTypes.SOUL_BASTION_SHIELD.get(), level);
      this.owner = owner;
      this.ownerId = owner.getUUID();
      this.setPos(spawn);
      this.entityData.set(DEST_X, (float)destination.x);
      this.entityData.set(DEST_Y, (float)destination.y);
      this.entityData.set(DEST_Z, (float)destination.z);
      this.entityData.set(DAMAGE, damage);
      this.entityData.set(OUTWARD, outward);
      this.entityData.set(RED, redSoul);
      this.updateRotation(destination.subtract(spawn));
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DEST_X, 0.0F);
      builder.define(DEST_Y, 0.0F);
      builder.define(DEST_Z, 0.0F);
      builder.define(DAMAGE, 0.0F);
      builder.define(OUTWARD, false);
      builder.define(RED, false);
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.hasUUID("Owner")) {
         this.ownerId = compound.getUUID("Owner");
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      if (this.ownerId != null) {
         compound.putUUID("Owner", this.ownerId);
      }
   }

   public void tick() {
      super.tick();
      if (this.owner == null
         && this.ownerId != null
         && this.level() instanceof ServerLevel serverLevel
         && serverLevel.getEntity(this.ownerId) instanceof LivingEntity livingEntity) {
         this.owner = livingEntity;
      }

      Vec3 destination = new Vec3(
         ((Float)this.entityData.get(DEST_X)).floatValue(),
         ((Float)this.entityData.get(DEST_Y)).floatValue(),
         ((Float)this.entityData.get(DEST_Z)).floatValue()
      );
      Vec3 toDest = destination.subtract(this.position());
      double distance = toDest.length();
      if (distance > 0.05) {
         double speed = 0.12 + distance * 0.05;
         Vec3 motion = toDest.normalize().scale(Math.min(speed, distance));
         this.setPos(this.position().add(motion));
         this.updateRotation(motion);
      }

      if (this.level() instanceof ServerLevel serverLevel) {
         this.spawnTrail(serverLevel);
         this.damageTargets();
      }

      if (this.tickCount > 55) {
         this.discard();
      }
   }

   private void spawnTrail(ServerLevel serverLevel) {
      if (this.tickCount % 5 == 0) {
         this.getLegendaryParticle(RedSoulHelper.particle(this.isRed(), "ground_soul"))
            .ifPresent(particle -> serverLevel.sendParticles(particle, this.getX(), this.getY() + 2.0, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0));
      }

      this.getLegendaryParticle(RedSoulHelper.particle(this.isRed(), "ghostly_soul"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, this.getX(), this.getY() + 0.9, this.getZ(), 4, 0.25, 0.45, 0.25, 0.02));
   }

   private void damageTargets() {
      if (this.tickCount % 5 == 0) {
         if (this.owner != null && this.owner.isAlive()) {
            AABB hitBox = this.getBoundingBox().inflate(0.25);

            for (LivingEntity target : this.level()
               .getEntitiesOfClass(LivingEntity.class, hitBox, entity -> entity.isAlive() && entity != this.owner && !entity.isAlliedTo(this.owner))) {
               if (DamageSources.applyDamage(
                  target, (Float)this.entityData.get(DAMAGE), ((SoulBastionSpell)ModSpells.SOUL_BASTION.get()).getDamageSource(this, this.owner)
               )) {
                  Vec3 push = new Vec3(target.getX() - this.getX(), 0.0, target.getZ() - this.getZ());
                  if (push.lengthSqr() > 1.0E-4) {
                     push = push.normalize().scale(this.entityData.get(OUTWARD) ? 0.55 : 0.35);
                     target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.08, push.z));
                  } else {
                     target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.08, 0.0));
                  }

                  target.hurtMarked = true;
               }
            }
         }
      }
   }

   private void updateRotation(Vec3 motion) {
      if (motion.lengthSqr() > 1.0E-4) {
         float yaw = (float)Math.toDegrees(Math.atan2(motion.z, motion.x)) - 90.0F;
         this.setYRot(yaw);
         this.yRotO = yaw;
      }
   }

   public boolean isOutward() {
      return (Boolean)this.entityData.get(OUTWARD);
   }

   public boolean isRed() {
      return (Boolean)this.entityData.get(RED);
   }

   public float getAnimationProgress(float partialTicks) {
      int remaining = 55 - this.tickCount - 2;
      return remaining <= 0 ? 1.0F : 1.0F - (remaining - partialTicks) / 20.0F;
   }

   private Optional<ParticleOptions> getLegendaryParticle(String path) {
      return BuiltInRegistries.PARTICLE_TYPE.getOptional(LegendaryMonstersCompat.id(path)).map(type -> (ParticleOptions)type);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
