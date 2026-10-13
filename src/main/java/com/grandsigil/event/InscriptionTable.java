package com.grandsigil.event;

import com.grandsigil.GrandSigil;
import java.lang.reflect.Method;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

final class InscriptionTable {
   private static final String CLASS = "io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu";
   @Nullable
   private static Class<?> menu;
   @Nullable
   private static Method slot;
   private static boolean looked;
   private static boolean broken;

   private InscriptionTable() {
   }

   static ItemStack bookIn(@Nullable AbstractContainerMenu open) {
      if (open != null && look() && menu.isInstance(open)) {
         try {
            return slot.invoke(open) instanceof Slot found ? found.getItem() : ItemStack.EMPTY;
         } catch (RuntimeException | ReflectiveOperationException var3) {
            broken = true;
            GrandSigil.LOGGER.warn("Cannot read the inscription table spell book slot; one spell per book will not be enforced", var3);
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private static boolean look() {
      if (looked) {
         return !broken;
      } else {
         looked = true;

         try {
            menu = Class.forName("io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu");
            slot = menu.getMethod("getSpellBookSlot");
         } catch (RuntimeException | ReflectiveOperationException var1) {
            broken = true;
            GrandSigil.LOGGER
               .warn(
                  "This build of Iron's has no {}#getSpellBookSlot; one spell per book will not be enforced",
                  "io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu"
               );
         }

         return !broken;
      }
   }
}
