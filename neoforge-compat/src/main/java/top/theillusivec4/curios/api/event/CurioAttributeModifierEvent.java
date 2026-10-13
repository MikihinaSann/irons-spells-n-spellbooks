package top.theillusivec4.curios.api.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import top.theillusivec4.curios.api.SlotContext;

public class CurioAttributeModifierEvent extends Event {
    private final ItemStack stack;
    private final SlotContext slotContext;
    private final ResourceLocation id;
    private final Multimap<Holder<Attribute>, AttributeModifier> originalModifiers;
    private final Multimap<Holder<Attribute>, AttributeModifier> modifiers;

    public CurioAttributeModifierEvent(ItemStack stack, SlotContext slotContext, ResourceLocation id,
                                       Multimap<Holder<Attribute>, AttributeModifier> originalModifiers) {
        this.stack = stack;
        this.slotContext = slotContext;
        this.id = id;
        this.originalModifiers = originalModifiers;
        this.modifiers = HashMultimap.create(originalModifiers);
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public SlotContext getSlotContext() {
        return slotContext;
    }

    public ResourceLocation getId() {
        return id;
    }

    /** The original (pre-event) modifier map. */
    public Multimap<Holder<Attribute>, AttributeModifier> getOriginalModifiers() {
        return originalModifiers;
    }

    /** The live modifier map; mutations are applied to the equipped stack. */
    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers() {
        return modifiers;
    }

    public void addModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
        modifiers.put(attribute, modifier);
    }

    public void removeModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
        modifiers.remove(attribute, modifier);
    }

    public void removeModifier(Holder<Attribute> attribute, ResourceLocation modifierId) {
        modifiers.get(attribute).removeIf(m -> m.is(modifierId));
    }

    public void clearModifiers() {
        modifiers.clear();
    }
}
