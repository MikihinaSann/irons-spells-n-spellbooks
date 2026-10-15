package com.somake.somakespells.entity;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.SpectralRondoSpell;
import com.somake.somakespells.util.RedSoulHelper;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.miauczel.legendary_monsters.Particle.custom.Circle.EnumRingBehavior;
import net.miauczel.legendary_monsters.Particle.custom.Circle.RingData;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpectralRondoControllerEntity extends Entity {
   public static final int DURATION = 21;
   private LivingEntity owner;
   private UUID ownerId;
   private float damage;
   private Vec3 forward = Vec3.ZERO;
   private Vec3 lateral = Vec3.ZERO;
   private final Set<UUID> firstHit = new HashSet<>();
   private final Set<UUID> secondHit = new HashSet<>();
   private boolean redSoul;

   public SpectralRondoControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SpectralRondoControllerEntity(Level level, LivingEntity owner, float damage, boolean redSoul) {
      this((EntityType<?>)ModEntityTypes.SPECTRAL_RONDO_CONTROLLER.get(), level);
      this.owner = owner;
      this.ownerId = owner.getUUID();
      this.damage = damage;
      this.redSoul = redSoul;
      Vec3 look = owner.getLookAngle();
      Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
      this.forward = horizontal.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 0.0, 1.0) : horizontal.normalize();
      this.lateral = new Vec3(-this.forward.z, 0.0, this.forward.x);
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
         if (this.level() instanceof ServerLevel serverLevel
            && this.owner != null
            && this.owner.isAlive()
            && this.owner.level() == this.level()
            && !this.owner.isPassenger()
            && !this.owner.isFallFlying()) {
            this.setPos(this.owner.position());
            this.owner.resetFallDistance();
            if (this.tickCount == 1) {
               this.owner.addEffect(new MobEffectInstance(ModMobEffects.SPECTRAL_RONDO, 22, 0, false, false, false));
               this.spawnMainSweep(serverLevel);
               this.playSound(serverLevel, "pp_swing", SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.75F);
               this.spawnAuraBurst(serverLevel, 60);
            }

            if (this.tickCount >= 1 && this.tickCount < 17) {
               this.advanceSpin();
               this.spawnSpinHelix(serverLevel);
               if ((this.tickCount - 1) % 3 == 0) {
                  this.spawnPillarRow(serverLevel);
               }
            }

            if (this.tickCount == 4) {
               this.performHit(serverLevel, this.firstHit, 0.32, 0.12);
               this.spawnImpactVfx(serverLevel, false);
            }

            if (this.tickCount == 17) {
               this.spawnMainSweep(serverLevel);
               this.playSound(serverLevel, "pp_swing", SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.75F);
               this.performHit(serverLevel, this.secondHit, 0.48, 0.2);
               this.spawnImpactVfx(serverLevel, true);
            }

            if (this.tickCount > 21) {
               this.cleanup();
            }
         } else {
            this.cleanup();
         }
      }
   }

   private void advanceSpin() {
      Vec3 current = this.owner.getDeltaMovement();
      Vec3 next = this.forward.scale(0.6).add(new Vec3(current.x, 0.0, current.z).scale(0.6));
      AABB nextBox = this.owner.getBoundingBox().move(next.x, 0.0, next.z);
      if (this.level().noCollision(this.owner, nextBox)) {
         this.owner.setDeltaMovement(next.x, current.y, next.z);
         this.owner.hasImpulse = true;
         this.owner.hurtMarked = true;
      }
   }

   private void spawnMainSweep(ServerLevel level) {
      float yaw = (float)Math.toRadians(-this.owner.yBodyRot + 20.0F);
      float pitch = (float)(-Math.atan2(3.0, 1.0));
      LegendaryMonstersCompat.createSoulSweepParticle(2.0F, yaw, pitch, this.redSoul)
         .ifPresent(
            particle -> level.sendParticles(
               particle, this.owner.getX(), this.owner.getY() + this.owner.getBbHeight() / 2.0 + 1.0, this.owner.getZ(), 1, 0.0, 0.0, 0.0, 0.0
            )
         );
      LegendaryMonstersCompat.createSoulSweepParticle(1.25F, yaw + (float) Math.PI, pitch, this.redSoul)
         .ifPresent(
            particle -> level.sendParticles(
               particle, this.owner.getX(), this.owner.getY() + this.owner.getBbHeight() / 2.0 + 0.6, this.owner.getZ(), 1, 0.0, 0.0, 0.0, 0.0
            )
         );
   }

   private void spawnSpinHelix(ServerLevel level) {
      Optional<ParticleOptions> soul = this.particle(RedSoulHelper.particle(this.redSoul, "ghostly_soul"));
      if (!soul.isEmpty()) {
         double phase = (this.tickCount - 1) * 0.9;

         for (int i = 0; i < 4; i++) {
            double angle = phase + i * Math.PI / 2.0;
            double radius = 0.85 + (i & 1) * 0.3;
            level.sendParticles(
               soul.get(),
               this.owner.getX() + Math.cos(angle) * radius,
               this.owner.getY() + 0.35 + i * 0.42,
               this.owner.getZ() + Math.sin(angle) * radius,
               1,
               0.02,
               0.08,
               0.02,
               0.01
            );
         }
      }
   }

   private void spawnPillarRow(ServerLevel level) {
      for (double offset : new double[]{-2.0, 0.0, 2.0}) {
         Vec3 location = this.owner.position().add(this.forward.scale(0.25)).add(this.lateral.scale(offset));
         this.findGround(location.x, location.z)
            .ifPresent(
               point -> {
                  SpectralRondoSoulPillarEntity pillar = new SpectralRondoSoulPillarEntity(
                     level, point.x, point.y, point.z, (float)Math.atan2(this.forward.z, this.forward.x), 0, this.owner, 20, this.redSoul
                  );
                  level.addFreshEntity(pillar);
               }
            );
      }
   }

   private void performHit(ServerLevel level, Set<UUID> hitSet, double knockback, double lift) {
      AABB area = this.owner.getBoundingBox().inflate(3.5, 3.0, 3.5);

      for (LivingEntity target : level.getEntitiesOfClass(
         LivingEntity.class, area, targetx -> targetx.isAlive() && targetx != this.owner && !targetx.isAlliedTo(this.owner)
      )) {
         double dx = target.getX() - this.owner.getX();
         double dz = target.getZ() - this.owner.getZ();
         if (!(dx * dx + dz * dz > 12.25)
            && hitSet.add(target.getUUID())
            && DamageSources.applyDamage(target, this.damage, ((SpectralRondoSpell)ModSpells.SPECTRAL_RONDO.get()).getDamageSource(this, this.owner))) {
            Vec3 away = new Vec3(dx, 0.0, dz);
            if (away.lengthSqr() > 1.0E-4) {
               away = away.normalize();
            }

            target.setDeltaMovement(target.getDeltaMovement().add(away.scale(knockback)).add(0.0, lift, 0.0));
            target.hurtMarked = true;
         }
      }
   }

   private void spawnImpactVfx(ServerLevel level, boolean finale) {
      CameraShakeEntity.cameraShake(level, this.owner.position(), 20.0F, finale ? 0.09F : 0.05F, 0, 10);
      level.sendParticles(
         new RingData(
            0.0F,
            (float) (Math.PI / 2),
            18,
            this.redSoul ? 1.0F : 0.0F,
            this.redSoul ? 0.0F : 0.9F,
            this.redSoul ? 0.0F : 0.8F,
            0.85F,
            finale ? 42.0F : 32.0F,
            false,
            EnumRingBehavior.GROW
         ),
         this.owner.getX(),
         this.owner.getY() + 0.15,
         this.owner.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
      this.spawnAuraBurst(level, finale ? 56 : 34);
      this.particle(RedSoulHelper.particle(this.redSoul, "soul_explosion"))
         .ifPresent(
            particle -> level.sendParticles(particle, this.owner.getX(), this.owner.getY() + 1.0, this.owner.getZ(), finale ? 6 : 3, 1.2, 0.6, 1.2, 0.02)
         );
   }

   private void spawnAuraBurst(ServerLevel level, int count) {
      this.particle(RedSoulHelper.particle(this.redSoul, "ghostly_soul"))
         .ifPresent(particle -> level.sendParticles(particle, this.owner.getX(), this.owner.getY() + 1.0, this.owner.getZ(), count, 0.9, 0.75, 0.9, 0.06));
   }

   private Optional<Vec3> findGround(double x, double z) {
      BlockPos center = BlockPos.containing(x, this.owner.getY() + 1.0, z);
      int lowest = (int)Math.floor(this.owner.getY()) - 1;

      do {
         BlockPos below = center.below();
         BlockState belowState = this.level().getBlockState(below);
         if (belowState.isFaceSturdy(this.level(), below, Direction.UP)) {
            double height = 0.0;
            BlockState state = this.level().getBlockState(center);
            if (!state.isAir()) {
               VoxelShape shape = state.getCollisionShape(this.level(), center);
               if (!shape.isEmpty()) {
                  height = shape.max(Axis.Y);
               }
            }

            return Optional.of(new Vec3(x, center.getY() + height, z));
         }

         center = center.below();
      } while (center.getY() >= lowest);

      return Optional.empty();
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
         this.owner.removeEffect(ModMobEffects.SPECTRAL_RONDO);
         this.owner.resetFallDistance();
      }

      this.discard();
   }

   private Optional<ParticleOptions> particle(String path) {
      return BuiltInRegistries.PARTICLE_TYPE.getOptional(LegendaryMonstersCompat.id(path)).map(type -> (ParticleOptions)type);
   }

   private void playSound(ServerLevel level, String path, SoundEvent fallback, float volume, float pitch) {
      SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getOptional(LegendaryMonstersCompat.id(path)).orElse(fallback);
      level.playSound(null, this.owner.blockPosition(), sound, this.owner.getSoundSource(), volume, pitch);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
