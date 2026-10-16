package net.kayn.better_spellcasting.event;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.kayn.better_spellcasting.BetterSpellcasting;
import net.kayn.better_spellcasting.combat.SpellAttackData;
import net.kayn.better_spellcasting.compat.SpellDataHolder;
import net.kayn.better_spellcasting.network.SwingMessage;
import net.kayn.better_spellcasting.util.SpellCastHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "better_spellcasting"
)
public class AttackSpellHandler {
   private static final long COMBO_TIMEOUT_TICKS = 40L;
   private static final ConcurrentHashMap<UUID, AttackSpellHandler.ComboState> serverCombo = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<UUID, AttackSpellHandler.PendingSpell> pending = new ConcurrentHashMap<>();

   @SubscribeEvent
   public static void onLeftClickEmpty(LeftClickEmpty event) {
      Player p = event.getEntity();
      if (p.level().isClientSide()) {
         try {
            PacketDistributor.sendToServer(new SwingMessage(), new CustomPacketPayload[0]);
         } catch (Throwable var3) {
         }
      }
   }

   @SubscribeEvent
   public static void onClientAttackEntity(AttackEntityEvent event) {
      Player p = event.getEntity();
      if (p.level().isClientSide()) {
         try {
            PacketDistributor.sendToServer(new SwingMessage(), new CustomPacketPayload[0]);
         } catch (Throwable var3) {
         }
      }
   }

   public static void onClientSwingFromPacket(ServerPlayer serverPlayer) {
      if (serverPlayer != null) {
         trackSwingServerSide(serverPlayer, null);
      }
   }

   @SubscribeEvent
   public static void onServerAttackEntity(AttackEntityEvent event) {
      Player p = event.getEntity();
      if (!p.level().isClientSide()) {
         if (p instanceof ServerPlayer serverPlayer) {
            LivingEntity attacked = null;
            if (event.getTarget() instanceof LivingEntity le) {
               attacked = le;
            }

            trackSwingServerSide(serverPlayer, attacked);
         }
      }
   }

   private static void trackSwingServerSide(ServerPlayer player, LivingEntity attackedEntity) {
      try {
         ItemStack handStack = player.getMainHandItem();
         Item item = handStack.getItem();
         ResourceLocation weaponId = BuiltInRegistries.ITEM.getKey(item);
         if (weaponId == null) {
            serverCombo.remove(player.getUUID());
            pending.remove(player.getUUID());
            return;
         }

         WeaponAttributes attributes = WeaponRegistry.getAttributes(player.getMainHandItem());
         if (attributes == null || attributes.attacks() == null || attributes.attacks().length == 0) {
            serverCombo.remove(player.getUUID());
            pending.remove(player.getUUID());
            return;
         }

         int max = attributes.attacks().length;
         long now = player.level().getGameTime();
         AttackSpellHandler.ComboState state = serverCombo.get(player.getUUID());
         if (state != null && state.lastSwingTick == now && weaponId.equals(state.weaponId)) {
            return;
         }

         int nextIndex = state != null && weaponId.equals(state.weaponId) && now - state.lastSwingTick <= 40L ? (state.index + 1) % max : 0;
         serverCombo.put(player.getUUID(), new AttackSpellHandler.ComboState(nextIndex, now, weaponId));
         SpellAttackData data = SpellDataHolder.getSpellData(weaponId, nextIndex);
         if (data == null) {
            pending.remove(player.getUUID());
            return;
         }

         if (data.getTrigger() == SpellAttackData.Trigger.ON_ATTACK) {
            if (data.isSelfCast()) {
               SpellCastHelper.castSpellFromAttack(player, data.getSpell(), data.getLevel(), player);
            } else {
               SpellCastHelper.castSpellFromAttack(player, data.getSpell(), data.getLevel(), attackedEntity);
            }

            pending.remove(player.getUUID());
         } else {
            pending.put(player.getUUID(), new AttackSpellHandler.PendingSpell(data, nextIndex));
         }
      } catch (Throwable var12) {
         BetterSpellcasting.LOGGER.error("Error tracking swing on server", var12);
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(Pre event) {
      if (!event.getEntity().level().isClientSide()) {
         if (event.getSource().getEntity() instanceof Player player) {
            UUID var7 = player.getUUID();
            AttackSpellHandler.PendingSpell ps = pending.remove(var7);
            if (ps != null) {
               LivingEntity victim = event.getEntity();
               if (victim != null) {
                  try {
                     SpellAttackData data = ps.data();
                     if (data == null) {
                        return;
                     }

                     if (data.isSelfCast()) {
                        SpellCastHelper.castSpellFromAttack(player, data.getSpell(), data.getLevel(), player);
                     } else {
                        SpellCastHelper.castSpellFromAttack(player, data.getSpell(), data.getLevel(), victim);
                     }
                  } catch (Throwable var6) {
                  }
               }
            }
         }
      }
   }

   public static void resetComboFor(Player player) {
      if (player != null) {
         serverCombo.remove(player.getUUID());
         pending.remove(player.getUUID());
      }
   }

   private record ComboState(int index, long lastSwingTick, ResourceLocation weaponId) {
   }

   private record PendingSpell(SpellAttackData data, int attackIndex) {
   }
}
