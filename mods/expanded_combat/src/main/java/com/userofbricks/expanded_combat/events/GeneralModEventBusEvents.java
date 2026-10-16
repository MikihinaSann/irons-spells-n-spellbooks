package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.init.ECAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(
   modid = "expanded_combat",
   bus = Bus.MOD
)
public class GeneralModEventBusEvents {
   @SubscribeEvent
   public static void existingEntityAttributes(EntityAttributeModificationEvent event) {
      for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
         if (!event.has(entityType, ECAttributes.GAUNTLET_DMG_WITHOUT_WEAPON)) {
            event.add(entityType, ECAttributes.GAUNTLET_DMG_WITHOUT_WEAPON);
         }

         if (!event.has(entityType, ECAttributes.HEAT_DMG)) {
            event.add(entityType, ECAttributes.HEAT_DMG);
         }

         if (!event.has(entityType, ECAttributes.COLD_DMG)) {
            event.add(entityType, ECAttributes.COLD_DMG);
         }

         if (!event.has(entityType, ECAttributes.VOID_DMG)) {
            event.add(entityType, ECAttributes.VOID_DMG);
         }

         if (!event.has(entityType, ECAttributes.SOUL_DMG)) {
            event.add(entityType, ECAttributes.SOUL_DMG);
         }
      }
   }
}
