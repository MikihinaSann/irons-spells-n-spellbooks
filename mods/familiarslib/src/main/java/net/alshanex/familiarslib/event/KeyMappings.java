package net.alshanex.familiarslib.event;

import com.mojang.blaze3d.platform.InputConstants.Type;
import io.redspace.ironsspellbooks.player.ExtendedKeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "familiarslib",
   bus = Bus.MOD
)
public class KeyMappings {
   public static final String KEY_BIND_SUMMONING_CATEGORY = "key.familiarslib.summoning_keys";
   public static final ExtendedKeyMapping SUMMONING_KEYMAP = new ExtendedKeyMapping(
      getResourceName("summoning_key"), KeyConflictContext.IN_GAME, Type.KEYSYM, 88, "key.familiarslib.summoning_keys"
   );
   public static final ExtendedKeyMapping SCREEN_KEYMAP = new ExtendedKeyMapping(
      getResourceName("screen_key"), KeyConflictContext.IN_GAME, Type.KEYSYM, 72, "key.familiarslib.summoning_keys"
   );

   private static String getResourceName(String name) {
      return String.format("key.familiarslib.%s", name);
   }

   @SubscribeEvent
   public static void onRegisterKeybinds(RegisterKeyMappingsEvent event) {
      event.register(SUMMONING_KEYMAP);
      event.register(SCREEN_KEYMAP);
   }
}
