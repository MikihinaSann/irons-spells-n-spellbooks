package com.gametechbc.traveloptics.api.item;

import com.gametechbc.traveloptics.api.TODataComponents;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

public abstract class AbstractRandomImbuingAttributeCurio extends CurioBaseItem {
   private final Multimap<Attribute, AttributeModifier> attributeMap = HashMultimap.create();
   private static final int EXPERIENCE_COST = 180;

   public AbstractRandomImbuingAttributeCurio(Properties properties, Map<Attribute, AttributeModifier> attributes) {
      super(properties.stacksTo(1).rarity(Rarity.EPIC));

      for (Entry<Attribute, AttributeModifier> entry : attributes.entrySet()) {
         this.attributeMap.put(entry.getKey(), entry.getValue());
      }
   }

   protected abstract Map<AbstractSpell, AbstractRandomImbuingAttributeCurio.SpellAttributes> getSpellAttributes();

   protected abstract Component getAssignedHoverText();

   protected abstract Component getUnassignedHoverText();

   protected SoundEvent getAssignSound() {
      return SoundEvents.ENCHANTMENT_TABLE_USE;
   }

   protected int selectLevel(AbstractRandomImbuingAttributeCurio.SpellAttributes attributes) {
      int min = attributes.levelRange.minLevel;
      int max = attributes.levelRange.maxLevel;
      if (!attributes.prioritizeLowerLevel) {
         return min + new Random().nextInt(max - min + 1);
      } else {
         int range = max - min + 1;
         int[] weights = new int[range];
         int totalWeight = 0;

         for (int i = 0; i < range; i++) {
            int level = min + i;
            weights[i] = max - level + 1;
            totalWeight += weights[i];
         }

         int randomWeight = new Random().nextInt(totalWeight);

         for (int i = 0; i < range; i++) {
            randomWeight -= weights[i];
            if (randomWeight < 0) {
               return min + i;
            }
         }

         return min;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      CompoundTag tag = stack.has(TODataComponents.ECHO_CURIO_COMPONENT) ? (CompoundTag)stack.get(TODataComponents.ECHO_CURIO_COMPONENT) : new CompoundTag();
      if (tag.getBoolean("spellAssigned")) {
         player.displayClientMessage(
            Component.translatable("item.traveloptics.spell_imbuing_curio.already_assigned.message").withStyle(ChatFormatting.RED), true
         );
         return InteractionResultHolder.pass(stack);
      } else if (player.totalExperience < 180) {
         player.displayClientMessage(Component.translatable("item.traveloptics.spell_imbuing_curio.not_enough_xp.message").withStyle(ChatFormatting.RED), true);
         return InteractionResultHolder.fail(stack);
      } else {
         Map<AbstractSpell, AbstractRandomImbuingAttributeCurio.SpellAttributes> possibleSpells = this.getSpellAttributes();
         if (!possibleSpells.isEmpty()) {
            AbstractSpell selectedSpell = this.selectWeightedRandomSpell(possibleSpells);
            if (selectedSpell != null) {
               AbstractRandomImbuingAttributeCurio.SpellAttributes attributes = possibleSpells.get(selectedSpell);
               int levelOfSpell = this.selectLevel(attributes);
               ISpellContainerMutable spellContainer = ISpellContainer.create(1, true, true).mutableCopy();
               spellContainer.addSpellAtIndex(selectedSpell, levelOfSpell, 0, true);
               ISpellContainer immutableContainer = spellContainer.toImmutable();
               stack.set(ComponentRegistry.SPELL_CONTAINER, immutableContainer);
               tag.putBoolean("spellAssigned", true);
               stack.set(TODataComponents.ECHO_CURIO_COMPONENT, tag);
               player.giveExperiencePoints(-180);
               level.playSound(null, player.blockPosition(), this.getAssignSound(), player.getSoundSource(), 1.0F, 1.0F);
               if (!level.isClientSide) {
                  player.displayClientMessage(
                     Component.translatable("item.traveloptics.spell_imbuing_curio.success", new Object[]{selectedSpell.getDisplayName(null)})
                        .withStyle(ChatFormatting.GREEN),
                     true
                  );
               }

               return InteractionResultHolder.success(stack);
            }
         }

         return InteractionResultHolder.fail(stack);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag pIsAdvanced) {
      CompoundTag tag = stack.has(TODataComponents.ECHO_CURIO_COMPONENT) ? (CompoundTag)stack.get(TODataComponents.ECHO_CURIO_COMPONENT) : new CompoundTag();
      if (tag.getBoolean("spellAssigned")) {
         tooltip.add(this.getAssignedHoverText());
      } else {
         tooltip.add(this.getUnassignedHoverText());
         tooltip.add(Component.translatable("item.traveloptics.random_imbue_curio.ex_required.tooltip").withStyle(ChatFormatting.AQUA));
      }
   }

   private AbstractSpell selectWeightedRandomSpell(Map<AbstractSpell, AbstractRandomImbuingAttributeCurio.SpellAttributes> possibleSpells) {
      int totalWeight = possibleSpells.values().stream().mapToInt(attributes -> attributes.weight).sum();
      if (totalWeight <= 0) {
         return null;
      } else {
         int randomWeight = new Random().nextInt(totalWeight);

         for (Entry<AbstractSpell, AbstractRandomImbuingAttributeCurio.SpellAttributes> entry : possibleSpells.entrySet()) {
            randomWeight -= entry.getValue().weight;
            if (randomWeight < 0) {
               return entry.getKey();
            }
         }

         return null;
      }
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Builder<Holder<Attribute>, AttributeModifier> attributeBuilder = new Builder();

      for (Attribute attribute : this.attributeMap.keySet()) {
         Holder<Attribute> attributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);

         for (AttributeModifier modifier : this.attributeMap.get(attribute)) {
            attributeBuilder.put(attributeHolder, new AttributeModifier(id, modifier.amount(), modifier.operation()));
         }
      }

      return attributeBuilder.build();
   }

   public static class LevelRange {
      public final int minLevel;
      public final int maxLevel;

      public LevelRange(int minLevel, int maxLevel) {
         super();
         this.minLevel = minLevel;
         this.maxLevel = maxLevel;
      }
   }

   public static class SpellAttributes {
      public final AbstractRandomImbuingAttributeCurio.LevelRange levelRange;
      public final int weight;
      public final boolean prioritizeLowerLevel;

      public SpellAttributes(AbstractRandomImbuingAttributeCurio.LevelRange levelRange, int weight, boolean prioritizeLowerLevel) {
         super();
         this.levelRange = levelRange;
         this.weight = weight;
         this.prioritizeLowerLevel = prioritizeLowerLevel;
      }
   }
}
