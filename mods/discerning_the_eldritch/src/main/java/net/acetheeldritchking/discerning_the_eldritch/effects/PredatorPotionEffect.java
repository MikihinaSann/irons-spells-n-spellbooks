package net.acetheeldritchking.discerning_the_eldritch.effects;

import net.acetheeldritchking.discerning_the_eldritch.entity.spells.ravenous_jaw.RavenousJawEntity;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

@EventBusSubscriber
public class PredatorPotionEffect extends MobEffect {
   public PredatorPotionEffect() {
      super(MobEffectCategory.BENEFICIAL, 11749198);
   }

   @SubscribeEvent
   public static void summonJawsEvent(Pre event) {
      LivingEntity entity = event.getEntity();
      DamageSource source = event.getSource();
      if (event.getSource().getDirectEntity() instanceof LivingEntity livingEntity) {
         if (livingEntity.hasEffect(DTEPotionEffectRegistry.PREDATOR_POTION_EFFECT)) {
            RavenousJawEntity jaw = new RavenousJawEntity(livingEntity.level(), livingEntity, entity);
            jaw.setPos(entity.position());
            jaw.setYRot(livingEntity.getYRot());
            jaw.setDamage(20.0F);
            livingEntity.level().addFreshEntity(jaw);
         } else if (livingEntity.hasEffect(DTEPotionEffectRegistry.PREDATOR_POTION_EFFECT) && entity.hasEffect(DTEPotionEffectRegistry.PREY_POTION_EFFECT)) {
            RavenousJawEntity jaw = new RavenousJawEntity(livingEntity.level(), livingEntity, entity);
            jaw.setPos(entity.position());
            jaw.setYRot(livingEntity.getYRot());
            jaw.setDamage(25.0F);
         }
      }
   }
}
