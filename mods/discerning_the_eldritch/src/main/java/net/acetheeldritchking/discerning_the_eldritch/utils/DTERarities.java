package net.acetheeldritchking.discerning_the_eldritch.utils;

import java.util.function.UnaryOperator;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class DTERarities {
   public static final EnumProxy<Rarity> APOTHIC_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "discerning_the_eldritch:apothic", (UnaryOperator<Style>)style -> style.withColor(12194087)}
   );
}
