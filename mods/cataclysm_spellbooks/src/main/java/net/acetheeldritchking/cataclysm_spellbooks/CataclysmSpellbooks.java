package net.acetheeldritchking.cataclysm_spellbooks;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import mod.azure.azurelib.common.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.common.render.item.AzItemRendererRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.AbyssalWarlockArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.AbyssalWarlockMaskRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.BloomStoneMageArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.CursiumMageArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.CursiumMageElytraArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.EngineerMageArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.ExcelsiusCooldownArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.ExcelsiusLegArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.ExcelsiusPowerArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.ExcelsiusResistArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.IgnisWizardArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.IgnisWizardElytraArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.MonstrousWizardHatArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor.PharaohMageArmorRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.items.CodexOfMaliceSpellBookRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.items.SpiritSundererStaffRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.CSArmorMaterialRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.loot.CSLootModifiers;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSAttachmentRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSAttributeRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSCreativeModeTabs;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSEntityRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSPotionEffectRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.ItemRegistries;
import net.acetheeldritchking.cataclysm_spellbooks.registries.SpellRegistries;
import net.acetheeldritchking.cataclysm_spellbooks.util.CSConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod("cataclysm_spellbooks")
public class CataclysmSpellbooks {
   public static final String MOD_ID = "cataclysm_spellbooks";
   public static final Logger LOGGER = LogUtils.getLogger();

