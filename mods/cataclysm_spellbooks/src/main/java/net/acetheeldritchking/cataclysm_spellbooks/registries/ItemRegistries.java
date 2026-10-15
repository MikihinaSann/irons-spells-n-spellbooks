package net.acetheeldritchking.cataclysm_spellbooks.registries;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.Collection;
import java.util.function.Supplier;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.AbyssalWarlockArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.AbyssalWarlockMaskItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.BloomStoneMageArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.CursiumMageArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.CursiumMageElytraArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.EngineerMageArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.ExcelsiusCooldownArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.ExcelsiusLegArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.ExcelsiusPowerArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.ExcelsiusResistArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.IgnisWizardArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.IgnisWizardElytraArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.MonstrousWizardHatArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.PharaohMageArmorItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.curios.LeviathansBlessingRing;
import net.acetheeldritchking.cataclysm_spellbooks.items.spellbooks.CodexOfMaliceSpellBook;
import net.acetheeldritchking.cataclysm_spellbooks.items.spellbooks.DesertSpellBook;
import net.acetheeldritchking.cataclysm_spellbooks.items.staffs.CSStaffTiers;
import net.acetheeldritchking.cataclysm_spellbooks.items.staffs.SpiritSundererStaff;
import net.acetheeldritchking.cataclysm_spellbooks.items.weapons.HellfireForgeItem;
import net.acetheeldritchking.cataclysm_spellbooks.items.weapons.MonstrousFlambergeItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemRegistries {
   public static final Items ITEMS = DeferredRegister.createItems("cataclysm_spellbooks");
   public static final DeferredHolder<Item, Item> ABYSS_SPELL_BOOK = ITEMS.register(
      "abyss_spell_book",
      () -> new SpellBook(12, ItemPropertiesHelper.equipment().fireResistant().stacksTo(1))
         .withSpellbookAttributes(
            new AttributeContainer[]{
               new AttributeContainer(CSAttributeRegistry.ABYSSAL_MAGIC_POWER, 0.3F, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE)
            }
         )
   );
   public static final DeferredHolder<Item, Item> DESERT_SPELL_BOOK = ITEMS.register("desert_spell_book", DesertSpellBook::new);
   public static final DeferredHolder<Item, Item> IGNIS_SPELL_BOOK = ITEMS.register(
      "ignis_spell_book",
      () -> new SpellBook(12, ItemPropertiesHelper.equipment().fireResistant().stacksTo(1))
         .withSpellbookAttributes(
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.3F, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE)
            }
         )
   );
   public static final DeferredHolder<Item, Item> CODEX_OF_MALICE = ITEMS.register("codex_of_malice_spell_book", CodexOfMaliceSpellBook::new);
   public static final DeferredHolder<Item, Item> BLOOM_STONE_STAFF = ITEMS.register(
      "bloom_stone_staff",
      () -> new StaffItem(ItemPropertiesHelper.equipment(1).attributes(ExtendedSwordItem.createAttributes(CSStaffTiers.BLOOM_STONE_STAFF)))
   );
   public static final DeferredHolder<Item, Item> CORAL_STAFF = ITEMS.register(
      "coral_staff", () -> new StaffItem(ItemPropertiesHelper.equipment(1).attributes(ExtendedSwordItem.createAttributes(CSStaffTiers.CORAL_STAFF)))
   );
   public static final DeferredHolder<Item, Item> FAKE_WUDJETS_STAFF = ITEMS.register(
      "fake_wudjets_staff",
      () -> new StaffItem(ItemPropertiesHelper.equipment(1).attributes(ExtendedSwordItem.createAttributes(CSStaffTiers.FAKE_WUDJETS_STAFF)))
   );
   public static final DeferredHolder<Item, Item> VOID_STAFF = ITEMS.register(
      "void_staff", () -> new StaffItem(ItemPropertiesHelper.equipment(1).attributes(ExtendedSwordItem.createAttributes(CSStaffTiers.VOID_STAFF)))
   );
   public static final DeferredHolder<Item, Item> SPIRIT_SUNDERER_STAFF = ITEMS.register("spirit_sunderer", SpiritSundererStaff::new);
   public static final DeferredHolder<Item, Item> SOUL_BRAZIER_STAFF = ITEMS.register(
      "soul_brazier", () -> new StaffItem(ItemPropertiesHelper.equipment(1).attributes(ExtendedSwordItem.createAttributes(CSStaffTiers.SOUL_BRAZIER_STAFF)))
   );
   public static final DeferredHolder<Item, Item> MONSTROUS_FLAMBERGE = ITEMS.register("monstrous_flamberge", MonstrousFlambergeItem::new);
   public static final DeferredHolder<Item, Item> HELLFIRE_FORGE = ITEMS.register("hellfire_forge", HellfireForgeItem::new);
   public static final DeferredHolder<Item, Item> ABYSSAL_RUNE = ITEMS.register("abyssal_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> ABYSSAL_UPGRADE_ORB = ITEMS.register(
      "abyssal_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, CSUpgradeOrbTypeRegistry.ABYSSAL_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> TECHNOMANCY_UPGRADE_ORB = ITEMS.register(
      "technomancy_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material()
            .rarity(Rarity.UNCOMMON)
            .component(ComponentRegistry.UPGRADE_ORB_TYPE, CSUpgradeOrbTypeRegistry.TECHNOMANCY_SPELL_POWER)
      )
   );
   public static final Supplier<CurioBaseItem> LEVIATHANS_BLESSING = ITEMS.register("leviathans_blessing", LeviathansBlessingRing::new);
   public static final DeferredHolder<Item, Item> IGNITIUM_WIZARD_HELMET = ITEMS.register(
      "ignis_helmet",
      () -> new IgnisWizardArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.HELMET.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> IGNITIUM_WIZARD_CHESTPLATE = ITEMS.register(
      "ignis_chestplate",
      () -> new IgnisWizardArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> IGNITIUM_WIZARD_CHESTPLATE_ELYTRA = ITEMS.register(
      "ignis_chestplate_elytra",
      () -> new IgnisWizardElytraArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> IGNITIUM_WIZARD_LEGGINGS = ITEMS.register(
      "ignis_leggings",
      () -> new IgnisWizardArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> IGNITIUM_WIZARD_BOOTS = ITEMS.register(
      "ignis_boots",
      () -> new IgnisWizardArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.BOOTS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> ABYSSAL_WARLOCK_HELMET = ITEMS.register(
      "abyssal_warlock_helmet",
      () -> new AbyssalWarlockArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.HELMET.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> ABYSSAL_WARLOCK_MASK = ITEMS.register(
      "abyssal_warlock_mask",
      () -> new AbyssalWarlockMaskItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> ABYSSAL_WARLOCK_CHESTPLATE = ITEMS.register(
      "abyssal_warlock_chestplate",
      () -> new AbyssalWarlockArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> ABYSSAL_WARLOCK_LEGGINGS = ITEMS.register(
      "abyssal_warlock_leggings",
      () -> new AbyssalWarlockArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> ABYSSAL_WARLOCK_BOOTS = ITEMS.register(
      "abyssal_warlock_boots",
      () -> new AbyssalWarlockArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.BOOTS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> CURSIUM_MAGE_HELMET = ITEMS.register(
      "cursium_mage_circlet",
      () -> new CursiumMageArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.HELMET.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> CURSIUM_MAGE_CHESTPLATE = ITEMS.register(
      "cursium_mage_chestplate",
      () -> new CursiumMageArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> CURSIUM_MAGE_CHESTPLATE_ELYTRA = ITEMS.register(
      "cursium_mage_elytra",
      () -> new CursiumMageElytraArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> CURSIUM_MAGE_LEGGINGS = ITEMS.register(
      "cursium_mage_skirt",
      () -> new CursiumMageArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> CURSIUM_MAGE_BOOTS = ITEMS.register(
      "cursium_mage_boots",
      () -> new CursiumMageArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.BOOTS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> PHARAOH_MAGE_HELMET = ITEMS.register(
      "pharaoh_helmet",
      () -> new PharaohMageArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.HELMET.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> PHARAOH_MAGE_CHESTPLATE = ITEMS.register(
      "pharaoh_chestplate",
      () -> new PharaohMageArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> PHARAOH_MAGE_LEGGINGS = ITEMS.register(
      "pharaoh_leggings",
      () -> new PharaohMageArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> PHARAOH_MAGE_BOOTS = ITEMS.register(
      "pharaoh_greaves",
      () -> new PharaohMageArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.BOOTS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> BLOOM_STONE_HAT = ITEMS.register(
      "bloom_stone_hat",
      () -> new BloomStoneMageArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.HELMET.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> BLOOM_STONE_CHESTPLATE = ITEMS.register(
      "bloom_stone_chestplate",
      () -> new BloomStoneMageArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> BLOOM_STONE_SKIRT = ITEMS.register(
      "bloom_stone_skirt",
      () -> new BloomStoneMageArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> BLOOM_STONE_GREAVES = ITEMS.register(
      "bloom_stone_greaves",
      () -> new BloomStoneMageArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.BOOTS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> MONSTROUS_WIZARD_HAT = ITEMS.register(
      "monstrous_wizard_hat",
      () -> new MonstrousWizardHatArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.HELMET.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> ENGINEER_MAGE_HOOD = ITEMS.register(
      "engineer_hood",
      () -> new EngineerMageArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ENGINEER_MAGE_SUIT = ITEMS.register(
      "engineer_suit",
      () -> new EngineerMageArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ENGINEER_MAGE_LEGGINGS = ITEMS.register(
      "engineer_leggings",
      () -> new EngineerMageArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.LEGGINGS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ENGINEER_MAGE_BOOTS = ITEMS.register(
      "engineer_boots", () -> new EngineerMageArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.BOOTS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> EXCELSIUS_SPEED_HELMET = ITEMS.register(
      "excelsius_speed_visors",
      () -> new ExcelsiusCooldownArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.HELMET.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> EXCELSIUS_SPEED_CHESTPLATE = ITEMS.register(
      "excelsius_speed_chestplate",
      () -> new ExcelsiusCooldownArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> EXCELSIUS_POWER_HELMET = ITEMS.register(
      "excelsius_power_visors",
      () -> new ExcelsiusPowerArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.HELMET.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> EXCELSIUS_POWER_CHESTPLATE = ITEMS.register(
      "excelsius_power_chestplate",
      () -> new ExcelsiusPowerArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> EXCELSIUS_RESIST_HELMET = ITEMS.register(
      "excelsius_resist_visors",
      () -> new ExcelsiusResistArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.HELMET.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> EXCELSIUS_RESIST_CHESTPLATE = ITEMS.register(
      "excelsius_resist_chestplate",
      () -> new ExcelsiusResistArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> EXCELSIUS_WARLOCK_LEGGINGS = ITEMS.register(
      "excelsius_leggings",
      () -> new ExcelsiusLegArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> EXCELSIUS_WARLOCK_BOOTS = ITEMS.register(
      "excelsius_greaves",
      () -> new ExcelsiusLegArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
            .fireResistant()
            .durability(Type.BOOTS.getDurability(40))
      )
   );

   public static Collection<DeferredHolder<Item, ? extends Item>> getCSItems() {
      return ITEMS.getEntries();
   }

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
