package com.chadate.spellelemental.integration.jei.data;

import com.chadate.spellelemental.SpellElemental;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SwordOilConfigLoader extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = new Gson();
   private static final List<SwordOilConfigLoader.SwordOilConfig> SWORD_OIL_CONFIGS = new ArrayList<>();
   private static boolean initialized = false;

   public SwordOilConfigLoader() {
      super(GSON, "sword_oils");
   }

   public static void preInitialize() {
      if (!initialized) {
         SWORD_OIL_CONFIGS.clear();
         initialized = true;
         SpellElemental.LOGGER.info("剑油配置加载器已预初始化，等待数据包加载");
      }
   }

   protected void apply(
      Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller
   ) {
      SWORD_OIL_CONFIGS.clear();
      initialized = false;

      for (Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
         ResourceLocation resourceLocation = entry.getKey();
         JsonElement jsonElement = entry.getValue();

         try {
            if (jsonElement.isJsonObject()) {
               JsonObject jsonObject = jsonElement.getAsJsonObject();
               if (jsonObject.has("sword_oils")) {
                  for (JsonElement oilElement : jsonObject.getAsJsonArray("sword_oils")) {
                     if (oilElement.isJsonObject()) {
                        JsonObject oilObject = oilElement.getAsJsonObject();
                        SwordOilConfigLoader.SwordOilConfig config = this.parseSwordOilConfig(oilObject);
                        if (config != null) {
                           SWORD_OIL_CONFIGS.add(config);
                        }
                     }
                  }
               }
            }
         } catch (Exception var14) {
         }
      }

      SpellElemental.LOGGER.info("精油配置数据包加载完成，共加载 {} 个配置", SWORD_OIL_CONFIGS.size());
   }

   private SwordOilConfigLoader.SwordOilConfig parseSwordOilConfig(JsonObject jsonObject) {
      try {
         String itemId = jsonObject.get("item").getAsString();
         String element = jsonObject.get("element").getAsString();
         int amount = jsonObject.get("amount").getAsInt();
         String displayName = jsonObject.has("display_name") ? jsonObject.get("display_name").getAsString() : itemId;
         return new SwordOilConfigLoader.SwordOilConfig(itemId, element, amount, displayName);
      } catch (Exception var6) {
         return null;
      }
   }

   public static List<SwordOilConfigLoader.SwordOilConfig> getSwordOilConfigs() {
      if (SWORD_OIL_CONFIGS.isEmpty() && !initialized) {
         preInitialize();
      }

      return new ArrayList<>(SWORD_OIL_CONFIGS);
   }

   public static SwordOilConfigLoader.SwordOilConfig getSwordOilConfig(Item item) {
      ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
      return SWORD_OIL_CONFIGS.stream().filter(config -> config.getItemId().equals(itemId.toString())).findFirst().orElse(null);
   }

   public static boolean isSwordOil(Item item) {
      return getSwordOilConfig(item) != null;
   }

   public static void clearConfigs() {
      SWORD_OIL_CONFIGS.clear();
   }

   public static void addConfig(SwordOilConfigLoader.SwordOilConfig config) {
      SWORD_OIL_CONFIGS.add(config);
   }

   public static class SwordOilConfig {
      private final String itemId;
      private final String element;
      private final int amount;
      private final String displayName;

      public SwordOilConfig(String itemId, String element, int amount, String displayName) {
         this.itemId = itemId;
         this.element = element;
         this.amount = amount;
         this.displayName = displayName;
      }

      public String getItemId() {
         return this.itemId;
      }

      public String getElement() {
         return this.element;
      }

      public int getAmount() {
         return this.amount;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public ItemStack getItemStack() {
         ResourceLocation resourceLocation = ResourceLocation.parse(this.itemId);
         Item item = (Item)BuiltInRegistries.ITEM.get(resourceLocation);
         return new ItemStack(item);
      }
   }
}
