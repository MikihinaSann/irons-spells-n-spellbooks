package top.theillusivec4.curios.api.type.inventory;

import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Curios ICurioStacksHandler - one slot identifier (e.g. "ring", "spellbook")
 * exposing its equipped stack handler, cosmetic view and size modifiers.
 */
public interface ICurioStacksHandler {

    /** The slot identifier this handler represents. */
    String getIdentifier();

    /** The main equipped stacks handler for this slot type. */
    IDynamicStackHandler getStacks();

    /** The cosmetic (display-only) stacks handler for this slot type. */
    IDynamicStackHandler getCosmeticStacks();

    /** Number of slots in this slot type. */
    int getSlots();

    /** Whether items in this slot render. */
    boolean isVisible();

    /**
     * Per-slot render flags; the size always matches {@link #getStacks()}.
     * Trinkets has no per-slot render toggle, so every entry reports true.
     */
    default net.minecraft.core.NonNullList<Boolean> getRenders() {
        var list = net.minecraft.core.NonNullList.<Boolean>createWithCapacity(getSlots());
        for (int i = 0; i < getSlots(); i++) {
            list.add(Boolean.TRUE);
        }
        return list;
    }

    /** Whether the slot render may be toggled by the wearer. */
    boolean canToggleRendering();

    /** Slot-size modifiers granted by equipped items (identifier -> map of attribute id -> modifier). */
    Map<ResourceLocation, AttributeModifier> getModifiers();

    /** Slot size modifiers added by non-item sources. */
    Map<ResourceLocation, AttributeModifier> getSizeModifiers();

    /** Adds a permanent slot-size modifier. */
    void addPermanentModifier(AttributeModifier modifier);

    /** Removes a permanent slot-size modifier by id. */
    void removeModifier(ResourceLocation id);
}
