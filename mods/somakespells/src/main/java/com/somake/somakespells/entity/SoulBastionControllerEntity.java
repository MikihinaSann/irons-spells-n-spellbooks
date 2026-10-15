package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.util.RedSoulHelper;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulBastionControllerEntity extends Entity {
   private static final int TOTAL_POINTS = 16;
   private static final int WAVES_TOTAL = 2;
   private static final int OUTER_WARNING_START = 6;
   private static final int OUTER_STRIKE_TICK = 28;
   private static final int INNER_WARNING_START = 36;
   private static final int INNER_STRIKE_TICK = 60;
   private static final int END_TICK = 86;
   private static final double OUTER_RADIUS = 12.0;
   private static final double INNER_RADIUS = 2.0;
   private LivingEntity owner;
   private float damage;
   private double centerX;
   private double centerY;
   private double centerZ;
   private boolean redSoul;

   public SoulBastionControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SoulBastionControllerEntity(EntityType<?> type, Level level, LivingEntity owner, float damage, boolean redSoul) {
      this(type, level);
      this.owner = owner;
      this.damage = damage;
      this.redSoul = redSoul;
      this.centerX = owner.getX();
      this.centerZ = owner.getZ();
      this.centerY = this.findGroundY(owner.getX(), owner.getY(), owner.getZ());
      this.setPos(this.centerX, this.centerY, this.centerZ);
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
         if (this.level() instanceof ServerLevel serverLevel && this.owner != null && this.owner.isAlive()) {
            this.spawnCenterPulse(serverLevel);
            if (this.tickCount >= 6 && this.tickCount < 28 && this.tickCount % 3 == 0) {
               this.spawnSoulShieldRing(serverLevel, 0, 12.0, false, true);
            }

            if (this.tickCount == 28) {
               this.spawnReleaseBurst(serverLevel, 12.0F, true);
               this.playShieldSmash(serverLevel, 1.0F);
               this.spawnSoulShieldRing(serverLevel, 0, 12.0, false, false);
            }

            if (this.tickCount >= 36 && this.tickCount < 60 && this.tickCount % 3 == 0) {
               this.spawnSoulShieldRing(serverLevel, 1, 2.0, true, true);
            }

            if (this.tickCount == 60) {
               this.spawnReleaseBurst(serverLevel, 4.2F, false);
               this.playShieldSmash(serverLevel, 0.92F);
               this.spawnSoulShieldRing(serverLevel, 1, 2.0, true, false);
            }

            if (this.tickCount > 86) {
               this.discard();
            }
         } else {
            this.discard();
         }
      }
   }

   private void spawnCenterPulse(ServerLevel serverLevel) {
      float progress = Math.min(1.0F, this.tickCount / 60.0F);
      float outerRadius = 1.6F + progress * 2.8F;
      float innerRadius = 0.8F + progress * 1.2F;
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 0.68F : 0.1F, this.redSoul ? 0.02F : 0.68F, this.redSoul ? 0.04F : 0.72F, outerRadius),
         this.centerX,
         this.centerY + 0.03,
         this.centerZ,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 1.0F : 0.72F, this.redSoul ? 0.22F : 0.98F, this.redSoul ? 0.18F : 0.92F, innerRadius),
         this.centerX,
         this.centerY + 0.08,
         this.centerZ,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      if ((this.tickCount & 3) == 0) {
         serverLevel.sendParticles(
            new RingParticleOptions(
               0.0F,
               (float) (Math.PI / 2),
               22,
               this.redSoul ? 210 : 86,
               this.redSoul ? 24 : 236,
               this.redSoul ? 30 : 204,
               1.0F,
               18.0F + outerRadius * 2.8F,
               false,
               0
            ),
            this.centerX,
            this.centerY + 0.12,
            this.centerZ,
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
         this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ghostly_soul"))
            .ifPresent(particle -> serverLevel.sendParticles(particle, this.centerX, this.centerY + 0.8, this.centerZ, 8, 0.45, 0.18, 0.45, 0.02));
      }
   }

   private void spawnReleaseBurst(ServerLevel serverLevel, float radius, boolean shrink) {
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 0.86F : 0.18F, this.redSoul ? 0.04F : 0.92F, this.redSoul ? 0.07F : 0.88F, radius),
         this.centerX,
         this.centerY + 0.04,
         this.centerZ,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 1.0F : 0.76F, this.redSoul ? 0.25F : 1.0F, this.redSoul ? 0.22F : 0.95F, radius * 0.7F),
         this.centerX,
         this.centerY + 0.08,
         this.centerZ,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      serverLevel.sendParticles(
         new RingParticleOptions(
            0.0F,
            (float) (Math.PI / 2),
            26,
            this.redSoul ? 210 : 86,
            this.redSoul ? 24 : 236,
            this.redSoul ? 30 : 204,
            1.0F,
            shrink ? 28.0F : 22.0F,
            false,
            shrink ? 1 : 2
         ),
         this.centerX,
         this.centerY + 0.16,
         this.centerZ,
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
   }

   private void spawnSoulShieldRing(ServerLevel serverLevel, int waveIndex, double radius, boolean outward, boolean warning) {
      double step = Math.PI / 8;
      int pointsThisWave = 8;
      double base = -0.41887902047863906;
      double waveOffset = waveIndex * step;

      for (int i = 0; i < pointsThisWave; i++) {
         double angle = base + waveOffset + i * (2.0 * step);
         double spawnX = this.centerX + Math.cos(angle) * radius;
         double spawnZ = this.centerZ + Math.sin(angle) * radius;
         double spawnY = this.findGroundY(spawnX, this.centerY, spawnZ);
         if (warning) {
            this.spawnWarning(serverLevel, spawnX, spawnY, spawnZ, outward);
         } else {
            Vec3 destination;
            if (outward) {
               Vec3 dir = new Vec3(spawnX - this.centerX, 0.0, spawnZ - this.centerZ).normalize();
               destination = new Vec3(spawnX + dir.x * 8.0, spawnY, spawnZ + dir.z * 8.0);
            } else {
               destination = new Vec3(this.centerX, this.centerY, this.centerZ);
            }

            SoulBastionShieldEntity shield = new SoulBastionShieldEntity(
               serverLevel, this.owner, new Vec3(spawnX, spawnY, spawnZ), destination, this.damage, outward, this.redSoul
            );
            serverLevel.addFreshEntity(shield);
         }
      }
   }

   private void spawnWarning(ServerLevel serverLevel, double x, double y, double z, boolean outward) {
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ground_soul"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, x, y + 2.0, z, outward ? 2 : 1, 0.18, 0.02, 0.18, 0.0));
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 0.76F : 0.16F, this.redSoul ? 0.03F : 0.8F, this.redSoul ? 0.05F : 0.78F, outward ? 0.78F : 0.94F),
         x,
         y + 0.03,
         z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      if ((this.tickCount & 3) == 0) {
         this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ghostly_soul"))
            .ifPresent(particle -> serverLevel.sendParticles(particle, x, y + 0.35, z, outward ? 4 : 3, 0.16, 0.08, 0.16, 0.01));
      }
   }

   private void playShieldSmash(ServerLevel serverLevel, float pitch) {
      SoundEvent soundEvent = this.getLegendarySound("soul_shield_smash").orElse(SoundEvents.SHIELD_BLOCK);
      serverLevel.playSound(null, this.centerX, this.centerY, this.centerZ, soundEvent, SoundSource.PLAYERS, 1.0F, pitch);
   }

   private double findGroundY(double x, double baseY, double z) {
      BlockPos center = BlockPos.containing(x, baseY + 1.5, z);

      for (int offset = 5; offset >= -16; offset--) {
         BlockPos pos = center.offset(0, offset, 0);
         BlockState state = this.level().getBlockState(pos);
         if (!state.isAir()) {
            VoxelShape shape = state.getCollisionShape(this.level(), pos);
            if (!shape.isEmpty() && state.isFaceSturdy(this.level(), pos, Direction.UP)) {
               return pos.getY() + shape.max(Axis.Y) + 0.02;
            }
         }
      }

      return baseY;
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
