package net.alshanex.familiarslib.entity;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.block.AbstractFamiliarBedBlock;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarBedBlockEntity;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarStorageBlockEntity;
import net.alshanex.familiarslib.data.PlayerFamiliarData;
import net.alshanex.familiarslib.registry.AttachmentRegistry;
import net.alshanex.familiarslib.registry.ComponentRegistry;
import net.alshanex.familiarslib.util.CurioUtils;
import net.alshanex.familiarslib.util.ModTags;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableIntegration;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableSystem;
import net.alshanex.familiarslib.util.consumables.FamiliarFoodComponent;
import net.alshanex.familiarslib.util.familiars.FamiliarAttributesHelper;
import net.alshanex.familiarslib.util.familiars.FamiliarBedHelper;
import net.alshanex.familiarslib.util.familiars.FamiliarDeathStorageHandler;
import net.alshanex.familiarslib.util.familiars.FamiliarGoals;
import net.alshanex.familiarslib.util.familiars.FamiliarHelper;
import net.alshanex.familiarslib.util.familiars.FamiliarManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractSpellCastingPet extends AbstractSpellCastingMob {
   protected static final EntityDataAccessor<Boolean> DATA_IS_SITTING = SynchedEntityData.defineId(AbstractSpellCastingPet.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> DATA_IS_HOUSE = SynchedEntityData.defineId(AbstractSpellCastingPet.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER_UUID = SynchedEntityData.defineId(
      AbstractSpellCastingPet.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Boolean> DATA_IMPOSTOR = SynchedEntityData.defineId(AbstractSpellCastingPet.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_TOTEM = SynchedEntityData.defineId(AbstractSpellCastingPet.class, EntityDataSerializers.BOOLEAN);
   protected LivingEntity cachedSummoner;
   protected boolean movementDisabled = false;
   private boolean lastTrinketState = false;
   private WizardAttackGoal currentAttackGoal;
   private boolean pendingGoalUpdate = false;
   private boolean pendingTrinketState = false;
   private boolean hasAttemptedMigration = false;
   private boolean hasAttemptedConsumableMigration = false;
   private boolean hasInitializedHealth = false;
   public BlockPos housePosition = null;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   protected AbstractSpell lastCastSpellType = SpellRegistry.none();
   public AbstractSpell instantCastSpellType = SpellRegistry.none();
   protected boolean cancelCastAnimation = false;
   protected boolean animatingLegs = false;
   protected final RawAnimation idle = RawAnimation.begin().thenLoop("idle");
   protected final RawAnimation walk = RawAnimation.begin().thenLoop("walk");
   protected final RawAnimation attack = RawAnimation.begin().thenPlay("skill");
   protected final RawAnimation longCast = RawAnimation.begin().thenPlay("long_cast");
   protected final RawAnimation continuousCast = RawAnimation.begin().thenLoop("continuous");
   protected final RawAnimation interact = RawAnimation.begin().thenPlay("interact");
   protected final RawAnimation stomp = RawAnimation.begin().thenPlay("stomp");
   protected final RawAnimation spawn = RawAnimation.begin().thenPlay("spawn");
   protected final RawAnimation sleep = RawAnimation.begin().thenPlayAndHold("sleep");
   protected final AnimationController animationControllerInstantCast = new AnimationController(this, "instant_casting", 0, this::instantCastingPredicate);
   protected final AnimationController animationControllerLongCast = new AnimationController(this, "long_casting", 0, this::longCastingPredicate);
   protected final AnimationController animationControllerOtherCast = new AnimationController(this, "other_casting", 0, this::otherCastingPredicate);

   protected AbstractSpellCastingPet(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void setRandomImpostor() {
      Random rand = new Random();
      if (rand.nextFloat() <= 0.1) {
         this.setIsImpostor(true);
         if (this.getIsImpostor()) {
            this.goalSelector.addGoal(3, new FamiliarGoals.StealItemsWhenNotWatchedGoal(this, 3.0));
         }
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(6, new FamiliarGoals.TeleportToOwnerGoal(this, this::getSummoner, 20.0F));
      this.goalSelector.addGoal(7, new FamiliarGoals.MovementAwareFollowOwnerGoal(this, this::getSummoner, 1.2F, 10.0F, 3.0F, false, Float.MAX_VALUE));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new FamiliarGoals.FindAndUsePetBedGoal(this, 10.0));
      this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(5, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(5, new FamiliarGoals.FamiliarHurtByTargetGoal(this));
      this.targetSelector.addGoal(6, new FamiliarGoals.AlliedFamiliarDefenseGoal(this));
   }

   public float getBaseMaxHealth() {
      AttributeInstance healthAttribute = this.getAttribute(Attributes.MAX_HEALTH);
      return healthAttribute != null ? (float)healthAttribute.getBaseValue() : 50.0F;
   }

   protected abstract WizardAttackGoal createAttackGoal(float var1, float var2);

   protected float[] getOriginalQualityValues() {
      return new float[]{0.1F, 0.3F};
   }

   protected float[] getTrinketQualityValues() {
      return new float[]{0.4F, 0.6F};
   }

   protected void initializeAttackGoal(int goalPriority) {
      float[] originalValues = this.getOriginalQualityValues();
      this.currentAttackGoal = this.createAttackGoal(originalValues[0], originalValues[1]);
      this.goalSelector.addGoal(goalPriority, this.currentAttackGoal);
   }

   private void updateAttackGoal() {
      LivingEntity summoner = this.getSummoner();
      if (summoner != null) {
         boolean hasTrinket = CurioUtils.isWearingCurio(summoner, this.getValidTrinket());
         if (hasTrinket != this.lastTrinketState) {
            if (this.currentAttackGoal != null && this.currentAttackGoal.isActing()) {
               this.pendingGoalUpdate = true;
               this.pendingTrinketState = hasTrinket;
               return;
            }

            this.updateGoalSafely(hasTrinket);
         }
      }
   }

   private void updateGoalSafely(boolean hasTrinket) {
      if (this.currentAttackGoal != null) {
         this.goalSelector.removeGoal(this.currentAttackGoal);
      }

      if (hasTrinket) {
         float[] trinketValues = this.getTrinketQualityValues();
         this.currentAttackGoal = this.createAttackGoal(trinketValues[0], trinketValues[1]);
      } else {
         float[] originalValues = this.getOriginalQualityValues();
         this.currentAttackGoal = this.createAttackGoal(originalValues[0], originalValues[1]);
      }

      this.goalSelector.addGoal(2, this.currentAttackGoal);
      this.lastTrinketState = hasTrinket;
      this.pendingGoalUpdate = false;
   }

   protected abstract Item getValidTrinket();

   public boolean isAlliedTo(Entity pEntity) {
      return super.isAlliedTo(pEntity) || this.isAlliedHelper(pEntity);
   }

   private boolean isAlliedHelper(Entity entity) {
      LivingEntity owner = this.getSummoner();
      if (owner == null) {
         return false;
      } else if (entity.is(this.getSummoner())) {
         return true;
      } else if (entity instanceof IMagicSummon magicSummon) {
         Entity otherOwner = magicSummon.getSummoner();
         return otherOwner != null && (owner == otherOwner || otherOwner.isAlliedTo(otherOwner));
      } else if (entity instanceof OwnableEntity tamableAnimal) {
         LivingEntity otherOwner = tamableAnimal.getOwner();
         return otherOwner != null && (owner == otherOwner || otherOwner.isAlliedTo(otherOwner));
      } else if (entity instanceof AbstractSpellCastingPet pet) {
         LivingEntity otherOwner = pet.getSummoner();
         return otherOwner != null && (owner == otherOwner || otherOwner.isAlliedTo(otherOwner));
      } else if (!(entity instanceof AoeEntity zone)) {
         return false;
      } else {
         Entity otherOwner = zone.getOwner();
         return otherOwner != null && (owner == otherOwner || otherOwner.isAlliedTo(otherOwner) || otherOwner.isAlliedTo(owner));
      }
   }

   public void setOwnerUUID(@Nullable UUID uuid) {
      this.entityData.set(DATA_ID_OWNER_UUID, Optional.ofNullable(uuid));
   }

   public UUID getOwnerUUID() {
      return ((Optional<UUID>)this.entityData.get(DATA_ID_OWNER_UUID)).orElse(null);
   }

   public boolean canUsePortal(boolean allowPassengers) {
      return false;
   }

   public GoalSelector getGoalSelector() {
      return this.goalSelector;
   }

   public void setEnragedStacks(Integer level) {
      FamiliarConsumableSystem.ConsumableData data = FamiliarConsumableIntegration.getConsumableData(this);
      data.setValue(FamiliarConsumableSystem.ConsumableType.ENRAGED, level);
      this.updateConsumableData(data);
   }

   public Integer getEnragedStacks() {
      return FamiliarConsumableIntegration.getCurrentBonus(this, FamiliarConsumableSystem.ConsumableType.ENRAGED);
   }

   public void setArmorStacks(Integer level) {
      FamiliarConsumableSystem.ConsumableData data = FamiliarConsumableIntegration.getConsumableData(this);
      data.setValue(FamiliarConsumableSystem.ConsumableType.ARMOR, level);
      this.updateConsumableData(data);
   }

   public Integer getArmorStacks() {
      return FamiliarConsumableIntegration.getCurrentBonus(this, FamiliarConsumableSystem.ConsumableType.ARMOR);
   }

   public void setHealthStacks(Integer level) {
      FamiliarConsumableSystem.ConsumableData data = FamiliarConsumableIntegration.getConsumableData(this);
      data.setValue(FamiliarConsumableSystem.ConsumableType.HEALTH, level);
      this.updateConsumableData(data);
   }

   public Integer getHealthStacks() {
      return FamiliarConsumableIntegration.getCurrentBonus(this, FamiliarConsumableSystem.ConsumableType.HEALTH);
   }

   public void setIsBlocking(Boolean level) {
      FamiliarConsumableSystem.ConsumableData data = FamiliarConsumableIntegration.getConsumableData(this);
      data.setValue(FamiliarConsumableSystem.ConsumableType.BLOCKING, level ? 1 : 0);
      this.updateConsumableData(data);
   }

   private void updateConsumableData(FamiliarConsumableSystem.ConsumableData data) {
      if (!this.level().isClientSide) {
         FamiliarConsumableSystem.applyAttributeModifiers(this, data);
      }
   }

   public Boolean getIsBlocking() {
      return FamiliarConsumableIntegration.canFamiliarBlock(this);
   }

   public void setTotem(Boolean level) {
      this.entityData.set(DATA_TOTEM, level);
   }

   public Boolean getTotem() {
      return (Boolean)this.entityData.get(DATA_TOTEM);
   }

   public void setIsImpostor(Boolean level) {
      this.entityData.set(DATA_IMPOSTOR, level);
   }

   public Boolean getIsImpostor() {
      return (Boolean)this.entityData.get(DATA_IMPOSTOR);
   }

   public void setSitting(Boolean level) {
      try {
         if (level) {
            FamiliarBedHelper.snapToExactBedPosition(this);
            this.clearMovementGoals();
            this.navigation.stop();
            this.setTarget(null);
            if (this instanceof AbstractFlyingSpellCastingPet) {
               this.setDeltaMovement(Vec3.ZERO);
               this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0);
            }
         } else {
            this.restoreMovementGoals();
            this.navigation.recomputePath();
         }

         this.entityData.set(DATA_IS_SITTING, level);
      } catch (Exception var3) {
         FamiliarsLib.LOGGER.error("Error in setSitting: ", var3);
      }
   }

   public Boolean getIsSitting() {
      return (Boolean)this.entityData.get(DATA_IS_SITTING);
   }

   public Boolean getIsInHouse() {
      return (Boolean)this.entityData.get(DATA_IS_HOUSE);
   }

   public void setIsInHouse(Boolean level, BlockPos housePos) {
      this.entityData.set(DATA_IS_HOUSE, level);
      this.housePosition = level ? housePos : null;
      if (level && housePos != null) {
         this.clearHouseGoals();
         this.addHouseGoals();
         FamiliarsLib.LOGGER.debug("Familiar {} entered house mode at {}", this.getUUID(), housePos);
      } else {
         this.clearHouseGoals();
         FamiliarsLib.LOGGER.debug("Familiar {} exited house mode", this.getUUID());
      }
   }

   public boolean hasAtteptedConsumableMigration() {
      return this.hasAttemptedConsumableMigration;
   }

   public void setHasAttemptedConsumableMigration(boolean bool) {
      this.hasAttemptedConsumableMigration = bool;
   }

   private void addHouseGoals() {
      if (this.housePosition != null) {
         this.goalSelector.addGoal(1, new FamiliarGoals.WanderAroundHouseGoal(this, this.housePosition, 1.0));
         this.goalSelector.addGoal(2, new FamiliarGoals.CasualLookAtPlayerGoal(this, Player.class, 8.0F));
         this.goalSelector.addGoal(3, new FamiliarGoals.CasualRandomLookGoal(this));
         this.goalSelector.addGoal(8, new FamiliarGoals.StayNearHouseGoal(this, this.housePosition));
         this.goalSelector.addGoal(9, new FloatGoal(this));
      }
   }

   private void clearHouseGoals() {
      this.goalSelector
         .getAvailableGoals()
         .removeIf(
            goal -> goal.getGoal() instanceof FamiliarGoals.WanderAroundHouseGoal
               || goal.getGoal() instanceof FamiliarGoals.StayNearHouseGoal
               || goal.getGoal() instanceof FamiliarGoals.CasualLookAtPlayerGoal
               || goal.getGoal() instanceof FamiliarGoals.CasualRandomLookGoal
         );
   }

   public int getHouseMaxDistance() {
      if (this.housePosition != null && this.level() != null) {
         return this.level().getBlockEntity(this.housePosition) instanceof AbstractFamiliarStorageBlockEntity storageEntity
            ? storageEntity.getMaxDistance()
            : 25;
      } else {
         return 25;
      }
   }

   protected void clearMovementGoals() {
      this.movementDisabled = true;
      this.navigation.stop();
      if (this instanceof AbstractFlyingSpellCastingPet) {
         this.setDeltaMovement(Vec3.ZERO);
         this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0);
      }
   }

   protected void restoreMovementGoals() {
      this.movementDisabled = false;
      this.navigation.recomputePath();
      if (this instanceof AbstractFlyingSpellCastingPet) {
         this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0);
      }
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      } else {
         this.setSitting(false);
         return super.hurt(pSource, pAmount);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.getEntity() != null && this.isAlliedTo(source.getEntity()) ? true : super.isInvulnerableTo(source);
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_ID_OWNER_UUID, Optional.empty());
      pBuilder.define(DATA_IS_SITTING, false);
      pBuilder.define(DATA_IS_HOUSE, false);
      pBuilder.define(DATA_IMPOSTOR, false);
      pBuilder.define(DATA_TOTEM, false);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      super.onSyncedDataUpdated(pKey);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      if (this.getOwnerUUID() != null) {
         pCompound.putUUID("ownerUUID", this.getOwnerUUID());
      } else {
         pCompound.putString("ownerUUID", "null");
      }

      float currentHealth = this.getHealth();
      pCompound.putFloat("currentHealth", currentHealth);
      pCompound.putFloat("baseMaxHealth", this.getBaseMaxHealth());
      FamiliarsLib.LOGGER
         .debug(
            "Saving health for familiar {}: current={}, max={}, base={}",
            new Object[]{this.getUUID(), currentHealth, this.getMaxHealth(), this.getBaseMaxHealth()}
         );
      pCompound.putBoolean("Sitting", this.getIsSitting());
      pCompound.putBoolean("isImpostor", this.getIsImpostor());
      pCompound.putBoolean("hasTotem", this.getTotem());
      pCompound.putBoolean("lastTrinketState", this.lastTrinketState);
      pCompound.putBoolean("pendingGoalUpdate", this.pendingGoalUpdate);
      pCompound.putBoolean("pendingTrinketState", this.pendingTrinketState);
      pCompound.putBoolean("hasAttemptedMigration", this.hasAttemptedMigration);
      pCompound.putBoolean("hasAttemptedConsumableMigration", this.hasAttemptedConsumableMigration);
      pCompound.putBoolean("hasInitializedHealth", this.hasInitializedHealth);
      pCompound.putBoolean("isInHouse", this.getIsInHouse());
      if (this.housePosition != null) {
         pCompound.putLong("housePosition", this.housePosition.asLong());
      }

      FamiliarConsumableIntegration.saveConsumableData(this, pCompound);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.contains("ownerUUID")) {
         String ownerUUIDString = pCompound.getString("ownerUUID");
         if (!"null".equals(ownerUUIDString)) {
            this.setOwnerUUID(pCompound.getUUID("ownerUUID"));
         } else {
            this.setOwnerUUID(null);
         }
      }

      FamiliarConsumableIntegration.loadConsumableData(this, pCompound);
      if (!pCompound.isEmpty() && (pCompound.contains("currentHealth") || pCompound.contains("ownerUUID") || pCompound.contains("Health"))) {
         this.hasInitializedHealth = pCompound.getBoolean("hasInitializedHealth");
         float savedHealth;
         if (pCompound.contains("currentHealth")) {
            savedHealth = pCompound.getFloat("currentHealth");
         } else {
            savedHealth = this.getHealth();
            if (savedHealth <= 0.0F) {
               savedHealth = this.getMaxHealth();
            }
         }

         FamiliarsLib.LOGGER.debug("Loading health for familiar {}: saved health = {}", this.getUUID(), savedHealth);
         if (!this.level().isClientSide) {
            FamiliarConsumableIntegration.applyConsumableModifiers(this);
            float newMaxHealth = this.getMaxHealth();
            float restoredHealth = Math.min(savedHealth, newMaxHealth);
            this.setHealth(restoredHealth);
            this.hasInitializedHealth = true;
            FamiliarsLib.LOGGER.debug("Restored health: {}/{}", restoredHealth, newMaxHealth);
         } else {
            this.getPersistentData().putFloat("pendingHealth", savedHealth);
         }
      } else {
         this.hasInitializedHealth = false;
      }

      if (pCompound.contains("Sitting")) {
         this.setSitting(pCompound.getBoolean("Sitting"));
         this.movementDisabled = pCompound.getBoolean("Sitting");
      }

      if (pCompound.contains("isImpostor")) {
         this.setIsImpostor(pCompound.getBoolean("isImpostor"));
      }

      if (pCompound.contains("hasTotem")) {
         this.setTotem(pCompound.getBoolean("hasTotem"));
      }

      this.lastTrinketState = pCompound.getBoolean("lastTrinketState");
      this.pendingGoalUpdate = pCompound.getBoolean("pendingGoalUpdate");
      this.pendingTrinketState = pCompound.getBoolean("pendingTrinketState");
      if (pCompound.contains("hasAttemptedMigration")) {
         this.hasAttemptedMigration = pCompound.getBoolean("hasAttemptedMigration");
      }

      if (pCompound.contains("isInHouse")) {
         boolean inHouse = pCompound.getBoolean("isInHouse");
         BlockPos housePos = null;
         if (pCompound.contains("housePosition")) {
            housePos = BlockPos.of(pCompound.getLong("housePosition"));
         }

         this.setIsInHouse(inHouse, housePos);
      }
   }

   public LivingEntity getSummoner() {
      return OwnerHelper.getAndCacheOwner(this.level(), this.cachedSummoner, this.getOwnerUUID());
   }

   public void die(DamageSource pDamageSource) {
      if (!this.level().isClientSide) {
         this.onDeathHelper();
      }

      if (this.getIsInHouse() && this.housePosition != null) {
         FamiliarDeathStorageHandler.notifyFamiliarDeath(this);
      }

      super.die(pDamageSource);
   }

   private void onDeathHelper() {
      if (this instanceof LivingEntity) {
         Level level = super.level;
         Component deathMessage = this.getCombatTracker().getDeathMessage();
         if (!level.isClientSide && level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getSummoner() instanceof ServerPlayer player) {
            player.sendSystemMessage(deathMessage);
         }
      }
   }

   protected MoveControl createMoveControl() {
      return new MoveControl(this) {
         protected float rotlerp(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedZ - this.mob.getZ();
            return d0 * d0 + d1 * d1 < 0.5 ? pSourceAngle : super.rotlerp(pSourceAngle, pTargetAngle, pMaximumChange * 0.25F);
         }
      };
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         this.noCulling = this.isAnimating();
      }

      if (!this.level().isClientSide && this.getIsInHouse()) {
         FamiliarHelper.handleHouseBehavior(this);
      }

      if (!this.level().isClientSide && !this.hasAttemptedMigration && this.getSummoner() != null) {
         if (this.getSummoner() instanceof ServerPlayer serverPlayer) {
            FamiliarHelper.attemptLegacyMigration(serverPlayer, this);
            this.hasAttemptedMigration = true;
         } else {
            UUID ownerUUID = this.getOwnerUUID();
            if (ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
               ServerPlayer serverPlayer = serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
               if (serverPlayer != null) {
                  FamiliarHelper.attemptLegacyMigration(serverPlayer, this);
                  this.hasAttemptedMigration = true;
               }
            }
         }
      }

      if (this.pendingGoalUpdate && !this.level.isClientSide && (this.currentAttackGoal == null || !this.currentAttackGoal.isActing())) {
         this.updateGoalSafely(this.pendingTrinketState);
      }

      if (this.tickCount % 20 == 0 && !this.level.isClientSide) {
         this.updateAttackGoal();
      }

      if (this.tickCount % 10 == 0
         && !this.level.isClientSide
         && (this.isOnBed() || this.getIsInHouse() || this.getMovementDisabled())
         && this.currentAttackGoal.isActing()) {
         this.currentAttackGoal.stop();
      }

      if (this.tickCount % 600 == 0 && !this.level.isClientSide && (this.getSummoner() != null || this.getOwnerUUID() != null)) {
         this.setPersistenceRequired();
      }

      if (this.getSummoner() != null
         && this.getTarget() != null
         && this.getTarget() instanceof AbstractSpellCastingPet familiar
         && this.tickCount % 10 == 0
         && familiar.getSummoner() != null
         && familiar.getSummoner().is(this.getSummoner())) {
         this.setTarget(null);
      }

      if (this.getTarget() != null && this.getIsInHouse() && this.tickCount % 10 == 0) {
         if (this.isHunterPet()) {
            if (this.getTarget() instanceof Mob mob) {
               if (mob.getTarget() != null && this.isAlliedTo(mob.getTarget()) && !mob.getTarget().getType().is(ModTags.HUNTER_CANNOT_ATTACK_IN_HOME)) {
                  this.setTarget(mob);
               } else {
                  this.setTarget(null);
               }
            } else {
               this.setTarget(null);
            }
         } else {
            this.setTarget(null);
         }
      }

      if (this.getSummoner() != null && this.tickCount % 10 == 0 && this.getHealth() > 0.0F) {
         if (CurioUtils.isWearingFamiliarSpellbook(this.getSummoner())) {
            FamiliarAttributesHelper.applyAttributes(this);
         } else {
            FamiliarAttributesHelper.removeAttributes(this);
         }
      }

      if (this.getSummoner() != null && this.tickCount % 40 == 0 && !this.level().isClientSide && this.getSummoner() instanceof ServerPlayer serverPlayer) {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)serverPlayer.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         if (familiarData.hasFamiliar(this.getUUID())) {
            FamiliarManager.updateFamiliarData(this);
         } else {
            FamiliarsLib.LOGGER.debug("Familiar {} not in player data, skipping update", this.getUUID());
         }
      }
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public boolean canBeLeashed() {
      return false;
   }

   public void checkDespawn() {
      if (this.getSummoner() == null && this.getOwnerUUID() == null) {
         super.checkDespawn();
      } else {
         this.setPersistenceRequired();
      }
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return this.getSummoner() == null && this.getOwnerUUID() == null ? super.removeWhenFarAway(distanceToClosestPlayer) : false;
   }

   public boolean requiresCustomPersistence() {
      return this.getSummoner() != null || this.getOwnerUUID() != null;
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (this.getIsInHouse() && this.housePosition != null) {
         if (this.level().getBlockEntity(this.housePosition) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
            if (!storageEntity.isOwner((ServerPlayer)player)) {
               return InteractionResult.FAIL;
            } else {
               if (!this.level().isClientSide) {
                  InteractionResult consumableResult = FamiliarConsumableIntegration.handleConsumableInteraction(this, player, itemstack);
                  if (consumableResult != InteractionResult.PASS) {
                     return consumableResult;
                  }

                  if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                     float healAmount = 4.0F;
                     if (itemstack.has(ComponentRegistry.FAMILIAR_FOOD)) {
                        healAmount = ((FamiliarFoodComponent)itemstack.get(ComponentRegistry.FAMILIAR_FOOD)).healing();
                     }

                     this.heal(healAmount);
                     itemstack.consume(1, player);
                     FamiliarHelper.spawnEatingParticles(this);
                     this.gameEvent(GameEvent.EAT);
                     return InteractionResult.sidedSuccess(this.level().isClientSide());
                  }
               }

               return InteractionResult.PASS;
            }
         } else {
            return InteractionResult.FAIL;
         }
      } else if (this.getSummoner() != null && this.getSummoner().is(player) && itemstack.is(Items.STICK)) {
         this.setSitting(!this.getIsSitting());
         return InteractionResult.SUCCESS;
      } else {
         if (!this.level().isClientSide) {
            if (this.getOwnerUUID() != null) {
               if (this.getOwnerUUID().equals(player.getUUID())) {
                  if (this.getSummoner() instanceof ServerPlayer serverPlayer) {
                     FamiliarHelper.attemptLegacyMigration(serverPlayer, this);
                  }

                  this.triggerAnim("interact_controller", "interact");
                  InteractionResult consumableResultx = FamiliarConsumableIntegration.handleConsumableInteraction(this, player, itemstack);
                  if (consumableResultx != InteractionResult.PASS) {
                     return consumableResultx;
                  }

                  if (itemstack.is(ModTags.BERRY)) {
                     itemstack.shrink(1);
                     Optional<Item> firstItem = BuiltInRegistries.ITEM
                        .getTag(ModTags.FRAGMENTS)
                        .map(holders -> (Item)((Holder)holders.iterator().next()).value())
                        .or(() -> Optional.empty());
                     ItemStack itemStack = firstItem.<ItemStack>map(item -> new ItemStack(item, 1)).orElse(ItemStack.EMPTY);
                     this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack));
                     if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(4.0F);
                        this.gameEvent(GameEvent.EAT);
                     }

                     return InteractionResult.sidedSuccess(this.level().isClientSide());
                  }

                  if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                     float healAmount = 4.0F;
                     if (itemstack.has(ComponentRegistry.FAMILIAR_FOOD)) {
                        healAmount = ((FamiliarFoodComponent)itemstack.get(ComponentRegistry.FAMILIAR_FOOD)).healing();
                     }

                     this.heal(healAmount);
                     itemstack.consume(1, player);
                     FamiliarHelper.spawnEatingParticles(this);
                     this.gameEvent(GameEvent.EAT);
                     return InteractionResult.sidedSuccess(this.level().isClientSide());
                  }

                  if (this.isHunterPet() && itemstack != null && itemstack != ItemStack.EMPTY) {
                     Item heldItem = itemstack.getItem();
                     AABB searchArea = this.getBoundingBox().inflate(20.0);

                     for (Entity entity : this.level()
                        .getEntities(
                           this,
                           searchArea,
                           entityx -> entityx instanceof LivingEntity
                              && FamiliarHelper.canDropItem((ServerLevel)this.level(), entityx, heldItem)
                              && !entityx.getType().is(ModTags.HUNTER_CANNOT_MARK)
                        )) {
                        if (entity instanceof LivingEntity livingEntity) {
                           livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, false, true));
                        }
                     }

                     return super.mobInteract(player, hand);
                  }
               }
            } else if (itemstack.is(ModTags.FAMILIAR_TAMING)) {
               itemstack.consume(1, player);
               this.tryToTame(player);
               return InteractionResult.SUCCESS;
            }
         }

         return super.mobInteract(player, hand);
      }
   }

   protected boolean isHunterPet() {
      return false;
   }

   protected void clericSetGoal(ServerPlayer player) {
   }

   protected boolean isFood(ItemStack item) {
      return item.has(ComponentRegistry.FAMILIAR_FOOD);
   }

   protected void tryToTame(Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)serverPlayer.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         if (!familiarData.canTameMoreFamiliars()) {
            serverPlayer.connection
               .send(
                  new ClientboundSetActionBarTextPacket(
                     Component.translatable("message.familiarslib.limit_familiars", new Object[]{10}).withStyle(ChatFormatting.RED)
                  )
               );
         } else {
            if (this.random.nextInt(10) <= 3) {
               FamiliarHelper.spawnTamingParticles(true, this);
               this.setTarget(null);
               this.setOwnerUUID(player.getUUID());
               boolean success = FamiliarManager.handleFamiliarTaming(this, serverPlayer);
               if (success) {
                  this.setPersistenceRequired();
                  this.triggerAdvancement(serverPlayer);
                  int remainingSlots = familiarData.getRemainingFamiliarSlots();
                  serverPlayer.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable("message.familiarslib.tamed_successfully", new Object[]{remainingSlots}).withStyle(ChatFormatting.WHITE)
                        )
                     );
                  serverPlayer.level()
                     .playSound(
                        null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5F, 2.0F
                     );
                  this.clericSetGoal(serverPlayer);
               } else {
                  FamiliarHelper.spawnTamingParticles(false, this);
               }
            } else {
               FamiliarHelper.spawnTamingParticles(false, this);
            }
         }
      }
   }

   protected void triggerAdvancement(ServerPlayer player) {
   }

   public void onAddedToLevel() {
      super.onAddedToLevel();
      this.triggerAnim("spawn_controller", "spawn");
   }

   public void onRemovedFromLevel() {
      super.onRemovedFromLevel();
      if (!this.level.isClientSide && this.getSummoner() instanceof ServerPlayer serverPlayer) {
         FamiliarAttributesHelper.handleFamiliarDismissed(serverPlayer, this);
      }

      if (!this.level.isClientSide) {
         FamiliarConsumableIntegration.removeConsumableModifiers(this);
      }

      if (!this.level.isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
      }
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
      SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData);
      if (reason == MobSpawnType.COMMAND) {
         FamiliarsLib.LOGGER.debug("Finalizing spawn for command-summoned familiar {}", this.getUUID());
         if (!level.isClientSide()) {
            FamiliarConsumableIntegration.applyConsumableModifiers(this);
            if (this.getHealth() <= 0.0F) {
               this.setHealth(this.getMaxHealth());
               FamiliarsLib.LOGGER
                  .debug("Set health for command-spawned familiar {}: {}/{}", new Object[]{this.getUUID(), this.getHealth(), this.getMaxHealth()});
            }

            this.hasInitializedHealth = true;
            this.setPersistenceRequired();
         }
      }

      return result;
   }

   public int getConsumableBonus(FamiliarConsumableSystem.ConsumableType type) {
      return FamiliarConsumableIntegration.getCurrentBonus(this, type);
   }

   public int getMaxUsableConsumableTier(FamiliarConsumableSystem.ConsumableType type) {
      return FamiliarConsumableIntegration.getMaxUsableTier(this, type);
   }

   public boolean canExecuteGoalsInHouse() {
      if (!this.getIsInHouse() || this.housePosition == null) {
         return true;
      } else if (this.level() != null && !this.level().isClientSide) {
         return !(this.level().getBlockEntity(this.housePosition) instanceof AbstractFamiliarStorageBlockEntity storageEntity)
            ? false
            : !storageEntity.isStoreMode() && storageEntity.canFamiliarsUseGoals();
      } else {
         return false;
      }
   }

   public boolean getMovementDisabled() {
      return this.movementDisabled;
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
   }

   public void setSyncedSpellData(SyncedSpellData syncedSpellData) {
      if (this.level().isClientSide) {
         MagicData playerMagicData = this.getMagicData();
         boolean wasCasting = playerMagicData.isCasting();
         playerMagicData.setSyncedData(syncedSpellData);
         SpellData castingSpell = playerMagicData.getCastingSpell();
         if (castingSpell != null) {
            if (playerMagicData.isCasting() && !wasCasting) {
               AbstractSpell spell = castingSpell.getSpell();
               this.initiateCastSpell(spell, playerMagicData.getCastingSpellLevel());
               if (castingSpell.getSpell().getCastType() == CastType.INSTANT) {
                  this.instantCastSpellType = castingSpell.getSpell();
                  castingSpell.getSpell().onClientPreCast(this.level(), castingSpell.getLevel(), this, InteractionHand.MAIN_HAND, playerMagicData);
                  this.castComplete();
               }
            } else if (!playerMagicData.isCasting() && wasCasting) {
               this.castComplete();
            }
         }
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.animationControllerInstantCast);
      controllerRegistrar.add(this.animationControllerLongCast);
      controllerRegistrar.add(this.animationControllerOtherCast);
      controllerRegistrar.add(new AnimationController(this, "idle", 0, this::idlePredicate));
      controllerRegistrar.add(new AnimationController(this, "sleep", 0, this::sleepPredicate));
      controllerRegistrar.add(new AnimationController(this, "interact_controller", state -> PlayState.STOP).triggerableAnim("interact", this.interact));
      controllerRegistrar.add(new AnimationController(this, "spawn_controller", state -> PlayState.STOP).triggerableAnim("spawn", this.spawn));
      controllerRegistrar.add(new AnimationController(this, "block_controller", state -> PlayState.STOP).triggerableAnim("block", this.attack));
   }

   protected PlayState idlePredicate(AnimationState event) {
      if (this.isAnimating()) {
         return PlayState.STOP;
      } else if (event.isMoving()) {
         event.getController().setAnimation(this.walk);
         return PlayState.CONTINUE;
      } else if (!event.isMoving()) {
         event.getController().setAnimation(this.idle);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   protected PlayState sleepPredicate(AnimationState event) {
      boolean shouldPlaySleepAnimation = this.getIsSitting() && this.isOnBed();
      if (shouldPlaySleepAnimation) {
         event.getController().setAnimation(this.sleep);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   public boolean isOnBed() {
      BlockPos petPos = this.blockPosition();

      for (int x = -1; x <= 1; x++) {
         for (int y = -1; y <= 0; y++) {
            for (int z = -1; z <= 1; z++) {
               BlockPos checkPos = petPos.offset(x, y, z);
               if (this.level().getBlockState(checkPos).getBlock() instanceof AbstractFamiliarBedBlock
                  && this.level().getBlockEntity(checkPos) instanceof AbstractFamiliarBedBlockEntity petBed) {
                  return petBed.isPositionCorrectForSleeping(this.position());
               }
            }
         }
      }

      return false;
   }

   protected PlayState instantCastingPredicate(AnimationState event) {
      if (this.cancelCastAnimation) {
         return PlayState.STOP;
      } else {
         AnimationController controller = event.getController();
         if (this.instantCastSpellType != SpellRegistry.none() && controller.getAnimationState() == State.STOPPED) {
            this.setStartAnimationFromSpell(controller, this.instantCastSpellType);
            this.instantCastSpellType = SpellRegistry.none();
         }

         return PlayState.CONTINUE;
      }
   }

   protected PlayState longCastingPredicate(AnimationState event) {
      AnimationController controller = event.getController();
      AbstractSpell castingSpell = null;
      if (this.isCasting()) {
         MagicData magicData = this.getMagicData();
         SpellData spellData = magicData.getCastingSpell();
         if (spellData != null) {
            castingSpell = spellData.getSpell();
         }
      }

      if (!this.cancelCastAnimation
         && (controller.getAnimationState() != State.STOPPED || this.isCasting() && castingSpell != null && castingSpell.getCastType() == CastType.LONG)) {
         if (this.isCasting() && controller.getAnimationState() == State.STOPPED) {
            if (castingSpell != null) {
               this.setStartAnimationFromSpell(controller, castingSpell);
            }
         } else if (!this.isCasting() && this.lastCastSpellType.getCastType() == CastType.LONG) {
            this.setFinishAnimationFromSpell(controller, this.lastCastSpellType);
         }

         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   protected PlayState otherCastingPredicate(AnimationState event) {
      if (this.cancelCastAnimation) {
         return PlayState.STOP;
      } else {
         AnimationController controller = event.getController();
         AbstractSpell castingSpell = null;
         if (this.isCasting()) {
            MagicData magicData = this.getMagicData();
            SpellData spellData = magicData.getCastingSpell();
            if (spellData != null) {
               castingSpell = spellData.getSpell();
            }
         }

         if (this.isCasting() && castingSpell != null && controller.getAnimationState() == State.STOPPED) {
            if (castingSpell.getCastType() == CastType.CONTINUOUS) {
               this.setStartAnimationFromSpell(controller, castingSpell);
            }

            return PlayState.CONTINUE;
         } else {
            return this.isCasting() ? PlayState.CONTINUE : PlayState.STOP;
         }
      }
   }

   protected void setFinishAnimationFromSpell(AnimationController controller, AbstractSpell spell) {
      controller.forceAnimationReset();
      this.lastCastSpellType = SpellRegistry.none();
   }

   protected void setStartAnimationFromSpell(AnimationController controller, AbstractSpell spell) {
      controller.forceAnimationReset();
      if (spell.getCastType() == CastType.CONTINUOUS) {
         controller.setAnimation(this.continuousCast);
      } else if (spell == SpellRegistry.STOMP_SPELL.get()) {
         controller.setAnimation(this.stomp);
      } else if (spell.getCastTime(spell.getMinLevel()) > 15) {
         controller.setAnimation(this.longCast);
      } else {
         controller.setAnimation(this.attack);
      }

      this.lastCastSpellType = spell;
      this.cancelCastAnimation = false;
      this.animatingLegs = false;
   }

   public boolean isAnimating() {
      boolean isSleeping = this.getIsSitting() && this.isOnBed();
      return this.isCasting()
         || this.animationControllerLongCast.getAnimationState() != State.STOPPED
         || this.animationControllerInstantCast.getAnimationState() != State.STOPPED
         || isSleeping;
   }

   public boolean shouldAlwaysAnimateHead() {
      return false;
   }

   public boolean shouldPointArmsWhileCasting() {
      return false;
   }

   public boolean shouldBeExtraAnimated() {
      return true;
   }

   public boolean shouldAlwaysAnimateLegs() {
      return !this.animatingLegs;
   }

   public boolean bobBodyWhileWalking() {
      return true;
   }

   public boolean shouldSheathSword() {
      return false;
   }
}
