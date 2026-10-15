package com.chadate.spellelemental.element.attachment.attack;

import com.chadate.spellelemental.config.ServerConfig;
import com.chadate.spellelemental.data.ElementContainerAttachment;
import com.chadate.spellelemental.data.SpellAttachments;
import com.chadate.spellelemental.element.attachment.data.UnifiedElementAttachmentAssets;
import com.chadate.spellelemental.event.element.ElementDecaySystem;
import com.chadate.spellelemental.network.ElementData;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import java.util.Map;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking;
import net.neoforged.neoforge.network.PacketDistributor;

public class ElementEventHandler {
   public static void handleElementAttachment(SpellDamageEvent event) {
      if (!event.getEntity().level().isClientSide()) {
         LivingEntity target = event.getEntity();
         String spellId = event.getSpellDamageSource().spell().getSpellId();
         String school = event.getSpellDamageSource().spell().getSchoolType().getId().toString();
         String elementId = null;
         ResourceLocation spellKey = ResourceLocation.tryParse(spellId);
         if (spellKey != null) {
            elementId = ServerConfig.getSpellElementOverride(spellKey);
         }

         if (elementId == null || elementId.isBlank()) {
            elementId = UnifiedElementAttachmentAssets.getElementIdBySchool(school);
            if (elementId == null || elementId.isBlank()) {
               return;
            }
         }

         int duration = (Integer)ServerConfig.ELEMENT_ATTACHMENT_DEFAULT.get();
         if (spellKey != null) {
            duration = ServerConfig.getSpellAttachmentAmount(spellKey);
         }

         Entity attacker = event.getSpellDamageSource().getEntity();
         int attackerId = attacker != null ? attacker.getId() : -1;
         int step = ServerConfig.getIcdHitStep();
         int timeTicks = ServerConfig.getIcdTimeTicks();
         long now = target.level().getGameTime();
         boolean allow = SpellIcdTracker.allowAndRecord(attacker, target, spellKey, now, step, timeTicks);
         if (allow) {
            applyAttachment(target, elementId.toLowerCase(), duration, attackerId);
         }
      }
   }

   private static void applyAttachment(LivingEntity entity, String elementKeyLower, int duration, int attackerId) {
      ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
      container.setValue(elementKeyLower, duration);
      long gameTime = entity.level().getGameTime();
      container.markAppliedWithAttacker(elementKeyLower, gameTime, attackerId);
      ElementDecaySystem.track(entity);
      if (entity.level() instanceof ServerLevel serverLevel) {
         for (ServerPlayer player : serverLevel.players()) {
            double distance = player.distanceTo(entity);
            if (distance <= 64.0) {
               PacketDistributor.sendToPlayer(player, new ElementData(entity.getId(), elementKeyLower, duration), new CustomPacketPayload[0]);
            }
         }
      }
   }

   public static void onStartTracking(StartTracking event) {
      if (!event.getEntity().level().isClientSide()) {
         if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getTarget() instanceof LivingEntity living) {
               ElementContainerAttachment container = (ElementContainerAttachment)living.getData(SpellAttachments.ELEMENTS_CONTAINER);
               Map<String, Integer> snap = container.snapshot();
               String[] keys = snap.keySet().toArray(new String[0]);
               int[] values = new int[keys.length];

               for (int i = 0; i < keys.length; i++) {
                  values[i] = snap.get(keys[i]);
               }

               PacketDistributor.sendToPlayer(player, new ElementData.ElementSnapshot(living.getId(), keys, values), new CustomPacketPayload[0]);
            }
         }
      }
   }
}
