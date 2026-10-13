package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.PhoenixParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.PhoenixFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEntities;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class PhoenixEntity extends Entity {
   public static final int IGNITE = 24;
   public static final int ASCEND = 30;
   public static final int LAP = 72;
   public static final int CLIMB = 24;
   public static final int DIVE = 14;
   public static final int BURST = 56;
   public static final int FADE = 40;
   public static final int T_ASCEND = 24;
   public static final int T_CIRCLE = 54;
   public static final int T_CLIMB = 198;
   public static final int T_DIVE = 222;
   public static final int T_IMPACT = 236;
   public static final int T_FADE = 292;
   public static final int LIFE = 332;
   public static final float OUTER = 34.0F;
   public static final float INNER = 20.0F;
   public static final float HEIGHT = 26.0F;
   public static final float APEX = 58.0F;
   public static final float BLAST = 16.0F;
   public static final int FALL = 8;
   public static final float RING_HALF = 3.4F;
   public static final float BODY_REACH = 11.0F;
   public static final float BURST_SHARE = 2.4F;
   public static final float RING_SHARE = 0.14F;
   public static final float BODY_SHARE = 0.35F;
   public static final int TRAIL = 576;
   private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(PhoenixEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Vector3f> DATA_START = SynchedEntityData.defineId(PhoenixEntity.class, EntityDataSerializers.VECTOR3);
   private static final EntityDataAccessor<Float> DATA_ENTRY = SynchedEntityData.defineId(PhoenixEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_TURN = SynchedEntityData.defineId(PhoenixEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   private float power = 21.0F;
   private int age;
   private int kills;
   private boolean counted;
   private final Set<UUID> burned = new HashSet<>();
   @Nullable
   private float[] floor;

   public PhoenixEntity(EntityType<? extends PhoenixEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public PhoenixEntity(Level level, LivingEntity caster, Vec3 target, float power) {
      this(GsEntities.PHOENIX.get(), level);
      this.setPos(target.x, target.y, target.z);
      this.casterId = caster.getUUID();
      this.power = power;
      Vec3 start = caster.position().add(0.0, 1.0, 0.0).subtract(target);
      this.entityData.set(DATA_START, new Vector3f((float)start.x, (float)start.y, (float)start.z));
      double flat = Math.sqrt(start.x * start.x + start.z * start.z);
      float entry;
      if (flat > 2.0) {
         entry = (float)Math.atan2(start.z, start.x);
      } else {
         float yaw = caster.getYRot() * (float) (Math.PI / 180.0);
         entry = (float)Math.atan2((double)Mth.cos(yaw), (double)(-Mth.sin(yaw)));
      }

      this.entityData.set(DATA_ENTRY, entry);
      Vec3 look = caster.getLookAngle();
      double side = look.x * -start.z + look.z * start.x;
      this.entityData.set(DATA_TURN, side >= 0.0 ? 1.0F : -1.0F);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_AGE, 0);
      builder.define(DATA_START, new Vector3f(0.0F, 1.0F, 0.0F));
      builder.define(DATA_ENTRY, 0.0F);
      builder.define(DATA_TURN, 1.0F);
   }

   public Vector3f start() {
      return this.entityData.get(DATA_START);
   }

   public float entry() {
      return this.entityData.get(DATA_ENTRY);
   }

   public float turn() {
      return this.entityData.get(DATA_TURN);
   }

   public float age(float partial) {
      return (float)this.age + partial;
   }

   public static Vector3f at(float t, Vector3f start, float entry, float turn) {
      if (t < 24.0F) {
         float u = smooth(Mth.clamp(t / 24.0F, 0.0F, 1.0F));
         return new Vector3f(start).add(0.0F, 0.6F + 11.0F * u, 0.0F);
      } else if (t < 54.0F) {
         float u = (t - 24.0F) / 30.0F;
         Vector3f p0 = new Vector3f(start).add(0.0F, 11.6F, 0.0F);
         Vector3f m0 = new Vector3f(0.0F, 32.0F, 0.0F);
         Vector3f p1 = orbit(0.0F, entry, turn);
         Vector3f m1 = orbitVelocity(0.0F, entry, turn).mul(30.0F);
         return hermite(p0, m0, p1, m1, u);
      } else if (t < 198.0F) {
         return orbit((t - 54.0F) / 144.0F, entry, turn);
      } else if (t < 222.0F) {
         float u = (t - 198.0F) / 24.0F;
         Vector3f p0 = orbit(1.0F, entry, turn);
         Vector3f m0 = orbitVelocity(1.0F, entry, turn).mul(21.599998F).add(0.0F, 36.0F, 0.0F);
         Vector3f p1 = apex(entry);
         Vector3f m1 = new Vector3f(-Mth.cos(entry), 0.0F, -Mth.sin(entry)).mul(6.0F).add(0.0F, 6.0F, 0.0F);
         return hermite(p0, m0, p1, m1, u);
      } else {
         float u = Mth.clamp((t - 222.0F) / 14.0F, 0.0F, 1.0F);
         Vector3f a = apex(entry);
         float k = (float)Math.pow((double)u, 2.2);
         return new Vector3f(a).lerp(new Vector3f(0.0F, 0.6F, 0.0F), k);
      }
   }

   private static Vector3f apex(float entry) {
      return new Vector3f(Mth.cos(entry) * 8.0F, 58.0F, Mth.sin(entry) * 8.0F);
   }

   public static Vector3f orbit(float u, float entry, float turn) {
      float a = entry + turn * (float) (Math.PI * 2) * 2.0F * u;
      float r = radius(u);
      float bob = 2.0F * Mth.sin(u * (float) (Math.PI * 2) * 3.0F);
      return new Vector3f(Mth.cos(a) * r, 26.0F + bob, Mth.sin(a) * r);
   }

   public static float radius(float u) {
      return u <= 0.5F ? 34.0F : Mth.lerp(smooth(Mth.clamp((u - 0.5F) / 0.1F, 0.0F, 1.0F)), 34.0F, 20.0F);
   }

   private static Vector3f orbitVelocity(float u, float entry, float turn) {
      float a = entry + turn * (float) (Math.PI * 2) * 2.0F * u;
      float w = turn * (float) (Math.PI * 2) / 72.0F;
      float r = radius(u);
      return new Vector3f(-Mth.sin(a) * r * w, 0.0F, Mth.cos(a) * r * w);
   }

   private static Vector3f hermite(Vector3f p0, Vector3f m0, Vector3f p1, Vector3f m1, float u) {
      float u2 = u * u;
      float u3 = u2 * u;
      float h00 = 2.0F * u3 - 3.0F * u2 + 1.0F;
      float h10 = u3 - 2.0F * u2 + u;
      float h01 = -2.0F * u3 + 3.0F * u2;
      float h11 = u3 - u2;
      return new Vector3f(p0).mul(h00).add(new Vector3f(m0).mul(h10)).add(new Vector3f(p1).mul(h01)).add(new Vector3f(m1).mul(h11));
   }

   public static float smooth(float u) {
      return u * u * (3.0F - 2.0F * u);
   }

   public static float trailTime(int i) {
      return 54.0F + ((float)i + 0.5F) * 144.0F / 576.0F;
   }

   public static float burning(int i, float t) {
      float lit = t - trailTime(i) - 8.0F;
      if (lit <= 0.0F) {
         return 0.0F;
      } else {
         float catching = Math.min(1.0F, lit / 6.0F);
         float dying = 1.0F - Mth.clamp((t - 292.0F) / 40.0F, 0.0F, 1.0F);
         return catching * dying;
      }
   }

   public float[] floor() {
      if (this.floor == null) {
         float[] read = new float[576];
         Vector3f s = this.start();
         float e = this.entry();
         float turn = this.turn();
         MutableBlockPos cursor = new MutableBlockPos();

         for (int i = 0; i < 576; i++) {
            Vector3f p = at(trailTime(i), s, e, turn);
            read[i] = surface(this.level(), cursor, this.getX() + (double)p.x, this.getY(), this.getZ() + (double)p.z) - (float)this.getY();
         }

         this.floor = read;
      }

      return this.floor;
   }

   private static float surface(BlockGetter level, MutableBlockPos cursor, double x, double baseY, double z) {
      int col = Mth.floor(x);
      int row = Mth.floor(z);
      int top = Mth.floor(baseY) + 12;
      int bottom = Mth.floor(baseY) - 20;

      for (int y = top; y >= bottom; y--) {
         cursor.set(col, y, row);
         if (!level.getBlockState(cursor).getCollisionShape(level, cursor).isEmpty()) {
            cursor.set(col, y + 1, row);
            if (level.getBlockState(cursor).getCollisionShape(level, cursor).isEmpty()) {
               return (float)(y + 1);
            }
         }
      }

      return (float)baseY;
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("PhoenixEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(14);
      }

      this.age++;
      if (this.level().isClientSide) {
         int served = this.entityData.get(DATA_AGE);
         if (Math.abs(served - this.age) > 2) {
            this.age = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            PhoenixParticles.tick(this);
         }
      } else {
         this.entityData.set(DATA_AGE, this.age);
         ServerLevel level = (ServerLevel)this.level();
         Entity caster = this.casterId == null ? null : level.getEntity(this.casterId);
         Vector3f s = this.start();
         float e = this.entry();
         float turn = this.turn();
         Vec3 bird = this.position().add(vec(at((float)this.age, s, e, turn)));
         if (this.age == 1) {
            PhoenixFx.ignites(level, this.position().add(vec(s)));
         }

         if (this.age == 24) {
            PhoenixFx.cries(level, bird, 1.0F);
         }

         if (this.age > 24 && this.age < 236 && this.age % 18 == 0) {
            PhoenixFx.beats(level, bird, this.age);
         }

         if (this.age == 126) {
            PhoenixFx.cries(level, bird, 1.15F);
         }

         if (this.age >= 54 && this.age < 292 && this.age % 12 == 0) {
            PhoenixFx.roars(level, this.position(), this.age);
         }

         if (this.age == 222) {
            PhoenixFx.dives(level, bird);
         }

         if (this.age > 24 && this.age < 236 && this.age % 10 == 0) {
            this.scorch(level, caster, bird);
         }

         if (this.age >= 62 && this.age < 312 && this.age % 10 == 5) {
            this.rings(level, caster);
         }

         if (this.age == 236) {
            this.burst(level, caster);
         }

         if (this.age == 248) {
            PhoenixFx.reborn(level, this.position().add(0.0, 4.0, 0.0));
         }

         if (this.age == 292 && caster instanceof ServerPlayer player) {
            GsAdvancements.reborn(player, this.kills);
         }

         if (this.age >= 332) {
            this.discard();
         }
      }
   }

   private void scorch(ServerLevel level, @Nullable Entity caster, Vec3 bird) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      AABB box = new AABB(bird, bird).inflate(11.0);

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         if (!(target.getBoundingBox().getCenter().distanceToSqr(bird) > 121.0)) {
            this.hurt(level, caster, target, this.power * 0.35F * scale);
            target.igniteForSeconds(4.0F);
         }
      }
   }

   private void rings(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      float[] ground = this.floor();
      Vector3f s = this.start();
      float e = this.entry();
      float turn = this.turn();
      float reach = 38.4F;
      AABB box = new AABB(
         this.getX() - (double)reach,
         this.getY() - 20.0,
         this.getZ() - (double)reach,
         this.getX() + (double)reach,
         this.getY() + 14.0,
         this.getZ() + (double)reach
      );
      int caught = 0;
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         if (caught >= cap) {
            break;
         }

         double tx = target.getX() - this.getX();
         double tz = target.getZ() - this.getZ();
         float ty = (float)(target.getY() - this.getY());
         boolean hit = false;

         for (int i = 0; i < 576 && !hit; i++) {
            if (!(burning(i, (float)this.age) < 0.3F)) {
               Vector3f p = at(trailTime(i), s, e, turn);
               double dx = tx - (double)p.x;
               double dz = tz - (double)p.z;
               hit = dx * dx + dz * dz < 11.56F && ty > ground[i] - 2.0F && ty < ground[i] + 6.0F;
            }
         }

         if (hit) {
            caught++;
            this.hurt(level, caster, target, this.power * 0.14F * scale);
            target.igniteForSeconds(5.0F);
         }
      }
   }

   private void burst(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      Vec3 at = this.position().add(0.0, 0.6, 0.0);
      PhoenixFx.bursts(level, at);
      float reach = 25.6F;
      AABB box = new AABB(at, at).inflate((double)reach);
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int caught = 0;

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         double d = target.getBoundingBox().getCenter().distanceTo(at);
         if (!(d > (double)reach) && caught < cap) {
            caught++;
            float falloff = d <= 16.0 ? 1.0F : (float)(1.0 - (d - 16.0) / (double)(reach - 16.0F));
            target.invulnerableTime = 0;
            this.hurt(level, caster, target, this.power * 2.4F * scale * falloff);
            target.igniteForSeconds(8.0F);
            Vec3 push = target.position().subtract(at);
            Vec3 flat = new Vec3(push.x, 0.0, push.z);
            flat = flat.lengthSqr() < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : flat.normalize();
            target.push(flat.x * 2.0 * (double)falloff, 0.9 * (double)falloff, flat.z * 2.0 * (double)falloff);
            target.hurtMarked = true;
         }
      }
   }

   private void hurt(ServerLevel level, @Nullable Entity caster, LivingEntity target, float amount) {
      boolean alive = target.isAlive();
      target.hurt(GsDamage.phoenix(level, (Entity)(caster == null ? this : caster)), amount);
      if (alive && !target.isAlive() && this.burned.add(target.getUUID())) {
         this.kills++;
      }
   }

   private static Vec3 vec(Vector3f v) {
      return new Vec3((double)v.x, (double)v.y, (double)v.z);
   }

   @Override
   public void remove(RemovalReason reason) {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(14);
      }

      super.remove(reason);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.age = tag.getInt("Age");
      this.entityData.set(DATA_AGE, this.age);
      this.power = tag.getFloat("Power");
      this.kills = tag.getInt("Kills");
      this.entityData.set(DATA_START, new Vector3f(tag.getFloat("StartX"), tag.getFloat("StartY"), tag.getFloat("StartZ")));
      this.entityData.set(DATA_ENTRY, tag.getFloat("Entry"));
      this.entityData.set(DATA_TURN, tag.contains("Turn") ? tag.getFloat("Turn") : 1.0F);
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Age", this.age);
      tag.putFloat("Power", this.power);
      tag.putInt("Kills", this.kills);
      Vector3f s = this.start();
      tag.putFloat("StartX", s.x);
      tag.putFloat("StartY", s.y);
      tag.putFloat("StartZ", s.z);
      tag.putFloat("Entry", this.entry());
      tag.putFloat("Turn", this.turn());
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
   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 65536.0;
   }
}
