package net.warphan.iss_magicfromtheeast.item.armor;
import net.warphan.iss_magicfromtheeast.IssMagicfromtheeastClientRenderers;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.warphan.iss_magicfromtheeast.entity.armor.JiangshiHatModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class JiangshiHatItem extends ExtendedArmorItem {
   public JiangshiHatItem(Type slot, Properties settings) {
      super(ArmorMaterials.LEATHER, slot, settings, new AttributeContainer[]{new AttributeContainer(Attributes.JUMP_STRENGTH, 0.2, Operation.ADD_VALUE)});
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return IssMagicfromtheeastClientRenderers.jiangshiHatItem(); }
}
