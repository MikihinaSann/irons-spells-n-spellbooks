package com.somake.somakespells.entity;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.ProcessionOfSoulsSpell;
import com.somake.somakespells.util.RedSoulHelper;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.HashMap;
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

public class ProcessionOfSoulsControllerEntity extends Entity {
   private static final int WARMUP_TICKS = 10;
   private static final int ACTIVATION_INTERVAL = 3;
   private static final int HIT_COOLDOWN_TICKS = 5;
   private static final float HIT_RADIUS = 1.35F;
   private static final float HIT_HEIGHT = 2.75F;
   private static final double START_DISTANCE = 1.75;
   private LivingEntity owner;
   private float damage;
   private int segmentCount;
   private float spacing;
   private double originX;
   private double originY;
   private double originZ;
   private double dirX;
   private double dirZ;
   private int nextSegmentIndex;
   private boolean redSoul;
   private final Map<Integer, Integer> recentHits = new HashMap<>();

   public ProcessionOfSoulsControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public ProcessionOfSoulsControllerEntity(EntityType<?> type, Level level, LivingEntity owner, float damage, int segmentCount, float spacing, boolean redSoul) {
      this(type, level);
      this.owner = owner;
      this.damage = damage;
      this.segmentCount = segmentCount;
      this.spacing = spacing;
      this.redSoul = redSoul;
      this.originX = owner.getX();
      this.originY = owner.getBoundingBox().minY;
      this.originZ = owner.getZ();
      Vec3 look = owner.getLookAngle();
      Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
      if (horizontal.lengthSqr() < 1.0E-4) {
         float yawRad = owner.getYRot() * (float) (Math.PI / 180.0);
         horizontal = new Vec3(-Math.sin(yawRad), 0.0, Math.cos(yawRad));
      }

      horizontal = horizontal.normalize();
      this.dirX = horizontal.x;
      this.dirZ = horizontal.z;
      this.setPos(this.originX, this.originY, this.originZ);
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

            while (this.nextSegmentIndex < this.segmentCount && this.tickCount >= this.getActivationTick(this.nextSegmentIndex)) {
               this.strikeSegment(serverLevel, this.nextSegmentIndex);
               this.nextSegmentIndex++;
            }

            if (this.tickCount > this.getActivationTick(this.segmentCount - 1) + 8) {
               this.discard();
            }
         } else {
            this.discard();
         }
      }
   }

   private void spawnTelegraphs(ServerLevel serverLevel) {
      if ((this.tickCount & 1) == 0) {
         for (int i = this.nextSegmentIndex; i < this.segmentCount; i++) {
            int activationTick = this.getActivationTick(i);
            int timeUntil = activationTick - this.tickCount;
            if (timeUntil > 0 && timeUntil <= 10) {
               Vec3 point = this.getSegmentPoint(i);
               float intensity = 0.35F + (10 - timeUntil) / 10.0F * 0.55F;
               serverLevel.sendParticles(
                  new BlastwaveParticleOptions(
                     this.redSoul ? 0.85F : 0.2F, this.redSoul ? 0.05F : 0.95F, this.redSoul ? 0.08F : 0.88F, 0.45F + intensity * 0.45F
                  ),
                  point.x,
                  point.y + 0.03,
                  point.z,
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0
               );
               this.spawnGroundTelegraphParticles(serverLevel, point, intensity);
            }
         }
      }
   }

   private void strikeSegment(ServerLevel serverLevel, int segmentIndex) {
      Vec3 point = this.getSegmentPoint(segmentIndex);
      float yaw = (float)Math.toDegrees(Math.atan2(this.dirZ, this.dirX));
      serverLevel.sendParticles(
         new BlastwaveParticleOptions(this.redSoul ? 0.95F : 0.28F, this.redSoul ? 0.08F : 0.98F, this.redSoul ? 0.1F : 0.92F, 1.0F),
         point.x,
         point.y + 0.04,
         point.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
      this.spawnSoulStrikeParticles(serverLevel, point, yaw);
      this.playStrikeSound(serverLevel, point, segmentIndex);
      this.damageEntitiesAt(point);
   }

   private void spawnGroundTelegraphParticles(ServerLevel serverLevel, Vec3 point, float intensity) {
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ground_soul"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, point.x, point.y + 2.0, point.z, 1 + (int)(intensity * 2.0F), 0.18, 0.02, 0.18, 0.0));
   }

   private void spawnSoulStrikeParticles(ServerLevel serverLevel, Vec3 point, float yaw) {
      BlockPos impactPos = BlockPos.containing(point.x, point.y - 0.2, point.z);
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "soul_strike"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, point.x, point.y + 2.0, point.z, 1, 0.0, 0.0, 0.0, 0.0));
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ghostly_soul"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, point.x, point.y + 1.15, point.z, 12, 0.22, 0.45, 0.22, 0.02));
      serverLevel.sendParticles(
         new BlockParticleOption(ParticleTypes.BLOCK, this.level().getBlockState(impactPos.below())),
         point.x,
         point.y + 0.03,
         point.z,
         24,
         0.42,
         0.04,
         0.42,
         0.03
      );
   }

   private void playStrikeSound(ServerLevel serverLevel, Vec3 point, int segmentIndex) {
      SoundEvent soundEvent = this.getLegendarySound("soul_fly")
         .or(() -> LegendaryMonstersCompat.getPossessedPaladinSwingSound())
         .orElse(SoundEvents.WITHER_SHOOT);
      float pitch = 0.85F + Math.min(segmentIndex, 6) * 0.035F;
      serverLevel.playSound(null, point.x, point.y, point.z, soundEvent, SoundSource.PLAYERS, 0.75F, pitch);
   }

   private void damageEntitiesAt(Vec3 point) {
      AABB hitBox = new AABB(point.x - 1.35F, point.y, point.z - 1.35F, point.x + 1.35F, point.y + 2.75, point.z + 1.35F);

      for (LivingEntity target : this.level()
         .getEntitiesOfClass(LivingEntity.class, hitBox, entity -> entity.isAlive() && entity != this.owner && !entity.isAlliedTo(this.owner))) {
         Integer lastHitTick = this.recentHits.get(target.getId());
         if ((lastHitTick == null || this.tickCount - lastHitTick >= 5)
            && DamageSources.applyDamage(target, this.damage, ((ProcessionOfSoulsSpell)ModSpells.PROCESSION_OF_SOULS.get()).getDamageSource(this, this.owner))) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.24, 0.0));
            target.hurtMarked = true;
            this.recentHits.put(target.getId(), this.tickCount);
         }
      }
   }

   private Vec3 getSegmentPoint(int segmentIndex) {
      double distance = 1.75 + segmentIndex * this.spacing;
      double x = this.originX + this.dirX * distance;
      double z = this.originZ + this.dirZ * distance;
      double y = this.findGroundY(x, z);
      return new Vec3(x, y, z);
   }

   private double findGroundY(double x, double z) {
      BlockPos center = BlockPos.containing(x, this.originY + 1.5, z);

      for (int offset = 4; offset >= -12; offset--) {
         BlockPos pos = center.offset(0, offset, 0);
         BlockState state = this.level().getBlockState(pos);
         if (!state.isAir()) {
            VoxelShape shape = state.getCollisionShape(this.level(), pos);
            if (!shape.isEmpty() && state.isFaceSturdy(this.level(), pos, Direction.UP)) {
               return pos.getY() + shape.max(Axis.Y) + 0.02;
            }
         }
      }

      return this.originY;
   }

   private int getActivationTick(int segmentIndex) {
      return 10 + segmentIndex * 3;
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
