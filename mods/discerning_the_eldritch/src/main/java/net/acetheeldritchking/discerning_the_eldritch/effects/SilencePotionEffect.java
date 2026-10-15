package net.acetheeldritchking.discerning_the_eldritch.effects;

import java.util.Set;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

public class SilencePotionEffect extends MobEffect {
   public SilencePotionEffect() {
      super(MobEffectCategory.HARMFUL, 3755355);
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
   }
}
