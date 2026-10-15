package com.somake.somakespells.compat;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

public final class BornInChaosCompat {
   public static final String MODID = "born_in_chaos_v1";
   public static final ResourceLocation PUMPKIN_STAFF_ID = ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "pumpkinstaffa");
   public static final ResourceLocation BONESCALLER_STAFF_ID = ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "bonescaller_staff");
   public static final ResourceLocation STAFF_OF_MAGIC_ARROWS_ID = ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "staff_of_magic_arrows");
   public static final ResourceLocation SOULBANE_ID = ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "soulbane");
   private static final Set<ResourceLocation> COMPAT_STAFF_IDS = Set.of(PUMPKIN_STAFF_ID, BONESCALLER_STAFF_ID, STAFF_OF_MAGIC_ARROWS_ID);

   private BornInChaosCompat() {
   }

   public static boolean isLoaded() {
      return ModList.get().isLoaded("born_in_chaos_v1");
   }

   public static boolean isCompatStaff(ResourceLocation itemId) {
      return COMPAT_STAFF_IDS.contains(itemId);
   }
}
