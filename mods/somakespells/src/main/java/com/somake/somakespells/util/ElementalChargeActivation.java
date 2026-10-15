package com.somake.somakespells.util;

import com.somake.somakespells.entity.ElementalChargeBeamEntity;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class ElementalChargeActivation {
   public static final int FIRE = 12546335;
   public static final int LIGHTNING = 4631295;
   public static final int SOUL_FIRE = 2462666;
   public static final int INFERNAL_FIRE = 3524643;
   public static final int GEO = 6701865;
   public static final int AQUA = 2331812;
   public static final int NATURE = 5029663;
   public static final int ICE = 8101293;
   public static final int ENDER = 6365119;
   public static final int BLOOD = 12525356;
   public static final int HOLY = 12558623;
   public static final int SPIRIT = 6002608;
   public static final int SYMMETRY = 4891252;
   public static final int EVOCATION = 9870500;
   public static final int SOUND = 10984042;
   public static final int ELDRITCH = 3242107;

   private ElementalChargeActivation() {
   }

   public static void activate(Level level, AbstractSpell spell, int spellLevel, LivingEntity target, Holder<MobEffect> effect, int color) {
      int duration = (int)(spell.getSpellPower(spellLevel, target) * 20.0F);
      int amplifier = spellLevel - 1;
      activate(level, target, effect, duration, amplifier, color);
   }

   public static boolean activate(Level level, LivingEntity target, MobEffectInstance effect, int color) {
      return activate(level, target, effect.getEffect(), effect.getDuration(), effect.getAmplifier(), color);
   }

   public static boolean activate(Level level, LivingEntity target, Holder<MobEffect> effect, int duration, int amplifier, int color) {
      if (level instanceof ServerLevel serverLevel) {
         ElementalChargeBeamEntity beam = new ElementalChargeBeamEntity(serverLevel, target, effect, duration, amplifier, color);
         if (serverLevel.addFreshEntity(beam)) {
            serverLevel.playSound(null, target.blockPosition(), (SoundEvent)SoundRegistry.SUNBEAM_WINDUP.get(), SoundSource.PLAYERS, 1.2F, 1.15F);
            return true;
         } else {
            return target.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
         }
      } else {
         return true;
      }
   }
}
