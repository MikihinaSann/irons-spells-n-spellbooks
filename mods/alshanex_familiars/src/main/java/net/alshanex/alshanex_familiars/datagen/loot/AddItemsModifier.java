package net.alshanex.alshanex_familiars.datagen.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class AddItemsModifier extends LootModifier {
   public static final MapCodec<AddItemsModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> codecStart(instance)
         .and(ExtraCodecs.nonEmptyList(BuiltInRegistries.ITEM.byNameCodec().listOf()).fieldOf("item").forGetter(o -> o.items))
         .apply(instance, AddItemsModifier::new)
   );
   private final List<Item> items;

   public AddItemsModifier(LootItemCondition[] conditionsIn, List<Item> items) {
      super(conditionsIn);
      this.items = items;
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      for (LootItemCondition condition : this.conditions) {
         if (!condition.test(context)) {
            return generatedLoot;
         }
      }

      this.items.forEach(item -> generatedLoot.add(item.getDefaultInstance()));
      return generatedLoot;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC;
   }
}
