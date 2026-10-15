package com.somake.somakespells.entity;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.SpiralOfRuinSpell;
import com.somake.somakespells.util.RedSoulHelper;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SpiralOfRuinProjectileEntity extends AbstractMagicProjectile {
   private static final EntityDataAccessor<Float> DIRECT_DAMAGE = SynchedEntityData.defineId(SpiralOfRuinProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> EXPLOSION_DAMAGE = SynchedEntityData.defineId(SpiralOfRuinProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> EXPLOSION_COUNT = SynchedEntityData.defineId(SpiralOfRuinProjectileEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> MAX_RADIUS = SynchedEntityData.defineId(SpiralOfRuinProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> RED_SOUL = SynchedEntityData.defineId(SpiralOfRuinProjectileEntity.class, EntityDataSerializers.BOOLEAN);
   private boolean impacted;

   public SpiralOfRuinProjectileEntity(EntityType<? extends AbstractMagicProjectile> type, Level level) {
      super(type, level);
      this.setNoGravity(false);
   }

   public SpiralOfRuinProjectileEntity(
      Level level, LivingEntity owner, float directDamage, float explosionDamage, int explosionCount, float maxRadius, boolean redSoul
   ) {
      this((EntityType<? extends AbstractMagicProjectile>)ModEntityTypes.SPIRAL_OF_RUIN_PROJECTILE.get(), level);
      this.setOwner(owner);
      this.entityData.set(DIRECT_DAMAGE, directDamage);
      this.entityData.set(EXPLOSION_DAMAGE, explosionDamage);
      this.entityData.set(EXPLOSION_COUNT, explosionCount);
      this.entityData.set(MAX_RADIUS, maxRadius);
      this.entityData.set(RED_SOUL, redSoul);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DIRECT_DAMAGE, 0.0F);
      builder.define(EXPLOSION_DAMAGE, 0.0F);
      builder.define(EXPLOSION_COUNT, 14);
      builder.define(MAX_RADIUS, 5.5F);
      builder.define(RED_SOUL, false);
   }

   public float getSpeed() {
      return 1.0F;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.tickCount > 80) {
         this.discard();
      }
   }

   protected boolean canHitEntity(Entity entity) {
      Entity owner = this.getOwner();
      return entity instanceof LivingEntity living && entity != owner ? owner == null || !living.isAlliedTo(owner) : false;
   }

   protected void onHitBlock(BlockHitResult result) {
      this.resolveImpact(result.getLocation(), this.getDeltaMovement());
   }

   protected void onHitEntity(EntityHitResult result) {
      if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity target) {
         DamageSources.applyDamage(
            target, (Float)this.entityData.get(DIRECT_DAMAGE), ((SpiralOfRuinSpell)ModSpells.SPIRAL_OF_RUIN.get()).getDamageSource(this, this.getOwner())
         );
      }

      this.resolveImpact(result.getLocation(), this.getDeltaMovement());
   }

   private void resolveImpact(Vec3 impactPoint, Vec3 incomingDirection) {
      if (!this.impacted && !this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
         this.impacted = true;
         LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
         if (owner != null && owner.isAlive()) {
            SpiralOfRuinControllerEntity controller = new SpiralOfRuinControllerEntity(
               (EntityType<?>)ModEntityTypes.SPIRAL_OF_RUIN_CONTROLLER.get(),
               serverLevel,
               owner,
               impactPoint,
               incomingDirection,
               (Float)this.entityData.get(EXPLOSION_DAMAGE),
               (Integer)this.entityData.get(EXPLOSION_COUNT),
               (Float)this.entityData.get(MAX_RADIUS),
               this.isRedSoul()
            );
            serverLevel.addFreshEntity(controller);
         }

         CameraShakeEntity.cameraShake(serverLevel, impactPoint, 10.0F, 0.15F, 10, 5);
         this.spawnOriginalImpactParticles(serverLevel, impactPoint);
         this.discard();
      }
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return BuiltInRegistries.SOUND_EVENT
         .getHolder(LegendaryMonstersCompat.id("soul_fly"))
         .map(holder -> (Holder<SoundEvent>)holder)
         .or(
            () -> BuiltInRegistries.SOUND_EVENT
               .getHolder((ResourceKey)BuiltInRegistries.SOUND_EVENT.getResourceKey(SoundEvents.TRIDENT_HIT_GROUND).orElseThrow())
               .map(holder -> (Holder<SoundEvent>)holder)
         );
   }

   public void trailParticles() {
      ParticleOptions particle = this.getLegendaryParticle(RedSoulHelper.particle(this.isRedSoul(), "ghostly_soul")).orElse(ParticleTypes.SOUL_FIRE_FLAME);
      this.level().addParticle(particle, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0.0, 0.025, 0.0);
   }

   public void impactParticles(double x, double y, double z) {
   }

   private void spawnOriginalImpactParticles(ServerLevel serverLevel, Vec3 impactPoint) {
      Optional<ParticleOptions> particle = this.getLegendaryParticle(RedSoulHelper.particle(this.isRedSoul(), "soul_explosion"));
      if (!particle.isEmpty()) {
         float f9 = (this.random.nextFloat() - 0.5F) * 8.0F;
         float f10 = (this.random.nextFloat() - 0.5F) * 4.0F;
         float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
         float f8 = (this.random.nextFloat() - 0.75F) * 5.0F;
         float f6 = (this.random.nextFloat() - 0.75F) * 3.0F;
         float f7 = (this.random.nextFloat() - 0.75F) * 5.0F;
         serverLevel.sendParticles(particle.get(), impactPoint.x + f9, impactPoint.y + f10, impactPoint.z + f2, 1, 0.0, 0.0, 0.0, 0.0);
         serverLevel.sendParticles(particle.get(), impactPoint.x + f8, impactPoint.y + f6, impactPoint.z + f7, 1, 0.0, 0.0, 0.0, 0.0);
         serverLevel.sendParticles(particle.get(), impactPoint.x + f9, impactPoint.y + 2.0 + f10, impactPoint.z + f2, 1, 0.0, 0.0, 0.0, 0.0);
         serverLevel.sendParticles(particle.get(), impactPoint.x + f8, impactPoint.y + 2.0 + f6, impactPoint.z + f7, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   private Optional<ParticleOptions> getLegendaryParticle(String path) {
      return BuiltInRegistries.PARTICLE_TYPE.getOptional(LegendaryMonstersCompat.id(path)).map(type -> (ParticleOptions)type);
   }

   public boolean isRedSoul() {
      return (Boolean)this.entityData.get(RED_SOUL);
   }
}
