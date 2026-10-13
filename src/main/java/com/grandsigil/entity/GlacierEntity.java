package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.GlacierParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.GlacierFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEntities;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class GlacierEntity extends Entity {
   public static final float PEAK = 42.0F;
   public static final float KEEL = 14.0F;
   public static final float SPAN = 1.05F;
   public static final int SERACS = 260;
   public static final float ALTITUDE = 170.0F;
   public static final int STAGE_LOOM = 0;
   public static final int STAGE_FALL = 1;
   public static final int STAGE_SETTLE = 2;
   public static final int STAGE_SHATTER = 3;
   public static final int LOOM_TICKS = 60;
   public static final int FALL_TICKS = 30;
   public static final int SETTLE_TICKS = 100;
   public static final int SHATTER_TICKS = 100;
   private static final float RETURN = 1.35F;
   private static final float SHARD = 1.5F;
   private static final int SPREAD_TICKS = 24;
   private static final int SPREAD_BUDGET = 160;
   private static final int CRUSH_BUDGET = 200;
   private static final float BLAST = 1.7F;
   private static final int BREAK_HEIGHT = 4;
   private static final int MAX_FROZEN = 3500;
   private static final int SURFACE_UP = 5;
   private static final int SURFACE_DOWN = 12;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(GlacierEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(GlacierEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(GlacierEntity.class, EntityDataSerializers.FLOAT);
   private final Map<UUID, Float> stored = new HashMap<>();
   @Nullable
   private List<IceField.Serac> seracs;
   private int cleared;
   @Nullable
   private List<BlockPos> columns;
   private int froze;
   @Nullable
   private List<BlockPos> rubble;
   private final Map<BlockPos, BlockState> thawed = new LinkedHashMap<>();
   @Nullable
   private UUID casterId;
   private float power = 20.0F;
   private boolean counted;
   private int stageAge;
   private int sealedCount;
   private static int standing;

   public GlacierEntity(EntityType<? extends GlacierEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public GlacierEntity(Level level, Vec3 at, LivingEntity caster, float power, float radius) {
      this(GsEntities.GLACIER.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_RADIUS, 32.0F);
   }

   public int stage() {
      return this.entityData.get(DATA_STAGE);
   }

   public int stageAge() {
      return this.stageAge;
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (this.level().isClientSide && DATA_STAGE.equals(key)) {
         this.stageAge = this.entityData.get(DATA_STAGE_AGE);
      }
   }

   public static boolean anyStanding() {
      return standing > 0;
   }

   public float radius() {
      return this.entityData.get(DATA_RADIUS);
   }

   public float mass() {
      return this.radius() * 1.05F;
   }

   public float altitude(float partial) {
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0 -> 170.0F - 6.0F * Mth.clamp(age / 60.0F, 0.0F, 1.0F);
         case 1 -> {
            float t = Mth.clamp(age / 30.0F, 0.0F, 1.0F);
            yield 164.0F * (1.0F - t * t);
         }
         default -> 0.0F;
      };
   }

   public float falling(float partial) {
      if (this.stage() != 1) {
         return this.stage() > 1 ? 1.0F : 0.0F;
      } else {
         return Mth.clamp(((float)this.stageAge() + partial) / 30.0F, 0.0F, 1.0F);
      }
   }

   public float sitting(float partial) {
      if (this.stage() < 2) {
         return 0.0F;
      } else {
         return this.stage() > 2 ? 1.0F : Mth.clamp(((float)this.stageAge() + partial) / 100.0F, 0.0F, 1.0F);
      }
   }

   public float breaking(float partial) {
      return this.stage() != 3 ? 0.0F : Mth.clamp(((float)this.stageAge() + partial) / 100.0F, 0.0F, 1.0F);
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
         GsWatchdog.end("GlacierEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(5);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            GlacierParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickLoom(level);
               break;
            case 1:
               this.tickFall(level);
               break;
            case 2:
               this.tickSettle(level);
               break;
            default:
               this.tickShatter(level);
         }
      }
   }

   private void tickLoom(ServerLevel level) {
      if (this.stageAge % 5 == 0) {
         GlacierFx.looming(level, this.position(), (double)this.mass(), (float)this.stageAge / 60.0F);
      }

      if (this.stageAge >= 60) {
         this.advance(1);
      }
   }

   private void tickFall(ServerLevel level) {
      if (this.stageAge % 6 == 0) {
         float near = (float)this.stageAge / 30.0F;
         GlacierFx.falling(level, this.position(), (double)this.mass(), near);
      }

      if (this.stageAge >= 30) {
         this.land(level);
         this.advance(2);
      }
   }

   private void tickSettle(ServerLevel level) {
      this.crush(level);
      this.freeze(level);
      this.hold(level);
      if (this.stageAge % 20 == 0) {
         GlacierFx.groaning(level, this.position(), (double)this.radius());
      }

      if (this.stageAge >= 100) {
         this.advance(3);
      }
   }

   private void tickShatter(ServerLevel level) {
      if (this.stageAge == 1) {
         this.release(level);
      }

      this.thaw(level);
      if (this.stageAge >= 100) {
         this.discard();
      }
   }

   private void advance(int stage) {
      this.entityData.set(DATA_STAGE, stage);
      this.stageAge = 0;
      this.entityData.set(DATA_STAGE_AGE, 0);
   }

   private void land(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      float r = this.mass();

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, this.reach(), other -> this.hostile(other, caster))) {
         double dx = target.getX() - this.getX();
         double dz = target.getZ() - this.getZ();
         double distance = Math.sqrt(dx * dx + dz * dz);
         if (!(distance <= (double)r) && !(distance > (double)(r * 1.7F))) {
            target.invulnerableTime = 0;
            target.hurt(GsDamage.glaciation(level, caster), this.power * 1.5F * scale);
            double push = 1.0 - (distance - (double)r) / (double)(r * 0.70000005F);
            Vec3 out = distance < 0.05
               ? new Vec3(this.random.nextDouble() - 0.5, 0.0, this.random.nextDouble() - 0.5).normalize()
               : new Vec3(dx / distance, 0.0, dz / distance);
            target.setDeltaMovement(target.getDeltaMovement().add(out.scale(1.1 * push)).add(0.0, 0.75 * push, 0.0));
            target.hurtMarked = true;
         }
      }

      GlacierFx.lands(level, this.position(), (double)this.mass());
   }

   private void crush(ServerLevel level) {
      if (GsConfig.COMMON.glaciationBreaksBlocks.get()) {
         List<BlockPos> all = this.rubble(level);
         int budget = 200;

         while (this.cleared < all.size() && budget-- > 0) {
            BlockPos at = all.get(this.cleared);
            this.cleared++;
            if (breakable(level, at)) {
               level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
            }
         }
      }
   }

   private List<BlockPos> rubble(ServerLevel level) {
      if (this.rubble != null) {
         return this.rubble;
      } else {
         LinkedHashSet<BlockPos> unique = new LinkedHashSet<>();
         int foot = Mth.floor(this.getY());

         for (IceField.Serac serac : this.seracs(level)) {
            int span = Math.max(1, Mth.ceil(serac.radius() * 0.8F));
            int deep = Math.max(1, Mth.ceil(4.0F * serac.keel() / 14.0F));
            int cx = Mth.floor(this.getX() + (double)serac.x());
            int cz = Mth.floor(this.getZ() + (double)serac.z());

            for (int dy = 0; dy < deep; dy++) {
               for (int dx = -span; dx <= span; dx++) {
                  for (int dz = -span; dz <= span; dz++) {
                     if (dx * dx + dz * dz <= span * span) {
                        unique.add(new BlockPos(cx + dx, foot - dy, cz + dz));
                     }
                  }
               }
            }
         }

         this.rubble = new ArrayList<>(unique);
         return this.rubble;
      }
   }

   private static boolean breakable(ServerLevel level, BlockPos at) {
      BlockState state = level.getBlockState(at);
      if (state.isAir() || !state.getFluidState().isEmpty()) {
         return false;
      } else {
         return state.hasBlockEntity() ? false : state.getDestroySpeed(level, at) >= 0.0F;
      }
   }

   private List<IceField.Serac> seracs(ServerLevel level) {
      if (this.seracs == null) {
         this.seracs = IceField.layout(this.getId(), this.mass());
      }

      return this.seracs;
   }

   private void freeze(ServerLevel level) {
      if (GsConfig.COMMON.glaciationFreezesGround.get()) {
         List<BlockPos> all = this.columns(level);
         float t = Mth.clamp((float)this.stageAge / 24.0F, 0.0F, 1.0F);
         float spread = Mth.sqrt(t) * this.mass() * 1.7F;
         MutableBlockPos cursor = new MutableBlockPos();
         int budget = 160;

         while (this.froze < all.size() && this.thawed.size() < 3500 && budget-- > 0) {
            BlockPos column = all.get(this.froze);
            float distance = Mth.sqrt((float)(column.getX() * column.getX() + column.getZ() * column.getZ()));
            if (distance > spread) {
               break;
            }

            this.froze++;
            if (rimed(column, distance, spread)) {
               this.ice(level, cursor, column);
            }
         }
      }
   }

   private static boolean rimed(BlockPos column, float distance, float reach) {
      float density = 1.0F - 0.62F * Mth.clamp(distance / Math.max(1.0F, reach), 0.0F, 1.0F);
      int hash = column.getX() * 374761393 + column.getZ() * 668265263;
      hash = (hash ^ hash >>> 13) * 1274126177;
      return (float)(hash >>> 8 & 0xFF) < density * 255.0F;
   }

   private void ice(ServerLevel level, MutableBlockPos cursor, BlockPos column) {
      int x = Mth.floor(this.getX()) + column.getX();
      int z = Mth.floor(this.getZ()) + column.getZ();
      int top = Mth.floor(this.getY()) + 5;
      int bottom = Mth.floor(this.getY()) - 12;

      for (int y = top; y >= bottom; y--) {
         cursor.set(x, y, z);
         BlockState state = level.getBlockState(cursor);
         if (!state.isAir()) {
            if (!freezable(level, cursor, state)) {
               return;
            }

            BlockPos at = cursor.immutable();
            this.thawed.putIfAbsent(at, state);
            level.setBlock(at, grain(at), 2);
            return;
         }
      }
   }

   private List<BlockPos> columns(ServerLevel level) {
      if (this.columns != null) {
         return this.columns;
      } else {
         int r = Mth.ceil(this.mass() * 1.7F);
         List<BlockPos> out = new ArrayList<>();

         for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
               if (dx * dx + dz * dz <= r * r) {
                  out.add(new BlockPos(dx, 0, dz));
               }
            }
         }

         out.sort(Comparator.comparingInt(at -> at.getX() * at.getX() + at.getZ() * at.getZ()));
         this.columns = out;
         return out;
      }
   }

   private static BlockState grain(BlockPos at) {
      int pick = Math.floorMod(at.getX() * 31 + at.getZ() * 17 + at.getY() * 11 + (at.getX() >> 2) * 7 + (at.getZ() >> 2) * 13, 12);
      if (pick < 7) {
         return Blocks.PACKED_ICE.defaultBlockState();
      } else {
         return pick < 11 ? Blocks.BLUE_ICE.defaultBlockState() : Blocks.SNOW_BLOCK.defaultBlockState();
      }
   }

   private static boolean freezable(ServerLevel level, BlockPos at, BlockState state) {
      if (state.hasBlockEntity()) {
         return false;
      } else if (!state.getFluidState().isEmpty()) {
         return true;
      } else if (state.getDestroySpeed(level, at) < 0.0F) {
         return false;
      } else {
         VoxelShape shape = state.getCollisionShape(level, at);
         return !shape.isEmpty();
      }
   }

   private void thaw(ServerLevel level) {
      if (!this.thawed.isEmpty() && GsConfig.COMMON.glaciationThaws.get()) {
         int budget = Math.max(64, this.thawed.size() / Math.max(1, 100 - this.stageAge + 1));

         for (Iterator<Entry<BlockPos, BlockState>> walk = this.thawed.entrySet().iterator(); walk.hasNext() && budget-- > 0; walk.remove()) {
            Entry<BlockPos, BlockState> entry = walk.next();
            if (level.getBlockState(entry.getKey()).is(Blocks.PACKED_ICE)
               || level.getBlockState(entry.getKey()).is(Blocks.BLUE_ICE)
               || level.getBlockState(entry.getKey()).is(Blocks.SNOW_BLOCK)) {
               level.setBlock(entry.getKey(), entry.getValue(), 2);
            }
         }
      }
   }

   @Override
   public void remove(RemovalReason reason) {
      this.release();
      if (!this.level().isClientSide && !this.thawed.isEmpty() && GsConfig.COMMON.glaciationThaws.get() && this.level() instanceof ServerLevel server) {
         for (Entry<BlockPos, BlockState> entry : this.thawed.entrySet()) {
            BlockState now = server.getBlockState(entry.getKey());
            if (now.is(Blocks.PACKED_ICE) || now.is(Blocks.BLUE_ICE) || now.is(Blocks.SNOW_BLOCK)) {
               server.setBlock(entry.getKey(), entry.getValue(), 2);
            }
         }

         this.thawed.clear();
      }

      super.remove(reason);
   }

   private void hold(ServerLevel level) {
      Entity caster = this.caster(level);

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster)) {
            if (this.stored.putIfAbsent(target.getUUID(), 0.0F) == null) {
               this.sealedCount++;
               GlacierFx.sealed(level, target);
            }

            target.setDeltaMovement(Vec3.ZERO);
            target.hasImpulse = true;
            target.hurtMarked = true;
            target.fallDistance = 0.0F;
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 12, 250, false, false));
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 12, 250, false, false));
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 12, 0, false, false));
         }
      }
   }

   public boolean seals(Entity entity) {
      return this.stage() != 3 && this.stored.containsKey(entity.getUUID());
   }

   public void absorb(Entity target, float amount) {
      this.stored.merge(target.getUUID(), amount, Float::sum);
   }

   private void release(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      float biggest = 0.0F;
      List<LivingEntity> caught = new ArrayList<>();

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, this.reach(), this::held)) {
         caught.add(target);
      }

      Map<UUID, Float> ledger = new HashMap<>(this.stored);
      this.stored.clear();

      for (LivingEntity target : caught) {
         float kept = ledger.getOrDefault(target.getUUID(), 0.0F);
         float total = this.power * 4.0F * scale + kept * 1.35F;
         biggest = Math.max(biggest, kept);
         target.invulnerableTime = 0;
         target.hurt(GsDamage.glaciation(level, caster), total);
      }

      GlacierFx.shatters(level, this.position(), (double)this.radius(), caught.size());
      if (caster instanceof ServerPlayer player) {
         GsAdvancements.glaciated(player, this.sealedCount, biggest);
      }
   }

   private boolean held(LivingEntity target) {
      return target.isAlive() && this.stored.containsKey(target.getUUID());
   }

   private AABB reach() {
      float r = this.mass() * 1.7F;
      return new AABB(this.getX() - (double)r, this.getY() - 8.0, this.getZ() - (double)r, this.getX() + (double)r, this.getY() + 42.0, this.getZ() + (double)r);
   }

   private List<LivingEntity> inside(ServerLevel level) {
      return level.getEntitiesOfClass(LivingEntity.class, this.reach(), this::covers);
   }

   public boolean covers(Entity entity) {
      double r = (double)this.mass();
      double dx = entity.getX() - this.getX();
      double dz = entity.getZ() - this.getZ();
      return dx * dx + dz * dz <= r * r && Math.abs(entity.getY() - this.getY()) <= 24.0;
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

   private void release() {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(5);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 32.0F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.sealedCount = tag.getInt("Sealed");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Radius", this.radius());
      tag.putFloat("Power", this.power);
      tag.putInt("Sealed", this.sealedCount);
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
