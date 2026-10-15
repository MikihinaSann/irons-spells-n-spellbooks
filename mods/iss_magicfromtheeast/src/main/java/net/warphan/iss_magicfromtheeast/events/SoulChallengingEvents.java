package net.warphan.iss_magicfromtheeast.events;

import io.redspace.ironsspellbooks.api.events.CounterSpellEvent;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;
import net.warphan.iss_magicfromtheeast.configs.MFTEServerConfigs;
import net.warphan.iss_magicfromtheeast.damage.MFTEDamageTypes;
import net.warphan.iss_magicfromtheeast.datagen.MFTEDamageTypeTagGenerator;
import net.warphan.iss_magicfromtheeast.entity.spells.spirit_challenging.ExtractedSoul;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;

@EventBusSubscriber
public class SoulChallengingEvents {
   @SubscribeEvent
   public static void linkedSoulChallengingEvent(Pre event) {
      if (event.getEntity() instanceof ExtractedSoul challengedSoul) {
         LivingEntity soulOwner = challengedSoul.getOwner();
         float linkingRange = 12.0F;
         if (!challengedSoul.level.isClientSide && soulOwner != null && challengedSoul.hasRadius()) {
            float distance = challengedSoul.distanceTo(soulOwner);
            if (distance > linkingRange) {
               challengedSoul.onUnSummon();
               punishingOwner(soulOwner);
               event.setCanceled(true);
            } else if (soulOwner.isDeadOrDying()) {
               challengedSoul.onUnSummon();
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void soulOwnerHurtEvent(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre event) {
      LivingEntity entity = event.getEntity();
      DamageSource source = event.getSource();
      Entity attacker = event.getSource().getEntity();
      if (entity instanceof ExtractedSoul extractedSoul) {
         LivingEntity soulOwner = extractedSoul.getOwner();
         float damageOnSoul = event.getOriginalDamage();
         float damageAmount = damageOnSoul * extractedSoul.bonusPercent;
         float damageAmountOver = extractedSoul.getHealth() * extractedSoul.bonusPercent;
         if (soulOwner != null) {
            if (source.is(MFTEDamageTypeTagGenerator.SOUL_HURTING)) {
               soulOwner.hurt(soulOwner.damageSources().source(MFTEDamageTypes.SOUL_DAMAGE, attacker), damageOnSoul);
            } else if (damageOnSoul <= extractedSoul.getHealth()) {
               soulOwner.hurt(soulOwner.damageSources().source(MFTEDamageTypes.SOUL_DAMAGE, attacker), damageAmount);
            } else if (damageOnSoul > extractedSoul.getHealth()) {
               soulOwner.hurt(soulOwner.damageSources().source(MFTEDamageTypes.SOUL_DAMAGE, attacker), damageAmountOver);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onSoulCrushedEvent(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level.isClientSide && entity instanceof ExtractedSoul challengedSoul) {
         LivingEntity soulOwner = challengedSoul.getOwner();
         if (soulOwner != null) {
            punishingOwner(soulOwner);
         }
      }
   }

   @SubscribeEvent
   public static void counterSpellCastOnSoul(CounterSpellEvent event) {
      Entity entity = event.target;
      Entity caster = event.caster;
      if (entity instanceof ExtractedSoul challengedSoul) {
         LivingEntity soulOwner = challengedSoul.getOwner();
         if (caster != challengedSoul.getExtractor() && !(Boolean)MFTEServerConfigs.PASS_CHALLENGING.get()) {
            punishingOwner(soulOwner);
         } else {
            challengedSoul.onUnSummon();
         }
      }
   }

   public static void punishingOwner(LivingEntity livingEntity) {
      livingEntity.addEffect(new MobEffectInstance(MFTEEffectRegistries.SOULBURN, 200, 0));
      livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.SLOWED, 200, 3));
   }
}
