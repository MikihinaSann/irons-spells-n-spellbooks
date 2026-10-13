package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.GateParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.GateFx;
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
import net.minecraft.tags.EntityTypeTags;
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

public class GateEntity extends Entity {
   public static final float CEILING = 120.0F;
   public static final float CORRIDOR = 12.0F;
   public static final int RIBS = 8;
   public static final int STAGE_OPEN = 0;
   public static final int STAGE_HOLD = 1;
   public static final int STAGE_CLOSE = 2;
   public static final int OPEN_TICKS = 60;
   public static final int HOLD_TICKS = 240;
   public static final int CLOSE_TICKS = 60;
   private static final int PULSE_INTERVAL = 10;
   private static final float UNDEAD = 3.0F;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(GateEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(GateEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(GateEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   private float power = 18.0F;
   private boolean counted;
   private int stageAge;
   private int judged;

   public GateEntity(EntityType<? extends GateEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public GateEntity(Level level, Vec3 at, LivingEntity caster, float power, float radius) {
      this(GsEntities.GATE.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_RADIUS, 28.0F);
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

   public float opening(float partial) {
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0 -> ease(Mth.clamp(age / 60.0F, 0.0F, 1.0F));
         case 1 -> 1.0F;
         default -> 1.0F - ease(Mth.clamp(age / 60.0F, 0.0F, 1.0F));
      };
   }

   private static float ease(float t) {
      return t * t * (3.0F - 2.0F * t);
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("GateEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(4);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            GateParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickOpen(level);
               break;
            case 1:
               this.tickHold(level);
               break;
            default:
               this.tickClose(level);
         }
      }
   }

   private void tickOpen(ServerLevel level) {
      if (this.stageAge == 1) {
         GateFx.opens(level, this.position(), (double)this.radius());
      }

      if (this.stageAge % 12 == 0) {
         GateFx.unfolding(level, this.position(), (float)this.stageAge / 60.0F);
      }

      if (this.stageAge >= 60) {
         this.advance(1);
      }
   }

   private void tickHold(ServerLevel level) {
      if (this.stageAge % 10 == 0) {
         this.judge(level);
      }

      if (this.stageAge % 45 == 0) {
         GateFx.choir(level, this.position(), (double)this.radius());
      }

      if (this.stageAge >= 240) {
         this.advance(2);
      }
   }

   private void tickClose(ServerLevel level) {
      if (this.stageAge == 1) {
         GateFx.closes(level, this.position(), (double)this.radius());
         if (this.caster(level) instanceof ServerPlayer player) {
            GsAdvancements.gateHeld(player, this.judged);
         }
      }

      if (this.stageAge >= 60) {
         this.discard();
      }
   }

   private void advance(int stage) {
      this.entityData.set(DATA_STAGE, stage);
      this.stageAge = 0;
      this.entityData.set(DATA_STAGE_AGE, 0);
   }

   private void judge(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster)) {
            boolean undead = target.getType().is(EntityTypeTags.UNDEAD);
            target.hurt(GsDamage.heavenGate(level, caster), this.power * 0.25F * (undead ? 3.0F : 1.0F) * scale);
            this.judged++;
         } else if (target.getHealth() < target.getMaxHealth()) {
            target.heal(this.power * 0.15F);
            GateFx.mended(level, target);
         }
      }
   }

   private List<LivingEntity> inside(ServerLevel level) {
      float r = this.radius();
      AABB box = new AABB(
         this.getX() - (double)r, this.getY() - 4.0, this.getZ() - (double)r, this.getX() + (double)r, this.getY() + 24.0, this.getZ() + (double)r
      );
      return level.getEntitiesOfClass(LivingEntity.class, box, this::covers);
   }

   public boolean covers(Entity entity) {
      double r = (double)this.radius();
      double dx = entity.getX() - this.getX();
      double dz = entity.getZ() - this.getZ();
      return dx * dx + dz * dz <= r * r && Math.abs(entity.getY() - this.getY()) <= 16.0;
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
         GsPresence.leave(4);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 28.0F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.judged = tag.getInt("Judged");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Radius", this.radius());
      tag.putFloat("Power", this.power);
      tag.putInt("Judged", this.judged);
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
