package net.veroxuniverse.crystal_chronicles.spells;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CCSpells {
   public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "crystal_chronicles");
   private static Supplier<PrismaticPortalSpell> PRISMATIC_PORTAL_SPELL_SUPPLIER;

   public static AbstractSpell getPrismaticPortalSpell() {
      return PRISMATIC_PORTAL_SPELL_SUPPLIER == null ? null : PRISMATIC_PORTAL_SPELL_SUPPLIER.get();
   }

   public static void register(IEventBus modEventBus) {
      Supplier<PrismaticPortalSpell> supplier = SPELLS.register(PrismaticPortalSpell.SPELL_ID.getPath(), PrismaticPortalSpell::new);
      PRISMATIC_PORTAL_SPELL_SUPPLIER = supplier;
      SPELLS.register(modEventBus);
   }
}
