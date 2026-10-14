package com.gametechbc.traveloptics.api.item;

import com.gametechbc.traveloptics.api.TODataComponents;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

public abstract class AbstractWildAugment extends HeavyAttributeCurio {
   private static final int EXPERIENCE_COST = 250;

   public AbstractWildAugment(Properties properties) {
      super(properties, Map.of());
   }

   protected abstract List<AbstractWildAugment.AttributeEntry> getPositiveAttributeEntries();

   protected abstract List<AbstractWildAugment.AttributeEntry> getNegativeAttributeEntries();

   protected abstract List<AbstractWildAugment.AttributeEntry> getChanceAttributeEntries();

   protected abstract double getGlobalChance();

   protected abstract SoundEvent getAssignSound();

   protected abstract Component getInitialHoverText();

   protected abstract Component getAssignedHoverText();

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
      if (stack.has(TODataComponents.ECHO_CURIO_COMPONENT)) {
         CompoundTag itemTag = (CompoundTag)stack.get(TODataComponents.ECHO_CURIO_COMPONENT);
         if (itemTag.getBoolean("attributesAssigned")) {
            tooltip.add(this.getAssignedHoverText());
         } else {
            tooltip.add(this.getInitialHoverText());
         }
      }
   }

   @Override
   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      if (stack.has(TODataComponents.ECHO_CURIO_COMPONENT)) {
         CompoundTag itemTag = (CompoundTag)stack.get(TODataComponents.ECHO_CURIO_COMPONENT);
         return this.getAttributesFromNBT(itemTag);
      } else {
         return ImmutableMultimap.of();
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (stack.has(TODataComponents.ECHO_CURIO_COMPONENT)) {
         CompoundTag itemTag = (CompoundTag)stack.get(TODataComponents.ECHO_CURIO_COMPONENT);
         if (!itemTag.getBoolean("attributesAssigned")) {
            if (player.totalExperience >= 250) {
               this.assignRandomAttributes(stack);
               itemTag.putBoolean("attributesAssigned", true);
               stack.set(TODataComponents.ECHO_CURIO_COMPONENT, itemTag);
               player.giveExperiencePoints(-250);
               this.playSound(level, player, this.getAssignSound());
               if (!level.isClientSide) {
                  player.displayClientMessage(Component.translatable("item.traveloptics.augment_wild.assigned.success"), true);
               }

               return InteractionResultHolder.success(stack);
            }

            if (!level.isClientSide) {
               player.displayClientMessage(Component.translatable("item.traveloptics.augment_wild.assigned.failed"), true);
            }

            return InteractionResultHolder.fail(stack);
         }
      }

      return InteractionResultHolder.pass(stack);
   }

   private void assignRandomAttributes(ItemStack stack) {
      List<AbstractWildAugment.AttributeEntry> possiblePositiveAttributes = this.getPositiveAttributeEntries();
      List<AbstractWildAugment.AttributeEntry> possibleNegativeAttributes = this.getNegativeAttributeEntries();
      List<AbstractWildAugment.AttributeEntry> possibleChanceAttributes = this.getChanceAttributeEntries();
      if (!possiblePositiveAttributes.isEmpty()) {
         AbstractWildAugment.AttributeEntry selectedPositiveEntry = this.selectRandomAttributeEntry(possiblePositiveAttributes);
         double positiveValue = this.selectRandomValue(selectedPositiveEntry.getMinValue(), selectedPositiveEntry.getMaxValue());
         this.saveAttributeToComponent(stack, selectedPositiveEntry.getAttribute(), selectedPositiveEntry.getOperation(), positiveValue, "positiveAttribute");
      }

      if (!possibleNegativeAttributes.isEmpty()) {
         AbstractWildAugment.AttributeEntry selectedNegativeEntry = this.selectRandomAttributeEntry(possibleNegativeAttributes);
         double negativeValue = this.selectRandomValue(selectedNegativeEntry.getMinValue(), selectedNegativeEntry.getMaxValue());
         this.saveAttributeToComponent(stack, selectedNegativeEntry.getAttribute(), selectedNegativeEntry.getOperation(), negativeValue, "negativeAttribute");
      }

      Random random = new Random();
      if (random.nextDouble() <= this.getGlobalChance() && !possibleChanceAttributes.isEmpty()) {
         AbstractWildAugment.AttributeEntry selectedChanceEntry = this.selectRandomAttributeEntry(possibleChanceAttributes);
         double chanceValue = this.selectRandomValue(selectedChanceEntry.getMinValue(), selectedChanceEntry.getMaxValue());
         this.saveAttributeToComponent(stack, selectedChanceEntry.getAttribute(), selectedChanceEntry.getOperation(), chanceValue, "chanceAttribute");
      }
   }

   private AbstractWildAugment.AttributeEntry selectRandomAttributeEntry(List<AbstractWildAugment.AttributeEntry> possibleAttributes) {
      double totalWeight = possibleAttributes.stream().mapToDouble(AbstractWildAugment.AttributeEntry::getWeight).sum();
      double randomValue = new Random().nextDouble() * totalWeight;

      for (AbstractWildAugment.AttributeEntry entry : possibleAttributes) {
         randomValue -= entry.getWeight();
         if (randomValue <= 0.0) {
            return entry;
         }
      }

      return possibleAttributes.get(possibleAttributes.size() - 1);
   }

   private double selectRandomValue(double minValue, double maxValue) {
      Random random = new Random();
      return minValue + (maxValue - minValue) * random.nextDouble();
   }

   private void saveAttributeToComponent(ItemStack stack, Attribute attribute, Operation operation, double value, String tagPrefix) {
      if (!stack.has(TODataComponents.WILD_ATTRIBUTE_COMPONENT)) {
         stack.set(TODataComponents.WILD_ATTRIBUTE_COMPONENT, new CompoundTag());
      }

      CompoundTag componentTag = (CompoundTag)stack.get(TODataComponents.WILD_ATTRIBUTE_COMPONENT);
      CompoundTag attributeTag = new CompoundTag();
      attributeTag.putString("attribute", BuiltInRegistries.ATTRIBUTE.getKey(attribute).toString());
      attributeTag.putString("operation", operation.name());
      attributeTag.putDouble("value", value);
      componentTag.put(tagPrefix, attributeTag);
      stack.set(TODataComponents.WILD_ATTRIBUTE_COMPONENT, componentTag);
   }

   private Multimap<Holder<Attribute>, AttributeModifier> getAttributesFromNBT(CompoundTag tag) {
      Builder<Holder<Attribute>, AttributeModifier> attributeBuilder = new Builder();
      if (tag.contains("positiveAttribute")) {
         CompoundTag positiveTag = tag.getCompound("positiveAttribute");
         this.addAttributeFromNBT(attributeBuilder, positiveTag);
      }

      if (tag.contains("negativeAttribute")) {
         CompoundTag negativeTag = tag.getCompound("negativeAttribute");
         this.addAttributeFromNBT(attributeBuilder, negativeTag);
      }

      if (tag.contains("chanceAttribute")) {
         CompoundTag chanceTag = tag.getCompound("chanceAttribute");
         this.addAttributeFromNBT(attributeBuilder, chanceTag);
      }

      return attributeBuilder.build();
   }

   private void addAttributeFromNBT(Builder<Holder<Attribute>, AttributeModifier> attributeBuilder, CompoundTag tag) {
      ResourceLocation attributeId = new ResourceLocation("traveloptics", tag.getString("attribute"));
      Attribute attribute = (Attribute)BuiltInRegistries.ATTRIBUTE.get(attributeId);

      try {
         Operation operation = Operation.valueOf(tag.getString("operation"));
         if (attribute != null) {
            AttributeModifier modifier = new AttributeModifier(
               new ResourceLocation("traveloptics", attribute.getDescriptionId()), tag.getDouble("value"), operation
            );
            attributeBuilder.put(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute), modifier);
         }
      } catch (IllegalArgumentException var7) {
         System.err.println("Invalid attribute modifier operation: " + tag.getString("operation"));
      }
   }

   private void playSound(Level level, Player player, SoundEvent soundEvent) {
      if (!level.isClientSide && soundEvent != null) {
         level.playSound(null, player.blockPosition(), soundEvent, player.getSoundSource(), 1.0F, 1.0F);
      }
   }

   public Component getName(ItemStack stack) {
      if (stack.has(TODataComponents.ECHO_CURIO_COMPONENT)) {
         CompoundTag tag = (CompoundTag)stack.get(TODataComponents.ECHO_CURIO_COMPONENT);
         return tag.getBoolean("attributesAssigned")
            ? Component.translatable(stack.getItem().getDescriptionId(stack) + ".assigned")
            : Component.translatable(stack.getItem().getDescriptionId(stack) + ".unassigned");
      } else {
         return super.getName(stack);
      }
   }

   public static class AttributeEntry {
      private final Attribute attribute;
      private final Operation operation;
      private final double minValue;
      private final double maxValue;
      private final double weight;

      public AttributeEntry(Attribute attribute, Operation operation, double minValue, double maxValue, double weight) {
         super();
         this.attribute = attribute;
         this.operation = operation;
         this.minValue = minValue;
         this.maxValue = maxValue;
         this.weight = weight;
      }

      public Attribute getAttribute() {
         return this.attribute;
      }

      public Operation getOperation() {
         return this.operation;
      }

      public double getMinValue() {
         return this.minValue;
      }

      public double getMaxValue() {
         return this.maxValue;
      }

      public double getWeight() {
         return this.weight;
      }
   }
}
