package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.ArenaEntity;
import com.grandsigil.entity.GazeEntity;
import com.grandsigil.entity.GlacierEntity;
import com.grandsigil.entity.GripEntity;
import com.grandsigil.entity.SigilEntity;
import com.grandsigil.entity.SunEntity;
import com.grandsigil.registry.GsEffects;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;

public final class Distortion {
   private static final float SIGIL_DURATION = 13.0F;
   private static final float SIGIL_PEAK = 0.11F;
   private static final float SUN_DURATION = 20.0F;
   private static final float SUN_PEAK = 0.2F;
   private static final float HEAT_PEAK = 0.016F;
   private static final float GAZE_DRAIN = 0.62F;
   private static final float GAZE_STARED = 0.92F;
   private static final float[] WARM = new float[]{1.0F, 0.93F, 0.74F};
   private static final float[] VIOLET = new float[]{0.62F, 0.3F, 0.94F};
   private static final float[] MAGENTA = new float[]{0.92F, 0.24F, 0.82F};
   private static final float[] ARTERIAL = new float[]{1.0F, 0.24F, 0.26F};
   private static final float[] GLACIAL = new float[]{0.66F, 0.86F, 1.0F};
   private static final float BLOOD_PEAK = 0.62F;
   private static ShaderInstance shader;
   private static RenderTarget copy;
   private static boolean broken;
   private static Entity subject;
   private static float smear;
   private static float wobble;
   private static float drain;
   private static float blood;
   private static float[] spill = WARM;

   private Distortion() {
   }

