package net.hazen.hazennstuff.Item.Armor.Geckolib.BountyHunter;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
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

public class BountyHunterArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   public BountyHunterArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.ARCHER_MATERIAL,
         type,
         settings,
         new AttributeContainer(Attributes.ARROW_DAMAGE, 0.1, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(Attributes.ARROW_VELOCITY, 0.1, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.25, Operation.ADD_MULTIPLIED_TOTAL)
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.bountyHunterArmorItem(); }
}
