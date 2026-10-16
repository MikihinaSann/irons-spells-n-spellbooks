package com.userofbricks.expanded_combat.api.registry;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public interface IExpandedCombatPlugin {
   ResourceLocation getPluginUid();

   default int loadOrder() {
      return 2;
   }

   default boolean addFletchingTableGui() {
      return true;
   }

   default void registerMaterials(RegistrationHandler registrationHandler) {
   }

   default List<ShieldMaterialUseTick> registerShieldMaterialUseTick() {
      return new ArrayList<>();
   }
}
