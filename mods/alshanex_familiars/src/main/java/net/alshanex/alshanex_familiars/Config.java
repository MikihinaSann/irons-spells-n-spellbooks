package net.alshanex.alshanex_familiars;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

@EventBusSubscriber(
   modid = "alshanex_familiars",
   bus = Bus.MOD
)
public class Config {
   private static final Builder BUILDER = new Builder();
   private static final BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);
   private static final IntValue MAGIC_NUMBER = BUILDER.comment("A magic number").defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
   public static final ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number")
      .define("magicNumberIntroduction", "The magic number is... ");
   private static final ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.")
      .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);
   static final ModConfigSpec SPEC = BUILDER.build();
   public static boolean logDirtBlock;
   public static int magicNumber;
   public static String magicNumberIntroduction;
   public static Set<Item> items;

   private static boolean validateItemName(Object obj) {
      return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse((String)itemName));
   }

   @SubscribeEvent
   static void onLoad(ModConfigEvent event) {
      logDirtBlock = (Boolean)LOG_DIRT_BLOCK.get();
      magicNumber = (Integer)MAGIC_NUMBER.get();
      magicNumberIntroduction = (String)MAGIC_NUMBER_INTRODUCTION.get();
      items = ((List<String>)ITEM_STRINGS.get())
         .stream()
         .map(itemName -> (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)itemName)))
         .collect(Collectors.toSet());
   }
}
