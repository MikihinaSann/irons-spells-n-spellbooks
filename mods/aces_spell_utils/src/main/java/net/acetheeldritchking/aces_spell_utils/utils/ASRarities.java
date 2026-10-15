package net.acetheeldritchking.aces_spell_utils.utils;

import java.util.function.UnaryOperator;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class ASRarities {
   public static final EnumProxy<Rarity> GLACIAL_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "aces_spell_utils:glacial", (UnaryOperator<Style>)style -> style.withColor(5228287)}
   );
   public static final EnumProxy<Rarity> AQUATIC_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "aces_spell_utils:aquatic", (UnaryOperator<Style>)style -> style.withColor(27371)}
   );
   public static final EnumProxy<Rarity> VERDANT_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "aces_spell_utils:verdant", (UnaryOperator<Style>)style -> style.withColor(5105722)}
   );
   public static final EnumProxy<Rarity> COSMIC_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "aces_spell_utils:cosmic", (UnaryOperator<Style>)style -> style.withColor(10163176)}
   );
   public static final EnumProxy<Rarity> FORBIDDEN_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "aces_spell_utils:forbidden", (UnaryOperator<Style>)style -> style.withColor(1187611)}
   );
   public static final EnumProxy<Rarity> ARID_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "aces_spell_utils:arid", (UnaryOperator<Style>)style -> style.withColor(16758338)}
   );
   public static final EnumProxy<Rarity> ACCURSED_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "aces_spell_utils:accursed", (UnaryOperator<Style>)style -> style.withColor(16067628)}
   );
   public static final EnumProxy<Rarity> SCULK_RARITY_PROXY = new EnumProxy(
      Rarity.class, new Object[]{-1, "aces_spell_utils:sculk", (UnaryOperator<Style>)style -> style.withColor(760476)}
   );
}
