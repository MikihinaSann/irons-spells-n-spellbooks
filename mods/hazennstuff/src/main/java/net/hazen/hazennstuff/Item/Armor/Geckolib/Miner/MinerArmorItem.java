package net.hazen.hazennstuff.Item.Armor.Geckolib.Miner;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MinerArmorItem extends ImbuableChestplateArmorItem implements IDisableJacket {
   public MinerArmorItem(Type type, Properties settings) {
      super(
         ArmorMaterials.DIAMOND,
         type,
         settings,
         new AttributeContainer[]{
            new AttributeContainer(Attributes.MINING_EFFICIENCY, 0.05, Operation.ADD_VALUE),
            new AttributeContainer(Attributes.SUBMERGED_MINING_SPEED, 0.05, Operation.ADD_VALUE)
         }
      );
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return HazennstuffClientRenderers.minerArmorItem(); }
}
