package net.neoforged.neoforge.common.extensions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

/**
 * NeoForge adds tab-ordering helpers to {@code CreativeModeTab.Builder}.
 * On Fabric, tab order comes from ItemGroupEvents/registration order, so these
 * are no-ops that keep the builder chain intact.
 */
public interface ICreativeModeTabBuilderExtension {
    private CreativeModeTab.Builder self() {
        return (CreativeModeTab.Builder) (Object) this;
    }

    default CreativeModeTab.Builder withTabsBefore(ResourceKey<CreativeModeTab>... tabs) {
        return self();
    }

    default CreativeModeTab.Builder withTabsAfter(ResourceKey<CreativeModeTab>... tabs) {
        return self();
    }

    default CreativeModeTab.Builder withTabsBefore(ResourceLocation... tabs) {
        return self();
    }

    default CreativeModeTab.Builder withTabsAfter(ResourceLocation... tabs) {
        return self();
    }

    default CreativeModeTab.Builder withTabsBefore(CreativeModeTab... tabs) {
        return self();
    }

    default CreativeModeTab.Builder withTabsAfter(CreativeModeTab... tabs) {
        return self();
    }

    default CreativeModeTab.Builder withSearchBar() {
        return self();
    }

    default CreativeModeTab.Builder withSearchBar(int width) {
        return self();
    }
}
