package net.hazen.hazennstuff.Entity.Mobs.Mobs.VoidWanderer;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.UniqueAbstractSpellCastingMob;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.hazen.hazennstuff.HazenNStuff;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VoidWanderer extends UniqueAbstractSpellCastingMob implements Enemy, IAnimatedAttacker {
   private final AnimatableInstanceCache cache;
   RawAnimation animationToPlay = null;
   private SpellData castingSpell;
   private final AnimationController<VoidWanderer> attackAnimationController;
   private final AnimationController<VoidWanderer> contCastAnimationController;
   private final AnimationController<VoidWanderer> animationController;
   private int nextHeightOffsetChangeTick;
   private float allowedHeightOffset = 0.25F;

   public VoidWanderer(EntityType<? extends UniqueAbstractSpellCastingMob> entityType, Level level) {
      super(entityType, level);
      this.cache = GeckoLibUtil.createInstanceCache(this);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.animationToPlay = null;
      this.attackAnimationController = new AnimationController(this, "attack_controller", 0, this::attackPredicate);
      this.contCastAnimationController = new AnimationController(this, "continuous_cast_controller", 0, this::continuousCastPredicate);
      this.animationController = new AnimationController(this, "controller", 0, this::predicate);
      this.xpReward = 0;
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
      this.noCulling = true;
      this.xpReward = 50;
   }

   public VoidWanderer(Level level, LivingEntity owner, boolean playAnimation) {
      this((EntityType<? extends UniqueAbstractSpellCastingMob>)HnSEntityRegistry.VOID_WANDERER.get(), level);
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float rotateTowards(float from, float to, float maxDelta) {
            return super.rotateTowards(from, to, maxDelta * 2.5F);
         }

         protected boolean resetXRotOnTick() {
            return VoidWanderer.this.getTarget() == null;
         }
      };
   }

   protected MoveControl createMoveControl() {
      return new MoveControl(this) {
         protected float rotlerp(float sourceAngle, float targetAngle, float maximumChange) {
            double x = this.wantedX - this.mob.getX();
            double z = this.wantedZ - this.mob.getZ();
            return x * x + z * z < 0.5 ? sourceAngle : super.rotlerp(sourceAngle, targetAngle, maximumChange * 0.25F);
         }
      };
   }

   protected PathNavigation createNavigation(Level level) {
      return new NotIdioticNavigation(this, level);
   }

   public void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, HnSSpellRegistries.EVERCOMET_BARRAGE.get(), 3, 5, 250, 350, 1));
      this.goalSelector
         .addGoal(
            4,
            new VoidWandererAttackGoal(this, 1.0, 10, 30)
               .setSpells(List.of(HnSSpellRegistries.COSMIC_BOLT.get()), List.of(HnSSpellRegistries.SHOOTING_STAR.get()), List.of(), List.of())
               .setDrinksPotions()
         );
      this.goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 1.0));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.9));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 8.0F));
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 9.0)
         .add(Attributes.ATTACK_KNOCKBACK, 2.0)
         .add(Attributes.MAX_HEALTH, 175.0)
         .add(Attributes.ARMOR, 18.0)
         .add(Attributes.ARMOR_TOUGHNESS, 15.0)
         .add(AttributeRegistry.SPELL_RESIST, 15.0)
         .add(ASAttributeRegistry.SPELL_RES_PENETRATION, 5.0)
         .add(Attributes.FOLLOW_RANGE, 48.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 6.0);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @org.jetbrains.annotations.Nullable
   protected SoundEvent getAmbientSound() {
      return (SoundEvent)SoundRegistry.KEEPER_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)SoundRegistry.KEEPER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)SoundRegistry.KEEPER_DEATH.get();
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   public boolean canDisableShield() {
      return true;
   }

   protected void customServerAiStep() {
      this.nextHeightOffsetChangeTick--;
      if (this.nextHeightOffsetChangeTick <= 0) {
         this.nextHeightOffsetChangeTick = 100;
         this.allowedHeightOffset = (float)this.random.triangle(0.5, 6.891);
      }

      LivingEntity livingentity = this.getTarget();
      if (livingentity != null && livingentity.getEyeY() > this.getEyeY() + this.allowedHeightOffset && this.canAttack(livingentity)) {
         Vec3 vec3 = this.getDeltaMovement();
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, (0.3F - vec3.y) * 0.3F, 0.0));
         this.hasImpulse = true;
      }

      super.customServerAiStep();
   }

   public void registerControllers(ControllerRegistrar controllers) {
      super.registerControllers(controllers);
      controllers.add(this.contCastAnimationController);
      controllers.add(this.attackAnimationController);
      controllers.add(this.animationController);
   }

   private PlayState continuousCastPredicate(AnimationState<VoidWanderer> event) {
      AnimationController<VoidWanderer> controller = event.getController();
      if (this.isCasting() && (this.castingSpell == null || this.castingSpell.getSpell().getCastType() == CastType.CONTINUOUS)) {
         if (controller.getAnimationState() == State.STOPPED) {
            controller.forceAnimationReset();
            controller.setAnimation(RawAnimation.begin().then("continous_cast", LoopType.LOOP));
         }

         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState attackPredicate(AnimationState<VoidWanderer> event) {
      AnimationController<VoidWanderer> controller = event.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
         return PlayState.CONTINUE;
      } else {
         return controller.getAnimationState() != State.STOPPED ? PlayState.CONTINUE : PlayState.STOP;
      }
   }

   private PlayState predicate(AnimationState<VoidWanderer> event) {
      event.getController().setAnimation(RawAnimation.begin().then("idle", LoopType.LOOP));
      return PlayState.CONTINUE;
   }

   public void playAnimation(String animationId) {
      try {
         VoidWanderer.AttackType attackType = VoidWanderer.AttackType.valueOf(animationId);
         this.animationToPlay = RawAnimation.begin().thenPlay(attackType.data.animationId);
      } catch (Exception var31) {
         HazenNStuff.LOGGER.error("Entity {} Failed to play animation: {}", this, animationId);
      }
   }

   public boolean isAnimating() {
      return this.attackAnimationController.getAnimationState() != State.STOPPED
         || this.animationController.getAnimationState() != State.STOPPED
         || super.isAnimating();
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static enum AttackType {
      Punch_1(10, "punch_1", 3),
      Punch_2(50, "punch_2", 18, 23, 30),
      Dash(27, "dash", 5, 8, 10, 13, 15, 18, 20),
      Spin(43, "spin", 10, 13, 15, 18, 21, 25, 28, 30, 33, 36),
      Slam(20, "slam", 10);

      public final AttackAnimationData data;

      private AttackType(int lengthInTicks, String animationId, int... attackTimestamps) {
         this.data = new AttackAnimationData(lengthInTicks, animationId, attackTimestamps);
      }
   }
}
