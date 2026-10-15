package net.warphan.iss_magicfromtheeast.entity.mobs.bone_hands;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
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

public class BoneHandsEntity extends AbstractSpellCastingMob implements GeoEntity, IMagicSummon, IAnimatedAttacker {
   private static final EntityDataAccessor<Boolean> DATA_IS_RISING = SynchedEntityData.defineId(BoneHandsEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_RETREATING = SynchedEntityData.defineId(BoneHandsEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_DYING = SynchedEntityData.defineId(BoneHandsEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_ATTACKING = SynchedEntityData.defineId(BoneHandsEntity.class, EntityDataSerializers.BOOLEAN);
   private int riseAnimTick = 20;
   private int retreatAnimTick = 20;
   public int duration;
   private final RawAnimation BONE_RISE = RawAnimation.begin().thenPlay("bone_rise");
   private final RawAnimation BONE_IDLE = RawAnimation.begin().thenPlay("bone_idle");
   private final RawAnimation BONE_DEFEATED = RawAnimation.begin().thenPlay("bone_dead");
   private final RawAnimation BONE_RETREAT = RawAnimation.begin().thenPlay("bone_retreat");
   private final AnimationController<BoneHandsEntity> riseController = new AnimationController(this, "bone_rise_control", 0, this::risePredicate);
   private final AnimationController<BoneHandsEntity> idleController = new AnimationController(this, "bone_idle_control", 3, this::idlePredicate);
   private final AnimationController<BoneHandsEntity> slamController = new AnimationController(this, "bone_slamming", 2, this::slamPredicate);
   private final AnimationController<BoneHandsEntity> defeatedController = new AnimationController(this, "bone_ded", 0, this::deadPredicate);
   private final AnimationController<BoneHandsEntity> retreatController = new AnimationController(this, "bone_bye", 2, this::retreatPredicate);
   RawAnimation animationToPlay = null;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BoneHandsEntity(EntityType<? extends BoneHandsEntity> entityType, Level level) {
      super(entityType, level);
   }

   public BoneHandsEntity(Level level, LivingEntity owner, boolean playRiseAnimation) {
      this((EntityType<? extends BoneHandsEntity>)MFTEEntityRegistries.BONE_HAND_ENTITY.get(), level);
      this.setNoGravity(true);
      this.noPhysics = true;
      this.setSummoner(owner);
      if (playRiseAnimation) {
         this.triggerRiseAnimation();
      }
   }

   public void setDuration(int durationTick) {
      this.duration = durationTick;
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
      this.isAnimatingRetreat();
      this.entityData.set(DATA_IS_RETREATING, true);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new BoneHandAttackGoal(this, 1.0, 0, 0));
      this.targetSelector.addGoal(1, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]));
   }

