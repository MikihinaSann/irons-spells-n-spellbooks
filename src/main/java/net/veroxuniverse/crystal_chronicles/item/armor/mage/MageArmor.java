package net.veroxuniverse.crystal_chronicles.item.armor.mage;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.veroxuniverse.crystal_chronicles.item.armor.AnimatedSpellArmor;

public class MageArmor extends AnimatedSpellArmor {
   public MageArmor(Holder<ArmorMaterial> holder, Type type, Properties properties) {
      super(holder, type, properties, schoolAttributes(AttributeRegistry.ENDER_SPELL_POWER));
   }
}
