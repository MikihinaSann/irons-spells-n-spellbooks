package com.somake.somakespells.entity;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.SpiralOfRuinSpell;
import com.somake.somakespells.util.RedSoulHelper;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.miauczel.legendary_monsters.Particle.custom.Circle.EnumRingBehavior;
import net.miauczel.legendary_monsters.Particle.custom.Circle.RingData;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpiralOfRuinControllerEntity extends Entity {
   private static final int HIT_COOLDOWN_TICKS = 5;
   private static final int MAX_HITS_PER_TARGET = 3;
   private static final float HIT_RADIUS = 1.15F;
   private static final float HIT_HEIGHT = 2.8F;
   private LivingEntity owner;
   private float damage;
   private int nextExplosionIndex;
   private final List<Vec3> points = new ArrayList<>();
   private final List<Integer> activationTicks = new ArrayList<>();
   private final Map<Integer, Integer> recentHits = new HashMap<>();
   private final Map<Integer, Integer> hitCounts = new HashMap<>();
   private boolean redSoul;

   public SpiralOfRuinControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SpiralOfRuinControllerEntity(
      EntityType<?> type, Level level, LivingEntity owner, Vec3 center, Vec3 direction, float damage, int explosionCount, float maxRadius, boolean redSoul
   ) {
      this(type, level);
      this.owner = owner;
      this.damage = damage;
      this.redSoul = redSoul;
      this.setPos(center);
      this.buildOriginalSpiralPoints(center);
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
         if (this.level() instanceof ServerLevel serverLevel && this.owner != null && this.owner.isAlive() && !this.points.isEmpty()) {
            while (this.nextExplosionIndex < this.points.size() && this.tickCount >= this.getActivationTick(this.nextExplosionIndex)) {
               this.strikePoint(serverLevel, this.points.get(this.nextExplosionIndex), this.nextExplosionIndex);
               this.nextExplosionIndex++;
            }

            if (this.nextExplosionIndex >= this.points.size() && this.tickCount > this.getActivationTick(this.points.size() - 1) + 8) {
               this.discard();
            }
         } else {
            this.discard();
         }
      }
   }

   private void buildOriginalSpiralPoints(Vec3 center) {
      for (double t = 0.0; t < 18.0; t += 0.3) {
         double radius = t * 0.65;
         double x = center.x + radius * Math.cos(t);
         double z = center.z + radius * Math.sin(t);
         int activationTick = 4 + (int)t;
         this.findGroundPoint(x, center.y, z).ifPresent(point -> {
            this.points.add(point);
            this.activationTicks.add(activationTick);
         });
      }
   }

   private void strikePoint(ServerLevel serverLevel, Vec3 point, int index) {
      serverLevel.sendParticles(
         new RingData(
            0.0F,
            (float) (Math.PI / 2),
            15,
            this.redSoul ? 1.0F : 0.0F,
            this.redSoul ? 0.0F : 0.9F,
            this.redSoul ? 0.0F : 0.8F,
            0.8F,
            20.0F,
            false,
            EnumRingBehavior.GROW
         ),
         point.x,
         point.y + 0.2,
         point.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
      this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "soul_strike"))
         .ifPresent(particle -> serverLevel.sendParticles(particle, point.x, point.y + 2.0, point.z, 1, 0.0, 0.0, 0.0, 0.0));
      SoundEvent sound = this.getLegendarySound("soul_fly").orElse(SoundEvents.WITHER_SHOOT);
      serverLevel.playSound(null, point.x, point.y, point.z, sound, SoundSource.PLAYERS, 0.3F, 1.25F);
      this.damageEntitiesAt(point);
   }

   private void damageEntitiesAt(Vec3 point) {
      AABB hitBox = new AABB(point.x - 1.15F, point.y, point.z - 1.15F, point.x + 1.15F, point.y + 2.8F, point.z + 1.15F);

      for (LivingEntity target : this.level()
         .getEntitiesOfClass(LivingEntity.class, hitBox, targetx -> targetx.isAlive() && targetx != this.owner && !targetx.isAlliedTo(this.owner))) {
         int id = target.getId();
         int hitCount = this.hitCounts.getOrDefault(id, 0);
         Integer lastHit = this.recentHits.get(id);
         if (hitCount < 3
            && (lastHit == null || this.tickCount - lastHit >= 5)
            && DamageSources.applyDamage(target, this.damage, ((SpiralOfRuinSpell)ModSpells.SPIRAL_OF_RUIN.get()).getDamageSource(this, this.owner))) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.28, 0.0));
            target.hurtMarked = true;
            this.recentHits.put(id, this.tickCount);
            this.hitCounts.put(id, hitCount + 1);
         }
      }
   }

   private Optional<Vec3> findGroundPoint(double x, double baseY, double z) {
      BlockPos current = BlockPos.containing(x, baseY, z);
      int lowestY = (int)baseY - 1;

      do {
         BlockPos below = current.below();
         BlockState belowState = this.level().getBlockState(below);
         if (belowState.isFaceSturdy(this.level(), below, Direction.UP)) {
            double collisionHeight = 0.0;
            BlockState currentState = this.level().getBlockState(current);
            if (!currentState.isAir()) {
               VoxelShape shape = currentState.getCollisionShape(this.level(), current);
               if (!shape.isEmpty()) {
                  collisionHeight = shape.max(Axis.Y);
               }
            }

            return Optional.of(new Vec3(x, current.getY() + collisionHeight, z));
         }

         current = current.below();
      } while (current.getY() >= lowestY);

      return Optional.empty();
   }

   private int getActivationTick(int index) {
      return this.activationTicks.get(index);
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
