package net.warphan.iss_magicfromtheeast.entity.mobs.spirit_ashigaru;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.warphan.iss_magicfromtheeast.ISS_MagicFromTheEast;
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

public class SpiritAshigaruEntity extends AbstractSpellCastingMob implements GeoEntity, IAnimatedAttacker, IMagicSummon {
   private static final EntityDataAccessor<Boolean> DATA_IS_RISING = SynchedEntityData.defineId(SpiritAshigaruEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_MELEE_TYPE = SynchedEntityData.defineId(SpiritAshigaruEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_RANGE_TYPE = SynchedEntityData.defineId(SpiritAshigaruEntity.class, EntityDataSerializers.BOOLEAN);
   public int riseTick = 25;
   private final RawAnimation ASHIGARU_RISE = RawAnimation.begin().thenPlay("rise");
   private final AnimationController<SpiritAshigaruEntity> riseController = new AnimationController(this, "rise_controller", 0, this::risePredicate);
   private final AnimationController<SpiritAshigaruEntity> attackTypeController = new AnimationController(
      this, "attack_type_controller", 0, this::attackTypePredicate
   );
   RawAnimation animationToPlay = null;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public SpiritAshigaruEntity(EntityType<? extends SpiritAshigaruEntity> entityType, Level level) {
      super(entityType, level);
      this.xpReward = 0;
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
   }

   public SpiritAshigaruEntity(Level level, LivingEntity owner, boolean playRiseAnimation) {
      this((EntityType<? extends SpiritAshigaruEntity>)MFTEEntityRegistries.ASHIGARU.get(), level);
      this.setSummoner(owner);
      if (playRiseAnimation) {
         this.triggerRiseAnimation();
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SpiritAshigaruAttackGoal(this, 1.0, 10, 30));
      this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(3, new GenericFollowOwnerGoal(this, this::getSummoner, 1.0, 12.0F, 5.0F, false, 25.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(5, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
   }

   public static Builder prepareAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 4.0)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ARMOR, 4.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.6)
         .add(Attributes.MOVEMENT_SPEED, 0.33)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ATTACK_KNOCKBACK, 1.6);
   }

   public void setSummoner(@Nullable LivingEntity owner) {
      if (owner != null) {
         SummonManager.setOwner(this, owner);
      }
   }

   public boolean isAlliedTo(Entity entity) {
      return super.isAlliedTo(entity) || this.isAlliedHelper(entity);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.getBoolean("melee")) {
         this.setMeleeType();
      }

      if (pCompound.getBoolean("range")) {
         this.setRangeType();
      }
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      if (this.isMeleeType()) {
         pCompound.putBoolean("melee", true);
      }

      if (this.isRangeType()) {
         pCompound.putBoolean("range", true);
      }
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.SCULK_SOUL, this.getX(), this.getY(), this.getZ(), 20, 1.5, 2.5, 1.5, 0.08, false);
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
            return SpiritAshigaruEntity.this.getTarget() == null;
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

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_IS_RISING, false);
      builder.define(DATA_IS_MELEE_TYPE, false);
      builder.define(DATA_IS_RANGE_TYPE, false);
   }

   public boolean isAnimatingRise() {
      return (Boolean)this.entityData.get(DATA_IS_RISING);
   }

   public void triggerRiseAnimation() {
      this.entityData.set(DATA_IS_RISING, true);
   }

   public boolean isMeleeType() {
      return (Boolean)this.entityData.get(DATA_IS_MELEE_TYPE);
   }

   public void setMeleeType() {
      this.entityData.set(DATA_IS_MELEE_TYPE, true);
   }

   public boolean isRangeType() {
      return (Boolean)this.entityData.get(DATA_IS_RANGE_TYPE);
   }

   public void setRangeType() {
      this.entityData.set(DATA_IS_RANGE_TYPE, true);
   }

   public void playAmbientSound() {
      this.playSound(this.getAmbientSound(), 2.0F, Mth.randomBetweenInclusive(this.getRandom(), 2, 10) * 0.1F);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)MFTESoundRegistries.ASHIGARU_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)MFTESoundRegistries.SPIRIT_MOB_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)MFTESoundRegistries.SPIRIT_MOB_DEATH.get();
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
      return pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || !this.isAnimatingRise() && !this.shouldIgnoreDamage(pSource)
         ? super.hurt(pSource, pAmount)
         : false;
   }

   public boolean doHurtTarget(Entity pEntity) {
      return Utils.doMeleeAttack(this, pEntity, MFTESpellRegistries.ASHIGARU_SQUAD_SPELL.get().getDamageSource(this, this.getSummoner()));
   }

   public void tick() {
      super.tick();
      if (this.isAnimatingRise() && --this.riseTick < 0) {
         this.entityData.set(DATA_IS_RISING, false);
         this.setXRot(0.0F);
         this.setOldPosAndRot();
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
            event.getController().setAnimation(this.ASHIGARU_RISE);
         }

         return PlayState.CONTINUE;
      }
   }

   public void playAnimation(String animationID) {
      try {
         SpiritAshigaruEntity.AttackType attackType = SpiritAshigaruEntity.AttackType.valueOf(animationID);
         this.animationToPlay = RawAnimation.begin().thenPlay(attackType.data.animationId);
      } catch (Exception var3) {
         ISS_MagicFromTheEast.LOGGER.error("Entity {} Failed to play animation: {}", this, animationID);
      }
   }

   private PlayState attackTypePredicate(AnimationState<SpiritAshigaruEntity> animationEvent) {
      AnimationController<SpiritAshigaruEntity> controller = animationEvent.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.riseController);
      controllerRegistrar.add(this.attackTypeController);
      super.registerControllers(controllerRegistrar);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static enum AttackType {
      MELEE(20, "melee_attack", 13),
      RANGE(50, "range_attack", 30);

      public final AttackAnimationData data;

      private AttackType(int lengthTick, String animationID, int... attackTimeStamp) {
         this.data = new AttackAnimationData(lengthTick, animationID, attackTimeStamp);
      }
   }
}
