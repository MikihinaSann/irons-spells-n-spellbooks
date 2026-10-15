package net.acetheeldritchking.discerning_the_eldritch.effects;

import io.redspace.ironsspellbooks.effect.CustomDescriptionMobEffect;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTEServerConfig;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTETags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable.Result;

@EventBusSubscriber
public class AbracadabraPotionEffect extends CustomDescriptionMobEffect {
   public AbracadabraPotionEffect() {
      super(MobEffectCategory.BENEFICIAL, 15661670);
   }

   public Component getDescriptionLine(MobEffectInstance instance) {
      int amp = instance.getAmplifier() + 1;
      float damageCapAmount = getCapAmount(amp);
      return Component.translatable("tooltip.discerning_the_eldritch.abracadabra_description", new Object[]{(int)damageCapAmount})
         .withStyle(ChatFormatting.BLUE);
   }

   public static float getCapAmount(int level) {
      return (float)DTEServerConfig.abracadabraDamageCap / level;
   }

   public static float getDamageCapAmount(int level, float damage) {
      return Mth.clamp(damage, 0.0F, getCapAmount(level));
   }

   @SubscribeEvent
   public static void damageCapEvent(Pre event) {
      if (DTEServerConfig.enableDamageCap) {
         LivingEntity entity = event.getEntity();
         DamageSource source = event.getSource();
         MobEffectInstance effect = entity.getEffect(DTEPotionEffectRegistry.ABRACADABRA_EFFECT);
         if (effect != null && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            int level = effect.getAmplifier() + 1;
            float baseDamage = event.getOriginalDamage();
            float newDamage = getDamageCapAmount(level, baseDamage);
            event.setNewDamage(newDamage);
         }
      }
   }

   @SubscribeEvent
   public static void preventHexes(Applicable event) {
      if (DTEServerConfig.enableHexPrevention) {
         LivingEntity entity = event.getEntity();
         Holder<MobEffect> hexEffect = event.getEffectInstance().getEffect();
         MobEffectInstance effect = entity.getEffect(DTEPotionEffectRegistry.ABRACADABRA_EFFECT);
         if (effect != null && ((MobEffect)hexEffect.value()).getCategory() == MobEffectCategory.HARMFUL) {
            if (hexEffect.is(DTETags.BYPASS_ABRACADABRA)) {
               event.setResult(Result.APPLY);
            } else {
               event.setResult(Result.DO_NOT_APPLY);
            }
         }
      }
   }
}
