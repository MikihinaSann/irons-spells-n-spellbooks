package it.crystalnest.soul_fire_d.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.core.Holder;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public final class ChestLootModifier extends LootModifier {
   public static final Supplier<MapCodec<ChestLootModifier>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.mapCodec(
         instance -> codecStart(instance)
            .and(ChestLootModifier.Addition.CODEC.listOf().fieldOf("additions").forGetter(modifier -> modifier.additions))
            .apply(instance, ChestLootModifier::new)
      )
   );
   private final List<ChestLootModifier.Addition> additions;

   private ChestLootModifier(LootItemCondition[] conditionsIn, List<ChestLootModifier.Addition> additions) {
      super(conditionsIn);
      this.additions = additions;
   }

   @Nonnull
   protected ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context) {
      for (ChestLootModifier.Addition addition : this.additions) {
         if (((Enchantment)addition.enchantment.value()).getSupportedItems().size() > 0 && context.getRandom().nextFloat() <= addition.chance) {
            generatedLoot.add(addition.getEnchantedBook());
         }
      }

      return generatedLoot;
   }

   @NotNull
   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }

   private static final class Addition {
      public static final Codec<ChestLootModifier.Addition> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Enchantment.CODEC.fieldOf("enchantment").forGetter(addition -> addition.enchantment),
               Codec.FLOAT.fieldOf("chance").forGetter(addition -> addition.chance),
               Codec.INT.fieldOf("level").forGetter(addition -> addition.level)
            )
            .apply(instance, ChestLootModifier.Addition::new)
      );
      private final Holder<Enchantment> enchantment;
      private final Float chance;
      private final Integer level;

      private Addition(Holder<Enchantment> enchantment, Float chance, Integer quantity) {
         this.enchantment = enchantment;
         this.chance = chance;
         this.level = quantity;
      }

      private ItemStack getEnchantedBook() {
         return EnchantedBookItem.createForEnchantment(
            new EnchantmentInstance(this.enchantment, Math.min(((Enchantment)this.enchantment.value()).getMaxLevel(), this.level))
         );
      }
   }
}
