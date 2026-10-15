package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.GraveSigilSpell;
import com.somake.somakespells.util.RedSoulHelper;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GraveSigilControllerEntity extends Entity {
   private static final int FIRST_WAVE_TICK = 24;
   private static final int WAVE_INTERVAL = 16;
   private static final int TOTAL_WAVES = 4;
   private static final int END_TICK = 90;
   private static final float HIT_RADIUS = 1.2F;
   private static final float HIT_HEIGHT = 2.75F;
   private static final int HIT_COOLDOWN_TICKS = 4;
   private static final double CARDINAL_DISTANCE = 2.6;
   private static final double DIAGONAL_FORWARD = 1.9;
   private static final double DIAGONAL_RIGHT = 1.9;
   private LivingEntity owner;
   private float damage;
   private double centerX;
   private double centerY;
   private double centerZ;
   private double forwardX;
   private double forwardZ;
   private double rightX;
   private double rightZ;
   private final Map<Integer, Integer> recentHits = new HashMap<>();
   private boolean redSoul;

   public GraveSigilControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public GraveSigilControllerEntity(EntityType<?> type, Level level, LivingEntity owner, Vec3 center, Vec3 look, float damage, boolean redSoul) {
      this(type, level);
      this.owner = owner;
      this.damage = damage;
      this.redSoul = redSoul;
      Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
      if (horizontal.lengthSqr() < 1.0E-4) {
         float yawRad = owner.getYRot() * (float) (Math.PI / 180.0);
         horizontal = new Vec3(-Math.sin(yawRad), 0.0, Math.cos(yawRad));
      }

      horizontal = horizontal.normalize();
      this.forwardX = horizontal.x;
      this.forwardZ = horizontal.z;
      this.rightX = -horizontal.z;
      this.rightZ = horizontal.x;
      this.centerX = center.x;
      this.centerZ = center.z;
      this.centerY = this.findGroundY(center.x, center.y, center.z);
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
            this.spawnTelegraphs(serverLevel);

            for (int waveIndex = 0; waveIndex < 4; waveIndex++) {
               if (this.tickCount == this.getWaveTick(waveIndex)) {
                  boolean firstPattern = (waveIndex & 1) == 0;
                  this.strikeWave(serverLevel, firstPattern ? this.firstWavePoints() : this.secondWavePoints(), firstPattern);
               }
            }

            if (this.tickCount > 90) {
               this.discard();
            }
         } else {
            this.discard();
         }
      }
   }

   private void spawnTelegraphs(ServerLevel serverLevel) {
      if ((this.tickCount & 1) == 0) {
         this.spawnCenterSigil(serverLevel);
         List<Vec3> points = new ArrayList<>(this.firstWavePoints());
         if (this.tickCount < this.getWaveTick(3)) {
            points.addAll(this.secondWavePoints());
         }

         for (Vec3 point : points) {
            boolean center = point.equals(this.getCenterPoint());
            boolean diagonal = !center && !this.firstWavePoints().contains(point);
            this.spawnGroundSoul(serverLevel, point, center ? 3 : (diagonal ? 2 : 1), center, diagonal);
         }
      }
   }

   private void strikeWave(ServerLevel serverLevel, List<Vec3> points, boolean firstWave) {
      this.playSigilSound(serverLevel, this.getCenterPoint());
      this.spawnWaveBurst(serverLevel, this.getCenterPoint(), firstWave ? 4.4F : 5.2F);

      for (Vec3 point : points) {
         this.strikePoint(serverLevel, point);
      }
   }

   private void strikePoint(ServerLevel serverLevel, Vec3 point) {
      BlockPos impactPos = BlockPos.containing(point.x, point.y - 0.2, point.z);
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 0.88F : 0.18F, this.redSoul ? 0.04F : 0.92F, this.redSoul ? 0.07F : 0.88F, 0.95F),
         point.x,
         point.y + 0.05,
         point.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 1.0F : 0.74F, this.redSoul ? 0.28F : 1.0F, this.redSoul ? 0.25F : 0.95F, 0.55F),
         point.x,
         point.y + 0.08,
         point.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "soul_strike"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, point.x, point.y + 2.0, point.z, 1, 0.0, 0.0, 0.0, 0.0));
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ghostly_soul")).ifPresent(particle -> {
         serverLevel.sendParticles(particle, point.x, point.y + 1.1, point.z, 10, 0.2, 0.4, 0.2, 0.02);
         serverLevel.sendParticles(particle, point.x, point.y + 0.3, point.z, 12, 0.28, 0.18, 0.28, 0.03);
      });
      serverLevel.sendParticles(
         new BlockParticleOption(ParticleTypes.BLOCK, this.level().getBlockState(impactPos.below())),
         point.x,
         point.y + 0.03,
         point.z,
         20,
         0.35,
         0.04,
         0.35,
         0.03
      );
      this.damageEntitiesAt(point);
   }

   private void spawnGroundSoul(ServerLevel serverLevel, Vec3 point, int count, boolean center, boolean diagonal) {
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ground_soul"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, point.x, point.y + 2.0, point.z, count, 0.16, 0.02, 0.16, 0.0));
      float baseRadius = center ? 1.15F : (diagonal ? 0.72F : 0.88F);
      float pulse = 0.18F + this.tickCount % 8 * 0.03F;
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 0.72F : 0.14F, this.redSoul ? 0.03F : 0.78F, this.redSoul ? 0.05F : 0.75F, baseRadius + pulse),
         point.x,
         point.y + 0.03,
         point.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      if ((this.tickCount & 3) == 0) {
         this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ghostly_soul"))
            .ifPresent(particle -> serverLevel.sendParticles(particle, point.x, point.y + 0.35, point.z, center ? 6 : 3, 0.2, 0.1, 0.2, 0.01));
      }
   }

   private void spawnCenterSigil(ServerLevel serverLevel) {
      Vec3 center = this.getCenterPoint();
      float progress = Math.min(1.0F, (float)this.tickCount / this.getWaveTick(3));
      float outerRadius = 2.1F + progress * 2.4F;
      float innerRadius = 0.9F + progress * 0.9F;
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 0.68F : 0.1F, this.redSoul ? 0.02F : 0.68F, this.redSoul ? 0.04F : 0.7F, outerRadius),
         center.x,
         center.y + 0.04,
         center.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 1.0F : 0.62F, this.redSoul ? 0.2F : 0.98F, this.redSoul ? 0.18F : 0.9F, innerRadius),
         center.x,
         center.y + 0.08,
         center.z,
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
               20,
               this.redSoul ? 210 : 86,
               this.redSoul ? 24 : 236,
               this.redSoul ? 30 : 204,
               1.0F,
               16.0F + outerRadius * 2.5F,
               false,
               0
            ),
            center.x,
            center.y + 0.12,
            center.z,
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
      }
   }

   private void spawnWaveBurst(ServerLevel serverLevel, Vec3 center, float radius) {
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 0.85F : 0.22F, this.redSoul ? 0.04F : 0.95F, this.redSoul ? 0.07F : 0.88F, radius),
         center.x,
         center.y + 0.04,
         center.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(this.redSoul ? 1.0F : 0.74F, this.redSoul ? 0.25F : 1.0F, this.redSoul ? 0.22F : 0.95F, radius * 0.66F),
         center.x,
         center.y + 0.08,
         center.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ghostly_soul"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, center.x, center.y + 0.9, center.z, 18, 0.7, 0.25, 0.7, 0.03));
   }

   private void playSigilSound(ServerLevel serverLevel, Vec3 point) {
      SoundEvent soundEvent = this.getLegendarySound("soul_fly").orElse(SoundEvents.TOTEM_USE);
      serverLevel.playSound(null, point.x, point.y, point.z, soundEvent, SoundSource.PLAYERS, 0.85F, 0.9F);
   }

   private void damageEntitiesAt(Vec3 point) {
      AABB hitBox = new AABB(point.x - 1.2F, point.y, point.z - 1.2F, point.x + 1.2F, point.y + 2.75, point.z + 1.2F);

      for (LivingEntity target : this.level()
         .getEntitiesOfClass(LivingEntity.class, hitBox, entity -> entity.isAlive() && entity != this.owner && !entity.isAlliedTo(this.owner))) {
         Integer lastHitTick = this.recentHits.get(target.getId());
         if ((lastHitTick == null || this.tickCount - lastHitTick >= 4)
            && DamageSources.applyDamage(target, this.damage, ((GraveSigilSpell)ModSpells.GRAVE_SIGIL.get()).getDamageSource(this, this.owner))) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.22, 0.0));
            target.hurtMarked = true;
            this.recentHits.put(target.getId(), this.tickCount);
         }
      }
   }

   private List<Vec3> firstWavePoints() {
      List<Vec3> points = new ArrayList<>();
      points.add(this.getCenterPoint());
      points.add(this.offsetPoint(2.6, 0.0));
      points.add(this.offsetPoint(-2.6, 0.0));
      points.add(this.offsetPoint(0.0, 2.6));
      points.add(this.offsetPoint(0.0, -2.6));
      return points;
   }

   private List<Vec3> secondWavePoints() {
      List<Vec3> points = new ArrayList<>();
      points.add(this.offsetPoint(1.9, 1.9));
      points.add(this.offsetPoint(1.9, -1.9));
      points.add(this.offsetPoint(-1.9, 1.9));
      points.add(this.offsetPoint(-1.9, -1.9));
      return points;
   }

   private Vec3 getCenterPoint() {
      return new Vec3(this.centerX, this.centerY, this.centerZ);
   }

   private Vec3 offsetPoint(double forward, double right) {
      double x = this.centerX + this.forwardX * forward + this.rightX * right;
      double z = this.centerZ + this.forwardZ * forward + this.rightZ * right;
      double y = this.findGroundY(x, this.centerY, z);
      return new Vec3(x, y, z);
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

   private int getWaveTick(int waveIndex) {
      return 24 + waveIndex * 16;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
