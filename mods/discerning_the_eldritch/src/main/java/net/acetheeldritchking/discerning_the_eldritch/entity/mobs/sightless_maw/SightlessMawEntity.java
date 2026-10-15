package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.sightless_maw;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import java.util.List;
import java.util.UUID;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SightlessMawEntity extends AbstractSpellCastingMob implements IMagicSummon, GeoAnimatable, IAnimatedAttacker {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   protected LivingEntity cachedSummoner;
   protected UUID summonerUUID;
   RawAnimation animationToPlay = null;
   private final AnimationController<SightlessMawEntity> animationController = new AnimationController(this, "controller", 0, this::predicate);
   private final AnimationController<SightlessMawEntity> attackAnimationController = new AnimationController(
      this, "attack_controller", 0, this::attackPredicate
   );

   public SightlessMawEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 0;
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
      this.noCulling = true;
   }

   public SightlessMawEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends PathfinderMob>)DTEEntityRegistry.SIGHTLESS_MAW.get(), level);
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float rotateTowards(float from, float to, float maxDelta) {
            return super.rotateTowards(from, to, maxDelta * 2.5F);
         }

         protected boolean resetXRotOnTick() {
            return SightlessMawEntity.this.getTarget() == null;
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

   public void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector
         .addGoal(
            1,
            new GenericAnimatedWarlockAttackGoal(this, 1.5, 5, 10)
               .setMoveset(List.of(new AttackAnimationData(13, "walk_bite", new int[]{6}), new AttackAnimationData(20, "leap_bite", new int[]{16})))
               .setComboChance(3.5F)
               .setMeleeAttackInverval(5, 10)
               .setMeleeBias(1.0F, 1.0F)
               .setMeleeMovespeedModifier(1.0F)
         );
      this.goalSelector.addGoal(7, new GenericFollowOwnerGoal(this, this::getSummoner, 0.9F, 8.0F, 2.0F, false, 50.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.9));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers(new Class[0]));
   }

   public static Builder createAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 5.5)
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.FOLLOW_RANGE, 45.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.45);
   }

   public boolean isAlliedTo(Entity entityIn) {
      return super.isAlliedTo(entityIn) || this.isAlliedHelper(entityIn);
   }

   public void die(DamageSource pDamageSource) {
      this.onDeathHelper();
      super.die(pDamageSource);
   }

   public void onRemovedFromLevel() {
      super.onRemovedFromLevel();
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.ENCHANT, this.getX(), this.getY(), this.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.discard();
      }
   }

   public boolean doHurtTarget(Entity entity) {
      return Utils.doMeleeAttack(this, entity, SpellRegistries.CONJURE_FORSAKE_AID.get().getDamageSource(this, this.getSummoner()));
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return this.shouldIgnoreDamage(pSource) ? false : super.hurt(pSource, pAmount);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(this.animationController);
      controllers.add(this.attackAnimationController);
   }

   private PlayState predicate(AnimationState<SightlessMawEntity> event) {
      if (event.isMoving() && this.animationToPlay == null) {
         event.getController().setAnimation(RawAnimation.begin().then("walking", LoopType.LOOP));
         return PlayState.CONTINUE;
      } else if (!event.isMoving() && this.animationToPlay == null) {
         event.getController().setAnimation(RawAnimation.begin().then("idle", LoopType.LOOP));
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState attackPredicate(AnimationState<SightlessMawEntity> event) {
      AnimationController<SightlessMawEntity> controller = event.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   public void playAnimation(String animationId) {
      try {
         this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
      } catch (Exception var3) {
         IronsSpellbooks.LOGGER.error("Entity {} Failed to play animation: {}", this, animationId);
      }
   }

   public boolean isAnimating() {
      return this.animationController.getAnimationState() != State.STOPPED || super.isAnimating();
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public double getTick(Object object) {
      return this.tickCount;
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.summonerUUID = OwnerHelper.deserializeOwner(pCompound);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      OwnerHelper.serializeOwner(pCompound, this.summonerUUID);
   }
}