   public static void accept(ShaderInstance instance) {
      shader = instance;
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_WEATHER) {
         if (!broken && shader != null && GsConfig.CLIENT.screenDistortion.get()) {
            if (GsPresence.anyAtAll()) {
               if (Shaders.packInUse()) {
                  release();
               } else {
                  Minecraft minecraft = Minecraft.getInstance();
                  ClientLevel level = minecraft.level;
                  if (level != null) {
                     Camera camera = event.getCamera();
                     Vec3 eye = camera.getPosition();
                     float partial = event.getPartialTick();
                     choose(level, eye, partial);
                     if (subject != null) {
                        Vector4f clip = new Vector4f(
                           (float)(subject.getX() - eye.x),
                           (float)(subject.getY() + (double)lift(subject, partial) - eye.y),
                           (float)(subject.getZ() - eye.z),
                           1.0F
                        );
                        clip.mul(event.getPoseStack().last().pose());
                        clip.mul(event.getProjectionMatrix());
                        float cx = 0.5F;
                        float cy = 0.5F;
                        if (clip.w > 1.0E-4F) {
                           cx = Mth.clamp(clip.x / clip.w * 0.5F + 0.5F, -0.5F, 1.5F);
                           cy = Mth.clamp(clip.y / clip.w * 0.5F + 0.5F, -0.5F, 1.5F);
                        } else {
                           smear *= 0.4F;
                        }

                        try {
                           apply(minecraft, cx, cy, smear, wobble, drain, ((float)subject.tickCount + partial) * 0.1F);
                        } catch (Throwable var10) {
                           broken = true;
                           GrandSigil.LOGGER.warn("Screen distortion disabled for this session", var10);
                           release();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void choose(ClientLevel level, Vec3 eye, float partial) {
      subject = null;
      smear = 0.0F;
      wobble = 0.0F;
      drain = 0.0F;
      blood = 0.0F;
      spill = WARM;
      float best = 0.0F;

      for (Entity entity : level.entitiesForRendering()) {
         float mySmear;
         float myWobble;
         float myDrain;
         float myBlood;
         float[] mySpill;
         label132: {
            mySmear = 0.0F;
            myWobble = 0.0F;
            myDrain = 0.0F;
            myBlood = 0.0F;
            mySpill = WARM;
            if (entity instanceof SigilEntity sigil && sigil.stage() == 2) {
               float age = (float)sigil.stageAge() + partial;
               if (age <= 13.0F) {
                  mySmear = 0.11F * (1.0F - age / 13.0F) * falloff(sigil, eye, 48.0) * (sigil.perfect() ? 1.3F : 1.0F);
               }
               break label132;
            }

            if (entity instanceof SunEntity sun) {
               float age = (float)sun.stageAge() + partial;
               double reach = (double)sun.radius() * 2.0;
               switch (sun.stage()) {
                  case 0:
                     myWobble = 0.0056000003F * (age / 80.0F) * falloff(sun, eye, reach);
                     break;
                  case 1:
                     myWobble = 0.016F * (0.35F + 0.65F * age / 60.0F) * falloff(sun, eye, reach);
                     break;
                  case 2:
                     mySmear = 0.2F * falloff(sun, eye, reach * 2.0);
                     myWobble = 0.016F * falloff(sun, eye, reach);
                     break;
                  default:
                     if (age <= 20.0F) {
                        mySmear = 0.2F * (1.0F - age / 20.0F) * falloff(sun, eye, reach * 2.0);
                        myWobble = 0.016F * (1.0F - age / 20.0F) * falloff(sun, eye, reach);
                     }
               }
            }
         }

         if (entity instanceof ArenaEntity arena) {
            LocalPlayer player = Minecraft.getInstance().player;
            boolean penned = player != null && arena.covers(player);
            float built = arena.built(partial);
            float down = arena.falling(partial);
            myBlood = built * 0.62F * (penned ? 0.85F : 0.18F) * (1.0F - down * 0.5F);
            if (down > 0.0F) {
               mySmear = 0.16F * Mth.clamp(1.0F - Math.abs(down - 0.5F) * 3.0F, 0.0F, 1.0F) * falloff(arena, eye, (double)arena.radius() * 1.6);
            }

            mySpill = ARTERIAL;
         }

         if (entity instanceof GripEntity grip) {
            float near = falloff(grip, eye, (double)grip.radius() * 1.4);
            float slam = grip.slam(partial);
            if (slam > 0.0F) {
               float at = Mth.clamp((slam - 0.18F) / 0.1F, 0.0F, 1.0F) * Mth.clamp(1.0F - (slam - 0.28F) / 0.22F, 0.0F, 1.0F);
               mySmear = 0.22F * at * near;
            } else {
               myWobble = 0.0112000005F * grip.risen(partial) * near;
               mySmear = 0.03F * grip.seized(partial) * near;
            }

            if (mySmear > 0.0F || myWobble > 0.0F) {
               mySpill = MAGENTA;
            }
         }

         if (entity instanceof GlacierEntity glacier) {
            float nearx = falloff(glacier, eye, (double)glacier.mass() * 2.0);
            if (nearx > 0.01F) {
               float coming = glacier.falling(partial);
               if (glacier.stage() == 0) {
                  myWobble = 0.0035200003F * nearx;
                  myDrain = 0.3F * nearx;
               } else if (glacier.stage() == 1) {
                  myWobble = 0.016F * (0.3F + 2.0F * coming * coming) * nearx;
                  mySmear = 0.16F * coming * coming * nearx;
                  myDrain = (0.3F + 0.35F * coming) * nearx;
               } else {
                  myDrain = 0.42F * nearx * (1.0F - glacier.breaking(partial));
                  myWobble = 0.0028800003F * nearx;
               }

               if (mySmear > 0.0F || myWobble > 0.0F || myDrain > 0.0F) {
                  mySpill = GLACIAL;
               }
            }
         }

         if (entity instanceof GazeEntity gaze) {
            float open = gaze.open(partial);
            if (open > 0.01F) {
               float nearx = falloff(gaze, eye, (double)gaze.radius() * 1.15);
               boolean watched = Minecraft.getInstance().player != null
                  && GsEffects.transfixed(Minecraft.getInstance().player)
                  && gaze.covers(Minecraft.getInstance().player);
               myDrain = open * nearx * (watched ? 0.92F : 0.62F);
               myWobble = open * nearx * (watched ? 0.022400001F : 0.0056000003F);
               mySmear = watched ? 0.05F * open * nearx : 0.0F;
               mySpill = VIOLET;
            }
         }

         float weight = mySmear + myWobble * 4.0F + myDrain * 0.25F + myBlood * 0.3F;
         if (weight > best) {
            best = weight;
            subject = entity;
            smear = mySmear;
            wobble = myWobble;
            drain = myDrain;
            blood = myBlood;
            spill = mySpill;
         }
      }

      if (best <= 0.0015F) {
         subject = null;
      }
   }

   private static float lift(Entity entity, float partial) {
      return entity instanceof SunEntity sun ? (float)(sun.sunCentre(partial).y - sun.getY()) : 3.0F;
   }

   private static float falloff(Entity source, Vec3 eye, double reach) {
      return (float)Mth.clamp(1.0 - Math.sqrt(source.distanceToSqr(eye)) / reach, 0.0, 1.0);
   }

   private static void apply(Minecraft minecraft, float cx, float cy, float strength, float shimmer, float grey, float time) {
      RenderTarget main = minecraft.getMainRenderTarget();
      int width = main.width;
      int height = main.height;
      if (width > 0 && height > 0) {
         if (copy == null) {
            copy = new TextureTarget(width, height, false, Minecraft.ON_OSX);
            copy.setFilterMode(9729);
         } else if (copy.width != width || copy.height != height) {
            copy.resize(width, height, Minecraft.ON_OSX);
            copy.setFilterMode(9729);
         }

         GlStateManager._glBindFramebuffer(36008, main.frameBufferId);
         GlStateManager._glBindFramebuffer(36009, copy.frameBufferId);
         GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, 16384, 9728);
         main.bindWrite(false);
         Matrix4f previousProjection = RenderSystem.getProjectionMatrix();
         VertexSorting previousSorting = RenderSystem.getVertexSorting();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableBlend();
         RenderSystem.disableCull();
         RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, (float)width, (float)height, 0.0F, 1000.0F, 3000.0F), VertexSorting.ORTHOGRAPHIC_Z);
         Matrix4fStack view = RenderSystem.getModelViewStack();
         view.pushMatrix();
         view.identity();
         view.translate(0.0F, 0.0F, -2000.0F);
         RenderSystem.applyModelViewMatrix();
         shader.safeGetUniform("Centre").set(cx, cy);
         shader.safeGetUniform("Strength").set(strength);
         shader.safeGetUniform("Wobble").set(shimmer);
         shader.safeGetUniform("Clock").set(time);
         shader.safeGetUniform("Ratio").set((float)width / (float)height);
         shader.safeGetUniform("Drain").set(grey);
         shader.safeGetUniform("Blood").set(blood);
         shader.safeGetUniform("Spill").set(spill[0], spill[1], spill[2]);
         RenderSystem.setShader(() -> shader);
         RenderSystem.setShaderTexture(0, copy.getColorTextureId());
         BufferBuilder builder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         builder.addVertex(0.0F, (float)height, 0.0F).setUv(0.0F, 0.0F);
         builder.addVertex((float)width, (float)height, 0.0F).setUv(1.0F, 0.0F);
         builder.addVertex((float)width, 0.0F, 0.0F).setUv(1.0F, 1.0F);
         builder.addVertex(0.0F, 0.0F, 0.0F).setUv(0.0F, 1.0F);
         BufferUploader.drawWithShader(builder.buildOrThrow());
         view.popMatrix();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setProjectionMatrix(previousProjection, previousSorting);
         RenderSystem.enableCull();
         RenderSystem.enableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
      }
   }

   private static void release() {
      if (copy != null) {
         copy.destroyBuffers();
         copy = null;
      }
   }
}
