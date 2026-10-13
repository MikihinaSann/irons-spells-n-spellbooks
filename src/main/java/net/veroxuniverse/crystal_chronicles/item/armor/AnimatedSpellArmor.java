package net.veroxuniverse.crystal_chronicles.item.armor;

import com.google.common.base.Suppliers;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import java.util.function.Supplier;
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

public class AnimatedSpellArmor extends ArmorItem implements IPresetSpellContainer {
   private final Supplier<ItemAttributeModifiers> defaultModifiers;

   public AnimatedSpellArmor(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties, AttributeContainer... attributes) {
      super(pMaterial, pType, pProperties);
      this.defaultModifiers = Suppliers.memoize(() -> {
         int i = pMaterial.value().getDefense(pType);
         float f = pMaterial.value().toughness();
         Builder builder = ItemAttributeModifiers.builder();
         EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(pType.getSlot());
         ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + pType.getName());
         builder.add(Attributes.ARMOR, new AttributeModifier(resourcelocation, (double)i, Operation.ADD_VALUE), equipmentslotgroup);
         builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourcelocation, (double)f, Operation.ADD_VALUE), equipmentslotgroup);
         float f1 = pMaterial.value().knockbackResistance();
         if (f1 > 0.0F) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(resourcelocation, (double)f1, Operation.ADD_VALUE), equipmentslotgroup);
         }

         for (AttributeContainer holder : attributes) {
            builder.add(holder.attribute(), holder.createModifier(pType.getSlot().getName()), equipmentslotgroup);
         }

         return builder.build();
      });
   }

   public static AttributeContainer[] schoolAttributes(Holder<Attribute> school) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
         new AttributeContainer(school, 0.15, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] tankSchoolAttributes(Holder<Attribute> school) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
         new AttributeContainer(school, 0.15, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] mixedSchoolAttributes(Holder<Attribute> school, Holder<Attribute> school2) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
         new AttributeContainer(school, 0.15, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.05, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] withManaAttribute(int mana) {
      return new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, (double)mana, Operation.ADD_VALUE)};
   }

   public static AttributeContainer[] withManaAndSpellPowerAttribute(int mana, double spellPower) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, (double)mana, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, spellPower, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   @Override
   public ItemAttributeModifiers getDefaultAttributeModifiers() {
      return this.defaultModifiers.get();
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getType() == Type.CHESTPLATE && !ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer spellContainer = ISpellContainer.create(1, true, true);
            itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer);
         }
      }
   }
}
