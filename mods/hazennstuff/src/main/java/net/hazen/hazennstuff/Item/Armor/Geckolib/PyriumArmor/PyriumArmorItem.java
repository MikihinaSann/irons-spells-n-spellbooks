package net.hazen.hazennstuff.Item.Armor.Geckolib.PyriumArmor;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSGeckolibArmorItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PyriumArmorItem extends HnSGeckolibArmorItem implements IDisableJacket, IDisableHat {
   public PyriumArmorItem(Type type, Properties settings) {
      super(HnSArmorMaterials.PYRIUM_MATERIAL, type, settings);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.pyriumArmorItem(); }
}
