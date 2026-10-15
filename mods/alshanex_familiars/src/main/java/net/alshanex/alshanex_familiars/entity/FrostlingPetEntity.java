package net.alshanex.alshanex_familiars.entity;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.List;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.alshanex.familiarslib.advancements.SimpleAdvancementTrigger;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.alshanex.familiarslib.util.HelperMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class FrostlingPetEntity extends AbstractTerrestrianSpellCastingPet {
   public static final ResourceLocation textureResource = new ResourceLocation("alshanex_familiars", "textures/entity/frostling_pet.png");

   public FrostlingPetEntity(EntityType<? extends AbstractTerrestrianSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.initializeAttackGoal(2);
      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level));
   }

   protected WizardAttackGoal createAttackGoal(float min, float max) {
      List<AbstractSpell> attackSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.FROSTLING_ATTACK_SPELLS);
      List<AbstractSpell> defenseSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.FROSTLING_DEFENSE_SPELLS);
      List<AbstractSpell> movementSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.FROSTLING_MOVEMENT_SPELLS);
      List<AbstractSpell> supportSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.FROSTLING_SUPPORT_SPELLS);
      return new WizardAttackGoal(this, 1.25, 40, 60)
         .setSpells(attackSpells, defenseSpells, movementSpells, supportSpells)
         .setSingleUseSpell(PetSpellRegistry.ICE_AGE.get(), 400, 600, (int)min, (int)max)
         .setAllowFleeing(true)
         .setSpellQuality(min, max);
   }

   protected Item getValidTrinket() {
      return (Item)ItemRegistry.FROSTLING_TRINKET.get();
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
   }

   public void onAddedToLevel() {
      super.onAddedToLevel();
      if (this.getSummoner() != null) {
         MagicManager.spawnParticles(
            this.level,
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.ICE.get()).getTargetingColor(), 2.0F),
            this.getX(),
            this.getY() + 0.165F,
            this.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
      }
   }

   protected void triggerAdvancement(ServerPlayer player) {
      ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_FROSTLING_TRIGGER.get()).trigger(player);
   }

   public boolean canFreeze() {
      return false;
   }

   public void tick() {
      super.tick();
      if (this.tickCount % 8 == 0) {
         if (this.getSummoner() != null && HelperMethods.horizontalDistanceSqr(this.getSummoner(), this.position()) < 400.0F) {
            if (this.getSummoner().hasEffect(MobEffectRegistry.CHILLED)) {
               this.getSummoner().removeEffect(MobEffectRegistry.CHILLED);
            }

            this.applyFrostWalkerEffect(this.getSummoner());
            this.getSummoner().setTicksFrozen(0);
            this.getSummoner().setIsInPowderSnow(false);
         }

         this.removeEffect(MobEffectRegistry.CHILLED);
         this.setTicksFrozen(0);
         this.applyFrostWalkerEffect(this);
      }
   }

   private void applyFrostWalkerEffect(LivingEntity entity) {
      if (entity.onGround()) {
         BlockState blockState = Blocks.FROSTED_ICE.defaultBlockState();
         float radius = 2.0F;
         BlockPos blockPos = entity.blockPosition();

         for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-2, -1, -2), blockPos.offset(2, -1, 2))) {
            if (pos.closerToCenterThan(entity.position(), radius)) {
               BlockState currentState = this.level().getBlockState(pos);
               if (currentState.getBlock() == Blocks.WATER
                  && (Integer)currentState.getValue(LiquidBlock.LEVEL) == 0
                  && this.level().isEmptyBlock(pos.above())
                  && currentState.canSurvive(this.level(), pos)
                  && this.level().isUnobstructed(blockState, pos, CollisionContext.empty())) {
                  this.level().setBlockAndUpdate(pos, blockState);
                  this.level().scheduleTick(pos, Blocks.FROSTED_ICE, Mth.nextInt(this.random, 60, 120));
               }
            }
         }
      }
   }
}
