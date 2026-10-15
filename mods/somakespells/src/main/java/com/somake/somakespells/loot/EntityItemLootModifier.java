package com.somake.somakespells.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somake.somakespells.registries.ModLootModifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class EntityItemLootModifier extends LootModifier {
   public static final MapCodec<EntityItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> codecStart(instance)
         .and(ResourceLocation.CODEC.fieldOf("entity_id").forGetter(modifier -> modifier.entityId))
         .and(ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(modifier -> modifier.item))
         .and(
            Codec.INT
               .optionalFieldOf("count")
               .forGetter(
                  modifier -> modifier.minCount == modifier.count && modifier.maxCount == modifier.count ? Optional.of(modifier.count) : Optional.empty()
               )
         )
         .and(
            Codec.INT
               .optionalFieldOf("min_count")
               .forGetter(
                  modifier -> modifier.minCount == modifier.count && modifier.maxCount == modifier.count ? Optional.empty() : Optional.of(modifier.minCount)
               )
         )
         .and(
            Codec.INT
               .optionalFieldOf("max_count")
               .forGetter(
                  modifier -> modifier.minCount == modifier.count && modifier.maxCount == modifier.count ? Optional.empty() : Optional.of(modifier.maxCount)
               )
         )
         .apply(instance, EntityItemLootModifier::new)
   );
   private final ResourceLocation entityId;
   private final ItemStack item;
   private final int count;
   private final int minCount;
   private final int maxCount;

   public EntityItemLootModifier(
      LootItemCondition[] conditions,
      ResourceLocation entityId,
      ItemStack item,
      Optional<Integer> count,
      Optional<Integer> minCount,
      Optional<Integer> maxCount
   ) {
      super(conditions);
      this.entityId = entityId;
      this.item = item;
      int baseCount = Math.max(1, count.orElse(item.getCount()));
      int resolvedMin = Math.max(1, minCount.orElse(baseCount));
      int resolvedMax = Math.max(1, maxCount.orElse(minCount.isPresent() ? resolvedMin : baseCount));
      if (resolvedMax < resolvedMin) {
         resolvedMax = resolvedMin;
      }

      this.count = baseCount;
      this.minCount = resolvedMin;
      this.maxCount = resolvedMax;
   }

   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      if (context.hasParam(LootContextParams.DAMAGE_SOURCE)
         && context.hasParam(LootContextParams.THIS_ENTITY)
         && ((Entity)context.getParam(LootContextParams.THIS_ENTITY)).getType().builtInRegistryHolder().key().location().equals(this.entityId)) {
         ItemStack stack = this.item.copy();
         int dropCount = this.minCount == this.maxCount ? this.minCount : context.getRandom().nextInt(this.maxCount - this.minCount + 1) + this.minCount;
         stack.setCount(dropCount);
         generatedLoot.add(stack);
      }

      return generatedLoot;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return (MapCodec<? extends IGlobalLootModifier>)ModLootModifiers.ENTITY_ITEM.get();
   }
}
