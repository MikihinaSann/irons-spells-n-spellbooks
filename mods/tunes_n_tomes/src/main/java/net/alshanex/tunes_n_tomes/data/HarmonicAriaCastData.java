package net.alshanex.tunes_n_tomes.data;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;

public class HarmonicAriaCastData implements ICastDataSerializable {
   private int beamEntityId = -1;
   private Vec3 aimPosition = Vec3.ZERO;
   private Vec3 lastAimPosition = Vec3.ZERO;

   public int getBeamEntityId() {
      return this.beamEntityId;
   }

   public void setBeamEntityId(int id) {
      this.beamEntityId = id;
   }

   public void updateAim(Entity target, float strength) {
      Vec3 wanted = target.getBoundingBox().getCenter();
      if (this.aimPosition.equals(Vec3.ZERO)) {
         this.aimPosition = wanted;
         this.lastAimPosition = wanted;
      } else {
         this.lastAimPosition = this.aimPosition;
         this.aimPosition = this.aimPosition.add(wanted.subtract(this.aimPosition).scale(strength));
      }
   }

   public Vec3 getAimPosition() {
      return this.aimPosition;
   }

   public Vec3 getAimPosition(float partialTick) {
      return this.lastAimPosition.add(this.aimPosition.subtract(this.lastAimPosition).scale(partialTick));
   }

   public Vec3 getForward(Entity host) {
      return this.aimPosition.subtract(host.getEyePosition()).normalize();
   }

   public void reset() {
      this.beamEntityId = -1;
      this.aimPosition = Vec3.ZERO;
      this.lastAimPosition = Vec3.ZERO;
   }

   public void writeToBuffer(FriendlyByteBuf buffer) {
      buffer.writeInt(this.beamEntityId);
      buffer.writeInt((int)(this.aimPosition.x * 10.0));
      buffer.writeInt((int)(this.aimPosition.y * 10.0));
      buffer.writeInt((int)(this.aimPosition.z * 10.0));
      buffer.writeInt((int)(this.lastAimPosition.x * 10.0));
      buffer.writeInt((int)(this.lastAimPosition.y * 10.0));
      buffer.writeInt((int)(this.lastAimPosition.z * 10.0));
   }

   public void readFromBuffer(FriendlyByteBuf buffer) {
      this.beamEntityId = buffer.readInt();
      this.aimPosition = new Vec3(buffer.readInt() / 10.0, buffer.readInt() / 10.0, buffer.readInt() / 10.0);
      this.lastAimPosition = new Vec3(buffer.readInt() / 10.0, buffer.readInt() / 10.0, buffer.readInt() / 10.0);
   }

   @UnknownNullability
   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      tag.putInt("BeamEntityId", this.beamEntityId);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      this.beamEntityId = nbt.getInt("BeamEntityId");
   }
}
