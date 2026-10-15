package net.warphan.iss_magicfromtheeast.item.consumables;

import io.redspace.ironsspellbooks.item.consumables.DrinkableItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;

public class RiceWineBottleItem extends DrinkableItem {
   boolean foilOverride;

   public RiceWineBottleItem(Properties properties) {
      super(properties, (itemstack, livingEntity) -> {
         livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 3, false, true, true));
         livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 900, 1, false, true, true));
         livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 900, 0, false, true, true));
      }, Items.BOWL, false);
   }
}
