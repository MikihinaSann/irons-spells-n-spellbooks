package net.neoforged.neoforge.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Collects creative-tab additions; drained into FAPI {@code ItemGroupEvents} at init.
 */
public class BuildCreativeModeTabContentsEvent extends Event {
    public record Addition(ResourceKey<CreativeModeTab> tabKey, Consumer<CreativeModeTab.Output> action) {
    }

    private final List<Addition> additions = new ArrayList<>();
    private CreativeModeTab.ItemDisplayParameters parameters;
    private ResourceKey<CreativeModeTab> tabKey;
    private CreativeModeTab tab;

    public void setTabKey(ResourceKey<CreativeModeTab> tabKey) {
        this.tabKey = tabKey;
    }

    public ResourceKey<CreativeModeTab> getTabKey() {
        return tabKey;
    }

    public void setTab(CreativeModeTab tab) {
        this.tab = tab;
    }

    /** The creative tab currently being populated. */
    public CreativeModeTab getTab() {
        if (tab == null && tabKey != null) {
            tab = net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.get(tabKey);
        }
        return tab;
    }

    public void setParameters(HolderLookup.Provider registries, FeatureFlagSet flags, boolean hasPermissions) {
        this.parameters = new CreativeModeTab.ItemDisplayParameters(flags, hasPermissions, registries);
    }

    public void setParameters(CreativeModeTab.ItemDisplayParameters parameters) {
        this.parameters = parameters;
    }

    public CreativeModeTab.ItemDisplayParameters getParameters() {
        return parameters;
    }

    /** Registers an action to run when the given tab is populated. */
    public void forTab(ResourceKey<CreativeModeTab> tabKey, Consumer<CreativeModeTab.Output> action) {
        additions.add(new Addition(tabKey, action));
    }

    public List<Addition> getAdditions() {
        return additions;
    }

    /** Direct insertion into the tab this event was created for. */
    public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
        if (tabKey != null) {
            additions.add(new Addition(tabKey, out -> out.accept(stack, visibility)));
        }
    }

    public void accept(net.minecraft.world.level.ItemLike item) {
        accept(new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    /** Stack overload — parent and search tabs visibility. */
    public void accept(ItemStack stack) {
        accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public void accept(net.minecraft.world.level.ItemLike item, CreativeModeTab.TabVisibility visibility) {
        accept(new ItemStack(item), visibility);
    }

    /** Convenience shim matching {@code event.accept(item)} call sites — used with explicit tab registration. */
    public static class TabOutput implements CreativeModeTab.Output {
        private final CreativeModeTab.Output delegate;

        public TabOutput(CreativeModeTab.Output delegate) {
            this.delegate = delegate;
        }

        @Override
        public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            delegate.accept(stack, visibility);
        }
    }
}
