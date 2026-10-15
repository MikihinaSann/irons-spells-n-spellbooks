package com.somake.somakespells.event;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.util.FireElementContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class HeldElementalFireEffectHandler {
   public static final String HELD_FIRE_SOURCE_TAG = "somakespells_held_fire_source";
   public static final String HELD_FIRE_BONUS_UNTIL_TAG = "somakespells_held_fire_bonus_until";
   public static final int SOURCE_NONE = 0;
   public static final int SOURCE_FIRE = 1;
   public static final int SOURCE_SOUL_FIRE = 2;
   public static final int SOURCE_INFERNAL_FIRE = 3;
   private static final int REFRESH_INTERVAL = 10;
   private static final int EFFECT_DURATION = 30;

   @SubscribeEvent
   public static void onEntityTick(Pre event) {
      if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
         if (player.tickCount % 10 == 0) {
            HeldElementalFireEffectHandler.HeldEffectData heldEffect = getHeldElementalEffect(player);
            if (heldEffect == null) {
               clearHeldFireBonusTags(player);
            } else {
               long gameTime = player.level().getGameTime();
               player.getPersistentData().putInt("somakespells_held_fire_source", heldEffect.source());
               player.getPersistentData().putLong("somakespells_held_fire_bonus_until", gameTime + 10L + 2L);
               Holder<MobEffect> effectHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(heldEffect.effect());
               player.addEffect(new MobEffectInstance(effectHolder, 30, 0, true, true, true));
            }
         }
      }
   }

   private static HeldElementalFireEffectHandler.HeldEffectData getHeldElementalEffect(Player player) {
      FireElementContext context = FireElementContext.resolve(player);
      if (!context.fromHeldItem()) {
         return null;
      } else {
         return switch (context.mode()) {
            case SOUL_FIRE -> new HeldElementalFireEffectHandler.HeldEffectData(2, (MobEffect)ModMobEffects.SOUL_FIRE.get());
            case INFERNAL_FIRE -> BornInChaosCompat.isLoaded()
               ? new HeldElementalFireEffectHandler.HeldEffectData(3, (MobEffect)ModMobEffects.INFERNAL_FIRE.get())
               : null;
            case NORMAL -> new HeldElementalFireEffectHandler.HeldEffectData(1, (MobEffect)ModMobEffects.BURNING.get());
         };
      }
   }

   private static void clearHeldFireBonusTags(Player player) {
      player.getPersistentData().remove("somakespells_held_fire_source");
      player.getPersistentData().remove("somakespells_held_fire_bonus_until");
   }

   private record HeldEffectData(int source, MobEffect effect) {
   }
}
