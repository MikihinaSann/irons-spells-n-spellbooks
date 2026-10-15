package net.hazen.hazennstuff.Spells.AbstractSpells;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public abstract class AbstractTaggedSpell extends AbstractSpell {
   public boolean hasTaggedItem(LivingEntity entity, TagKey<Item> tag) {
      if (!entity.getMainHandItem().is(tag) && !entity.getOffhandItem().is(tag)) {
         for (ItemStack armor : entity.getArmorSlots()) {
            if (armor.is(tag)) {
               return true;
            }
         }

         return CuriosApi.getCuriosInventory(entity).map(curios -> !curios.findCurios(item -> item != null && item.is(tag)).isEmpty()).orElse(false);
      } else {
         return true;
      }
   }

   public boolean hasItem(LivingEntity entity, Item item) {
      return entity.getMainHandItem().is(item) || entity.getOffhandItem().is(item);
   }
}
