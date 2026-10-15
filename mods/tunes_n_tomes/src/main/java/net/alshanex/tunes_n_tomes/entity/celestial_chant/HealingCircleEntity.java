package net.alshanex.tunes_n_tomes.entity.celestial_chant;

import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.alshanex.tunes_n_tomes.util.HelperMethods;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HealingCircleEntity extends AoeEntity implements GeoEntity {
   private int effectAmplifier;
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;
   private DamageSource damageSource;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("idle");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HealingCircleEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public HealingCircleEntity(Level level, int effectAmplifier, LivingEntity owner) {
      this((EntityType<? extends Projectile>)TEntityRegistry.HEALING_CIRCLE.get(), level);
      this.effectAmplifier = effectAmplifier;
      this.setOwner(owner);
   }

   public void applyEffect(LivingEntity target) {
   }

   public void tick() {
      super.tick();
      if (this.getOwner() != null && this.tickCount % 5 == 0) {
         int radiusSqr = 16;
         this.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               new AABB(this.position().subtract(4.0, 4.0, 4.0), this.position().add(4.0, 4.0, 4.0)),
               livingEntity -> HelperMethods.horizontalDistanceSqr(livingEntity, this.position()) < radiusSqr
                  && (this.getOwner().isAlliedTo(livingEntity) || livingEntity.is(this.getOwner()))
                  && !livingEntity.hasEffect(MobEffects.REGENERATION)
            )
            .forEach(livingEntity -> {
               livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, Math.min(2, this.effectAmplifier), false, false, true));
               livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.OAKSKIN, 100, this.effectAmplifier, false, false, true));
            });
      }
   }

   public float getParticleCount() {
      return 0.0F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.hasUUID("Owner")) {
         this.ownerUUID = pCompound.getUUID("Owner");
      }
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      if (this.ownerUUID != null) {
         pCompound.putUUID("Owner", this.ownerUUID);
      }
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      this.owner = pOwner;
      this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
         Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "idle", 0, this::idlePredicate));
   }

   private PlayState idlePredicate(AnimationState event) {
      event.getController().setAnimation(this.idle);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
