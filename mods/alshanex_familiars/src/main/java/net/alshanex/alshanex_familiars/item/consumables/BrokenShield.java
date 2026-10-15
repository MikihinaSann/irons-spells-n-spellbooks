package net.alshanex.alshanex_familiars.item.consumables;

import java.util.List;
import net.alshanex.familiarslib.registry.ComponentRegistry;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableComponent;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableSystem.ConsumableType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class BrokenShield extends Item {
   public BrokenShield() {
      super(
         new Properties()
            .stacksTo(1)
            .component((DataComponentType)ComponentRegistry.FAMILIAR_CONSUMABLE.get(), new FamiliarConsumableComponent(ConsumableType.BLOCKING, 1))
      );
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.translatable("ui.alshanex_familiars.consumable_item.shield"));
   }
}
