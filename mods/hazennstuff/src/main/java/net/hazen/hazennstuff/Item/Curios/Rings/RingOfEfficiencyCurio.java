package net.hazen.hazennstuff.Item.Curios.Rings;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import top.theillusivec4.curios.api.SlotContext;

public class RingOfEfficiencyCurio extends CurioBaseItem {
   public RingOfEfficiencyCurio() {
      super(ItemPropertiesHelper.equipment().stacksTo(1).fireResistant().rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()));
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> attr = LinkedHashMultimap.create();
      attr.put(AttributeRegistry.COOLDOWN_REDUCTION, new AttributeModifier(id, 0.15, Operation.ADD_VALUE));
      attr.put(AttributeRegistry.CAST_TIME_REDUCTION, new AttributeModifier(id, 0.1, Operation.ADD_VALUE));
      return attr;
   }
}
