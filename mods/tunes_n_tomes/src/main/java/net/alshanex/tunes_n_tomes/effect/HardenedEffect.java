package net.alshanex.tunes_n_tomes.effect;

import io.redspace.ironsspellbooks.effect.CustomDescriptionMobEffect;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class HardenedEffect extends CustomDescriptionMobEffect {
   public static final float REDUCTION_PER_LEVEL = 0.05F;
   public static final float BASE_REDUCTION = 0.5F;

   public HardenedEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public void onEffectStarted(LivingEntity pLivingEntity, int pAmplifier) {
      super.onEffectStarted(pLivingEntity, pAmplifier);
      pLivingEntity.setAbsorptionAmount(pLivingEntity.getAbsorptionAmount() + (pAmplifier + 1));
   }

   public Component getDescriptionLine(MobEffectInstance instance) {
      int amp = instance.getAmplifier() + 1;
      float reductionAmount = getReductionAmount(amp);
      return Component.translatable("tooltip.irons_spellbooks.oakskin_description", new Object[]{(int)(reductionAmount * 100.0F)})
         .withStyle(ChatFormatting.BLUE);
   }

   @SubscribeEvent
   public static void reduceDamage(LivingIncomingDamageEvent event) {
      LivingEntity entity = event.getEntity();
      MobEffectInstance effect = entity.getEffect(TEffectRegistry.HARDENED);
      if (effect != null) {
         int lvl = effect.getAmplifier() + 1;
         float before = event.getAmount();
         float multiplier = 1.0F - getReductionAmount(lvl);
         event.setAmount(event.getAmount() * multiplier);
      }
   }

   public static float getReductionAmount(int level) {
      return 0.5F + 0.05F * level;
   }
}
