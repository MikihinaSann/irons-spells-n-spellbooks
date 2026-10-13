package com.grandsigil.registry;

import com.grandsigil.effect.BoundEffect;
import com.grandsigil.effect.TransfixedEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GsEffects {
   private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "grandsigil");
   public static final DeferredHolder<MobEffect, TransfixedEffect> TRANSFIXED = EFFECTS.register("transfixed", TransfixedEffect::new);
   public static final DeferredHolder<MobEffect, BoundEffect> BOUND = EFFECTS.register("bound", BoundEffect::new);

   private GsEffects() {
   }

   public static void register(IEventBus modBus) {
      EFFECTS.register(modBus);
   }

   public static boolean transfixed(LivingEntity target) {
      return target.hasEffect(TRANSFIXED);
   }

   public static boolean bound(LivingEntity target) {
      return target.hasEffect(BOUND);
   }

   public static boolean pinned(LivingEntity target) {
      return transfixed(target) || bound(target);
   }

   public static int held(LivingEntity target) {
      MobEffectInstance live = target.getEffect(TRANSFIXED);
      return live == null ? 0 : live.getDuration();
   }

   public static boolean hold(ServerLevel level, LivingEntity target, int ticks) {
      return apply(level, target, TRANSFIXED, ticks);
   }

   public static boolean bind(ServerLevel level, LivingEntity target, int ticks) {
      return apply(level, target, BOUND, ticks);
   }

   private static boolean apply(ServerLevel level, LivingEntity target, Holder<MobEffect> effect, int ticks) {
      boolean fresh = !target.hasEffect(effect);
      target.addEffect(new MobEffectInstance(effect, ticks, 0, false, false, true));
      MobEffectInstance live = target.getEffect(effect);
      if (live == null) {
         return false;
      } else {
         level.getChunkSource().broadcastAndSend(target, new ClientboundUpdateMobEffectPacket(target.getId(), live, false));
         return fresh;
      }
   }
}
