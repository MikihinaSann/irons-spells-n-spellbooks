package net.alshanex.alshanex_familiars.entity;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.SupportMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.FindSupportableTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardSupportGoal;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.Comparator;
import java.util.List;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.alshanex.familiarslib.advancements.SimpleAdvancementTrigger;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.alshanex.familiarslib.util.HelperMethods;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClericPetEntity extends AbstractTerrestrianSpellCastingPet implements SupportMob {
   public static final ResourceLocation textureResource = new ResourceLocation("alshanex_familiars", "textures/entity/cleric_pet.png");
   public GoalSelector supportTargetSelector;
   LivingEntity supportTarget;

   public ClericPetEntity(EntityType<? extends AbstractTerrestrianSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void registerGoals() {
      super.registerGoals();
      List<AbstractSpell> healingSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.CLERIC_HEALING_SPELLS);
      List<AbstractSpell> buffingSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.CLERIC_BUFFING_SPELLS);
      this.goalSelector.addGoal(2, new WizardSupportGoal(this, 1.25, 100, 180).setSpells(healingSpells, buffingSpells));
      this.initializeAttackGoal(2);
      this.targetSelector
         .addGoal(
            6,
            new NearestAttackableTargetGoal<>(
               this, Monster.class, 10, false, false, entity -> entity.getType().is(EntityTypeTags.UNDEAD) && !(entity instanceof ZombieVillager)
            )
         );
      this.supportTargetSelector = new GoalSelector(this.level.getProfilerSupplier());
      this.supportTargetSelector
         .addGoal(
            0,
            new FindSupportableTargetGoal<>(
               this,
               LivingEntity.class,
               true,
               mob -> mob.getHealth() * 1.6F < mob.getMaxHealth() && (mob.getType().is(ModTags.VILLAGE_ALLIES) || mob instanceof Player)
            )
         );
   }

   protected WizardAttackGoal createAttackGoal(float min, float max) {
      List<AbstractSpell> attackSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.CLERIC_ATTACK_SPELLS);
      List<AbstractSpell> defenseSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.CLERIC_DEFENSE_SPELLS);
      List<AbstractSpell> movementSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.CLERIC_MOVEMENT_SPELLS);
      List<AbstractSpell> supportSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.CLERIC_SUPPORT_SPELLS);
      return new WizardAttackGoal(this, 1.25, 50, 75)
         .setSpells(attackSpells, defenseSpells, movementSpells, supportSpells)
         .setSingleUseSpell((AbstractSpell)SpellRegistry.GREATER_HEAL_SPELL.get(), 800, 1000, 1, 1)
         .setAllowFleeing(true)
         .setSpellQuality(min, max);
   }

   protected Item getValidTrinket() {
      return (Item)ItemRegistry.CLERIC_TRINKET.get();
   }

   protected void clericSetGoal(ServerPlayer player) {
      this.setSupportTarget(player);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 50.0)
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
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.HOLY.get()).getTargetingColor(), 2.0F),
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

   @Nullable
   public LivingEntity getSupportTarget() {
      return this.supportTarget;
   }

   public void setSupportTarget(LivingEntity target) {
      this.supportTarget = target;
   }

   public void tick() {
      super.tick();
      if (this.getSummoner() != null && this.tickCount % 40 == 0) {
         ZombieVillager zombieVillager = this.level()
            .getEntitiesOfClass(ZombieVillager.class, this.getBoundingBox().inflate(10.0), entity -> !entity.isConverting())
            .stream()
            .min(Comparator.comparingDouble(this::distanceTo))
            .orElse(null);
         if (zombieVillager != null) {
            this.getLookControl().setLookAt(zombieVillager, 10.0F, 10.0F);
            this.triggerAnim("interact_controller", "interact");
            zombieVillager.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 5, false, false, true));
            zombieVillager.startConverting(this.getOwnerUUID(), 400);
         }
      }
   }

   protected void triggerAdvancement(ServerPlayer player) {
      ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_CLERIC_TRIGGER.get()).trigger(player);
   }
}
