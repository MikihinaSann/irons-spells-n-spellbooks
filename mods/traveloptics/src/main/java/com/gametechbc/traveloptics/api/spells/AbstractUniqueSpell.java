package com.gametechbc.traveloptics.api.spells;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;

public abstract class AbstractUniqueSpell extends AbstractSpell {
   public AbstractUniqueSpell() {
      super();
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean allowCrafting() {
      return false;
   }
}
