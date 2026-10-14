package com.gametechbc.traveloptics.api.entity.mobs;

import io.redspace.ironsspellbooks.effect.SummonTimer;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.neoforged.neoforge.registries.DeferredHolder;

public interface MagicForlornSummon extends IMagicSummon {
   default void onRemovedHelper(Entity entity, DeferredHolder<MobEffect, SummonTimer> holder) {
      RemovalReason reason = entity.getRemovalReason();
      if (reason != null && this.getSummoner() instanceof ServerPlayer player && reason.shouldDestroy()) {
         MobEffectInstance effect = player.getEffect(holder);
         if (effect != null) {
            MobEffectInstance decrement = new MobEffectInstance(holder, effect.getDuration(), effect.getAmplifier() - 1, false, false, true);
            if (decrement.getAmplifier() >= 0) {
               player.getActiveEffectsMap().put(holder, decrement);
               player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), decrement, false));
            } else {
               player.removeEffect(holder);
            }
         }

         if (reason.equals(RemovalReason.DISCARDED)) {
            player.sendSystemMessage(Component.translatable("ui.traveloptics.forlorn_summon_despawn_message", new Object[]{((Entity)this).getDisplayName()}));
         }
      }
   }
}
