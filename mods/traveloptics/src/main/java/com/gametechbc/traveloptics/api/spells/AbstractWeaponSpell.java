package com.gametechbc.traveloptics.api.spells;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;

public abstract class AbstractWeaponSpell extends AbstractSpell {
   public AbstractWeaponSpell() {
      super();
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean allowCrafting() {
      return true;
   }
}
