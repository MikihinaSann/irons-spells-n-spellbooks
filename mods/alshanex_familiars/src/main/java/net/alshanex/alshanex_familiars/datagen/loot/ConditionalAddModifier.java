package net.alshanex.alshanex_familiars.datagen.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class ConditionalAddModifier extends LootModifier {
   public static final MapCodec<ConditionalAddModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> codecStart(instance)
         .and(ExtraCodecs.nonEmptyList(BuiltInRegistries.ITEM.byNameCodec().listOf()).fieldOf("items").forGetter(o -> o.items))
         .and(ExtraCodecs.POSITIVE_FLOAT.fieldOf("chance").forGetter(o -> o.chance))
         .apply(instance, ConditionalAddModifier::new)
   );
   private final List<Item> items;
   private final float chance;

   public ConditionalAddModifier(LootItemCondition[] conditionsIn, List<Item> items, float chance) {
      super(conditionsIn);
      this.items = items;
      this.chance = chance;
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      for (LootItemCondition condition : this.conditions) {
         if (!condition.test(context)) {
            return generatedLoot;
         }
      }

      if (context.getRandom().nextFloat() < this.chance) {
         ObjectArrayList<ItemStack> newLoot = new ObjectArrayList();
         Item randomItem = (Item)Util.getRandom(this.items, context.getRandom());
         newLoot.add(randomItem.getDefaultInstance());
         return newLoot;
      } else {
         return generatedLoot;
      }
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC;
   }
}
