package net.hazen.hazennstuff.Item.Curios.Crystals.LifeCrystals;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

public class StrengthenedCurio extends CurioBaseItem {
   public StrengthenedCurio() {
      super(ItemPropertiesHelper.equipment().stacksTo(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()));
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(Component.translatable("item.hazennstuff.life_crystal.description").withStyle(ChatFormatting.WHITE));
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> attr = LinkedHashMultimap.create();
      attr.put(Attributes.MAX_HEALTH, new AttributeModifier(id, 15.0, Operation.ADD_VALUE));
      attr.put(dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.HEALING_RECEIVED, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE));
      attr.put(Attributes.ARMOR, new AttributeModifier(id, 1.5, Operation.ADD_VALUE));
      return attr;
   }
}
