package net.hazen.hazennstuff;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

@EventBusSubscriber({Dist.CLIENT})
public class HnSConfig {
   private static final Builder BUILDER = new Builder();
   private static final ConfigValue<Integer> GALVANIZED_SHEATH_COOLDOWN = BUILDER.comment(
         "Defines the cooldown in seconds value for the Galvanized Sheath's ability"
      )
      .comment("Default is 8]")
      .define("Galvanized Sheath CD", 8);
   private static final ConfigValue<Integer> SCROLL_SHEATH_COOLDOWN = BUILDER.comment("Defines the cooldown in seconds value for the Scroll Sheath's ability")
      .comment("Default is [60]")
      .define("Scroll Sheath CD", 60);
   static final ModConfigSpec SPEC = BUILDER.build();
   public static int galvanizedSheathCooldown;
   public static int scrollSheathCooldown;

   @SubscribeEvent
   static void onLoad(ModConfigEvent event) {
      galvanizedSheathCooldown = (Integer)GALVANIZED_SHEATH_COOLDOWN.get();
      scrollSheathCooldown = (Integer)SCROLL_SHEATH_COOLDOWN.get();
   }
}
