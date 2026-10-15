package com.chadate.spellelemental;

import com.chadate.spellelemental.command.DebugCommand;
import com.chadate.spellelemental.config.ClientConfig;
import com.chadate.spellelemental.config.ServerConfig;
import com.chadate.spellelemental.data.SpellAttachments;
import com.chadate.spellelemental.element.attachment.attack.ElementEventHandler;
import com.chadate.spellelemental.element.attachment.data.UnifiedElementAttachmentDataRegistry;
import com.chadate.spellelemental.element.reaction.data.ElementReactionDataRegistry;
import com.chadate.spellelemental.element.reaction.runtime.AttributeEffectManager;
import com.chadate.spellelemental.element.reaction.runtime.ElementReactionHandler;
import com.chadate.spellelemental.event.crit.SpellDamageCritHandler;
import com.chadate.spellelemental.event.element.ElementDecaySystem;
import com.chadate.spellelemental.event.heal.HealingEventHandler;
import com.chadate.spellelemental.event.physical.PhysicalEventHandler;
import com.chadate.spellelemental.integration.jei.data.SwordOilDataManager;
import com.chadate.spellelemental.register.ModAttributes;
import com.chadate.spellelemental.register.ModBlockEntities;
import com.chadate.spellelemental.register.ModBlocks;
import com.chadate.spellelemental.register.ModCreativeTabs;
import com.chadate.spellelemental.register.ModFluid;
import com.chadate.spellelemental.register.ModItems;
import com.chadate.spellelemental.register.ModParticles;
import com.chadate.spellelemental.register.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("spellelemental")
public class SpellElemental {
   public static final String MODID = "spellelemental";
   public static final Logger LOGGER = LoggerFactory.getLogger("spellelemental");

   public SpellElemental(IEventBus modEventBus, ModContainer modContainer) {
      ModAttributes.register(modEventBus);
      ModBlocks.register(modEventBus);
      ModBlockEntities.register(modEventBus);
      ModItems.register(modEventBus);
      ModCreativeTabs.register(modEventBus);
      ModParticles.register(modEventBus);
      SpellAttachments.register(modEventBus);
      ModSounds.register(modEventBus);
      ModFluid.register(modEventBus);
      NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, SpellDamageCritHandler::applyCritBonus);
      NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, PhysicalEventHandler::applyPhysicalBonus);
      NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ElementEventHandler::handleElementAttachment);
      NeoForge.EVENT_BUS.addListener(ElementReactionHandler::damageTypeReaction);
      NeoForge.EVENT_BUS.addListener(ElementReactionHandler::tickTypeReaction);
      NeoForge.EVENT_BUS.addListener(ElementReactionDataRegistry::onAddReloadListeners);
      NeoForge.EVENT_BUS.addListener(UnifiedElementAttachmentDataRegistry::onAddReloadListeners);
      NeoForge.EVENT_BUS.addListener(SwordOilDataManager::onAddReloadListeners);
      NeoForge.EVENT_BUS.addListener(ElementEventHandler::onStartTracking);
      NeoForge.EVENT_BUS.addListener(HealingEventHandler::onLivingHeal);
      NeoForge.EVENT_BUS.addListener(ElementDecaySystem::elementDecay);
      NeoForge.EVENT_BUS.addListener(AttributeEffectManager::onServerTick);
      NeoForge.EVENT_BUS.addListener(DebugCommand::onRegisterCommands);
      modContainer.registerConfig(Type.CLIENT, ClientConfig.SPEC, String.format("%s-client.toml", "spellelemental"));
      modContainer.registerConfig(Type.SERVER, ServerConfig.SPEC, String.format("%s-server.toml", "spellelemental"));
      modEventBus.addListener(net.neoforged.fml.event.config.ModConfigEvent.class, e -> {
         if (e.getConfig().getSpec() == ServerConfig.SPEC) {
            ServerConfig.invalidateCache();
         }
      });
      modEventBus.addListener(net.neoforged.fml.event.config.ModConfigEvent.class, e -> {
         if (e.getConfig().getSpec() == ServerConfig.SPEC) {
            ServerConfig.invalidateCache();
         }
      });
   }

   public static ResourceLocation id(@NotNull String path) {
      return ResourceLocation.fromNamespaceAndPath("spellelemental", path);
   }
}
