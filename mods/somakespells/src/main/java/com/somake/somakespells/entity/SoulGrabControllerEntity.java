package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.symmetry.SoulGrabSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulGrabControllerEntity extends Entity {
   private LivingEntity owner;
   private LivingEntity target;
   private float damage;
   private int phase = 0;
   private int phaseTicks = 0;

   public SoulGrabControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SoulGrabControllerEntity(EntityType<?> type, Level level, LivingEntity owner, LivingEntity target, float damage) {
      this(type, level);
      this.owner = owner;
      this.target = target;
      this.damage = damage;
      this.setPos(owner.getX(), owner.getY(), owner.getZ());
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         if (this.owner != null && this.owner.isAlive() && this.target != null && this.target.isAlive()) {
            this.phaseTicks++;
            if (this.phase == 0) {
               Vec3 dir = this.target.position().subtract(this.owner.position());
               double dist = dir.length();
               if (dist < 2.0 || this.phaseTicks > 20) {
                  this.phase = 1;
                  this.phaseTicks = 0;
                  this.owner.setDeltaMovement(Vec3.ZERO);
                  this.owner.hasImpulse = true;
                  return;
               }

               dir = dir.normalize().scale(3.0);
               this.owner.setDeltaMovement(dir);
               this.owner.hasImpulse = true;
               this.owner.hurtMarked = true;
               this.setPos(this.owner.position());
               if (this.level() instanceof ServerLevel serverLevel) {
                  for (int i = 0; i < 2; i++) {
                     double d0 = this.owner.getRandomX(1.0);
                     double d1 = this.owner.getRandomY();
                     double d2 = this.owner.getRandomZ(1.0);
                     serverLevel.sendParticles((ParticleOptions)ModParticle.PHANTOM_WING_FLAME.get(), d0, d1, d2, 1, 0.0, 0.0, 0.0, 0.0);
                  }
               }
            } else if (this.phase == 1) {
               if (this.phaseTicks > 10) {
                  this.phase = 2;
                  this.phaseTicks = 0;
                  return;
               }

               this.owner.setDeltaMovement(0.0, 0.8, 0.0);
               this.owner.hasImpulse = true;
               this.owner.hurtMarked = true;
               this.target.setDeltaMovement(0.0, 0.8, 0.0);
               this.target.hasImpulse = true;
               this.target.hurtMarked = true;
               Vec3 forward = this.owner.getLookAngle();
               this.target.setPos(this.owner.getX() + forward.x * 1.5, this.owner.getY(), this.owner.getZ() + forward.z * 1.5);
               this.setPos(this.owner.position());
               if (this.phaseTicks == 1 && this.level() instanceof ServerLevel serverLevel) {
                  this.playSound((SoundEvent)ModSounds.MALEDICTUS_SHORT_ROAR.get(), 1.0F, 1.0F);
               }
            } else if (this.phase == 2) {
               double smashSpeed = 3.0;
               Vec3 currentPos = this.owner.position();
               Vec3 velocity = new Vec3(0.0, -smashSpeed, 0.0);
               Vec3 predictedPos = currentPos.add(velocity);
               boolean impact = false;
               BlockPos below = BlockPos.containing(predictedPos);
               if (!this.level().getBlockState(below).getCollisionShape(this.level(), below).isEmpty()) {
                  impact = true;
                  this.owner.setPos(this.owner.getX(), below.getY() + 1.0, this.owner.getZ());
                  this.owner.setDeltaMovement(Vec3.ZERO);
               } else {
                  this.owner.setDeltaMovement(velocity);
                  this.owner.hasImpulse = true;
                  this.owner.resetFallDistance();
               }

               Vec3 forward = this.owner.getLookAngle();
               this.target.setPos(this.owner.getX() + forward.x * 1.5, this.owner.getY(), this.owner.getZ() + forward.z * 1.5);
               this.target.setDeltaMovement(this.owner.getDeltaMovement());
               this.target.hasImpulse = true;
               this.target.resetFallDistance();
               this.setPos(this.owner.position());
               if (this.level() instanceof ServerLevel serverLevel) {
                  for (int i = 0; i < 2; i++) {
                     double d0 = this.owner.getRandomX(1.0);
                     double d1 = this.owner.getRandomY();
                     double d2 = this.owner.getRandomZ(1.0);
                     serverLevel.sendParticles((ParticleOptions)ModParticle.PHANTOM_WING_FLAME.get(), d0, d1, d2, 1, 0.0, 0.0, 0.0, 0.0);
                  }
               }

               if (impact || this.phaseTicks > 5 && this.owner.onGround() || this.phaseTicks > 60) {
                  if (this.level() instanceof ServerLevel serverLevel) {
                     DamageSources.applyDamage(this.target, this.damage, ((SoulGrabSpell)ModSpells.SOUL_GRAB.get()).getDamageSource(this, this.owner));
                     ScreenShake_Entity.ScreenShake(serverLevel, this.position(), 15.0F, 0.2F, 0, 20);
                     serverLevel.sendParticles(
                        new RingParticleOptions(0.0F, (float) (Math.PI / 2), 20, 86, 236, 204, 1.0F, 20.0F, false, 2),
                        this.getX(),
                        this.getY() + 0.2,
                        this.getZ(),
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                     );

                     for (int i = 0; i < 30; i++) {
                        double dx = (this.random.nextDouble() - 0.5) * 2.0;
                        double dy = this.random.nextDouble() * 1.0;
                        double dz = (this.random.nextDouble() - 0.5) * 2.0;
                        serverLevel.sendParticles(
                           (ParticleOptions)ModParticle.PHANTOM_WING_FLAME.get(), this.getX(), this.getY() + 0.5, this.getZ(), 0, dx, dy, dz, 0.2
                        );
                     }

                     this.playSound((SoundEvent)ModSounds.MALEDICTUS_SHORT_ROAR.get(), 1.0F, 1.0F);
                  }

                  this.discard();
               }
            }
         } else {
            this.discard();
         }
      }
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
