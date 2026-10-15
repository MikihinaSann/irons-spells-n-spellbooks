package net.alshanex.alshanex_familiars.item.armor;
import net.alshanex.alshanex_familiars.AlshanexFamiliarsClientRenderers;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.alshanex.alshanex_familiars.entity.armor.BardArmorModel;
import net.alshanex.familiarslib.registry.AttributeRegistry;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BardArmorItem extends ImbuableChestplateArmorItem {
   public BardArmorItem(Type slot, Properties settings) {
      super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(AttributeRegistry.SOUND_SPELL_POWER));
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return AlshanexFamiliarsClientRenderers.bardArmorItem(); }
}
