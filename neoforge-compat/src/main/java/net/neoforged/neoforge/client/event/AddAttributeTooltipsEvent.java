/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 * Ported from NeoForge's AddAttributeTooltipsEvent (obfuscated refs deobfuscated).
 */
package net.neoforged.neoforge.client.event;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/**
 * Fired while assembling an item's attribute tooltips - listeners append lines
 * via {@link #addTooltipLines(Component...)}.
 */
public class AddAttributeTooltipsEvent extends Event {
    protected final ItemStack stack;
    protected final Consumer<Component> tooltip;
    protected final AttributeTooltipContext ctx;

    public AddAttributeTooltipsEvent(ItemStack stack, Consumer<Component> tooltip, AttributeTooltipContext ctx) {
        this.stack = stack;
        this.tooltip = tooltip;
        this.ctx = ctx;
    }

    public AttributeTooltipContext getContext() {
        return this.ctx;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void addTooltipLines(Component... comps) {
        for (Component comp : comps) {
            this.tooltip.accept(comp);
        }
    }

    public boolean shouldShow() {
        return true;
    }
}
