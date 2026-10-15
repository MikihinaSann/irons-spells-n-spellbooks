package net.acetheeldritchking.aces_spell_utils.entity.mobs;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.spells.fire.BurningDashSpell.BurningDashDirectionOverrideCastData;
import net.acetheeldritchking.aces_spell_utils.utils.ASTags;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class UniqueAbstractSpellCastingMob extends AbstractSpellCastingMob implements GeoEntity, IMagicEntity {
   private static final EntityDataAccessor<Boolean> DATA_CANCEL_CAST = SynchedEntityData.defineId(
      UniqueAbstractSpellCastingMob.class, EntityDataSerializers.BOOLEAN
   );
   private SpellData castingSpell;
   protected AbstractSpell lastCastSpellType = SpellRegistry.none();
   protected AbstractSpell instantCastSpellType = SpellRegistry.none();
   protected boolean cancelCastAnimation = false;
   protected boolean animatingLegs = false;
   private final MagicData playerMagicData = new MagicData(true);
   private boolean recreateSpell;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final AnimationController animationController = new AnimationController(this, "controller", 0, this::predicate);
   private final AnimationController instantCastAnimationController = new AnimationController(this, "instant_cast_controller", 0, this::instantCastPredicate);
   private final AnimationController longCastAnimationController = new AnimationController(this, "long_cast_controller", 0, this::longCastPredicate);
   private final AnimationController contCastAnimationController = new AnimationController(this, "continuous_cast_controller", 0, this::continuousCastPredicate);
   private final AnimationController castingAnimationController = new AnimationController(this, "casting_controller", 0, this::castingPredicate);

   public UniqueAbstractSpellCastingMob(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.playerMagicData.setSyncedData(new SyncedSpellData(this));
      this.noCulling = true;
      this.lookControl = this.createLookControl();
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float rotateTowards(float from, float to, float maxDelta) {
            return super.rotateTowards(from, to, maxDelta * 2.5F);
         }

         protected boolean resetXRotOnTick() {
            return UniqueAbstractSpellCastingMob.this.getTarget() == null;
         }
      };
   }

   public MagicData getMagicData() {
      return this.playerMagicData;
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      super.onSyncedDataUpdated(pKey);
      if (this.level().isClientSide && pKey.id() == DATA_CANCEL_CAST.id()) {
         IronsSpellbooks.LOGGER
            .debug(
               "ASCM.onSyncedDataUpdated.1 this.isCasting:{}, playerMagicData.isCasting:{} isClient:{}",
               new Object[]{this.isCasting(), this.playerMagicData == null ? "null" : this.playerMagicData.isCasting(), this.level().isClientSide()}
            );
         this.cancelCast();
      }
   }

   public void cancelCast() {
      if (this.isCasting()) {
         if (this.level().isClientSide) {
            this.cancelCastAnimation = true;
         } else {
            this.entityData.set(DATA_CANCEL_CAST, !(Boolean)this.entityData.get(DATA_CANCEL_CAST));
         }

         this.castComplete();
      }
   }

   public void castComplete() {
      if (!this.level().isClientSide) {
         if (this.castingSpell != null) {
            this.castingSpell.getSpell().onServerCastComplete(this.level(), this.castingSpell.getLevel(), this, this.playerMagicData, false);
         }
      } else {
         this.playerMagicData.resetCastingState();
      }

      this.castingSpell = null;
   }

   public void setSyncedSpellData(SyncedSpellData syncedSpellData) {
      if (this.level().isClientSide) {
         boolean isCasting = this.playerMagicData.isCasting();
         this.playerMagicData.setSyncedData(syncedSpellData);
         this.castingSpell = this.playerMagicData.getCastingSpell();
         IronsSpellbooks.LOGGER
            .debug(
               "ASCM.setSyncedSpellData playerMagicData:{}, priorIsCastingState:{}, spell:{}", new Object[]{this.playerMagicData, isCasting, this.castingSpell}
            );
         if (this.castingSpell != null) {
            if (!this.playerMagicData.isCasting() && isCasting) {
               this.castComplete();
            } else if (this.playerMagicData.isCasting() && !isCasting) {
               AbstractSpell spell = this.playerMagicData.getCastingSpell().getSpell();
               this.initiateCastSpell(spell, this.playerMagicData.getCastingSpellLevel());
               if (this.castingSpell.getSpell().getCastType() == CastType.INSTANT) {
                  this.instantCastSpellType = this.castingSpell.getSpell();
                  this.castingSpell
                     .getSpell()
                     .onClientPreCast(this.level(), this.castingSpell.getLevel(), this, InteractionHand.MAIN_HAND, this.playerMagicData);
                  this.castComplete();
               }
            }
         }
      }
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.recreateSpell) {
         this.recreateSpell = false;
         SyncedSpellData syncedSpellData = this.playerMagicData.getSyncedData();
         AbstractSpell spell = SpellRegistry.getSpell(syncedSpellData.getCastingSpellId());
         this.initiateCastSpell(spell, syncedSpellData.getCastingSpellLevel());
      }

      if (this.castingSpell != null) {
         this.playerMagicData.handleCastDuration();
         if (this.playerMagicData.isCasting()) {
            this.castingSpell.getSpell().onServerCastTick(this.level(), this.castingSpell.getLevel(), this, this.playerMagicData);
         }

         IronsSpellbooks.LOGGER.debug("ASCM.customServerAiStep.1");
         this.forceLookAtTarget(this.getTarget());
         if (this.playerMagicData.getCastDurationRemaining() <= 0) {
            IronsSpellbooks.LOGGER.debug("ASCM.customServerAiStep.2");
            if (this.castingSpell.getSpell().getCastType() == CastType.LONG || this.castingSpell.getSpell().getCastType() == CastType.INSTANT) {
               IronsSpellbooks.LOGGER.debug("ASCM.customServerAiStep.3");
               this.castingSpell.getSpell().onCast(this.level(), this.castingSpell.getLevel(), this, CastSource.MOB, this.playerMagicData);
            }

            this.castComplete();
         } else if (this.castingSpell.getSpell().getCastType() == CastType.CONTINUOUS && (this.playerMagicData.getCastDurationRemaining() + 1) % 10 == 0) {
            this.castingSpell.getSpell().onCast(this.level(), this.castingSpell.getLevel(), this, CastSource.MOB, this.playerMagicData);
         }
      }
   }

   public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
      IronsSpellbooks.LOGGER
         .debug("ASCM.initiateCastSpell: spellType:{} spellLevel:{}, isClient:{}", new Object[]{spell.getSpellId(), spellLevel, this.level().isClientSide});
      if (spell == SpellRegistry.none()) {
         this.castingSpell = null;
      } else {
         if (this.level().isClientSide) {
            this.cancelCastAnimation = false;
         }

         this.castingSpell = new SpellData(spell, spellLevel);
         if (this.getTarget() != null) {
            this.forceLookAtTarget(this.getTarget());
         }

         if (!this.level().isClientSide && !this.castingSpell.getSpell().checkPreCastConditions(this.level(), spellLevel, this, this.playerMagicData)) {
            IronsSpellbooks.LOGGER
               .debug("ASCM.precastfailed: spellType:{} spellLevel:{}, isClient:{}", new Object[]{spell.getSpellId(), spellLevel, this.level().isClientSide});
            this.castingSpell = null;
         } else {
            if (spell == SpellRegistry.TELEPORT_SPELL.get() || spell == SpellRegistry.FROST_STEP_SPELL.get()) {
               this.setTeleportLocationBehindTarget(10);
            } else if (spell == SpellRegistry.BLOOD_STEP_SPELL.get()) {
               this.setTeleportLocationBehindTarget(3);
            } else if (spell == SpellRegistry.BURNING_DASH_SPELL.get()) {
               this.setBurningDashDirectionData();
            }

            this.playerMagicData
               .initiateCast(
                  this.castingSpell.getSpell(),
                  this.castingSpell.getLevel(),
                  this.castingSpell.getSpell().getEffectiveCastTime(this.castingSpell.getLevel(), this),
                  CastSource.MOB,
                  SpellSelectionManager.MAINHAND
               );
            if (!this.level().isClientSide) {
               this.castingSpell.getSpell().onServerPreCast(this.level(), this.castingSpell.getLevel(), this, this.playerMagicData);
            }
         }
      }
   }

   public boolean isCasting() {
      return this.playerMagicData.isCasting();
   }

   public void setBurningDashDirectionData() {
      this.playerMagicData.setAdditionalCastData(new BurningDashDirectionOverrideCastData());
   }

   private void forceLookAtTarget(LivingEntity target) {
      if (target != null) {
         double d0 = target.getX() - this.getX();
         double d2 = target.getZ() - this.getZ();
         double d1 = target.getEyeY() - this.getEyeY();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         float f = (float)(Mth.atan2(d2, d0) * 180.0F / (float)Math.PI) - 90.0F;
         float f1 = (float)(-(Mth.atan2(d1, d3) * 180.0F / (float)Math.PI));
         this.setXRot(f1 % 360.0F);
         this.setYRot(f % 360.0F);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(this.animationController);
      controllers.add(this.instantCastAnimationController);
      controllers.add(this.longCastAnimationController);
      controllers.add(this.contCastAnimationController);
   }

   private PlayState predicate(AnimationState event) {
      if (event.isMoving()) {
         event.getController().setAnimation(RawAnimation.begin().then("walking", LoopType.LOOP));
         return PlayState.CONTINUE;
      } else if (!event.isMoving()) {
         event.getController().setAnimation(RawAnimation.begin().then("idle", LoopType.LOOP));
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState instantCastPredicate(AnimationState event) {
      if (this.cancelCastAnimation) {
         return PlayState.STOP;
      } else {
         AnimationController controller = event.getController();
         if (this.instantCastSpellType != SpellRegistry.none() && controller.getAnimationState() == State.STOPPED) {
            this.setStartAnimationFromSpell(controller, this.instantCastSpellType);
            this.instantCastSpellType = SpellRegistry.none();
         }

         return PlayState.CONTINUE;
      }
   }

   private PlayState longCastPredicate(AnimationState event) {
      AnimationController controller = event.getController();
      if (!this.cancelCastAnimation
         && (
            controller.getAnimationState() != State.STOPPED
               || this.isCasting() && this.castingSpell != null && this.castingSpell.getSpell().getCastType() == CastType.LONG
         )) {
         if (this.isCasting() && this.castingSpell != null && controller.getAnimationState() == State.STOPPED) {
            this.setStartAnimationFromSpell(controller, this.castingSpell.getSpell());
         }

         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState continuousCastPredicate(AnimationState event) {
      AnimationController controller = event.getController();
      if (!this.cancelCastAnimation
         && (
            controller.getAnimationState() != State.STOPPED
               || this.isCasting() && this.castingSpell != null && this.castingSpell.getSpell().getCastType() == CastType.LONG
         )) {
         if (this.isCasting() && this.castingSpell != null && controller.getAnimationState() == State.STOPPED) {
            this.setStartAnimationFromSpell(controller, this.castingSpell.getSpell());
         }

         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState castingPredicate(AnimationState event) {
      AnimationController controller = event.getController();
      if (this.isCasting()) {
         controller.forceAnimationReset();
         controller.setAnimation(RawAnimation.begin().thenPlay("long_cast"));
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   protected void setStartAnimationFromSpell(AnimationController controller, AbstractSpell spell) {
      spell.getCastStartAnimation().getForMob().ifPresentOrElse(animationBuilder -> {
         controller.forceAnimationReset();
         if (ASUtils.isLongAnimCast(spell)) {
            controller.setAnimation(RawAnimation.begin().then("long_cast", LoopType.PLAY_ONCE));
         } else if (ASUtils.isContAnimCast(spell)) {
            controller.setAnimation(RawAnimation.begin().then("continous_cast", LoopType.PLAY_ONCE));
         } else if (spell == ASUtils.getSpellsFromTag(ASTags.STOMP_LIKE_SPELL)) {
            controller.setAnimation(RawAnimation.begin().then("stomp_cast", LoopType.PLAY_ONCE));
         } else if (spell == ASUtils.getSpellsFromTag(ASTags.SLASH_LIKE_SPELL)) {
            controller.setAnimation(RawAnimation.begin().then("slash_cast", LoopType.PLAY_ONCE));
         } else {
            controller.setAnimation(RawAnimation.begin().then("instant_cast", LoopType.PLAY_ONCE));
         }

         this.lastCastSpellType = spell;
         this.cancelCastAnimation = false;
         this.animatingLegs = false;
      }, () -> this.cancelCastAnimation = true);
   }

   public boolean isAnimating() {
      return this.isCasting()
         || this.longCastAnimationController.getAnimationState() != State.STOPPED
         || this.instantCastAnimationController.getAnimationState() != State.STOPPED
         || this.contCastAnimationController.getAnimationState() != State.STOPPED;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      SyncedSpellData syncedSpellData = new SyncedSpellData(this);
      syncedSpellData.loadNBTData(pCompound, this.level().registryAccess());
      if (syncedSpellData.isCasting()) {
         this.recreateSpell = true;
      }

      this.playerMagicData.setSyncedData(syncedSpellData);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      this.playerMagicData.getSyncedData().saveNBTData(pCompound, this.level().registryAccess());
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_CANCEL_CAST, false);
   }
}
