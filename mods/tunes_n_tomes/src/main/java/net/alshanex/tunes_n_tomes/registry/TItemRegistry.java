package net.alshanex.tunes_n_tomes.registry;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.alshanex.tunes_n_tomes.item.BardHarp;
import net.alshanex.tunes_n_tomes.item.armor.BardArmorItem;
import net.alshanex.tunes_n_tomes.item.armor.MelodicMasteryArmorItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TItemRegistry {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "tunes_n_tomes");
   public static final DeferredHolder<Item, Item> MELODY_RUNE = ITEMS.register("melody_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> MELODY_UPGRADE_ORB = ITEMS.register(
      "melody_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, TUpgradeOrbRegistry.MELODY_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> MELODY_SPELL_BOOK = ITEMS.register(
      "melody_spell_book",
      () -> new SpellBook(10)
         .withSpellbookAttributes(
            new AttributeContainer[]{
               new AttributeContainer(TAttributeRegistry.MELODY_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE)
            }
         )
   );
   public static final DeferredHolder<Item, Item> BARD_HARP = ITEMS.register("bard_harp", BardHarp::new);
   public static final DeferredHolder<Item, Item> BARD_HELMET = ITEMS.register(
      "bard_helmet", () -> new BardArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> BARD_CHESTPLATE = ITEMS.register(
      "bard_chestplate", () -> new BardArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> BARD_LEGGINGS = ITEMS.register(
      "bard_leggings", () -> new BardArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> BARD_BOOTS = ITEMS.register(
      "bard_boots", () -> new BardArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> MELODIC_MASTERY_HELMET = ITEMS.register(
      "melodic_mastery_helmet", () -> new MelodicMasteryArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> MELODIC_MASTERY_CHESTPLATE = ITEMS.register(
      "melodic_mastery_chestplate",
      () -> new MelodicMasteryArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> MELODIC_MASTERY_LEGGINGS = ITEMS.register(
      "melodic_mastery_leggings",
      () -> new MelodicMasteryArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> MELODIC_MASTERY_BOOTS = ITEMS.register(
      "melodic_mastery_boots", () -> new MelodicMasteryArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(37)))
   );

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
