package net.hazen.hazennstuff.Rarity;

import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class HnSRarities {
   public static final EnumProxy<Rarity> BLOOD_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:blood", (UnaryOperator<Style>)style -> style.withColor(ChatFormatting.DARK_RED)}
   );
   public static final EnumProxy<Rarity> DEUS_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:deus", (UnaryOperator<Style>)style -> style.withColor(12910574)}
   );
   public static final EnumProxy<Rarity> ELDRITCH_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:eldritch", (UnaryOperator<Style>)style -> style.withColor(1016732)}
   );
   public static final EnumProxy<Rarity> RADIANCE_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:radiance", (UnaryOperator<Style>)style -> style.withColor(14984938)}
   );
   public static final EnumProxy<Rarity> ENDER_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:ender", (UnaryOperator<Style>)style -> style.withColor(ChatFormatting.LIGHT_PURPLE)}
   );
   public static final EnumProxy<Rarity> SHADOW_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:shadow", (UnaryOperator<Style>)style -> style.withColor(5585535)}
   );
   public static final EnumProxy<Rarity> EVOCATION_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:evocation", (UnaryOperator<Style>)style -> style.withColor(ChatFormatting.WHITE)}
   );
   public static final EnumProxy<Rarity> FIRE_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:fire", (UnaryOperator<Style>)style -> style.withColor(ChatFormatting.GOLD)}
   );
   public static final EnumProxy<Rarity> HOLY_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:holy", (UnaryOperator<Style>)style -> style.withColor(16775380)}
   );
   public static final EnumProxy<Rarity> ICE_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:ice", (UnaryOperator<Style>)style -> style.withColor(13695487)}
   );
   public static final EnumProxy<Rarity> LIGHTNING_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:lightning", (UnaryOperator<Style>)style -> style.withColor(ChatFormatting.AQUA)}
   );
   public static final EnumProxy<Rarity> NATURE_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:nature", (UnaryOperator<Style>)style -> style.withColor(ChatFormatting.GREEN)}
   );
   public static final EnumProxy<Rarity> COSMIC_RARITY = new EnumProxy(
      Rarity.class, new Object[]{-1, "hazennstuff:cosmic", (UnaryOperator<Style>)style -> style.withColor(4665750)}
   );
}
