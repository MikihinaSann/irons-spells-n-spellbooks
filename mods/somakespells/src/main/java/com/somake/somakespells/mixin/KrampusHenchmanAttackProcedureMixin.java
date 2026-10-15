package com.somake.somakespells.mixin;

import com.somake.somakespells.config.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   targets = {"net.mcreator.borninchaosv.procedures.KrampusHenchmanAttackProcedure"},
   remap = false
)
public abstract class KrampusHenchmanAttackProcedureMixin {
   @Redirect(
      method = {"execute(Lnet/neoforged/bus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
      )
   )
   private static boolean somake$blockHenchmanDropSpawn(ServerLevel level, Entity toSpawn) {
      return Config.DISABLE_KRAMPUS_ITEM_STEAL.get() ? false : level.addFreshEntity(toSpawn);
   }

   @Redirect(
      method = {"execute(Lnet/neoforged/bus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"
      )
   )
   private static void somake$blockHenchmanMainHandShrink(ItemStack stack, int amount) {
      if (!(Boolean)Config.DISABLE_KRAMPUS_ITEM_STEAL.get()) {
         stack.shrink(amount);
      }
   }
}
