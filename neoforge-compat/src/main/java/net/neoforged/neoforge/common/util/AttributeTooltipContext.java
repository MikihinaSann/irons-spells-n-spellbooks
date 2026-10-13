package net.neoforged.neoforge.common.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.Nullable;

public interface AttributeTooltipContext extends TooltipContext {
   @Nullable
   Player player();

   TooltipFlag flag();

   static AttributeTooltipContext of(@Nullable Player player, TooltipContext itemCtx, TooltipFlag flag) {
      return new AttributeTooltipContext() {
         @Nullable
         @Override
         public Player player() {
            return player;
         }

         @Override
         public TooltipFlag flag() {
            return flag;
         }

         @Override
         public net.minecraft.core.HolderLookup.Provider registries() {
            return itemCtx.registries();
         }

         @Override
         public float tickRate() {
            return itemCtx.tickRate();
         }

         @Override
         public net.minecraft.world.level.saveddata.maps.MapItemSavedData mapData(
               net.minecraft.world.level.saveddata.maps.MapId id) {
            return itemCtx.mapData(id);
         }

      };
   }
}
