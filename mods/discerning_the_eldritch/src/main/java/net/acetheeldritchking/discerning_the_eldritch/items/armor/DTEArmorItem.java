package net.acetheeldritchking.discerning_the_eldritch.items.armor;

import com.google.common.base.Suppliers;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.function.Supplier;
import net.acetheeldritchking.discerning_the_eldritch.items.custom.DTEItemDispatcher;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTETags;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.level.Level;

public class DTEArmorItem extends ArmorItem {
   private final Supplier<ItemAttributeModifiers> defaultModifiers;
   public final DTEItemDispatcher dispatcher = new DTEItemDispatcher();

   public DTEArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, AttributeContainer... attributeContainers) {
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

   public static AttributeContainer[] schoolAttributes(Holder<Attribute> school, int mana, float schoolSpellPower, float spellPower) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, mana, Operation.ADD_VALUE),
         new AttributeContainer(school, schoolSpellPower, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, spellPower, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] schoolAttributesWithResistance(
      Holder<Attribute> school, Holder<Attribute> resistSchool, int mana, float schoolSpellPower, float spellPower, float resistSpellPower
   ) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, mana, Operation.ADD_VALUE),
         new AttributeContainer(school, schoolSpellPower, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(resistSchool, resistSpellPower, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, spellPower, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers() {
      return this.defaultModifiers.get();
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (!level.isClientSide && entity instanceof Player player) {
         player.getArmorSlots().forEach(wornArmor -> {
            if (wornArmor != null && wornArmor.is(DTETags.ARMORS_FOR_IDLE)) {
               this.dispatcher.idle(player, wornArmor);
            }
         });
      }
   }
}
