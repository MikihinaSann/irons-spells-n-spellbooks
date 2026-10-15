package net.hazen.hazennstuff.Item.Armor.Geckolib.NecromancerArmor;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class NecromancerArmorItem extends ImbuableChestplateArmorItem implements IDisableJacket {
   public NecromancerArmorItem(Type slot, Properties settings) {
      super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(AttributeRegistry.BLOOD_SPELL_POWER));
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return HazennstuffClientRenderers.necromancerArmorItem(); }
}
