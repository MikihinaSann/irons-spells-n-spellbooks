package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ServantsOfEnder;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.GustDefenseGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import java.util.List;
import javax.annotation.Nullable;
import net.hazen.hazennstuff.Registries.HnSItemRegistry;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class ServantsOfEnderEntity extends AbstractSpellCastingMob implements Enemy {
   public ServantsOfEnderEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 15;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new GustDefenseGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(), 3, 6, 100, 250, 1));
      this.goalSelector
         .addGoal(
            3,
            new WizardAttackGoal(this, 1.25, 35, 70)
               .setSpells(
                  List.of((AbstractSpell)SpellRegistry.MAGIC_MISSILE_SPELL.get(), (AbstractSpell)SpellRegistry.MAGIC_ARROW_SPELL.get()),
                  List.of((AbstractSpell)SpellRegistry.ECHOING_STRIKES_SPELL.get()),
                  List.of(),
                  List.of((AbstractSpell)SpellRegistry.MAGIC_MISSILE_SPELL.get())
               )
               .setSingleUseSpell((AbstractSpell)SpellRegistry.EVASION_SPELL.get(), 80, 400, 3, 4)
               .setSpellQuality(0.3F, 0.5F)
               .setDrinksPotions()
         );
      this.goalSelector.addGoal(7, new PatrolNearLocationGoal(this, 30.0F, 1.0));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new WizardRecoverGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Endermite.class, true));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDERMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.ENDERMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMAN_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.STEM_STEP;
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)HnSItemRegistry.ENDERMAN_MASKED.get()));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)HnSItemRegistry.ENDERMAN_CHESTPLATE.get()));
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack((ItemLike)HnSItemRegistry.ENDERMAN_LEGGINGS.get()));
      this.setDropChance(EquipmentSlot.LEGS, 0.0F);
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike)HnSItemRegistry.ENDERMAN_BOOTS.get()));
      this.setDropChance(EquipmentSlot.FEET, 0.0F);
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
