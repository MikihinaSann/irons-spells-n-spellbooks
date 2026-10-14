package com.github.L_Ender.lionfishapi.server.animation;

import com.github.L_Ender.lionfishapi.server.event.AnimationEvent;
import com.github.L_Ender.lionfishapi.server.network.AnimationMessage;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

public enum AnimationHandler {
   INSTANCE;

   public <T extends Entity & IAnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
      if (!entity.level().isClientSide) {
         AnimationMessage particlePacket = new AnimationMessage(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation));
         entity.setAnimation(animation);
         PacketDistributor.sendToPlayersTrackingEntity(entity, particlePacket, new CustomPacketPayload[0]);
      }
   }

   public <T extends Entity & IAnimatedEntity> void updateAnimations(T entity) {
      if (entity.getAnimation() == null) {
         entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
      } else if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
         if (entity.getAnimationTick() == 0) {
            AnimationEvent event = new AnimationEvent.Start(entity, entity.getAnimation());
            if (!((AnimationEvent.Start)NeoForge.EVENT_BUS.post(new AnimationEvent.Start(entity, entity.getAnimation()))).isCanceled()) {
               this.sendAnimationMessage(entity, event.getAnimation());
            }
         }

         if (entity.getAnimationTick() < entity.getAnimation().getDuration()) {
            entity.setAnimationTick(entity.getAnimationTick() + 1);
            NeoForge.EVENT_BUS.post(new AnimationEvent.Tick(entity, entity.getAnimation(), entity.getAnimationTick()));
         }

         if (entity.getAnimationTick() == entity.getAnimation().getDuration()) {
            if (!entity.getAnimation().doesLoop()) {
               entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
            }

            entity.setAnimationTick(0);
         }
      }
   }
}
