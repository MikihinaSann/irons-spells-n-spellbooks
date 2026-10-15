package net.warphan.iss_magicfromtheeast.item.curios;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.item.curios.PassiveAbilityCurio;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.warphan.iss_magicfromtheeast.compat.MFTECurios;
import net.warphan.iss_magicfromtheeast.damage.MFTEDamageTypes;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.setup.MFTERarity;
import net.warphan.iss_magicfromtheeast.util.MFTEParticleHelper;

@EventBusSubscriber
public class JadePendant extends PassiveAbilityCurio {
   public static final int COOLDOWN_IN_TICKS = 160;
   public static final int RADIUS = 5;
   public static final int RADIUS_SPR = 25;

   public JadePendant() {
      super(new Properties().stacksTo(1).rarity((Rarity)MFTERarity.JADELIGHT_RARITY_PROXY.getValue()), MFTECurios.BELT_SLOT);
   }

   protected int getCooldownTicks() {
      return 160;
   }

   @SubscribeEvent
   public static void toughnessReflection(LivingIncomingDamageEvent event) {
      JadePendant PENDANT = (JadePendant)MFTEItemRegistries.JADE_PENDANT.get();
      if (event.getEntity() instanceof ServerPlayer player
         && event.getSource().getEntity() != null
         && PENDANT.isEquippedBy(player)
         && PENDANT.tryProcCooldown(player)) {
         Vec3 vec3 = player.getBoundingBox().getCenter();
         Level level = player.level;
         if (!level.isClientSide) {
            MagicManager.spawnParticles(
               level,
               new BlastwaveParticleOptions(MFTESchoolRegistries.SYMMETRY.get().getTargetingColor(), 5.0F),
               vec3.x,
               vec3.y + 0.165F,
               vec3.z,
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
            MagicManager.spawnParticles(level, MFTEParticleHelper.JADE_SHATTER, vec3.x, vec3.y, vec3.z, 8, 0.2, 0.2, 0.2, 0.3, false);
         }

         level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundRegistry.ECHOING_STRIKE, SoundSource.NEUTRAL, 1.0F, 1.0F);
         float incomingDamage = event.getOriginalAmount();
         double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
         float reflectDamage = incomingDamage * (0.5F + (float)(0.05F * armorToughness));
         DamageSource source = player.damageSources().source(MFTEDamageTypes.PENDANT_REFLECT, player);
         level.getEntities(
               player,
               player.getBoundingBox().inflate(5.0, 4.0, 5.0),
               target -> !DamageSources.isFriendlyFireBetween(target, player) && Utils.hasLineOfSight(level, player, target, true)
            )
            .forEach(target -> {
               if (target instanceof LivingEntity livingEntity && livingEntity.distanceToSqr(player) < 25.0) {
                  DamageSources.applyDamage(target, reflectDamage, source);
               }
            });
      }
   }
}
