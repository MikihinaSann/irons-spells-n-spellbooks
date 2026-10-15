package com.somake.somakespells.effect.aqua;

import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModParticles;
import dev.shadowsoffire.apothic_attributes.util.AttributesUtil;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class SubmergedEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float AQUA_SPELL_POWER_PER_LEVEL = 0.05F;
   private static final int WET_DURATION_TICKS = 100;

   public SubmergedEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      if (!event.getEntity().level().isClientSide()) {
         if (AttributesUtil.isPhysicalDamage(event.getSource())) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
               if (attacker.hasEffect(ModMobEffects.SUBMERGED)) {
                  LivingEntity target = event.getEntity();
                  target.addEffect(new MobEffectInstance(ModMobEffects.WET, 100, 0, false, false, true));
                  if (target.level() instanceof ServerLevel serverLevel) {
                     Vec3 center = target.getBoundingBox().getCenter();
                     Vec3 towardAttacker = attacker.getEyePosition().subtract(center);
                     if (towardAttacker.lengthSqr() > 1.0E-4) {
                        center = center.add(towardAttacker.normalize().scale(target.getBbWidth() * 0.45));
                     }

                     serverLevel.sendParticles(
                        (SimpleParticleType)ModParticles.SUBMERGED_HIT_SPLASH.get(), center.x, center.y, center.z, 1, 0.02, 0.03, 0.02, 0.0
                     );
                  }
               }
            }
         }
      }
   }
}
