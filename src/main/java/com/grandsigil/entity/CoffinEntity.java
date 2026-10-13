package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.CoffinParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.CoffinFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEffects;
import com.grandsigil.registry.GsEntities;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class CoffinEntity extends Entity {
   public static final int STAGE_SEAL = 0;
   public static final int STAGE_HOLD = 1;
   public static final int STAGE_BREAK = 2;
   public static final int SEAL_TICKS = 30;
   public static final int HOLD_TICKS = 80;
   public static final int BREAK_TICKS = 34;
   public static final int SPEARS = 24;
   public static final int THRUST = 7;
   public static final float RISE = 2.6F;
   public static final float SPEAR = 0.28F;
   public static final float RUPTURE = 2.5F;
   private static final float PULL_REACH = 2.0F;
   private static final double PULL = 0.055;
   private static final int ROOMFUL = 5;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(CoffinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(CoffinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_HALF = SynchedEntityData.defineId(CoffinEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   private float power = 22.0F;
   private int stageAge;
   private int driven;
   private boolean counted;
   private int caught;

   public CoffinEntity(EntityType<? extends CoffinEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public CoffinEntity(Level level, Vec3 at, LivingEntity caster, float power, float half) {
      this(GsEntities.COFFIN.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_HALF, half);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_HALF, 4.5F);
   }

   public int stage() {
      return this.entityData.get(DATA_STAGE);
   }

   public int stageAge() {
      return this.stageAge;
   }

   public float half() {
      return this.entityData.get(DATA_HALF);
   }

   public float height() {
      return this.half() * 2.6F;
   }

   public float sealed(float partial) {
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0 -> {
            float t = Mth.clamp(age / 30.0F, 0.0F, 1.0F);
            yield t * t * (3.0F - 2.0F * t);
         }
         case 1 -> 1.0F;
         default -> 1.0F;
      };
   }

   public float breaking(float partial) {
      return this.stage() == 2 ? Mth.clamp(((float)this.stageAge() + partial) / 34.0F, 0.0F, 1.0F) : 0.0F;
   }

   public static int born(int index) {
      return Mth.floor((float)(index * 73) / 24.0F);
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("CoffinEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(11);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            CoffinParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickSeal(level);
               break;
            case 1:
               this.tickHold(level);
               break;
            default:
               this.tickBreak(level);
         }
      }
   }

   private void tickSeal(ServerLevel level) {
      if (this.stageAge == 1) {
         CoffinFx.closes(level, this.position(), (double)this.half());
         this.pin(level);
      }

      if (this.stageAge % 6 == 0) {
         CoffinFx.rising(level, this.position(), (double)this.half(), (float)this.stageAge / 30.0F);
      }

      this.drag(level);
      if (this.stageAge >= 30) {
         this.advance(1);
         CoffinFx.lidded(level, this.position(), (double)this.half());
      }
   }

   private void tickHold(ServerLevel level) {
      if (!this.standing(level)) {
         this.advance(2);
      } else {
         this.drag(level);

         while (this.driven < 24 && this.stageAge >= born(this.driven) + 7) {
            this.spear(level, this.driven);
            this.driven++;
         }

         if (this.stageAge >= 80) {
            this.advance(2);
         }
      }
   }

   private void tickBreak(ServerLevel level) {
      if (this.stageAge == 1) {
         this.rupture(level);
      }

      if (this.stageAge >= 34) {
         this.discard();
      }
   }

   private void advance(int stage) {
      this.entityData.set(DATA_STAGE, stage);
      this.stageAge = 0;
      this.entityData.set(DATA_STAGE_AGE, 0);
   }

   private void pin(ServerLevel level) {
      if (GsConfig.COMMON.coffinHolds.get()) {
         Entity caster = this.caster(level);
         int ticks = 30 - this.stageAge + 80 + 10;

         for (LivingEntity target : this.inside(level)) {
            if (this.hostile(target, caster)) {
               GsEffects.bind(level, target, ticks);
               this.caught++;
            }
         }

         if (caster instanceof ServerPlayer player) {
            GsAdvancements.coffined(player, this.caught);
         }
      }
   }

   private void drag(ServerLevel level) {
      Entity caster = this.caster(level);
      float reach = this.half() * 2.0F;
      AABB box = new AABB(
         this.getX() - (double)reach,
         this.getY() - 2.0,
         this.getZ() - (double)reach,
         this.getX() + (double)reach,
         this.getY() + (double)this.height(),
         this.getZ() + (double)reach
      );

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box)) {
         if (this.hostile(target, caster) && !this.covers(target)) {
            Vec3 toward = new Vec3(this.getX() - target.getX(), 0.0, this.getZ() - target.getZ());
            double distance = toward.length();
            if (!(distance < 0.1)) {
               double strength = 0.055 * (1.0 - distance / (double)reach);
               target.setDeltaMovement(target.getDeltaMovement().add(toward.scale(strength / distance)));
               target.hurtMarked = true;
            }
         }
      }
   }

   private void spear(ServerLevel level, int index) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int struck = 0;

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster) && struck < cap) {
            target.invulnerableTime = 0;
            target.hurt(GsDamage.blackCoffin(level, caster), this.power * 0.28F * scale);
            struck++;
         }
      }

      CoffinFx.driven(level, this.position(), (double)this.half(), index);
   }

   private void rupture(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      CoffinFx.breaks(level, this.position(), (double)this.half());

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster)) {
            target.invulnerableTime = 0;
            target.hurt(GsDamage.blackCoffin(level, caster), this.power * 2.5F * scale);
            Vec3 out = new Vec3(target.getX() - this.getX(), 0.0, target.getZ() - this.getZ());
            if (out.lengthSqr() < 0.01) {
               out = new Vec3(0.1, 0.0, 0.1);
            }

            target.setDeltaMovement(target.getDeltaMovement().add(out.normalize().scale(0.55)).add(0.0, 0.45, 0.0));
            target.hurtMarked = true;
         }
      }
   }

   private boolean standing(ServerLevel level) {
      Entity caster = this.caster(level);
      return caster == null || caster.isAlive() && !caster.isRemoved();
   }

   private List<LivingEntity> inside(ServerLevel level) {
      float h = this.half();
      AABB box = new AABB(
         this.getX() - (double)h,
         this.getY() - 1.0,
         this.getZ() - (double)h,
         this.getX() + (double)h,
         this.getY() + (double)this.height(),
         this.getZ() + (double)h
      );
      return level.getEntitiesOfClass(LivingEntity.class, box, this::covers);
   }

   public boolean covers(Entity entity) {
      double h = (double)this.half();
      double dy = entity.getY() - this.getY();
      return Math.abs(entity.getX() - this.getX()) <= h && Math.abs(entity.getZ() - this.getZ()) <= h && dy >= -1.0 && dy <= (double)this.height();
   }

   private boolean hostile(LivingEntity target, @Nullable Entity caster) {
      if (target.isAlive() && target != caster) {
         if (caster != null) {
            if (target.isAlliedTo(caster)) {
               return false;
            }

            if (target instanceof TamableAnimal tame && this.casterId != null && this.casterId.equals(tame.getOwnerUUID())) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   private Entity caster(ServerLevel level) {
      return this.casterId == null ? null : level.getEntity(this.casterId);
   }

   @Override
   public void remove(RemovalReason reason) {
      this.release();
      super.remove(reason);
   }

   private void release() {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(11);
      }
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (this.level().isClientSide && DATA_STAGE.equals(key)) {
         this.stageAge = this.entityData.get(DATA_STAGE_AGE);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_HALF, tag.contains("Half") ? tag.getFloat("Half") : 4.5F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.driven = tag.getInt("Driven");
      this.caught = tag.getInt("Caught");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Half", this.half());
      tag.putFloat("Power", this.power);
      tag.putInt("Driven", this.driven);
      tag.putInt("Caught", this.caught);
      if (this.casterId != null) {
         tag.putUUID("Caster", this.casterId);
      }
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity tracker) {
      return super.getAddEntityPacket(tracker);
   }

   @Override
   public boolean isPickable() {
      return false;
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   public boolean canBeCollidedWith() {
      return false;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 102400.0;
   }

   @Override
   public boolean isAttackable() {
      return false;
   }

   @Override
   protected boolean canRide(Entity vehicle) {
      return false;
   }
}
