package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.init.ECEnchantments;
import com.userofbricks.expanded_combat.item.ArrowBlockWeaponItem;
import com.userofbricks.expanded_combat.item.GauntletItem;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class EnchantentEvents {
   public void HealthStealEvent(Pre event) {
      DamageSource damageSource = event.getSource();
      if (damageSource.isDirect()
         && damageSource.getEntity() instanceof LivingEntity livingEntity
         && (damageSource.is(DamageTypes.GENERIC) || damageSource.is(DamageTypes.GENERIC_KILL))
         && livingEntity.getItemInHand(livingEntity.getUsedItemHand()).getItem() instanceof ArrowBlockWeaponItem) {
         ItemStack weapon = livingEntity.getItemInHand(livingEntity.getUsedItemHand());
         int var5 = weapon.getEnchantmentLevel(
            event.getEntity().level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(ECEnchantments.BLOCKING)
         );
      }
   }

   @SubscribeEvent
   public static void miningSpeed(BreakSpeed event) {
      Optional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(event.getEntity());
      if (handlerOptional.isPresent()) {
         float speedIncrease = 0.0F;
         Registry<Enchantment> enchantmentRegistry = event.getEntity().level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);

         for (SlotResult slotResult : handlerOptional.get().findCurios(stack -> stack.getItem() instanceof GauntletItem)) {
            speedIncrease += slotResult.stack().getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.AGILITY)) * 0.2F;
         }

         event.setNewSpeed(event.getOriginalSpeed() - speedIncrease);
      }
   }

   @SubscribeEvent
   public static void agilityDoge(LivingIncomingDamageEvent event) {
      LivingEntity entity = event.getEntity();
      ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
      Registry<Enchantment> enchantmentRegistry = event.getEntity().level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
      int agility = chestplate.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.AGILITY));
      if (!chestplate.isEmpty()
         && agility > 0
         && entity.getRandom().nextIntBetweenInclusive(1, Math.round(20 + agility - 10.0F * ((float)Math.sqrt(agility) - 1.0F))) == 1) {
         float move = 1.0F / agility;
         double xMove = entity.getRandom().nextInt(0, 1) == 1 ? -1.0 : 1.0;
         double yMove = entity.getRandom().nextInt(0, 1) == 1 ? -1.0 : 1.0;
         entity.addDeltaMovement(new Vec3(xMove * move, 0.5, yMove * move));
         event.setCanceled(true);
      }
   }
}
