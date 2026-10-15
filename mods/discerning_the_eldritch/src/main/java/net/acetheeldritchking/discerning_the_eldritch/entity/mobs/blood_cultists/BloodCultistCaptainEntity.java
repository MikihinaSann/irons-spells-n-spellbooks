package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.List;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicAcolyteEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicCrusaderEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicSummonerEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one.AscendedOneBoss;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTEServerConfig;
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
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;

public class BloodCultistCaptainEntity extends BloodMageEntity implements Enemy, IAnimatedAttacker {
   RawAnimation animationToPlay = null;
   private final AnimationController<BloodCultistCaptainEntity> meleeController = new AnimationController(this, "keeper_animations", 0, this::predicate);

   public BloodCultistCaptainEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 25;
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float rotateTowards(float from, float to, float maxDelta) {
            return super.rotateTowards(from, to, maxDelta * 2.5F);
         }

         protected boolean resetXRotOnTick() {
            return BloodCultistCaptainEntity.this.getTarget() == null;
         }
      };
   }

   protected MoveControl createMoveControl() {
      return new MoveControl(this) {
         protected float rotlerp(float sourceAngle, float targetAngle, float maximumChange) {
            double x = this.wantedX - this.mob.getX();
            double z = this.wantedZ - this.mob.getZ();
            return x * x + z * z < 0.5 ? sourceAngle : super.rotlerp(sourceAngle, targetAngle, maximumChange * 0.25F);
         }
      };
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get(), 1, 5, 100, 250, 2));
      this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 32.0F, 0.75));
      this.goalSelector
         .addGoal(
            3,
            new GenericAnimatedWarlockAttackGoal(this, 1.5, 25, 40)
               .setMoveset(
                  List.of(
                     new AttackAnimationData(9, "simple_sword_upward_swipe", new int[]{5}),
                     new AttackAnimationData(8, "simple_sword_lunge_stab", new int[]{6}),
                     new AttackAnimationData(10, "simple_sword_stab_alternate", new int[]{8}),
                     new AttackAnimationData(10, "simple_sword_horizontal_cross_swipe", new int[]{8}),
                     new AttackAnimationData(17, "overhead_sword_slam", new int[]{13}),
                     new AttackAnimationData(24, "spin_slash_normal", new int[]{12, 15, 19, 22})
                  )
               )
               .setComboChance(0.8F)
               .setMeleeAttackInverval(10, 15)
               .setMeleeMovespeedModifier(1.5F)
               .setMeleeBias(0.3F, 0.8F)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get(),
                     (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get()
                  ),
                  List.of(
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.HEAL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.CHARGE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.RAY_OF_SIPHONING_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get()),
                  List.of((AbstractSpell)SpellRegistry.HEARTSTOP_SPELL.get())
               )
               .setSingleUseSpell((AbstractSpell)SpellRegistry.ACUPUNCTURE_SPELL.get(), 50, 100, 2, 5)
               .setSpellQuality(1.0F, 1.0F)
               .setDrinksPotions()
         );
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new WizardRecoverGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ApothicCrusaderEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ApothicAcolyteEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ApothicSummonerEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AscendedOneBoss.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, e -> this.isHostileTowards((net.minecraft.world.entity.LivingEntity) e)));
      this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, true));
   }

   protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistry.CULTIST_CHESTPLATE.get()));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack((ItemLike)ItemRegistry.CULTIST_LEGGINGS.get()));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike)ItemRegistry.CULTIST_BOOTS.get()));
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ItemRegistry.MISERY.get()));
      this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)ItemRegistries.RAZOR_SHEATH.get()));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      this.setDropChance(EquipmentSlot.LEGS, 0.0F);
      this.setDropChance(EquipmentSlot.FEET, 0.0F);
      this.setDropChance(EquipmentSlot.MAINHAND, 0.01F);
      this.setDropChance(EquipmentSlot.OFFHAND, 0.45F);
   }

   public boolean isAlliedTo(Entity entityIn) {
      return entityIn instanceof IMagicSummon summon && summon.getSummoner() == this ? true : entityIn.getType().is(DTETags.BLOOD_ALLIES);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, difficulty);
      return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
   }

   public boolean isPersistenceRequired() {
      return true;
   }

   public static Builder createAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 5.5)
         .add(Attributes.ATTACK_KNOCKBACK, 0.5)
         .add(Attributes.MAX_HEALTH, 85.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 3.0)
         .add(Attributes.MOVEMENT_SPEED, 0.15)
         .add(AttributeRegistry.MAX_MANA, 500.0)
         .add(Attributes.SCALE, 1.2);
   }

   public boolean shouldSheathSword() {
      return true;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.meleeController);
      super.registerControllers(controllerRegistrar);
   }

   public void playAnimation(String animationId) {
      this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
   }

   private PlayState predicate(AnimationState<BloodCultistCaptainEntity> animationState) {
      AnimationController<BloodCultistCaptainEntity> controller = animationState.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   public boolean isAnimating() {
      return this.meleeController.getAnimationState() != State.STOPPED || super.isAnimating();
   }

   protected PathNavigation createNavigation(Level level) {
      return new NotIdioticNavigation(this, level);
   }

   public boolean isHostileTowards(LivingEntity pTarget) {
      return !DTEServerConfig.enableBloodCultistAggression
         ? super.isHostileTowards(pTarget)
         : super.isHostileTowards(pTarget) || pTarget.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER) < 1.15;
   }
}
