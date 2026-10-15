package com.somake.somakespells.effect.blood;

import com.somake.somakespells.particle.BloodSlashParticleOptions;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.blood.BloodCutSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;

@EventBusSubscriber
public class BloodCutEffect extends MagicMobEffect {
   public BloodCutEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (event.getSource() instanceof SpellDamageSource) {
            return;
         }

         MobEffectInstance effect = attacker.getEffect(ModMobEffects.BLOOD_CUT);
         if (effect != null) {
            float damage = event.getNewDamage() * getDamageModifier(effect.getAmplifier(), attacker);
            Vec3 forward = attacker.getForward();
            Vec3 hitLocation = attacker.position().add(0.0, attacker.getBbHeight() * 0.3F, 0.0).add(forward.scale(1.9F));
            boolean mirrored = attacker.tickCount % 2 == 0;
            MagicManager.spawnParticles(
               attacker.level(),
               new BloodSlashParticleOptions((float)forward.x, (float)forward.y, (float)forward.z, mirrored, false, 1.0F),
               hitLocation.x,
               hitLocation.y + 0.3,
               hitLocation.z,
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
            event.getEntity().invulnerableTime = 0;
            DamageSources.applyDamage(event.getEntity(), damage, ((BloodCutSpell)ModSpells.BLOOD_CUT.get()).getDamageSource(attacker));
         }
      }
   }

   public static float getDamageModifier(int effectAmplifier, @Nullable LivingEntity caster) {
      float power = caster == null ? 1.0F : ((BloodCutSpell)ModSpells.BLOOD_CUT.get()).getEntityPowerMultiplier(caster);
      return (effectAmplifier + 1) * power * 0.1F;
   }
}
