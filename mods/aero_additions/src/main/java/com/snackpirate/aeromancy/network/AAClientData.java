package com.snackpirate.aeromancy.network;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class AAClientData {
   private static final AeromancySpellData playerMagicData = new AeromancySpellData();
   private static final HashMap<Integer, AeromancySpellData> playerSyncedDataLookup = new HashMap<>();
   private static final AeromancySpellData emptySyncedData = new AeromancySpellData(-999);

   public static AeromancySpellData getAeroSpellData(LivingEntity livingEntity) {
      return livingEntity instanceof Player ? playerSyncedDataLookup.getOrDefault(livingEntity.getId(), emptySyncedData) : new AeromancySpellData(null);
   }

   public static void handlePlayerSyncedData(AeromancySpellData playerSyncedData) {
      playerSyncedDataLookup.put(playerSyncedData.getServerPlayerId(), playerSyncedData);
   }

   public static void handleAbstractCastingMobSyncedData(int entityId, AeromancySpellData syncedSpellData) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         if (level.getEntity(entityId) instanceof IMagicEntity var4) {
            ;
         }
      }
   }
}
