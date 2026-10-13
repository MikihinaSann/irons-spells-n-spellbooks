package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.DragonParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.DragonFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEntities;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ThunderDragonEntity extends Entity {
   public static final int GATHER = 40;
   public static final int EMERGE = 34;
   public static final int STRIKE_EVERY = 13;
   public static final int STRIKES = 12;
   public static final int T_CLIMB = 204;
   public static final int CLIMB = 46;
   public static final int T_HANG = 250;
   public static final int HANG = 12;
   public static final int T_DIVE = 262;
   public static final int DIVE = 13;
   public static final int T_IMPACT = 275;
   public static final float APEX = 120.0F;
   public static final float SPEED = 3.2F;
   public static final float LENGTH = 150.0F;
   public static final float POUR_RATE = 4.0F;
   public static final int POUR = Mth.ceil(11.71875F);
   public static final int FADE = 34;
   public static final int LIFE = 315 + POUR + 34;
   public static final float CLOUD_HEIGHT = 48.0F;
   public static final float CLOUD_RADIUS = 64.0F;
   public static final float ORBIT = 40.0F;
   public static final float ORBIT_HEIGHT = 30.0F;
   public static final float HEAVE = 7.0F;
   public static final float SWAY = 6.0F;
   public static final float REACH = 64.0F;
   public static final float STRIKE_SHARE = 0.3F;
   public static final float STRIKE_GROWTH = 0.12F;
   public static final float POUR_SHARE = 0.15F;
   public static final float FINALE_SHARE = 3.0F;
   public static final float FINALE_BLAST = 30.0F;
   public static final float POUR_RADIUS = 16.0F;
   public static final float CLOSING_SHARE = 0.6F;
   private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(ThunderDragonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_ENTRY = SynchedEntityData.defineId(ThunderDragonEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_TURN = SynchedEntityData.defineId(ThunderDragonEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Vector3f> DATA_FINAL = SynchedEntityData.defineId(ThunderDragonEntity.class, EntityDataSerializers.VECTOR3);
   private static final EntityDataAccessor<Integer> DATA_STRIKES = SynchedEntityData.defineId(ThunderDragonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Vector3f> DATA_STRIKE_AT = SynchedEntityData.defineId(ThunderDragonEntity.class, EntityDataSerializers.VECTOR3);
   @Nullable
   private UUID casterId;
   private float power = 20.0F;
   private int age;
   private int strikes;
   private int kills;
   private boolean counted;
   private final Set<UUID> struck = new HashSet<>();
   private final Set<UUID> dead = new HashSet<>();
   private static final float CLIMB_TURN = 4.2F;

   public ThunderDragonEntity(EntityType<? extends ThunderDragonEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public ThunderDragonEntity(Level level, LivingEntity caster, float power) {
      this(GsEntities.THUNDER_DRAGON.get(), level);
      float yaw = caster.getYRot() * (float) (Math.PI / 180.0);
      Vec3 forward = new Vec3((double)(-Mth.sin(yaw)), 0.0, (double)Mth.cos(yaw));
      Vec3 centre = caster.position().add(forward.scale(20.0));
      int y = level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(centre.x), Mth.floor(centre.z));
      this.setPos(centre.x, Math.min((double)y, caster.getY() + 6.0), centre.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_ENTRY, (float)Math.atan2(-forward.z, -forward.x));
      this.entityData.set(DATA_TURN, caster.getRandom().nextBoolean() ? 1.0F : -1.0F);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_AGE, 0);
      builder.define(DATA_ENTRY, 0.0F);
      builder.define(DATA_TURN, 1.0F);
      builder.define(DATA_FINAL, new Vector3f());
      builder.define(DATA_STRIKES, 0);
      builder.define(DATA_STRIKE_AT, new Vector3f());
   }

   public float age(float partial) {
      return (float)this.age + partial;
   }

   public float flight(float partial) {
      return (float)this.age + partial - 40.0F;
   }

   public float entry() {
      return this.entityData.get(DATA_ENTRY);
   }

   public float turn() {
      return this.entityData.get(DATA_TURN);
   }

   public Vector3f finalPoint() {
      return this.entityData.get(DATA_FINAL);
   }

   public int strikes() {
      return this.entityData.get(DATA_STRIKES);
   }

   public Vec3 strikeAt() {
      Vector3f c = this.entityData.get(DATA_STRIKE_AT);
      return new Vec3((double)c.x, (double)c.y, (double)c.z);
   }

   public Vec3 cloud() {
      return this.position().add(0.0, 48.0, 0.0);
   }

   public float storm(float partial) {
      float a = (float)this.age + partial;
      return a < 40.0F ? a / 40.0F : 1.0F - Mth.clamp((a - (float)(LIFE - 34)) / 34.0F, 0.0F, 1.0F);
   }

   public static Vector3f at(float t, float entry, float turn, Vector3f last) {
      if (t <= 0.0F) {
         return new Vector3f(0.0F, 46.0F, 0.0F);
      } else if (t < 34.0F) {
         float u = t / 34.0F;
         Vector3f p0 = new Vector3f(0.0F, 46.0F, 0.0F);
         Vector3f m0 = new Vector3f(Mth.cos(entry), -0.9F, Mth.sin(entry)).mul(30.0F);
         Vector3f p1 = ring(0.0F, entry, turn);
         Vector3f m1 = ringVelocity(0.0F, entry, turn).mul(34.0F);
         return hermite(p0, m0, p1, m1, u);
      } else if (t < 204.0F) {
         return ring(t - 34.0F, entry, turn);
      } else if (t < 250.0F) {
         return climb((t - 204.0F) / 46.0F, entry, turn);
      } else if (t < 262.0F) {
         float u = (t - 250.0F) / 12.0F;
         Vector3f top = climb(1.0F, entry, turn);
         float a = entry + turn * (4.2F + 1.6F * u);
         return new Vector3f(top).add(Mth.cos(a) * 4.0F * u, 6.0F * Mth.sin((float) Math.PI * u), Mth.sin(a) * 4.0F * u);
      } else if (t < 275.0F) {
         float u = (t - 262.0F) / 13.0F;
         Vector3f from = at(261.99F, entry, turn, last);
         return new Vector3f(from).lerp(new Vector3f(last), u * u);
      } else {
         return new Vector3f(last);
      }
   }

   public static Vector3f climb(float u, float entry, float turn) {
      Vector3f from = ring(170.0F, entry, turn);
      float a0 = (float)Math.atan2((double)from.z, (double)from.x);
      float r0 = (float)Math.sqrt((double)(from.x * from.x + from.z * from.z));
      float k = u * u * (3.0F - 2.0F * u);
      float a = a0 + turn * 4.2F * u;
      float r = Mth.lerp(k, r0, 6.0F);
      float h = Mth.lerp((float)Math.pow((double)u, 1.4), from.y, 120.0F);
      return new Vector3f(Mth.cos(a) * r, h, Mth.sin(a) * r);
   }

   public static Vector3f ring(float t, float entry, float turn) {
      float a = entry + turn * 0.08F * t;
      float r = 40.0F + 6.0F * Mth.sin(a * 3.0F);
      float h = 30.0F + 7.0F * Mth.sin(a * 2.0F + 0.7F);
      return new Vector3f(Mth.cos(a) * r, h, Mth.sin(a) * r);
   }

   private static Vector3f ringVelocity(float t, float entry, float turn) {
      return ring(t + 0.5F, entry, turn).sub(ring(t - 0.5F, entry, turn));
   }

   private static Vector3f hermite(Vector3f p0, Vector3f m0, Vector3f p1, Vector3f m1, float u) {
      float u2 = u * u;
      float u3 = u2 * u;
      return new Vector3f(p0)
         .mul(2.0F * u3 - 3.0F * u2 + 1.0F)
         .add(new Vector3f(m0).mul(u3 - 2.0F * u2 + u))
         .add(new Vector3f(p1).mul(-2.0F * u3 + 3.0F * u2))
         .add(new Vector3f(m1).mul(u3 - u2));
   }

   public Vector3f head(float partial) {
      return at(this.flight(partial), this.entry(), this.turn(), this.finalPoint());
   }

   public static int strikeTime(int n) {
      return 42 + n * 13;
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("ThunderDragonEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      this.age++;
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(15);
      }

      if (this.level().isClientSide) {
         int served = this.entityData.get(DATA_AGE);
         if (Math.abs(served - this.age) > 2) {
            this.age = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            DragonParticles.tick(this);
         }
      } else {
         this.entityData.set(DATA_AGE, this.age);
         ServerLevel level = (ServerLevel)this.level();
         Entity caster = this.casterId == null ? null : level.getEntity(this.casterId);
         int t = this.age - 40;
         Vec3 head = this.position().add(vec(this.head(0.0F)));
         if (this.age == 1) {
            DragonFx.gathers(level, this.cloud());
         }

         if (this.age < 40 && this.age % 12 == 6) {
            DragonFx.rumbles(level, this.cloud(), this.age);
         }

         if (t == 0) {
            DragonFx.emerges(level, this.cloud());
         }

         if (t > 0 && t < 204 && t % 24 == 0) {
            DragonFx.crackles(level, head, t);
         }

         if (this.strikes < 12 && t == strikeTime(this.strikes)) {
            this.strike(level, caster, head);
         }

         if (t == 204) {
            DragonFx.climbs(level, head);
         }

         if (t == 250) {
            this.chooseFinal(level, caster);
            DragonFx.coils(level, head);
         }

         if (t == 262) {
            DragonFx.dives(level, head);
         }

         Vec3 last = this.position().add(vec(this.finalPoint()));
         if (t == 275) {
            DragonFx.impact(level, last);
            this.blast(level, caster, 30.0F, 3.0F, true);
         }

         if (t > 275 && t < 275 + POUR && (t - 275) % 3 == 0) {
            DragonFx.pours(level, last, t - 275);
            this.blast(level, caster, 16.0F, 0.15F, false);
         }

         if (t == 275 + POUR) {
            DragonFx.finale(level, last);
            this.blast(level, caster, 20.8F, 0.6F, true);
            if (caster instanceof ServerPlayer player) {
               GsAdvancements.unleashed(player, this.strikes, this.kills);
            }
         }

         if (this.age >= LIFE) {
            this.discard();
         }
      }
   }

   private void strike(ServerLevel level, @Nullable Entity caster, Vec3 head) {
      LivingEntity target = this.choose(level, caster, head);
      Vec3 point;
      if (target != null) {
         point = target.getBoundingBox().getCenter();
      } else {
         float a = (float)this.strikes * 2.39996F;
         float r = 12.0F + 4.0F * (float)(this.strikes % 4);
         double x = this.getX() + (double)(Mth.cos(a) * r);
         double z = this.getZ() + (double)(Mth.sin(a) * r);
         point = new Vec3(x, (double)level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z)), z);
      }

      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      float grow = 1.0F + 0.12F * (float)this.strikes;
      float radius = 6.0F + 0.6F * (float)this.strikes;
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int caught = 0;

      for (LivingEntity hit : level.getEntitiesOfClass(
         LivingEntity.class, new AABB(point, point).inflate((double)radius), e -> ArsenalEntity.hostile(e, caster, this.casterId)
      )) {
         if (!(hit.distanceToSqr(point) > (double)(radius * radius)) && caught < cap) {
            caught++;
            hit.invulnerableTime = 0;
            this.hurt(level, caster, hit, this.power * 0.3F * grow * scale);
            hit.igniteForSeconds(3.0F);
            this.struck.add(hit.getUUID());
         }
      }

      this.strikes++;
      this.entityData.set(DATA_STRIKES, this.strikes);
      this.entityData.set(DATA_STRIKE_AT, new Vector3f((float)point.x, (float)point.y, (float)point.z));
      DragonFx.strikes(level, point, this.strikes);
   }

   @Nullable
   private LivingEntity choose(ServerLevel level, @Nullable Entity caster, Vec3 from) {
      LivingEntity best = null;
      double bestScore = Double.MAX_VALUE;
      AABB box = new AABB(this.position(), this.position()).inflate(64.0, 24.0, 64.0);

      for (LivingEntity found : level.getEntitiesOfClass(LivingEntity.class, box, e -> ArsenalEntity.hostile(e, caster, this.casterId))) {
         double score = found.distanceToSqr(from) + (this.struck.contains(found.getUUID()) ? 1000000.0 : 0.0) + (found instanceof Enemy ? 0.0 : 500000.0);
         if (score < bestScore) {
            bestScore = score;
            best = found;
         }
      }

      return best;
   }

   private void chooseFinal(ServerLevel level, @Nullable Entity caster) {
      List<LivingEntity> all = new ArrayList<>(
         level.getEntitiesOfClass(
            LivingEntity.class, new AABB(this.position(), this.position()).inflate(64.0, 24.0, 64.0), e -> ArsenalEntity.hostile(e, caster, this.casterId)
         )
      );
      LivingEntity best = null;
      int crowd = -1;

      for (LivingEntity t : all) {
         int near = t instanceof Enemy ? 100 : 0;

         for (LivingEntity o : all) {
            if (o != t && o.distanceToSqr(t) < 144.0) {
               near++;
            }
         }

         if (near > crowd) {
            crowd = near;
            best = t;
         }
      }

      Vec3 point = best != null ? best.position() : this.position();
      Vec3 rel = point.subtract(this.position());
      this.entityData.set(DATA_FINAL, new Vector3f((float)rel.x, (float)rel.y + 0.5F, (float)rel.z));
   }

   private void blast(ServerLevel level, @Nullable Entity caster, float radius, float share, boolean finishing) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      Vec3 at = this.position().add(vec(this.finalPoint()));
      float reach = radius * (finishing ? 1.5F : 1.0F);
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int caught = 0;

      for (LivingEntity hit : level.getEntitiesOfClass(
         LivingEntity.class, new AABB(at, at).inflate((double)reach), e -> ArsenalEntity.hostile(e, caster, this.casterId)
      )) {
         double d = hit.position().distanceTo(at);
         if (!(d > (double)reach) && caught < cap) {
            caught++;
            float falloff = d <= (double)radius ? 1.0F : (float)(1.0 - (d - (double)radius) / (double)(reach - radius));
            if (finishing) {
               hit.invulnerableTime = 0;
            }

            this.hurt(level, caster, hit, this.power * share * scale * falloff);
            if (finishing) {
               Vec3 push = hit.position().subtract(at);
               Vec3 flat = new Vec3(push.x, 0.0, push.z);
               flat = flat.lengthSqr() < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : flat.normalize();
               hit.push(flat.x * 2.2 * (double)falloff, 1.0 * (double)falloff, flat.z * 2.2 * (double)falloff);
               hit.hurtMarked = true;
               hit.igniteForSeconds(5.0F);
            }
         }
      }
   }

   private void hurt(ServerLevel level, @Nullable Entity caster, LivingEntity target, float amount) {
      boolean alive = target.isAlive();
      target.hurt(GsDamage.thunderDragon(level, (Entity)(caster == null ? this : caster)), amount);
      if (alive && !target.isAlive() && this.dead.add(target.getUUID())) {
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
         GsPresence.leave(15);
      }

      super.remove(reason);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.discard();
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
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
      return distance < 147456.0;
   }
}
