package com.somake.somakespells.event;

import com.somake.somakespells.capabilities.PlayerSpellMastery;
import com.somake.somakespells.registries.ModAttachments;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.CrimsonReflectionSpell;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class MagicBlockHandler {
   @SubscribeEvent
   public static void onSpellPreCast(SpellPreCastEvent event) {
      Player player = event.getEntity();
      if (!player.level().isClientSide && !player.isCreative()) {
         String spellId = event.getSpellId();
         int spellLevel = event.getSpellLevel();
         if (ModSpells.CRIMSON_REFLECTION == null || !spellId.equals(((CrimsonReflectionSpell)ModSpells.CRIMSON_REFLECTION.get()).getSpellId())) {
            PlayerSpellMastery mastery = (PlayerSpellMastery)player.getData(ModAttachments.PLAYER_SPELL_MASTERY);
            int unlockedLevel = mastery.getUnlockedLevel(spellId);
            if (spellLevel > unlockedLevel) {
               event.setCanceled(true);
               player.displayClientMessage(Component.translatable("ui.irons_spellbooks.cast_error_unlearned").withStyle(ChatFormatting.RED), true);
            }
         }
      }
   }
}
