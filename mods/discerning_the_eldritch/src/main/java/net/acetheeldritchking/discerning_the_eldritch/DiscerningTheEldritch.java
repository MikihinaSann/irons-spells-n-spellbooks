package net.acetheeldritchking.discerning_the_eldritch;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import mod.azure.azurelib.common.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.common.render.item.AzItemRendererRegistry;
import net.acetheeldritchking.aces_spell_utils.entity.render.items.SheathCurioRenderer;
import net.acetheeldritchking.aces_spell_utils.items.curios.SheathCurioItem;
import net.acetheeldritchking.discerning_the_eldritch.compat.CompatRegistry;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.armor.CrimsonStagArmorRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.armor.EldritchWarlockArmorRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.armor.EldritchWarlockHelmetRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.armor.EldritchWarlockMaskRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.items.CastersMantleCurioItemRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.items.CastersMantleCurioRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.items.KingsEffigyCurioItemRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.items.KingsEffigyCurioRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.items.StaffOfVehemenceRenderer;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.DTEArmorMaterialRegistry;
import net.acetheeldritchking.discerning_the_eldritch.loot.DTELootModifiers;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttachmentRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttributeRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTECreativeModeTabs;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEDataComponentRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEFluidRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEParticleRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESchoolRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTEServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod("discerning_the_eldritch")
public class DiscerningTheEldritch {
   public static final String MOD_ID = "discerning_the_eldritch";
   public static final Logger LOGGER = LogUtils.getLogger();

   public DiscerningTheEldritch(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(this::commonSetup);
      NeoForge.EVENT_BUS.register(this);
      DTECreativeModeTabs.register(modEventBus);
      ItemRegistries.register(modEventBus);
      SpellRegistries.register(modEventBus);
      DTEPotionEffectRegistry.register(modEventBus);
      DTESoundRegistry.register(modEventBus);
      DTEEntityRegistry.register(modEventBus);
      DTEArmorMaterialRegistry.register(modEventBus);
      DTELootModifiers.register(modEventBus);
      DTEAttributeRegistry.register(modEventBus);
      DTESchoolRegistry.register(modEventBus);
      DTEAttachmentRegistry.register(modEventBus);
      DTEParticleRegistry.register(modEventBus);
      DTEFluidRegistry.register(modEventBus);
      DTEDataComponentRegistry.register(modEventBus);
      CompatRegistry.registerPastelItems(modEventBus);
      modContainer.registerConfig(Type.COMMON, DTEServerConfig.SPEC, String.format("%s-common.toml", "discerning_the_eldritch"));
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      AzIdentityRegistry.register(
         (Item)ItemRegistries.ELDRITCH_WARLOCK_HOOD.get(),
         new Item[]{
            (Item)ItemRegistries.ELDRITCH_WARLOCK_ROBES.get(),
            (Item)ItemRegistries.ELDRITCH_WARLOCK_LEGGINGS.get(),
            (Item)ItemRegistries.ELDRITCH_WARLOCK_GREAVES.get(),
            (Item)ItemRegistries.ELDRITCH_WARLOCK_MASK.get(),
            (Item)ItemRegistries.ELDRITCH_WARLOCK_HELMET.get(),
            (Item)ItemRegistries.STAFF_OF_VEHEMENCE.get(),
            (Item)ItemRegistries.KINGS_EFFIGY.get()
         }
      );
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   public static ResourceLocation id(@NotNull String path) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", path);
   }

   @EventBusSubscriber(
      modid = "discerning_the_eldritch",
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
         event.enqueueWork(
            () -> {
               ItemRegistries.getDTEItems()
                  .stream()
                  .filter(item -> item.get() instanceof SpellBook)
                  .forEach(item -> CuriosRendererRegistry.register((Item)item.get(), SpellBookCurioRenderer::new));
               ItemRegistries.getDTEItems()
                  .stream()
                  .filter(item -> item.get() instanceof SheathCurioItem)
                  .forEach(item -> CuriosRendererRegistry.register((Item)item.get(), SheathCurioRenderer::new));
            }
         );
         AzArmorRendererRegistry.register(
            EldritchWarlockArmorRenderer::new,
            (Item)ItemRegistries.ELDRITCH_WARLOCK_HOOD.get(),
            new Item[]{
               (Item)ItemRegistries.ELDRITCH_WARLOCK_ROBES.get(),
               (Item)ItemRegistries.ELDRITCH_WARLOCK_LEGGINGS.get(),
               (Item)ItemRegistries.ELDRITCH_WARLOCK_GREAVES.get()
            }
         );
         AzArmorRendererRegistry.register(EldritchWarlockMaskRenderer::new, (Item)ItemRegistries.ELDRITCH_WARLOCK_MASK.get(), new Item[0]);
         AzArmorRendererRegistry.register(EldritchWarlockHelmetRenderer::new, (Item)ItemRegistries.ELDRITCH_WARLOCK_HELMET.get(), new Item[0]);
         AzArmorRendererRegistry.register(KingsEffigyCurioItemRenderer::new, (Item)ItemRegistries.KINGS_EFFIGY.get(), new Item[0]);
         AzArmorRendererRegistry.register(CastersMantleCurioItemRenderer::new, (Item)ItemRegistries.CASTERS_MANTLE.get(), new Item[0]);
         AzArmorRendererRegistry.register(
            CrimsonStagArmorRenderer::new,
            (Item)ItemRegistries.CRIMSON_STAG_ANTLERS.get(),
            new Item[]{
               (Item)ItemRegistries.CRIMSON_STAG_ROBES.get(), (Item)ItemRegistries.CRIMSON_STAG_LEGGINGS.get(), (Item)ItemRegistries.CRIMSON_STAG_BOOTS.get()
            }
         );
         AzItemRendererRegistry.register(StaffOfVehemenceRenderer::new, (Item)ItemRegistries.STAFF_OF_VEHEMENCE.get(), new Item[0]);
         CuriosRendererRegistry.register((Item)ItemRegistries.KINGS_EFFIGY.get(), KingsEffigyCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ItemRegistries.CASTERS_MANTLE.get(), CastersMantleCurioRenderer::new);
      }
   }
}
