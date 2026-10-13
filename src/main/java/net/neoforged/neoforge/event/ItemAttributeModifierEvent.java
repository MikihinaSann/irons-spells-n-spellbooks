package net.neoforged.neoforge.event;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects attribute modifier additions for an item stack. Applied at bootstrap
 * by rebuilding the item's default component modifiers.
 */
public class ItemAttributeModifierEvent extends Event {
    private final ItemStack stack;
    private final ItemAttributeModifiers defaultModifiers;
    private final List<Entry> additions = new ArrayList<>();
    private final List<Entry> removals = new ArrayList<>();

    public record Entry(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slotGroup) {
    }

    public ItemAttributeModifierEvent(ItemStack stack, ItemAttributeModifiers defaultModifiers) {
        this.stack = stack;
        this.defaultModifiers = defaultModifiers;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public ItemAttributeModifiers getDefaultModifiers() {
        return defaultModifiers;
    }

    public void addModifier(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slotGroup) {
        additions.add(new Entry(unwrap(attribute), modifier, slotGroup));
    }

    public void removeModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
        removals.add(new Entry(unwrap(attribute), modifier, EquipmentSlotGroup.ANY));
    }

    public void removeModifier(Holder<Attribute> attribute, net.minecraft.resources.ResourceLocation modifierId) {
        List<ItemAttributeModifiers.Entry> all = new ArrayList<>(getModifiers());
        for (ItemAttributeModifiers.Entry e : all) {
            if (e.attribute().equals(unwrap(attribute)) && e.modifier().id().equals(modifierId)) {
                removals.add(new Entry(e.attribute(), e.modifier(), e.slot()));
                return;
            }
        }
    }

    /**
     * All modifiers currently applied to the stack (defaults + queued
     * additions, minus queued removals) as vanilla entries.
     */
    public List<ItemAttributeModifiers.Entry> getModifiers() {
        List<ItemAttributeModifiers.Entry> all = new ArrayList<>();
        if (defaultModifiers != null) {
            all.addAll(defaultModifiers.modifiers());
        }
        for (Entry e : additions) {
            all.add(new ItemAttributeModifiers.Entry(e.attribute(), e.modifier(), e.slotGroup()));
        }
        for (Entry r : removals) {
            all.removeIf(e -> e.attribute().equals(r.attribute()) && e.modifier().equals(r.modifier()));
        }
        return all;
    }

    @SuppressWarnings("unchecked")
    private static Holder<Attribute> unwrap(Holder<Attribute> attribute) {
        if (attribute instanceof net.neoforged.neoforge.registries.DeferredHolder<?, ?> shim) {
            Holder<?> bound = ((net.neoforged.neoforge.registries.DeferredHolder<?, ?>) shim).asHolder();
            if (bound != null) {
                return (Holder<Attribute>) bound;
            }
        }
        return attribute;
    }

    public List<Entry> getAdditions() {
        return additions;
    }

    public List<Entry> getRemovals() {
        return removals;
    }

    /** The holder unwrapping helper — public for the bootstrap applier. */
    @SuppressWarnings("unchecked")
    public static Holder<Attribute> unwrapHolder(Holder<Attribute> attribute) {
        return unwrap(attribute);
    }
}
