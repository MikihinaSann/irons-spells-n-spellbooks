package com.snackpirate.aeromancy;

import com.mojang.logging.LogUtils;
import com.snackpirate.aeromancy.item.AAItems;
import com.snackpirate.aeromancy.network.AADataAttachments;
import com.snackpirate.aeromancy.spells.AASpells;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod("aero_additions")
public class Aeromancy {
   public static final String MOD_ID = "aero_additions";
   public static final Logger LOGGER = LogUtils.getLogger();

   public static ResourceLocation id(String s) {
      return ResourceLocation.fromNamespaceAndPath("aero_additions", s);
   }

   public Aeromancy(IEventBus modEventBus, ModContainer modContainer) {
      AASounds.register(modEventBus);
      AASpells.register(modEventBus);
      AAItems.register(modEventBus);
      AACreativeTab.register(modEventBus);
      AADataAttachments.register(modEventBus);
   }
}
