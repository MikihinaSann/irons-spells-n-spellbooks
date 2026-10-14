package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Exposes vanilla's {@code SpawnEggItem.BY_ID} so deferred spawn eggs can insert
 * themselves once their entity type holder is bound. Vanilla populates the map
 * in the item constructor, which requires the entity type eagerly — NeoForge's
 * {@code DeferredSpawnEggItem} passes {@code null} instead, so we insert after
 * deferred registration drains. This also makes {@code SpawnEggItem.eggs()}
 * return modded eggs, which is what vanilla's spawn-egg item color handler
 * iterates (fixes all modded eggs rendering with a single color).
 */
@Mixin(SpawnEggItem.class)
public interface SpawnEggItemAccessor {

    @Accessor("BY_ID")
    static Map<EntityType<? extends Mob>, SpawnEggItem> getById() {
        throw new AssertionError();
    }
}
