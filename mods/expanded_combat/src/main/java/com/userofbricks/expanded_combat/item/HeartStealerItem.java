package com.userofbricks.expanded_combat.item;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.init.DataAttachments;
import com.userofbricks.expanded_combat.init.ECBasePlugin;
import com.userofbricks.expanded_combat.init.ItemDataComponents;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@EventBusSubscriber
public class HeartStealerItem extends ECWeaponItem {
   int tick = 0;
   public static ResourceLocation IncreasedHeartSteelerDmg = ExpandedCombat.modLoc("heart_stealer_dmg");

   public HeartStealerItem(Properties properties) {
      super(ECBasePlugin.HEART_STEALER, ECBasePlugin.CLAYMORE, properties.component(ItemDataComponents.CHARGE, 0), 2);
   }

   @Override
   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (this.getMaxDamage(stack) - this.getDamage(stack) <= 1) {
         return false;
      } else {
         int charge = (Integer)stack.getOrDefault(ItemDataComponents.CHARGE, 0);
         if (charge >= 500
            && target.getMaxHealth() >= this.getDamage()
            && attacker.level()
                  .random
                  .nextInt(
                     (int)Math.round(
                           Math.sqrt(
                              ((Integer)attacker.getData(DataAttachments.ADDED_HEALTH)).intValue()
                                 + Math.pow(((Integer)attacker.getData(DataAttachments.STOLEN_HEALTH)).intValue(), 3.0)
                           )
                        )
                        + 1
                  )
               <= 1) {
            stack.set(ItemDataComponents.CHARGE, 0);
            attacker.setData(DataAttachments.STOLEN_HEALTH, (Integer)attacker.getData(DataAttachments.STOLEN_HEALTH) + 1);
            if (target instanceof Player && target.getMaxHealth() + ((Integer)target.getData(DataAttachments.ADDED_HEALTH)).intValue() > 10.0F) {
               attacker.setData(DataAttachments.ADDED_HEALTH, (Integer)attacker.getData(DataAttachments.ADDED_HEALTH) - 1);
            }
         } else {
            stack.set(ItemDataComponents.CHARGE, charge + 1);
         }

         return super.hurtEnemy(stack, target, attacker);
      }
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      ItemStack stack = pPlayer.getItemInHand(pUsedHand);
      return this.getMaxDamage(stack) - this.getDamage(stack) <= 1 ? InteractionResultHolder.fail(stack) : super.use(pLevel, pPlayer, pUsedHand);
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int pi, boolean pb) {
      super.inventoryTick(stack, level, entity, pi, pb);
      if (entity instanceof Player player && this.tick % 5 == 0) {
         int stolenHearts = (Integer)player.getData(DataAttachments.STOLEN_HEALTH);
         int addedDmg = stolenHearts / 5;
         Builder builder = ItemAttributeModifiers.builder();
         ItemAttributeModifiers itemMods = (ItemAttributeModifiers)stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
         if (itemMods != null) {
            for (Entry entry : itemMods.modifiers()) {
               if (!entry.matches(Attributes.ATTACK_DAMAGE, IncreasedHeartSteelerDmg)) {
                  builder.add(entry.attribute(), entry.modifier(), entry.slot());
               }
            }
         }

         builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(IncreasedHeartSteelerDmg, addedDmg, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
         stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
      }

      this.tick++;
   }

   @SubscribeEvent
   public static void onItemExpire(ItemExpireEvent event) {
      if (event.getEntity().getItem().getItem() instanceof HeartStealerItem) {
         event.addExtraLife(12000);
      }
   }
}
