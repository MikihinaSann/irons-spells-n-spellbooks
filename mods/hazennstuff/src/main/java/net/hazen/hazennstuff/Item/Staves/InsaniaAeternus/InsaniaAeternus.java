package net.hazen.hazennstuff.Item.Staves.InsaniaAeternus;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.hazen.hazennstuff.Animations.HnSDispatcher;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSStaffTier;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class InsaniaAeternus extends StaffItem {
   public final HnSDispatcher dispatcher = new HnSDispatcher();

   public InsaniaAeternus() {
      super(
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(HnSStaffTier.INSANIA_AETERNUS))
      );
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (!level.isClientSide && entity instanceof Player player) {
         this.dispatcher.idle(player, stack);
      }
   }
}
