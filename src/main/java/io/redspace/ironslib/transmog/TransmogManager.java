package io.redspace.ironslib.transmog;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class TransmogManager {
   private static final HashMap<ResourceLocation, TransmogHolder> TRANSMOGS = new HashMap<>();

   private static void register(ResourceLocation id, Function<ResourceLocation, TransmogHolder> factory) {
      TRANSMOGS.put(id, factory.apply(id));
   }

   @Nullable
   public static TransmogHolder get(ResourceLocation id) {
      if (id.getNamespace().equals("irons_patreon_lib")) {
         id = IronsLib.id(id.getPath());
      }

      return TRANSMOGS.get(id);
   }

   public static Collection<TransmogHolder> getAllTransmogs() {
      return TRANSMOGS.values();
   }

   static {
      register(
         IronsLib.id("standard_wizard_robes"),
         id -> new TransmogHolder(id, PatreonPermissions.None, TransmogHolder.ALL_SLOTS, new TransmogHolder.DyeConfig(true, 12117491))
      );
      register(
         IronsLib.id("standard_wizard_hat"),
         id -> new TransmogHolder(id, PatreonPermissions.None, Set.of(EquipmentSlot.HEAD), new TransmogHolder.DyeConfig(true, 12117491))
      );
      register(IronsLib.id("red_rogue"), TransmogManager.TransmogBuilder.simple(PatreonPermissions.Acolyte));
      register(
         IronsLib.id("silver_sorcerer"),
         id -> new TransmogHolder(id, PatreonPermissions.Acolyte, Set.of(EquipmentSlot.CHEST), new TransmogHolder.DyeConfig(true, 3355443))
      );
      register(
         IronsLib.id("adventurer"),
         id -> new TransmogHolder(id, PatreonPermissions.Acolyte, TransmogHolder.ALL_SLOTS, new TransmogHolder.DyeConfig(true, 4960938))
      );
      register(
         IronsLib.id("witchhunter"),
         id -> new TransmogHolder(id, PatreonPermissions.Acolyte, TransmogHolder.ALL_SLOTS, new TransmogHolder.DyeConfig(true, 8408672))
      );
      register(
         IronsLib.id("traveler"),
         id -> new TransmogHolder(id, PatreonPermissions.Acolyte, TransmogHolder.ALL_SLOTS, new TransmogHolder.DyeConfig(true, 4813659))
      );
      register(IronsLib.id("ruby_warrior"), id -> new TransmogHolder(id, PatreonPermissions.Acolyte, TransmogHolder.ALL_SLOTS, TransmogHolder.DyeConfig.NONE));
      register(IronsLib.id("onyx_shadow"), id -> new TransmogHolder(id, PatreonPermissions.Acolyte, TransmogHolder.ALL_SLOTS, TransmogHolder.DyeConfig.NONE));
      register(
         IronsLib.id("sapphire_scholar"), id -> new TransmogHolder(id, PatreonPermissions.Acolyte, TransmogHolder.ALL_SLOTS, TransmogHolder.DyeConfig.NONE)
      );
   }

   private interface TransmogBuilder extends Function<ResourceLocation, TransmogHolder> {
      static Function<ResourceLocation, TransmogHolder> simple(PatreonPermissions permissions) {
         return id -> new TransmogHolder(id, permissions);
      }
   }
}
