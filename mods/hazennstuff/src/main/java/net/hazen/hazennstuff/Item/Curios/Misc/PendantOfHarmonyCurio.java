package net.hazen.hazennstuff.Item.Curios.Misc;
import net.minecraft.Util;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.awt.Color;
import java.util.List;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.hazen.hazennstuff.Registries.HnSAttributeRegistry;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

public class PendantOfHarmonyCurio extends SimpleDescriptiveCurio {
   public PendantOfHarmonyCurio() {
      super(ItemPropertiesHelper.equipment().stacksTo(1).fireResistant().rarity((Rarity)HnSRarities.RADIANCE_RARITY.getValue()));
   }

   public Component getName(ItemStack stack) {
      String baseName = super.getName(stack).getString();
      MutableComponent rainbowName = Component.literal("");
      long ticks = Util.getMillis() / 50L;

      for (int i = 0; i < baseName.length(); i++) {
         float hue = (i * 40 + (float)ticks * 2.25F) % 360.0F / 360.0F;
         int rgb = Color.HSBtoRGB(hue, 1.0F, 1.0F);
         String hex = String.format("#%06X", 16777215 & rgb);
         TextColor color = (TextColor)TextColor.parseColor(hex).getOrThrow();
         rainbowName = rainbowName.append(Component.literal(String.valueOf(baseName.charAt(i))).withStyle(style -> style.withColor(color)));
      }

      return rainbowName;
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> attr = LinkedHashMultimap.create();
      attr.put(HnSAttributeRegistry.RADIANCE_SPELL_POWER, new AttributeModifier(id, 0.1, Operation.ADD_VALUE));
      attr.put(HnSAttributeRegistry.RADIANCE_SPELL_RESIST, new AttributeModifier(id, 0.05, Operation.ADD_VALUE));
      return attr;
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(Component.translatable("item.hazennstuff.pendant_of_harmony.description").withStyle(Style.EMPTY.withColor(15100659).withItalic(true)));
   }
}