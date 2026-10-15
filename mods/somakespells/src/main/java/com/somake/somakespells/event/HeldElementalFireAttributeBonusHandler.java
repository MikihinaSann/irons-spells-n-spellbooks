package com.somake.somakespells.event;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.registries.ModAttributes;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class HeldElementalFireAttributeBonusHandler {
   private static final double BONUS_AMOUNT = 0.15;
   private static final ResourceLocation HELD_FIRE_SPELL_POWER_BONUS_ID = ResourceLocation.fromNamespaceAndPath("somakespells", "held_fire_spell_power_bonus");
   private static final ResourceLocation HELD_SOUL_FIRE_SPELL_POWER_BONUS_ID = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "held_soul_fire_spell_power_bonus"
   );
   private static final ResourceLocation HELD_INFERNAL_FIRE_SPELL_POWER_BONUS_ID = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "held_infernal_fire_spell_power_bonus"
   );

   @SubscribeEvent
   public static void onEntityTick(Pre event) {
      if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
         long gameTime = player.level().getGameTime();
         long activeUntil = player.getPersistentData().getLong("somakespells_held_fire_bonus_until");
         int source = activeUntil >= gameTime ? player.getPersistentData().getInt("somakespells_held_fire_source") : 0;
         if (source == 0) {
            player.getPersistentData().remove("somakespells_held_fire_source");
            player.getPersistentData().remove("somakespells_held_fire_bonus_until");
         }

         syncModifier(player.getAttribute(AttributeRegistry.FIRE_SPELL_POWER), HELD_FIRE_SPELL_POWER_BONUS_ID, source == 1);
         syncModifier(player.getAttribute(ModAttributes.SOUL_FIRE_SPELL_POWER), HELD_SOUL_FIRE_SPELL_POWER_BONUS_ID, source == 2);
         syncModifier(
            BornInChaosCompat.isLoaded() ? player.getAttribute(ModAttributes.INFERNAL_FIRE_SPELL_POWER) : null,
            HELD_INFERNAL_FIRE_SPELL_POWER_BONUS_ID,
            source == 3
         );
      }
   }

   private static void syncModifier(AttributeInstance attribute, ResourceLocation id, boolean active) {
      if (attribute != null) {
         if (active) {
            attribute.addOrUpdateTransientModifier(new AttributeModifier(id, 0.15, Operation.ADD_MULTIPLIED_BASE));
         } else if (attribute.hasModifier(id)) {
            attribute.removeModifier(id);
         }
      }
   }
}
