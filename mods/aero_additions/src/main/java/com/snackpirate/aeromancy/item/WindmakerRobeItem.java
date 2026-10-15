package com.snackpirate.aeromancy.item;
import com.snackpirate.aeromancy.AeroAdditionsClientRenderers;

import com.snackpirate.aeromancy.entity.armor.WindmakerArmorModel;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WindmakerRobeItem extends ImbuableChestplateArmorItem {
   public WindmakerRobeItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties, AttributeContainer... attributes) {
      super(pMaterial, pType, pProperties, attributes);
   }

   public WindmakerRobeItem(Type slot, Properties settings) {
      super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(AASpells.Attributes.WIND_SPELL_POWER));
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return AeroAdditionsClientRenderers.windmakerRobeItem(); }
}
