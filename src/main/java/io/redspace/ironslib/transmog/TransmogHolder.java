package io.redspace.ironslib.transmog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

public record TransmogHolder(ResourceLocation id, PatreonPermissions requiredPermission, Set<EquipmentSlot> supportedSlots, TransmogHolder.DyeConfig dyeConfig) {
   public static final Set<EquipmentSlot> ALL_SLOTS = Set.of(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
   public static final Codec<TransmogHolder> CODEC = ResourceLocation.CODEC
      .xmap(TransmogManager::get, TransmogHolder::id)
      .validate(holder -> holder == null ? DataResult.error(() -> "Unknown transmog holder attempted to load") : DataResult.success(holder));

   public TransmogHolder(ResourceLocation id, PatreonPermissions permissions) {
      this(id, permissions, ALL_SLOTS, TransmogHolder.DyeConfig.NONE);
   }

   public boolean supportsSlot(EquipmentSlot slot) {
      return this.supportedSlots.contains(slot);
   }

   public boolean supportsSlot(ItemStack itemStack) {
      if (itemStack.getItem() instanceof Equipable equipable && this.supportsSlot(equipable.getEquipmentSlot())) {
         return true;
      }

      return false;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         if (obj instanceof TransmogHolder oth && this.id.equals(oth.id)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   public String descriptionId() {
      return String.format("transmog.%s.%s", this.id.getNamespace(), this.id.getPath());
   }

   public static record DyeConfig(boolean dyeable, int defaultColor) {
      public static final TransmogHolder.DyeConfig NONE = new TransmogHolder.DyeConfig(false, -1);
   }
}
