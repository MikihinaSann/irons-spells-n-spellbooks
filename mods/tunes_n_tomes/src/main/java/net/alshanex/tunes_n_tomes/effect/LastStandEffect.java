package net.alshanex.tunes_n_tomes.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class LastStandEffect extends MagicMobEffect {
   public LastStandEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @SubscribeEvent
   public static void reduceDamage(LivingIncomingDamageEvent event) {
      LivingEntity entity = event.getEntity();
      MobEffectInstance effect = entity.getEffect(TEffectRegistry.LAST_STAND);
      if (effect != null) {
         event.setAmount(0.0F);
      }
   }
}
