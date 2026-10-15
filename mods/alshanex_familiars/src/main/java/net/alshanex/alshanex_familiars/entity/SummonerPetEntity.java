package net.alshanex.alshanex_familiars.entity;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.alshanex.familiarslib.advancements.SimpleAdvancementTrigger;
import net.alshanex.familiarslib.entity.AbstractFlyingSpellCastingPet;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.alshanex.familiarslib.util.HelperMethods;
import net.alshanex.familiarslib.util.familiars.FamiliarGoals.DisableSculkShriekerGoal;
import net.alshanex.familiarslib.util.familiars.FamiliarGoals.FlyingWizardAttackGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class SummonerPetEntity extends AbstractFlyingSpellCastingPet {
   public static final ResourceLocation textureResource = new ResourceLocation("alshanex_familiars", "textures/entity/summoner_pet.png");

   public SummonerPetEntity(EntityType<? extends AbstractFlyingSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.initializeAttackGoal(2);
      this.goalSelector.addGoal(3, new DisableSculkShriekerGoal(this, this::getSummoner, 10));
   }

   protected WizardAttackGoal createAttackGoal(float min, float max) {
      List<AbstractSpell> attackSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.SUMMONER_ATTACK_SPELLS);
      List<AbstractSpell> defenseSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.SUMMONER_DEFENSE_SPELLS);
      List<AbstractSpell> movementSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.SUMMONER_MOVEMENT_SPELLS);
      List<AbstractSpell> supportSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.SUMMONER_SUPPORT_SPELLS);
      return new FlyingWizardAttackGoal(this, 1.0, 80, 120)
         .setSpells(attackSpells, defenseSpells, movementSpells, supportSpells)
         .setSingleUseSpell(PetSpellRegistry.SUMMON_SHADOW.get(), 200, 400, 1, 4)
         .setIsFlying()
         .setAllowFleeing(true)
         .setSpellQuality(min, max);
   }

   protected Item getValidTrinket() {
      return (Item)ItemRegistry.SUMMONER_TRINKET.get();
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.FLYING_SPEED, 0.1F)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
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
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.ELDRITCH.get()).getTargetingColor(), 2.0F),
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

   public void tick() {
      super.tick();
      if (this.getSummoner() != null && this.tickCount % 5 == 0 && HelperMethods.horizontalDistanceSqr(this.getSummoner(), this.position()) < 400.0F) {
         if (this.getSummoner().hasEffect(MobEffects.NIGHT_VISION)) {
            if (this.getSummoner().getEffect(MobEffects.NIGHT_VISION).getDuration() < 500) {
               this.getSummoner()
                  .addEffect(
                     new MobEffectInstance(
                        MobEffects.NIGHT_VISION, 500 - this.getSummoner().getEffect(MobEffects.NIGHT_VISION).getDuration(), 0, false, false, true
                     )
                  );
            }
         } else {
            this.getSummoner().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 500, 0, false, false, true));
         }

         if (this.getSummoner().hasEffect(MobEffects.BLINDNESS)) {
            this.triggerAnim("interact_controller", "interact");
            this.getSummoner().removeEffect(MobEffects.BLINDNESS);
         }

         if (this.getSummoner().hasEffect(MobEffects.DARKNESS)) {
            this.triggerAnim("interact_controller", "interact");
            this.getSummoner().removeEffect(MobEffects.DARKNESS);
         }
      }
   }

   protected void triggerAdvancement(ServerPlayer player) {
      ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_SUMMONER_TRIGGER.get()).trigger(player);
   }
}
