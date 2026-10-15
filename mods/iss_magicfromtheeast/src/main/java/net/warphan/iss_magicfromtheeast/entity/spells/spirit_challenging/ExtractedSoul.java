package net.warphan.iss_magicfromtheeast.entity.spells.spirit_challenging;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import java.util.Collections;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import org.jetbrains.annotations.Nullable;

public class ExtractedSoul extends LivingEntity implements IEntityWithComplexSpawn, AntiMagicSusceptible {
   protected static final EntityDataAccessor<Boolean> DATA_IS_BABY = SynchedEntityData.defineId(ExtractedSoul.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> DATA_HAS_RADIUS = SynchedEntityData.defineId(ExtractedSoul.class, EntityDataSerializers.BOOLEAN);
   public int duration = -1;
   public float bonusPercent;
   private UUID ownerUUID;
   private UUID extractorUUID;
   private LivingEntity cachedExtractor;
   private LivingEntity cachedOwner;
   @Nullable
   EntityType<?> entityToCopy;
   private HumanoidArm mainArm = HumanoidArm.RIGHT;

   public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
      LivingEntity owner = this.getOwner();
      LivingEntity extractor = this.getExtractor();
      buffer.writeInt(owner == null ? -1 : owner.getId());
      buffer.writeInt(extractor == null ? -1 : extractor.getId());
      if (this.entityToCopy == null) {
         buffer.writeBoolean(false);
      } else {
         buffer.writeBoolean(true);
         buffer.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(this.entityToCopy));
      }
   }

   public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
      Entity owner = this.level.getEntity(additionalData.readInt());
      Entity extractor = this.level.getEntity(additionalData.readInt());
      if (owner instanceof LivingEntity livingEntity) {
         this.setOwner(livingEntity);
      }

      if (extractor instanceof LivingEntity livingEntity) {
         this.setExtractor(livingEntity);
      }

      if (additionalData.readBoolean()) {
         this.setEntityTypeToCopy((EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(additionalData.readResourceLocation()));
      }
   }

   public ExtractedSoul(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_IS_BABY, false);
      pBuilder.define(DATA_HAS_RADIUS, false);
   }

   public boolean hasRadius() {
      return (Boolean)this.entityData.get(DATA_HAS_RADIUS);
   }

   public void enableRadius() {
      this.entityData.set(DATA_HAS_RADIUS, true);
   }

   protected static void copyEntityVisualProperties(LivingEntity baseEntity, LivingEntity entityToCopy) {
      baseEntity.moveTo(entityToCopy.getX(), entityToCopy.getY(), entityToCopy.getZ(), entityToCopy.getYRot(), entityToCopy.getXRot());
      baseEntity.setYBodyRot(entityToCopy.yBodyRot);
      baseEntity.yBodyRotO = baseEntity.yBodyRot;
      baseEntity.setYHeadRot(entityToCopy.getYHeadRot());
      baseEntity.yHeadRotO = baseEntity.yHeadRot;
      baseEntity.setPose(entityToCopy.getPose());
      if (baseEntity instanceof ExtractedSoul extractedSoul) {
         extractedSoul.mainArm = entityToCopy.getMainArm();
         if (entityToCopy.isBaby()) {
            extractedSoul.getEntityData().set(DATA_IS_BABY, true);
         }
      } else if (baseEntity.level.isClientSide && entityToCopy.isBaby()) {
         if (baseEntity instanceof AgeableMob ageableMob) {
            ageableMob.setAge(-10);
         } else if (baseEntity instanceof Zombie zombie) {
            zombie.setBaby(true);
         }
      }

      if (baseEntity.getAttributes().hasAttribute(Attributes.SCALE) && entityToCopy.getAttributes().hasAttribute(Attributes.SCALE)) {
         baseEntity.getAttributes().getInstance(Attributes.SCALE).setBaseValue(entityToCopy.getAttributeValue(Attributes.SCALE));
      }

      if (entityToCopy instanceof Player player) {
         baseEntity.setCustomName(player.getDisplayName());
         baseEntity.setCustomNameVisible(true);
      }
   }

   public boolean isBaby() {
      return (Boolean)this.entityData.get(DATA_IS_BABY);
   }

   protected EntityDimensions getDefaultDimensions(Pose pose) {
      return this.entityToCopy == null ? super.getDefaultDimensions(pose) : this.entityToCopy.getDimensions();
   }

   public void setEntityTypeToCopy(@Nullable EntityType<?> entityToCopy) {
      this.entityToCopy = entityToCopy;
      this.refreshDimensions();
   }

   public ExtractedSoul(Level level, LivingEntity entityToCopy, @Nullable LivingEntity extractorEntity) {
      this((EntityType<? extends LivingEntity>)MFTEEntityRegistries.EXTRACTED_SOUL.get(), level);
      copyEntityVisualProperties(this, entityToCopy);
      if (!(entityToCopy instanceof Player)) {
         this.setEntityTypeToCopy(entityToCopy.getType());
      }

      this.setNoGravity(true);
      this.setOwner(entityToCopy);
      this.setExtractor(extractorEntity);
   }

   public void setOwner(@javax.annotation.Nullable LivingEntity owner) {
      if (owner != null) {
         this.ownerUUID = owner.getUUID();
         this.cachedOwner = owner;
      }
   }

   public LivingEntity getOwner() {
      if (this.cachedOwner != null && this.cachedOwner.isAlive()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
         if (((ServerLevel)this.level()).getEntity(this.ownerUUID) instanceof LivingEntity livingEntity) {
            this.cachedOwner = livingEntity;
         }

         return this.cachedOwner;
      } else {
         return null;
      }
   }

   public void setExtractor(@javax.annotation.Nullable LivingEntity extractor) {
      if (extractor != null) {
         this.extractorUUID = extractor.getUUID();
         this.cachedExtractor = extractor;
      }
   }

   public LivingEntity getExtractor() {
      if (this.cachedExtractor != null && this.cachedExtractor.isAlive()) {
         return this.cachedExtractor;
      } else if (this.extractorUUID != null && this.level() instanceof ServerLevel) {
         if (((ServerLevel)this.level()).getEntity(this.extractorUUID) instanceof LivingEntity livingEntity) {
            this.cachedExtractor = livingEntity;
         }

         return this.cachedExtractor;
      } else {
         return null;
      }
   }

   public void tick() {
      super.tick();
      if (this.duration > 0) {
         this.duration--;
         if (this.duration == 0) {
            this.onUnSummon();
         }
      }
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.SCULK_SOUL, this.getX(), this.getY(), this.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.discard();
      }
   }

   public void setDuration(int durationTick) {
      this.duration = durationTick;
   }

   public void setBonusPercent(float bonusAmount) {
      this.bonusPercent = bonusAmount / 100.0F;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean isPickable() {
      return true;
   }

   public boolean fireImmune() {
      return true;
   }

   public boolean canFreeze() {
      return false;
   }

   public void setTicksFrozen(int ticksFrozen) {
   }

   public boolean isPushedByFluid() {
      return false;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)SoundEvents.SOUL_ESCAPE.value();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return (SoundEvent)MFTESoundRegistries.SOUL_CRUSHED.value();
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return !this.level().isClientSide && !this.isInvulnerableTo(pSource) && !this.shouldIgnoreDamage(pSource) ? super.hurt(pSource, pAmount) : false;
   }

   public boolean shouldIgnoreDamage(DamageSource damageSource) {
      return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !ServerConfigs.CAN_ATTACK_OWN_SUMMONS.get() && damageSource.getEntity() != null
         ? DamageSources.isFriendlyFireBetween(this.getOwner(), this)
         : false;
   }

   public void die(DamageSource source) {
      super.die(source);
      if (this.isDeadOrDying() && !this.level.isClientSide) {
         this.onUnSummon();
      }
   }

   public Iterable<ItemStack> getArmorSlots() {
      return Collections.singleton(ItemStack.EMPTY);
   }

   public ItemStack getItemBySlot(EquipmentSlot pSlot) {
      return ItemStack.EMPTY;
   }

   public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if (compoundTag.hasUUID("Owner")) {
         this.ownerUUID = compoundTag.getUUID("Owner");
      }

      if (compoundTag.hasUUID("Extractor")) {
         this.extractorUUID = compoundTag.getUUID("Extractor");
      }

      if (compoundTag.contains("entityToCopy")) {
         try {
            this.setEntityTypeToCopy((EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(compoundTag.getString("entityToCopy"))));
         } catch (Exception var3) {
         }
      }

      this.duration = compoundTag.getInt("durationTimer");
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if (this.ownerUUID != null) {
         compoundTag.putUUID("Owner", this.ownerUUID);
      }

      if (this.extractorUUID != null) {
         compoundTag.putUUID("Extractor", this.extractorUUID);
      }

      if (this.entityToCopy != null) {
         compoundTag.putString("entityToCopy", BuiltInRegistries.ENTITY_TYPE.getKey(this.entityToCopy).toString());
      }

      compoundTag.putInt("durationTimer", this.duration);
   }

   public HumanoidArm getMainArm() {
      return this.mainArm;
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 0.0)
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.FOLLOW_RANGE, 0.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 100.0)
         .add(Attributes.MOVEMENT_SPEED, 0.0);
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.onUnSummon();
   }
}
