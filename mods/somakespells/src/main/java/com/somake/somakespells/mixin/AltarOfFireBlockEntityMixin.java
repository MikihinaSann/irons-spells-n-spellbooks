package com.somake.somakespells.mixin;

import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.somake.somakespells.block.entity.SoulFireAltarState;
import com.somake.somakespells.ritual.RitualElementalMode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AltarOfFire_Block_Entity.class})
public abstract class AltarOfFireBlockEntityMixin implements SoulFireAltarState {
   @Unique
   private String somake$ritualMode = RitualElementalMode.NORMAL.id();
   @Unique
   private int somake$ritualCharges;
   @Unique
   private long[] somake$ritualPedestalPositions = new long[0];

   @Override
   public RitualElementalMode somake$getRitualMode() {
      return RitualElementalMode.fromId(this.somake$ritualMode);
   }

   @Override
   public int somake$getRitualCharges() {
      return this.somake$ritualCharges;
   }

   @Override
   public List<BlockPos> somake$getRitualPedestals() {
      List<BlockPos> positions = new ArrayList<>(this.somake$ritualPedestalPositions.length);

      for (long encodedPos : this.somake$ritualPedestalPositions) {
         positions.add(BlockPos.of(encodedPos));
      }

      return positions;
   }

   @Override
   public void somake$activateRitualMode(RitualElementalMode mode, List<BlockPos> pedestalPositions, int charges) {
      boolean active = mode.isEmpowered() && !pedestalPositions.isEmpty() && charges > 0;
      this.somake$ritualMode = active ? mode.id() : RitualElementalMode.NORMAL.id();
      this.somake$ritualCharges = active ? charges : 0;
      this.somake$ritualPedestalPositions = active ? pedestalPositions.stream().map(BlockPos::immutable).mapToLong(BlockPos::asLong).toArray() : new long[0];
      this.somake$sync();
   }

   @Override
   public void somake$clearRitualMode() {
      if (this.somake$getRitualMode() != RitualElementalMode.NORMAL || this.somake$ritualCharges != 0 || this.somake$ritualPedestalPositions.length != 0) {
         this.somake$ritualMode = RitualElementalMode.NORMAL.id();
         this.somake$ritualCharges = 0;
         this.somake$ritualPedestalPositions = new long[0];
         this.somake$sync();
      }
   }

   @Inject(
      method = {"saveAdditional"},
      at = {@At("TAIL")}
   )
   private void somake$saveSoulFireState(CompoundTag tag, Provider registries, CallbackInfo ci) {
      tag.putString("SomakeRitualMode", this.somake$ritualMode);
      tag.putInt("SomakeRitualCharges", this.somake$ritualCharges);
      tag.putLongArray("SomakeRitualPedestals", this.somake$ritualPedestalPositions);
   }

   @Inject(
      method = {"loadAdditional"},
      at = {@At("TAIL")}
   )
   private void somake$loadSoulFireState(CompoundTag tag, Provider registries, CallbackInfo ci) {
      if (tag.contains("SomakeRitualMode")) {
         this.somake$ritualMode = tag.getString("SomakeRitualMode");
         this.somake$ritualCharges = tag.getInt("SomakeRitualCharges");
         this.somake$ritualPedestalPositions = tag.getLongArray("SomakeRitualPedestals");
      } else {
         boolean soulFireActive = tag.getBoolean("SomakeSoulFireActive");
         this.somake$ritualMode = soulFireActive ? RitualElementalMode.SOUL_FIRE.id() : RitualElementalMode.NORMAL.id();
         this.somake$ritualCharges = tag.getInt("SomakeSoulFireCharges");
         this.somake$ritualPedestalPositions = tag.getLongArray("SomakeSoulFirePedestals");
      }
   }

   @Inject(
      method = {"getUpdateTag"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void somake$appendSoulFireUpdateTag(Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
      CompoundTag tag = (CompoundTag)cir.getReturnValue();
      tag.putString("SomakeRitualMode", this.somake$ritualMode);
      tag.putInt("SomakeRitualCharges", this.somake$ritualCharges);
      tag.putLongArray("SomakeRitualPedestals", this.somake$ritualPedestalPositions);
   }

   @Unique
   private void somake$sync() {
      BlockEntity self = (BlockEntity)(Object)this;
      self.setChanged();
      Level level = self.getLevel();
      if (level != null) {
         BlockState state = self.getBlockState();
         level.sendBlockUpdated(self.getBlockPos(), state, state, 2);
      }
   }
}
