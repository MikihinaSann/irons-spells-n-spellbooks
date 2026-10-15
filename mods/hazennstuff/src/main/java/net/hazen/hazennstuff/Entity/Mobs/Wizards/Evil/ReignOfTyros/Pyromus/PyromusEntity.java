package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Pyromus;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.List;
import javax.annotation.Nullable;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class PyromusEntity extends AbstractSpellCastingMob implements Enemy {
   public PyromusEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 25;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.VOLT_STRIKE_SPELL.get(), 3, 6, 100, 250, 1));
      this.goalSelector
         .addGoal(
            3,
            new WizardAttackGoal(this, 1.25, 50, 75)
               .setSpells(
                  List.of(
                     HnSSpellRegistries.ENERGY_BURST.get(),
                     HnSSpellRegistries.ENERGY_BURST.get(),
                     HnSSpellRegistries.ENERGY_BURST.get(),
                     (AbstractSpell)SpellRegistry.BALL_LIGHTNING_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.LIGHTNING_LANCE_SPELL.get()),
                  List.of(),
                  List.of()
               )
               .setSingleUseSpell((AbstractSpell)SpellRegistry.CHARGE_SPELL.get(), 80, 400, 3, 6)
               .setDrinksPotions()
         );
      this.goalSelector.addGoal(4, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new WizardRecoverGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, true, entity -> !entity.getType().is(ModTags.INFERNAL_ALLIES)));
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ItemRegistry.PYRIUM_STAFF.get()));
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(AttributeRegistry.SPELL_RESIST, 20.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public boolean isAlliedTo(Entity pEntity) {
      return super.isAlliedTo(pEntity) || pEntity.getType().is(ModTags.INFERNAL_ALLIES);
   }
}
