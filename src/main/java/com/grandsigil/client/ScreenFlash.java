package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.GlacierEntity;
import com.grandsigil.entity.ReckoningEntity;
import com.grandsigil.entity.SigilEntity;
import com.grandsigil.entity.SunEntity;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class ScreenFlash {
   private static final ResourceLocation FLASH = GrandSigil.id("textures/gui/flash.png");
   private static final float SIGIL_DURATION = 11.0F;
   private static final float SIGIL_PEAK = 0.85F;
   private static final float SUN_DURATION = 16.0F;
   private static final float SUN_PEAK = 1.0F;
   private static final float BURN_PEAK = 0.3F;
   private static final float RECKONING_PEAK = 1.0F;
   private static final float RECKONING_DURATION = 26.0F;
   private static final float RECKONING_GLARE = 0.22F;

   private ScreenFlash() {
   }

   public static void render(GuiGraphics graphics, float partial) {
      if (GsConfig.CLIENT.screenFlash.get()) {
         if (GsPresence.anyAtAll()) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null && !minecraft.options.hideGui) {
               Vec3 eye = minecraft.gameRenderer.getMainCamera().getPosition();
               float best = 0.0F;
               float g = 0.92F;
               float b = 0.74F;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof SigilEntity) {
                     SigilEntity sigil = (SigilEntity)entity;
                     float strength = fromSigil(sigil, eye, partial);
                     if (strength > best) {
                        best = strength;
                        g = sigil.perfect() ? 0.99F : 0.92F;
                        b = sigil.perfect() ? 1.0F : 0.74F;
                     }
                  } else if (entity instanceof GlacierEntity) {
                     GlacierEntity glacier = (GlacierEntity)entity;
                     float strength = fromGlacier(glacier, eye, partial);
                     if (strength > best) {
                        best = strength;
                        g = 0.97F;
                        b = 1.0F;
                     }
                  } else if (entity instanceof ReckoningEntity) {
                     ReckoningEntity star = (ReckoningEntity)entity;
                     float strength = fromReckoning(star, eye, partial);
                     if (strength > best) {
                        best = strength;
                        g = 0.98F;
                        b = 0.88F;
                     }
                  } else if (entity instanceof SunEntity) {
                     SunEntity sun = (SunEntity)entity;
                     float strength = fromSun(sun, eye, partial);
                     if (strength > best) {
                        best = strength;
                        boolean landed = sun.stage() >= 2;
                        g = landed ? 0.95F : 0.62F;
                        b = landed ? 0.86F : 0.24F;
                     }
                  }
               }

               if (!(best <= 0.004F)) {
                  int width = graphics.guiWidth();
                  int height = graphics.guiHeight();
                  RenderSystem.enableBlend();
                  RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
                  RenderSystem.setShaderColor(1.0F, g, b, Math.min(1.0F, best));
                  graphics.blit(FLASH, 0, 0, width, height, 0.0F, 0.0F, 256, 256, 256, 256);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.disableBlend();
               }
            }
         }
      }
   }

   private static float fromSigil(SigilEntity sigil, Vec3 eye, float partial) {
      if (sigil.stage() != 2) {
         return 0.0F;
      } else {
         float age = (float)sigil.stageAge() + partial;
         if (age > 11.0F) {
            return 0.0F;
         } else {
            float near = falloff(sigil, eye, 56.0);
            return 0.85F * (1.0F - age / 11.0F) * near * (sigil.perfect() ? 1.15F : 1.0F);
         }
      }
   }

   private static float fromGlacier(GlacierEntity glacier, Vec3 eye, float partial) {
      if (glacier.stage() != 2) {
         return 0.0F;
      } else {
         float age = (float)glacier.stageAge() + partial;
         if (age > 12.0F) {
            return 0.0F;
         } else {
            float fade = 1.0F - age / 12.0F;
            return 0.92F * fade * fade * falloff(glacier, eye, (double)glacier.mass() * 3.0);
         }
      }
   }

   private static float fromReckoning(ReckoningEntity star, Vec3 eye, float partial) {
      float age = (float)star.stageAge() + partial;
      double reach = (double)star.radius() * 6.0;

      return switch (star.stage()) {
         case 2 -> 1.0F * falloff(star, eye, reach);
         case 3 -> {
            float burst = 14.0F + age;
            float wash = burst >= 26.0F ? 0.0F : 1.0F * (1.0F - burst / 26.0F);
            yield Math.max(wash, 0.22F) * falloff(star, eye, reach);
         }
         case 4 -> 0.22F * star.standing(partial) * falloff(star, eye, reach);
         default -> 0.0F;
      };
   }

   private static float fromSun(SunEntity sun, Vec3 eye, float partial) {
      float age = (float)sun.stageAge() + partial;
      double reach = (double)sun.radius() * 2.0;

      return switch (sun.stage()) {
         case 0 -> 0.120000005F * (age / 80.0F) * falloff(sun, eye, reach);
         case 1 -> 0.3F * (0.4F + 0.6F * age / 60.0F) * falloff(sun, eye, reach);
         case 2 -> 1.0F * falloff(sun, eye, reach * 2.4);
         default -> age > 16.0F ? 0.0F : 1.0F * (1.0F - age / 16.0F) * falloff(sun, eye, reach * 2.4);
      };
   }

   private static float falloff(Entity source, Vec3 eye, double reach) {
      return (float)Mth.clamp(1.0 - Math.sqrt(source.distanceToSqr(eye)) / reach, 0.0, 1.0);
   }
}
