package com.somake.somakespells.compat.mowzie;

import com.bobmowzie.mowziesmobs.client.particle.ParticleDecal;
import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.KeyTrack;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.PropertyControl;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.PropertyControl.EnumParticleProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class MowzieFootprintCompat {
   private MowzieFootprintCompat() {
   }

   public static void spawnFootprint(final Player player, double rotationRadians, boolean leftFoot, int red, int green, int blue) {
      float startRed = red / 255.0F;
      float startGreen = green / 255.0F;
      float startBlue = blue / 255.0F;
      Vec3 offset = new Vec3(0.0, 0.0, leftFoot ? 0.15 : -0.15).yRot((float)(-rotationRadians) + 90.0F);
      ParticleDecal.spawnDecal(
         player.level(),
         ParticleHandler.PLAYER_FOOTPRINT,
         player.getX() + offset.x(),
         player.getY() + 0.01,
         player.getZ() + offset.z(),
         0.0,
         0.0,
         0.0,
         rotationRadians,
         0.5,
         startRed,
         startGreen,
         startBlue,
         1.0,
         1.0,
         100.0,
         true,
         8,
         32,
         new ParticleComponent[]{
            new PropertyControl(EnumParticleProperty.RED, new KeyTrack(new float[]{startRed, 0.05F}, new float[]{0.0F, 0.5F}), false),
            new PropertyControl(EnumParticleProperty.GREEN, new KeyTrack(new float[]{startGreen, 0.05F}, new float[]{0.0F, 0.5F}), false),
            new PropertyControl(EnumParticleProperty.BLUE, new KeyTrack(new float[]{startBlue, 0.05F}, new float[]{0.0F, 0.5F}), false),
            new PropertyControl(EnumParticleProperty.ALPHA, new KeyTrack(new float[]{1.0F, 0.8F}, new float[]{0.0F, 0.5F}), false),
            new ParticleComponent() {
               public void postUpdate(AdvancedParticleBase particle) {
                  super.postUpdate(particle);
                  if (particle.getAge() < 60.0F && player.getRandom().nextFloat() < 0.3F) {
                     float theta = player.getRandom().nextFloat() * (float) (Math.PI * 2);
                     float radius = player.getRandom().nextFloat() * 0.2F;
                     float x = radius * Mth.cos(theta);
                     float z = radius * Mth.sin(theta);
                     player.level().addParticle(ParticleTypes.SMOKE, particle.getPosX() + x, particle.getPosY() + 0.05, particle.getPosZ() + z, 0.0, 0.0, 0.0);
                  }
               }
            }
         }
      );
   }
}
