package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Aptos;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import javax.annotation.Nullable;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AptosEntity extends AbstractSpellCastingMob implements GeoEntity, IAnimatedAttacker, IMagicSummon {
   private static final EntityDataAccessor<Boolean> DATA_IS_RISING = SynchedEntityData.defineId(AptosEntity.class, EntityDataSerializers.BOOLEAN);
   public int riseTick = 30;
   private final RawAnimation APTOS_RISE = RawAnimation.begin().thenPlay("keeper_rise");
   private final AnimationController<AptosEntity> riseController = new AnimationController(this, "rise_controller", 0, this::risePredicate);
   private final AnimationController<AptosEntity> combatController = new AnimationController(this, "combat_controller", 0, this::combatPredicate);
   RawAnimation animationToPlay = null;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AptosEntity(EntityType<? extends AptosEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 0;
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
   }

   public AptosEntity(Level level, LivingEntity owner, boolean playRiseAnimation) {
      this((EntityType<? extends AptosEntity>)HnSEntityRegistry.APTOS.get(), level);
      this.setSummoner(owner);
      if (playRiseAnimation) {
         this.triggerRiseAnimation();
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(4, new AptosAxeStrikeGoal(this, 1.0, 10, 30));
      this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, true, entity -> !entity.getType().is(ModTags.INFERNAL_ALLIES)));
   }

   public static Builder prepareAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 3.5)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ARMOR, 8.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
         .add(Attributes.MOVEMENT_SPEED, 0.21)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ATTACK_KNOCKBACK, 1.6);
   }

   public void setSummoner(@Nullable LivingEntity owner) {
      if (owner != null) {
         SummonManager.setOwner(this, owner);
      }
   }

   public boolean isAlliedTo(Entity pEntity) {
      return super.isAlliedTo(pEntity) || pEntity.getType().is(ModTags.INFERNAL_ALLIES);
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.SCULK_SOUL, this.getX(), this.getY(), this.getZ(), 40, 1.5, 2.5, 1.5, 0.08, false);
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

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(HnSItemRegistry.LEGIONNAIRE_WARLOCK_AXE));
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
      return pSpawnData;
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
            return AptosEntity.this.getTarget() == null;
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
      return (SoundEvent)SoundRegistry.KEEPER_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)SoundRegistry.KEEPER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)SoundRegistry.KEEPER_DEATH.get();
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

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return super.hurt(pSource, pAmount);
      } else if (this.level().isClientSide) {
         return false;
      } else {
         return !this.isAnimatingRise() && !this.shouldIgnoreDamage(pSource) ? super.hurt(pSource, pAmount) : false;
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_IS_RISING, false);
   }

   public boolean isAnimatingRise() {
      return (Boolean)this.entityData.get(DATA_IS_RISING);
   }

   public void triggerRiseAnimation() {
      this.entityData.set(DATA_IS_RISING, true);
   }

   public boolean isImmobile() {
      return super.isImmobile() || this.isAnimatingRise();
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
            event.getController().setAnimation(this.APTOS_RISE);
         }

         return PlayState.CONTINUE;
      }
   }

   public void playAnimation(String animationID) {
      try {
         AptosEntity.AttackType attackType = AptosEntity.AttackType.valueOf(animationID);
         this.animationToPlay = RawAnimation.begin().thenPlay(attackType.data.animationId);
      } catch (Exception var31) {
         IronsSpellbooks.LOGGER.error("Entity {} Failed to play animation: {}", this, animationID);
      }
   }

   private PlayState combatPredicate(AnimationState<AptosEntity> animationEvent) {
      AnimationController<AptosEntity> controller = animationEvent.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.riseController);
      controllerRegistrar.add(this.combatController);
      super.registerControllers(controllerRegistrar);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static enum AttackType {
      DOUBLE_SLASH(15, "sword_double_slash", 10),
      SINGLE_UPWARD(15, "sword_single_upward", 10),
      SINGLE_HORIZONTAL(15, "sword_single_horizontal", 10),
      SINGLE_HORIZONTAL_FAST(15, "sword_single_horizontal_fast", 10),
      SINGLE_STAB(15, "sword_stab", 10),
      LUNGE(40, "sword_lunge", 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);

      public final AttackAnimationData data;

      private AttackType(int lengthTick, String animationID, int... attackTimeStamp) {
         this.data = new AttackAnimationData(lengthTick, animationID, attackTimeStamp);
      }
   }
}
