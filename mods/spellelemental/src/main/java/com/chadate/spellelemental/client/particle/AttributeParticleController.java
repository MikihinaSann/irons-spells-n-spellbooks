package com.chadate.spellelemental.client.particle;

import com.chadate.spellelemental.register.ModAttributes;
import com.chadate.spellelemental.register.ModParticles;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;

@EventBusSubscriber(
   modid = "spellelemental",
   value = {Dist.CLIENT}
)
public class AttributeParticleController {
   private static final Map<UUID, Set<ResourceLocation>> TRACKED_PHYSICAL_RESIST_MODIFIERS = new HashMap<>();
   private static final Map<UUID, Integer> PARTICLE_TIMERS = new HashMap<>();
   private static final int PARTICLE_INTERVAL = 10;

   @SubscribeEvent
   public static void onClientTick(Post event) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && !mc.isPaused()) {
         ClientLevel level = mc.level;
         if (level != null) {
            level.entitiesForRendering().forEach(entity -> {
               if (entity instanceof LivingEntity livingEntity) {
                  checkPhysicalResistanceModifiers(livingEntity);
               }
            });
            cleanupRemovedEntities(level);
         }
      }
   }

   private static void checkPhysicalResistanceModifiers(LivingEntity entity) {
      UUID entityId = entity.getUUID();
      AttributeInstance physicalResistInstance = entity.getAttribute(ModAttributes.PHYSICAL_DAMAGE_RESIST);
      if (physicalResistInstance != null) {
         boolean hasNegativeModifier = false;

         for (AttributeModifier modifier : physicalResistInstance.getModifiers()) {
            if (modifier.amount() < 0.0) {
               hasNegativeModifier = true;
               break;
            }
         }

         if (hasNegativeModifier) {
            spawnPhysicalResistanceDownParticles(entity);
            TRACKED_PHYSICAL_RESIST_MODIFIERS.put(entityId, new HashSet<>());
         } else {
            PARTICLE_TIMERS.remove(entityId);
            TRACKED_PHYSICAL_RESIST_MODIFIERS.remove(entityId);
         }
      }
   }

   private static void spawnPhysicalResistanceDownParticles(LivingEntity entity) {
      UUID entityId = entity.getUUID();
      int timer = PARTICLE_TIMERS.getOrDefault(entityId, 0);
      if (timer <= 0) {
         ClientLevel level = (ClientLevel)entity.level();
         int particleCount = 1 + level.random.nextInt(3);

         for (int i = 0; i < particleCount; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            double radius = 0.3 + level.random.nextDouble() * 0.4;
            double height = level.random.nextDouble() * entity.getBbHeight();
            double x = entity.getX() + Math.cos(angle) * radius;
            double y = entity.getY() + height;
            double z = entity.getZ() + Math.sin(angle) * radius;
            level.addParticle((ParticleOptions)ModParticles.PHYSICAL_RESISTANCE_DOWN.get(), x, y, z, 0.0, 0.0, 0.0);
         }

         PARTICLE_TIMERS.put(entityId, 10);
      } else {
         PARTICLE_TIMERS.put(entityId, timer - 1);
      }
   }

   private static void cleanupRemovedEntities(ClientLevel level) {
      Set<UUID> existingEntities = new HashSet<>();
      level.entitiesForRendering().forEach(entity -> {
         if (entity instanceof LivingEntity) {
            existingEntities.add(entity.getUUID());
         }
      });
      TRACKED_PHYSICAL_RESIST_MODIFIERS.keySet().removeIf(uuid -> !existingEntities.contains(uuid));
      PARTICLE_TIMERS.keySet().removeIf(uuid -> !existingEntities.contains(uuid));
   }

   public static void cleanupEntity(UUID entityId) {
      TRACKED_PHYSICAL_RESIST_MODIFIERS.remove(entityId);
      PARTICLE_TIMERS.remove(entityId);
   }
}
