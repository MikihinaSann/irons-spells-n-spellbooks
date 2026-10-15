package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.cultist.CultistEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.entity.mobs.wizards.priest.PriestEntity;
import java.util.List;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistCaptainEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistMageEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistWitchEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.minibosses.blood_matriarch.BloodMatriarchEntity;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTETags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class ApothicSummonerEntity extends NeutralWizard implements Enemy {
   public ApothicSummonerEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 25;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.ELDRITCH_BLAST_SPELL.get(), 1, 5, 100, 250, 1));
      this.goalSelector
         .addGoal(
            3,
            new WizardAttackGoal(this, 1.25, 35, 45)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get(),
                     (AbstractSpell)SpellRegistry.RAISE_DEAD_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SACRIFICE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SUMMON_SWORDS.get()
                  ),
                  List.of(
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.HEAL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ROOT_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get(), SpellRegistries.BOOGIE_WOOGIE.get()),
                  List.of((AbstractSpell)SpellRegistry.ABYSSAL_SHROUD_SPELL.get())
               )
               .setSingleUseSpell(SpellRegistries.CONJURE_FORSAKE_AID.get(), 150, 400, 1, 1)
               .setSpellQuality(1.0F, 1.0F)
               .setDrinksPotions()
         );
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new WizardRecoverGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, KeeperEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PriestEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, CultistEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, BloodCultistCaptainEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, BloodCultistMageEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, BloodCultistWitchEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, BloodMatriarchEntity.class, true));
   }

   public boolean isAlliedTo(Entity entityIn) {
      return entityIn instanceof IMagicSummon summon && summon.getSummoner() == this ? true : entityIn.getType().is(DTETags.APOTHIC_ALLIES);
   }

   protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_ELDRITCH_WARLOCK_MASK.get()));
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_ELDRITCH_WARLOCK_ROBES.get()));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_ELDRITCH_WARLOCK_LEGGINGS.get()));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_ELDRITCH_WARLOCK_GREAVES.get()));
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ItemRegistries.FORLORN_RAPIER.get()));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      this.setDropChance(EquipmentSlot.LEGS, 0.0F);
      this.setDropChance(EquipmentSlot.FEET, 0.0F);
      this.setDropChance(EquipmentSlot.MAINHAND, 0.1F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, difficulty);
      return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
   }

   public static Builder createAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 2.5)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 3.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(AttributeRegistry.MAX_MANA, 350.0);
   }

   protected PathNavigation createNavigation(Level level) {
      return new NotIdioticNavigation(this, level);
   }
}
