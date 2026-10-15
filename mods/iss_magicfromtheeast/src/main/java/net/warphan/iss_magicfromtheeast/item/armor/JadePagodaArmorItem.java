package net.warphan.iss_magicfromtheeast.item.armor;
import net.warphan.iss_magicfromtheeast.IssMagicfromtheeastClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.warphan.iss_magicfromtheeast.entity.armor.JadePagodaArmorModel;
import net.warphan.iss_magicfromtheeast.registries.MFTEArmorMaterialRegistries;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class JadePagodaArmorItem extends ImbuableChestplateArmorItem {
   public JadePagodaArmorItem(Type slot, Properties properties) {
      super(
         MFTEArmorMaterialRegistries.JADE,
         slot,
         properties,
         new AttributeContainer[]{
            new AttributeContainer(Attributes.MOVEMENT_SPEED, -0.025, Operation.ADD_MULTIPLIED_BASE),
            new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
            new AttributeContainer(AttributeRegistry.MAX_MANA, 50.0, Operation.ADD_VALUE)
         }
      );
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return IssMagicfromtheeastClientRenderers.jadePagodaArmorItem(); }
}
