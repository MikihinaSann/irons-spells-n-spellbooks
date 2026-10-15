package net.alshanex.alshanex_familiars.item.curios;

import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.util.CurioUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

public class EvasionCurio extends SimpleDescriptiveCurio {
   public EvasionCurio(Properties properties) {
      super(properties.rarity(Rarity.EPIC), Curios.RING_SLOT);
   }

   private void handleEvasion(SlotContext slotContext) {
      LivingEntity entity = slotContext.entity();
      if (entity != null && !slotContext.entity().level.isClientSide && CurioUtils.isWearingCurio(slotContext.entity(), (Item)ItemRegistry.EVASION_CURIO.get())
         )
       {
         this.addEvasion(slotContext.entity().level, slotContext.entity());
      }
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      super.curioTick(slotContext, stack);
      if (slotContext.entity().tickCount % 100 == 0) {
         this.handleEvasion(slotContext);
      }
   }

   private void addEvasion(Level level, LivingEntity player) {
      for (Entity entity : level.getEntities(
         player,
         player.getBoundingBox().inflate(20.0),
         entityx -> entityx instanceof AbstractSpellCastingPet petx && petx.getSummoner() != null && petx.getSummoner().is(player)
      )) {
         AbstractSpellCastingPet pet = (AbstractSpellCastingPet)entity;
         if (pet.getHealth() <= pet.getMaxHealth() / 4.0F) {
            pet.addEffect(new MobEffectInstance(MobEffectRegistry.EVASION, 100, 2, false, false, true));
         }
      }
   }
}
