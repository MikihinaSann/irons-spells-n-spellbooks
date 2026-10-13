package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.GazeParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.GazeFx;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class GazeEntity extends Entity {
   public static final float ALTITUDE = 150.0F;
   public static final float PUPIL_SPAN = 120.0F;
   public static final int STAGE_OPEN = 0;
   public static final int STAGE_WATCH = 1;
   public static final int STAGE_CLOSE = 2;
   public static final int OPEN_TICKS = 60;
   public static final int WATCH_TICKS = 220;
   public static final int CLOSE_TICKS = 60;
   public static final int HOLD_SPAN = 280;
   private static final int HOLD_TICKS = 30;
   private static final int HOLD_INTERVAL = 10;
   private static final int PULSE_INTERVAL = 20;
   private static final double LOOKING = 0.86;
   private static final int TOLERANCE = 60;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(GazeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(GazeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(GazeEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   private float power = 20.0F;
   private int stageAge;
   private boolean counted;
   private int held;
   private int casterLooked;

   public GazeEntity(EntityType<? extends GazeEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public GazeEntity(Level level, Vec3 at, LivingEntity caster, float power, float radius) {
      this(GsEntities.GAZE.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_RADIUS, 45.0F);
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

   public float open(float partial) {
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0 -> {
            float t = Mth.clamp(age / 60.0F, 0.0F, 1.0F);
            yield 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
         }
         case 1 -> 1.0F;
         default -> {
            float t = Mth.clamp(age / 60.0F, 0.0F, 1.0F);
            yield 1.0F - t * t;
         }
      };
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("GazeEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(6);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            GazeParticles.ambient(this);
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
               this.tickWatch(level);
               break;
            default:
               this.tickClose(level);
         }
      }
   }

   private void tickOpen(ServerLevel level) {
      if (this.stageAge == 1) {
         GazeFx.opens(level, this.position(), (double)this.radius());
      }

      if (this.stageAge % 10 == 0) {
         this.grip(level);
      }

      if (this.stageAge % 15 == 0) {
         GazeFx.tearing(level, this.position(), (float)this.stageAge / 60.0F);
      }

      if (this.stageAge >= 60) {
         this.advance(1);
      }
   }

   private void tickWatch(ServerLevel level) {
      if (this.stageAge % 10 == 0) {
         this.grip(level);
      }

      if (this.stageAge % 20 == 0) {
         this.pulse(level);
      }

      if (this.stageAge % 55 == 0) {
         GazeFx.watching(level, this.position(), (double)this.radius());
      }

      this.price(level);
      if (this.stageAge >= 220) {
         this.release(level);
         this.advance(2);
      }
   }

   private void tickClose(ServerLevel level) {
      if (this.stageAge == 1) {
         GazeFx.closes(level, this.position(), (double)this.radius());
         if (this.caster(level) instanceof ServerPlayer player) {
            GsAdvancements.gazed(player, this.held, this.casterLooked);
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

   private void grip(ServerLevel level) {
      if (GsConfig.COMMON.gazeHolds.get()) {
         Entity caster = this.caster(level);
         int cap = (Integer)GsConfig.COMMON.maxTargets.get();
         int caught = 0;

         for (LivingEntity target : this.inside(level)) {
            if (this.hostile(target, caster)) {
               if (GsEffects.hold(level, target, 30)) {
                  GazeFx.caught(level, target);
               }

               if (++caught >= cap) {
                  break;
               }
            }
         }

         this.held = Math.max(this.held, caught);
      }
   }

   private void pulse(ServerLevel level) {
      Entity caster = this.caster(level);
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int seen = 0;

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster) && GsEffects.transfixed(target)) {
            GazeFx.stares(level, target);
            if (++seen >= cap) {
               break;
            }
         }
      }

      if (seen > 0) {
         GazeFx.blinks(level, this.position());
      }
   }

   private void release(ServerLevel level) {
      Entity caster = this.caster(level);
      int seen = 0;

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster) && GsEffects.transfixed(target)) {
            if (seen < 12) {
               GazeFx.releases(level, target);
            }

            if (++seen >= 64) {
               break;
            }
         }
      }
   }

   private void price(ServerLevel level) {
      if (this.caster(level) instanceof Player player && player.isAlive()) {
         Vec3 look = player.getLookAngle();
         Vec3 toEye = new Vec3(this.getX() - player.getX(), 150.0, this.getZ() - player.getZ()).normalize();
         if (look.dot(toEye) < 0.86) {
            this.casterLooked = Math.max(0, this.casterLooked - 2);
            return;
         }

         this.casterLooked++;
         if (this.casterLooked >= 60 && this.casterLooked % 20 == 0) {
            float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
            player.hurt(GsDamage.gaze(level, this), this.power * 0.1F * scale);
            GazeFx.noticed(level, player);
            return;
         }

         return;
      }
   }

   private List<LivingEntity> inside(ServerLevel level) {
      float r = this.radius();
      AABB box = new AABB(
         this.getX() - (double)r, this.getY() - 8.0, this.getZ() - (double)r, this.getX() + (double)r, this.getY() + 32.0, this.getZ() + (double)r
      );
      return level.getEntitiesOfClass(LivingEntity.class, box, this::covers);
   }

   public boolean covers(Entity entity) {
      double r = (double)this.radius();
      double dx = entity.getX() - this.getX();
      double dz = entity.getZ() - this.getZ();
      return entity.isAlive() && dx * dx + dz * dz <= r * r && Math.abs(entity.getY() - this.getY()) <= 24.0;
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
         GsPresence.leave(6);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 45.0F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.held = tag.getInt("Held");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Radius", this.radius());
      tag.putFloat("Power", this.power);
      tag.putInt("Held", this.held);
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
      return distance < 230400.0;
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
