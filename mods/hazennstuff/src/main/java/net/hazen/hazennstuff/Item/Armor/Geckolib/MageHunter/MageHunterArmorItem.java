package net.hazen.hazennstuff.Item.Armor.Geckolib.MageHunter;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableGeckolibHnSArmorItem;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MageHunterArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   public MageHunterArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.MAGEHUNTER_MATERIAL,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.125, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, -0.2, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, -0.1, Operation.ADD_MULTIPLIED_BASE)
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.mageHunterArmorItem(); }
}
