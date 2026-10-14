package net.neoforged.neoforge.common.loot;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * NeoForge's global loot modifier contract.
 */
public interface IGlobalLootModifier {

    /**
     * NeoForge's shared conditions codec — {@code LootItemCondition.CODEC} is
     * holder-based; modifiers store plain condition arrays.
     */
    com.mojang.serialization.Codec<net.minecraft.world.level.storage.loot.predicates.LootItemCondition[]> LOOT_CONDITIONS_CODEC =
            net.minecraft.world.level.storage.loot.predicates.LootItemCondition.CODEC.listOf()
                    .xmap(list -> list.stream().map(net.minecraft.core.Holder::value)
                                    .toArray(net.minecraft.world.level.storage.loot.predicates.LootItemCondition[]::new),
                            array -> java.util.Arrays.stream(array)
                                    .<net.minecraft.core.Holder<net.minecraft.world.level.storage.loot.predicates.LootItemCondition>>map(net.minecraft.core.Holder::direct).toList());

    ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);

    MapCodec<? extends IGlobalLootModifier> codec();
}
