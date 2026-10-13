package top.theillusivec4.curios.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Curios SlotContext, adapted onto Trinkets' SlotReference semantics.
 */
public record SlotContext(String identifier, LivingEntity entity, int index,
                          boolean renderCosmetic, boolean visible) {

    public SlotContext(String identifier, LivingEntity entity, int index) {
        this(identifier, entity, index, false, true);
    }

    /** Convenience used by NeoForge-era Curios code: identifier without group prefix. */
    public String identifier() {
        return identifier;
    }

    /** Whether the stack in this slot is worn cosmetically (not rendered). */
    public boolean renderCosmetic() {
        return renderCosmetic;
    }

    public boolean isVisible() {
        return visible;
    }
}
