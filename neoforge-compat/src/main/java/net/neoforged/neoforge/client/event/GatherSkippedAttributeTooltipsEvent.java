/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 * Ported from NeoForge's GatherSkippedAttributeTooltipsEvent (obfuscated refs deobfuscated).
 */
package net.neoforged.neoforge.client.event;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import org.jetbrains.annotations.Nullable;

/**
 * Fired by {@code AttributeUtil.applyModifierTooltips} - listeners can skip
 * attribute modifiers by id or slot group.
 */
public class GatherSkippedAttributeTooltipsEvent extends Event {
    protected final ItemStack stack;
    protected final AttributeTooltipContext ctx;
    @Nullable
    private Set<ResourceLocation> skippedIds = null;
    @Nullable
    private Set<EquipmentSlotGroup> skippedGroups = null;
    private boolean skipAll = false;

    public GatherSkippedAttributeTooltipsEvent(ItemStack stack, AttributeTooltipContext ctx) {
        this.stack = stack;
        this.ctx = ctx;
    }

    public AttributeTooltipContext getContext() {
        return this.ctx;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public boolean isSkipped(ResourceLocation id) {
        return this.skipAll || (this.skippedIds != null && this.skippedIds.contains(id));
    }

    public boolean isSkipped(EquipmentSlotGroup group) {
        return this.skipAll || (this.skippedGroups != null && this.skippedGroups.contains(group));
    }

    public boolean isSkippingAll() {
        return this.skipAll;
    }

    public void skipAll() {
        this.skipAll = true;
    }

    public void skip(ResourceLocation... ids) {
        if (this.skippedIds == null) {
            this.skippedIds = new HashSet<>();
        }
        for (ResourceLocation id : ids) {
            this.skippedIds.add(id);
        }
    }

    public void skipAllGroups() {
        this.skip(EquipmentSlotGroup.values());
    }

    public void skip(EquipmentSlotGroup... groups) {
        if (this.skippedGroups == null) {
            this.skippedGroups = EnumSet.noneOf(EquipmentSlotGroup.class);
        }
        for (EquipmentSlotGroup group : groups) {
            this.skippedGroups.add(group);
        }
    }
}
