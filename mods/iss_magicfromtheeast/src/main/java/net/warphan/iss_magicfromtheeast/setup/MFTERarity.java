package net.warphan.iss_magicfromtheeast.setup;

import java.util.function.UnaryOperator;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class MFTERarity {
   public static final EnumProxy<Rarity> BLOODFUL_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "iss_magicfromtheeast:bloodful", (UnaryOperator<Style>)style -> style.withColor(13697024)}
   );
   public static final EnumProxy<Rarity> JADELIGHT_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "iss_magicfromtheeast:jadelight", (UnaryOperator<Style>)style -> style.withColor(7855479)}
   );
}
