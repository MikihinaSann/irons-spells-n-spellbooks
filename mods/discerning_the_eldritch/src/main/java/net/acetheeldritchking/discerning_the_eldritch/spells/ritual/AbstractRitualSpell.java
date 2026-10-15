package net.acetheeldritchking.discerning_the_eldritch.spells.ritual;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastResult;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastResult.Type;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRitualSpell extends AbstractSpell {
   public boolean allowLooting() {
      return !this.isComplex();
   }

   public boolean canBeCraftedBy(Player player) {
      return false;
   }

   public boolean allowCrafting() {
      return false;
   }

   public boolean isComplex() {
      return true;
   }

   public boolean isSuperComplex() {
      return true;
   }

   public CastResult canBeCastedBy(int spellLevel, CastSource castSource, MagicData playerMagicData, Player player) {
      if ((castSource == CastSource.SPELLBOOK || castSource == CastSource.SWORD) && this.isComplex() && this.isSuperComplex()) {
         return new CastResult(
            Type.FAILURE,
            Component.translatable("ui.discerning_the_eldritch.ritual_cast_failure", new Object[]{this.getDisplayName(player)}).withStyle(ChatFormatting.RED)
         );
      } else {
         return castSource == CastSource.SPELLBOOK && this.isComplex() && !this.isSuperComplex()
            ? new CastResult(
               Type.FAILURE,
               Component.translatable("ui.discerning_the_eldritch.ritual_cast_failure", new Object[]{this.getDisplayName(player)})
                  .withStyle(ChatFormatting.RED)
            )
            : super.canBeCastedBy(spellLevel, castSource, playerMagicData, player);
      }
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }
}
