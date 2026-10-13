package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.BoltParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.BoltFx;
import com.grandsigil.registry.GsDamage;
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

public class BoltEntity extends Entity {
   public static final float COLUMN_RADIUS = 8.0F;
   public static final float CEILING = 210.0F;
   public static final int STAGE_GATHER = 0;
   public static final int STAGE_HOLD = 1;
   public static final int STAGE_STRIKE = 2;
   public static final int GATHER_TICKS = 60;
   public static final int HOLD_TICKS = 10;
   public static final int STRIKE_TICKS = 120;
   private static final float SHELTERED = 0.55F;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(BoltEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(BoltEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(BoltEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   private float power = 22.0F;
   private boolean counted;
   private int stageAge;

   public BoltEntity(EntityType<? extends BoltEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public BoltEntity(Level level, Vec3 at, LivingEntity caster, float power, float radius) {
      this(GsEntities.BOLT.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_RADIUS, 40.0F);
   }

   public int stage() {
      return this.entityData.get(DATA_STAGE);
   }

   public int stageAge() {
      return this.stageAge;
   }

   public float radius() {
      return this.entityData.get(DATA_RADIUS);
   }

   public float spread(float partial) {
      if (this.stage() != 0) {
         return 1.0F;
      } else {
         float t = ((float)this.stageAge() + partial) / 60.0F;
         return Math.min(1.0F, t * t * 0.35F + t * 0.65F);
      }
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("BoltEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(2);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            BoltParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickGather(level);
               break;
            case 1:
               this.tickHold(level);
               break;
            default:
               this.tickStrike(level);
         }
      }
   }

   private void tickGather(ServerLevel level) {
      if (this.stageAge == 1) {
         BoltFx.gathering(level, this.position(), (double)this.radius());
      }

      if (this.stageAge % 5 == 0) {
         BoltFx.charging(level, this.position(), (double)this.radius(), (float)this.stageAge / 60.0F);
      }

      if (this.stageAge >= 60) {
         this.advance(1);
      }
   }

   private void tickHold(ServerLevel level) {
      if (this.stageAge >= 10) {
         this.advance(2);
      }
   }

   private void tickStrike(ServerLevel level) {
      if (this.stageAge == 1) {
         this.strike(level);
      }

      BoltFx.ionised(level, this.position(), (double)this.radius(), this.stageAge);
      if (this.stageAge >= 120) {
         this.discard();
      }
   }

   private void advance(int stage) {
      this.entityData.set(DATA_STAGE, stage);
      this.stageAge = 0;
      this.entityData.set(DATA_STAGE_AGE, 0);
   }

   private void strike(ServerLevel level) {
      Entity caster = this.caster(level);
      float radius = this.radius();
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int caught = 0;
      int sheltered = 0;

      for (LivingEntity target : this.targets(level, radius)) {
         if (caught >= cap) {
            break;
         }

         caught++;
         double distance = this.horizontal(target);
         boolean underColumn = distance <= 8.0;
         boolean open = level.canSeeSky(target.blockPosition());
         if (!open) {
            sheltered++;
         }

         float scale = (underColumn ? 5.2F : 3.5F) * (open ? 1.0F : 0.55F);
         float damage = (float)((double)(this.power * scale) * (Double)GsConfig.COMMON.damageMultiplier.get());
         target.hurt(GsDamage.skybreak(level, caster), damage);
         Vec3 out = target.position().subtract(this.position());
         double length = Math.max(0.1, Math.sqrt(out.x * out.x + out.z * out.z));
         target.setDeltaMovement(target.getDeltaMovement().add(out.x / length * 0.8, 0.7, out.z / length * 0.8));
         target.hurtMarked = true;
      }

      BoltFx.struck(level, this.position(), (double)radius);
      if (caster instanceof ServerPlayer player) {
         GsAdvancements.skybroke(player, caught, sheltered);
      }
   }

   private List<LivingEntity> targets(ServerLevel level, float radius) {
      AABB box = new AABB(
         this.getX() - (double)radius,
         this.getY() - 12.0,
         this.getZ() - (double)radius,
         this.getX() + (double)radius,
         this.getY() + 32.0,
         this.getZ() + (double)radius
      );
      Entity caster = this.caster(level);
      return level.getEntitiesOfClass(LivingEntity.class, box, target -> this.affects(target, caster, radius));
   }

   private boolean affects(LivingEntity target, @Nullable Entity caster, float radius) {
      if (target.isAlive() && target != caster) {
         if (caster != null) {
            if (target.isAlliedTo(caster)) {
               return false;
            }

            if (target instanceof TamableAnimal tame && this.casterId != null && this.casterId.equals(tame.getOwnerUUID())) {
               return false;
            }
         }

         return this.horizontal(target) <= (double)radius;
      } else {
         return false;
      }
   }

   private double horizontal(Entity target) {
      double dx = target.getX() - this.getX();
      double dz = target.getZ() - this.getZ();
      return Math.sqrt(dx * dx + dz * dz);
   }

   @Nullable
   private Entity caster(ServerLevel level) {
      return this.casterId == null ? null : level.getEntity(this.casterId);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (this.level().isClientSide && DATA_STAGE.equals(key)) {
         this.stageAge = this.entityData.get(DATA_STAGE_AGE);
      }
   }

   @Override
   public void remove(RemovalReason reason) {
      this.release();
      super.remove(reason);
   }

   private void release() {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(2);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 40.0F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Radius", this.radius());
      tag.putFloat("Power", this.power);
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
      return distance < 160000.0;
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
