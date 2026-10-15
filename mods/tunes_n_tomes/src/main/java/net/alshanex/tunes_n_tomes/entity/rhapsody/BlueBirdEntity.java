package net.alshanex.tunes_n_tomes.entity.rhapsody;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.alshanex.tunes_n_tomes.registry.TParticleRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlueBirdEntity extends PathfinderMob implements GeoEntity, IMagicSummon {
   private int effectAmplifier;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("idle");
   private final RawAnimation walk = RawAnimation.begin().thenLoop("walk");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BlueBirdEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
      super(entityType, level);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.xpReward = 0;
   }

   public BlueBirdEntity(Level level, int effectAmplifier) {
      this((EntityType<? extends PathfinderMob>)TEntityRegistry.BLUE_BIRD.get(), level);
      this.effectAmplifier = effectAmplifier;
   }

   public void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(6, new BlueBirdEntity.BuffAllyGoal(this, this::getSummoner, this.effectAmplifier));
      this.goalSelector.addGoal(7, new GenericFollowOwnerGoal(this, this::getSummoner, 0.9F, 10.0F, 3.0F, false, 25.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new BlueBirdEntity.FlyingBirdWanderGoal(this, 60));
   }

   protected PathNavigation createNavigation(Level level) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
      flyingpathnavigation.setCanOpenDoors(false);
      flyingpathnavigation.setCanFloat(true);
      flyingpathnavigation.setCanPassDoors(true);
      return flyingpathnavigation;
   }

   public void travel(Vec3 travelVector) {
      if (this.isControlledByLocalInstance()) {
         if (this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
         } else if (this.isInLava()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
         } else {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
         }
      }

      this.calculateEntityAnimation(false);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.FLYING_SPEED, 0.15F)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
   }

   public boolean isAttackable() {
      return false;
   }

   public boolean isInvulnerableTo(DamageSource pSource) {
      return true;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canBeLeashed() {
      return false;
   }

   public boolean attackable() {
      return false;
   }

   public boolean isPickable() {
      return false;
   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public boolean canCollideWith(Entity entity) {
      return false;
   }

   public boolean isAlliedTo(Entity pEntity) {
      return super.isAlliedTo(pEntity) || this.isAlliedHelper(pEntity);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
   }

   public void tick() {
      super.tick();
      Entity summoner = SummonManager.getOwner(this);
      if (summoner != null && summoner instanceof LivingEntity livingSummoner && this.tickCount % 5 == 0) {
         if (livingSummoner.hasEffect(MobEffectRegistry.HASTENED)) {
            if (livingSummoner.getEffect(MobEffectRegistry.HASTENED).getDuration() < 500) {
               livingSummoner.addEffect(
                  new MobEffectInstance(
                     MobEffectRegistry.HASTENED,
                     500 - livingSummoner.getEffect(MobEffectRegistry.HASTENED).getDuration(),
                     Math.max(0, this.effectAmplifier - 1)
                  )
               );
            }
         } else {
            livingSummoner.addEffect(new MobEffectInstance(MobEffectRegistry.HASTENED, 500, Math.max(0, this.effectAmplifier - 1)));
         }

         if (livingSummoner.hasEffect(TEffectRegistry.CHEERED)) {
            if (livingSummoner.getEffect(TEffectRegistry.CHEERED).getDuration() < 500) {
               livingSummoner.addEffect(
                  new MobEffectInstance(TEffectRegistry.CHEERED, 500 - livingSummoner.getEffect(TEffectRegistry.CHEERED).getDuration(), 0)
               );
            }
         } else {
            livingSummoner.addEffect(new MobEffectInstance(TEffectRegistry.CHEERED, 500, 0));
         }
      }
   }

   public void die(DamageSource pDamageSource) {
      this.onDeathHelper();
      super.die(pDamageSource);
   }

   public void onRemovedFromLevel() {
      this.onRemovedHelper(this);
      super.onRemovedFromLevel();
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.discard();
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "idle", 0, this::idlePredicate));
   }

   private PlayState idlePredicate(AnimationState event) {
      if (event.isMoving()) {
         event.getController().setAnimation(this.walk);
         return PlayState.CONTINUE;
      } else if (!event.isMoving()) {
         event.getController().setAnimation(this.idle);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   protected class BuffAllyGoal extends Goal {
      private final BlueBirdEntity bird;
      private LivingEntity ally;
      private final Supplier<Entity> petOwner;
      private final int effectAmplifier;

      public BuffAllyGoal(BlueBirdEntity bird, Supplier<Entity> owner, int effectAmplifier) {
         this.bird = bird;
         this.petOwner = owner;
         this.effectAmplifier = effectAmplifier;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         Entity livingentity = this.petOwner.get();
         if (livingentity == null) {
            return false;
         } else if (livingentity instanceof LivingEntity owner) {
            if (this.ally != null) {
               return false;
            } else {
               this.ally = this.bird
                  .level()
                  .getEntitiesOfClass(
                     LivingEntity.class,
                     this.bird.getBoundingBox().inflate(15.0),
                     livingEntity -> livingEntity != owner
                        && owner.isAlliedTo(livingEntity)
                        && !livingEntity.hasEffect(MobEffectRegistry.HASTENED)
                        && !livingEntity.hasEffect(TEffectRegistry.CHEERED)
                  )
                  .stream()
                  .min(Comparator.comparingDouble(this.bird::distanceTo))
                  .orElse(null);
               return this.ally != null;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.ally != null && this.ally.isAlive() && !this.ally.hasEffect(MobEffectRegistry.HASTENED) && !this.ally.hasEffect(TEffectRegistry.CHEERED);
      }

      public void start() {
         this.bird.getNavigation().moveTo(this.ally, 1.0);
      }

      public void tick() {
         if (this.ally != null) {
            if (this.bird.distanceTo(this.ally) < 3.0) {
               this.bird.getLookControl().setLookAt(this.ally, 10.0F, 10.0F);
               if (!this.ally.hasEffect(MobEffectRegistry.HASTENED)) {
                  this.ally.addEffect(new MobEffectInstance(MobEffectRegistry.HASTENED, 300, Math.max(0, this.effectAmplifier - 1), false, false, true));
                  this.ally.addEffect(new MobEffectInstance(TEffectRegistry.CHEERED, 300, 0, false, false, true));
                  if (!BlueBirdEntity.this.level().isClientSide) {
                     BlueBirdEntity.this.level()
                        .playSound(
                           null,
                           this.bird.getX(),
                           this.bird.getY(),
                           this.bird.getZ(),
                           (SoundEvent)TSoundRegistry.BIRD_CHIRP.get(),
                           SoundSource.MUSIC,
                           1.0F,
                           1.0F
                        );
                     MagicManager.spawnParticles(
                        BlueBirdEntity.this.level(),
                        (ParticleOptions)TParticleRegistry.MUSICAL_NOTE_PARTICLE.get(),
                        this.bird.getX(),
                        this.bird.getY(),
                        this.bird.getZ(),
                        5,
                        0.0,
                        0.0,
                        0.0,
                        0.18,
                        true
                     );
                  }
               }
            } else {
               this.bird.getNavigation().moveTo(this.ally, 1.0);
            }
         }
      }

      public void stop() {
         this.ally = null;
      }
   }

   class FlyingBirdWanderGoal extends Goal {
      private static final int WANDER_THRESHOLD = 22;
      private final BlueBirdEntity bird;
      private final int interval;
      private int cooldown;

      FlyingBirdWanderGoal(BlueBirdEntity bird, int interval) {
         this.bird = bird;
         this.interval = interval;
         this.cooldown = 0;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (this.cooldown > 0) {
            this.cooldown--;
            return false;
         } else {
            return this.bird.getNavigation().isDone() && this.bird.getRandom().nextInt(10) == 0;
         }
      }

      public boolean canContinueToUse() {
         return this.bird.getNavigation().isInProgress();
      }

      public void start() {
         Vec3 vec3 = this.findPos();
         if (vec3 != null) {
            this.bird.getNavigation().moveTo(this.bird.getNavigation().createPath(BlockPos.containing(vec3), 1), 1.0);
         }

         this.cooldown = this.interval;
      }

      @Nullable
      private Vec3 findPos() {
         Vec3 vec3;
         if (this.bird.getTarget() != null) {
            Vec3 targetPos = this.bird.getTarget().position();
            vec3 = targetPos.subtract(this.bird.position()).normalize();
         } else {
            vec3 = this.bird.getViewVector(0.0F);
         }

         int i = 8;
         Vec3 randomPos = HoverRandomPos.getPos(this.bird, i, 7, vec3.x, vec3.z, (float) (Math.PI / 2), 3, 1);
         return randomPos != null ? randomPos : AirAndWaterRandomPos.getPos(this.bird, i, 4, -2, vec3.x, vec3.z, (float) (Math.PI / 2));
      }
   }
}
