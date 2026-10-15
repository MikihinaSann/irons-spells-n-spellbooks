package net.hazen.hazennstuff.Item.Armor.Geckolib.PyromancerBrute;
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

public class PyromancerBruteArmorItem extends ImbuableChestplateArmorItem implements IDisableJacket {
   public PyromancerBruteArmorItem(Type slot, Properties settings) {
      super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(AttributeRegistry.FIRE_SPELL_POWER));
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return HazennstuffClientRenderers.pyromancerBruteArmorItem(); }
}
