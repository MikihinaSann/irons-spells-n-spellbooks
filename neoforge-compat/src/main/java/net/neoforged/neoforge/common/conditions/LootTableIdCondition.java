package net.neoforged.neoforge.common.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * NeoForge {@code loot_table_id} condition. The table being generated is tracked
 * by {@code GlobalLootModifierManager} while global loot modifiers run.
 */
public class LootTableIdCondition implements LootItemCondition {
    public static final MapCodec<LootTableIdCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("loot_table_id").forGetter(c -> c.target)
    ).apply(instance, LootTableIdCondition::new));

    public static final LootItemConditionType TYPE = new LootItemConditionType(CODEC);

    private final ResourceLocation target;

    public LootTableIdCondition(ResourceLocation target) {
        this.target = target;
    }

    @Override
    public boolean test(LootContext context) {
        ResourceLocation current = net.neoforged.neoforge.internal.fabric.GlobalLootModifierManager.currentTableId();
        return target.equals(current);
    }

    @Override
    public LootItemConditionType getType() {
        return TYPE;
    }
}
