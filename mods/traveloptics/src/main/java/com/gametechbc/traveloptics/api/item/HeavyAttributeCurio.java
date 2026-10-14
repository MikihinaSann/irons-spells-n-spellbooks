package com.gametechbc.traveloptics.api.item;

import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import top.theillusivec4.curios.api.SlotContext;

public class HeavyAttributeCurio extends CurioBaseItem {
   private final Multimap<Holder<Attribute>, AttributeModifier> attributeMap;

   public HeavyAttributeCurio(Properties properties, Map<Attribute, AttributeModifier> attributes) {
      super(properties);
      Builder<Holder<Attribute>, AttributeModifier> attributeBuilder = new Builder();

      for (Entry<Attribute, AttributeModifier> entry : attributes.entrySet()) {
         Holder<Attribute> attributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(entry.getKey());
         attributeBuilder.put(attributeHolder, entry.getValue());
      }

      this.attributeMap = attributeBuilder.build();
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      return this.attributeMap;
   }
}
