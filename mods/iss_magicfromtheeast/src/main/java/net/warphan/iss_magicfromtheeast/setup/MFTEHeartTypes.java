package net.warphan.iss_magicfromtheeast.setup;

import java.util.List;
import net.minecraft.client.gui.Gui.HeartType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class MFTEHeartTypes {
   public static final EnumProxy<HeartType> JADE_HEART_PROXY = new EnumProxy(HeartType.class, createParameter("jade"));
   public static final EnumProxy<HeartType> SOUL_HEART_PROXY = new EnumProxy(HeartType.class, createParameter("soul"));

   private static List<Object> createParameter(String type) {
      return List.of(
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "hud/heart/" + type + "_full"),
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "hud/heart/" + type + "_full_blinking"),
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "hud/heart/" + type + "_half"),
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "hud/heart/" + type + "_half_blinking"),
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "hud/heart/" + type + "_hardcore_full"),
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "hud/heart/" + type + "_hardcore_full_blinking"),
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "hud/heart/" + type + "_hardcore_half"),
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "hud/heart/" + type + "_hardcore_half_blinking")
      );
   }
}
