package com.chadate.spellelemental.event;

import com.chadate.spellelemental.config.ServerConfig;
import com.chadate.spellelemental.data.ElementContainerAttachment;
import com.chadate.spellelemental.data.SpellAttachments;
import com.chadate.spellelemental.element.attachment.attack.SpellIcdTracker;
import com.chadate.spellelemental.event.element.ElementDecaySystem;
import com.chadate.spellelemental.network.ElementData;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class SwordOilAttackHandler {
   private static final int MIN_ELEMENT_CONSUMPTION = 20;
   private static final int MAX_ELEMENT_CONSUMPTION = 200;
   private static final Map<Integer, Float> mainAttackDamageMap = new ConcurrentHashMap<>();
   private static final Map<Integer, Integer> sweepElementConsumptionMap = new ConcurrentHashMap<>();
   private static final Map<Integer, Integer> mainTargetMap = new ConcurrentHashMap<>();

   @SubscribeEvent
   public static void onPlayerAttack(AttackEntityEvent event) {
      Player player = event.getEntity();
      ItemStack weapon = player.getMainHandItem();
      if (!player.level().isClientSide()) {
         if (weapon.getItem() instanceof SwordItem) {
            if (event.getTarget() instanceof LivingEntity target) {
               CompoundTag customData = ((CustomData)weapon.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               CompoundTag elementData = customData.getCompound("ElementAttachment");
               if (elementData.contains("element") && elementData.contains("amount")) {
                  String elementType = elementData.getString("element");
                  int currentAmount = elementData.getInt("amount");
                  float baseDamage = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                  mainAttackDamageMap.put(player.getId(), baseDamage);
                  sweepElementConsumptionMap.put(player.getId(), 0);
                  mainTargetMap.put(player.getId(), target.getId());
                  float chargeRatio = player.getAttackStrengthScale(0.5F);
                  applyElementToTarget(target, elementType, player.getId(), chargeRatio);
                  int mainElementConsumption = Math.round(20.0F + 180.0F * chargeRatio);
                  mainElementConsumption = Math.max(20, Math.min(200, mainElementConsumption));
                  scheduleElementConsumption(player, weapon, currentAmount, mainElementConsumption);
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onLivingDamage(Pre event) {
      if (!event.getEntity().level().isClientSide()) {
         if (event.getSource().getEntity() instanceof Player attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (weapon.getItem() instanceof SwordItem) {
               CompoundTag customData = ((CustomData)weapon.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               CompoundTag elementData = customData.getCompound("ElementAttachment");
               if (elementData.contains("element") && elementData.contains("amount")) {
                  String elementType = elementData.getString("element");
                  Integer mainTargetId = mainTargetMap.get(attacker.getId());
                  if (mainTargetId == null || !mainTargetId.equals(event.getEntity().getId())) {
                     float chargeRatio = attacker.getAttackStrengthScale(0.5F);
                     float currentDamage = event.getNewDamage();
                     Float mainDamage = mainAttackDamageMap.get(attacker.getId());
                     float damageRatio = 1.0F;
                     if (mainDamage != null && mainDamage > 0.0F) {
                        damageRatio = Math.min(1.0F, currentDamage / mainDamage);
                        damageRatio = Math.max(0.1F, damageRatio);
                     }

                     int consumedAmount = applyElementToTargetWithRatio(event.getEntity(), elementType, attacker.getId(), chargeRatio, damageRatio);
                     sweepElementConsumptionMap.merge(attacker.getId(), consumedAmount, Integer::sum);
                  }
               }
            }
         }
      }
   }

   private static int applyElementToTargetWithRatio(LivingEntity target, String elementType, int attackerId, float chargeRatio, float damageRatio) {
      LivingEntity attacker = null;
      if (target.level() instanceof ServerLevel serverLevel && serverLevel.getEntity(attackerId) instanceof LivingEntity livingAttacker) {
         attacker = livingAttacker;
      }

      ResourceLocation swordOilSweepSpellId = ResourceLocation.fromNamespaceAndPath("spellelemental", "sword_oil_sweep_" + elementType.toLowerCase());
      int step = ServerConfig.getIcdHitStep();
      int timeTicks = ServerConfig.getIcdTimeTicks();
      long currentTick = target.level().getGameTime();
      boolean allowAttachment = SpellIcdTracker.allowAndRecord(attacker, target, swordOilSweepSpellId, currentTick, step, timeTicks);
      if (!allowAttachment) {
         return 0;
      } else {
         int baseElementAmount = Math.round(20.0F + 180.0F * chargeRatio);
         baseElementAmount = Math.max(20, Math.min(200, baseElementAmount));
         int elementAmount = Math.round(baseElementAmount * damageRatio);
         int minAmount = Math.max(2, baseElementAmount / 10);
         elementAmount = Math.max(minAmount, elementAmount);
         ElementContainerAttachment container = (ElementContainerAttachment)target.getData(SpellAttachments.ELEMENTS_CONTAINER);
         container.setValue(elementType.toLowerCase(), elementAmount);
         long gameTime = target.level().getGameTime();
         container.markAppliedWithAttacker(elementType.toLowerCase(), gameTime, attackerId);
         ElementDecaySystem.track(target);
         if (target.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
               double distance = player.distanceTo(target);
               if (distance <= 64.0) {
                  PacketDistributor.sendToPlayer(player, new ElementData(target.getId(), elementType.toLowerCase(), elementAmount), new CustomPacketPayload[0]);
               }
            }
         }

         return elementAmount;
      }
   }

   private static void applyElementToTarget(LivingEntity target, String elementType, int attackerId, float chargeRatio) {
      LivingEntity attacker = null;
      if (target.level() instanceof ServerLevel serverLevel && serverLevel.getEntity(attackerId) instanceof LivingEntity livingAttacker) {
         attacker = livingAttacker;
      }

      ResourceLocation swordOilSpellId = ResourceLocation.fromNamespaceAndPath("spellelemental", "sword_oil_" + elementType.toLowerCase());
      int step = ServerConfig.getIcdHitStep();
      int timeTicks = ServerConfig.getIcdTimeTicks();
      long currentTick = target.level().getGameTime();
      boolean allowAttachment = SpellIcdTracker.allowAndRecord(attacker, target, swordOilSpellId, currentTick, step, timeTicks);
      if (allowAttachment) {
         int elementAmount = Math.round(20.0F + 180.0F * chargeRatio);
         elementAmount = Math.max(20, Math.min(200, elementAmount));
         ElementContainerAttachment container = (ElementContainerAttachment)target.getData(SpellAttachments.ELEMENTS_CONTAINER);
         container.setValue(elementType.toLowerCase(), elementAmount);
         long gameTime = target.level().getGameTime();
         container.markAppliedWithAttacker(elementType.toLowerCase(), gameTime, attackerId);
         ElementDecaySystem.track(target);
         if (target.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
               double distance = player.distanceTo(target);
               if (distance <= 64.0) {
                  PacketDistributor.sendToPlayer(player, new ElementData(target.getId(), elementType.toLowerCase(), elementAmount), new CustomPacketPayload[0]);
               }
            }
         }
      }
   }

   private static void scheduleElementConsumption(Player player, ItemStack weapon, int currentAmount, int mainElementConsumption) {
      if (player.level() instanceof ServerLevel serverLevel) {
         serverLevel.getServer().execute(() -> {
            Integer sweepConsumption = sweepElementConsumptionMap.remove(player.getId());
            if (sweepConsumption == null) {
               sweepConsumption = 0;
            }

            int totalConsumption = mainElementConsumption + sweepConsumption;
            updateWeaponElementAmount(weapon, currentAmount, totalConsumption);
            mainAttackDamageMap.remove(player.getId());
            mainTargetMap.remove(player.getId());
         });
      }
   }

   private static void updateWeaponElementAmount(ItemStack weapon, int currentAmount, int totalConsumption) {
      CompoundTag customData = ((CustomData)weapon.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      CompoundTag elementData = customData.getCompound("ElementAttachment");
      int newAmount = Math.max(0, currentAmount - totalConsumption);
      if (newAmount == 0) {
         customData.remove("ElementAttachment");
      } else {
         elementData.putInt("amount", newAmount);
         customData.put("ElementAttachment", elementData);
      }

      weapon.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
   }
}
