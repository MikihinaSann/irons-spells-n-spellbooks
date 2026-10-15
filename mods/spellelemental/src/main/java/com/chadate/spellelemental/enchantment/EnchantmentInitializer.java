package com.chadate.spellelemental.enchantment;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

@EventBusSubscriber(
   modid = "spellelemental"
)
public class EnchantmentInitializer {
   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      AstralBlessingEnchantmentHandler.updateAstralBlessingAttribute(player);
   }

   @SubscribeEvent
   public static void onPlayerRespawn(PlayerRespawnEvent event) {
      Player player = event.getEntity();
      AstralBlessingEnchantmentHandler.updateAstralBlessingAttribute(player);
   }

   @SubscribeEvent
   public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof LivingEntity livingEntity && !(livingEntity instanceof Player)) {
         AstralBlessingEnchantmentHandler.updateAstralBlessingAttribute(livingEntity);
      }
   }
}
