package com.somake.somakespells.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class FireOrbsRecastData implements INBTSerializable<CompoundTag> {
   private final List<UUID> frontOrbIds = new ArrayList<>();
   private final List<UUID> skyOrbIds = new ArrayList<>();
   private long expiresAt;
   private boolean targetLocked;
   private Vec3 lockedTargetPos = Vec3.ZERO;

   public void clear() {
      this.frontOrbIds.clear();
      this.skyOrbIds.clear();
      this.expiresAt = 0L;
      this.targetLocked = false;
      this.lockedTargetPos = Vec3.ZERO;
   }

   public void addFrontOrb(UUID orbId) {
      if (this.frontOrbIds.size() + this.skyOrbIds.size() < 5) {
         this.frontOrbIds.add(orbId);
      }
   }

   public UUID popFrontOrb() {
      return this.frontOrbIds.isEmpty() ? null : this.frontOrbIds.remove(0);
   }

   public List<UUID> getFrontOrbIds() {
      return List.copyOf(this.frontOrbIds);
   }

   public void setFrontOrbIds(List<UUID> orbIds) {
      this.frontOrbIds.clear();
      this.frontOrbIds.addAll(orbIds);
   }

   public int getFrontOrbCount() {
      return this.frontOrbIds.size();
   }

   public boolean hasFrontOrbs() {
      return !this.frontOrbIds.isEmpty();
   }

   public void addSkyOrb(UUID orbId) {
      this.skyOrbIds.add(orbId);
   }

   public List<UUID> getSkyOrbIds() {
      return List.copyOf(this.skyOrbIds);
   }

   public void setSkyOrbIds(List<UUID> orbIds) {
      this.skyOrbIds.clear();
      this.skyOrbIds.addAll(orbIds);
   }

   public int getSkyOrbCount() {
      return this.skyOrbIds.size();
   }

   public int getTotalOrbCount() {
      return this.frontOrbIds.size() + this.skyOrbIds.size();
   }

   public void setLockedTargetPos(Vec3 lockedTargetPos) {
      this.targetLocked = true;
      this.lockedTargetPos = lockedTargetPos;
   }

   public boolean hasLockedTarget() {
      return this.targetLocked;
   }

   public Vec3 getLockedTargetPos() {
      return this.lockedTargetPos;
   }

   public void setExpiresAt(long expiresAt) {
      this.expiresAt = expiresAt;
   }

   public long getExpiresAt() {
      return this.expiresAt;
   }

   public boolean isActive(long gameTime) {
      return this.expiresAt > gameTime;
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      tag.putLong("ExpiresAt", this.expiresAt);
      tag.putInt("FrontOrbCount", this.frontOrbIds.size());

      for (int i = 0; i < this.frontOrbIds.size(); i++) {
         tag.putUUID("FrontOrb" + i, this.frontOrbIds.get(i));
      }

      tag.putInt("SkyOrbCount", this.skyOrbIds.size());

      for (int i = 0; i < this.skyOrbIds.size(); i++) {
         tag.putUUID("SkyOrb" + i, this.skyOrbIds.get(i));
      }

      tag.putBoolean("TargetLocked", this.targetLocked);
      tag.putDouble("LockedTargetX", this.lockedTargetPos.x);
      tag.putDouble("LockedTargetY", this.lockedTargetPos.y);
      tag.putDouble("LockedTargetZ", this.lockedTargetPos.z);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag tag) {
      this.frontOrbIds.clear();
      this.skyOrbIds.clear();
      this.expiresAt = tag.getLong("ExpiresAt");
      int frontOrbCount = tag.getInt("FrontOrbCount");

      for (int i = 0; i < frontOrbCount; i++) {
         String key = "FrontOrb" + i;
         if (tag.hasUUID(key)) {
            this.frontOrbIds.add(tag.getUUID(key));
         }
      }

      int skyOrbCount = tag.getInt("SkyOrbCount");

      for (int ix = 0; ix < skyOrbCount; ix++) {
         String key = "SkyOrb" + ix;
         if (tag.hasUUID(key)) {
            this.skyOrbIds.add(tag.getUUID(key));
         }
      }

      this.targetLocked = tag.getBoolean("TargetLocked");
      this.lockedTargetPos = new Vec3(tag.getDouble("LockedTargetX"), tag.getDouble("LockedTargetY"), tag.getDouble("LockedTargetZ"));
   }
}
