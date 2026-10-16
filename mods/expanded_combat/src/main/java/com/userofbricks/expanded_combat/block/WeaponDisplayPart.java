package com.userofbricks.expanded_combat.block;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum WeaponDisplayPart implements StringRepresentable {
   LEFT("left"),
   RIGHT("right");

   private final String name;

   private WeaponDisplayPart(String p_61339_) {
      this.name = p_61339_;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @NotNull
   public String getSerializedName() {
      return this.name;
   }
}
