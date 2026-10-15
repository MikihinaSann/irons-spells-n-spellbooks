package net.warphan.iss_magicfromtheeast.item.armor;
import net.warphan.iss_magicfromtheeast.IssMagicfromtheeastClientRenderers;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.warphan.iss_magicfromtheeast.entity.armor.OnmyojiArmorModel;
import net.warphan.iss_magicfromtheeast.registries.MFTEAttributeRegistries;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class OnmyojiArmorItem extends ImbuableChestplateArmorItem {
   public OnmyojiArmorItem(Type slot, Properties setting) {
      super(ArmorMaterialRegistry.SCHOOL, slot, setting, schoolAttributes(MFTEAttributeRegistries.SPIRIT_SPELL_POWER));
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return IssMagicfromtheeastClientRenderers.onmyojiArmorItem(); }
}
