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
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class KeyLootModifier extends LootModifier {
   public static final Supplier<MapCodec<KeyLootModifier>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.mapCodec(
         builder -> codecStart(builder).and(Codec.STRING.fieldOf("key").forGetter(m -> m.resourceLocationKey)).apply(builder, KeyLootModifier::new)
      )
   );
   private final String resourceLocationKey;

   protected KeyLootModifier(LootItemCondition[] conditionsIn, String resourceLocationKey) {
      super(conditionsIn);
      this.resourceLocationKey = resourceLocationKey;
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      ResourceLocation path = ResourceLocation.parse(this.resourceLocationKey);
      LootTable lootTable = context.getLevel().getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, path));
      ObjectArrayList<ItemStack> objectarraylist = new ObjectArrayList();
      lootTable.getRandomItemsRaw(context, objectarraylist::add);
      generatedLoot.addAll(objectarraylist);
      return generatedLoot;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }
}
