package io.redspace.ironslib.attribute;

import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.util.Utils;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(
   modid = "irons_lib"
)
public final class AttributeEventsHandler {
   @SubscribeEvent
   public static void handleDamageAttributes(LivingIncomingDamageEvent event) {
      LivingEntity target = event.getEntity();
      if (!target.level().isClientSide()) {
         if (target.getRandom().nextDouble() < target.getAttributeValue(IronsLibRegistries.AttributeRegistry.DODGE_CHANCE) - 1.0) {
            event.setCanceled(true);
            target.level().playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, target.getSoundSource(), 1.5F, 2.0F);
            Utils.spawnParticles(
               target.level(),
               ParticleTypes.CAMPFIRE_COSY_SMOKE,
               target.getX(),
               target.getY() + (double)(target.getBbHeight() * 0.5F),
               target.getZ(),
               15,
               0.2,
               0.4,
               0.2,
               0.01,
               false
            );
            target.invulnerableTime = 20;
         } else if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            float var6 = event.getAmount();
            if (event.getSource().getDirectEntity() instanceof AbstractArrow) {
               double arrowDamage = attacker.getAttributeValue(IronsLibRegistries.AttributeRegistry.ARROW_DAMAGE);
               if (arrowDamage != 1.0) {
                  var6 *= (float)arrowDamage;
               }
            }

            if (event.getSource().getDirectEntity() instanceof Projectile) {
               double projectileDamage = attacker.getAttributeValue(IronsLibRegistries.AttributeRegistry.PROJECTILE_DAMAGE);
               if (projectileDamage != 1.0) {
                  var6 *= (float)projectileDamage;
               }
            }

            event.setAmount(var6);
         }
      }
   }

   @SubscribeEvent
   public static void handleCritMultiplier(CriticalHitEvent event) {
      if (!event.getEntity().level().isClientSide()) {
         double critDamage = event.getEntity().getAttributeValue(IronsLibRegistries.AttributeRegistry.CRIT_DAMAGE);
         if (critDamage != 1.0) {
            event.setDamageMultiplier((float)((double)event.getDamageMultiplier() + critDamage - 1.0));
         }
      }
   }

   @SubscribeEvent
   public static void handleBreakSpeed(BreakSpeed event) {
      double miningSpeed = event.getEntity().getAttributeValue(IronsLibRegistries.AttributeRegistry.MINING_SPEED);
      if (miningSpeed != 1.0) {
         event.setNewSpeed((float)((double)event.getNewSpeed() * miningSpeed));
      }
   }

   @SubscribeEvent
   public static void handleBlockXpGained(BlockDropsEvent event) {
      int xp = event.getDroppedExperience();
      if (xp > 0 && event.getBreaker() != null) {
         event.setDroppedExperience(applyBonusXp(event.getBreaker(), xp));
      }
   }

   @SubscribeEvent
   public static void handleMobXpGained(LivingExperienceDropEvent event) {
      int xp = event.getDroppedExperience();
      if (xp > 0 && event.getAttackingPlayer() != null) {
         event.setDroppedExperience(applyBonusXp(event.getAttackingPlayer(), xp));
      }
   }

   @SubscribeEvent
   public static void handleHealingReceived(LivingHealEvent event) {
      double healingReceived = event.getEntity().getAttributeValue(IronsLibRegistries.AttributeRegistry.HEALING_RECEIVED);
      if (healingReceived != 1.0) {
         event.setAmount((float)((double)event.getAmount() * healingReceived));
      }
   }

   private static int applyBonusXp(Entity entity, int baseXp) {
      if (entity instanceof LivingEntity livingEntity) {
         double bonusXp = livingEntity.getAttributeValue(IronsLibRegistries.AttributeRegistry.EXPERIENCE_GAINED);
         return bonusXp == 1.0 ? baseXp : (int)Math.max(0.0, (double)baseXp * bonusXp + 0.5);
      } else {
         return baseXp;
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
      for (Entry entry : event.getModifiers()) {
         AttributeInstance modifier = AttributeInstance.from(entry);
         handleAttributeEvent(
            modifier,
            (attribute, attributeModifier) -> event.removeModifier(attribute, attributeModifier.id()),
            (attribute, attributeModifier) -> event.addModifier(attribute, attributeModifier, modifier.slot())
         );
      }
   }

   public static void handleAttributeEvent(
      AttributeInstance entry, BiConsumer<Holder<Attribute>, AttributeModifier> remove, BiConsumer<Holder<Attribute>, AttributeModifier> add
   ) {
      Holder<Attribute> attribute = entry.attribute();
      AttributeModifier modifier = entry.modifier();
      AttributeRemapRegistry.findTarget(attribute).ifPresent(remapped -> {
         remove.accept(attribute, modifier);
         add.accept((Holder<Attribute>)remapped, modifier);
      });
   }
}
