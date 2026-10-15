package net.warphan.iss_magicfromtheeast.entity.mobs.jade_executioner;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JadeExecutionerEntity extends AbstractSpellCastingMob implements GeoEntity, IAnimatedAttacker, IMagicSummon {
   private static final EntityDataAccessor<Boolean> DATA_IS_SHOWDOWN = SynchedEntityData.defineId(JadeExecutionerEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_DYING = SynchedEntityData.defineId(JadeExecutionerEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_ATTACKING = SynchedEntityData.defineId(JadeExecutionerEntity.class, EntityDataSerializers.BOOLEAN);
   private int showdownAnimTick = 40;
   private final RawAnimation EXECUTIONER_SHOWDOWN = RawAnimation.begin().thenPlay("showdown");
   private final RawAnimation EXECUTIONER_IDLE = RawAnimation.begin().thenPlay("idle");
   private final RawAnimation EXECUTIONER_MOVING = RawAnimation.begin().thenPlay("moving");
   private final RawAnimation EXECUTIONER_DEAD = RawAnimation.begin().thenPlay("defeat");
   private final AnimationController<JadeExecutionerEntity> showdownController = new AnimationController(
      this, "showdown_controller", 0, this::showdownPredicate
   );
   private final AnimationController<JadeExecutionerEntity> movingController = new AnimationController(this, "moving_controller", 5, this::movePredicate);
   private final AnimationController<JadeExecutionerEntity> deadController = new AnimationController(this, "dead_controller", 0, this::deadPredicate);
   private final AnimationController<JadeExecutionerEntity> combatController = new AnimationController(this, "combat_controller", 0, this::combatPredicate);
   RawAnimation animationToPlay = null;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public JadeExecutionerEntity(EntityType<? extends JadeExecutionerEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 0;
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
      this.noCulling = true;
   }

   public JadeExecutionerEntity(Level level, LivingEntity owner, boolean playShowdownAnimation) {
      this((EntityType<? extends JadeExecutionerEntity>)MFTEEntityRegistries.JADE_EXECUTIONER.get(), level);
      this.setSummoner(owner);
      if (playShowdownAnimation) {
         this.triggerShowdownAnimation();
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new JadeExecutionerAttackGoal(this, 1.0, 0, 0));
      this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 48.0F, 0.7F));
      this.goalSelector.addGoal(7, new GenericFollowOwnerGoal(this, this::getSummoner, 1.0, 16.0F, 8.0F, false, 35.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
   }

   public static Builder prepareAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 200.0)
         .add(Attributes.ATTACK_DAMAGE, 9.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 3.5)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.ARMOR, 12.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ATTACK_KNOCKBACK, 2.0);
   }

   public void setSummoner(@Nullable LivingEntity owner) {
      if (owner != null) {
         SummonManager.setOwner(this, owner);
      }
   }

   public boolean isAlliedTo(Entity entity) {
      return super.isAlliedTo(entity) || this.isAlliedHelper(entity);
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.SCRAPE, this.getX(), this.getY(), this.getZ(), 60, 1.5, 2.5, 1.5, 0.08, false);
         this.setRemoved(RemovalReason.DISCARDED);
      }
   }

   public void onRemovedFromLevel() {
      this.onRemovedHelper(this);
      super.onRemovedFromLevel();
   }

   public void remove(RemovalReason reason) {
      super.remove(reason);
   }

   protected BodyRotationControl createBodyControl() {
      return new BodyRotationControl(this);
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float rotateTowards(float pFrom, float pTo, float pMaxDelta) {
            return super.rotateTowards(pFrom, pTo, pMaxDelta * 2.5F);
         }

         protected boolean resetXRotOnTick() {
            return JadeExecutionerEntity.this.getTarget() == null;
         }
      };
   }

   protected MoveControl createMoveControl() {
      return new MoveControl(this) {
         protected float rotlerp(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedZ - this.mob.getZ();
            return d0 * d0 + d1 * d1 < 0.5 ? pSourceAngle : super.rotlerp(pSourceAngle, pTargetAngle, pMaximumChange * 0.25F);
         }
      };
   }

   public void playAmbientSound() {
      this.playSound(this.getAmbientSound(), 2.0F, Mth.randomBetweenInclusive(this.getRandom(), 2, 10) * 0.1F);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)MFTESoundRegistries.EXECUTIONER_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)MFTESoundRegistries.EXECUTIONER_HURT.get();
   }

   public boolean isPushable() {
      return false;
   }

   public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
      return false;
   }

   public boolean fireImmune() {
      return true;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || !this.isAnimatingShowdown() && !this.shouldIgnoreDamage(pSource)
         ? super.hurt(pSource, pAmount)
         : false;
   }

   public boolean doHurtTarget(Entity pEntity) {
      return Utils.doMeleeAttack(this, pEntity, MFTESpellRegistries.PUNISHING_HEAVEN_SPELL.get().getDamageSource(this, this.getSummoner()));
   }

   public void die(DamageSource pDamageSource) {
      super.die(pDamageSource);
      if (this.isDeadOrDying() && !this.level.isClientSide) {
         this.isAnimatingDead();
         this.entityData.set(DATA_IS_DYING, true);
      }
   }

   public void tickDeath() {
      this.deathTime++;
      if (!this.level.isClientSide && this.deathTime >= 15 && !this.isRemoved()) {
         this.remove(RemovalReason.KILLED);
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_IS_SHOWDOWN, false);
      pBuilder.define(DATA_IS_DYING, false);
      pBuilder.define(DATA_IS_ATTACKING, false);
   }

   public boolean isAnimatingShowdown() {
      return (Boolean)this.entityData.get(DATA_IS_SHOWDOWN);
   }

   public boolean isAnimatingDead() {
      return (Boolean)this.entityData.get(DATA_IS_DYING);
   }

   public void triggerShowdownAnimation() {
      this.entityData.set(DATA_IS_SHOWDOWN, true);
   }

   public boolean isImmobile() {
      return super.isImmobile() || this.isAnimatingShowdown();
   }

   public void tick() {
      if (this.isAnimatingShowdown()) {
         if (!this.level.isClientSide) {
            if (this.tickCount == 5) {
               this.playSound((SoundEvent)MFTESoundRegistries.EXECUTIONER_SHOWDOWN.value(), 16.0F, 0.75F);
            }

            if (this.tickCount == 10) {
               this.damageOnLandingImpact(this);
            }

            if (this.tickCount == 20) {
               this.playSound((SoundEvent)MFTESoundRegistries.ROAR.value(), 20.0F, 0.95F);
            }
         }

         if (--this.showdownAnimTick < 0) {
            this.entityData.set(DATA_IS_SHOWDOWN, false);
            this.setXRot(0.0F);
            this.setOldPosAndRot();
         }
      } else {
         super.tick();
      }
   }

   protected void damageOnLandingImpact(LivingEntity entity) {
      Level level = entity.level;
      float radius = 4.5F;
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(MFTESchoolRegistries.SYMMETRY.get().getTargetingColor(), radius),
         this.getX(),
         this.getY() + 0.165F,
         this.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(level, ParticleTypes.SCRAPE, this.getX(), this.getY() + 0.1, this.getZ(), 60, 2.0, 0.2, 2.0, 0.5, false);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 10, entity.position(), radius * 2.0F));
      float damage = entity.getMaxHealth() / 10.0F;
      level.getEntities(
            entity,
            entity.getBoundingBox().inflate(radius, 4.0, radius),
            target -> !DamageSources.isFriendlyFireBetween(target, entity) && Utils.hasLineOfSight(level, entity, target, true)
         )
         .forEach(target -> {
            if (target instanceof LivingEntity livingEntity && livingEntity.distanceToSqr(entity) < radius * radius) {
               DamageSources.applyDamage(target, damage, MFTESpellRegistries.PUNISHING_HEAVEN_SPELL.get().getDamageSource(this, this.getSummoner()));
            }
         });
   }

   public double getTick(Object o) {
      return this.tickCount;
   }

   private PlayState showdownPredicate(AnimationState event) {
      if (!this.isAnimatingShowdown()) {
         return PlayState.STOP;
      } else {
         if (event.getController().getAnimationState() == State.STOPPED) {
            event.getController().setAnimation(this.EXECUTIONER_SHOWDOWN);
         }

         return PlayState.CONTINUE;
      }
   }

   private PlayState movePredicate(AnimationState event) {
      Vec3 motion = this.getDeltaMovement();
      float horizontalSpeed = (float)Math.sqrt(motion.x * motion.x + motion.z * motion.z);
      if (!this.isAnimatingShowdown() && !this.isAnimatingDead()) {
         if (horizontalSpeed > 0.001F) {
            event.getController().setAnimation(this.EXECUTIONER_MOVING);
            if (horizontalSpeed > 0.25F) {
               event.getController().setAnimationSpeed(this.walkAnimation.speed() * 2.5F);
            } else {
               event.getController().setAnimationSpeed(1.0);
            }
         } else {
            event.getController().setAnimation(this.EXECUTIONER_IDLE);
         }

         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState deadPredicate(AnimationState event) {
      if (this.isAnimatingDead()) {
         event.getController().setAnimation(this.EXECUTIONER_DEAD);
      }

      return PlayState.CONTINUE;
   }

   public void playAnimation(String animationID) {
      try {
         JadeExecutionerEntity.AttackAnim attackAnim = JadeExecutionerEntity.AttackAnim.valueOf(animationID);
         this.animationToPlay = RawAnimation.begin().thenPlay(attackAnim.data.animationId);
      } catch (Exception var3) {
         IronsSpellbooks.LOGGER.error("Entity {} Failed to play animation: {}", this, animationID);
      }
   }

   private PlayState combatPredicate(AnimationState<JadeExecutionerEntity> animationEvent) {
      AnimationController<JadeExecutionerEntity> controller = animationEvent.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.showdownController);
      controllerRegistrar.add(this.movingController);
      controllerRegistrar.add(this.deadController);
      controllerRegistrar.add(this.combatController);
      super.registerControllers(controllerRegistrar);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static enum AttackAnim {
      AXE_DOWN(25, "axe_down", 15),
      AXE_LEFT(20, "axe_sweep_left", 10),
      AXE_RIGHT(25, "axe_sweep_right", 13),
      SHIELD_BASH(20, "shield_bash", 8),
      BITE(15, "bite", 10);

      public final AttackAnimationData data;

      private AttackAnim(int lengthTick, String animationID, int... attackTimeStamp) {
         this.data = new AttackAnimationData(lengthTick, animationID, attackTimeStamp);
      }
   }
}
