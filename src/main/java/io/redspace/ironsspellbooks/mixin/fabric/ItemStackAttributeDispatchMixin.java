package io.redspace.ironsspellbooks.mixin.fabric;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

/**
 * Fires {@link ItemAttributeModifierEvent} whenever item attribute modifiers are
 * enumerated (equipment application + tooltip rendering). NeoForge patches the
 * equivalent of {@code ItemStack#forEachModifier}; the shim collects additions
 * and removals and the merged list is driven here instead of the defaults.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackAttributeDispatchMixin {

    @Unique
    private static ItemAttributeModifiers irons_spellbooks$fire(ItemStack stack) {
        var defaults = stack.getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY);
        if (defaults.modifiers().isEmpty()) {
            defaults = stack.getItem().getDefaultAttributeModifiers();
        }
        var event = new ItemAttributeModifierEvent(stack, defaults);
        NeoForge.EVENT_BUS.post(event);
        if (!event.getAdditions().isEmpty() || !event.getRemovals().isEmpty()) {
            defaults = new ItemAttributeModifiers(event.getModifiers(), defaults.showInTooltip());
        }
        return defaults;
    }

    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V",
            at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$forEachModifier(EquipmentSlotGroup group, BiConsumer<Holder<Attribute>, AttributeModifier> action,
                                                  CallbackInfo ci) {
        var stack = (ItemStack) (Object) this;
        irons_spellbooks$fire(stack).forEach(group, action);
        // enchantment-based modifiers still apply on top, same as vanilla
        net.minecraft.world.item.enchantment.EnchantmentHelper.forEachModifier(stack, group, action);
        ci.cancel();
    }

    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
            at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$forEachModifierSlot(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> action,
                                                      CallbackInfo ci) {
        var stack = (ItemStack) (Object) this;
        irons_spellbooks$fire(stack).forEach(slot, action);
        net.minecraft.world.item.enchantment.EnchantmentHelper.forEachModifier(stack, slot, action);
        ci.cancel();
    }
}
