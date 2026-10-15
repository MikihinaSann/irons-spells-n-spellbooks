package net.alshanex.alshanex_familiars.entity;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.alshanex.alshanex_familiars.util.familiars.AlshanexFamiliarGoals;
import net.alshanex.familiarslib.advancements.SimpleAdvancementTrigger;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.alshanex.familiarslib.util.HelperMethods;
import net.alshanex.familiarslib.util.familiars.FamiliarGoals.WitherifyGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class NecromancerPetEntity extends AbstractTerrestrianSpellCastingPet {
   public static final ResourceLocation textureResource = new ResourceLocation("alshanex_familiars", "textures/entity/necromancer_pet.png");

   public NecromancerPetEntity(EntityType<? extends AbstractTerrestrianSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.initializeAttackGoal(2);
      this.goalSelector.addGoal(2, new AlshanexFamiliarGoals.UndeadChaosGoal(this, this::getSummoner, 10.0, 100));
      this.goalSelector.addGoal(10, new WitherifyGoal(this));
      this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Piglin.class, true));
   }

   protected WizardAttackGoal createAttackGoal(float min, float max) {
      List<AbstractSpell> attackSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.NECROMANCER_ATTACK_SPELLS);
      List<AbstractSpell> defenseSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.NECROMANCER_DEFENSE_SPELLS);
      List<AbstractSpell> movementSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.NECROMANCER_MOVEMENT_SPELLS);
      List<AbstractSpell> supportSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.NECROMANCER_SUPPORT_SPELLS);
      return new WizardAttackGoal(this, 1.0, 60, 100)
         .setSpells(attackSpells, defenseSpells, movementSpells, supportSpells)
         .setSingleUseSpell((AbstractSpell)SpellRegistry.RAISE_DEAD_SPELL.get(), 2000, 3000, 1, 4)
         .setAllowFleeing(true)
         .setSpellQuality(min, max);
   }

   protected Item getValidTrinket() {
      return (Item)ItemRegistry.NECROMANCER_TRINKET.get();
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 30.0)
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

   protected void triggerAdvancement(ServerPlayer player) {
      ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_NECROMANCER_TRIGGER.get()).trigger(player);
   }
}
