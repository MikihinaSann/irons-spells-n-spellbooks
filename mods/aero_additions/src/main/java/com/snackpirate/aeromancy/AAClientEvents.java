package com.snackpirate.aeromancy;

import com.snackpirate.aeromancy.item.AAItems;
import com.snackpirate.aeromancy.spells.AASpells;
import com.snackpirate.aeromancy.spells.airstep.AirstepCounterBar;
import com.snackpirate.aeromancy.spells.airstep.AirstepPacket;
import com.snackpirate.aeromancy.spells.airstep.AirstepSpell;
import com.snackpirate.aeromancy.spells.tornado.TornadoRenderer;
import com.snackpirate.aeromancy.spells.updraft.UpdraftVisualRenderer;
import com.snackpirate.aeromancy.spells.wind_blade.WindBladeRenderer;
import com.snackpirate.aeromancy.spells.wind_charge.MagicWindChargeRenderer;
import com.snackpirate.aeromancy.spells.wind_shield.WindySwirlRenderer;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@EventBusSubscriber(
   modid = "aero_additions",
   value = {Dist.CLIENT}
)
public class AAClientEvents {
   @SubscribeEvent
   public static void rendererRegister(RegisterRenderers event) {
      CuriosRendererRegistry.register((Item)AAItems.UPDRAFT_TOME.get(), SpellBookCurioRenderer::new);
      event.registerEntityRenderer((EntityType)AASpells.Entities.MAGIC_WIND_CHARGE.get(), MagicWindChargeRenderer::new);
      event.registerEntityRenderer((EntityType)AASpells.Entities.UPDRAFT_VISUAL_ENTITY.get(), UpdraftVisualRenderer::new);
      event.registerEntityRenderer((EntityType)AASpells.Entities.SUMMONED_BREEZE.get(), BreezeRenderer::new);
      event.registerEntityRenderer((EntityType)AASpells.Entities.WIND_BLADE_PROJECTILE.get(), WindBladeRenderer::new);
      event.registerEntityRenderer((EntityType)AASpells.Entities.TORNADO.get(), TornadoRenderer::new);
   }

   @SubscribeEvent
   public static void registerRenderers(AddLayers event) {
      addLayerToPlayerSkin(event, Model.SLIM);
      addLayerToPlayerSkin(event, Model.WIDE);
   }

   private static void addLayerToPlayerSkin(AddLayers event, Model skinName) {
      if (event.getSkin(skinName) instanceof LivingEntityRenderer livingRenderer) {
         livingRenderer.addLayer(new WindySwirlRenderer.Vanilla(livingRenderer, Aeromancy.id("textures/entity/wind_shield_layer.png"), 2L));
      }
   }

   @SubscribeEvent
   public static void registerLayers(RegisterGuiLayersEvent event) {
      event.registerAboveAll(Aeromancy.id("screen_effects"), AAOverlay.instance);
      event.registerAbove(Aeromancy.id("screen_effects"), Aeromancy.id("airstep_counter"), AirstepCounterBar.instance);
   }

   @EventBusSubscriber(
      modid = "aero_additions",
      value = {Dist.CLIENT}
   )
   static class Game {
      private static boolean wasJumping = false;

      @SubscribeEvent
      private static void handleJump(Pre event) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player != null && minecraft.player == event.getEntity() && !minecraft.player.isSpectator()) {
            boolean isJumping = minecraft.options.keyJump.isDown();
            if (!wasJumping && isJumping && !event.getEntity().onGround() && !event.getEntity().isFallFlying() && AirstepSpell.canJump(event.getEntity())) {
               int x = (minecraft.player.input.up ? 1 : 0) + (minecraft.player.input.down ? -1 : 0);
               int z = (minecraft.player.input.right ? 1 : 0) + (minecraft.player.input.left ? -1 : 0);
               AirstepPacket packet = new AirstepPacket(x, z);
               AirstepSpell.airstepJump(packet, event.getEntity());
               PacketDistributor.sendToServer(packet, new CustomPacketPayload[0]);
            }

            wasJumping = isJumping;
         }
      }
   }
}
