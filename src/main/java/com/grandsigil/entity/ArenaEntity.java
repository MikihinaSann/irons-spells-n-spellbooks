package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.ArenaParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.ArenaFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEntities;
import java.util.ArrayList;
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

public class ArenaEntity extends Entity {
   public static final float WALL = 45.0F;
   public static final int BAYS = 24;
   public static final int TIERS = 3;
   public static final int STAGE_RISE = 0;
   public static final int STAGE_HOLD = 1;
   public static final int STAGE_FALL = 2;
   public static final int RISE_TICKS = 60;
   public static final int HOLD_TICKS = 280;
   public static final int FALL_TICKS = 60;
   private static final int BLEED = 10;
   public static final float RAIN = 0.95F;
   public static final int FLIGHT = 16;
   public static final int LANCES = 300;
   public static final float CEILING = 50.0F;
   public static final float LOFT = 46.0F;
   private static final float LANCE_BITE = 0.22F;
   public static final float INSET = 0.86F;
   private static final float THIRST = 0.09F;
   private static final float BITE = 0.1F;
   private static final float RETURN = 0.5F;
   private static final float COLLAPSE = 3.0F;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(ArenaEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(ArenaEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(ArenaEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_FED = SynchedEntityData.defineId(ArenaEntity.class, EntityDataSerializers.INT);
   @Nullable
   private UUID casterId;
   private float power = 20.0F;
   private boolean counted;
   private int stageAge;
   private float drawn;
   private int killed;

   public ArenaEntity(EntityType<? extends ArenaEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public ArenaEntity(Level level, Vec3 at, LivingEntity caster, float power, float radius) {
      this(GsEntities.ARENA.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_RADIUS, 34.0F);
      builder.define(DATA_FED, 0);
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

   public float height() {
      return 45.0F;
   }

   public int fed() {
      return this.entityData.get(DATA_FED);
   }

   public float built(float partial) {
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0 -> {
            float t = Mth.clamp(age / 60.0F, 0.0F, 1.0F);
            yield 1.0F - (1.0F - t) * (1.0F - t);
         }
         case 1 -> 1.0F;
         default -> 1.0F;
      };
   }

   public float loosed(float partial) {
      return this.stage() == 1 ? ((float)this.stageAge() + partial) * 0.95F : 0.0F;
   }

   public int planted(float partial) {
      if (this.stage() == 0) {
         return 0;
      } else {
         return this.stage() == 2 ? Math.min(300, Mth.floor(250.8F)) : Mth.clamp(Mth.floor(this.loosed(partial) - 15.2F), 0, 300);
      }
   }

   public float falling(float partial) {
      return this.stage() != 2 ? 0.0F : Mth.clamp(((float)this.stageAge() + partial) / 60.0F, 0.0F, 1.0F);
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("ArenaEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(7);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            ArenaParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickRise(level);
               break;
            case 1:
               this.tickHold(level);
               break;
            default:
               this.tickFall(level);
         }
      }
   }

   private void tickRise(ServerLevel level) {
      if (this.stageAge == 1) {
         ArenaFx.erupts(level, this.position(), (double)this.radius());
      }

      if (this.stageAge % 15 == 0) {
         ArenaFx.building(level, this.position(), (double)this.radius(), (float)this.stageAge / 60.0F);
      }

      this.pen(level, this.inside(level, this.radius() * 1.35F));
      if (this.stageAge >= 60) {
         this.advance(1);
      }
   }

   private void tickHold(ServerLevel level) {
      List<LivingEntity> here = this.inside(level, this.radius() * 1.35F);
      this.pen(level, here);
      this.pour(level, here);
      if (this.stageAge % 10 == 0) {
         this.bleed(level, here);
      }

      if (this.stageAge % 60 == 0) {
         ArenaFx.roaring(level, this.position(), (double)this.radius(), this.killed);
      }

      if (this.stageAge >= 280) {
         this.advance(2);
      }
   }

   private void tickFall(ServerLevel level) {
      if (this.stageAge == 1) {
         ArenaFx.collapses(level, this.position(), (double)this.radius(), 45.0F);
      }

      this.pen(level, this.inside(level, this.radius() * 1.35F));
      if (this.stageAge == 30) {
         this.bury(level);
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

   private void pen(ServerLevel level, List<LivingEntity> here) {
      Entity caster = this.caster(level);
      double r = (double)this.radius() * 0.93;

      for (LivingEntity target : here) {
         if (this.hostile(target, caster)) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double out = Math.sqrt(dx * dx + dz * dz);
            if (!(out < r) && !(out < 0.01)) {
               double push = Math.min(0.9, 0.22 + (out - r) * 0.16);
               target.setDeltaMovement(target.getDeltaMovement().add(-dx / out * push, 0.04, -dz / out * push));
               target.hurtMarked = true;
               if (level.random.nextInt(6) == 0) {
                  ArenaFx.repelled(level, target.position());
               }
            }
         }
      }
   }

   private void pour(ServerLevel level, List<LivingEntity> here) {
      int from = Mth.clamp(Mth.floor((float)(this.stageAge - 1) * 0.95F - 15.2F), 0, 300);
      int to = Mth.clamp(Mth.floor((float)this.stageAge * 0.95F - 15.2F), 0, 300);
      if (to > from) {
         Entity caster = this.caster(level);
         float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
         List<LivingEntity> marks = new ArrayList<>();

         for (LivingEntity candidate : here) {
            if (this.covers(candidate, this.radius() * 0.86F)) {
               marks.add(candidate);
            }
         }

         int struck = 0;

         for (int i = from; i < to && !marks.isEmpty(); i++) {
            LivingEntity mark = marks.get(this.random.nextInt(marks.size()));
            if (this.hostile(mark, caster)) {
               mark.hurt(GsDamage.colosseum(level, caster), this.power * 0.22F * scale);
               struck++;
            }
         }

         if (this.stageAge % 3 == 0) {
            ArenaFx.pouring(level, this.position(), (double)this.radius(), struck);
         }
      }
   }

   private void bleed(ServerLevel level, List<LivingEntity> here) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      float taken = 0.0F;

      for (LivingEntity target : here) {
         if (this.covers(target, this.radius()) && this.hostile(target, caster)) {
            float before = target.getHealth();
            float bite = this.power * 0.1F * (1.0F + (float)this.killed * 0.09F) * scale;
            target.hurt(GsDamage.colosseum(level, caster), bite);
            taken += Math.max(0.0F, before - target.getHealth());
            ArenaFx.bled(level, target);
            if (before > 0.0F && !target.isAlive()) {
               this.killed++;
               this.entityData.set(DATA_FED, this.killed);
               ArenaFx.fed(level, target.position(), this.killed);
            }
         }
      }

      if (!(taken <= 0.0F)) {
         this.drawn += taken;
         if (caster instanceof LivingEntity living && living.isAlive()) {
            living.heal(taken * 0.5F);
         }
      }
   }

   private void bury(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      float force = (this.power * 3.0F + this.drawn * 0.55F) * scale;

      for (LivingEntity target : this.inside(level, this.radius())) {
         if (this.hostile(target, caster)) {
            target.invulnerableTime = 0;
            target.hurt(GsDamage.colosseum(level, caster), force);
         }
      }

      ArenaFx.buried(level, this.position(), (double)this.radius());
      if (caster instanceof ServerPlayer player) {
         GsAdvancements.penned(player, this.killed);
      }
   }

   private List<LivingEntity> inside(ServerLevel level, float reach) {
      AABB box = new AABB(
         this.getX() - (double)reach,
         this.getY() - 8.0,
         this.getZ() - (double)reach,
         this.getX() + (double)reach,
         this.getY() + (double)this.height() + 8.0,
         this.getZ() + (double)reach
      );
      return level.getEntitiesOfClass(LivingEntity.class, box, target -> this.covers(target, reach));
   }

   public boolean covers(Entity entity, float reach) {
      double dx = entity.getX() - this.getX();
      double dz = entity.getZ() - this.getZ();
      return entity.isAlive()
         && dx * dx + dz * dz <= (double)(reach * reach)
         && entity.getY() > this.getY() - 8.0
         && entity.getY() < this.getY() + (double)this.height();
   }

   public boolean covers(Entity entity) {
      return this.covers(entity, this.radius());
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
         GsPresence.leave(7);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 34.0F);
      this.entityData.set(DATA_FED, tag.getInt("Killed"));
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.drawn = tag.getFloat("Drawn");
      this.killed = tag.getInt("Killed");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Radius", this.radius());
      tag.putFloat("Power", this.power);
      tag.putFloat("Drawn", this.drawn);
      tag.putInt("Killed", this.killed);
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
