package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.init.TOSounds;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Orb_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class OrbitalVoidEffect extends MobEffect {
   private static final Random RANDOM = new Random();

   public OrbitalVoidEffect() {
      super(MobEffectCategory.BENEFICIAL, 0);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      super.applyEffectTick(entity, amplifier);
      if (entity.level() instanceof ServerLevel serverLevel && entity.tickCount % 60 == 0) {
         int effectLevel = amplifier + 1;
         double totalOrbPower = effectLevel;
         int orbCount = (int)Math.ceil(totalOrbPower * 4.0);
         double angleBetween = (Math.PI * 2) / orbCount;

         for (int i = 0; i < orbCount; i++) {
            double angle = angleBetween * i;
            double offsetX = Math.sin(angle) * 6.0;
            double offsetZ = Math.cos(angle) * 6.0;
            double motionScale = 3.0;
            Vec3 motion = new Vec3(offsetX, 0.0, offsetZ).normalize().scale(motionScale);
            EntityType<?> entityType = (EntityType<?>)ModEntities.ABYSS_ORB.get();
            if (entityType != null) {
               Projectile projectile = (Projectile)entityType.create(serverLevel);
               if (projectile != null) {
                  projectile.moveTo(entity.getX(), entity.getY() + 1.5, entity.getZ(), 0.0F, 0.0F);
                  projectile.setDeltaMovement(motion.x(), motion.y(), motion.z());
                  if (projectile instanceof Abyss_Orb_Entity abyssOrb) {
                     abyssOrb.setDamage(amplifier * 5);
                     abyssOrb.setOwner(entity);
                  }

                  serverLevel.addFreshEntity(projectile);
               }
            }
         }

         if (entity instanceof Player player) {
            this.playSound(player, (SoundEvent)TOSounds.ORBITAL_VOID_PULSE.get());
         }
      }

      return true;
   }

   private void playSound(Player player, SoundEvent soundEvent) {
      if (soundEvent != null && !player.level().isClientSide()) {
         player.playNotifySound(soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
