package net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;

public abstract class AbstractAbyssalSpell extends AbstractSpell {
   public boolean allowLooting() {
      return false;
   }
}
