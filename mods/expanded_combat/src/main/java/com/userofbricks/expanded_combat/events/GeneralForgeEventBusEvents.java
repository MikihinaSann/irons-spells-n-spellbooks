package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.init.DataAttachments;
import com.userofbricks.expanded_combat.init.ECAttributes;
import com.userofbricks.expanded_combat.init.ECDamageInit;
import com.userofbricks.expanded_combat.network.server.PacketIntAttachment;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "expanded_combat",
   bus = Bus.GAME,
   value = {Dist.DEDICATED_SERVER}
)
public class GeneralForgeEventBusEvents {
   private static final List<Runnable> ADDITIONAL_DMG_RUNS = new ArrayList<>();

   @SubscribeEvent
   public static void dealExtraDmg(Pre event) {
      ADDITIONAL_DMG_RUNS.forEach(Runnable::run);
      ADDITIONAL_DMG_RUNS.clear();
   }

   @SubscribeEvent
   public static void moreDamageSources(Post ev) {
      Level level = ev.getEntity().level();
      Entity entity = ev.getSource().getEntity();
      if (entity instanceof LivingEntity causingEntity) {
         Entity directEntity = ev.getSource().getDirectEntity();
         if (entity == directEntity) {
            double gauntletDmg = causingEntity.getAttributeValue(ECAttributes.GAUNTLET_DMG_WITHOUT_WEAPON);
            double coldDmg = causingEntity.getAttributeValue(ECAttributes.COLD_DMG);
            double heatDmg = causingEntity.getAttributeValue(ECAttributes.HEAT_DMG);
            double voidDmg = causingEntity.getAttributeValue(ECAttributes.VOID_DMG);
            double soulDmg = causingEntity.getAttributeValue(ECAttributes.SOUL_DMG);
            if (coldDmg > 0.0 && !ev.getSource().is(DamageTypeTags.BYPASSES_COOLDOWN)) {
               ADDITIONAL_DMG_RUNS.add(() -> ev.getEntity().hurt(ECDamageInit.coldDmgAttack(level, entity), (float)coldDmg));
            }

            if (heatDmg > 0.0 && !ev.getSource().is(DamageTypeTags.BYPASSES_COOLDOWN)) {
               ADDITIONAL_DMG_RUNS.add(() -> ev.getEntity().hurt(ECDamageInit.heatDmgAttack(level, entity), (float)heatDmg));
            }

            if (voidDmg > 0.0 && !ev.getSource().is(DamageTypeTags.BYPASSES_COOLDOWN)) {
               ADDITIONAL_DMG_RUNS.add(() -> ev.getEntity().hurt(ECDamageInit.voidDmgAttack(level, entity), (float)voidDmg));
            }

            if (soulDmg > 0.0 && !ev.getSource().is(DamageTypeTags.BYPASSES_COOLDOWN)) {
               ADDITIONAL_DMG_RUNS.add(() -> ev.getEntity().hurt(ECDamageInit.soulDmgAttack(level, entity), (float)soulDmg));
            }

            if (!entityHoldingWeapon(causingEntity) && gauntletDmg > 0.0 && !ev.getSource().is(DamageTypeTags.BYPASSES_COOLDOWN)) {
               ADDITIONAL_DMG_RUNS.add(() -> ev.getEntity().hurt(ECDamageInit.gauntletAttack(level, entity), (float)gauntletDmg));
            }
         }
      }
   }

   public static boolean entityHoldingWeapon(LivingEntity entity) {
      List<AttributeModifier> mainHandAttackModifiers = new ArrayList<>();
      entity.getMainHandItem().getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (attributeHolder, attributeModifier) -> {
         if (attributeHolder == Attributes.ATTACK_DAMAGE) {
            mainHandAttackModifiers.add(attributeModifier);
         }
      });
      List<AttributeModifier> offHandAttackModifiers = new ArrayList<>();
      entity.getOffhandItem().getAttributeModifiers().forEach(EquipmentSlot.OFFHAND, (attributeHolder, attributeModifier) -> {
         if (attributeHolder == Attributes.ATTACK_DAMAGE) {
            offHandAttackModifiers.add(attributeModifier);
         }
      });

      for (AttributeModifier modifier : mainHandAttackModifiers) {
         if (modifier.amount() > 1.0) {
            return true;
         }
      }

      for (AttributeModifier modifierx : offHandAttackModifiers) {
         if (modifierx.amount() > 1.0) {
            return true;
         }
      }

      return false;
   }

   @SubscribeEvent
   public static void syncPlayerVariablesOnLogin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayer(
            serverPlayer,
            new PacketIntAttachment(serverPlayer.getId(), DataAttachments.ARROW_SLOT.get(), (Integer)serverPlayer.getData(DataAttachments.ARROW_SLOT)),
            new CustomPacketPayload[0]
         );
         PacketDistributor.sendToPlayer(
            serverPlayer,
            new PacketIntAttachment(serverPlayer.getId(), DataAttachments.STOLEN_HEALTH.get(), (Integer)serverPlayer.getData(DataAttachments.STOLEN_HEALTH)),
            new CustomPacketPayload[0]
         );
      }
   }

   @SubscribeEvent
   public static void onPlayerRespawnedSyncPlayerVariables(PlayerRespawnEvent event) {
      if (!event.getEntity().level().isClientSide()) {
         event.getEntity().setData(DataAttachments.STOLEN_HEALTH, Math.max((Integer)event.getEntity().getData(DataAttachments.STOLEN_HEALTH) - 1, 0));
      }
   }
}
