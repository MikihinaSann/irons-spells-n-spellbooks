package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BloodmarkMarkerEntity extends Entity {
   private int lifetime;

   public BloodmarkMarkerEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public BloodmarkMarkerEntity(Level level, double x, double y, double z, int lifetime) {
      this((EntityType<?>)ModEntityTypes.BLOODMARK_MARKER.get(), level);
      this.setPos(x, y, z);
      this.lifetime = lifetime;
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel serverLevel) {
         this.spawnPulse(serverLevel);
      }

      if (this.tickCount >= this.lifetime) {
         this.discard();
      }
   }

   private void spawnPulse(ServerLevel serverLevel) {
      float phase = (float)((Math.sin(this.tickCount * 0.35F) + 1.0) * 0.5);
      if ((this.tickCount & 3) == 0) {
         float radius = 0.55F + phase * 0.25F;
         serverLevel.sendParticles(
            new BlastwaveParticleOptions(0.82F, 0.12F, 0.16F, radius), this.getX(), this.getY() + 0.03, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0
         );
      }

      int count = 4 + (int)(phase * 6.0F);
      double spread = 0.06 + phase * 0.08;
      MagicManager.spawnParticles(
         serverLevel, ParticleHelper.BLOOD, this.getX(), this.getY() + 0.08, this.getZ(), count, spread, 0.06, spread, 0.01 + phase * 0.015, true
      );
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.lifetime = compound.getInt("Lifetime");
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("Lifetime", this.lifetime);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
