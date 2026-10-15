package net.hazen.hazennstuff.Item.Armor.Geckolib.MithrilBattlemage;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableGeckolibHnSArmorItem;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MithrilBattlemageArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   public MithrilBattlemageArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.ADVANCED_BATTLEMAGE_MATERIAL,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.1, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.1, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.1, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_VALUE)
      );
   }

   @Override
   public List<Entry> createExtraAttributes() {
      EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.getEquipmentSlot());
      Builder attributes = ItemAttributeModifiers.builder();
      MalumCompat.addScytheProfeciency(attributes, group);
      return attributes.build().modifiers();
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.mithrilBattlemageArmorItem(); }
}