   public static Builder prepareAttributes() {
      return Mob.createMobAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.0).add(Attributes.ATTACK_DAMAGE, 5.0).add(Attributes.MAX_HEALTH, 30.0);
   }

   protected BodyRotationControl createBodyControl() {
      return new BodyRotationControl(this);
   }

   protected LookControl createLookControl() {
      return super.createLookControl();
   }

   public boolean isPushable() {
      return false;
   }

   public boolean fireImmune() {
      return true;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || !this.isAnimatingRise() && !this.shouldIgnoreDamage(pSource)) {
         if (pAmount > 10.0F && !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            pAmount = 10.0F;
            return super.hurt(pSource, pAmount);
         } else {
            return super.hurt(pSource, pAmount);
         }
      } else {
         return false;
      }
   }

   public boolean doHurtTarget(Entity pEntity) {
      return Utils.doMeleeAttack(this, pEntity, MFTESpellRegistries.BONE_HANDS_SPELL.get().getDamageSource(this, this.getSummoner()));
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
      if (!this.level.isClientSide && this.deathTime >= 20 && !this.isRemoved()) {
         this.remove(RemovalReason.KILLED);
      }
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)MFTESoundRegistries.BONE_HURT.get();
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_IS_RISING, false);
      pBuilder.define(DATA_IS_RETREATING, false);
      pBuilder.define(DATA_IS_DYING, false);
      pBuilder.define(DATA_IS_ATTACKING, false);
   }

   public boolean isAnimatingRise() {
      return (Boolean)this.entityData.get(DATA_IS_RISING);
   }

   public boolean isAnimatingRetreat() {
      return (Boolean)this.entityData.get(DATA_IS_RETREATING);
   }

   public boolean isAnimatingDead() {
      return (Boolean)this.entityData.get(DATA_IS_DYING);
   }

   public boolean isAnimatingAttack() {
      return (Boolean)this.entityData.get(DATA_IS_ATTACKING);
   }

   public void stopAnimationAttack() {
      this.entityData.set(DATA_IS_ATTACKING, false);
   }

   public void triggerAnimatingAttack() {
      this.entityData.set(DATA_IS_ATTACKING, true);
   }

   public void triggerRiseAnimation() {
      this.entityData.set(DATA_IS_RISING, true);
   }

   public boolean isImmobile() {
      return super.isImmobile() || this.isAnimatingRise() || this.isAnimatingRetreat();
   }

   public void tick() {
      super.tick();
      if (this.isAnimatingRise()) {
         if (--this.riseAnimTick < 0) {
            this.entityData.set(DATA_IS_RISING, false);
            this.setXRot(0.0F);
            this.setOldPosAndRot();
         }
      } else {
         this.duration--;
         if (this.duration == 0) {
            this.onUnSummon();
         }
      }

      if (this.isAnimatingRetreat() && --this.retreatAnimTick < 0) {
         this.entityData.set(DATA_IS_RETREATING, false);
         this.discard();
      }
   }

   public double getTick(Object o) {
      return this.tickCount;
   }

   private PlayState risePredicate(AnimationState event) {
      if (!this.isAnimatingRise()) {
         return PlayState.STOP;
      } else {
         if (event.getController().getAnimationState() == State.STOPPED) {
            event.getController().setAnimation(this.BONE_RISE);
         }

         return PlayState.CONTINUE;
      }
   }

   private PlayState idlePredicate(AnimationState event) {
      if (!this.isAnimatingRise() && !this.isAnimatingAttack()) {
         event.getController().setAnimation(this.BONE_IDLE);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState retreatPredicate(AnimationState event) {
      if (this.isAnimatingRetreat()) {
         event.getController().setAnimation(this.BONE_RETREAT);
      }

      return PlayState.CONTINUE;
   }

   private PlayState deadPredicate(AnimationState event) {
      if (this.isAnimatingDead()) {
         event.getController().setAnimation(this.BONE_DEFEATED);
      }

      return PlayState.CONTINUE;
   }

   public void playAnimation(String animationID) {
      try {
         BoneHandsEntity.AttackAnim attackAnim = BoneHandsEntity.AttackAnim.valueOf(animationID);
         this.animationToPlay = RawAnimation.begin().thenPlay(attackAnim.data.animationId);
      } catch (Exception var3) {
         IronsSpellbooks.LOGGER.error("Entity {} Failed to play animation: {}", this, animationID);
      }
   }

   private PlayState slamPredicate(AnimationState<BoneHandsEntity> animationEvent) {
      AnimationController<BoneHandsEntity> controller = animationEvent.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.slamController);
      controllerRegistrar.add(this.riseController);
      controllerRegistrar.add(this.idleController);
      controllerRegistrar.add(this.retreatController);
      controllerRegistrar.add(this.defeatedController);
      super.registerControllers(controllerRegistrar);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static enum AttackAnim {
      BONE_SLAM(40, "bone_slam", 15);

      public final AttackAnimationData data;

      private AttackAnim(int lengthTick, String animationID, int... attackTimeStamp) {
         this.data = new AttackAnimationData(lengthTick, animationID, attackTimeStamp);
      }
   }
}
