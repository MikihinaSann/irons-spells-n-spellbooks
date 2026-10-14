package com.gametechbc.traveloptics.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.internal.fabric.GlobalLootModifierManager;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class UniversalLootModifier extends LootModifier {
   public static final Supplier<MapCodec<UniversalLootModifier>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.mapCodec(
         builder -> codecStart(builder).and(Codec.STRING.fieldOf("loot_table").forGetter(m -> m.lootTable)).apply(builder, UniversalLootModifier::new)
      )
   );
   private final String lootTable;

   protected UniversalLootModifier(LootItemCondition[] conditionsIn, String lootTable) {
      super(conditionsIn);
      this.lootTable = lootTable;
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      ResourceLocation queriedTable = GlobalLootModifierManager.currentTableId();
      if (queriedTable == null || !queriedTable.getPath().startsWith("chests/")) {
         return generatedLoot;
      } else {
         ResourceLocation lootTablePath = ResourceLocation.parse(this.lootTable);
         LootTable targetLootTable = context.getLevel()
            .getServer()
            .reloadableRegistries()
            .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTablePath));
         ObjectArrayList<ItemStack> additionalLoot = new ObjectArrayList();
         targetLootTable.getRandomItemsRaw(context, additionalLoot::add);
         generatedLoot.addAll(additionalLoot);
         return generatedLoot;
      }
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }
}
