package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.init.ModParticle;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.IgnisShieldSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

public class IgnisShieldEntity extends AbstractShieldEntity {
   private static final int RETALIATION_COOLDOWN_TICKS = 8;
   protected int width = 3;
   protected int height = 5;
   protected ShieldPart[] subEntities = new ShieldPart[this.width * this.height];
   protected final Vec3[] subPositions = new Vec3[this.width * this.height];
   protected final int LIFETIME;
   protected int age;
   @Nullable
   private UUID ownerUUID;
   private float retaliationDamage = 8.0F;
   private float retaliationKnockback = 1.3F;
   private int retaliationCooldown;

   public IgnisShieldEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setHealth(50.0F);
      this.LIFETIME = 600;
      this.createShield();
   }

   public IgnisShieldEntity(Level level, float health) {
      this((EntityType<?>)ModEntityTypes.IGNIS_SHIELD.get(), level);
      this.setHealth(health);
   }

   protected void createShield() {
      for (int x = 0; x < this.width; x++) {
         for (int y = 0; y < this.height; y++) {
            int i = x * this.height + y;
            this.subEntities[i] = new ShieldPart(this, "part" + (i + 1), 0.5F, 0.5F, true);
            this.subPositions[i] = new Vec3((x - this.width / 2.0F) * 0.5F + 0.25F, (y - this.height / 2.0F) * 0.5F, 0.0);
         }
      }
   }

   public void setRotation(float x, float y) {
      this.setXRot(x);
      this.xRotO = x;
      this.setYRot(y);
      this.yRotO = y;
   }

   public void setOwner(@Nullable LivingEntity owner) {
      this.ownerUUID = owner == null ? null : owner.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
         return serverLevel.getEntity(this.ownerUUID) instanceof LivingEntity livingEntity ? livingEntity : null;
      } else {
         return null;
      }
   }

   public void configureRetaliation(float retaliationDamage, float retaliationKnockback) {
      this.retaliationDamage = retaliationDamage;
      this.retaliationKnockback = retaliationKnockback;
   }

   public void takeDamage(DamageSource source, float amount, @Nullable Vec3 location) {
      if (!this.isInvulnerableTo(source)) {
         this.setHealth(this.getHealth() - amount);
         if (!this.level().isClientSide && location != null) {
            MagicManager.spawnParticles(this.level(), ParticleTypes.FLAME, location.x, location.y, location.z, 30, 0.1, 0.1, 0.1, 0.5, false);
            this.level().playSound(null, location.x, location.y, location.z, (SoundEvent)SoundRegistry.FORCE_IMPACT.get(), SoundSource.NEUTRAL, 0.8F, 1.0F);
            this.triggerRetaliation((ServerLevel)this.level(), location, source.getEntity());
         }
      }
   }

   public void tick() {
      this.hurtThisTick = false;
      if (this.retaliationCooldown > 0) {
         this.retaliationCooldown--;
      }

      if (this.getHealth() <= 0.0F) {
         this.destroy();
      } else if (++this.age > this.LIFETIME) {
         if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.NEUTRAL, 1.0F, 1.4F);
         }

         this.discard();
      } else {
         for (int i = 0; i < this.subEntities.length; i++) {
            ShieldPart subEntity = this.subEntities[i];
            Vec3 pos = this.subPositions[i]
               .xRot((float) (Math.PI / 180.0) * -this.getXRot())
               .yRot((float) (Math.PI / 180.0) * -this.getYRot())
               .add(this.position());
            subEntity.setPos(pos);
            subEntity.xo = pos.x;
            subEntity.yo = pos.y;
            subEntity.zo = pos.z;
            subEntity.xOld = pos.x;
            subEntity.yOld = pos.y;
            subEntity.zOld = pos.z;
         }
      }
   }

   public PartEntity<?>[] getParts() {
      return this.subEntities;
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if (compoundTag.hasUUID("OwnerUUID")) {
         this.ownerUUID = compoundTag.getUUID("OwnerUUID");
      }

      if (compoundTag.contains("RetaliationDamage")) {
         this.retaliationDamage = compoundTag.getFloat("RetaliationDamage");
      }

      if (compoundTag.contains("RetaliationKnockback")) {
         this.retaliationKnockback = compoundTag.getFloat("RetaliationKnockback");
      }

      this.retaliationCooldown = compoundTag.getInt("RetaliationCooldown");
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if (this.ownerUUID != null) {
         compoundTag.putUUID("OwnerUUID", this.ownerUUID);
      }

      compoundTag.putFloat("RetaliationDamage", this.retaliationDamage);
      compoundTag.putFloat("RetaliationKnockback", this.retaliationKnockback);
      compoundTag.putInt("RetaliationCooldown", this.retaliationCooldown);
   }

   protected void destroy() {
      if (!this.level().isClientSide) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 2.0F, 0.65F);
         MagicManager.spawnParticles(this.level(), ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 50, 2.0, 2.0, 2.0, 0.5, false);
      }

      super.destroy();
   }

   private void triggerRetaliation(ServerLevel serverLevel, Vec3 impactLocation, @Nullable Entity attacker) {
      if (this.retaliationCooldown <= 0) {
         if (attacker instanceof LivingEntity target) {
            this.retaliationCooldown = 8;
            LivingEntity owner = this.getOwner();
            if (target != owner && (owner == null || !owner.isAlliedTo(target))) {
               serverLevel.sendParticles(
                  (ParticleOptions)ModParticle.IGNIS_EXPLODE.get(), impactLocation.x, impactLocation.y + 0.02, impactLocation.z, 1, 0.0, 0.0, 0.0, 0.0
               );
               serverLevel.sendParticles(ParticleTypes.FLAME, impactLocation.x, impactLocation.y + 0.08, impactLocation.z, 8, 0.14, 0.08, 0.14, 0.03);
               serverLevel.playSound(
                  null,
                  impactLocation.x,
                  impactLocation.y,
                  impactLocation.z,
                  SoundEvents.GENERIC_EXPLODE,
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.15F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F
               );
               target.invulnerableTime = 0;
               DamageSources.applyDamage(
                  target,
                  this.retaliationDamage,
                  ((IgnisShieldSpell)ModSpells.IGNIS_SHIELD.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
               );
               Vec3 pushVector = target.getBoundingBox().getCenter().subtract(impactLocation).multiply(1.0, 0.0, 1.0);
               if (pushVector.lengthSqr() < 1.0E-4) {
                  pushVector = target.position().subtract(this.position()).multiply(1.0, 0.0, 1.0);
               }

               if (pushVector.lengthSqr() < 1.0E-4) {
                  double yawRadians = Math.toRadians(this.getYRot());
                  pushVector = new Vec3(-Math.sin(yawRadians), 0.0, Math.cos(yawRadians));
               }

               pushVector = pushVector.normalize().scale(this.retaliationKnockback);
               target.push(pushVector.x, 0.18, pushVector.z);
               target.hurtMarked = true;
               target.hasImpulse = true;
               target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), 40));
            }
         }
      }
   }
}
