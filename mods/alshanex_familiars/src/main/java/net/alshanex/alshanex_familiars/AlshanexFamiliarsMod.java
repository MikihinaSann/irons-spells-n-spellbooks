package net.alshanex.alshanex_familiars;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.alshanex.alshanex_familiars.block.ShrinkingStationBlock;
import net.alshanex.alshanex_familiars.block.entity.ShrinkingStationBlockEntity;
import net.alshanex.alshanex_familiars.datagen.loot.ModLootCodec;
import net.alshanex.alshanex_familiars.registry.AFCreativeTab;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.alshanex.alshanex_familiars.registry.BlockRegistry;
import net.alshanex.alshanex_familiars.registry.DataComponentRegistry;
import net.alshanex.alshanex_familiars.registry.EffectRegistry;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.alshanex_familiars.registry.ModLootModifiers;
import net.alshanex.alshanex_familiars.registry.ParticleRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.alshanex.alshanex_familiars.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod("alshanex_familiars")
public class AlshanexFamiliarsMod {
   public static final String MODID = "alshanex_familiars";
   public static final Logger LOGGER = LogUtils.getLogger();

   public static ResourceLocation res(String name) {
      return new ResourceLocation("alshanex_familiars", name);
   }

   public AlshanexFamiliarsMod(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(this::commonSetup);
      EntityRegistry.register(modEventBus);
      ItemRegistry.register(modEventBus);
      BlockRegistry.register(modEventBus);
      BlockEntityRegistry.TILES.register(modEventBus);
      DataComponentRegistry.register(modEventBus);
      PetSpellRegistry.register(modEventBus);
      EffectRegistry.register(modEventBus);
      ModLootCodec.register(modEventBus);
      ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
      ParticleRegistry.register(modEventBus);
      AFCreativeTab.register(modEventBus);
      AFSoundRegistry.register(modEventBus);
      RecipeRegistry.register(modEventBus);
      NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
      NeoForge.EVENT_BUS.register(this);
      modEventBus.addListener(this::addCreative);
      modEventBus.addListener(this::registerCapabilities);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
   }

   private void onAddReloadListeners(AddReloadListenerEvent event) {
   }

   private void addCreative(BuildCreativeModeTabContentsEvent event) {
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   @SubscribeEvent
   public void onServerStopping(ServerStoppingEvent event) {
   }

   public void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlock(ItemHandler.BLOCK, (level, pos, state, blockEntity, direction) -> {
         if (state.getValue(ShrinkingStationBlock.HALF) == DoubleBlockHalf.UPPER) {
            if (level.getBlockEntity(pos.below()) instanceof ShrinkingStationBlockEntity lowerBe) {
               return lowerBe.getItemHandler(direction);
            }
         } else if (blockEntity instanceof ShrinkingStationBlockEntity shrinkingStation) {
            return shrinkingStation.getItemHandler(direction);
         }

         return null;
      }, new Block[]{BlockRegistry.SHRINKING_STATION.get()});
   }

   public static ResourceLocation id(@NotNull String path) {
      return new ResourceLocation("alshanex_familiars", path);
   }

   @EventBusSubscriber(
      modid = "alshanex_familiars",
      bus = Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
         event.enqueueWork(
            () -> ItemRegistry.getAFItems()
               .stream()
               .filter(item -> item.get() instanceof SpellBook)
               .forEach(item -> CuriosRendererRegistry.register((Item)item.get(), SpellBookCurioRenderer::new))
         );
      }
   }
}
