package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.ArsenalParticles;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.ArsenalFx;
import com.grandsigil.registry.GsEntities;
import java.util.ArrayList;
import java.util.Comparator;
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
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ArsenalEntity extends Entity {
   public static final int STAGE_OPEN = 0;
   public static final int STAGE_VOLLEY = 1;
   public static final int STAGE_CLOSE = 2;
   public static final int OPEN_TICKS = 24;
   public static final int VOLLEY_TICKS = 160;
   public static final int CLOSE_TICKS = 44;
   public static final int GATES = 64;
   public static final int BLADES = 120;
   public static final float REACH = 48.0F;
   private static final int SPREAD = 16;
   public static final int GRAND_OPEN = 108;
   public static final int GRAND_LAUNCH = 130;
   public static final float GRAND_SIZE = 9.0F;
   public static final float GRAND_HEIGHT = 36.0F;
   public static final float GRAND_BACK = 18.0F;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(ArsenalEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(ArsenalEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(ArsenalEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Vector3f> DATA_GRAND = SynchedEntityData.defineId(ArsenalEntity.class, EntityDataSerializers.VECTOR3);
   private static final EntityDataAccessor<Vector3f> DATA_GRAND_GATE = SynchedEntityData.defineId(ArsenalEntity.class, EntityDataSerializers.VECTOR3);
   @Nullable
   private UUID casterId;
   private float power = 22.0F;
   private int stageAge;
   private int launched;
   private int landed;
   private boolean counted;
   private final List<LivingEntity> targets = new ArrayList<>();
   private static final float WALL_WIDTH = 30.0F;
   private static final float WALL_HEIGHT = 14.0F;
   private static final float WALL_RADIUS = 22.0F;

   public ArsenalEntity(EntityType<? extends ArsenalEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public ArsenalEntity(Level level, LivingEntity caster, float power, float yaw) {
      this(GsEntities.ARSENAL.get(), level);
      this.setPos(caster.getX(), caster.getY(), caster.getZ());
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_YAW, yaw);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_YAW, 0.0F);
      builder.define(DATA_GRAND, new Vector3f());
      builder.define(DATA_GRAND_GATE, new Vector3f());
   }

   public Vec3 grand() {
      Vector3f g = this.entityData.get(DATA_GRAND_GATE);
      return new Vec3((double)g.x, (double)g.y, (double)g.z);
   }

   public Vec3 grandMark() {
      Vector3f g = this.entityData.get(DATA_GRAND);
      return new Vec3((double)g.x, (double)g.y, (double)g.z);
   }

   public Vec3 grandFacing() {
      Vec3 d = this.grandMark().subtract(this.grand());
      return d.lengthSqr() < 1.0E-4 ? new Vec3(0.0, -1.0, 0.0) : d.normalize();
   }

   public float grandOpen(float partial) {
      float volley = this.volleyAge(partial);
      if (volley < 108.0F) {
         return 0.0F;
      } else {
         float u = Mth.clamp((volley - 108.0F) / 20.0F, 0.0F, 1.0F);
         float c = 1.7F;
         float t = u - 1.0F;
         float open = 1.0F + (c + 1.0F) * t * t * t + c * t * t;
         if (this.stage() == 2) {
            float shut = Mth.clamp(((float)this.stageAge() + partial - 24.2F) / 19.8F, 0.0F, 1.0F);
            open *= 1.0F - shut * shut;
         }

         return open;
      }
   }

   public int stage() {
      return this.entityData.get(DATA_STAGE);
   }

   public int stageAge() {
      return this.stageAge;
   }

   public float yaw() {
      return this.entityData.get(DATA_YAW);
   }

   public float volleyAge(float partial) {
      return switch (this.stage()) {
         case 0 -> -1.0F;
         case 1 -> (float)this.stageAge() + partial;
         default -> (float)(160 + this.stageAge()) + partial;
      };
   }

   public float open(float partial) {
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0 -> Mth.clamp(age / 24.0F, 0.0F, 1.0F);
         case 1 -> 1.0F;
         default -> 1.0F - Mth.clamp(age / 44.0F, 0.0F, 1.0F);
      };
   }

   public static Vector3f local(int i) {
      float p = (float)Math.sqrt((double)(((float)i + 0.5F) / 64.0F));
      float theta = (float)i * 2.3999631F;
      float x = p * Mth.cos(theta) * 30.0F;
      float y = p * Mth.sin(theta) * 14.0F;
      float az = x / 22.0F;
      float depth = 22.0F + 5.0F * frac((float)i * 0.4142136F);
      return new Vector3f(Mth.sin(az) * depth, 16.5F + y, -Mth.cos(az) * depth);
   }

   public static float size(int i) {
      return 1.7F + 1.8F * frac((float)i * 0.324718F + 0.11F);
   }

   public static Vec3 gate(Vec3 anchor, float yaw, int i) {
      Vector3f l = local(i);
      Vec3 forward = forward(yaw);
      Vec3 right = right(yaw);
      return anchor.add(right.scale((double)l.x)).add(0.0, (double)l.y, 0.0).add(forward.scale((double)l.z));
   }

   public static Vec3 facing(float yaw, int i) {
      Vector3f l = local(i);
      Vec3 forward = forward(yaw);
      Vec3 right = right(yaw);
      return forward.scale(3.0).add(right.scale((double)(-l.x) * 0.1)).add(0.0, -((double)l.y - 2.0) * 0.08, 0.0).normalize();
   }

   public static Vec3 forward(float yaw) {
      float r = yaw * (float) (Math.PI / 180.0);
      return new Vec3((double)(-Mth.sin(r)), 0.0, (double)Mth.cos(r));
   }

   public static Vec3 right(float yaw) {
      float r = yaw * (float) (Math.PI / 180.0);
      return new Vec3((double)(-Mth.cos(r)), 0.0, (double)(-Mth.sin(r)));
   }

   public static int launchTick(int k) {
      float t = ((float)k + 1.0F) / 120.0F;
      return Mth.floor(131.2F * (float)Math.pow((double)t, 0.68));
   }

   public static int gateOf(int k) {
      return Math.floorMod(k * 7 + 3, 64);
   }

   private static float frac(float v) {
      return v - (float)Mth.floor(v);
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("ArsenalEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(12);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            ArsenalParticles.ambient(this);
         }
      } else {
         ServerLevel level;
         Entity caster;
         boolean var10000;
         label83: {
            this.stageAge++;
            this.entityData.set(DATA_STAGE_AGE, this.stageAge);
            level = (ServerLevel)this.level();
            caster = this.caster(level);
            if (caster instanceof LivingEntity living && living.isAlive() && !living.isRemoved()) {
               var10000 = true;
               break label83;
            }

            var10000 = false;
         }

         boolean standing = var10000;
         if (standing) {
            this.setPos(caster.getX(), caster.getY(), caster.getZ());
         } else if (this.stage() != 2) {
            this.advance(2);
            return;
         }

         switch (this.stage()) {
            case 0:
               if (this.stageAge == 1) {
                  ArsenalFx.opens(level, this.position());
               }

               if (this.stageAge >= 24) {
                  this.advance(1);
               }
               break;
            case 1:
               if (this.stageAge % 10 == 1) {
                  this.look(level, (LivingEntity)caster);
               }

               while (this.launched < 120 && this.stageAge >= launchTick(this.launched)) {
                  this.launch(level, (LivingEntity)caster, this.launched);
                  this.launched++;
               }

               if (this.stageAge == 108) {
                  Vec3 mark = this.mark(level, (LivingEntity)caster);
                  Vec3 back = new Vec3(this.getX() - mark.x, 0.0, this.getZ() - mark.z);
                  back = back.lengthSqr() < 1.0 ? forward(this.yaw()).scale(-1.0) : back.normalize();
                  Vec3 gate = mark.add(back.scale(18.0)).add(0.0, 36.0, 0.0);
                  this.entityData.set(DATA_GRAND, new Vector3f((float)mark.x, (float)mark.y, (float)mark.z));
                  this.entityData.set(DATA_GRAND_GATE, new Vector3f((float)gate.x, (float)gate.y, (float)gate.z));
                  ArsenalFx.grandOpens(level, this.grand());
               }

               if (this.stageAge == 130) {
                  level.addFreshEntity(BladeEntity.colossus(level, this, this.grand(), this.grandMark(), this.power));
                  ArsenalFx.drawsColossus(level, this.grand());
               }

               if (this.stageAge >= 160) {
                  this.advance(2);
               }
               break;
            default:
               if (this.stageAge == 1) {
                  ArsenalFx.closes(level, this.position());
                  if (caster instanceof ServerPlayer player) {
                     GsAdvancements.armed(player, this.landed, this.launched);
                  }
               }

               if (this.stageAge >= 44) {
                  this.discard();
               }
         }
      }
   }

   private void advance(int stage) {
      this.entityData.set(DATA_STAGE, stage);
      this.stageAge = 0;
      this.entityData.set(DATA_STAGE_AGE, 0);
   }

   private void look(ServerLevel level, LivingEntity caster) {
      this.targets.clear();
      AABB box = new AABB(this.getX() - 48.0, this.getY() - 12.0, this.getZ() - 48.0, this.getX() + 48.0, this.getY() + 24.0, this.getZ() + 48.0);

      for (LivingEntity found : level.getEntitiesOfClass(LivingEntity.class, box, e -> hostile(e, caster, this.casterId) && e.distanceToSqr(this) <= 2304.0)) {
         this.targets.add(found);
      }

      this.targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(this)));
      if (this.targets.size() > 16) {
         this.targets.subList(16, this.targets.size()).clear();
      }
   }

   private void launch(ServerLevel level, LivingEntity caster, int k) {
      int g = gateOf(k);
      Vec3 from = gate(this.position(), this.yaw(), g);
      Vec3 out = facing(this.yaw(), g);
      this.targets.removeIf(e -> !e.isAlive());
      LivingEntity target = this.targets.isEmpty() ? null : this.targets.get(k % this.targets.size());
      Vec3 aim = target != null ? target.getBoundingBox().getCenter() : this.groundAhead(level, caster, k);
      level.addFreshEntity(new BladeEntity(level, this, from, out, target, aim, this.power, k));
      ArsenalFx.launched(level, from, k);
   }

   private Vec3 groundAhead(ServerLevel level, LivingEntity caster, int k) {
      float spread = (frac((float)k * 0.618034F) - 0.5F) * 1.4F;
      float reach = 10.0F + 14.0F * frac((float)k * 0.381966F);
      Vec3 dir = forward(this.yaw() + spread * (180.0F / (float)Math.PI));
      Vec3 high = this.position().add(dir.scale((double)reach)).add(0.0, 6.0, 0.0);
      BlockHitResult hit = level.clip(new ClipContext(high, high.add(0.0, -24.0, 0.0), Block.COLLIDER, Fluid.NONE, this));
      return hit.getType() == Type.MISS ? high.add(0.0, -6.0, 0.0) : hit.getLocation();
   }

   private Vec3 mark(ServerLevel level, LivingEntity caster) {
      this.look(level, caster);
      LivingEntity best = null;
      int crowd = -1;

      for (LivingEntity t : this.targets) {
         int near = 0;

         for (LivingEntity o : this.targets) {
            if (o != t && o.distanceToSqr(t) < 64.0) {
               near++;
            }
         }

         if (near > crowd) {
            crowd = near;
            best = t;
         }
      }

      if (best != null) {
         return best.position();
      } else {
         Vec3 high = this.position().add(forward(this.yaw()).scale(20.0)).add(0.0, 8.0, 0.0);
         BlockHitResult hit = level.clip(new ClipContext(high, high.add(0.0, -32.0, 0.0), Block.COLLIDER, Fluid.NONE, this));
         return hit.getType() == Type.MISS ? high.add(0.0, -8.0, 0.0) : hit.getLocation();
      }
   }

   void landed() {
      this.landed++;
   }

   static boolean hostile(LivingEntity target, @Nullable Entity caster, @Nullable UUID casterId) {
      if (target.isAlive() && target != caster && !(target instanceof ArmorStand)) {
         if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
         }

         if (caster != null && target.isAlliedTo(caster)) {
            return false;
         } else {
            if (target instanceof TamableAnimal tame && casterId != null && casterId.equals(tame.getOwnerUUID())) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Nullable
   Entity caster(ServerLevel level) {
      return this.casterId == null ? null : level.getEntity(this.casterId);
   }

   @Nullable
   UUID casterId() {
      return this.casterId;
   }

   @Override
   public void remove(RemovalReason reason) {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(12);
      }

      super.remove(reason);
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
      this.entityData.set(DATA_YAW, tag.getFloat("Yaw"));
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.launched = tag.getInt("Launched");
      this.landed = tag.getInt("Landed");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putFloat("Yaw", this.yaw());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Power", this.power);
      tag.putInt("Launched", this.launched);
      tag.putInt("Landed", this.landed);
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
