package com.somake.somakespells.client.rarity;

import java.util.function.UnaryOperator;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public final class SomakeRarities {
   public static final EnumProxy<Rarity> CERANIUM_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "somakespells:ceranium", (UnaryOperator<Style>)style -> style.withColor(getPulsingColor(2000L, 15388562, 8429999))}
   );
   public static final EnumProxy<Rarity> ABYSSIUM_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "somakespells:abyssium", (UnaryOperator<Style>)style -> style.withColor(getPulsingColor(2000L, 7340160, 3017042))}
   );
   public static final EnumProxy<Rarity> IGNITIUM_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "somakespells:ignitium", (UnaryOperator<Style>)style -> style.withColor(getPulsingColor(2000L, 16766783, 10173952))}
   );
   public static final EnumProxy<Rarity> CURSIUM_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "somakespells:cursium", (UnaryOperator<Style>)style -> style.withColor(getPulsingColor(2000L, 7733218, 1534809))}
   );

   private SomakeRarities() {
   }

   private static int getPulsingColor(long cycle, int color1, int color2) {
      float progress = (float)(Math.sin((double)(System.currentTimeMillis() % cycle) / cycle * 2.0 * Math.PI) + 1.0) / 2.0F;
      int red1 = color1 >> 16 & 0xFF;
      int green1 = color1 >> 8 & 0xFF;
      int blue1 = color1 & 0xFF;
      int red2 = color2 >> 16 & 0xFF;
      int green2 = color2 >> 8 & 0xFF;
      int blue2 = color2 & 0xFF;
      int red = (int)Mth.lerp(progress, red1, red2);
      int green = (int)Mth.lerp(progress, green1, green2);
      int blue = (int)Mth.lerp(progress, blue1, blue2);
      return red << 16 | green << 8 | blue;
   }
}
