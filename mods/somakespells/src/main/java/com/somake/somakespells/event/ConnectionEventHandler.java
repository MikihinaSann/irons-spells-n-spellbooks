package com.somake.somakespells.event;

import com.somake.somakespells.capabilities.ConnectionKind;
import com.somake.somakespells.capabilities.ConnectionLinkData;
import com.somake.somakespells.capabilities.ConnectionPendingCastData;
import com.somake.somakespells.capabilities.ConnectionTransferQueueData;
import com.somake.somakespells.entity.ConnectionChainVisualEntity;
import com.somake.somakespells.network.ConnectionVfxPacket;
import com.somake.somakespells.registries.ModAttachments;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.blood.CursedConnectionSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class ConnectionEventHandler {
   private static final ThreadLocal<Boolean> APPLYING_LINK_DAMAGE = ThreadLocal.withInitial(() -> false);
   private static final ThreadLocal<Boolean> APPLYING_LINK_HEAL = ThreadLocal.withInitial(() -> false);
   private static final boolean MOWZIE_LOADED = ModList.get().isLoaded("mowziesmobs");
   private static final double CONNECTION_MAX_DISTANCE_BASE = 24.0;
   private static final double CONNECTION_MAX_DISTANCE_PER_LEVEL = 8.0;
   private static final double SHACKLE_PULL_BASE = 0.08;
   private static final double SHACKLE_PULL_PER_BLOCK = 0.14;
   private static final double SHACKLE_MAX_PULL = 0.6;
   private static final ResourceLocation MOWZIE_BLOCK_SOUND = ResourceLocation.fromNamespaceAndPath("mowziesmobs", "wroughtnaut.undamaged");
   private static final ResourceLocation[] MOWZIE_GUARDIAN_CAST_SOUNDS = new ResourceLocation[]{
      ResourceLocation.fromNamespaceAndPath("mowziesmobs", "umvuthana.healstart1"),
      ResourceLocation.fromNamespaceAndPath("mowziesmobs", "umvuthana.healstart2"),
      ResourceLocation.fromNamespaceAndPath("mowziesmobs", "umvuthana.healstart3")
   };

   @SubscribeEvent
   public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
      if (!APPLYING_LINK_DAMAGE.get()) {
         LivingEntity entity = event.getEntity();
         if (entity.level() instanceof ServerLevel serverLevel && !(event.getAmount() <= 0.0F) && entity.isAlive()) {
            ConnectionLinkData.LinkEntry link = ((ConnectionLinkData)entity.getData(ModAttachments.CONNECTION_LINK_DATA)).getLink(ConnectionKind.GUARDIAN);
            if (link != null) {
               if (!link.isActive(serverLevel.getGameTime())) {
                  clearLink(entity, ConnectionKind.GUARDIAN);
               } else {
                  LivingEntity partner = getLinkedEntity(serverLevel, link.getPartnerUuid());
                  if (partner == null || !partner.isAlive()) {
                     clearLink(entity, ConnectionKind.GUARDIAN);
                  } else if (!isLinkOwner(entity, link)) {
                     if (isPastConnectionMaxDistance(entity, partner, link.getSpellLevel())) {
                        clearLink(entity, ConnectionKind.GUARDIAN);
                     } else {
                        float redirectedDamage = event.getAmount();
                        if (!(redirectedDamage <= 0.0F)) {
                           playMowzieGuardianBlockSound(serverLevel, entity);
                           event.setCanceled(true);
                           event.setInvulnerabilityTicks(0);
                           int travelTicks = calculateTransferTravelTicks(entity, partner);
                           sendVfx(serverLevel, entity, partner, link.getKind(), true, travelTicks, true);
                           APPLYING_LINK_DAMAGE.set(true);

                           try {
                              partner.invulnerableTime = 0;
                              partner.hurt(event.getSource(), redirectedDamage);
                           } finally {
                              APPLYING_LINK_DAMAGE.set(false);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      if (!APPLYING_LINK_DAMAGE.get()) {
         LivingEntity entity = event.getEntity();
         if (entity.level() instanceof ServerLevel serverLevel && !(event.getNewDamage() <= 0.0F) && entity.isAlive()) {
            ConnectionLinkData.LinkEntry link = ((ConnectionLinkData)entity.getData(ModAttachments.CONNECTION_LINK_DATA)).getLink(ConnectionKind.CURSED);
            if (link != null) {
               if (!link.isActive(serverLevel.getGameTime())) {
                  clearLink(entity, ConnectionKind.CURSED);
               } else {
                  LivingEntity partner = getLinkedEntity(serverLevel, link.getPartnerUuid());
                  if (partner == null || !partner.isAlive()) {
                     clearLink(entity, ConnectionKind.CURSED);
                  } else if (isPastConnectionMaxDistance(entity, partner, link.getSpellLevel())) {
                     clearLink(entity, ConnectionKind.CURSED);
                  } else {
                     float reflectedDamage = event.getNewDamage() * getShareMultiplier(link.getSpellLevel());
                     if (!(reflectedDamage <= 0.0F)) {
                        LivingEntity owner = getLinkedEntity(serverLevel, link.getOwnerUuid());
                        int travelTicks = calculateTransferTravelTicks(entity, partner);
                        queueTransfer(serverLevel, entity, partner, owner, reflectedDamage, link.getKind(), travelTicks);
                        sendVfx(serverLevel, entity, partner, link.getKind(), true, travelTicks, true);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHeal(LivingHealEvent event) {
      if (!APPLYING_LINK_HEAL.get()) {
         LivingEntity entity = event.getEntity();
         if (entity.level() instanceof ServerLevel serverLevel && !(event.getAmount() <= 0.0F) && entity.isAlive()) {
            ConnectionLinkData.LinkEntry link = ((ConnectionLinkData)entity.getData(ModAttachments.CONNECTION_LINK_DATA)).getLink(ConnectionKind.BLESSED);
            if (link != null) {
               if (!link.isActive(serverLevel.getGameTime())) {
                  clearLink(entity, ConnectionKind.BLESSED);
               } else {
                  LivingEntity partner = getLinkedEntity(serverLevel, link.getPartnerUuid());
                  if (partner == null || !partner.isAlive()) {
                     clearLink(entity, ConnectionKind.BLESSED);
                  } else if (isPastConnectionMaxDistance(entity, partner, link.getSpellLevel())) {
                     clearLink(entity, ConnectionKind.BLESSED);
                  } else {
                     float sharedHeal = event.getAmount() * getShareMultiplier(link.getSpellLevel());
                     if (!(sharedHeal <= 0.0F)) {
                        int travelTicks = calculateTransferTravelTicks(entity, partner);
                        LivingEntity owner = getLinkedEntity(serverLevel, link.getOwnerUuid());
                        queueTransfer(serverLevel, entity, partner, owner, sharedHeal, link.getKind(), travelTicks);
                        sendVfx(serverLevel, entity, partner, link.getKind(), true, travelTicks, true);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingTick(Pre event) {
      if (event.getEntity() instanceof LivingEntity entity && entity.level() instanceof ServerLevel serverLevel) {
         if (entity instanceof ServerPlayer player) {
            ConnectionPendingCastData pendingCastData = (ConnectionPendingCastData)player.getData(ModAttachments.CONNECTION_PENDING_CAST);
            if (pendingCastData.hasPendingTarget() && !pendingCastData.isActive(serverLevel.getGameTime())) {
               pendingCastData.clear();
            }
         }

         processPendingTransfers(serverLevel, entity);
         ConnectionLinkData linkData = (ConnectionLinkData)entity.getData(ModAttachments.CONNECTION_LINK_DATA);
         if (linkData.hasLink()) {
            for (ConnectionLinkData.LinkEntry link : new ArrayList<>(linkData.getLinks())) {
               if (!link.isActive(serverLevel.getGameTime())) {
                  clearLink(entity, link.getKind());
               } else {
                  LivingEntity partner = getLinkedEntity(serverLevel, link.getPartnerUuid());
                  if (partner == null || !partner.isAlive()) {
                     clearLink(entity, link.getKind());
                  } else if (!link.getKind().isRestraining() && isPastConnectionMaxDistance(entity, partner, link.getSpellLevel())) {
                     clearLink(entity, link.getKind());
                  } else if (link.getKind().isRestraining()) {
                     if (entity.getUUID().compareTo(partner.getUUID()) < 0) {
                        applyShackleConstraint(entity, partner, link.getSpellLevel());
                        ensureChainVisual(serverLevel, entity, partner);
                     }
                  } else if (entity.tickCount % 3 == 0 && entity.getUUID().compareTo(partner.getUUID()) < 0) {
                     sendVfx(serverLevel, entity, partner, link.getKind(), false, 0, true);
                  }
               }
            }
         }
      }
   }

   public static void createLink(
      ServerLevel level, LivingEntity owner, LivingEntity firstTarget, LivingEntity secondTarget, int spellLevel, int durationTicks, ConnectionKind kind
   ) {
      if (firstTarget != secondTarget) {
         clearLink(firstTarget, kind);
         clearLink(secondTarget, kind);
         long expiresAt = level.getGameTime() + durationTicks;
         ((ConnectionLinkData)firstTarget.getData(ModAttachments.CONNECTION_LINK_DATA))
            .set(secondTarget.getUUID(), owner.getUUID(), spellLevel, expiresAt, kind);
         ((ConnectionLinkData)secondTarget.getData(ModAttachments.CONNECTION_LINK_DATA))
            .set(firstTarget.getUUID(), owner.getUUID(), spellLevel, expiresAt, kind);
         MobEffect effect = getEffectForKind(kind);
         if (effect != null) {
            Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
            if (kind.isProtective()) {
               secondTarget.addEffect(new MobEffectInstance(holder, durationTicks, spellLevel - 1, false, true, true));
            } else {
               firstTarget.addEffect(new MobEffectInstance(holder, durationTicks, spellLevel - 1, false, true, true));
               secondTarget.addEffect(new MobEffectInstance(holder, durationTicks, spellLevel - 1, false, true, true));
            }
         }

         sendVfx(level, firstTarget, secondTarget, kind, false, 0, true);
         if (kind.isProtective()) {
            playMowzieGuardianCastSound(level, owner);
         }
      }
   }

   public static void clearLink(LivingEntity entity) {
      ConnectionLinkData linkData = (ConnectionLinkData)entity.getData(ModAttachments.CONNECTION_LINK_DATA);
      if (!linkData.hasLink()) {
         removeConnectionEffects(entity);
      } else {
         for (ConnectionKind kind : new ArrayList<>(linkData.getKinds())) {
            clearLink(entity, kind);
         }
      }
   }

   public static void clearLink(LivingEntity entity, ConnectionKind kind) {
      if (kind != null) {
         if (entity.level() instanceof ServerLevel serverLevel) {
            ConnectionLinkData linkData = (ConnectionLinkData)entity.getData(ModAttachments.CONNECTION_LINK_DATA);
            ConnectionLinkData.LinkEntry link = linkData.getLink(kind);
            if (link == null) {
               removeConnectionEffect(entity, kind);
            } else {
               LivingEntity partner = getLinkedEntity(serverLevel, link.getPartnerUuid());
               linkData.clear(kind);
               ((ConnectionTransferQueueData)entity.getData(ModAttachments.CONNECTION_TRANSFER_QUEUE)).clear(kind);
               removeConnectionEffect(entity, kind);
               if (partner != null) {
                  ConnectionLinkData partnerData = (ConnectionLinkData)partner.getData(ModAttachments.CONNECTION_LINK_DATA);
                  ConnectionLinkData.LinkEntry partnerLink = partnerData.getLink(kind);
                  if (partnerLink != null && entity.getUUID().equals(partnerLink.getPartnerUuid())) {
                     partnerData.clear(kind);
                     ((ConnectionTransferQueueData)partner.getData(ModAttachments.CONNECTION_TRANSFER_QUEUE)).clear(kind);
                     removeConnectionEffect(partner, kind);
                  }
               }
            }
         } else {
            ((ConnectionLinkData)entity.getData(ModAttachments.CONNECTION_LINK_DATA)).clear(kind);
            ((ConnectionTransferQueueData)entity.getData(ModAttachments.CONNECTION_TRANSFER_QUEUE)).clear(kind);
            removeConnectionEffect(entity, kind);
         }
      }
   }

   private static void removeConnectionEffects(LivingEntity entity) {
      if (ModMobEffects.BLESSED_CONNECTION != null) {
         entity.removeEffect(ModMobEffects.BLESSED_CONNECTION);
      }

      if (ModMobEffects.GUARDIAN_CONNECTION != null) {
         entity.removeEffect(ModMobEffects.GUARDIAN_CONNECTION);
      }

      if (ModMobEffects.CURSED_CONNECTION != null) {
         entity.removeEffect(ModMobEffects.CURSED_CONNECTION);
      }
   }

   private static void removeConnectionEffect(LivingEntity entity, ConnectionKind kind) {
      MobEffect effect = getEffectForKind(kind);
      if (effect != null) {
         entity.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect));
      }
   }

   private static MobEffect getEffectForKind(ConnectionKind kind) {
      if (kind.isHealing()) {
         return ModMobEffects.BLESSED_CONNECTION != null ? (MobEffect)ModMobEffects.BLESSED_CONNECTION.get() : null;
      } else if (kind.isProtective()) {
         return ModMobEffects.GUARDIAN_CONNECTION != null ? (MobEffect)ModMobEffects.GUARDIAN_CONNECTION.get() : null;
      } else if (kind.isRestraining()) {
         return null;
      } else {
         return ModMobEffects.CURSED_CONNECTION != null ? (MobEffect)ModMobEffects.CURSED_CONNECTION.get() : null;
      }
   }

   private static boolean isLinkOwner(LivingEntity entity, ConnectionLinkData.LinkEntry link) {
      return link != null && entity.getUUID().equals(link.getOwnerUuid());
   }

   private static void playMowzieGuardianCastSound(ServerLevel level, LivingEntity entity) {
      if (MOWZIE_LOADED && entity != null) {
         ResourceLocation soundId = MOWZIE_GUARDIAN_CAST_SOUNDS[level.random.nextInt(MOWZIE_GUARDIAN_CAST_SOUNDS.length)];
         SoundEvent sound = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(soundId);
         if (sound != null) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, entity.getSoundSource(), 4.0F, 1.0F);
         }
      }
   }

   private static void playMowzieGuardianBlockSound(ServerLevel level, LivingEntity entity) {
      if (MOWZIE_LOADED && entity != null) {
         SoundEvent sound = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(MOWZIE_BLOCK_SOUND);
         if (sound != null && eventSourceCanTriggerBlockSound(entity)) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, entity.getSoundSource(), 0.4F, 2.0F);
         }
      }
   }

   private static boolean eventSourceCanTriggerBlockSound(LivingEntity entity) {
      return entity.isAlive();
   }

   private static void ensureChainVisual(ServerLevel level, LivingEntity firstTarget, LivingEntity secondTarget) {
      AABB searchBox = new AABB(firstTarget.position(), secondTarget.position()).inflate(8.0);

      for (ConnectionChainVisualEntity visual : level.getEntitiesOfClass(ConnectionChainVisualEntity.class, searchBox)) {
         if (visual.matches(firstTarget, secondTarget)) {
            return;
         }
      }

      ConnectionChainVisualEntity visualx = new ConnectionChainVisualEntity(level, firstTarget, secondTarget);
      level.addFreshEntity(visualx);
   }

   private static boolean applyShackleConstraint(LivingEntity firstTarget, LivingEntity secondTarget, int spellLevel) {
      double maxDistance = getShackleMaxDistance(spellLevel);
      Vec3 firstCenter = firstTarget.getBoundingBox().getCenter();
      Vec3 secondCenter = secondTarget.getBoundingBox().getCenter();
      Vec3 between = secondCenter.subtract(firstCenter);
      double distance = between.length();
      if (!(distance <= maxDistance) && !(distance < 0.001)) {
         Vec3 direction = between.normalize();
         double excess = distance - maxDistance;
         double pullStrength = Math.min(0.08 + excess * 0.14, 0.6);
         Vec3 pullVector = direction.scale(pullStrength);
         firstTarget.setDeltaMovement(firstTarget.getDeltaMovement().add(pullVector));
         secondTarget.setDeltaMovement(secondTarget.getDeltaMovement().subtract(pullVector));
         firstTarget.hurtMarked = true;
         secondTarget.hurtMarked = true;
         firstTarget.resetFallDistance();
         secondTarget.resetFallDistance();
         return true;
      } else {
         return false;
      }
   }

   private static double getShackleMaxDistance(int spellLevel) {
      return 4.5 + spellLevel * 0.5;
   }

   private static LivingEntity getLinkedEntity(ServerLevel level, UUID uuid) {
      if (uuid == null) {
         return null;
      } else {
         return level.getEntity(uuid) instanceof LivingEntity livingEntity ? livingEntity : null;
      }
   }

   private static float getShareMultiplier(int spellLevel) {
      return Math.max(0.0F, (float)spellLevel) * 0.1F;
   }

   public static double getConnectionMaxDistance(int spellLevel) {
      return 24.0 + Math.max(0, spellLevel - 1) * 8.0;
   }

   private static boolean isPastConnectionMaxDistance(LivingEntity firstTarget, LivingEntity secondTarget, int spellLevel) {
      double maxDistance = getConnectionMaxDistance(spellLevel);
      return firstTarget.distanceToSqr(secondTarget) > maxDistance * maxDistance;
   }

   private static int calculateTransferTravelTicks(LivingEntity firstTarget, LivingEntity secondTarget) {
      Vec3 start = firstTarget.getBoundingBox().getCenter();
      Vec3 end = secondTarget.getBoundingBox().getCenter();
      return Math.max(1, (int)Math.ceil(Math.min(2.0 * start.distanceTo(end), 60.0)));
   }

   private static void queueTransfer(
      ServerLevel level, LivingEntity source, LivingEntity target, LivingEntity owner, float amount, ConnectionKind kind, int travelTicks
   ) {
      long executeAt = level.getGameTime() + Math.max(1, travelTicks);
      UUID ownerUuid = owner != null ? owner.getUUID() : null;
      ((ConnectionTransferQueueData)target.getData(ModAttachments.CONNECTION_TRANSFER_QUEUE)).enqueue(source.getUUID(), ownerUuid, amount, executeAt, kind);
   }

   private static void processPendingTransfers(ServerLevel level, LivingEntity target) {
      ConnectionTransferQueueData queueData = (ConnectionTransferQueueData)target.getData(ModAttachments.CONNECTION_TRANSFER_QUEUE);
      if (queueData.hasPendingTransfers()) {
         Iterator<ConnectionTransferQueueData.PendingTransfer> iterator = queueData.getPendingTransfers().iterator();

         while (iterator.hasNext()) {
            ConnectionTransferQueueData.PendingTransfer pendingTransfer = iterator.next();
            if (pendingTransfer.isReady(level.getGameTime())) {
               iterator.remove();
               if (canApplyPendingTransfer(level, target, pendingTransfer)) {
                  applyPendingTransfer(level, target, pendingTransfer);
               }
            }
         }
      }
   }

   private static boolean canApplyPendingTransfer(ServerLevel level, LivingEntity target, ConnectionTransferQueueData.PendingTransfer pendingTransfer) {
      if (!target.isAlive()) {
         return false;
      } else {
         LivingEntity source = getLinkedEntity(level, pendingTransfer.getSourceUuid());
         if (source != null && source.isAlive()) {
            ConnectionLinkData.LinkEntry targetLink = ((ConnectionLinkData)target.getData(ModAttachments.CONNECTION_LINK_DATA))
               .getLink(pendingTransfer.getKind());
            if (targetLink != null && targetLink.isActive(level.getGameTime())) {
               if (!source.getUUID().equals(targetLink.getPartnerUuid())) {
                  return false;
               } else {
                  ConnectionLinkData.LinkEntry sourceLink = ((ConnectionLinkData)source.getData(ModAttachments.CONNECTION_LINK_DATA))
                     .getLink(pendingTransfer.getKind());
                  return sourceLink != null && sourceLink.isActive(level.getGameTime()) ? target.getUUID().equals(sourceLink.getPartnerUuid()) : false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private static void applyPendingTransfer(ServerLevel level, LivingEntity target, ConnectionTransferQueueData.PendingTransfer pendingTransfer) {
      LivingEntity source = getLinkedEntity(level, pendingTransfer.getSourceUuid());
      LivingEntity owner = getLinkedEntity(level, pendingTransfer.getOwnerUuid());
      if (pendingTransfer.getKind().isHealing()) {
         APPLYING_LINK_HEAL.set(true);

         try {
            target.heal(pendingTransfer.getAmount());
         } finally {
            APPLYING_LINK_HEAL.set(false);
         }
      } else {
         APPLYING_LINK_DAMAGE.set(true);

         try {
            target.invulnerableTime = 0;
            if (ModSpells.CURSED_CONNECTION != null) {
               LivingEntity damageOwner = owner != null ? owner : (source != null ? source : target);
               DamageSources.applyDamage(
                  target, pendingTransfer.getAmount(), ((CursedConnectionSpell)ModSpells.CURSED_CONNECTION.get()).getDamageSource(damageOwner)
               );
            }
         } finally {
            APPLYING_LINK_DAMAGE.set(false);
         }
      }
   }

   private static void sendVfx(
      ServerLevel level, LivingEntity firstTarget, LivingEntity secondTarget, ConnectionKind kind, boolean transferPulse, int travelTicks, boolean shouldSend
   ) {
      if (MOWZIE_LOADED && shouldSend && !kind.isRestraining()) {
         Vec3 start = firstTarget.getBoundingBox().getCenter();
         Vec3 end = secondTarget.getBoundingBox().getCenter();
         ConnectionVfxPacket packet = new ConnectionVfxPacket(start.x, start.y, start.z, end.x, end.y, end.z, kind, transferPulse, travelTicks);

         for (ServerPlayer player : level.players()) {
            PacketDistributor.sendToPlayer(player, packet, new CustomPacketPayload[0]);
         }
      }
   }
}
