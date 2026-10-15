package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.IncineratorSlashSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class IncineratorSlashEntity extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(IncineratorSlashEntity.class, EntityDataSerializers.FLOAT);
   private int maxAge = 20;
   private int age;
   private final Set<UUID> hitEntities = new HashSet<>();

   public IncineratorSlashEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public IncineratorSlashEntity(Level level, LivingEntity owner, float damage) {
      this((EntityType<? extends Projectile>)ModEntityTypes.INCINERATOR_SLASH.get(), level);
      this.setOwner(owner);
      this.setDamage(damage);
      Vec3 look = owner.getLookAngle();
      this.setPos(owner.position().add(0.0, owner.getEyeHeight() * 0.6, 0.0).add(look.scale(0.5)));
      this.setYRot(owner.getYRot());
      this.setXRot(owner.getXRot());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_DAMAGE, 10.0F);
   }

   public void setDamage(float damage) {
      this.entityData.set(DATA_DAMAGE, damage);
   }

   public float getDamage() {
      return (Float)this.entityData.get(DATA_DAMAGE);
   }

   public void tick() {
      super.tick();
      if (this.age++ >= this.maxAge) {
         this.discard();
      } else {
         Entity owner = this.getOwner();
         if (owner != null) {
            Vec3 look = Vec3.directionFromRotation(owner.getXRot(), owner.getYRot());
            this.setPos(owner.position().add(0.0, owner.getEyeHeight() * 0.6, 0.0).add(look.scale(1.5)));
            this.setYRot(owner.getYRot());
            this.setXRot(owner.getXRot());
         }

         if (!this.level().isClientSide) {
            Vec3 look = Vec3.directionFromRotation(this.getXRot(), this.getYRot());
            Vec3 center = this.position().add(look.scale(1.0));
            AABB area = this.getBoundingBox().inflate(2.0, 1.0, 2.0).move(look.scale(1.0));

            for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
               boolean isAlly = owner != null && target.isAlliedTo(owner);
               if (target != owner
                  && !isAlly
                  && !this.hitEntities.contains(target.getUUID())
                  && target.hurt(
                     ((IncineratorSlashSpell)ModSpells.INCINERATOR_SLASH.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)), this.getDamage()
                  )) {
                  this.hitEntities.add(target.getUUID());
                  target.invulnerableTime = 0;
               }
            }

            if (this.age % 2 == 0) {
               ((ServerLevel)this.level()).sendParticles(ParticleTypes.FLAME, center.x, center.y, center.z, 5, 1.0, 0.5, 1.0, 0.1);
            }
         }
      }
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.discard();
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.age = compound.getInt("Age");
      this.setDamage(compound.getFloat("Damage"));
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("Age", this.age);
      compound.putFloat("Damage", this.getDamage());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
