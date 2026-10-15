package net.alshanex.alshanex_familiars.entity;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ShadowEntity extends PathfinderMob implements IMagicSummon, GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ShadowEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
      super(entityType, level);
      this.xpReward = 0;
   }

   public ShadowEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends PathfinderMob>)EntityRegistry.SHADOW_SUMMON.get(), level);
   }

   public void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2F, true));
      this.goalSelector.addGoal(7, new GenericFollowOwnerGoal(this, this::getSummoner, 0.9F, 15.0F, 5.0F, false, 25.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers(new Class[0]));
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
   }

   public boolean isAlliedTo(Entity pEntity) {
      return super.isAlliedTo(pEntity) || this.isAlliedHelper(pEntity);
   }

   public void onRemovedFromLevel() {
      this.onRemovedHelper(this);
      super.onRemovedFromLevel();
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && this.shouldIgnoreDamage(pSource) ? false : super.hurt(pSource, pAmount);
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.discard();
      }
   }

   public boolean doHurtTarget(Entity pEntity) {
      return Utils.doMeleeAttack(this, pEntity, PetSpellRegistry.SUMMON_SHADOW.get().getDamageSource(this, this.getSummoner()));
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.ATTACK_SPEED, 3.8)
         .add(Attributes.ARMOR, 2.0);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return true;
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
   }

   public int getCurrentSwingDuration() {
      return 11;
   }

   public void aiStep() {
      super.aiStep();
      this.updateSwingTime();
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "livingController", 2, state -> {
         if (state.isMoving() && !this.swinging) {
            state.setAnimation(RawAnimation.begin().then("walk", LoopType.LOOP));
            return PlayState.CONTINUE;
         } else if (!state.isMoving() && !this.swinging) {
            state.setAnimation(RawAnimation.begin().then("idle", LoopType.LOOP));
            return PlayState.CONTINUE;
         } else {
            return PlayState.STOP;
         }
      }));
      controllers.add(new AnimationController(this, "attackController", 2, state -> {
         if (this.swinging) {
            state.setAnimation(RawAnimation.begin().then("attack", LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
         } else {
            state.getController().forceAnimationReset();
            return PlayState.STOP;
         }
      }));
   }
}
