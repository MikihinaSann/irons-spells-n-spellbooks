package net.alshanex.tunes_n_tomes.entity.segno_marker;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.TunesNTomesConfig;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.alshanex.tunes_n_tomes.util.SoundMarkerManager;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;

public class SegnoMarkerEntity extends AbstractSpellCastingMob {
   private static final EntityDataAccessor<Boolean> DATA_RESONATING = SynchedEntityData.defineId(SegnoMarkerEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(
      SegnoMarkerEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> DATA_RESONANCE_TICKS = SynchedEntityData.defineId(SegnoMarkerEntity.class, EntityDataSerializers.INT);
   private int lifetimeTicks = 0;
   private int maxLifetimeTicks;
   private float rotationAngle = 0.0F;
   private AbstractSpell pendingSpell = null;
   private int pendingSpellLevel = 0;
   private int pendingResonanceDelay = 0;
   private UUID pendingTargetUUID = null;

   public SegnoMarkerEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
      this.noPhysics = true;
      this.maxLifetimeTicks = (Integer)TunesNTomesConfig.MARKER_DURATION_SECONDS.get() * 20;
   }

   public SegnoMarkerEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends PathfinderMob>)TEntityRegistry.SEGNO_MARKER.get(), level);
      this.setOwnerUUID(owner.getUUID());
      this.maxLifetimeTicks = (Integer)TunesNTomesConfig.MARKER_DURATION_SECONDS.get() * 20;
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.MAX_HEALTH, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 0.0)
         .add(Attributes.FOLLOW_RANGE, 0.0)
         .add(Attributes.FOLLOW_RANGE, 64.0);
   }

   protected void registerGoals() {
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_RESONATING, false);
      pBuilder.define(DATA_OWNER_UUID, Optional.empty());
      pBuilder.define(DATA_RESONANCE_TICKS, 0);
   }

   public void setOwnerUUID(UUID uuid) {
      this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(uuid));
   }

   @Nullable
   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.entityData.get(DATA_OWNER_UUID)).orElse(null);
   }

   @Nullable
   public Player getOwnerPlayer() {
      UUID ownerUUID = this.getOwnerUUID();
      return ownerUUID == null ? null : this.level().getPlayerByUUID(ownerUUID);
   }

   public boolean isResonating() {
      return (Boolean)this.entityData.get(DATA_RESONATING);
   }

   public void setResonating(boolean resonating) {
      this.entityData.set(DATA_RESONATING, resonating);
   }

   public int getResonanceTicks() {
      return (Integer)this.entityData.get(DATA_RESONANCE_TICKS);
   }

   public void setResonanceTicks(int ticks) {
      this.entityData.set(DATA_RESONANCE_TICKS, ticks);
   }

   public void scheduleResonance(AbstractSpell spell, int spellLevel, int delayTicks, @Nullable UUID targetUUID) {
      if (!this.level().isClientSide) {
         if (this.isResonating() || this.pendingSpell != null) {
            return;
         }

         this.pendingTargetUUID = targetUUID;
         if (delayTicks <= 0) {
            this.triggerResonance(spell, spellLevel);
         } else {
            this.pendingSpell = spell;
            this.pendingSpellLevel = spellLevel;
            this.pendingResonanceDelay = delayTicks;
         }
      }
   }

   public void triggerResonance(AbstractSpell spell, int spellLevel) {
      if (!this.level().isClientSide) {
         if (this.isResonating()) {
            return;
         }

         Player owner = this.getOwnerPlayer();
         int spellCastTime;
         if (owner != null) {
            spellCastTime = spell.getEffectiveCastTime(spellLevel, owner) + 20;
         } else {
            spellCastTime = spell.getEffectiveCastTime(spellLevel, this) + 20;
         }

         this.setResonating(true);
         this.setResonanceTicks(spellCastTime);
         LivingEntity resolvedTarget = null;
         if (owner instanceof ServerPlayer serverPlayer) {
            ServerLevel serverLevel = (ServerLevel)this.level();
            if (this.pendingTargetUUID != null && serverLevel.getEntity(this.pendingTargetUUID) instanceof LivingEntity living && living.isAlive()) {
               resolvedTarget = living;
            }

            if (resolvedTarget == null) {
               MagicData ownerMagicData = MagicData.getPlayerMagicData(serverPlayer);
               if (ownerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
                  LivingEntity castTarget = targetData.getTarget(serverLevel);
                  if (castTarget != null && castTarget.isAlive()) {
                     resolvedTarget = castTarget;
                  }
               }
            }

            if (resolvedTarget == null) {
               resolvedTarget = this.getOwnerTarget(owner);
            }
         }

         if (resolvedTarget != null) {
            this.setTarget(resolvedTarget);
            MagicData markerMagicData = this.getMagicData();
            markerMagicData.setAdditionalCastData(new TargetEntityCastData(resolvedTarget));
         }

         this.pendingTargetUUID = null;
         this.initiateCastSpell(spell, spellLevel);
      }
   }

   @Nullable
   private LivingEntity getOwnerTarget(Player owner) {
      if (owner instanceof ServerPlayer serverPlayer) {
         LivingEntity lastHurt = serverPlayer.getLastHurtMob();
         if (lastHurt != null && lastHurt.isAlive()) {
            return lastHurt;
         }

         LivingEntity lastAttacker = serverPlayer.getLastHurtByMob();
         if (lastAttacker != null && lastAttacker.isAlive()) {
            return lastAttacker;
         }
      }

      return null;
   }

   public void tick() {
      super.tick();
      if (!this.isCasting() || this.getTarget() == null) {
         this.rotationAngle += 2.0F;
         if (this.rotationAngle >= 360.0F) {
            this.rotationAngle -= 360.0F;
         }

         this.setYRot(this.rotationAngle);
         this.yBodyRot = this.rotationAngle;
         this.yHeadRot = this.rotationAngle;
      }

      if (!this.level().isClientSide) {
         this.lifetimeTicks++;
         if (this.lifetimeTicks >= this.maxLifetimeTicks) {
            this.expireMarker();
            return;
         }

         if (this.pendingSpell != null) {
            this.pendingResonanceDelay--;
            if (this.pendingResonanceDelay <= 0) {
               AbstractSpell spell = this.pendingSpell;
               int level = this.pendingSpellLevel;
               this.pendingSpell = null;
               this.pendingSpellLevel = 0;
               this.pendingResonanceDelay = 0;
               this.triggerResonance(spell, level);
            }
         }

         if (this.isResonating()) {
            int remaining = this.getResonanceTicks();
            if (remaining > 0) {
               this.setResonanceTicks(remaining - 1);
               if (this.isCasting() && this.getOwnerPlayer() instanceof ServerPlayer serverPlayer) {
                  MagicData ownerMagicData = MagicData.getPlayerMagicData(serverPlayer);
                  SpellData castingSpell = this.getMagicData().getCastingSpell();
                  if (castingSpell != null && castingSpell.getSpell().getCastType() == CastType.CONTINUOUS && !ownerMagicData.isCasting()) {
                     this.cancelCast();
                     this.setResonating(false);
                     this.setResonanceTicks(0);
                  }
               }
            } else {
               this.setResonating(false);
            }
         }
      }
   }

   private void expireMarker() {
      if (this.getOwnerPlayer() instanceof ServerPlayer serverPlayer) {
         SoundMarkerManager.onMarkerExpired(serverPlayer, this.getUUID());
         serverPlayer.displayClientMessage(Component.translatable("message.tunes_n_tomes.marker_expired"), true);
      }

      this.discard();
   }

   public boolean isAlliedTo(Entity pEntity) {
      if (super.isAlliedTo(pEntity)) {
         return true;
      } else {
         Player owner = this.getOwnerPlayer();
         if (owner == null) {
            return false;
         } else if (pEntity.is(owner)) {
            return true;
         } else if (owner.isAlliedTo(pEntity)) {
            return true;
         } else if (!(pEntity instanceof SegnoMarkerEntity otherMarker)) {
            return false;
         } else {
            UUID otherOwner = otherMarker.getOwnerUUID();
            return otherOwner != null && otherOwner.equals(this.getOwnerUUID());
         }
      }
   }

   public boolean isPickable() {
      return false;
   }

   public boolean canCollideWith(Entity entity) {
      return false;
   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public boolean addEffect(MobEffectInstance effectInstance, @org.jetbrains.annotations.Nullable Entity entity) {
      return false;
   }

   public boolean hasEffect(Holder<MobEffect> effect) {
      return false;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   public boolean isInvulnerable() {
      return true;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean canBeLeashed() {
      return false;
   }

   public void travel(Vec3 travelVector) {
   }

   public void push(Entity pEntity) {
   }

   protected void pushEntities() {
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
      return false;
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("lifetimeTicks", this.lifetimeTicks);
      pCompound.putInt("maxLifetimeTicks", this.maxLifetimeTicks);
      if (this.getOwnerUUID() != null) {
         pCompound.putUUID("ownerUUID", this.getOwnerUUID());
      }

      pCompound.putBoolean("resonating", this.isResonating());
      pCompound.putInt("resonanceTicks", this.getResonanceTicks());
      if (this.pendingSpell != null) {
         pCompound.putString("pendingSpellId", this.pendingSpell.getSpellId());
         pCompound.putInt("pendingSpellLevel", this.pendingSpellLevel);
         pCompound.putInt("pendingResonanceDelay", this.pendingResonanceDelay);
      }

      if (this.pendingTargetUUID != null) {
         pCompound.putUUID("pendingTargetUUID", this.pendingTargetUUID);
      }
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.lifetimeTicks = pCompound.getInt("lifetimeTicks");
      this.maxLifetimeTicks = pCompound.getInt("maxLifetimeTicks");
      if (pCompound.hasUUID("ownerUUID")) {
         this.setOwnerUUID(pCompound.getUUID("ownerUUID"));
      }

      this.setResonating(pCompound.getBoolean("resonating"));
      this.setResonanceTicks(pCompound.getInt("resonanceTicks"));
      if (pCompound.contains("pendingSpellId")) {
         this.pendingSpell = SpellRegistry.getSpell(pCompound.getString("pendingSpellId"));
         this.pendingSpellLevel = pCompound.getInt("pendingSpellLevel");
         this.pendingResonanceDelay = pCompound.getInt("pendingResonanceDelay");
      }

      if (pCompound.hasUUID("pendingTargetUUID")) {
         this.pendingTargetUUID = pCompound.getUUID("pendingTargetUUID");
      }
   }

   public float getRotationAngle() {
      return this.rotationAngle;
   }

   public int getRemainingSeconds() {
      return Math.max(0, (this.maxLifetimeTicks - this.lifetimeTicks) / 20);
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      super.registerControllers(controllerRegistrar);
   }

   public boolean shouldBeExtraAnimated() {
      return false;
   }

   public boolean shouldAlwaysAnimateHead() {
      return false;
   }

   public boolean shouldAlwaysAnimateLegs() {
      return false;
   }

   public boolean shouldPointArmsWhileCasting() {
      return false;
   }

   public boolean bobBodyWhileWalking() {
      return false;
   }
}
