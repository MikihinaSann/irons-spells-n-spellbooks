package com.somake.somakespells.event;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.RedEchoEntity;
import com.somake.somakespells.item.RedSoulLanternProgressHelper;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public final class SpiritRitualProgressionHandler {
   private SpiritRitualProgressionHandler() {
   }

   @SubscribeEvent
   public static void onSpellCast(SpellOnCastEvent event) {
      if (LegendaryMonstersCompat.isLoaded()
         && event.getEntity() instanceof ServerPlayer player
         && event.getSchoolType() != null
         && MagicFromTheEastCompat.spiritSchoolOrEvocation().equals(event.getSchoolType().getId())
         && event.getCastSource() != CastSource.COMMAND
         && isLegendarySpiritSpell(event.getSpellId())) {
         ResourceLocation spellId = ResourceLocation.tryParse(event.getSpellId());
         if (spellId != null) {
            RedSoulLanternProgressHelper.recordSpiritSpell(player, spellId);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
      if (LegendaryMonstersCompat.isLoaded() && event.getEntity() instanceof ServerPlayer player && RedEchoEntity.hasActiveDuelFor(player)) {
         RedEchoEntity.cancelDuel(player, false);
      }
   }

   @SubscribeEvent
   public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      if (LegendaryMonstersCompat.isLoaded() && event.getEntity() instanceof ServerPlayer player && RedEchoEntity.hasActiveDuelFor(player)) {
         RedEchoEntity.cancelDuel(player, false);
      }
   }

   private static boolean isLegendarySpiritSpell(String spellId) {
      return matches(ModSpells.SPIRIT_EMPOWERMENT, spellId)
         || matches(ModSpells.PROCESSION_OF_SOULS, spellId)
         || matches(ModSpells.GRAVE_SIGIL, spellId)
         || matches(ModSpells.SOUL_BASTION, spellId)
         || matches(ModSpells.SOUL_LATCH, spellId)
         || matches(ModSpells.SPIRAL_OF_RUIN, spellId)
         || matches(ModSpells.SOULFALL_JUDGMENT, spellId)
         || matches(ModSpells.SOUL_REPRISAL, spellId)
         || matches(ModSpells.SPECTRAL_RONDO, spellId)
         || matches(ModSpells.WINGED_RUIN, spellId);
   }

   private static boolean matches(DeferredHolder<? extends AbstractSpell, ?> spell, String spellId) {
      return spell != null && ((AbstractSpell)spell.get()).getSpellId().equals(spellId);
   }
}
