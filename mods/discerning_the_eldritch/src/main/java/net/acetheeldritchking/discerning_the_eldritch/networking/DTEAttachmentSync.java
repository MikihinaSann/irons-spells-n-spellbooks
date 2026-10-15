package net.acetheeldritchking.discerning_the_eldritch.networking;

import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttachmentRegistry;
import net.minecraft.world.entity.LivingEntity;

public class DTEAttachmentSync {
   public static int getDevour(LivingEntity entity) {
      return (Integer)entity.getData(DTEAttachmentRegistry.DEVOURED_ENTITIES);
   }

   public static void setDevour(int val, LivingEntity entity) {
      entity.getData(DTEAttachmentRegistry.DEVOURED_ENTITIES);
      entity.setData(DTEAttachmentRegistry.DEVOURED_ENTITIES, (Integer)entity.getData(DTEAttachmentRegistry.DEVOURED_ENTITIES) + val);
   }

   public static void resetDevour(LivingEntity entity) {
      entity.getData(DTEAttachmentRegistry.DEVOURED_ENTITIES);
      entity.setData(DTEAttachmentRegistry.DEVOURED_ENTITIES, 0);
   }
}
