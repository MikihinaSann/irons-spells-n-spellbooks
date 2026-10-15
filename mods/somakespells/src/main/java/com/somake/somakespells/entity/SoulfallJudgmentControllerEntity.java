package com.somake.somakespells.entity;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.SoulfallJudgmentSpell;
import com.somake.somakespells.util.RedSoulHelper;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.Optional;
import java.util.UUID;
import net.miauczel.legendary_monsters.Particle.custom.Circle.EnumRingBehavior;
import net.miauczel.legendary_monsters.Particle.custom.Circle.RingData;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.util.ParticleUtils;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulfallJudgmentControllerEntity extends Entity {
   private LivingEntity owner;
   private UUID ownerId;
   private float impactDamage;
   private float bladeDamage;
   private boolean redSoul;

   public SoulfallJudgmentControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SoulfallJudgmentControllerEntity(EntityType<?> type, Level level, LivingEntity owner, float impactDamage, float bladeDamage, boolean redSoul) {
      this(type, level);
      this.owner = owner;
      this.ownerId = owner.getUUID();
      this.impactDamage = impactDamage;
      this.bladeDamage = bladeDamage;
      this.redSoul = redSoul;
      this.setPos(owner.position());
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      if (tag.hasUUID("Owner")) {
         this.ownerId = tag.getUUID("Owner");
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      if (this.ownerId != null) {
         tag.putUUID("Owner", this.ownerId);
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.resolveOwner();
         if (this.level() instanceof ServerLevel serverLevel && this.owner != null && this.owner.isAlive() && this.owner.level() == this.level()) {
            this.setPos(this.owner.position());
            if (this.tickCount == 1) {
               this.performImpact(serverLevel);
            } else if (this.tickCount == 4) {
               this.spawnFallingBlades(serverLevel);
               this.discard();
            }
         } else {
            this.discard();
         }
      }
   }

   private void performImpact(ServerLevel serverLevel) {
      this.owner.setDeltaMovement(Vec3.ZERO);
      this.owner.hurtMarked = true;
      this.owner.resetFallDistance();
      serverLevel.playSound(null, this.owner.blockPosition(), SoundEvents.TOTEM_USE, this.owner.getSoundSource(), 1.0F, 0.75F);
      this.spawnImpactRing(serverLevel);
      this.spawnBladeWarnings(serverLevel);
      CameraShakeEntity.cameraShake(serverLevel, this.owner.position(), 20.0F, 0.25F, 0, 20);
      ParticleUtils.controlledSmashParticles(this.owner, 0.0F, 0.0F, 0.0F, 7.5F, 3.0F);
      AABB area = this.owner.getBoundingBox().inflate(3.0, 3.0, 3.0);

      for (LivingEntity target : serverLevel.getEntitiesOfClass(
         LivingEntity.class,
         area,
         targetx -> targetx.isAlive() && targetx != this.owner && !targetx.isAlliedTo(this.owner) && targetx.distanceTo(this.owner) <= 3.0F
      )) {
         if (DamageSources.applyDamage(target, this.impactDamage, ((SoulfallJudgmentSpell)ModSpells.SOULFALL_JUDGMENT.get()).getDamageSource(this, this.owner))
            )
          {
            double dx = target.getX() - this.owner.getX();
            double dz = target.getZ() - this.owner.getZ();
            double distanceSqr = Math.max(dx * dx + dz * dz, 1.0E-4);
            target.push(dx / distanceSqr * 2.0, 0.5, dz / distanceSqr * 2.0);
            target.hurtMarked = true;
         }
      }
   }

   private void spawnImpactRing(ServerLevel serverLevel) {
      Vec3 center = this.owner.position();
      serverLevel.sendParticles(
         new RingData(
            0.0F,
            (float) (Math.PI / 2),
            35,
            this.redSoul ? 1.0F : 0.25F,
            this.redSoul ? 0.0F : 1.0F,
            this.redSoul ? 0.0F : 0.75F,
            1.0F,
            60.0F,
            false,
            EnumRingBehavior.GROW
         ),
         center.x,
         this.owner.getY() + 0.2,
         center.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
      BlockState state = serverLevel.getBlockState(this.owner.getOnPos());
      if (state.getRenderShape() != RenderShape.INVISIBLE) {
         int count = 80 + this.random.nextInt(12);

         for (int i = 0; i < count; i++) {
            double angle = Math.toRadians(this.owner.yBodyRot) + i;
            double x = center.x + 5.0 * Math.sin(Math.PI + angle);
            double z = center.z + 5.0 * Math.cos(angle);
            serverLevel.sendParticles(
               new BlockParticleOption(ParticleTypes.BLOCK, state),
               x,
               this.owner.getY() + 0.3,
               z,
               0,
               this.random.nextGaussian() * 0.07,
               1.0 + this.random.nextGaussian() * 0.07,
               this.random.nextGaussian() * 0.07,
               1.0
            );
         }
      }
   }

   private void spawnBladeWarnings(ServerLevel serverLevel) {
      Optional<ParticleOptions> groundSoul = this.getLegendaryParticle(RedSoulHelper.particle(this.redSoul, "ground_soul"));
      this.forEachBladePoint(
         (point, rotation, delay) -> {
            groundSoul.ifPresent(particle -> serverLevel.sendParticles(particle, point.x, point.y + 2.0, point.z, 1, 0.0, 0.0, 0.0, 0.0));
            serverLevel.sendParticles(
               new RingData(
                  0.0F,
                  (float) (Math.PI / 2),
                  35,
                  this.redSoul ? 1.0F : 0.0F,
                  this.redSoul ? 0.0F : 0.9F,
                  this.redSoul ? 0.0F : 0.8F,
                  0.8F,
                  15.0F,
                  false,
                  EnumRingBehavior.SHRINK
               ),
               point.x,
               point.y + 0.25,
               point.z,
               1,
               0.0,
               0.0,
               0.0,
               0.0
            );
         }
      );
   }

   private void spawnFallingBlades(ServerLevel serverLevel) {
      this.forEachBladePoint(
         (point, rotation, delay) -> serverLevel.addFreshEntity(
            new SoulfallJudgmentBladeEntity(serverLevel, point.x, point.y, point.z, rotation, delay, this.owner, this.bladeDamage, this.redSoul)
         )
      );
   }

   private void forEachBladePoint(SoulfallJudgmentControllerEntity.BladePointConsumer consumer) {
      for (int i = 0; i < 6; i++) {
         float angle = i * (float) Math.PI / 3.0F;
         float perpX = -((float)Math.sin(angle));
         float perpZ = (float)Math.cos(angle);

         for (int k = 0; k < 8; k++) {
            double forward = 1.25 * (k + 1);
            double lateral = k % 2 == 0 ? 2.0 : -2.0;
            double x = this.owner.getX() + Math.cos(angle) * forward + perpX * lateral;
            double z = this.owner.getZ() + Math.sin(angle) * forward + perpZ * lateral;
            Optional<Vec3> point = this.findBladeGround(x, z);
            if (point.isPresent()) {
               consumer.accept(point.get(), angle, k + 1);
            }
         }
      }
   }

   private Optional<Vec3> findBladeGround(double x, double z) {
      BlockPos current = BlockPos.containing(x, this.owner.getY() + 2.0, z);
      int minimumY = (int)Math.floor(this.owner.getY()) - 1;

      do {
         BlockPos below = current.below();
         if (this.level().getBlockState(below).isFaceSturdy(this.level(), below, Direction.UP)) {
            double collisionHeight = 0.0;
            BlockState state = this.level().getBlockState(current);
            if (!state.isAir()) {
               VoxelShape shape = state.getCollisionShape(this.level(), current);
               if (!shape.isEmpty()) {
                  collisionHeight = shape.max(Axis.Y);
               }
            }

            return Optional.of(new Vec3(x, current.getY() + collisionHeight, z));
         }

         current = current.below();
      } while (current.getY() >= minimumY);

      return Optional.empty();
   }

   private void resolveOwner() {
      if (this.owner == null
         && this.ownerId != null
         && this.level() instanceof ServerLevel serverLevel
         && serverLevel.getEntity(this.ownerId) instanceof LivingEntity living) {
         this.owner = living;
      }
   }

   private Optional<ParticleOptions> getLegendaryParticle(String path) {
      return BuiltInRegistries.PARTICLE_TYPE.getOptional(LegendaryMonstersCompat.id(path)).map(type -> (ParticleOptions)type);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }

   @FunctionalInterface
   private interface BladePointConsumer {
      void accept(Vec3 var1, float var2, int var3);
   }
}
