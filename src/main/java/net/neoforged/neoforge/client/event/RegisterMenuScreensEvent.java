package net.neoforged.neoforge.client.event;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegisterMenuScreensEvent extends Event {
    private final List<Runnable> registrations = new ArrayList<>();

    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
            MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, U> factory) {
        registrations.add(() -> MenuScreens.register(menuType, factory));
    }

    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
            Supplier<MenuType<M>> menuType, MenuScreens.ScreenConstructor<M, U> factory) {
        registrations.add(() -> MenuScreens.register(menuType.get(), factory));
    }

    public void drain() {
        registrations.forEach(Runnable::run);
    }
}
