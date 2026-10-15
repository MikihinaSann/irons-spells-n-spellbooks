package com.chadate.spellelemental.element.reaction.runtime;

import com.chadate.spellelemental.SpellElemental;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;

public class AttributeEffectManager {
   private static final Map<Integer, List<AttributeEffectManager.ActiveAttributeEffect>> ACTIVE_EFFECTS = new ConcurrentHashMap<>();

   public static void registerTimedEffect(LivingEntity entity, Holder<Attribute> attributeHolder, ResourceLocation modifierId, int durationTicks) {
      if (entity != null && !entity.level().isClientSide() && durationTicks > 0) {
         long currentTime = entity.level().getGameTime();
         long expireTime = currentTime + durationTicks;
         int entityId = entity.getId();
         List<AttributeEffectManager.ActiveAttributeEffect> effects = ACTIVE_EFFECTS.computeIfAbsent(entityId, k -> new ArrayList<>());
         boolean found = false;

         for (int i = 0; i < effects.size(); i++) {
            AttributeEffectManager.ActiveAttributeEffect existing = effects.get(i);
            if (existing.modifierId.equals(modifierId) && existing.attributeHolder.equals(attributeHolder)) {
               effects.set(i, new AttributeEffectManager.ActiveAttributeEffect(entityId, attributeHolder, modifierId, expireTime));
               found = true;
               break;
            }
         }

         if (!found) {
            AttributeEffectManager.ActiveAttributeEffect effect = new AttributeEffectManager.ActiveAttributeEffect(
               entityId, attributeHolder, modifierId, expireTime
            );
            effects.add(effect);
         }
      } else {
         SpellElemental.LOGGER
            .warn(
               "跳过定时效果注册：entity={}, isClientSide={}, duration={}",
               new Object[]{entity != null ? entity.getName().getString() : "null", entity != null ? entity.level().isClientSide() : "unknown", durationTicks}
            );
      }
   }

   @SubscribeEvent
   public static void onServerTick(Post event) {
      if (!ACTIVE_EFFECTS.isEmpty()) {
         ServerLevel level = event.getServer().overworld();
         long currentTime = level.getGameTime();
         Iterator<Entry<Integer, List<AttributeEffectManager.ActiveAttributeEffect>>> entityIterator = ACTIVE_EFFECTS.entrySet().iterator();

         while (entityIterator.hasNext()) {
            Entry<Integer, List<AttributeEffectManager.ActiveAttributeEffect>> entry = entityIterator.next();
            int entityId = entry.getKey();
            List<AttributeEffectManager.ActiveAttributeEffect> effects = entry.getValue();
            LivingEntity livingEntity = findLivingEntityById(event.getServer(), entityId);
            if (livingEntity != null) {
               Iterator<AttributeEffectManager.ActiveAttributeEffect> effectIterator = effects.iterator();

               while (effectIterator.hasNext()) {
                  AttributeEffectManager.ActiveAttributeEffect effect = effectIterator.next();
                  if (currentTime >= effect.expireTime) {
                     removeAttributeModifier(livingEntity, effect.attributeHolder, effect.modifierId);
                     effectIterator.remove();
                  }
               }

               if (effects.isEmpty()) {
                  entityIterator.remove();
               }
            }
         }
      }
   }

   private static LivingEntity findLivingEntityById(MinecraftServer server, int entityId) {
      for (ServerLevel level : server.getAllLevels()) {
         if (level.getEntity(entityId) instanceof LivingEntity livingEntity) {
            return livingEntity;
         }
      }

      return null;
   }

   private static void removeAttributeModifier(LivingEntity entity, Holder<Attribute> attributeHolder, ResourceLocation modifierId) {
      try {
         AttributeInstance instance = entity.getAttribute(attributeHolder);
         if (instance != null) {
            instance.removeModifier(modifierId);
         }
      } catch (Exception var4) {
      }
   }

   public record ActiveAttributeEffect(int entityId, Holder<Attribute> attributeHolder, ResourceLocation modifierId, long expireTime) {
   }
}
