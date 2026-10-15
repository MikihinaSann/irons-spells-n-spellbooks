package com.userofbricks.ecisasbplugin.item;

import com.google.common.collect.Multimap;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.item.GauntletItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import top.theillusivec4.curios.api.SlotContext;

public class ExtraAttributesGauntlet extends GauntletItem {
   private final List<AttributeContainer> additionalAttributes;

   public ExtraAttributesGauntlet(Properties properties, Material materialIn, AttributeContainer... attributes) {
      super(properties, materialIn);
      this.additionalAttributes = Arrays.stream(attributes).toList();
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> atts = super.getAttributeModifiers(slotContext, id, stack);

      for (AttributeContainer entry : this.additionalAttributes) {
         atts.put(entry.attribute(), new AttributeModifier(id, entry.value(), entry.operation()));
      }

      return atts;
   }
}
