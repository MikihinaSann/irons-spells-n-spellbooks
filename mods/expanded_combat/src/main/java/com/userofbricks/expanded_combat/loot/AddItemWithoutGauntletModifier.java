package com.userofbricks.expanded_combat.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public class AddItemWithoutGauntletModifier extends LootModifier {
   public static final Supplier<MapCodec<AddItemWithoutGauntletModifier>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.mapCodec(
         inst -> codecStart(inst)
            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("loot_item").forGetter(m -> m.lootItem))
            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("gauntlet").forGetter(m -> m.gauntlet))
            .apply(inst, AddItemWithoutGauntletModifier::new)
      )
   );
   protected final Item lootItem;
   protected final Item gauntlet;

   public AddItemWithoutGauntletModifier(LootItemCondition[] conditionsIn, Item lootItem, Item gauntlet) {
      super(conditionsIn);
      this.lootItem = lootItem;
      this.gauntlet = gauntlet;
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context) {
      for (LootItemCondition condition : this.conditions) {
         if (!condition.test(context)) {
            return generatedLoot;
         }
      }

      if (context.getParamOrNull(LootContextParams.ATTACKING_ENTITY) instanceof LivingEntity killer) {
         Optional<SlotResult> optionalSlotResult = CuriosApi.getCuriosInventory(killer)
            .flatMap(curiosInventory -> curiosInventory.findFirstCurio(this.gauntlet));
         if (optionalSlotResult.isEmpty()) {
            generatedLoot.add(new ItemStack(this.lootItem));
         }
      }

      return generatedLoot;
   }

   @NotNull
   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }
}
