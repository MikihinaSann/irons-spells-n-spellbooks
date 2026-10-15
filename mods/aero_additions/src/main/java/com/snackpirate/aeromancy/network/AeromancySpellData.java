package com.snackpirate.aeromancy.network;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class AeromancySpellData {
   public static final long ASPHYXIATING = 1L;
   public static final long WIND_SHIELD = 2L;
   public static final long AIR_STEP = 4L;
   private final int serverPlayerId;
   @Nullable
   private LivingEntity livingEntity;
   private boolean isMob = false;
   private long syncedEffectFlags;
   private int airStepHitsRemaining;
   private ResourceLocation shapeshiftedEntityId;

   public AeromancySpellData(int serverPlayerId) {
      this.serverPlayerId = serverPlayerId;
      this.syncedEffectFlags = 0L;
      this.airStepHitsRemaining = 0;
      this.shapeshiftedEntityId = ResourceLocation.withDefaultNamespace("pig");
   }

   public AeromancySpellData(boolean isMob) {
      this.serverPlayerId = -999;
      this.isMob = isMob;
   }

   public AeromancySpellData() {
      this.serverPlayerId = -999;
   }

   public static void write(FriendlyByteBuf buffer, AeromancySpellData data) {
      buffer.writeInt(data.serverPlayerId);
      buffer.writeLong(data.syncedEffectFlags);
      buffer.writeInt(data.airStepHitsRemaining);
      buffer.writeResourceLocation(data.shapeshiftedEntityId);
   }

   public static AeromancySpellData read(FriendlyByteBuf buffer) {
      AeromancySpellData data = new AeromancySpellData(buffer.readInt());
      data.syncedEffectFlags = buffer.readLong();
      data.airStepHitsRemaining = buffer.readInt();
      data.shapeshiftedEntityId = buffer.readResourceLocation();
      return data;
   }

   public AeromancySpellData(LivingEntity livingEntity) {
      this(livingEntity == null ? -1 : livingEntity.getId());
      this.livingEntity = livingEntity;
   }

   public void saveNBTData(CompoundTag compound, Provider provider) {
      compound.putLong("effectFlags", this.syncedEffectFlags);
      compound.putFloat("airStepHitsRemaining", this.airStepHitsRemaining);
      compound.putString("shapeshiftId", this.shapeshiftedEntityId.toString());
   }

   public void loadNBTData(CompoundTag compound, Provider provider) {
      this.syncedEffectFlags = compound.getLong("effectFlags");
      this.airStepHitsRemaining = compound.getInt("airStepHitsRemaining");
      this.shapeshiftedEntityId = ResourceLocation.parse(compound.getString("shapeshiftId"));
   }

   public int getServerPlayerId() {
      return this.serverPlayerId;
   }

   public void addEffects(long effectFlags) {
      this.syncedEffectFlags |= effectFlags;
      this.doSync();
   }

   public void removeEffects(long effectFlags) {
      this.syncedEffectFlags &= ~effectFlags;
      this.doSync();
   }

   public boolean hasEffect(long effectFlags) {
      return (this.syncedEffectFlags & effectFlags) == effectFlags;
   }

   public int getAirStepHitsRemaining() {
      this.doSync();
      return this.airStepHitsRemaining;
   }

   public void subtractAirStepHits() {
      this.airStepHitsRemaining--;
      this.doSync();
   }

   public void setAirStepHitsRemaining(int hitsRemaining) {
      this.airStepHitsRemaining = hitsRemaining;
      this.doSync();
   }

   public void setShapeshiftId(ResourceLocation loc) {
      this.shapeshiftedEntityId = loc;
      this.doSync();
   }

   public ResourceLocation getShapeshiftedEntityId() {
      this.doSync();
      return this.shapeshiftedEntityId;
   }

   public static AeromancySpellData getAeromancyData(LivingEntity entity) {
      return (AeromancySpellData)entity.getData(AADataAttachments.AEROMANCY_DATA);
   }

   public void doSync() {
      if (this.livingEntity instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new AASyncPlayerDataPacket(this), new CustomPacketPayload[0]);
      } else if (this.livingEntity instanceof IMagicEntity abstractSpellCastingMob) {
         PacketDistributor.sendToPlayersTrackingEntity(this.livingEntity, new AASyncEntityDataPacket(this, abstractSpellCastingMob), new CustomPacketPayload[0]);
      }
   }

   public void syncToPlayer(ServerPlayer serverPlayer) {
      PacketDistributor.sendToPlayer(serverPlayer, new AASyncPlayerDataPacket(this), new CustomPacketPayload[0]);
   }

   protected AeromancySpellData clone() {
      return new AeromancySpellData(this.livingEntity);
   }

   public AeromancySpellData getPersistentData(ServerPlayer serverPlayer) {
      AeromancySpellData persistentData = new AeromancySpellData(this.livingEntity);
      persistentData.livingEntity = serverPlayer;
      return persistentData;
   }
}
