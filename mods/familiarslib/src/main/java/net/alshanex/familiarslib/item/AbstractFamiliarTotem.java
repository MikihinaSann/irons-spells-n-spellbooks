package net.alshanex.familiarslib.item;

import java.util.List;
import java.util.UUID;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.registry.ComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFamiliarTotem extends Item {
   public AbstractFamiliarTotem(Properties properties) {
      super(properties);
   }

   @NotNull
   public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
      if (!this.isValidPet(target)) {
         return InteractionResult.FAIL;
      } else if (target instanceof AbstractSpellCastingPet pet && pet.getTotem()) {
         return InteractionResult.FAIL;
      } else if (!this.bind(stack, target, playerIn)) {
         return InteractionResult.FAIL;
      } else {
         playerIn.swing(hand);
         playerIn.setItemInHand(hand, stack);
         return InteractionResult.SUCCESS;
      }
   }

   protected boolean isValidPet(LivingEntity target) {
      return target instanceof AbstractSpellCastingPet;
   }

   public boolean bind(ItemStack stack, LivingEntity target, Player player) {
      if (target.getCommandSenderWorld().isClientSide) {
         return false;
      } else if (!(target instanceof AbstractSpellCastingPet pet && pet.getSummoner() != null && pet.getSummoner().is(player))) {
         return false;
      } else if (stack.has(ComponentRegistry.SOUL_LINK)) {
         CompoundTag nbt = (CompoundTag)stack.get(ComponentRegistry.SOUL_LINK);
         UUID petUUID = nbt.getUUID("petUUID");
         stack.remove(ComponentRegistry.SOUL_LINK);
         if (!target.getUUID().equals(petUUID)) {
            CompoundTag nbtSwap = new CompoundTag();
            nbtSwap.putUUID("petUUID", target.getUUID());
            if (target.hasCustomName()) {
               nbtSwap.putString("name", target.getCustomName().getString());
            }

            nbtSwap.putString("entity", EntityType.getKey(target.getType()).toString());
            target.saveWithoutId(nbtSwap);
            stack.set(ComponentRegistry.SOUL_LINK, nbtSwap);
         }

         if (target instanceof AbstractSpellCastingPet familiar) {
            familiar.setTotem(true);
         }

         return true;
      } else {
         CompoundTag nbtx = new CompoundTag();
         nbtx.putUUID("petUUID", target.getUUID());
         if (target.hasCustomName()) {
            nbtx.putString("name", target.getCustomName().getString());
         }

         nbtx.putString("entity", EntityType.getKey(target.getType()).toString());
         target.saveWithoutId(nbtx);
         stack.set(ComponentRegistry.SOUL_LINK, nbtx);
         if (target instanceof AbstractSpellCastingPet familiar) {
            familiar.setTotem(true);
         }

         return true;
      }
   }

   public String getID(ItemStack stack) {
      CompoundTag nbt = (CompoundTag)stack.get(ComponentRegistry.SOUL_LINK);
      return nbt.contains("name") ? nbt.getString("name") : nbt.getString("entity");
   }

   public Component getName(ItemStack stack) {
      return !this.containsEntity(stack)
         ? Component.translatable(super.getDescriptionId(stack))
         : Component.translatable(super.getDescriptionId(stack)).append(" (" + this.getID(stack) + ")");
   }

   public boolean containsEntity(ItemStack stack) {
      return !stack.isEmpty() && stack.has(ComponentRegistry.SOUL_LINK);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.translatable("tooltip.familiarslib.totem").withStyle(ChatFormatting.WHITE));
   }
}
