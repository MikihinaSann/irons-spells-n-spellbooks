package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.config.GsConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class FieldEntity extends Entity {
   public static final int STAGE_SPREAD = 0;
   public static final int STAGE_HOLD = 1;
   public static final int STAGE_FADE = 2;
   public static final int SPREAD_TICKS = 60;
   public static final int FADE_TICKS = 60;
   public static final int LIFE_TICKS = 1200;
   protected static final int TOUCH_INTERVAL = 10;
   private static final int CONTEST_INTERVAL = 20;
   private static final int ABSENCE = 60;
   private static final int CEILING = 400;
   private static final int SURFACE_UP = 24;
   private static final int SURFACE_DOWN = 12;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(FieldEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(FieldEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(FieldEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   private float power = 20.0F;
   private int stageAge;
   private int life;
   private int absent;
   private boolean muted;
   private final Map<BlockPos, BlockState> laid = new LinkedHashMap<>();
   @Nullable
   private List<BlockPos> columns;
   private int paved;
   @Nullable
   private List<BlockPos> lifting;
   private int lifted;
   private boolean counted;

   protected FieldEntity(EntityType<? extends FieldEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   protected FieldEntity(EntityType<? extends FieldEntity> type, Level level, Vec3 at, LivingEntity caster, float power, float radius) {
      this(type, level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
   }

   @Nullable
   protected abstract BlockState pave(ServerLevel var1, BlockPos var2, BlockState var3, float var4);

   @Nullable
   protected BlockState clear(ServerLevel level, BlockPos at, BlockState was) {
      return null;
   }

   protected int relief(float spread, int dx, int dz) {
      return 0;
   }

   protected BlockState raised(int layer, int height, float spread, BlockState ground) {
      return ground;
   }

   protected abstract boolean ours(BlockState var1);

   protected abstract void favour(ServerLevel var1, LivingEntity var2);

   protected abstract void oppose(ServerLevel var1, LivingEntity var2);

   protected void opened(ServerLevel level) {
   }

   protected void standing(ServerLevel level) {
   }

   protected void closed(ServerLevel level) {
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_RADIUS, 30.0F);
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
      float age = (float)this.stageAge + partial;

      return switch (this.stage()) {
         case 0 -> Mth.clamp(age / 60.0F, 0.0F, 1.0F);
         case 1 -> 1.0F;
         default -> Mth.clamp(1.0F - age / 60.0F, 0.0F, 1.0F);
      };
   }

   public float strength() {
      return this.power;
   }

   protected float power() {
      return this.power;
   }

   public boolean muted() {
      return this.muted;
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("FieldEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(9);
      }

      this.stageAge++;
      if (this.level().isClientSide) {
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         this.clientTick();
      } else {
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         this.life++;
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               if (this.stageAge == 1) {
                  this.opened(level);
               }

               this.repave(level);
               this.live(level);
               if (this.stageAge >= 60) {
                  this.advance(1);
               }
               break;
            case 1:
               this.repave(level);
               this.live(level);
               if (this.life >= 1140) {
                  this.advance(2);
               }
               break;
            default:
               if (this.stageAge == 1) {
                  this.closed(level);
               }

               this.unpave(level);
               if (this.stageAge >= 60 && this.laid.isEmpty()) {
                  this.discard();
               }
         }
      }
   }

   protected void clientTick() {
   }

   private void live(ServerLevel level) {
      if (this.held(level)) {
         if (this.stageAge % 20 == 0) {
            this.contest(level);
         }

         if (this.stageAge % 10 == 0) {
            this.touch(level);
         }

         if (this.stageAge % 40 == 0) {
            this.standing(level);
         }
      }
   }

   private boolean held(ServerLevel level) {
      Entity caster = this.caster(level);
      boolean inside = caster != null && caster.isAlive() && this.covers(caster);
      this.absent = inside ? 0 : this.absent + 1;
      if (this.absent > 60) {
         this.advance(2);
         return false;
      } else {
         return true;
      }
   }

   private void touch(ServerLevel level) {
      Entity caster = this.caster(level);
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int touched = 0;

      for (LivingEntity living : this.inside(level)) {
         if (this.allied(living, caster)) {
            this.favour(level, living);
         } else if (!this.muted) {
            this.oppose(level, living);
         }

         if (++touched >= cap) {
            return;
         }
      }
   }

   private void contest(ServerLevel level) {
      float mine = this.strength();
      boolean stalemate = false;
      double reach = (double)this.radius() * 2.0;

      for (FieldEntity other : level.getEntitiesOfClass(FieldEntity.class, this.getBoundingBox().inflate(reach), field -> field != this && field.isAlive())) {
         if (!this.sameSide(level, other)) {
            double gap = Math.sqrt(this.distanceToSqr(other));
            if (!(gap > (double)(this.radius() + other.radius()))) {
               float theirs = other.strength();
               if (mine > theirs) {
                  other.overrule(level);
               } else {
                  if (mine < theirs) {
                     this.overrule(level);
                     return;
                  }

                  stalemate = true;
               }
            }
         }
      }

      this.muted = stalemate;
   }

   private void overrule(ServerLevel level) {
      if (this.stage() != 2) {
         this.advance(2);
         this.closed(level);
      }
   }

   private boolean sameSide(ServerLevel level, FieldEntity other) {
      Entity mine = this.caster(level);
      Entity theirs = other.caster(level);
      return mine != null && theirs != null && (mine == theirs || mine.isAlliedTo(theirs));
   }

   private void advance(int stage) {
      if (this.stage() != stage) {
         this.entityData.set(DATA_STAGE, stage);
         this.stageAge = 0;
         this.entityData.set(DATA_STAGE_AGE, 0);
      }
   }

   private void repave(ServerLevel level) {
      if (GsConfig.COMMON.fieldsPave.get()) {
         List<BlockPos> all = this.columns(level);
         float reach = this.radius();
         float front = this.stage() == 0 ? reach * Mth.clamp((float)this.stageAge / 60.0F, 0.0F, 1.0F) : reach;
         float edge = front * front;
         int budget = 400;
         MutableBlockPos cursor = new MutableBlockPos();

         while (this.paved < all.size() && budget > 0) {
            BlockPos column = all.get(this.paved);
            float d2 = (float)(column.getX() * column.getX()) + (float)column.getZ() * (float)column.getZ();
            if (d2 > edge) {
               return;
            }

            this.paved++;
            budget -= this.column(level, cursor, column, Mth.clamp(Mth.sqrt(d2) / reach, 0.0F, 1.0F));
         }
      }
   }

   private int column(ServerLevel level, MutableBlockPos cursor, BlockPos column, float spread) {
      int x = Mth.floor(this.getX()) + column.getX();
      int z = Mth.floor(this.getZ()) + column.getZ();
      int top = Mth.floor(this.getY()) + 24;
      int bottom = Mth.floor(this.getY()) - 12;
      int changed = 0;

      for (int y = top; y >= bottom; y--) {
         cursor.set(x, y, z);
         BlockState state = level.getBlockState(cursor);
         if (!state.isAir()) {
            if (!pavable(level, cursor, state)) {
               return changed;
            }

            BlockState swept = this.clear(level, cursor, state);
            if (swept == null) {
               BlockState made = this.pave(level, cursor.immutable(), state, spread);
               if (made == null) {
                  return changed;
               }

               BlockPos floor = cursor.immutable();
               changed += this.put(level, floor, made);
               return changed + this.build(level, cursor, floor, made, spread, column);
            }

            changed += this.put(level, cursor.immutable(), swept);
         }
      }

      return changed;
   }

   private int build(ServerLevel level, MutableBlockPos cursor, BlockPos floor, BlockState ground, float spread, BlockPos column) {
      int height = this.relief(spread, column.getX(), column.getZ());
      int changed = 0;

      for (int layer = 0; layer < height; layer++) {
         cursor.set(floor.getX(), floor.getY() + 1 + layer, floor.getZ());
         BlockState there = level.getBlockState(cursor);
         if (!there.isAir() && !this.ours(there)) {
            break;
         }

         changed += this.put(level, cursor.immutable(), this.raised(layer, height, spread, ground));
      }

      return changed;
   }

   private int put(ServerLevel level, BlockPos at, BlockState state) {
      this.laid.putIfAbsent(at, level.getBlockState(at));
      level.setBlock(at, state, 2);
      return 1;
   }

   private static boolean pavable(ServerLevel level, BlockPos at, BlockState state) {
      return state.getFluidState().isEmpty() && !state.hasBlockEntity() ? state.getDestroySpeed(level, at) >= 0.0F : false;
   }

   private void unpave(ServerLevel level) {
      if (!this.laid.isEmpty()) {
         if (this.lifting == null) {
            this.lifting = new ArrayList<>(this.laid.keySet());
            Collections.reverse(this.lifting);
         }

         float reach = this.radius();
         float front = reach * Mth.clamp(1.0F - (float)this.stageAge / 60.0F, 0.0F, 1.0F);
         float edge = front * front;
         int cx = Mth.floor(this.getX());
         int cz = Mth.floor(this.getZ());
         int budget = 400;

         while (this.lifted < this.lifting.size() && budget > 0) {
            BlockPos at = this.lifting.get(this.lifted);
            float dx = (float)(at.getX() - cx);
            float dz = (float)(at.getZ() - cz);
            if (dx * dx + dz * dz < edge) {
               return;
            }

            this.lifted++;
            BlockState was = this.laid.remove(at);
            if (was != null) {
               budget--;
               if (this.ours(level.getBlockState(at))) {
                  level.setBlock(at, was, 2);
               }
            }
         }
      }
   }

   @Override
   public void remove(RemovalReason reason) {
      if (!this.level().isClientSide && !this.laid.isEmpty() && this.level() instanceof ServerLevel server) {
         for (Entry<BlockPos, BlockState> entry : this.laid.entrySet()) {
            if (this.ours(server.getBlockState(entry.getKey()))) {
               server.setBlock(entry.getKey(), entry.getValue(), 2);
            }
         }

         this.laid.clear();
      }

      this.release();
      super.remove(reason);
   }

   private void release() {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(9);
      }
   }

   private List<BlockPos> columns(ServerLevel level) {
      if (this.columns != null) {
         return this.columns;
      } else {
         int r = Mth.ceil(this.radius());
         List<BlockPos> out = new ArrayList<>();

         for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
               int d2 = dx * dx + dz * dz;
               if (d2 <= r * r) {
                  out.add(new BlockPos(dx, 0, dz));
               }
            }
         }

         out.sort((a, b) -> Integer.compare(a.getX() * a.getX() + a.getZ() * a.getZ(), b.getX() * b.getX() + b.getZ() * b.getZ()));
         this.columns = out;
         return this.columns;
      }
   }

   protected List<LivingEntity> inside(ServerLevel level) {
      float r = this.radius();
      AABB box = new AABB(
         this.getX() - (double)r, this.getY() - 12.0, this.getZ() - (double)r, this.getX() + (double)r, this.getY() + 20.0, this.getZ() + (double)r
      );
      return level.getEntitiesOfClass(LivingEntity.class, box, this::covers);
   }

   public boolean covers(Entity entity) {
      double r = (double)this.radius();
      double dx = entity.getX() - this.getX();
      double dz = entity.getZ() - this.getZ();
      return entity.isAlive() && dx * dx + dz * dz <= r * r && Math.abs(entity.getY() - this.getY()) <= 20.0;
   }

   protected boolean allied(LivingEntity living, @Nullable Entity caster) {
      if (caster == null) {
         return false;
      } else if (living != caster && !living.isAlliedTo(caster)) {
         if (living instanceof TamableAnimal tame && this.casterId != null && this.casterId.equals(tame.getOwnerUUID())) {
            return true;
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean raisedBy(Entity entity) {
      return this.casterId != null && this.casterId.equals(entity.getUUID());
   }

   @Nullable
   protected Entity caster(ServerLevel level) {
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
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 30.0F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.life = tag.getInt("Life");
      this.power = tag.getFloat("Power");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putInt("Life", this.life);
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
      return distance < 36864.0;
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
