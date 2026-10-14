package com.github.L_Ender.lionfishapi;

import com.github.L_Ender.lionfishapi.server.network.AnimationMessage;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("lionfishapi")
public class LionfishAPI {
   public static final String MODID = "lionfishapi";
   public static final Logger LOGGER = LogManager.getLogger();
   private static final String MODEL_DIR = "textures/entity/";

   public LionfishAPI(IEventBus modEventBus) {
      modEventBus.addListener(this::setupPackets);
   }

   public static ResourceLocation prefix(String name) {
      return ResourceLocation.fromNamespaceAndPath("lionfishapi", name.toLowerCase(Locale.ROOT));
   }

   public static ResourceLocation getModelTexture(String name) {
      return ResourceLocation.fromNamespaceAndPath("lionfishapi", "textures/entity/" + name);
   }

   public void setupPackets(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("lionfishapi").versioned("1.0.0").optional();
      registrar.playToClient(AnimationMessage.TYPE, AnimationMessage.STREAM_CODEC, AnimationMessage::handle);
   }
}
