package net.acetheeldritchking.discerning_the_eldritch.effects;

import net.acetheeldritchking.aces_spell_utils.network.RemoveShaderEffectPacket;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class PortentEffect extends MobEffect {
   public PortentEffect() {
      super(MobEffectCategory.NEUTRAL, 1710620);
   }

   @SubscribeEvent
   public static void removeShader(Remove event) {
      LivingEntity entity = event.getEntity();
      Holder<MobEffect> effect = event.getEffect();
      if (effect instanceof PortentEffect && entity.hasEffect(effect) && entity instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayer(serverPlayer, new RemoveShaderEffectPacket(), new CustomPacketPayload[0]);
      }
   }

   @SubscribeEvent
   public static void removeShader(Expired event) {
      LivingEntity entity = event.getEntity();
      if (entity.hasEffect(DTEPotionEffectRegistry.PORTENT_EFFECT) && entity instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayer(serverPlayer, new RemoveShaderEffectPacket(), new CustomPacketPayload[0]);
      }
   }
}
