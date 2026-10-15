package com.somake.somakespells.event;

import com.somake.somakespells.capabilities.PlayerSpellMastery;
import com.somake.somakespells.command.RedSoulCommand;
import com.somake.somakespells.command.SpellCommand;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.config.ArmorBalanceConfig;
import com.somake.somakespells.config.SpellbookBalanceConfig;
import com.somake.somakespells.config.StaffBalanceConfig;
import com.somake.somakespells.config.WeaponBalanceConfig;
import com.somake.somakespells.network.SyncSpellMasteryPacket;
import com.somake.somakespells.network.SyncWeaponBalancePacket;
import com.somake.somakespells.registries.ModAttachments;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class ModEvents {
   @SubscribeEvent
   public static void onRegisterCommands(RegisterCommandsEvent event) {
      SpellCommand.register(event.getDispatcher());
      if (LegendaryMonstersCompat.isLoaded()) {
         RedSoulCommand.register(event.getDispatcher());
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         PlayerSpellMastery mastery = (PlayerSpellMastery)player.getData(ModAttachments.PLAYER_SPELL_MASTERY);
         PacketDistributor.sendToPlayer(player, new SyncSpellMasteryPacket(mastery.getAllUnlockedSpells()), new CustomPacketPayload[0]);
         PacketDistributor.sendToPlayer(
            player,
            new SyncWeaponBalancePacket(
               WeaponBalanceConfig.getLocalWeaponOverrideSnapshot(),
               ArmorBalanceConfig.getLocalArmorOverrideSnapshot(),
               ArmorBalanceConfig.getLocalCustomArmorPieceSnapshot(),
               SpellbookBalanceConfig.getLocalSpellbookOverrideSnapshot(),
               StaffBalanceConfig.getLocalStaffOverrideSnapshot()
            ),
            new CustomPacketPayload[0]
         );
      }
   }

   @SubscribeEvent
   public static void onDatapackSync(OnDatapackSyncEvent event) {
      SyncWeaponBalancePacket packet = new SyncWeaponBalancePacket(
         WeaponBalanceConfig.getLocalWeaponOverrideSnapshot(),
         ArmorBalanceConfig.getLocalArmorOverrideSnapshot(),
         ArmorBalanceConfig.getLocalCustomArmorPieceSnapshot(),
         SpellbookBalanceConfig.getLocalSpellbookOverrideSnapshot(),
         StaffBalanceConfig.getLocalStaffOverrideSnapshot()
      );
      event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, packet, new CustomPacketPayload[0]));
   }

   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
   }
}
