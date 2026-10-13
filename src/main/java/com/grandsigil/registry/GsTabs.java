package com.grandsigil.registry;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GsTabs {
   private static final ResourceLocation SCROLL = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "scroll");
   private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "grandsigil");
   private static final List<Supplier<AbstractSpell>> SPELLS = List.of(
      GsSpells.GRAND_SIGIL,
      GsSpells.THOUSAND_BLADES,
      GsSpells.SUNFALL,
      GsSpells.PHOENIX,
      GsSpells.SKYBREAK,
      GsSpells.THUNDER_DRAGON,
      GsSpells.WORLD_TREE,
      GsSpells.FULL_BLOOM,
      GsSpells.COLOSSEUM,
      GsSpells.CRIMSON_THREADS,
      GsSpells.GLACIATION,
      GsSpells.ICE_AGE,
      GsSpells.HEAVEN_GATE,
      GsSpells.RECKONING,
      GsSpells.THE_GAZE,
      GsSpells.BLACK_COFFIN,
      GsSpells.VOIDGRIP,
      GsSpells.DIMENSION_CUT
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPELLS_TAB = TABS.register(
      "spells",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.grandsigil.spells"))
            .icon(() -> scroll(GsSpells.GRAND_SIGIL.get()))
            .displayItems((parameters, output) -> {
               for (Supplier<AbstractSpell> spell : SPELLS) {
                  ItemStack made = scroll(spell.get());
                  if (!made.isEmpty()) {
                     output.accept(made);
                  }
               }
            })
            .build()
   );

   private GsTabs() {
   }

   public static void register(IEventBus modBus) {
      TABS.register(modBus);
   }

   private static ItemStack scroll(AbstractSpell spell) {
      Item item = BuiltInRegistries.ITEM.get(SCROLL);
      if (item != null && item != Items.AIR) {
         ItemStack stack = new ItemStack(item);
         ISpellContainer.createScrollContainer(spell, Math.max(1, spell.getDefaultConfig().maxLevel), stack);
         return stack;
      } else {
         return ItemStack.EMPTY;
      }
   }
}
