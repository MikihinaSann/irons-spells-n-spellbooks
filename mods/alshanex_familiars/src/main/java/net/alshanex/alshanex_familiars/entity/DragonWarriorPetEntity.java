package net.alshanex.alshanex_familiars.entity;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.List;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.alshanex.alshanex_familiars.util.familiars.AlshanexFamiliarGoals;
import net.alshanex.familiarslib.entity.AbstractMeleeSpellCastingPet;
import net.alshanex.familiarslib.util.HelperMethods;
import net.alshanex.familiarslib.util.familiars.FamiliarGoals.MeleeSpellCastWarlockAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DragonWarriorPetEntity extends AbstractMeleeSpellCastingPet {
   public static final ResourceLocation textureResource = new ResourceLocation("alshanex_familiars", "textures/entity/dragon_warrior_pet.png");
   private int regenTimer = 0;
   private int breathCooldown = 0;

   public DragonWarriorPetEntity(EntityType<? extends AbstractMeleeSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.initializeAttackGoal(2);
      this.goalSelector.addGoal(1, new AlshanexFamiliarGoals.ProtectiveSwapGoal(this, this::getSummoner, 4.0, 10));
   }

   protected WizardAttackGoal createAttackGoal(float min, float max) {
      List<AbstractSpell> attackSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.DRAGON_WARRIOR_ATTACK_SPELLS);
      List<AbstractSpell> defenseSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.DRAGON_WARRIOR_DEFENSE_SPELLS);
      List<AbstractSpell> movementSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.DRAGON_WARRIOR_MOVEMENT_SPELLS);
      List<AbstractSpell> supportSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.DRAGON_WARRIOR_SUPPORT_SPELLS);
      return new MeleeSpellCastWarlockAttackGoal(this, 1.25F, 40, 50)
         .setGapCloserSpell((AbstractSpell)SpellRegistry.SHADOW_SLASH.get(), (int)max)
         .setGapCloserDistance(3.0)
         .setMoveset(List.of(new AttackAnimationData(20, "melee_attack_1", new int[]{6}), new AttackAnimationData(20, "melee_attack_2", new int[]{10})))
         .setComboChance(0.4F)
         .setMeleeAttackInverval(15, 20)
         .setMeleeMovespeedModifier(1.5F)
         .setSpells(attackSpells, defenseSpells, movementSpells, supportSpells)
         .setSingleUseSpell(PetSpellRegistry.MAYHEM.get(), 2000, 2400, (int)min, (int)max)
         .setSpellQuality(min, max);
   }

   protected Item getValidTrinket() {
      return (Item)ItemRegistry.DRAGON_WARRIOR_TRINKET.get();
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.ATTACK_KNOCKBACK, 1.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 3.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.breathCooldown > 0) {
         this.breathCooldown--;
      }

      if (!this.level().isClientSide && this.isAlive()) {
         this.regenTimer++;
         if (!this.isOnBed() && this.regenTimer >= 60) {
            this.regenTimer = 0;
            if (this.getHealth() < this.getMaxHealth()) {
               this.heal(1.0F);
            }
         }
      }

      if (this.tickCount % 5 == 0) {
         if (this.hasEffect(MobEffectRegistry.ECHOING_STRIKES)) {
            if (this.getEffect(MobEffectRegistry.ECHOING_STRIKES).getDuration() < 500) {
               this.addEffect(
                  new MobEffectInstance(
                     MobEffectRegistry.ECHOING_STRIKES, 500 - this.getEffect(MobEffectRegistry.ECHOING_STRIKES).getDuration(), 0, false, false, false
                  )
               );
            }
         } else {
            this.addEffect(new MobEffectInstance(MobEffectRegistry.ECHOING_STRIKES, 500, 0, false, false, false));
         }
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (stack.is(Items.GLASS_BOTTLE)) {
         if (!this.level().isClientSide) {
            if (this.breathCooldown > 0) {
               return InteractionResult.FAIL;
            }

            stack.shrink(1);
            ItemStack dragonBreath = new ItemStack(Items.DRAGON_BREATH);
            if (!player.getInventory().add(dragonBreath)) {
               player.drop(dragonBreath, false);
            }

            this.breathCooldown = 100;
            this.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.0F);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         return super.mobInteract(player, hand);
      }
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
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.ENDER.get()).getTargetingColor(), 2.0F),
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

   public boolean hasEffect(Holder<MobEffect> effect) {
      if (effect.is(MobEffectRegistry.ABYSSAL_SHROUD)) {
         return this.random.nextFloat() <= 0.2;
      } else {
         return effect.is(MobEffects.SLOW_FALLING) ? true : super.hasEffect(effect);
      }
   }

   protected void triggerAdvancement(ServerPlayer player) {
   }

   public boolean shouldAlwaysAnimateLegs() {
      return false;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
   }
}
