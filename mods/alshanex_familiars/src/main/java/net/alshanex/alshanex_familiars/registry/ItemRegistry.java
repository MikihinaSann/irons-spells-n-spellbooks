package net.alshanex.alshanex_familiars.registry;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.Collection;
import java.util.function.Supplier;
import net.alshanex.alshanex_familiars.item.BardHarp;
import net.alshanex.alshanex_familiars.item.BottledSoul;
import net.alshanex.alshanex_familiars.item.MemoryFragment;
import net.alshanex.alshanex_familiars.item.MonocleItem;
import net.alshanex.alshanex_familiars.item.MysteriousOrb;
import net.alshanex.alshanex_familiars.item.PandoraBox;
import net.alshanex.alshanex_familiars.item.PetBedBlockItem;
import net.alshanex.alshanex_familiars.item.TinyTotem;
import net.alshanex.alshanex_familiars.item.TruthMirror;
import net.alshanex.alshanex_familiars.item.armor.BardArmorItem;
import net.alshanex.alshanex_familiars.item.consumables.BrokenShield;
import net.alshanex.alshanex_familiars.item.consumables.LifeGem;
import net.alshanex.alshanex_familiars.item.consumables.PoisonVial;
import net.alshanex.alshanex_familiars.item.consumables.RustyHelmet;
import net.alshanex.alshanex_familiars.item.consumables.StrangeMushroom;
import net.alshanex.alshanex_familiars.item.curios.ArchmageTrinket;
import net.alshanex.alshanex_familiars.item.curios.BardTrinket;
import net.alshanex.alshanex_familiars.item.curios.ClericTrinket;
import net.alshanex.alshanex_familiars.item.curios.DragonWarriorTrinket;
import net.alshanex.alshanex_familiars.item.curios.DruidTrinket;
import net.alshanex.alshanex_familiars.item.curios.EvasionCurio;
import net.alshanex.alshanex_familiars.item.curios.FamiliarSpellbook;
import net.alshanex.alshanex_familiars.item.curios.FrostlingTrinket;
import net.alshanex.alshanex_familiars.item.curios.HealingCurio;
import net.alshanex.alshanex_familiars.item.curios.HunterTrinket;
import net.alshanex.alshanex_familiars.item.curios.IllusionistTrinket;
import net.alshanex.alshanex_familiars.item.curios.InvisibilityCurio;
import net.alshanex.alshanex_familiars.item.curios.MageTrinket;
import net.alshanex.alshanex_familiars.item.curios.NecromancerTrinket;
import net.alshanex.alshanex_familiars.item.curios.PlagueTrinket;
import net.alshanex.alshanex_familiars.item.curios.ScorcherTrinket;
import net.alshanex.alshanex_familiars.item.curios.StatueCurio;
import net.alshanex.alshanex_familiars.item.curios.SummonerTrinket;
import net.alshanex.alshanex_familiars.item.shards.ArchmageShard;
import net.alshanex.alshanex_familiars.item.shards.BardShard;
import net.alshanex.alshanex_familiars.item.shards.ClericShard;
import net.alshanex.alshanex_familiars.item.shards.DragonWarriorShard;
import net.alshanex.alshanex_familiars.item.shards.DruidShard;
import net.alshanex.alshanex_familiars.item.shards.FrostlingShard;
import net.alshanex.alshanex_familiars.item.shards.HunterShard;
import net.alshanex.alshanex_familiars.item.shards.IllusionistShard;
import net.alshanex.alshanex_familiars.item.shards.MageShard;
import net.alshanex.alshanex_familiars.item.shards.NecromancerShard;
import net.alshanex.alshanex_familiars.item.shards.PlagueShard;
import net.alshanex.alshanex_familiars.item.shards.ScorcherShard;
import net.alshanex.alshanex_familiars.item.shards.SummonerShard;
import net.alshanex.familiarslib.compat.FamiliarsCurios;
import net.alshanex.familiarslib.registry.FoodRegistry;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableComponent;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableSystem.ConsumableType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "alshanex_familiars");
   public static final DeferredHolder<Item, Item> SOUND_RUNE = ITEMS.register("sound_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> SOUND_UPGRADE_ORB = ITEMS.register(
      "sound_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, FUpgradeOrbRegistry.SOUND_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> FAMILIAR_TOME = ITEMS.register(
      "familiar_tome", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
   );
   public static final DeferredHolder<Item, Item> FAMILIAR_SOUL = ITEMS.register("pet_soul", () -> new BottledSoul());
   public static final Supplier<CurioBaseItem> FOREST_TRINKET = ITEMS.register(
      "forest_trinket",
      () -> new DruidTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.NATURE_MAGIC_RESIST, 0.15, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> HUNTER_TRINKET = ITEMS.register(
      "hunter_trinket",
      () -> new HunterTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> NECROMANCER_TRINKET = ITEMS.register(
      "necromancer_trinket",
      () -> new NecromancerTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.SUMMON_DAMAGE, 0.15, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> SUMMONER_TRINKET = ITEMS.register(
      "summoner_trinket",
      () -> new SummonerTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.ELDRITCH_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> ARCHMAGE_TRINKET = ITEMS.register(
      "archmage_trinket",
      () -> new ArchmageTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.05, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> MAGE_TRINKET = ITEMS.register(
      "mage_trinket",
      () -> new MageTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.LIGHTNING_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> ILLUSIONIST_TRINKET = ITEMS.register(
      "illusionist_trinket",
      () -> new IllusionistTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.EVOCATION_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> SCORCHER_TRINKET = ITEMS.register(
      "scorcher_trinket",
      () -> new ScorcherTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.FIRE_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> CLERIC_TRINKET = ITEMS.register(
      "cleric_trinket",
      () -> new ClericTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.HOLY_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> PLAGUE_TRINKET = ITEMS.register(
      "plague_trinket",
      () -> new PlagueTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.NATURE_MAGIC_RESIST, 0.05, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> BARD_TRINKET = ITEMS.register(
      "bard_trinket",
      () -> new BardTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(net.alshanex.familiarslib.registry.AttributeRegistry.SOUND_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(net.alshanex.familiarslib.registry.AttributeRegistry.SOUND_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> FROSTLING_TRINKET = ITEMS.register(
      "frostling_trinket",
      () -> new FrostlingTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.ICE_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> DRAGON_WARRIOR_TRINKET = ITEMS.register(
      "dragon_warrior_trinket",
      () -> new DragonWarriorTrinket()
         .withAttributes(
            FamiliarsCurios.FAMILIAR_TRINKET_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.ENDER_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> HEALING_CURIO = ITEMS.register(
      "healing_curio",
      () -> new HealingCurio(ItemPropertiesHelper.equipment(1))
         .withAttributes(Curios.RING_SLOT, new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.05, Operation.ADD_MULTIPLIED_BASE)})
   );
   public static final Supplier<CurioBaseItem> STATUE_CURIO = ITEMS.register(
      "statue_curio",
      () -> new StatueCurio(ItemPropertiesHelper.equipment(1))
         .withAttributes(
            Curios.RING_SLOT, new AttributeContainer[]{new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.05, Operation.ADD_MULTIPLIED_BASE)}
         )
   );
   public static final Supplier<CurioBaseItem> INVISIBILITY_CURIO = ITEMS.register(
      "invisibility_curio",
      () -> new InvisibilityCurio(ItemPropertiesHelper.equipment(1))
         .withAttributes(Curios.RING_SLOT, new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, 50.0, Operation.ADD_VALUE)})
   );
   public static final Supplier<CurioBaseItem> EVASION_CURIO = ITEMS.register(
      "evasion_curio",
      () -> new EvasionCurio(ItemPropertiesHelper.equipment(1))
         .withAttributes(
            Curios.RING_SLOT, new AttributeContainer[]{new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE)}
         )
   );
   public static final DeferredHolder<Item, Item> SOUND_SPELL_BOOK = ITEMS.register(
      "sound_spell_book",
      () -> new SpellBook(10)
         .withSpellbookAttributes(
            new AttributeContainer[]{
               new AttributeContainer(net.alshanex.familiarslib.registry.AttributeRegistry.SOUND_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE)
            }
         )
   );
   public static final DeferredHolder<Item, Item> FAMILIAR_SPELLBOOK = ITEMS.register("familiar_spellbook", FamiliarSpellbook::new);
   public static final DeferredHolder<Item, Item> BROKEN_SHIELD = ITEMS.register("broken_shield", BrokenShield::new);
   public static final DeferredHolder<Item, Item> LIFE_GEM = ITEMS.register("life_gem", LifeGem::new);
   public static final DeferredHolder<Item, Item> RUSTY_HELMET = ITEMS.register("rusty_helmet", RustyHelmet::new);
   public static final DeferredHolder<Item, Item> STRANGE_MUSHROOM = ITEMS.register("strange_mushroom", StrangeMushroom::new);
   public static final DeferredHolder<Item, Item> POISON_VIAL = ITEMS.register("poison_vial", PoisonVial::new);
   public static final Supplier<CurioBaseItem> PANDORA_BOX = ITEMS.register(
      "pandora_box",
      () -> new PandoraBox(new Properties().stacksTo(1).rarity(Rarity.EPIC)).withAttributes(FamiliarsCurios.FAMILIAR_TRINKET_SLOT, new AttributeContainer[0])
   );
   public static final DeferredHolder<Item, Item> SPIDER_FANG = ITEMS.register(
      "spider_fang", () -> new Item(new Properties().stacksTo(64).rarity(Rarity.COMMON))
   );
   public static final DeferredHolder<Item, Item> MYSTERIOUS_ORB = ITEMS.register(
      "mysterious_orb", () -> new MysteriousOrb(new Properties().stacksTo(1).rarity(Rarity.RARE))
   );
   public static final DeferredHolder<Item, Item> BARD_HARP = ITEMS.register("bard_harp", BardHarp::new);
   public static final DeferredHolder<Item, Item> BLUEBERRY = ITEMS.register(
      "crystal_berry", () -> new Item(new Properties().stacksTo(64).rarity(Rarity.RARE).food(FoodRegistry.BLUEBERRY))
   );
   public static final DeferredHolder<Item, Item> MEMORY_FRAGMENT = ITEMS.register("memory_fragment", MemoryFragment::new);
   public static final Supplier<Item> PET_BED = ITEMS.register("pet_bed", () -> new PetBedBlockItem(BlockRegistry.PET_BED.get(), new Properties()));
   public static final DeferredHolder<Item, Item> ARCHMAGE_STATUE = ITEMS.register(
      "pet_archmage_statue_model", () -> new BlockItem(BlockRegistry.ARCHMAGE_STATUE.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> HUNTER_STATUE = ITEMS.register(
      "pet_beastmaster_statue_model", () -> new BlockItem(BlockRegistry.HUNTER_STATUE.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> DRUID_STATUE = ITEMS.register(
      "pet_druid_statue_model", () -> new BlockItem(BlockRegistry.DRUID_STATUE.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> CLERIC_STATUE = ITEMS.register(
      "pet_cleric_statue_model", () -> new BlockItem(BlockRegistry.CLERIC_STATUE.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> RITUAL_CORE = ITEMS.register(
      "ritual_core", () -> new BlockItem(BlockRegistry.RITUAL_CORE.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> ARCHMAGE_RUNE = ITEMS.register("archmage_shard", ArchmageShard::new);
   public static final DeferredHolder<Item, Item> DRUID_RUNE = ITEMS.register("druid_shard", DruidShard::new);
   public static final DeferredHolder<Item, Item> HUNTER_RUNE = ITEMS.register("hunter_shard", HunterShard::new);
   public static final DeferredHolder<Item, Item> ILLUSIONIST_RUNE = ITEMS.register("illusionist_shard", IllusionistShard::new);
   public static final DeferredHolder<Item, Item> MAGE_RUNE = ITEMS.register("lightning_mage_shard", MageShard::new);
   public static final DeferredHolder<Item, Item> SUMMONER_RUNE = ITEMS.register("summoner_shard", SummonerShard::new);
   public static final DeferredHolder<Item, Item> FROSTLING_RUNE = ITEMS.register("frostling_shard", FrostlingShard::new);
   public static final DeferredHolder<Item, Item> BARD_RUNE = ITEMS.register("bard_shard", BardShard::new);
   public static final DeferredHolder<Item, Item> CLERIC_RUNE = ITEMS.register("cleric_shard", ClericShard::new);
   public static final DeferredHolder<Item, Item> NECROMANCER_RUNE = ITEMS.register("necromancer_shard", NecromancerShard::new);
   public static final DeferredHolder<Item, Item> PLAGUE_RUNE = ITEMS.register("plague_shard", PlagueShard::new);
   public static final DeferredHolder<Item, Item> SCORCHER_RUNE = ITEMS.register("scorcher_shard", ScorcherShard::new);
   public static final DeferredHolder<Item, Item> DRAGON_WARRIOR_RUNE = ITEMS.register("dragon_warrior_shard", DragonWarriorShard::new);
   public static final DeferredHolder<Item, Item> TRUTH_MIRROR = ITEMS.register("truth_mirror", TruthMirror::new);
   public static final DeferredHolder<Item, Item> TINY_TOTEM = ITEMS.register("tiny_totem", TinyTotem::new);
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
   public static final DeferredHolder<Item, BlockItem> FAMILIAR_STORAGE = ITEMS.register(
      "familiar_storage", () -> new BlockItem(BlockRegistry.FAMILIAR_STORAGE.get(), new Properties())
   );
   public static final DeferredHolder<Item, BlockItem> SHRINKING_STATION = ITEMS.register(
      "shrinking_station", () -> new BlockItem(BlockRegistry.SHRINKING_STATION.get(), new Properties())
   );
   public static final DeferredHolder<Item, BlockItem> RITUAL_AUTOMATOR = ITEMS.register(
      "ritual_automator", () -> new BlockItem(BlockRegistry.RITUAL_AUTOMATOR.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> IRON_PLATE = ITEMS.register(
      "armor_plate_tier_1",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.ARMOR, 1)
            )
      )
   );
   public static final DeferredHolder<Item, Item> DIAMOND_PLATE = ITEMS.register(
      "armor_plate_tier_2",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.ARMOR, 2)
            )
      )
   );
   public static final DeferredHolder<Item, Item> NETHERITE_PLATE = ITEMS.register(
      "armor_plate_tier_3",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.ARMOR, 3)
            )
      )
   );
   public static final DeferredHolder<Item, Item> LIFE_FRUIT = ITEMS.register(
      "life_fruit_tier_1",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.HEALTH, 1)
            )
      )
   );
   public static final DeferredHolder<Item, Item> GOLDEN_LIFE_FRUIT = ITEMS.register(
      "life_fruit_tier_2",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.HEALTH, 2)
            )
      )
   );
   public static final DeferredHolder<Item, Item> DIAMOND_LIFE_FRUIT = ITEMS.register(
      "life_fruit_tier_3",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.HEALTH, 3)
            )
      )
   );
   public static final DeferredHolder<Item, Item> MAGIC_POWER_TIER_1 = ITEMS.register(
      "magic_power_tier_1",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.SPELL_POWER, 1)
            )
      )
   );
   public static final DeferredHolder<Item, Item> MAGIC_POWER_TIER_2 = ITEMS.register(
      "magic_power_tier_2",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.SPELL_POWER, 2)
            )
      )
   );
   public static final DeferredHolder<Item, Item> MAGIC_POWER_TIER_3 = ITEMS.register(
      "magic_power_tier_3",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.SPELL_POWER, 3)
            )
      )
   );
   public static final DeferredHolder<Item, Item> MAGIC_RESIST_TIER_1 = ITEMS.register(
      "magic_resist_tier_1",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.SPELL_RESIST, 1)
            )
      )
   );
   public static final DeferredHolder<Item, Item> MAGIC_RESIST_TIER_2 = ITEMS.register(
      "magic_resist_tier_2",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.SPELL_RESIST, 2)
            )
      )
   );
   public static final DeferredHolder<Item, Item> MAGIC_RESIST_TIER_3 = ITEMS.register(
      "magic_resist_tier_3",
      () -> new Item(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC)
            .component(
               (DataComponentType)net.alshanex.familiarslib.registry.ComponentRegistry.FAMILIAR_CONSUMABLE.get(),
               new FamiliarConsumableComponent(ConsumableType.SPELL_RESIST, 3)
            )
      )
   );
   public static final DeferredHolder<Item, Item> MONOCLE = ITEMS.register(
      "monocle", () -> new MonocleItem(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
   );

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }

   public static Collection<DeferredHolder<Item, ? extends Item>> getAFItems() {
      return ITEMS.getEntries();
   }
}
