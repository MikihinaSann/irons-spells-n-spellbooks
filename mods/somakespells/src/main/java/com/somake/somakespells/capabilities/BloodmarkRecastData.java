package com.somake.somakespells.capabilities;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class BloodmarkRecastData implements INBTSerializable<CompoundTag> {
   private final List<Vec3> points = new ArrayList<>();
   private long expiresAt;

   public void clear() {
      this.points.clear();
      this.expiresAt = 0L;
   }

   public void addPoint(Vec3 point) {
      if (this.points.size() < 4) {
         this.points.add(point);
      }
   }

   public List<Vec3> getPoints() {
      return List.copyOf(this.points);
   }

   public int getPointCount() {
      return this.points.size();
   }

   public boolean hasAllPoints() {
      return this.points.size() >= 4;
   }

   public void setExpiresAt(long expiresAt) {
      this.expiresAt = expiresAt;
   }

   public boolean isActive(long gameTime) {
      return this.expiresAt > gameTime;
   }

   public long getExpiresAt() {
      return this.expiresAt;
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      tag.putLong("ExpiresAt", this.expiresAt);
      tag.putInt("PointCount", this.points.size());

      for (int i = 0; i < this.points.size(); i++) {
         Vec3 point = this.points.get(i);
         CompoundTag pointTag = new CompoundTag();
         pointTag.putDouble("X", point.x);
         pointTag.putDouble("Y", point.y);
         pointTag.putDouble("Z", point.z);
         tag.put("Point" + i, pointTag);
      }

      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag tag) {
      this.points.clear();
      this.expiresAt = tag.getLong("ExpiresAt");
      int pointCount = tag.getInt("PointCount");

      for (int i = 0; i < pointCount; i++) {
         CompoundTag pointTag = tag.getCompound("Point" + i);
         this.points.add(new Vec3(pointTag.getDouble("X"), pointTag.getDouble("Y"), pointTag.getDouble("Z")));
      }
   }
}
