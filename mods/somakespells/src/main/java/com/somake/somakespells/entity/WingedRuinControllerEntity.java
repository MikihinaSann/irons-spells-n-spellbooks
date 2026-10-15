package com.somake.somakespells.entity;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.WingedRuinSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.miauczel.legendary_monsters.Particle.custom.Circle.EnumRingBehavior;
import net.miauczel.legendary_monsters.Particle.custom.Circle.RingData;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.util.ParticleUtils;
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
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WingedRuinControllerEntity extends Entity {
   public static final int TOTAL_DURATION = 72;
   private static final int ASCENT_END = 12;
   private static final int HOVER_END = 28;
   private LivingEntity owner;
   private UUID ownerId;
   private final Set<UUID> impactHits = new HashSet<>();
   private Vec3 destination = Vec3.ZERO;
   private Vec3 ascentTarget = Vec3.ZERO;
   private float impactDamage;
   private float ruptureDamage;
   private float impactRadius;
   private boolean impacted;

   public WingedRuinControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public WingedRuinControllerEntity(Level level, LivingEntity owner, Vec3 destination, float impactDamage, float ruptureDamage, float impactRadius) {
      this((EntityType<?>)ModEntityTypes.WINGED_RUIN_CONTROLLER.get(), level);
      this.owner = owner;
      this.ownerId = owner.getUUID();
      this.destination = destination;
      this.ascentTarget = owner.position().lerp(destination, 0.25).add(0.0, 6.5, 0.0);
      this.impactDamage = impactDamage;
      this.ruptureDamage = ruptureDamage;
      this.impactRadius = impactRadius;
      this.setPos(owner.position());
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.resolveOwner();
         if (this.level() instanceof ServerLevel level
            && this.owner != null
            && this.owner.isAlive()
            && this.owner.level() == this.level()
            && !this.owner.isPassenger()
            && !this.owner.isFallFlying()) {
            this.setPos(this.owner.position());
            this.owner.resetFallDistance();
            if (this.tickCount == 1) {
               this.owner.addEffect(new MobEffectInstance(ModMobEffects.WINGED_RUIN, 73, 0, false, false, false));
            }

            if (this.tickCount <= 12) {
               this.moveToward(this.ascentTarget, 0.22, 0.72);
               this.attractSouls(level);
            } else if (this.tickCount <= 28) {
               this.hover();
               this.attractSouls(level);
               if (this.tickCount == 17) {
                  this.playSound(level, "ominous_windup", SoundEvents.WITHER_SPAWN, 1.0F, 1.0F);
               }
            } else if (!this.impacted) {
               this.dive(level);
            } else if (this.tickCount > 72) {
               this.cleanup();
            }
         } else {
            this.cleanup();
         }
      }
   }

   private void moveToward(Vec3 target, double strength, double verticalCap) {
      Vec3 velocity = target.subtract(this.owner.position()).scale(strength);
      velocity = new Vec3(Mth.clamp(velocity.x, -0.65, 0.65), Mth.clamp(velocity.y, -0.15, verticalCap), Mth.clamp(velocity.z, -0.65, 0.65));
      this.applyVelocity(velocity);
   }

   private void hover() {
      Vec3 velocity = this.owner.getDeltaMovement().scale(0.35).add(this.ascentTarget.subtract(this.owner.position()).scale(0.18));
      this.applyVelocity(new Vec3(Mth.clamp(velocity.x, -0.35, 0.35), Mth.clamp(velocity.y, -0.3, 0.3), Mth.clamp(velocity.z, -0.35, 0.35)));
   }

   private void dive(ServerLevel level) {
      Vec3 toTarget = this.destination.subtract(this.owner.position());
      double remaining = toTarget.length();
      if (!(remaining < 0.9) && (!this.owner.onGround() || this.tickCount <= 30)) {
         double speed = Mth.clamp(remaining * 0.24, 0.65, 1.8);
         Vec3 velocity = toTarget.normalize().scale(speed);
         if (!this.level().noCollision(this.owner, this.owner.getBoundingBox().move(velocity))) {
            this.performImpact(level);
         } else {
            this.applyVelocity(velocity);
            this.particle("ghostly_soul_red")
               .ifPresent(particle -> level.sendParticles(particle, this.owner.getX(), this.owner.getY() + 1.0, this.owner.getZ(), 10, 0.35, 0.45, 0.35, 0.02));
         }
      } else {
         this.performImpact(level);
      }
   }

   private void performImpact(ServerLevel level) {
      if (!this.impacted) {
         this.impacted = true;
         this.owner.setDeltaMovement(Vec3.ZERO);
         this.owner.hurtMarked = true;
         this.owner.resetFallDistance();
         this.destination = this.owner.position();
         CameraShakeEntity.cameraShake(level, this.destination, 10.0F, 0.15F, 10, 10);
         this.playSound(level, "stab_hit", (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 1.0F);
         ParticleUtils.controlledSmashParticles(this.owner, 1.0F, 0.0F, 0.0F, 7.5F, 3.0F);
         level.sendParticles(
            new RingData(0.0F, (float) (Math.PI / 2), 30, 1.0F, 0.0F, 0.0F, 0.8F, 100.0F, false, EnumRingBehavior.GROW),
            this.destination.x,
            this.destination.y + 0.2,
            this.destination.z,
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
         this.particle("soul_explosion_red")
            .ifPresent(particle -> level.sendParticles(particle, this.destination.x, this.destination.y + 1.0, this.destination.z, 12, 1.5, 0.75, 1.5, 0.02));
         this.damageImpact(level);
         this.spawnCardinalStrikes(level);
      }
   }

   private void damageImpact(ServerLevel level) {
      AABB area = new AABB(
         this.destination.x - this.impactRadius,
         this.destination.y - 1.0,
         this.destination.z - this.impactRadius,
         this.destination.x + this.impactRadius,
         this.destination.y + 3.0,
         this.destination.z + this.impactRadius
      );

      for (LivingEntity target : level.getEntitiesOfClass(
         LivingEntity.class, area, targetx -> targetx.isAlive() && targetx != this.owner && !targetx.isAlliedTo(this.owner)
      )) {
         if (!(target.position().distanceToSqr(this.destination) > this.impactRadius * this.impactRadius)
            && this.impactHits.add(target.getUUID())
            && DamageSources.applyDamage(target, this.impactDamage, ((WingedRuinSpell)ModSpells.WINGED_RUIN.get()).getDamageSource(this, this.owner))) {
            Vec3 away = target.position().subtract(this.destination).multiply(1.0, 0.0, 1.0);
            if (away.lengthSqr() > 1.0E-4) {
               away = away.normalize();
            }

            target.setDeltaMovement(target.getDeltaMovement().add(away.scale(0.55)).add(0.0, 0.25, 0.0));
            target.hurtMarked = true;
         }
      }
   }

   private void spawnCardinalStrikes(ServerLevel level) {
      Vec3 forward = this.horizontalLook();
      Vec3 right = new Vec3(-forward.z, 0.0, forward.x);

      for (Vec3 direction : new Vec3[]{forward, forward.scale(-1.0), right, right.scale(-1.0)}) {
         Vec3 center = this.destination.add(direction.scale(4.0));
         this.spawnOriginalSquare(level, center, (float)Math.atan2(direction.z, direction.x));
      }
   }

   private void spawnOriginalSquare(ServerLevel level, Vec3 center, float yaw) {
      double div = 5.0 / Math.sqrt(2.0);
      Vec3[] corners = new Vec3[]{center.add(div, 0.0, div), center.add(-div, 0.0, div), center.add(-div, 0.0, -div), center.add(div, 0.0, -div)};
      int[][] edges = new int[][]{{0, 1}, {1, 2}, {0, 3}, {3, 2}};

      for (double distance : new double[]{1.25, 3.75, 6.25}) {
         for (int[] edge : edges) {
            Vec3 start = corners[edge[0]];
            Vec3 direction = corners[edge[1]].subtract(start).normalize();
            this.spawnExplosion(level, start.add(direction.scale(distance)), yaw);
         }
      }

      this.spawnExplosion(level, center, yaw);
   }

   private void spawnExplosion(ServerLevel level, Vec3 raw, float yaw) {
      this.findGround(raw.x, raw.z)
         .ifPresent(
            point -> level.addFreshEntity(new WingedRuinSoulPillarEntity(level, point.x, point.y, point.z, yaw, 10, this.owner, 20, this.ruptureDamage, true))
         );
   }

   private Optional<Vec3> findGround(double x, double z) {
      BlockPos current = BlockPos.containing(x, this.destination.y + 2.0, z);
      int minimum = (int)Math.floor(this.destination.y) - 2;

      do {
         BlockPos below = current.below();
         BlockState belowState = this.level().getBlockState(below);
         if (belowState.isFaceSturdy(this.level(), below, Direction.UP)) {
            double height = 0.0;
            BlockState state = this.level().getBlockState(current);
            if (!state.isAir()) {
               VoxelShape shape = state.getCollisionShape(this.level(), current);
               if (!shape.isEmpty()) {
                  height = shape.max(Axis.Y);
               }
            }

            return Optional.of(new Vec3(x, current.getY() + height, z));
         }

         current = current.below();
      } while (current.getY() >= minimum);

      return Optional.empty();
   }

   private void attractSouls(ServerLevel level) {
      Optional<ParticleOptions> soul = this.particle("ghostly_soul_red");
      if (!soul.isEmpty()) {
         for (int i = 0; i < 15; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double radius = 2.0 + this.random.nextDouble() * 3.0;
            Vec3 origin = this.owner.position().add(Math.cos(angle) * radius, this.random.nextDouble() * 5.0, Math.sin(angle) * radius);
            Vec3 velocity = this.owner.position().add(0.0, 3.5, 0.0).subtract(origin).scale(0.075);
            level.sendParticles(soul.get(), origin.x, origin.y, origin.z, 1, velocity.x, velocity.y, velocity.z, 0.0);
         }
      }
   }

   private void applyVelocity(Vec3 velocity) {
      this.owner.setDeltaMovement(velocity);
      this.owner.hasImpulse = true;
      this.owner.hurtMarked = true;
   }

   private Vec3 horizontalLook() {
      Vec3 look = this.owner.getLookAngle();
      Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
      return horizontal.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 0.0, 1.0) : horizontal.normalize();
   }

   private void resolveOwner() {
      if (this.owner == null
         && this.ownerId != null
         && this.level() instanceof ServerLevel level
         && level.getEntity(this.ownerId) instanceof LivingEntity living) {
         this.owner = living;
      }
   }

   private void cleanup() {
      if (this.owner != null) {
         this.owner.removeEffect(ModMobEffects.WINGED_RUIN);
         this.owner.resetFallDistance();
      }

      this.discard();
   }

   private Optional<ParticleOptions> particle(String path) {
      return BuiltInRegistries.PARTICLE_TYPE.getOptional(LegendaryMonstersCompat.id(path)).map(type -> (ParticleOptions)type);
   }

   private void playSound(ServerLevel level, String path, SoundEvent fallback, float volume, float pitch) {
      level.playSound(
         null,
         this.owner.blockPosition(),
         BuiltInRegistries.SOUND_EVENT.getOptional(LegendaryMonstersCompat.id(path)).orElse(fallback),
         this.owner.getSoundSource(),
         volume,
         pitch
      );
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
