package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsHeartbeat;
import com.grandsigil.GsWatchdog;
import com.grandsigil.registry.GsEffects;
import com.grandsigil.registry.GsEntities;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.RenderGuiEvent.Post;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor;
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog;

public final class GsClient {
   private GsClient() {
   }

   @EventBusSubscriber(
      modid = "grandsigil",
      value = {Dist.CLIENT}
   )
   public static final class Events {
      private Events() {
      }

      @SubscribeEvent
      public static void onRenderLevel(RenderLevelStageEvent event) {
         long t = GsWatchdog.start();
         SigilRenderer.render(event);
         GsWatchdog.end("SigilRenderer", t);
         t = GsWatchdog.start();
         SunRenderer.render(event);
         GsWatchdog.end("SunRenderer", t);
         t = GsWatchdog.start();
         SkybreakRenderer.render(event);
         GsWatchdog.end("SkybreakRenderer", t);
         t = GsWatchdog.start();
         TreeRenderer.render(event);
         GsWatchdog.end("TreeRenderer", t);
         t = GsWatchdog.start();
         GateRenderer.render(event);
         GsWatchdog.end("GateRenderer", t);
         t = GsWatchdog.start();
         GlacierRenderer.render(event);
         GsWatchdog.end("GlacierRenderer", t);
         t = GsWatchdog.start();
         GazeRenderer.render(event);
         GsWatchdog.end("GazeRenderer", t);
         t = GsWatchdog.start();
         GazeSwarm.render(event);
         GsWatchdog.end("GazeSwarm", t);
         t = GsWatchdog.start();
         ArenaRenderer.render(event);
         GsWatchdog.end("ArenaRenderer", t);
         t = GsWatchdog.start();
         GripRenderer.render(event);
         GsWatchdog.end("GripRenderer", t);
         t = GsWatchdog.start();
         FieldRenderer.render(event);
         GsWatchdog.end("FieldRenderer", t);
         t = GsWatchdog.start();
         ReckoningRenderer.render(event);
         GsWatchdog.end("ReckoningRenderer", t);
         t = GsWatchdog.start();
         CoffinRenderer.render(event);
         GsWatchdog.end("CoffinRenderer", t);
         t = GsWatchdog.start();
         ArsenalRenderer.render(event);
         GsWatchdog.end("ArsenalRenderer", t);
         t = GsWatchdog.start();
         PhoenixRenderer.render(event);
         GsWatchdog.end("PhoenixRenderer", t);
         t = GsWatchdog.start();
         DragonRenderer.render(event);
         GsWatchdog.end("DragonRenderer", t);
         t = GsWatchdog.start();
         BloomRenderer.render(event);
         GsWatchdog.end("BloomRenderer", t);
         t = GsWatchdog.start();
         IceAgeRenderer.render(event);
         GsWatchdog.end("IceAgeRenderer", t);
         t = GsWatchdog.start();
         ThreadsRenderer.render(event);
         GsWatchdog.end("ThreadsRenderer", t);
         t = GsWatchdog.start();
         CleaveRenderer.render(event);
         GsWatchdog.end("CleaveRenderer", t);
         t = GsWatchdog.start();
         Distortion.render(event);
         GsWatchdog.end("Distortion", t);
      }

      @SubscribeEvent
      public static void onRenderGui(Post event) {
         GsHeartbeat.beat();
         ScreenFlash.render(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
      }

      @SubscribeEvent
      public static void onFogColour(ComputeFogColor event) {
         IceAgeRenderer.fogColour(event);
         ThreadsRenderer.fogColour(event);
         CleaveRenderer.fogColour(event);
      }

      @SubscribeEvent
      public static void onFog(RenderFog event) {
         IceAgeRenderer.fog(event);
         ThreadsRenderer.fog(event);
         CleaveRenderer.fog(event);
      }

      @SubscribeEvent
      public static void onCameraAngles(ComputeCameraAngles event) {
         Quake.render(event);
      }

      @SubscribeEvent
      public static void onMovementInput(MovementInputUpdateEvent event) {
         if (event.getEntity() instanceof LocalPlayer player && GsEffects.pinned(player)) {
            Input input = event.getInput();
            input.forwardImpulse = 0.0F;
            input.leftImpulse = 0.0F;
            input.up = false;
            input.down = false;
            input.left = false;
            input.right = false;
            input.jumping = false;
            return;
         }
      }
   }

   @EventBusSubscriber(
      modid = "grandsigil",
      bus = Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static final class Setup {
      private Setup() {
      }

      @SubscribeEvent
      public static void onRegisterRenderers(RegisterRenderers event) {
         event.registerEntityRenderer(GsEntities.SIGIL.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.SUN.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.BOLT.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.TREE.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.GATE.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.GLACIER.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.GAZE.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.ARENA.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.GRIP.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.CALDERA.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.RECKONING.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.COFFIN.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.ARSENAL.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.BLADE.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.PHOENIX.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.THUNDER_DRAGON.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.FULL_BLOOM.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.ICE_AGE.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.CRIMSON_THREADS.get(), NoopRenderer::new);
         event.registerEntityRenderer(GsEntities.DIMENSION_CUT.get(), NoopRenderer::new);
      }

      @SubscribeEvent
      public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
         event.registerShader(GrandSigil.id("distort"), DefaultVertexFormat.POSITION_TEX, Distortion::accept);
      }
   }
}
