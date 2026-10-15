package net.acetheeldritchking.discerning_the_eldritch.registries;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.SpellSlotUpgradeItem;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.Collection;
import java.util.function.Supplier;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.CrimsonStagArmorItem;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.EldritchWarlockArmorItem;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.EldritchWarlockHelmetItem;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.EldritchWarlockMaskItem;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.AscendedArmorItem;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.CapelessAscendedArmorItem;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.GeckolibCrimsonStagArmorItem;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.GeckolibEldritchWarlockArmorItem;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.GeckolibEldritchWarlockHelmetItem;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.GeckolibEldritchWarlockMaskItem;
import net.acetheeldritchking.discerning_the_eldritch.items.curios.AmuletOfSculkTreasureCurio;
import net.acetheeldritchking.discerning_the_eldritch.items.curios.CastersMantleCurio;
import net.acetheeldritchking.discerning_the_eldritch.items.curios.EchoVibrationRingCurio;
import net.acetheeldritchking.discerning_the_eldritch.items.curios.FrostbourneSheathCurio;
import net.acetheeldritchking.discerning_the_eldritch.items.curios.IronboundFeatherCurio;
import net.acetheeldritchking.discerning_the_eldritch.items.curios.KingsEffigyCurio;
import net.acetheeldritchking.discerning_the_eldritch.items.curios.PyriumSheathCurio;
import net.acetheeldritchking.discerning_the_eldritch.items.curios.RazorSheathCurio;
import net.acetheeldritchking.discerning_the_eldritch.items.curios.RiftRipperEmblemCurio;
import net.acetheeldritchking.discerning_the_eldritch.items.custom.FadingSculkLantern;
import net.acetheeldritchking.discerning_the_eldritch.items.spellbooks.BlackBookSpellbook;
import net.acetheeldritchking.discerning_the_eldritch.items.spellbooks.DiaryOfDecaySpellbook;
import net.acetheeldritchking.discerning_the_eldritch.items.spellbooks.GuardianGuidebookSpellbook;
import net.acetheeldritchking.discerning_the_eldritch.items.spellbooks.StormWeaverTomeSpellbook;
import net.acetheeldritchking.discerning_the_eldritch.items.staffs.BrokenLegendsStaff;
import net.acetheeldritchking.discerning_the_eldritch.items.staffs.DTEStaffTier;
import net.acetheeldritchking.discerning_the_eldritch.items.staffs.StaffOfVehemenceStaffItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.BrokenLegendsBlade;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.CataclysmBladeAwakenedItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.CataclysmBladeItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.DTEWeaponTiers;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.DevourerAxeAwakenedItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.DevourerAxeItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.IceSpearItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.MourningStarMaceAwakenedItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.MourningStarMaceItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.SoulFireScytheItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.TheSnowgraveItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.YmirSwordItem;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTERarities;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemRegistries {
   public static final Items ITEMS = DeferredRegister.createItems("discerning_the_eldritch");
   public static final DeferredHolder<Item, Item> BLACK_BOOK_SPELLBOOK = ITEMS.register("black_book_spellbook", BlackBookSpellbook::new);
   public static final DeferredHolder<Item, Item> THE_APOCRYPHA_SPELLBOOK = ITEMS.register(
      "the_apocrypha_spellbook",
      () -> new SpellBook(13, ItemPropertiesHelper.equipment().rarity((Rarity)DTERarities.APOTHIC_RARITY_PROXY.getValue()).fireResistant().stacksTo(1))
         .withSpellbookAttributes(
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.2F, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE)
            }
         )
   );
   public static final DeferredHolder<Item, Item> TEMPESTUOUS_TOME = ITEMS.register("tempestuous_tome", StormWeaverTomeSpellbook::new);
   public static final DeferredHolder<Item, Item> FROZEN_FOLIO = ITEMS.register(
      "frozen_folio",
      () -> new SpellBook(8)
         .withSpellbookAttributes(
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.1F, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE)
            }
         )
   );
   public static final DeferredHolder<Item, Item> DIARY_OF_DECAY = ITEMS.register("diary_of_decay", DiaryOfDecaySpellbook::new);
   public static final DeferredHolder<Item, Item> GUARDIANS_GAZE = ITEMS.register("guardian_guidebook", GuardianGuidebookSpellbook::new);
   public static final DeferredHolder<Item, Item> STAFF_OF_VEHEMENCE = ITEMS.register("staff_of_eldritch", StaffOfVehemenceStaffItem::new);
   public static final DeferredHolder<Item, Item> STAFF_OF_ASCENSION = ITEMS.register(
      "staff_of_ascension",
      () -> new StaffItem(
         ItemPropertiesHelper.equipment(1).fireResistant().rarity(Rarity.EPIC).attributes(ExtendedSwordItem.createAttributes(DTEStaffTier.STAFF_OF_ASCENSION))
      )
   );
   public static final DeferredHolder<Item, Item> BROKEN_LEGENDS_STAFF = ITEMS.register("broken_legends_staff", BrokenLegendsStaff::new);
   public static final DeferredHolder<Item, Item> DEEP_GREATSWORD = ITEMS.register(
      "deep_greatsword",
      () -> new MagicSwordItem(
         DTEWeaponTiers.DEEP_GREATSWORD,
         ItemPropertiesHelper.equipment(1).rarity(Rarity.RARE).attributes(ExtendedSwordItem.createAttributes(DTEWeaponTiers.DEEP_GREATSWORD)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.PLANAR_SIGHT_SPELL, 4)})
      )
   );
   public static final DeferredHolder<Item, Item> GOD_SPEAR = ITEMS.register(
      "god_spear",
      () -> new MagicSwordItem(
         DTEWeaponTiers.GOD_SPEAR,
         ItemPropertiesHelper.equipment(1).rarity(Rarity.UNCOMMON).attributes(ExtendedSwordItem.createAttributes(DTEWeaponTiers.GOD_SPEAR)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.DIVINE_SMITE_SPELL, 6)})
      )
   );
   public static final DeferredHolder<Item, Item> YMIR = ITEMS.register("ymir", YmirSwordItem::new);
   public static final DeferredHolder<Item, Item> ICE_SPEAR = ITEMS.register("ice_spear", IceSpearItem::new);
   public static final DeferredHolder<Item, Item> RITUAL_DAGGER = ITEMS.register(
      "ritual_dagger",
      () -> new MagicSwordItem(
         DTEWeaponTiers.RITUAL_DAGGER,
         ItemPropertiesHelper.equipment(1).rarity(Rarity.RARE).attributes(ExtendedSwordItem.createAttributes(DTEWeaponTiers.RITUAL_DAGGER)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.SACRIFICE_SPELL, 1)})
      )
   );
   public static final DeferredHolder<Item, Item> FORLORN_RAPIER = ITEMS.register(
      "forlorn_rapier",
      () -> new MagicSwordItem(
         DTEWeaponTiers.FORLORN_RAPIER,
         ItemPropertiesHelper.equipment(1).rarity(Rarity.RARE).attributes(ExtendedSwordItem.createAttributes(DTEWeaponTiers.FORLORN_RAPIER)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.COUNTERSPELL_SPELL, 1)})
      )
   );
   public static final DeferredHolder<Item, Item> BROKEN_LEGENDS_BLADE = ITEMS.register("broken_legends_blade", BrokenLegendsBlade::new);
   public static final DeferredHolder<Item, Item> FORSAKEN_FLAMBERGE = ITEMS.register(
      "forsaken_flamberge",
      () -> new ExtendedSwordItem(
         DTEWeaponTiers.FORSAKEN_FLAMBERGE,
         ItemPropertiesHelper.equipment(1).rarity(Rarity.RARE).attributes(ExtendedSwordItem.createAttributes(DTEWeaponTiers.FORSAKEN_FLAMBERGE))
      )
   );
   public static final DeferredHolder<Item, Item> SNOWGRAVE = ITEMS.register("snowgrave", TheSnowgraveItem::new);
   public static final DeferredHolder<Item, Item> SOUL_FIRE_SCYTHE = ITEMS.register("soul_fire_scythe", SoulFireScytheItem::new);
   public static final DeferredHolder<Item, Item> CATACLYSM = ITEMS.register("cataclysm", CataclysmBladeItem::new);
   public static final DeferredHolder<Item, Item> CATACLYSM_AWAKENED = ITEMS.register("cataclysm_awakened", CataclysmBladeAwakenedItem::new);
   public static final DeferredHolder<Item, Item> DEVOURER = ITEMS.register("devourer", DevourerAxeItem::new);
   public static final DeferredHolder<Item, Item> DEVOURER_AWAKENED = ITEMS.register("devourer_awakened", DevourerAxeAwakenedItem::new);
   public static final DeferredHolder<Item, Item> MOURNING_STAR = ITEMS.register("mourning_star", MourningStarMaceItem::new);
   public static final DeferredHolder<Item, Item> MOURNING_STAR_AWAKENED = ITEMS.register("mourning_star_awakened", MourningStarMaceAwakenedItem::new);
   public static final DeferredItem<Item> CORRUPTED_CLOTH = ITEMS.register("corrupted_cloth", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> ELDRITCH_RUNE = ITEMS.register("eldritch_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> ELDRITCH_UPGRADE_TEMPLATE = ITEMS.register(
      "eldritch_upgrade_template", () -> new Item(ItemPropertiesHelper.material().stacksTo(16).rarity(Rarity.EPIC))
   );
   public static final DeferredHolder<Item, Item> RITUAL_RUNE = ITEMS.register("ritual_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> APOTHIC_KEY = ITEMS.register("apothic_key", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> ASCENDED_KEY = ITEMS.register("ascended_key", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> RITUAL_KEY = ITEMS.register("ritual_key", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> ELDRITCH_SOUL_SHARD = ITEMS.register(
      "eldritch_soul_shard", () -> new Item(ItemPropertiesHelper.material().rarity((Rarity)DTERarities.APOTHIC_RARITY_PROXY.getValue()))
   );
   public static final DeferredHolder<Item, Item> FADING_SCULK_LANTERN = ITEMS.register("fading_sculk_lantern", FadingSculkLantern::new);
   public static final DeferredHolder<Item, Item> SHARD_OF_MALICE = ITEMS.register(
      "shard_of_malice", () -> new Item(ItemPropertiesHelper.material().stacksTo(16).rarity((Rarity)ASRarities.ACCURSED_RARITY_PROXY.getValue()))
   );
   public static final DeferredHolder<Item, Item> BOTTLE_OF_LIQUID_MALICE = ITEMS.register(
      "bottle_of_liquid_malice", () -> new Item(ItemPropertiesHelper.material().stacksTo(16).rarity((Rarity)ASRarities.ACCURSED_RARITY_PROXY.getValue()))
   );
   public static final DeferredHolder<Item, Item> FORBIDDEN_SPELL_IMPROVEMENT = ITEMS.register(
      "forbidden_spell_improvement", () -> new SpellSlotUpgradeItem(15)
   );
   public static final DeferredHolder<Item, Item> SOUL_EMBER = ITEMS.register(
      "soul_ember", () -> new Item(ItemPropertiesHelper.material().rarity((Rarity)ASRarities.AQUATIC_RARITY_PROXY.getValue()))
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_UPGRADE_ORB = ITEMS.register(
      "eldritch_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, DTEUpgradeOrbTypeRegistry.ELDRITCH_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> RITUAL_UPGRADE_ORB = ITEMS.register(
      "ritual_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, DTEUpgradeOrbTypeRegistry.RITUAL_SPELL_POWER)
      )
   );
   public static final Supplier<CurioBaseItem> ECHO_VIBRATION_RING = ITEMS.register("echo_vibration_ring", EchoVibrationRingCurio::new);
   public static final Supplier<CurioBaseItem> KINGS_EFFIGY = ITEMS.register("kings_effigy", KingsEffigyCurio::new);
   public static final Supplier<CurioBaseItem> AMULET_OF_SCULK_TREASURE = ITEMS.register("amulet_of_sculk_treasure", AmuletOfSculkTreasureCurio::new);
   public static final Supplier<CurioBaseItem> CASTERS_MANTLE = ITEMS.register("casters_mantle", CastersMantleCurio::new);
   public static final Supplier<CurioBaseItem> IRONBOUND_FEATHER = ITEMS.register("ironbound_feather", IronboundFeatherCurio::new);
   public static final Supplier<CurioBaseItem> PYRIUM_SHEATH = ITEMS.register("pyrium_sheath", PyriumSheathCurio::new);
   public static final Supplier<CurioBaseItem> FROSTBOURNE_SHEATH = ITEMS.register("frostbourne_sheath", FrostbourneSheathCurio::new);
   public static final Supplier<CurioBaseItem> RIFT_RIPPER_EMBLEM = ITEMS.register("rift_ripper_emblem", RiftRipperEmblemCurio::new);
   public static final Supplier<CurioBaseItem> RAZOR_SHEATH = ITEMS.register("razor_sheath", RazorSheathCurio::new);
   public static final DeferredHolder<Item, Item> ELDRITCH_WARLOCK_HOOD = ITEMS.register(
      "eldritch_warlock_hood",
      () -> new EldritchWarlockArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_WARLOCK_ROBES = ITEMS.register(
      "eldritch_warlock_robes",
      () -> new EldritchWarlockArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_WARLOCK_LEGGINGS = ITEMS.register(
      "eldritch_warlock_leggings",
      () -> new EldritchWarlockArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.LEGGINGS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_WARLOCK_GREAVES = ITEMS.register(
      "eldritch_warlock_greaves",
      () -> new EldritchWarlockArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.BOOTS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_WARLOCK_MASK = ITEMS.register(
      "eldritch_warlock_mask",
      () -> new EldritchWarlockMaskItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_WARLOCK_HELMET = ITEMS.register(
      "eldritch_warlock_helmet",
      () -> new EldritchWarlockHelmetItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> CRIMSON_STAG_ANTLERS = ITEMS.register(
      "crimson_stag_antlers",
      () -> new CrimsonStagArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> CRIMSON_STAG_ROBES = ITEMS.register(
      "crimson_stag_robes",
      () -> new CrimsonStagArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> CRIMSON_STAG_LEGGINGS = ITEMS.register(
      "crimson_stag_leggings",
      () -> new CrimsonStagArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.LEGGINGS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> CRIMSON_STAG_BOOTS = ITEMS.register(
      "crimson_stag_boots",
      () -> new CrimsonStagArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.BOOTS.getDurability(40)))
   );
   public static final DeferredItem<Item> ACOLYTE_SPAWN_EGG = ITEMS.register(
      "apothic_acolyte_spawn_egg", () -> new DeferredSpawnEggItem(DTEEntityRegistry.APOTHIC_ACOLYTE, 1973277, 5147788, new Properties())
   );
   public static final DeferredItem<Item> SUMMONER_SPAWN_EGG = ITEMS.register(
      "apothic_summoner_spawn_egg", () -> new DeferredSpawnEggItem(DTEEntityRegistry.APOTHIC_SUMMONER, 1973277, 9324137, new Properties())
   );
   public static final DeferredItem<Item> CRUSADER_SPAWN_EGG = ITEMS.register(
      "apothic_crusader_spawn_egg", () -> new DeferredSpawnEggItem(DTEEntityRegistry.APOTHIC_CRUSADER, 1973277, 12556098, new Properties())
   );
   public static final DeferredItem<Item> ASCENDED_CULTIST_SPAWN_EGG = ITEMS.register(
      "ascended_cultist_spawn_egg", () -> new DeferredSpawnEggItem(DTEEntityRegistry.ASCENDED_ONE_CULTIST, 1973277, 10554050, new Properties())
   );
   public static final DeferredItem<Item> BLOOD_CULTIST_CAPTAIN_SPAWN_EGG = ITEMS.register(
      "blood_cultist_captain_spawn_egg", () -> new DeferredSpawnEggItem(DTEEntityRegistry.BLOOD_CULTIST_CAPTAIN, 4203295, 10357001, new Properties())
   );
   public static final DeferredItem<Item> BLOOD_CULTIST_MAGE_SPAWN_EGG = ITEMS.register(
      "blood_cultist_mage_spawn_egg", () -> new DeferredSpawnEggItem(DTEEntityRegistry.BLOOD_CULTIST_MAGE, 4203295, 16209219, new Properties())
   );
   public static final DeferredItem<Item> BLOOD_CULTIST_WITCH_SPAWN_EGG = ITEMS.register(
      "blood_cultist_witch_spawn_egg", () -> new DeferredSpawnEggItem(DTEEntityRegistry.BLOOD_CULTIST_WITCH, 4203295, 10231815, new Properties())
   );
   public static final DeferredItem<Item> BLOOD_MATRIARCH_SPAWN_EGG = ITEMS.register(
      "blood_matriarch_spawn_egg", () -> new DeferredSpawnEggItem(DTEEntityRegistry.BLOOD_MATRIARCH, 4203295, 16066367, new Properties())
   );
   public static final DeferredItem<Item> ELECTROMANCER_SPAWN_EGG = ITEMS.register(
      "electromancer_spawn_egg", () -> new DeferredSpawnEggItem(DTEEntityRegistry.ELECTROMANCER_MAGE, 16097366, 7729392, new Properties())
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ELDRITCH_WARLOCK_HOOD = ITEMS.register(
      "gecko_eldritch_warlock_hood",
      () -> new GeckolibEldritchWarlockArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ELDRITCH_WARLOCK_ROBES = ITEMS.register(
      "gecko_eldritch_warlock_robes",
      () -> new GeckolibEldritchWarlockArmorItem(
         Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(40))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ELDRITCH_WARLOCK_LEGGINGS = ITEMS.register(
      "gecko_eldritch_warlock_leggings",
      () -> new GeckolibEldritchWarlockArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.LEGGINGS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ELDRITCH_WARLOCK_GREAVES = ITEMS.register(
      "gecko_eldritch_warlock_greaves",
      () -> new GeckolibEldritchWarlockArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.BOOTS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ELDRITCH_WARLOCK_MASK = ITEMS.register(
      "gecko_eldritch_warlock_mask",
      () -> new GeckolibEldritchWarlockMaskItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ELDRITCH_WARLOCK_HELMET = ITEMS.register(
      "gecko_eldritch_warlock_helmet",
      () -> new GeckolibEldritchWarlockHelmetItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ASCENDED_ONE_HOOD = ITEMS.register(
      "ascended_one_hood",
      () -> new AscendedArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ASCENDED_ONE_ROBES = ITEMS.register(
      "ascended_one_robes",
      () -> new AscendedArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> CAPELESS_ASCENDED_ONE_ROBES = ITEMS.register(
      "capeless_ascended_one_robes",
      () -> new CapelessAscendedArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ASCENDED_ONE_LEGGINGS = ITEMS.register(
      "ascended_one_leggings",
      () -> new AscendedArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.LEGGINGS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> ASCENDED_ONE_GREAVES = ITEMS.register(
      "ascended_one_greaves",
      () -> new AscendedArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.BOOTS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_CRIMSON_STAG_ANTLERS = ITEMS.register(
      "gecko_crimson_stag_antlers",
      () -> new GeckolibCrimsonStagArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_CRIMSON_STAG_ROBES = ITEMS.register(
      "gecko_crimson_stag_robes",
      () -> new GeckolibCrimsonStagArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_CRIMSON_STAG_LEGGINGS = ITEMS.register(
      "gecko_crimson_stag_leggings",
      () -> new GeckolibCrimsonStagArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.LEGGINGS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_CRIMSON_STAG_BOOTS = ITEMS.register(
      "gecko_crimson_stag_boots",
      () -> new GeckolibCrimsonStagArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.BOOTS.getDurability(40)))
   );

   public static Collection<DeferredHolder<Item, ? extends Item>> getDTEItems() {
      return ITEMS.getEntries();
   }

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
