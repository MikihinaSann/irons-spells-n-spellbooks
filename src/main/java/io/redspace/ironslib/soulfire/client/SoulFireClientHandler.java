package io.redspace.ironslib.soulfire.client;

import io.redspace.ironslib.soulfire.SoulFireHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public final class SoulFireClientHandler {
   public static void onSoulFire(int entityId, boolean soul) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         Entity entity = minecraft.level.getEntity(entityId);
         if (entity != null) {
            SoulFireHooks.onSoulFirePacket(entity, soul);
         }
      }
   }
}
