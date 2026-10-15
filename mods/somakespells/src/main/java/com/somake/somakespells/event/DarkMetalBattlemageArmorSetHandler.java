package com.somake.somakespells.event;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.registries.ModCompatItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public final class DarkMetalBattlemageArmorSetHandler {
   private static final float LOW_HEALTH_THRESHOLD = 6.0F;
   private static final int EFFECT_DURATION_TICKS = 100;
   private static final int HUNGER_AMPLIFIER = 2;
   private static final int REFRESH_THRESHOLD_TICKS = 20;
   private static final ResourceLocation RAMPANT_RAMPAGE_ID = ResourceLocation.parse("born_in_chaos_v1:rampant_rampage");

   private DarkMetalBattlemageArmorSetHandler() {
   }

   @SubscribeEvent
   public static void onEntityTick(Pre event) {
      if (event.getEntity() instanceof Player player && !player.level().isClientSide() && BornInChaosCompat.isLoaded()) {
         if (isWearingFullBattlemageSet(player)) {
            player.removeEffect(MobEffects.WITHER);
            if (!(player.getHealth() > 6.0F)) {
               refreshRampage(player);
               refreshHunger(player);
            }
         }
      }
   }

   private static boolean isWearingFullBattlemageSet(Player player) {
      return player.getItemBySlot(EquipmentSlot.HEAD).is(ModCompatItems.DARK_METAL_BATTLEMAGE_HELMET)
         && player.getItemBySlot(EquipmentSlot.CHEST).is(ModCompatItems.DARK_METAL_BATTLEMAGE_CHESTPLATE)
         && player.getItemBySlot(EquipmentSlot.LEGS).is(ModCompatItems.DARK_METAL_BATTLEMAGE_LEGGINGS)
         && player.getItemBySlot(EquipmentSlot.FEET).is(ModCompatItems.DARK_METAL_BATTLEMAGE_BOOTS);
   }

   private static void refreshRampage(Player player) {
      MobEffect rampage = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(RAMPANT_RAMPAGE_ID);
      if (rampage != null) {
         Holder<MobEffect> rampageHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(rampage);
         MobEffectInstance currentEffect = player.getEffect(rampageHolder);
         if (currentEffect == null || currentEffect.getDuration() <= 20) {
            player.addEffect(new MobEffectInstance(rampageHolder, 100, 0, false, false, true));
         }
      }
   }

   private static void refreshHunger(Player player) {
      MobEffectInstance currentEffect = player.getEffect(MobEffects.HUNGER);
      if (currentEffect == null || currentEffect.getAmplifier() < 2 || currentEffect.getDuration() <= 20) {
         player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2, false, false, true));
      }
   }
}
