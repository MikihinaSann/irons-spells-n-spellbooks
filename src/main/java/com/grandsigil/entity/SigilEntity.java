package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.SigilParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.GsFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEffects;
import com.grandsigil.registry.GsEntities;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class SigilEntity extends Entity {
   public static final int RUNES = 12;
   public static final float STRIKE = 8.0F;
   private static final int BIND_TICKS = 30;
   private static final int BIND_INTERVAL = 10;
   public static final int STAGE_CHARGING = 0;
   public static final int STAGE_IGNITING = 1;
   public static final int STAGE_FIRING = 2;
   public static final int DRAW_TICKS = 8;
   public static final int IGNITE_TICKS = 40;
   public static final int FIRE_TICKS = 45;
   private static final int GROUND_INTERVAL = 40;
   private static final EntityDataAccessor<Integer> DATA_RUNES = SynchedEntityData.defineId(SigilEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(SigilEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(SigilEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_WINDOW = SynchedEntityData.defineId(SigilEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(SigilEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   @Nullable
   private UUID boundId;
   private float power = 12.0F;
   private boolean counted;
   private int stageAge;
   public float spin;
   public float spinOld;
   public Terrain ground = Terrain.FLAT;
   private int groundAge;

   public SigilEntity(EntityType<? extends SigilEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SigilEntity(Level level, Vec3 at, LivingEntity caster, LivingEntity bound, float power, float radius, int window) {
      this(GsEntities.SIGIL.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.boundId = bound.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
      this.entityData.set(DATA_WINDOW, window);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_RUNES, 0);
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_WINDOW, 300);
      builder.define(DATA_RADIUS, 6.0F);
   }

   public int runes() {
      return this.entityData.get(DATA_RUNES);
   }

   public int stage() {
      return this.entityData.get(DATA_STAGE);
   }

   public int stageAge() {
      return this.stageAge;
   }

   public int window() {
      return this.entityData.get(DATA_WINDOW);
   }

   public float radius() {
      return this.entityData.get(DATA_RADIUS);
   }

   public boolean perfect() {
      return this.runes() >= 12;
   }

   public Vec3 centre() {
      return this.position();
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("SigilEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(0);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         this.refreshGround();
         this.advanceSpin();
         if (FMLEnvironment.dist.isClient()) {
            SigilParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickCharging(level);
               break;
            case 1:
               this.tickIgniting(level);
               break;
            default:
               this.tickFiring(level);
         }
      }
   }

   private void refreshGround() {
      if (--this.groundAge <= 0) {
         this.groundAge = 40;
         this.ground = Terrain.sample(this.level(), this.centre(), this.radius());
      }
   }

   private void advanceSpin() {
      this.spinOld = this.spin;

      float boost = switch (this.stage()) {
         case 1 -> {
            float t = Mth.clamp((float)this.stageAge() / 40.0F, 0.0F, 1.0F);
            yield 1.0F + 16.0F * t * t;
         }
         case 2 -> 17.0F * Math.max(0.15F, 1.0F - (float)this.stageAge() / 25.0F);
         default -> 1.0F;
      };
      this.spin += 0.013F * boost;
   }

   private void tickCharging(ServerLevel level) {
      if (this.stageAge == 1) {
         GsFx.circleDrawn(level, this.centre(), (double)this.radius());
         if (this.caster(level) instanceof ServerPlayer player) {
            GsAdvancements.award(player, "root");
         }
      }

      if (this.standing(level)) {
         if (this.stageAge % 35 == 0) {
            GsFx.circleHum(level, this.centre(), (float)this.runes() / 12.0F);
         }

         int due = Mth.clamp(Mth.floor((float)this.stageAge * 12.0F / (float)this.window()), 0, 12);

         while (this.runes() < due && this.stage() == 0) {
            this.lightRune(level);
         }

         if (this.stageAge % 5 == 0) {
            this.tellCaster(level);
         }

         if (this.stageAge >= this.window()) {
            this.beginIgnition();
         }
      }
   }

   private boolean standing(ServerLevel level) {
      LivingEntity victim = this.victim(level);
      Entity caster = this.caster(level);
      if (victim != null && caster != null && caster.isAlive() && this.covers(victim)) {
         if (GsConfig.COMMON.sigilBinds.get() && (this.stageAge - 1) % 10 == 0) {
            GsEffects.bind(level, victim, 30);
         }

         return true;
      } else {
         this.fizzle(level);
         return false;
      }
   }

   private void fizzle(ServerLevel level) {
      GsFx.collapses(level, this.centre(), (double)this.radius());
      this.discard();
   }

   private void tickIgniting(ServerLevel level) {
      if (this.stageAge == 1) {
         GsFx.ignition(level, this.centre(), (double)this.radius(), this.perfect());
      }

      if (this.standing(level)) {
         GsFx.ignitionStep(level, this.centre(), (double)this.radius(), this.stageAge, 40);
         if (this.stageAge >= 40) {
            this.entityData.set(DATA_STAGE, 2);
            this.stageAge = 0;
            this.entityData.set(DATA_STAGE_AGE, 0);
         }
      }
   }

   private void tickFiring(ServerLevel level) {
      if (this.stageAge == 1) {
         this.strike(level);
      }

      if (this.stageAge <= 16) {
         GsFx.columnStep(level, this.centre(), (double)this.radius(), this.stageAge, this.perfect());
      }

      if (this.stageAge >= 45) {
         this.discard();
      }
   }

   private boolean lightRune(ServerLevel level) {
      if (this.stage() == 0 && this.runes() < 12) {
         int lit = this.runes() + 1;
         this.entityData.set(DATA_RUNES, lit);
         GsFx.runeLit(level, this.centre(), (double)this.radius(), lit - 1, 12, (float)Math.pow(2.0, (double)(lit - 1) / 12.0));
         this.tellCaster(level);
         if (lit >= 12) {
            this.beginIgnition();
         }

         return true;
      } else {
         return false;
      }
   }

   private void beginIgnition() {
      if (this.stage() == 0) {
         this.entityData.set(DATA_STAGE, 1);
         this.stageAge = 0;
         this.entityData.set(DATA_STAGE_AGE, 0);
      }
   }

   public boolean fedBy(Entity entity) {
      return this.casterId != null && this.casterId.equals(entity.getUUID());
   }

   public boolean covers(Entity entity) {
      double r = (double)this.radius();
      Vec3 c = this.centre();
      double dx = entity.getX() - c.x;
      double dz = entity.getZ() - c.z;
      return dx * dx + dz * dz <= r * r && Math.abs(entity.getY() - c.y) <= 8.0;
   }

   private void strike(ServerLevel level) {
      Entity caster = this.caster(level);
      LivingEntity victim = this.victim(level);
      if (victim != null) {
         float damage = (float)((double)(this.power * 8.0F) * (Double)GsConfig.COMMON.damageMultiplier.get());
         victim.invulnerableTime = 0;
         victim.hurt(GsDamage.column(level, caster), damage);
         boolean killed = !victim.isAlive();
         victim.setDeltaMovement(victim.getDeltaMovement().add(0.0, 0.85, 0.0));
         victim.hurtMarked = true;
         GsFx.struck(level, victim, true);
         GsFx.column(level, this.centre(), (double)this.radius(), this.perfect());
         if (caster instanceof ServerPlayer player) {
            GsAdvancements.fired(player, killed);
         }

         if (GsConfig.COMMON.scorchesGround.get()) {
            this.scorch(level);
         }
      }
   }

   private void scorch(ServerLevel level) {
      double r = (double)this.radius();
      int points = (int)Math.max(12.0, r * 4.0);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         BlockPos at = BlockPos.containing(this.getX() + Math.cos(a) * r, this.getY(), this.getZ() + Math.sin(a) * r);
         if (level.getBlockState(at).isAir() && BaseFireBlock.canBePlacedAt(level, at, Direction.UP)) {
            level.setBlockAndUpdate(at, Blocks.FIRE.defaultBlockState());
         }
      }
   }

   @Nullable
   private LivingEntity victim(ServerLevel level) {
      return this.boundId != null && level.getEntity(this.boundId) instanceof LivingEntity victim && victim.isAlive() ? victim : null;
   }

   @Nullable
   private Entity caster(ServerLevel level) {
      return this.casterId == null ? null : level.getEntity(this.casterId);
   }

   private void tellCaster(ServerLevel level) {
      if (this.caster(level) instanceof ServerPlayer player) {
         int var4 = Math.max(0, this.window() + 40 - this.stageAge);
         player.displayClientMessage(Component.translatable("ui.grandsigil.charging", this.runes(), 12, String.format("%.1f", (double)var4 / 20.0)), true);
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
   public void remove(RemovalReason reason) {
      this.release();
      super.remove(reason);
   }

   private void release() {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(0);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_RUNES, tag.getInt("Runes"));
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_WINDOW, tag.contains("Window") ? tag.getInt("Window") : 300);
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 6.0F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
      this.boundId = tag.hasUUID("Bound") ? tag.getUUID("Bound") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Runes", this.runes());
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putInt("Window", this.window());
      tag.putFloat("Radius", this.radius());
      tag.putFloat("Power", this.power);
      if (this.casterId != null) {
         tag.putUUID("Caster", this.casterId);
      }

      if (this.boundId != null) {
         tag.putUUID("Bound", this.boundId);
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
      return distance < 65536.0;
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
