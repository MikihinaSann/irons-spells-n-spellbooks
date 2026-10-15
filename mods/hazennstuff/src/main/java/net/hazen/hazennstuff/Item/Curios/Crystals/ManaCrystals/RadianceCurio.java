package net.hazen.hazennstuff.Item.Curios.Crystals.ManaCrystals;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
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

public class RadianceCurio extends CurioBaseItem {
   public RadianceCurio() {
      super(ItemPropertiesHelper.equipment().stacksTo(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()));
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(Component.translatable("item.hazennstuff.mana_crystal.description").withStyle(ChatFormatting.WHITE));
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> attr = LinkedHashMultimap.create();
      attr.put(AttributeRegistry.MAX_MANA, new AttributeModifier(id, 300.0, Operation.ADD_VALUE));
      attr.put(AttributeRegistry.MANA_REGEN, new AttributeModifier(id, 0.15, Operation.ADD_MULTIPLIED_BASE));
      attr.put(AttributeRegistry.COOLDOWN_REDUCTION, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE));
      attr.put(AttributeRegistry.SPELL_POWER, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE));
      return attr;
   }
}
