package net.hazen.hazennstuff.Entity.Mobs.Mobs.Blazes.TheInferno;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.hazen.hazennstuff.Entity.Mobs.Mobs.Blazes.CinderousFurnace.CinderousFurnace;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TheInferno extends CinderousFurnace {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(TheInferno.class, EntityDataSerializers.BYTE);
   private final AnimationController<TheInferno> animationController = new AnimationController(this, "controller", 0, this::idlePredicate);

   public TheInferno(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.xpReward = 25;
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(4, new TheInferno.TheInfernoAttackGoal(this));
      this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.goalSelector.addGoal(4, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
   }

   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 240.0)
         .add(Attributes.ARMOR, 16.0)
         .add(AttributeRegistry.SPELL_RESIST, 20.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.BLAZE_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.BLAZE_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.BLAZE_DEATH;
   }

   @Override
   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   @Override
   public boolean isOnFire() {
      return this.isCharged();
   }

   private boolean isCharged() {
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   @Override
   public void setCharged(boolean charged) {
      byte b0 = (Byte)this.entityData.get(DATA_FLAGS_ID);
      if (charged) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.entityData.set(DATA_FLAGS_ID, b0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_FLAGS_ID, (byte)0);
   }

   @Override
   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(this.animationController);
   }

   private PlayState idlePredicate(AnimationState<TheInferno> event) {
      event.getController().setAnimation(RawAnimation.begin().then("animation.the_inferno.idle", LoopType.LOOP));
      return PlayState.CONTINUE;
   }

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Override
   public double getTick(Object object) {
      return this.tickCount;
   }

   static class TheInfernoAttackGoal extends Goal {
      private final TheInferno theInferno;
      private int attackStep;
      private int attackTime;
      private int lastSeen;

      public TheInfernoAttackGoal(TheInferno theInferno) {
         this.theInferno = theInferno;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.theInferno.getTarget();
         return livingentity != null && livingentity.isAlive() && this.theInferno.canAttack(livingentity);
      }

      public void start() {
         this.attackStep = 0;
      }

      public void stop() {
         this.theInferno.setCharged(false);
         this.lastSeen = 0;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         this.attackTime--;
         LivingEntity livingentity = this.theInferno.getTarget();
         if (livingentity != null) {
            boolean flag = this.theInferno.getSensing().hasLineOfSight(livingentity);
            if (flag) {
               this.lastSeen = 0;
            } else {
               this.lastSeen++;
            }

            double d0 = this.theInferno.distanceToSqr(livingentity);
            if (d0 < 4.0) {
               if (!flag) {
                  return;
               }

               if (this.attackTime <= 0) {
                  this.attackTime = 20;
                  this.theInferno.doHurtTarget(livingentity);
               }

               this.theInferno.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0);
            } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
               double d1 = livingentity.getX() - this.theInferno.getX();
               double d2 = livingentity.getY(0.5) - this.theInferno.getY(0.5);
               double d3 = livingentity.getZ() - this.theInferno.getZ();
               if (this.attackTime <= 0) {
                  this.attackStep++;
                  if (this.attackStep == 1) {
                     this.attackTime = 75;
                     this.theInferno.setCharged(true);
                  } else if (this.attackStep <= 10) {
                     this.attackTime = 2;
                  } else {
                     this.attackTime = 125;
                     this.attackStep = 0;
                     this.theInferno.setCharged(false);
                  }

                  if (this.attackStep > 1) {
                     double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5;
                     if (!this.theInferno.isSilent()) {
                        this.theInferno.level().levelEvent((Player)null, 1018, this.theInferno.blockPosition(), 0);
                     }

                     for (int i = 0; i < 1; i++) {
                        Vec3 vec3 = new Vec3(this.theInferno.getRandom().triangle(d1, 1.0 * d4), d2, this.theInferno.getRandom().triangle(d3, 1.0 * d4));
                        SmallFireball smallfireball = new SmallFireball(this.theInferno.level(), this.theInferno, vec3.normalize());
                        smallfireball.setPos(smallfireball.getX(), this.theInferno.getY(0.5) + 0.5, smallfireball.getZ());
                        this.theInferno.level().addFreshEntity(smallfireball);
                     }
                  }
               }

               this.theInferno.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
               this.theInferno.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0);
            }

            super.tick();
         }
      }

      private double getFollowDistance() {
         return this.theInferno.getAttributeValue(Attributes.FOLLOW_RANGE);
      }
   }
}
