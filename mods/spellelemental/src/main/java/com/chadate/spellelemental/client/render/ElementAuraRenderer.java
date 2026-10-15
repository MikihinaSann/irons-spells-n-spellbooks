package com.chadate.spellelemental.client.render;

import com.chadate.spellelemental.client.network.custom.ClientPayloadHandler;
import com.chadate.spellelemental.data.ElementContainerAttachment;
import com.chadate.spellelemental.data.SpellAttachments;
import com.chadate.spellelemental.element.attachment.data.UnifiedElementAttachmentAssets;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Post;

@EventBusSubscriber(
   modid = "spellelemental",
   value = {Dist.CLIENT}
)
public class ElementAuraRenderer {
   private static final Random RNG = new Random();

   @SubscribeEvent
   public static void onRenderLivingPost(Post<LivingEntity, ?> event) {
      Minecraft mc = Minecraft.getInstance();
      if (!mc.isPaused()) {
         LivingEntity entity = event.getEntity();
         Level level = entity.level();
         if (level instanceof ClientLevel) {
            Map<String, Integer> snapshot = ((ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER)).snapshot();
            if (!snapshot.isEmpty()) {
               for (String elementKey : snapshot.keySet()) {
                  int remain = ClientPayloadHandler.DisplayCache.predictRemaining(entity.getId(), elementKey);
                  if (remain != 0) {
                     String particleKey = UnifiedElementAttachmentAssets.getParticleEffect(elementKey);
                     if (particleKey != null && !particleKey.isEmpty()) {
                        SimpleParticleType simple = resolveSimpleParticle(particleKey);
                        if (simple != null) {
                           float baseChance = 0.15F;
                           float bonus = Math.max(0.0F, Math.min(0.15F, (50 - remain) / 50.0F * 0.15F));
                           float chance = baseChance + bonus;
                           if (!(RNG.nextFloat() > chance)) {
                              double radius = 0.6;
                              double angle = RNG.nextDouble() * Math.PI * 2.0;
                              double yOff = 0.2 + RNG.nextDouble() * (entity.getBbHeight() * 0.8);
                              double px = entity.getX() + Math.cos(angle) * radius;
                              double py = entity.getY() + yOff;
                              double pz = entity.getZ() + Math.sin(angle) * radius;
                              double vx = (RNG.nextDouble() - 0.5) * 0.02;
                              double vy = 0.02 + RNG.nextDouble() * 0.02;
                              double vz = (RNG.nextDouble() - 0.5) * 0.02;
                              level.addParticle(simple, px, py, pz, vx, vy, vz);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static SimpleParticleType resolveSimpleParticle(String key) {
      try {
         ResourceLocation rl = ResourceLocation.parse(key);
         ParticleType<?> type = (ParticleType<?>)BuiltInRegistries.PARTICLE_TYPE.get(rl);
         if (type instanceof SimpleParticleType simple) {
            return simple;
         }
      } catch (Exception var4) {
      }

      return null;
   }

   private static final class ClientAuraConfig {
      static final double RADIUS = 0.6;
      static final int FLASH_THRESHOLD = 50;
   }
}
