package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.SoulLatchSpell;
import com.somake.somakespells.util.RedSoulHelper;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulLatchControllerEntity extends Entity {
   private LivingEntity owner;
   private UUID ownerId;
   private LivingEntity target;
   private UUID targetId;
   private float burstDamage;
   private int latchDuration;
   private SoulLatchTetherEntity tether;
   private boolean redSoul;

   public SoulLatchControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SoulLatchControllerEntity(
      EntityType<?> type, Level level, LivingEntity owner, LivingEntity target, float burstDamage, int latchDuration, boolean redSoul
   ) {
      this(type, level);
      this.owner = owner;
      this.ownerId = owner.getUUID();
      this.target = target;
      this.targetId = target.getUUID();
      this.burstDamage = burstDamage;
      this.latchDuration = latchDuration;
      this.redSoul = redSoul;
      this.setPos(target.getX(), target.getY(), target.getZ());
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.burstDamage = compound.getFloat("BurstDamage");
      this.latchDuration = compound.getInt("LatchDuration");
      if (compound.hasUUID("Owner")) {
         this.ownerId = compound.getUUID("Owner");
      }

      if (compound.hasUUID("Target")) {
         this.targetId = compound.getUUID("Target");
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putFloat("BurstDamage", this.burstDamage);
      compound.putInt("LatchDuration", this.latchDuration);
      if (this.ownerId != null) {
         compound.putUUID("Owner", this.ownerId);
      }

      if (this.targetId != null) {
         compound.putUUID("Target", this.targetId);
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.resolveEntities();
         if (this.level() instanceof ServerLevel serverLevel && this.owner != null && this.target != null && this.owner.isAlive() && this.target.isAlive()) {
            this.setPos(this.target.getX(), this.target.getY(), this.target.getZ());
            this.holdTarget();
            this.ensureTether(serverLevel);
            this.spawnLatchVisuals(serverLevel);
            if (this.tickCount == 1) {
               this.playSound(this.getLegendarySound("pp_stab").orElse(SoundEvents.TRIDENT_HIT), 1.0F, 0.9F);
               serverLevel.sendParticles(
                  new RingParticleOptions(
                     0.0F, (float) (Math.PI / 2), 26, this.redSoul ? 210 : 86, this.redSoul ? 24 : 236, this.redSoul ? 30 : 204, 1.0F, 24.0F, false, 0
                  ),
                  this.target.getX(),
                  this.target.getY() + 1.0,
                  this.target.getZ(),
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0
               );
            }

            if (this.tickCount >= this.latchDuration) {
               this.release(serverLevel);
               this.discardTether();
               this.discard();
            }
         } else {
            this.discard();
         }
      }
   }

   private void resolveEntities() {
      if (this.owner == null
         && this.ownerId != null
         && this.level() instanceof ServerLevel serverLevel
         && serverLevel.getEntity(this.ownerId) instanceof LivingEntity livingEntity) {
         this.owner = livingEntity;
      }

      if (this.target == null
         && this.targetId != null
         && this.level() instanceof ServerLevel serverLevel
         && serverLevel.getEntity(this.targetId) instanceof LivingEntity livingEntity) {
         this.target = livingEntity;
      }
   }

   private void ensureTether(ServerLevel serverLevel) {
      if (this.tether == null || !this.tether.isAlive()) {
         this.tether = new SoulLatchTetherEntity(serverLevel, this.owner, this.target, this.redSoul);
         serverLevel.addFreshEntity(this.tether);
      }
   }

   private void discardTether() {
      if (this.tether != null && this.tether.isAlive()) {
         this.tether.discard();
      }

      this.tether = null;
   }

   private void holdTarget() {
      Vec3 lift = new Vec3(this.owner.getX() - this.target.getX(), 0.0, this.owner.getZ() - this.target.getZ()).scale(0.08);
      this.target.setDeltaMovement(lift.x, 0.02, lift.z);
      this.target.hurtMarked = true;
      this.target.fallDistance = 0.0F;
      this.target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 8, 4, false, false, true));
      this.target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 8, 1, false, false, true));
   }

   private void release(ServerLevel serverLevel) {
      this.getLegendarySound("soul_shield_smash").or(LegendaryMonstersCompat::getSoulFlySound).ifPresent(sound -> this.playSound(sound, 1.0F, 1.0F));
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 0.86F : 0.22F, this.redSoul ? 0.04F : 0.95F, this.redSoul ? 0.07F : 0.9F, 3.4F),
         this.target.getX(),
         this.target.getY() + 0.06,
         this.target.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 1.0F : 0.76F, this.redSoul ? 0.25F : 1.0F, this.redSoul ? 0.22F : 0.95F, 2.2F),
         this.target.getX(),
         this.target.getY() + 0.1,
         this.target.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      serverLevel.sendParticles(
         new RingParticleOptions(
            0.0F, (float) (Math.PI / 2), 24, this.redSoul ? 210 : 86, this.redSoul ? 24 : 236, this.redSoul ? 30 : 204, 1.0F, 28.0F, false, 2
         ),
         this.target.getX(),
         this.target.getY() + 0.25,
         this.target.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
      serverLevel.sendParticles(
         new RingParticleOptions(
            0.0F, (float) (Math.PI / 2), 18, this.redSoul ? 255 : 210, this.redSoul ? 70 : 255, this.redSoul ? 70 : 245, 0.95F, 18.0F, false, 0
         ),
         this.target.getX(),
         this.target.getY() + 0.45,
         this.target.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ghostly_soul")).ifPresent(particle -> {
         serverLevel.sendParticles(particle, this.target.getX(), this.target.getY() + 1.0, this.target.getZ(), 24, 0.65, 0.55, 0.65, 0.06);
         serverLevel.sendParticles(particle, this.target.getX(), this.target.getY() + 1.4, this.target.getZ(), 18, 0.35, 0.8, 0.35, 0.09);
      });
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "soul_strike"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, this.target.getX(), this.target.getY() + 2.0, this.target.getZ(), 1, 0.0, 0.0, 0.0, 0.0));
      ScreenShake_Entity.ScreenShake(serverLevel, this.target.position(), 15.0F, 0.2F, 0, 20);
      if (DamageSources.applyDamage(this.target, this.burstDamage, ((SoulLatchSpell)ModSpells.SOUL_LATCH.get()).getDamageSource(this, this.owner))) {
         this.owner.heal(4.0F);
         Vec3 push = this.target.position().subtract(this.owner.position());
         if (push.lengthSqr() > 1.0E-4) {
            push = push.normalize().scale(1.05);
            this.target.setDeltaMovement(push.x, 0.52, push.z);
         } else {
            this.target.setDeltaMovement(0.0, 0.52, 0.0);
         }

         this.target.hurtMarked = true;
      }
   }

   private void spawnLatchVisuals(ServerLevel serverLevel) {
      Vec3 center = this.target.position().add(0.0, this.target.getBbHeight() * 0.5, 0.0);
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ghostly_soul")).ifPresent(particle -> {
         serverLevel.sendParticles(particle, center.x, center.y, center.z, 6, 0.25, 0.5, 0.25, 0.01);
         this.spawnAttractionColumns(serverLevel, particle, center);
         this.spawnTetherParticles(serverLevel, particle);
      });
   }

   private void spawnAttractionColumns(ServerLevel serverLevel, ParticleOptions particle, Vec3 center) {
      for (int i = 0; i < 4; i++) {
         double angle = this.tickCount * 0.22 + (Math.PI / 2) * i;
         double radius = 0.9;
         double x = center.x + Math.cos(angle) * radius;
         double z = center.z + Math.sin(angle) * radius;
         double y = center.y + 0.15 + Math.sin(this.tickCount * 0.1 + i) * 0.12;
         serverLevel.sendParticles(particle, x, y, z, 1, center.x - x, 0.02, center.z - z, 0.04);
      }
   }

   private void spawnTetherParticles(ServerLevel serverLevel, ParticleOptions particle) {
      Vec3 start = this.owner.position().add(0.0, this.owner.getBbHeight() * 0.72, 0.0);
      Vec3 end = this.target.position().add(0.0, this.target.getBbHeight() * 0.58, 0.0);
      Vec3 delta = end.subtract(start);
      int segments = 10;

      for (int i = 1; i < segments; i++) {
         double progress = (double)i / segments;
         Vec3 point = start.add(delta.scale(progress));
         serverLevel.sendParticles(particle, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   private Optional<ParticleOptions> getLegendaryParticle(String path) {
      return BuiltInRegistries.PARTICLE_TYPE.getOptional(LegendaryMonstersCompat.id(path)).map(type -> (ParticleOptions)type);
   }

   private Optional<SoundEvent> getLegendarySound(String path) {
      return BuiltInRegistries.SOUND_EVENT.getOptional(LegendaryMonstersCompat.id(path));
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
