package com.userofbricks.expanded_combat.item;

import com.userofbricks.expanded_combat.api.material.Material;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ECTippedArrowItem extends ECArrowItem {
   private final ItemLike notTipped;

   public ECTippedArrowItem(Properties properties, Material material, ItemLike notTipped) {
      super(properties, material);
      this.notTipped = notTipped;
   }

   public ItemStack getDefaultInstance() {
      ItemStack itemstack = super.getDefaultInstance();
      itemstack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON));
      return itemstack;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
      PotionContents potioncontents = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
      if (potioncontents != null) {
         potioncontents.addPotionTooltip(tooltip::add, 0.125F, context.tickRate());
      }
   }

   public Component getName(ItemStack stack) {
      return Component.translatable(this.getDescriptionId(stack)).append(" ").append(Component.translatable(this.getPotionId(stack)));
   }

   public String getPotionId(ItemStack stack) {
      return Potion.getName(
         ((PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion(), "arrow.expanded_combat.effect."
      );
   }

   public Item getNotTipped() {
      return this.notTipped.asItem();
   }
}
