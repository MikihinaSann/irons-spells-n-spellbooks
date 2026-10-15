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
import net.alshanex.familiarslib.entity.AbstractFlyingSpellCastingPet;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.alshanex.familiarslib.util.HelperMethods;
import net.alshanex.familiarslib.util.familiars.FamiliarGoals.FlyingWizardAttackGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ArchmagePetEntity extends AbstractFlyingSpellCastingPet {
   public static final ResourceLocation textureResource = new ResourceLocation("alshanex_familiars", "textures/entity/archmage_pet.png");

   public ArchmagePetEntity(EntityType<? extends AbstractFlyingSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.initializeAttackGoal(2);
      this.goalSelector.addGoal(3, new AlshanexFamiliarGoals.RerollScrollGoal(this, this::getSummoner, 10, 200));
   }

   protected WizardAttackGoal createAttackGoal(float min, float max) {
      List<AbstractSpell> attackSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.ARCHMAGE_ATTACK_SPELLS);
      List<AbstractSpell> defenseSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.ARCHMAGE_DEFENSE_SPELLS);
      List<AbstractSpell> movementSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.ARCHMAGE_MOVEMENT_SPELLS);
      List<AbstractSpell> supportSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.ARCHMAGE_SUPPORT_SPELLS);
      return new FlyingWizardAttackGoal(this, 1.0, 30, 40)
         .setSpells(attackSpells, defenseSpells, movementSpells, supportSpells)
         .setSingleUseSpell((AbstractSpell)SpellRegistry.RAY_OF_FROST_SPELL.get(), 160, 300, 3, 5)
         .setIsFlying()
         .setAllowFleeing(true)
         .setSpellQuality(min, max);
   }

   protected Item getValidTrinket() {
      return (Item)ItemRegistry.ARCHMAGE_TRINKET.get();
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.FLYING_SPEED, 0.1F)
         .add(Attributes.MOVEMENT_SPEED, 0.2);
   }

   public void tick() {
      super.tick();
      if (this.tickCount % 5 == 0 && !this.level.isClientSide) {
         List<ShulkerBullet> bullets = this.level.getEntitiesOfClass(ShulkerBullet.class, this.getBoundingBox().inflate(10.0));
         if (!bullets.isEmpty()) {
            this.triggerAnim("interact_controller", "interact");

            for (ShulkerBullet bullet : bullets) {
               MagicManager.spawnParticles(this.level(), ParticleTypes.POOF, bullet.getX(), bullet.getY(), bullet.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
               bullet.remove(RemovalReason.DISCARDED);
            }
         }
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
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.ICE.get()).getTargetingColor(), 2.0F),
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
      ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_ARCHMAGE_TRIGGER.get()).trigger(player);
   }
}
