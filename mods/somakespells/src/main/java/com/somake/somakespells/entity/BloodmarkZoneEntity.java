package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.blood.BloodmarkSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BloodmarkZoneEntity extends Entity {
   private static final int DAMAGE_INTERVAL = 10;
   private final List<Vec3> points = new ArrayList<>();
   private UUID ownerUuid;
   private int lifetime;
   private float damagePerPulse;

   public BloodmarkZoneEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public BloodmarkZoneEntity(Level level, LivingEntity owner, List<Vec3> points, int lifetime, float damagePerPulse) {
      this((EntityType<?>)ModEntityTypes.BLOODMARK_ZONE.get(), level);
      this.ownerUuid = owner != null ? owner.getUUID() : null;
      this.points.addAll(points);
      this.lifetime = lifetime;
      this.damagePerPulse = damagePerPulse;
      this.updateCenter();
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel serverLevel) {
         if (this.tickCount == 1) {
            this.spawnBoundaryVisuals(serverLevel);
         }

         if (this.tickCount % 10 == 0) {
            this.pulseDamage(serverLevel);
         }

         if (this.tickCount >= this.lifetime) {
            this.discard();
         }
      }
   }

   private void spawnBoundaryVisuals(ServerLevel serverLevel) {
      if (this.points.size() >= 4) {
         this.clearExistingMarkers(serverLevel);

         for (int i = 0; i < this.points.size(); i++) {
            Vec3 start = this.points.get(i);
            Vec3 end = this.points.get((i + 1) % this.points.size());
            serverLevel.addFreshEntity(new BloodmarkTraceEntity(serverLevel, start, end, this.lifetime + 10));
         }

         for (Vec3 point : this.points) {
            serverLevel.addFreshEntity(new BloodmarkMarkerEntity(serverLevel, point.x, point.y, point.z, this.lifetime));
         }
      }
   }

   private void clearExistingMarkers(ServerLevel serverLevel) {
      AABB area = this.buildBounds().inflate(1.5, 1.5, 1.5);

      for (BloodmarkMarkerEntity marker : serverLevel.getEntitiesOfClass(BloodmarkMarkerEntity.class, area)) {
         marker.discard();
      }
   }

   private void pulseDamage(ServerLevel serverLevel) {
      if (this.points.size() >= 4) {
         LivingEntity owner = this.getOwnerEntity(serverLevel);
         AABB area = this.buildBounds();

         for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, area)) {
            if ((owner == null || target != owner && !target.isAlliedTo(owner)) && this.isInsidePolygonXZ(target.position())) {
               DamageSources.applyDamage(
                  target, this.damagePerPulse, ((BloodmarkSpell)ModSpells.BLOODMARK.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
               );
               this.spawnHitVfx(serverLevel, target);
            }
         }
      }
   }

   private void spawnHitVfx(ServerLevel serverLevel, LivingEntity target) {
      double x = target.getX();
      double y = target.getY() + target.getBbHeight() * 0.5F;
      double z = target.getZ();
      MagicManager.spawnParticles(serverLevel, ParticleHelper.BLOOD, x, y, z, 16, 0.25, 0.35, 0.25, 0.04, true);
      serverLevel.sendParticles(new BlastwaveParticleOptions(0.82F, 0.12F, 0.16F, 0.75F), x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
   }

   private LivingEntity getOwnerEntity(ServerLevel level) {
      if (this.ownerUuid == null) {
         return null;
      } else {
         return level.getEntity(this.ownerUuid) instanceof LivingEntity living ? living : null;
      }
   }

   private AABB buildBounds() {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double minZ = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      double maxZ = Double.NEGATIVE_INFINITY;

      for (Vec3 point : this.points) {
         minX = Math.min(minX, point.x);
         minY = Math.min(minY, point.y);
         minZ = Math.min(minZ, point.z);
         maxX = Math.max(maxX, point.x);
         maxY = Math.max(maxY, point.y);
         maxZ = Math.max(maxZ, point.z);
      }

      return new AABB(minX, minY - 1.0, minZ, maxX, maxY + 3.0, maxZ);
   }

   private boolean isInsidePolygonXZ(Vec3 point) {
      boolean inside = false;
      double x = point.x;
      double z = point.z;
      int i = 0;

      for (int j = this.points.size() - 1; i < this.points.size(); j = i++) {
         Vec3 pi = this.points.get(i);
         Vec3 pj = this.points.get(j);
         boolean intersect = pi.z > z != pj.z > z && x < (pj.x - pi.x) * (z - pi.z) / (pj.z - pi.z + 1.0E-7) + pi.x;
         if (intersect) {
            inside = !inside;
         }
      }

      return inside;
   }

   private void updateCenter() {
      if (!this.points.isEmpty()) {
         Vec3 sum = Vec3.ZERO;

         for (Vec3 point : this.points) {
            sum = sum.add(point);
         }

         Vec3 center = sum.scale(1.0 / this.points.size());
         this.setPos(center.x, center.y, center.z);
      }
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.points.clear();
      int pointCount = compound.getInt("PointCount");

      for (int i = 0; i < pointCount; i++) {
         CompoundTag pointTag = compound.getCompound("Point" + i);
         this.points.add(new Vec3(pointTag.getDouble("X"), pointTag.getDouble("Y"), pointTag.getDouble("Z")));
      }

      this.ownerUuid = compound.hasUUID("Owner") ? compound.getUUID("Owner") : null;
      this.lifetime = compound.getInt("Lifetime");
      this.damagePerPulse = compound.getFloat("DamagePerPulse");
      this.updateCenter();
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("PointCount", this.points.size());

      for (int i = 0; i < this.points.size(); i++) {
         Vec3 point = this.points.get(i);
         CompoundTag pointTag = new CompoundTag();
         pointTag.putDouble("X", point.x);
         pointTag.putDouble("Y", point.y);
         pointTag.putDouble("Z", point.z);
         compound.put("Point" + i, pointTag);
      }

      if (this.ownerUuid != null) {
         compound.putUUID("Owner", this.ownerUuid);
      }

      compound.putInt("Lifetime", this.lifetime);
      compound.putFloat("DamagePerPulse", this.damagePerPulse);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
