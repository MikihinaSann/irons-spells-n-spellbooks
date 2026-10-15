package com.somake.somakespells.registries;

import com.somake.somakespells.gui.upgrade_forge.UpgradeForgeMenu;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
   private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "somakespells");
   public static final Supplier<MenuType<UpgradeForgeMenu>> UPGRADE_FORGE_MENU = registerMenuType(UpgradeForgeMenu::new, "upgrade_forge_menu");

   public static void register(IEventBus eventBus) {
      MENUS.register(eventBus);
   }

   private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
      return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
   }
}
