package net.warphan.iss_magicfromtheeast.entity.mobs.jiangshi;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.warphan.iss_magicfromtheeast.ISS_MagicFromTheEast;
import net.warphan.iss_magicfromtheeast.item.curios.RustedCoinsSword;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JiangshiEntity extends AbstractSpellCastingMob implements GeoEntity, IAnimatedAttacker, Enemy {
   private static final EntityDataAccessor<Boolean> DATA_IS_ANIMATING_RISE = SynchedEntityData.defineId(JiangshiEntity.class, EntityDataSerializers.BOOLEAN);
   private int riseAnimTime = 45;
   private final AnimationController<JiangshiEntity> attackController = new AnimationController(this, "attackController", 0, this::attackPredicate);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final RawAnimation animationHop = RawAnimation.begin().thenPlay("hop");
   private final RawAnimation animationIdle = RawAnimation.begin().thenPlay("idle");
   private final RawAnimation animationRise = RawAnimation.begin().thenPlay("rise");
   RawAnimation animationToPlay = null;

   public JiangshiEntity(EntityType<? extends JiangshiEntity> pEntityType, Level plevel) {
      super(pEntityType, plevel);
      this.xpReward = 15;
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
   }

   public JiangshiEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends JiangshiEntity>)MFTEEntityRegistries.JIANGSHI.get(), level);
   }

   protected boolean shouldDropLoot() {
      return super.shouldDropLoot();
   }

   public boolean shouldDropExperience() {
      return super.shouldDropExperience();
   }

   protected BodyRotationControl createBodyControl() {
      return new BodyRotationControl(this);
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float rotateTowards(float pFrom, float pTo, float pMaxDelta) {
            return super.rotateTowards(pFrom, pTo, pMaxDelta * 2.5F);
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

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_AMBIENT;
   }

   public void playAmbientSound() {
      this.playSound(this.getAmbientSound(), 1.0F, Mth.randomBetweenInclusive(this.getRandom(), 5, 10) * 0.1F);
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_DEATH;
   }

   public void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new JiangshiEntity.JiangshiAttackGoal(this, 1.0, 5, 10));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public static Builder prepareAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.ARMOR, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.STEP_HEIGHT, 3.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
         .add(Attributes.SAFE_FALL_DISTANCE, 7.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 1.8F);
   }

   public boolean doHurtTarget(Entity entity) {
      return super.doHurtTarget(entity);
   }

   public boolean hurt(DamageSource source, float amount) {
      return source.is(DamageTypeTags.IS_DROWNING) ? false : super.hurt(source, amount);
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_IS_ANIMATING_RISE, false);
   }

   public boolean isAnimatingRise() {
      return (Boolean)this.entityData.get(DATA_IS_ANIMATING_RISE);
   }

   public void triggerRiseAnimation() {
      this.entityData.set(DATA_IS_ANIMATING_RISE, true);
   }

   public void tick() {
      if (this.isAnimatingRise()) {
         if (this.level().isClientSide) {
            this.clientDiggingParticles(this);
         }

         if (--this.riseAnimTime < 0) {
            this.entityData.set(DATA_IS_ANIMATING_RISE, false);
            this.setXRot(0.0F);
            this.setOldPosAndRot();
         }
      } else {
         super.tick();
      }
   }

   protected void clientDiggingParticles(LivingEntity livingEntity) {
      RandomSource randomSource = livingEntity.getRandom();
      BlockState blockState = livingEntity.getBlockStateOn();
      if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
         for (int i = 0; i < 15; i++) {
            double d0 = livingEntity.getX() + Mth.randomBetween(randomSource, -0.5F, 0.5F);
            double d1 = livingEntity.getY();
            double d2 = livingEntity.getZ() + Mth.randomBetween(randomSource, -2.5F, 0.5F);
            livingEntity.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), d0, d1, d2, 0.0, 0.0, 0.0);
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "movementController", 2, this::movePredicate));
      controllerRegistrar.add(new AnimationController(this, "riseController", 0, this::risePredicate));
      controllerRegistrar.add(this.attackController);
   }

   public double getTick(Object o) {
      return this.tickCount;
   }

   private PlayState movePredicate(AnimationState event) {
      Vec3 motion = this.getDeltaMovement();
      float horizontalSpeed = (float)Math.sqrt(motion.x * motion.x + motion.z * motion.z);
      if (this.isAnimatingRise()) {
         return PlayState.STOP;
      } else {
         if (horizontalSpeed > 0.001F) {
            event.getController().setAnimation(this.animationHop);
            if (horizontalSpeed > 0.5F) {
               event.getController().setAnimationSpeed(this.walkAnimation.speed() * 1.8F);
            } else {
               event.getController().setAnimationSpeed(1.0);
            }
         } else {
            event.getController().setAnimation(this.animationIdle);
         }

         return PlayState.CONTINUE;
      }
   }

   private PlayState risePredicate(AnimationState event) {
      if (!this.isAnimatingRise()) {
         return PlayState.STOP;
      } else {
         if (event.getController().getAnimationState() == State.STOPPED) {
            event.getController().setAnimation(this.animationRise);
         }

         return PlayState.CONTINUE;
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void playAnimation(String animationID) {
      try {
         JiangshiEntity.AttackType attackType = JiangshiEntity.AttackType.valueOf(animationID);
         this.animationToPlay = RawAnimation.begin().thenPlay(attackType.data.animationId);
      } catch (Exception var3) {
         ISS_MagicFromTheEast.LOGGER.error("Entity {} Failed to play animation: {}", this, animationID);
      }
   }

   private PlayState attackPredicate(AnimationState<JiangshiEntity> animationEvent) {
      AnimationController<JiangshiEntity> controller = animationEvent.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   public static enum AttackType {
      MELEE(15, "attack", 5);

      public final AttackAnimationData data;

      private AttackType(int lengthTick, String animationID, int... attackTimeStamp) {
         this.data = new AttackAnimationData(lengthTick, animationID, attackTimeStamp);
      }
   }

   static class JiangshiAttackGoal extends WarlockAttackGoal {
      final JiangshiEntity jiangshi;
      int meleeAnimTimer = 1;
      public JiangshiEntity.AttackType attacks = JiangshiEntity.AttackType.MELEE;

      public JiangshiAttackGoal(JiangshiEntity jiangshi, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
         super(jiangshi, pSpeedModifier, minAttackInterval, maxAttackInterval);
         this.jiangshi = jiangshi;
         this.wantsToMelee = true;
      }

      protected float meleeBias() {
         return 1.0F;
      }

      public boolean isActing() {
         return super.isActing() || this.meleeAnimTimer > 0;
      }

      protected void handleAttackLogic(double distanceSquared) {
         float meleeRange = this.meleeRange();
         float distance = Mth.sqrt((float)distanceSquared);
         this.mob.getLookControl().setLookAt(this.target);
         if (this.meleeAnimTimer > 0) {
            this.forceFaceTarget();
            this.meleeAnimTimer--;
            if (this.attacks.data.isHitFrame(this.meleeAnimTimer)) {
               Vec3 push = this.target.position().subtract(this.mob.position()).normalize().scale(0.1F);
               this.mob.push(push.x, push.y, push.z);
               if (distance <= meleeRange && Utils.hasLineOfSight(this.mob.level, this.mob, this.target, true)) {
                  boolean flag = this.mob.doHurtTarget(this.target);
                  if (flag) {
                     if (this.jiangshi instanceof SummonedJiangshiEntity summonedJiangshi
                        && summonedJiangshi.getSummoner() != null
                        && summonedJiangshi.getSummoner() instanceof ServerPlayer serverPlayer) {
                        RustedCoinsSword RUSTED_SWORD = (RustedCoinsSword)MFTEItemRegistries.RUSTED_COINS_SWORD.get();
                        if (RUSTED_SWORD.isEquippedBy(serverPlayer)) {
                           summonedJiangshi.heal((float)summonedJiangshi.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F);
                        }
                     } else {
                        this.jiangshi.heal((float)this.jiangshi.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.25F);
                     }
                  }
               }
            }
         } else if (this.target != null && !this.target.isDeadOrDying()) {
            this.doMeleeAction();
         } else if (this.meleeAnimTimer == 0) {
            this.resetMeleeAttackInterval(distanceSquared);
            this.meleeAnimTimer = -1;
         } else if (distance < meleeRange) {
            if (this.hasLineOfSight && --this.meleeAttackDelay == 0) {
               this.doMeleeAction();
            } else if (this.meleeAttackDelay < 0) {
               this.resetMeleeAttackInterval(distanceSquared);
            }
         } else if (--this.meleeAttackDelay < 0) {
            this.resetMeleeAttackInterval(distanceSquared);
         }
      }

      private void forceFaceTarget() {
         double d0 = this.target.getX() - this.mob.getX();
         double d1 = this.target.getZ() - this.mob.getZ();
         float yRot = (float)(Mth.atan2(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
         this.mob.setYBodyRot(yRot);
         this.mob.setYHeadRot(yRot);
         this.mob.setYRot(yRot);
      }

      protected void doMeleeAction() {
         if (this.attacks != null) {
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.meleeAnimTimer = this.attacks.data.lengthInTicks;
            PacketDistributor.sendToPlayersTrackingEntity(
               this.jiangshi, new SyncAnimationPacket(this.attacks.toString(), this.jiangshi), new CustomPacketPayload[0]
            );
         }
      }

      protected void doMovement(double distanceSquared) {
         float meleeRange = this.meleeRange();
         if (this.target.isDeadOrDying()) {
            this.mob.getNavigation().stop();
         } else if (distanceSquared > meleeRange * meleeRange) {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier * 1.3F);
         }
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() || this.meleeAnimTimer > 0;
      }

      public void stop() {
         super.stop();
         this.meleeAnimTimer = -1;
      }
   }
}
