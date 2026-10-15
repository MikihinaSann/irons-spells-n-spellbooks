package net.hazen.hazennstuff.Item.HnSUtilities;

import com.google.common.base.Suppliers;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import java.util.function.Supplier;
import net.hazen.hazennstuff.Animations.HnSDispatcher;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;

public class HnSArmorItem extends ArmorItem {
   private final Supplier<ItemAttributeModifiers> defaultModifiers;
   public final HnSDispatcher dispatcher = new HnSDispatcher();

   public HnSArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, AttributeContainer... attributeContainers) {
      super(material, type, properties);
      this.defaultModifiers = Suppliers.memoize(() -> {
         int i = ((ArmorMaterial)material.value()).getDefense(type);
         float f = ((ArmorMaterial)material.value()).toughness();
         Builder builder = ItemAttributeModifiers.builder();
         EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
         ResourceLocation resourceLocation = ResourceLocation.withDefaultNamespace("armor." + type.getName());
         builder.add(Attributes.ARMOR, new AttributeModifier(resourceLocation, i, Operation.ADD_VALUE), equipmentSlotGroup);
         builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourceLocation, f, Operation.ADD_VALUE), equipmentSlotGroup);
         float resistance = ((ArmorMaterial)material.value()).knockbackResistance();
         if (resistance > 0.0F) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(resourceLocation, resistance, Operation.ADD_VALUE), equipmentSlotGroup);
         }

         for (AttributeContainer holder : attributeContainers) {
            builder.add(holder.attribute(), holder.createModifier(type.getSlot().getName()), equipmentSlotGroup);
         }

         return builder.build();
      });
   }

   public static AttributeContainer[] pureTier(Holder<Attribute> school) {
      return new AttributeContainer[]{
         new AttributeContainer(school, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.05F, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] pureTierMulti(Holder<Attribute> school1, Holder<Attribute> school2) {
      return new AttributeContainer[]{
         new AttributeContainer(school1, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE)
      };
   }

   public static AttributeContainer[] pureTierTri(Holder<Attribute> school1, Holder<Attribute> school2) {
      return new AttributeContainer[]{
         new AttributeContainer(school1, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE)
      };
   }

   public static AttributeContainer[] paragonTier(Holder<Attribute> school) {
      return new AttributeContainer[]{
         new AttributeContainer(school, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] paragonTierMulti(Holder<Attribute> school1, Holder<Attribute> school2) {
      return new AttributeContainer[]{
         new AttributeContainer(school1, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] paragonTierTri(Holder<Attribute> school1, Holder<Attribute> school2, Holder<Attribute> school3) {
      return new AttributeContainer[]{
         new AttributeContainer(school1, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school3, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers() {
      return this.defaultModifiers.get();
   }

   public List<Entry> createExtraAttributes() {
      return List.of();
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      ItemAttributeModifiers modifiers = super.getDefaultAttributeModifiers(stack);
      Builder builder = ItemAttributeModifiers.builder();

      for (Entry entry : modifiers.modifiers()) {
         builder.add(entry.attribute(), entry.modifier(), entry.slot());
      }

      for (Entry entry : this.createExtraAttributes()) {
         builder.add(entry.attribute(), entry.modifier(), entry.slot());
      }

      return builder.build();
   }
}