   public CataclysmSpellbooks(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(this::commonSetup);
      NeoForge.EVENT_BUS.register(this);
      CSCreativeModeTabs.register(modEventBus);
      CSAttachmentRegistry.register(modEventBus);
      ItemRegistries.register(modEventBus);
      CSAttributeRegistry.register(modEventBus);
      CSSchoolRegistry.register(modEventBus);
      SpellRegistries.register(modEventBus);
      CSPotionEffectRegistry.register(modEventBus);
      CSEntityRegistry.register(modEventBus);
      CSArmorMaterialRegistry.register(modEventBus);
      CSLootModifiers.register(modEventBus);
      modContainer.registerConfig(Type.SERVER, CSConfig.BUILDING, String.format("%s-server.toml", "cataclysm_spellbooks"));
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      AzIdentityRegistry.register(
         (Item)ItemRegistries.IGNITIUM_WIZARD_CHESTPLATE_ELYTRA.get(),
         new Item[]{
            (Item)ItemRegistries.CURSIUM_MAGE_CHESTPLATE_ELYTRA.get(),
            (Item)ItemRegistries.EXCELSIUS_SPEED_HELMET.get(),
            (Item)ItemRegistries.EXCELSIUS_RESIST_HELMET.get(),
            (Item)ItemRegistries.EXCELSIUS_POWER_HELMET.get(),
            (Item)ItemRegistries.EXCELSIUS_SPEED_CHESTPLATE.get(),
            (Item)ItemRegistries.EXCELSIUS_RESIST_CHESTPLATE.get(),
            (Item)ItemRegistries.EXCELSIUS_POWER_CHESTPLATE.get()
         }
      );
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   public static ResourceLocation id(@NotNull String path) {
      return ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", path);
   }

   @EventBusSubscriber(
      modid = "cataclysm_spellbooks",
      bus = Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
         ItemRegistries.getCSItems()
            .stream()
            .filter(item -> item.get() instanceof SpellBook)
            .forEach(item -> CuriosRendererRegistry.register((Item)item.get(), SpellBookCurioRenderer::new));
         AzArmorRendererRegistry.register(
            AbyssalWarlockArmorRenderer::new,
            (Item)ItemRegistries.ABYSSAL_WARLOCK_HELMET.get(),
            new Item[]{
               (Item)ItemRegistries.ABYSSAL_WARLOCK_CHESTPLATE.get(),
               (Item)ItemRegistries.ABYSSAL_WARLOCK_LEGGINGS.get(),
               (Item)ItemRegistries.ABYSSAL_WARLOCK_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(AbyssalWarlockMaskRenderer::new, (Item)ItemRegistries.ABYSSAL_WARLOCK_MASK.get(), new Item[0]);
         AzArmorRendererRegistry.register(
            IgnisWizardArmorRenderer::new,
            (Item)ItemRegistries.IGNITIUM_WIZARD_HELMET.get(),
            new Item[]{
               (Item)ItemRegistries.IGNITIUM_WIZARD_CHESTPLATE.get(),
               (Item)ItemRegistries.IGNITIUM_WIZARD_LEGGINGS.get(),
               (Item)ItemRegistries.IGNITIUM_WIZARD_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(IgnisWizardElytraArmorRenderer::new, (Item)ItemRegistries.IGNITIUM_WIZARD_CHESTPLATE_ELYTRA.get(), new Item[0]);
         AzArmorRendererRegistry.register(
            CursiumMageArmorRenderer::new,
            (Item)ItemRegistries.CURSIUM_MAGE_HELMET.get(),
            new Item[]{
               (Item)ItemRegistries.CURSIUM_MAGE_CHESTPLATE.get(),
               (Item)ItemRegistries.CURSIUM_MAGE_LEGGINGS.get(),
               (Item)ItemRegistries.CURSIUM_MAGE_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(CursiumMageElytraArmorRenderer::new, (Item)ItemRegistries.CURSIUM_MAGE_CHESTPLATE_ELYTRA.get(), new Item[0]);
         AzArmorRendererRegistry.register(
            PharaohMageArmorRenderer::new,
            (Item)ItemRegistries.PHARAOH_MAGE_HELMET.get(),
            new Item[]{
               (Item)ItemRegistries.PHARAOH_MAGE_CHESTPLATE.get(),
               (Item)ItemRegistries.PHARAOH_MAGE_LEGGINGS.get(),
               (Item)ItemRegistries.PHARAOH_MAGE_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            BloomStoneMageArmorRenderer::new,
            (Item)ItemRegistries.BLOOM_STONE_HAT.get(),
            new Item[]{
               (Item)ItemRegistries.BLOOM_STONE_CHESTPLATE.get(), (Item)ItemRegistries.BLOOM_STONE_SKIRT.get(), (Item)ItemRegistries.BLOOM_STONE_GREAVES.get()
            }
         );
         AzArmorRendererRegistry.register(MonstrousWizardHatArmorRenderer::new, (Item)ItemRegistries.MONSTROUS_WIZARD_HAT.get(), new Item[0]);
         AzArmorRendererRegistry.register(
            EngineerMageArmorRenderer::new,
            (Item)ItemRegistries.ENGINEER_MAGE_HOOD.get(),
            new Item[]{
               (Item)ItemRegistries.ENGINEER_MAGE_SUIT.get(), (Item)ItemRegistries.ENGINEER_MAGE_LEGGINGS.get(), (Item)ItemRegistries.ENGINEER_MAGE_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            ExcelsiusCooldownArmorRenderer::new,
            (Item)ItemRegistries.EXCELSIUS_SPEED_HELMET.get(),
            new Item[]{(Item)ItemRegistries.EXCELSIUS_SPEED_CHESTPLATE.get()}
         );
         AzArmorRendererRegistry.register(
            ExcelsiusPowerArmorRenderer::new,
            (Item)ItemRegistries.EXCELSIUS_POWER_HELMET.get(),
            new Item[]{(Item)ItemRegistries.EXCELSIUS_POWER_CHESTPLATE.get()}
         );
         AzArmorRendererRegistry.register(
            ExcelsiusResistArmorRenderer::new,
            (Item)ItemRegistries.EXCELSIUS_RESIST_HELMET.get(),
            new Item[]{(Item)ItemRegistries.EXCELSIUS_RESIST_CHESTPLATE.get()}
         );
         AzArmorRendererRegistry.register(
            ExcelsiusLegArmorRenderer::new,
            (Item)ItemRegistries.EXCELSIUS_WARLOCK_LEGGINGS.get(),
            new Item[]{(Item)ItemRegistries.EXCELSIUS_WARLOCK_BOOTS.get()}
         );
         AzItemRendererRegistry.register(CodexOfMaliceSpellBookRenderer::new, (Item)ItemRegistries.CODEX_OF_MALICE.get(), new Item[0]);
         AzItemRendererRegistry.register(SpiritSundererStaffRenderer::new, (Item)ItemRegistries.SPIRIT_SUNDERER_STAFF.get(), new Item[0]);
      }
   }
}
