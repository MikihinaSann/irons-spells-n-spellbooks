package com.somake.somakespells.compat.mowzie;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.ParticleRibbon;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.Attractor;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.FaceMotion;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.KeyTrack;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.Oscillator;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.PropertyControl;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.Attractor.EnumAttractorBehavior;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent.PropertyControl.EnumParticleProperty;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent.BeamPinning;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent.PanTexture;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent.PropertyOverLength;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent.PropertyOverLength.EnumRibbonProperty;
import com.somake.somakespells.capabilities.ConnectionKind;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class MowzieConnectionVfx {
   private static final int TRANSFER_ARROW_COUNT = 4;
   private static final float HOLY_R = 0.95F;
   private static final float HOLY_G = 0.9F;
   private static final float HOLY_B = 0.35F;
   private static final float HOLY_A = 0.75F;
   private static final float HOLY_RING_R = 1.0F;
   private static final float HOLY_RING_G = 0.8745098F;
   private static final float HOLY_RING_B = 0.25882354F;
   private static final float BLOOD_R = 0.82F;
   private static final float BLOOD_G = 0.1F;
   private static final float BLOOD_B = 0.1F;
   private static final float BLOOD_A = 0.82F;
   private static final float BLOOD_RING_R = 0.92F;
   private static final float BLOOD_RING_G = 0.2F;
   private static final float BLOOD_RING_B = 0.2F;

   private MowzieConnectionVfx() {
   }

   public static void spawnBeamOnly(Level level, Vec3 start, Vec3 end, ConnectionKind kind) {
      if (level.isClientSide()) {
         boolean holyLink = kind.isHealing() || kind.isProtective();
         float r = holyLink ? 0.95F : 0.82F;
         float g = holyLink ? 0.9F : 0.1F;
         float b = holyLink ? 0.35F : 0.1F;
         float a = holyLink ? 0.75F : 0.82F;
         Vec3[] startPos = new Vec3[]{start};
         Vec3[] endPos = new Vec3[]{end};
         double dist = Math.max(end.distanceTo(start), 0.01);
         int spawnFreq = 5;
         ParticleRibbon.spawnRibbon(
            level,
            ParticleHandler.RIBBON_SQUIGGLE,
            (int)(0.5 * dist),
            start.x(),
            start.y(),
            start.z(),
            0.0,
            0.0,
            0.0,
            true,
            0.0,
            0.0,
            0.0,
            0.5,
            r,
            g,
            b,
            a,
            1.0,
            spawnFreq,
            true,
            new ParticleComponent[]{new BeamPinning(startPos, endPos), new PanTexture(0.0F, 1.0F)}
         );
      }
   }

   public static void spawnTransferPulse(Level level, Vec3 start, Vec3 end, ConnectionKind kind, int travelTicks) {
      if (level.isClientSide()) {
         boolean holyLink = kind.isHealing() || kind.isProtective();
         float r = holyLink ? 0.95F : 0.82F;
         float g = holyLink ? 0.9F : 0.1F;
         float b = holyLink ? 0.35F : 0.1F;
         float a = holyLink ? 0.75F : 0.82F;
         float ringR = holyLink ? 1.0F : 0.92F;
         float ringG = holyLink ? 0.8745098F : 0.2F;
         float ringB = holyLink ? 0.25882354F : 0.2F;
         double dist = Math.max(end.distanceTo(start), 0.01);
         float particleLifetime = Math.max(1, travelTicks);

         for (int i = 0; i < 4; i++) {
            Vec3[] endPos = new Vec3[]{end};
            double radius = 0.5;
            double yaw = level.random.nextFloat() * Math.PI * 2.0;
            double pitch = level.random.nextFloat() * Math.PI * 2.0;
            double ox = radius * Math.sin(yaw) * Math.sin(pitch);
            double oy = radius * Math.cos(pitch);
            double oz = radius * Math.cos(yaw) * Math.sin(pitch);
            double startOffsetRadius = 0.18;
            double offsetYaw = level.random.nextFloat() * Math.PI * 2.0;
            double offsetPitch = level.random.nextFloat() * Math.PI * 2.0;
            double startX = start.x() + startOffsetRadius * Math.sin(offsetYaw) * Math.sin(offsetPitch);
            double startY = start.y() + startOffsetRadius * Math.cos(offsetPitch);
            double startZ = start.z() + startOffsetRadius * Math.cos(offsetYaw) * Math.sin(offsetPitch);
            AdvancedParticleBase.spawnParticle(
               level,
               ParticleHandler.ARROW_HEAD,
               startX,
               startY,
               startZ,
               0.0,
               0.0,
               0.0,
               false,
               0.0,
               0.0,
               0.0,
               0.0,
               3.5,
               r,
               g,
               b,
               a,
               1.0,
               particleLifetime,
               true,
               false,
               new ParticleComponent[]{
                  new Attractor(endPos, 0.5F, 0.2F, EnumAttractorBehavior.LINEAR),
                  new RibbonComponent(
                     ParticleHandler.RIBBON_FLAT,
                     10,
                     0.0,
                     0.0,
                     0.0,
                     0.12F,
                     r,
                     g,
                     b,
                     a,
                     true,
                     true,
                     new ParticleComponent[]{new PropertyOverLength(EnumRibbonProperty.SCALE, KeyTrack.startAndEnd(1.0F, 0.0F))}
                  ),
                  new PropertyControl(EnumParticleProperty.POS_X, new Oscillator(0.0F, (float)ox, (float)dist, 2.5F), true),
                  new PropertyControl(EnumParticleProperty.POS_Y, new Oscillator(0.0F, (float)oy, (float)dist, 2.5F), true),
                  new PropertyControl(EnumParticleProperty.POS_Z, new Oscillator(0.0F, (float)oz, (float)dist, 2.5F), true),
                  new FaceMotion(),
                  new PropertyControl(EnumParticleProperty.ALPHA, new KeyTrack(new float[]{0.0F, 0.0F, 1.0F}, new float[]{0.0F, 0.05F, 0.06F}), false)
               }
            );
         }

         AdvancedParticleBase.spawnParticle(
            level,
            ParticleHandler.RING2,
            start.x(),
            start.y(),
            start.z(),
            0.0,
            0.0,
            0.0,
            true,
            0.0,
            0.0,
            0.0,
            0.0,
            1.5,
            ringR,
            ringG,
            ringB,
            1.0,
            1.0,
            15.0,
            true,
            false,
            new ParticleComponent[]{
               new PropertyControl(EnumParticleProperty.ALPHA, KeyTrack.startAndEnd(1.0F, 0.0F), false),
               new PropertyControl(EnumParticleProperty.SCALE, KeyTrack.startAndEnd(1.0F, 10.0F), false)
            }
         );
      }
   }
}
