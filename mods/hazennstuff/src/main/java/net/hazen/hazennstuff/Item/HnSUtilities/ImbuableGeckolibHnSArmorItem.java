package net.hazen.hazennstuff.Item.HnSUtilities;

import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class ImbuableGeckolibHnSArmorItem extends HnSGeckolibArmorItem implements IPresetSpellContainer {
   public ImbuableGeckolibHnSArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, AttributeContainer... attributeContainers) {
      super(material, type, properties, attributeContainers);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getType() == Type.CHESTPLATE && !ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer spellContainer = ISpellContainer.create(1, true, true);
            itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer);
         }
      }
   }
}
