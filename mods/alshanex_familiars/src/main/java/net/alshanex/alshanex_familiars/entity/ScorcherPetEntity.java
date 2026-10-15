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
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.alshanex.familiarslib.util.HelperMethods;
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

public class ScorcherPetEntity extends AbstractTerrestrianSpellCastingPet {
   public static final ResourceLocation textureResource = new ResourceLocation("alshanex_familiars", "textures/entity/scorcher_pet.png");

   public ScorcherPetEntity(EntityType<? extends AbstractTerrestrianSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.initializeAttackGoal(2);
   }

   protected WizardAttackGoal createAttackGoal(float min, float max) {
      List<AbstractSpell> attackSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.SCORCHER_ATTACK_SPELLS);
      List<AbstractSpell> defenseSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.SCORCHER_DEFENSE_SPELLS);
      List<AbstractSpell> movementSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.SCORCHER_MOVEMENT_SPELLS);
      List<AbstractSpell> supportSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.SCORCHER_SUPPORT_SPELLS);
      return new WizardAttackGoal(this, 1.25, 50, 75)
         .setSpells(attackSpells, defenseSpells, movementSpells, supportSpells)
         .setSingleUseSpell(PetSpellRegistry.HIKEN.get(), 400, 500, (int)min, (int)max)
         .setAllowFleeing(true)
         .setSpellQuality(min, max);
   }

   protected Item getValidTrinket() {
      return (Item)ItemRegistry.SCORCHER_TRINKET.get();
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
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

   public boolean isOnFire() {
      return false;
   }

   public void onAddedToLevel() {
      super.onAddedToLevel();
      if (this.getSummoner() != null) {
         MagicManager.spawnParticles(
            this.level,
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.FIRE.get()).getTargetingColor(), 2.0F),
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
      if (this.tickCount % 5 == 0) {
         if (this.getSummoner() != null && HelperMethods.horizontalDistanceSqr(this.getSummoner(), this.position()) < 400.0F) {
            if (this.getSummoner().hasEffect(MobEffects.FIRE_RESISTANCE)) {
               if (this.getSummoner().getEffect(MobEffects.FIRE_RESISTANCE).getDuration() < 500) {
                  this.getSummoner()
                     .addEffect(
                        new MobEffectInstance(
                           MobEffects.FIRE_RESISTANCE, 500 - this.getSummoner().getEffect(MobEffects.FIRE_RESISTANCE).getDuration(), 0, false, false, true
                        )
                     );
               }
            } else {
               this.getSummoner().addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 500, 0, false, false, true));
            }
         }

         this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 0, false, false, true));
      }
   }

   protected void triggerAdvancement(ServerPlayer player) {
      ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_SCORCHER_TRIGGER.get()).trigger(player);
   }
}
