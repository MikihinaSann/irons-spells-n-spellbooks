package net.neoforged.neoforge.common.loot;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Base class for global loot modifiers. On Fabric, modifiers are applied via
 * {@code LootTableEvents.MODIFY} by the platform bridge reading
 * {@code data/<ns>/neoforge/loot_modifiers} JSON.
 */
public abstract class LootModifier implements IGlobalLootModifier {
    protected final LootItemCondition[] conditionsIn;

    protected LootModifier(LootItemCondition[] conditionsIn) {
        this.conditionsIn = conditionsIn;
    }

    @NotNull
    protected abstract it.unimi.dsi.fastutil.objects.ObjectArrayList<ItemStack> doApply(
            it.unimi.dsi.fastutil.objects.ObjectArrayList<ItemStack> generatedLoot, LootContext context);

    @Override
    public final it.unimi.dsi.fastutil.objects.ObjectArrayList<ItemStack> apply(
            it.unimi.dsi.fastutil.objects.ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (LootItemCondition condition : conditionsIn) {
            if (!condition.test(context)) {
                return generatedLoot;
            }
        }
        return doApply(generatedLoot, context);
    }

    /** Starts a record codec with the shared {@code conditions} field. */
    protected static <T extends LootModifier> com.mojang.datafixers.Products.P1<RecordCodecBuilder.Mu<T>, LootItemCondition[]> codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(LootItemCondition.CODEC.listOf()
                .xmap(list -> list.stream().map(Holder::value).toArray(LootItemCondition[]::new),
                        array -> Arrays.stream(array).<Holder<LootItemCondition>>map(Holder::direct).toList())
                .fieldOf("conditions").forGetter(m -> m.conditionsIn));
    }
}
