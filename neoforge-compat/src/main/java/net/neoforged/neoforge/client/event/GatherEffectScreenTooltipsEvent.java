package net.neoforged.neoforge.client.event;

import java.util.List;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.Event;

/**
 * NeoForge GatherEffectScreenTooltipsEvent - fired when the effect-screen tooltip
 * is assembled; listeners append lines to {@link #getTooltip()}.
 */
public class GatherEffectScreenTooltipsEvent extends Event {
    private final AbstractContainerScreen<?> screen;
    private final MobEffectInstance effectInstance;
    private final List<Component> tooltip;

    public GatherEffectScreenTooltipsEvent(AbstractContainerScreen<?> screen, MobEffectInstance effectInstance, List<Component> tooltip) {
        this.screen = screen;
        this.effectInstance = effectInstance;
        this.tooltip = tooltip;
    }

    public AbstractContainerScreen<?> getScreen() {
        return screen;
    }

    public MobEffectInstance getEffectInstance() {
        return effectInstance;
    }

    public List<Component> getTooltip() {
        return tooltip;
    }
}
