package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.PiglinPyromancer;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import java.util.List;
import javax.annotation.Nullable;
import net.hazen.hazennstuff.Registries.HnSItemRegistry;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class PiglinPyromancerEntity extends AbstractSpellCastingMob implements Enemy {
   public PiglinPyromancerEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 15;
   }

   protected void registerGoals() {
      this.goalSelector
         .addGoal(
            4,
            new WizardAttackGoal(this, 1.25, 35, 80)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.FIREBOLT_SPELL.get(),
                     (AbstractSpell)SpellRegistry.FIREBOLT_SPELL.get(),
                     (AbstractSpell)SpellRegistry.MAGIC_MISSILE_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.FIRE_BREATH_SPELL.get()),
                  List.of(),
                  List.of(HnSSpellRegistries.CINDEROUS_STEP.get())
               )
               .setSingleUseSpell((AbstractSpell)SpellRegistry.FIREBALL_SPELL.get(), 80, 350, 4, 5)
               .setDrinksPotions()
         );
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(10, new WizardRecoverGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Hoglin.class, true));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PIGLIN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.PIGLIN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PIGLIN_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.PIGLIN_STEP;
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)HnSItemRegistry.PYROMANCER_BRUTE_HELMET.get()));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)HnSItemRegistry.PYROMANCER_BRUTE_CHESTPLATE.get()));
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 25.0)
         .add(Attributes.FOLLOW_RANGE, 25.0)
         .add(AttributeRegistry.SPELL_POWER, 0.75)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }
}
