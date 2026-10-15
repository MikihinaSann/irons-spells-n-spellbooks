package com.somake.somakespells.item;

import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

public abstract class ElementalChargeNecklaceItem extends SimpleDescriptiveCurio {
   private static final String CHARGES_KEY = "elemental_charges";
   private static final String MAX_CHARGES_KEY = "elemental_max_charges";

   protected ElementalChargeNecklaceItem() {
      super(new Properties().stacksTo(1), "necklace");
   }

   public abstract int getMaxCharges();

   protected abstract int getBarColorValue();

   public int getCurrentCharges(ItemStack stack) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      int maxCharges = this.getStackMaxCharges(stack);
      return tag.contains("elemental_charges") ? Math.max(0, Math.min(maxCharges, tag.getInt("elemental_charges"))) : maxCharges;
   }

   public int getStackMaxCharges(ItemStack stack) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      return tag.contains("elemental_max_charges") ? Math.max(1, tag.getInt("elemental_max_charges")) : Math.max(1, this.getMaxCharges());
   }

   public boolean hasCharges(ItemStack stack) {
      return this.getCurrentCharges(stack) > 0;
   }

   public void recharge(ItemStack stack) {
      int configuredMax = Math.max(1, this.getMaxCharges());
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
         tag.putInt("elemental_max_charges", configuredMax);
         tag.remove("elemental_charges");
      });
   }

   public boolean consumeCharge(ItemStack stack) {
      this.synchronizeCapacity(stack);
      int current = this.getCurrentCharges(stack);
      if (current <= 0) {
         return false;
      } else {
         this.setCurrentCharges(stack, current - 1);
         return true;
      }
   }

   public void setCurrentCharges(ItemStack stack, int charges) {
      int maxCharges = this.getStackMaxCharges(stack);
      int clamped = Math.max(0, Math.min(maxCharges, charges));
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
         tag.putInt("elemental_max_charges", maxCharges);
         if (clamped >= maxCharges) {
            tag.remove("elemental_charges");
         } else {
            tag.putInt("elemental_charges", clamped);
         }
      });
   }

   private void synchronizeCapacity(ItemStack stack) {
      int configuredMax = Math.max(1, this.getMaxCharges());
      CompoundTag currentTag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      int previousMax = currentTag.contains("elemental_max_charges") ? Math.max(1, currentTag.getInt("elemental_max_charges")) : configuredMax;
      boolean hasStoredCurrent = currentTag.contains("elemental_charges");
      int current = hasStoredCurrent ? Math.max(0, Math.min(previousMax, currentTag.getInt("elemental_charges"))) : previousMax;
      int migratedCurrent = hasStoredCurrent ? Math.min(current, configuredMax) : configuredMax;
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
         tag.putInt("elemental_max_charges", configuredMax);
         if (migratedCurrent >= configuredMax) {
            tag.remove("elemental_charges");
         } else {
            tag.putInt("elemental_charges", migratedCurrent);
         }
      });
   }

   public boolean isBarVisible(ItemStack stack) {
      return true;
   }

   public int getBarWidth(ItemStack stack) {
      return Math.max(1, Math.round(13.0F * ((float)this.getCurrentCharges(stack) / this.getStackMaxCharges(stack))));
   }

   public int getBarColor(ItemStack stack) {
      return this.getBarColorValue();
   }

   public List<Component> getDescriptionLines(ItemStack stack) {
      List<Component> lines = new ArrayList<>();
      lines.add(this.getDescription(stack));
      lines.add(
         Component.literal(" ")
            .append(
               Component.translatable(
                  "item.somakespells.elemental_necklace.charges", new Object[]{this.getCurrentCharges(stack), this.getStackMaxCharges(stack)}
               )
            )
            .withStyle(ChatFormatting.AQUA)
      );
      if (!this.hasCharges(stack)) {
         lines.add(Component.literal(" ").append(Component.translatable("item.somakespells.elemental_necklace.empty")).withStyle(ChatFormatting.RED));
      }

      return lines;
   }

   public static Optional<ItemStack> findEquippedStack(@Nullable LivingEntity entity, Item item) {
      return entity == null ? Optional.empty() : CuriosApi.getCuriosHelper().findFirstCurio(entity, item).map(slotResult -> slotResult.stack());
   }

   public static Optional<ItemStack> findChargedEquippedStack(@Nullable LivingEntity entity, Item item) {
      return findEquippedStack(entity, item).filter(stack -> stack.getItem() instanceof ElementalChargeNecklaceItem necklace && necklace.hasCharges(stack));
   }

   public static void rechargeIfElementalNecklace(ItemStack stack) {
      if (stack.getItem() instanceof ElementalChargeNecklaceItem necklace) {
         necklace.recharge(stack);
      }
   }
}
