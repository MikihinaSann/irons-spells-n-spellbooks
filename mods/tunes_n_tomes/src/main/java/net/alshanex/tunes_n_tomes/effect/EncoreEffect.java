package net.alshanex.tunes_n_tomes.effect;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.util.LastSpellTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EncoreEffect extends MagicMobEffect implements ISyncedMobEffect {
   public EncoreEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide && entity instanceof ServerPlayer player) {
         MagicData magicData = MagicData.getPlayerMagicData(player);
         if (!magicData.isCasting()) {
            LastSpellTracker.SpellRecord record = LastSpellTracker.get(player);
            if (record != null) {
               AbstractSpell spell = SpellRegistry.getSpell(record.spellId());
               if (spell != SpellRegistry.none()) {
                  magicData.getPlayerCooldowns().removeCooldown(record.spellId());
                  spell.attemptInitiateCast(player.getMainHandItem(), record.spellLevel(), player.level(), player, CastSource.MOB, false, "mainhand");
               }

               player.removeEffect(TEffectRegistry.ENCORE);
            }
         }

         return super.applyEffectTick(entity, amplifier);
      } else {
         return super.applyEffectTick(entity, amplifier);
      }
   }
}
