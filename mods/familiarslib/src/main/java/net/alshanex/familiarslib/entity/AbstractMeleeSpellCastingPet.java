package net.alshanex.familiarslib.entity;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import javax.annotation.Nullable;
import net.alshanex.familiarslib.FamiliarsLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;

public abstract class AbstractMeleeSpellCastingPet extends AbstractTerrestrianSpellCastingPet implements IAnimatedAttacker {
   private static final ResourceLocation SPELL_POWER_DAMAGE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("familiarslib", "spell_power_damage_boost");
   RawAnimation animationToPlay = null;
   private final AnimationController<AbstractMeleeSpellCastingPet> meleeController = new AnimationController(this, "melee_animations", 0, this::predicate);

   protected AbstractMeleeSpellCastingPet(EntityType<? extends AbstractSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float rotateTowards(float pFrom, float pTo, float pMaxDelta) {
            return super.rotateTowards(pFrom, pTo, pMaxDelta * 2.5F);
         }

         protected boolean resetXRotOnTick() {
            return AbstractMeleeSpellCastingPet.this.getTarget() == null;
         }
      };
   }

   @Override
   protected MoveControl createMoveControl() {
      return new MoveControl(this) {
         protected float rotlerp(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedZ - this.mob.getZ();
            return d0 * d0 + d1 * d1 < 0.5 ? pSourceAngle : super.rotlerp(pSourceAngle, pTargetAngle, pMaximumChange * 0.25F);
         }
      };
   }

   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.tickCount % 20 == 0) {
         this.updateSpellPowerDamageModifier();
      }
   }

   private void updateSpellPowerDamageModifier() {
      AttributeInstance attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attackDamage != null) {
         double spellPower = this.getAttributeValue(AttributeRegistry.SPELL_POWER);
         attackDamage.removeModifier(SPELL_POWER_DAMAGE_MODIFIER_ID);
         double multiplier = spellPower - 1.0;
         if (multiplier > 0.0) {
            AttributeModifier modifier = new AttributeModifier(SPELL_POWER_DAMAGE_MODIFIER_ID, multiplier, Operation.ADD_MULTIPLIED_BASE);
            attackDamage.addTransientModifier(modifier);
         }
      }
   }

   @Override
   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
   }

   public void playAnimation(String animationId) {
      try {
         this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
      } catch (Exception var3) {
         FamiliarsLib.LOGGER.error("Entity {} Failed to play animation: {}", this, animationId);
      }
   }

   private PlayState predicate(AnimationState<AbstractMeleeSpellCastingPet> animationEvent) {
      AnimationController<AbstractMeleeSpellCastingPet> controller = animationEvent.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.meleeController);
      super.registerControllers(controllerRegistrar);
   }

   @Override
   public boolean isAnimating() {
      return this.meleeController.getAnimationState() != State.STOPPED || super.isAnimating();
   }

   protected PathNavigation createNavigation(Level pLevel) {
      return new NotIdioticNavigation(this, pLevel);
   }
}
