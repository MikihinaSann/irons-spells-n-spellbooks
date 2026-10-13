package com.grandsigil.client;

import com.grandsigil.GsPresence;
import com.grandsigil.entity.GlacierEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles;

public final class Quake {
   private static final float ROLL = 9.0F;
   private static final float PITCH = 6.5F;
   private static final float YAW = 3.2F;
   private static final float CEILING = 1.9F;
   private static final float REACH = 3.4F;

   private Quake() {
   }

   public static void render(ComputeCameraAngles event) {
      if (GsPresence.any(5)) {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;
         if (level != null && !minecraft.isPaused()) {
            float partial = (float)event.getPartialTick();
            Vec3 eye = event.getCamera().getPosition();
            float strength = 0.0F;

            for (Entity entity : level.entitiesForRendering()) {
               if (entity instanceof GlacierEntity glacier) {
                  strength = Math.max(strength, from(glacier, eye, partial));
               }
            }

            if (!(strength <= 0.002F)) {
               strength = Math.min(strength, 1.9F);
               float t = (float)level.getGameTime() + partial;
               float speed = 0.8F + 1.5F * Mth.clamp(strength, 0.0F, 1.6F);
               event.setRoll(event.getRoll() + 9.0F * strength * wave(t, 1.7F * speed, 2.9F * speed));
               event.setPitch(event.getPitch() + 6.5F * strength * wave(t, 2.3F * speed, 3.7F * speed));
               event.setYaw(event.getYaw() + 3.2F * strength * wave(t, 1.3F * speed, 4.1F * speed));
            }
         }
      }
   }

   private static float from(GlacierEntity glacier, Vec3 eye, float partial) {
      double reach = (double)(glacier.mass() * 3.4F);
      double distance = Math.sqrt(glacier.distanceToSqr(eye.x, eye.y, eye.z));
      if (distance > reach) {
         return 0.0F;
      } else {
         float near = 1.0F - (float)(distance / reach);
         near = Mth.clamp(near * near * 1.4F, 0.0F, 1.0F);
         float age = (float)glacier.stageAge() + partial;

         float own = switch (glacier.stage()) {
            case 0, 1 -> 0.0F;
            case 2 -> {
               float kick = 1.2F * (float)Math.exp((double)(-age) / 5.0);
               float ring = 0.62F * (float)Math.exp((double)(-age) / 28.0);
               yield kick + ring;
            }
            default -> 0.0F;
         };
         return own * near;
      }
   }

   private static float wave(float t, float a, float b) {
      return 0.62F * Mth.sin(t * a) + 0.38F * Mth.sin(t * b + 1.7F);
   }
}
