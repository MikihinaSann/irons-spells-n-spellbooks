package com.somake.somakespells.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.level.Level;

public class ToggleableSpellbladeItem extends SwordItem {
   private static final String ACTIVE_KEY = "saber_active";
   private final int activeAttackDamageBonus;
   private final int inactiveAttackDamageBonus;
   private final float attackSpeedModifier;
   private final double entityInteractionRange;
   private final Holder<Attribute> extraAttribute;
   private final ResourceLocation modifierId;
   private final double extraAmount;

   public ToggleableSpellbladeItem(
      Tier tier,
      int activeAttackDamageBonus,
      int inactiveAttackDamageBonus,
      float attackSpeedModifier,
      Properties properties,
      Holder<Attribute> extraAttribute,
      ResourceLocation modifierId,
      double extraAmount
   ) {
      this(tier, activeAttackDamageBonus, inactiveAttackDamageBonus, attackSpeedModifier, properties, 3.0, extraAttribute, modifierId, extraAmount);
   }

   public ToggleableSpellbladeItem(
      Tier tier,
      int activeAttackDamageBonus,
      int inactiveAttackDamageBonus,
      float attackSpeedModifier,
      Properties properties,
      double entityInteractionRange,
      Holder<Attribute> extraAttribute,
      ResourceLocation modifierId,
      double extraAmount
   ) {
      super(tier, properties);
      this.activeAttackDamageBonus = activeAttackDamageBonus;
      this.inactiveAttackDamageBonus = inactiveAttackDamageBonus;
      this.attackSpeedModifier = attackSpeedModifier;
      this.entityInteractionRange = entityInteractionRange;
      this.extraAttribute = extraAttribute;
      this.modifierId = modifierId;
      this.extraAmount = extraAmount;
   }

   public static boolean isActive(ItemStack stack) {
      CustomData customData = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      return customData.copyTag().getBoolean("saber_active");
   }

   public static void setActive(ItemStack stack, boolean active) {
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
         if (active) {
            tag.putBoolean("saber_active", true);
         } else {
            tag.remove("saber_active");
         }
      });
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      boolean active = !isActive(stack);
      setActive(stack, active);
      SoundEvent sound = active ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE;
      level.playSound(player, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 0.8F, active ? 1.15F : 0.9F);
      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
   }

   public boolean isFoil(ItemStack stack) {
      return false;
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      Builder builder = ItemAttributeModifiers.builder();
      int attackDamageBonus = isActive(stack) ? this.activeAttackDamageBonus : this.inactiveAttackDamageBonus;
      builder.add(
         Attributes.ATTACK_DAMAGE,
         new AttributeModifier(ResourceLocation.withDefaultNamespace("base_attack_damage"), attackDamageBonus, Operation.ADD_VALUE),
         EquipmentSlotGroup.MAINHAND
      );
      builder.add(
         Attributes.ATTACK_SPEED,
         new AttributeModifier(ResourceLocation.withDefaultNamespace("base_attack_speed"), this.attackSpeedModifier, Operation.ADD_VALUE),
         EquipmentSlotGroup.MAINHAND
      );
      double rangeDelta = this.entityInteractionRange - 3.0;
      if (Math.abs(rangeDelta) > 1.0E-6) {
         builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(this.modifierId, rangeDelta, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      }

      if (this.extraAttribute != null && Math.abs(this.extraAmount) > 1.0E-6) {
         builder.add(this.extraAttribute, new AttributeModifier(this.modifierId, this.extraAmount, Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
      }

      return builder.build();
   }
}
