package net.warphan.iss_magicfromtheeast.item.armor;
import net.warphan.iss_magicfromtheeast.IssMagicfromtheeastClientRenderers;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.warphan.iss_magicfromtheeast.entity.armor.TaoistArmorModel;
import net.warphan.iss_magicfromtheeast.registries.MFTEAttributeRegistries;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class TaoistArmorItem extends ImbuableChestplateArmorItem {
   public TaoistArmorItem(Type slot, Properties settings) {
      super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(MFTEAttributeRegistries.SYMMETRY_SPELL_POWER));
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return IssMagicfromtheeastClientRenderers.taoistArmorItem(); }
}
