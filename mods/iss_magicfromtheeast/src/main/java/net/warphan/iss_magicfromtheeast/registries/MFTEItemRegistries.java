package net.warphan.iss_magicfromtheeast.registries;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.AnimalArmorItem.BodyType;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.warphan.iss_magicfromtheeast.compat.MFTECurios;
import net.warphan.iss_magicfromtheeast.item.RitualOrihonSpellbookItem;
import net.warphan.iss_magicfromtheeast.item.armor.BootsOfMistArmorItem;
import net.warphan.iss_magicfromtheeast.item.armor.ElementalCommanderArmorItem;
import net.warphan.iss_magicfromtheeast.item.armor.JadePagodaArmorItem;
import net.warphan.iss_magicfromtheeast.item.armor.JiangshiHatItem;
import net.warphan.iss_magicfromtheeast.item.armor.OnmyojiArmorItem;
import net.warphan.iss_magicfromtheeast.item.armor.TaoistArmorItem;
import net.warphan.iss_magicfromtheeast.item.consumables.RiceWineBottleItem;
import net.warphan.iss_magicfromtheeast.item.curios.JadePendant;
import net.warphan.iss_magicfromtheeast.item.curios.RustedCoinsSword;
import net.warphan.iss_magicfromtheeast.item.curios.SoulwardRing;
import net.warphan.iss_magicfromtheeast.item.weapons.MFTEExtendedWeaponTier;
import net.warphan.iss_magicfromtheeast.item.weapons.MFTEStaffTier;
import net.warphan.iss_magicfromtheeast.item.weapons.MasamuneItem;
import net.warphan.iss_magicfromtheeast.item.weapons.MuramasaItem;
import net.warphan.iss_magicfromtheeast.item.weapons.RepeatingCrossbow;
import net.warphan.iss_magicfromtheeast.item.weapons.SoulBreakerItem;
import net.warphan.iss_magicfromtheeast.item.weapons.SoulPiercer;
import net.warphan.iss_magicfromtheeast.item.weapons.SpiritCrusherItem;
import net.warphan.iss_magicfromtheeast.setup.MFTERarity;
import net.warphan.iss_magicfromtheeast.util.MFTETags;

public class MFTEItemRegistries {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "iss_magicfromtheeast");
   public static final DeferredHolder<Item, Item> TAIJI_SWORD = ITEMS.register(
      "taiji_sword", () -> new StaffItem(ItemPropertiesHelper.equipment(1).attributes(ExtendedSwordItem.createAttributes(MFTEStaffTier.TAIJI_SWORD)))
   );
   public static final DeferredHolder<Item, Item> RITUAL_ORIHON = ITEMS.register("ritual_orihon", RitualOrihonSpellbookItem::new);
   public static final DeferredHolder<Item, Item> JADE = ITEMS.register("jade", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> BOTTLE_OF_SOULS = ITEMS.register(
      "bottle_of_souls", () -> new Item(ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredHolder<Item, Item> ARCANE_RELICS = ITEMS.register(
      "arcane_relics", () -> new Item(ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredHolder<Item, Item> RED_STRING = ITEMS.register("red_string", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> COPPER_COINS = ITEMS.register("copper_coins", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> YIN_YANG_CORE = ITEMS.register(
      "yin_yang_core", () -> new Item(ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredHolder<Item, Item> RED_SHAFT = ITEMS.register("red_shaft", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> REFINED_JADE_INGOT = ITEMS.register(
      "refined_jade_ingot", () -> new Item(ItemPropertiesHelper.material().rarity((Rarity)MFTERarity.JADELIGHT_RARITY_PROXY.getValue()))
   );
   public static final DeferredHolder<Item, Item> CRYSTALLIZED_SOUL = ITEMS.register(
      "crystallized_soul", () -> new Item(ItemPropertiesHelper.material().rarity(Rarity.RARE).fireResistant())
   );
   public static final DeferredHolder<Item, Item> RICE_WINE_BOTTLE = ITEMS.register(
      "rice_wine_bottle", () -> new RiceWineBottleItem(ItemPropertiesHelper.material(4))
   );
   public static final DeferredHolder<Item, Item> JADE_GUANDAO = ITEMS.register(
      "jade_guandao",
      () -> new MagicSwordItem(
         MFTEExtendedWeaponTier.JADE_GUANDAO,
         ItemPropertiesHelper.equipment()
            .rarity((Rarity)MFTERarity.JADELIGHT_RARITY_PROXY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(MFTEExtendedWeaponTier.JADE_GUANDAO)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(MFTESpellRegistries.NEPHRITE_SLASH_SPELL, 5)})
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_BREAKER = ITEMS.register(
      "soul_breaker",
      () -> new SoulBreakerItem(
         MFTEExtendedWeaponTier.SOUL_BREAKER,
         ItemPropertiesHelper.equipment()
            .rarity(Rarity.RARE)
            .fireResistant()
            .attributes(ExtendedSwordItem.createAttributes(MFTEExtendedWeaponTier.SOUL_BREAKER))
      )
   );
   public static final DeferredHolder<Item, Item> SPIRIT_CRUSHER = ITEMS.register(
      "spirit_crusher",
      () -> new SpiritCrusherItem(
         MFTEExtendedWeaponTier.SPIRIT_CRUSHER,
         ItemPropertiesHelper.equipment()
            .rarity(Rarity.RARE)
            .fireResistant()
            .attributes(ExtendedSwordItem.createAttributes(MFTEExtendedWeaponTier.SPIRIT_CRUSHER))
      )
   );
   public static final DeferredHolder<Item, Item> MURAMASA = ITEMS.register(
      "muramasa",
      () -> new MuramasaItem(
         MFTEExtendedWeaponTier.MURAMASA,
         ItemPropertiesHelper.equipment()
            .rarity((Rarity)MFTERarity.BLOODFUL_RARITY_PROXY.getValue())
            .fireResistant()
            .attributes(ExtendedSwordItem.createAttributes(MFTEExtendedWeaponTier.MURAMASA)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.BLOOD_SLASH_SPELL, 5)})
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_KATANA = ITEMS.register(
      "soul_katana",
      () -> new MasamuneItem(
         MFTEExtendedWeaponTier.SOUL_KATANA,
         ItemPropertiesHelper.equipment()
            .rarity(Rarity.EPIC)
            .fireResistant()
            .attributes(ExtendedSwordItem.createAttributes(MFTEExtendedWeaponTier.SOUL_KATANA)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(MFTESpellRegistries.CALAMITY_CUT_SPELL, 5)})
      )
   );
   public static final DeferredHolder<Item, Item> SOULPIERCER = ITEMS.register(
      "soulpiercer", () -> new SoulPiercer(ItemPropertiesHelper.hidden().durability(635).rarity(Rarity.RARE).fireResistant())
   );
   public static final DeferredHolder<Item, Item> REPEATING_CROSSBOW = ITEMS.register(
      "repeating_crossbow", () -> new RepeatingCrossbow(ItemPropertiesHelper.hidden().durability(465))
   );
   public static final Supplier<CurioBaseItem> BAGUA_MIRROR = ITEMS.register(
      "bagua_mirror",
      () -> new CurioBaseItem(ItemPropertiesHelper.equipment(1))
         .withAttributes(
            MFTECurios.BELT_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(MFTEAttributeRegistries.SYMMETRY_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.03, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> COINS_SWORD = ITEMS.register(
      "coins_sword",
      () -> new CurioBaseItem(ItemPropertiesHelper.equipment(1))
         .withAttributes(
            MFTECurios.BELT_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.BLOOD_MAGIC_RESIST, 0.15, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.ENDER_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.ELDRITCH_MAGIC_RESIST, 0.05, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final Supplier<CurioBaseItem> SOULWARD_RING = ITEMS.register("soulward_ring", SoulwardRing::new);
   public static final Supplier<CurioBaseItem> JADE_PENDANT = ITEMS.register("jade_pendant", JadePendant::new);
   public static final Supplier<CurioBaseItem> RUSTED_COINS_SWORD = ITEMS.register(
      "rusted_coins_sword",
      () -> new RustedCoinsSword(ItemPropertiesHelper.equipment(1))
         .withAttributes(
            MFTECurios.BELT_SLOT,
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.SUMMON_DAMAGE, 0.05, Operation.ADD_MULTIPLIED_BASE)
            }
         )
   );
   public static final DeferredHolder<Item, Item> SYMMETRY_RUNE = ITEMS.register("symmetry_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> SPIRIT_RUNE = ITEMS.register("spirit_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> DUNE_RUNE = ITEMS.register("dune_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> SYMMETRY_UPGRADE_ORB = ITEMS.register(
      "symmetry_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material()
            .rarity(Rarity.UNCOMMON)
            .component(ComponentRegistry.UPGRADE_ORB_TYPE, MFTEUpgradeOrbTypeRegistries.SYMMETRY_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> SPIRIT_UPGRADE_ORB = ITEMS.register(
      "spirit_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, MFTEUpgradeOrbTypeRegistries.SPIRIT_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> TAOIST_HAT = ITEMS.register(
      "taoist_helmet", () -> new TaoistArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> TAOIST_ROBES = ITEMS.register(
      "taoist_chestplate", () -> new TaoistArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> TAOIST_LEGGINGS = ITEMS.register(
      "taoist_leggings", () -> new TaoistArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> TAOIST_BOOTS = ITEMS.register(
      "taoist_boots", () -> new TaoistArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ELEMENTAL_COMMANDER_CHESTPLATE = ITEMS.register(
      "elemental_commander_chestplate",
      () -> new ElementalCommanderArmorItem(
         Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.CHESTPLATE.getDurability(37))
      )
   );
   public static final DeferredHolder<Item, Item> ONMYOJI_HAT = ITEMS.register(
      "onmyoji_helmet", () -> new OnmyojiArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ONMYOJI_ROBES = ITEMS.register(
      "onmyoji_chestplate", () -> new OnmyojiArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ONMYOJI_LEGGINGS = ITEMS.register(
      "onmyoji_leggings", () -> new OnmyojiArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ONMYOJI_GETA = ITEMS.register(
      "onmyoji_boots", () -> new OnmyojiArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> BOOTS_OF_MIST = ITEMS.register(
      "mist_boots",
      () -> new BootsOfMistArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.BOOTS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> JIANGSHI_HAT = ITEMS.register(
      "jiangshi_hat",
      () -> new JiangshiHatItem(Type.HELMET, ItemPropertiesHelper.equipment(1).rarity(Rarity.UNCOMMON).durability(Type.HELMET.getDurability(24)))
   );
   public static final DeferredHolder<Item, Item> JADE_PAGODA_HELMET = ITEMS.register(
      "jade_pagoda_helmet",
      () -> new JadePagodaArmorItem(
         Type.HELMET, ItemPropertiesHelper.equipment(1).rarity((Rarity)MFTERarity.JADELIGHT_RARITY_PROXY.getValue()).durability(Type.HELMET.getDurability(45))
      )
   );
   public static final DeferredHolder<Item, Item> JADE_PAGODA_CHESTPLATE = ITEMS.register(
      "jade_pagoda_chestplate",
      () -> new JadePagodaArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)MFTERarity.JADELIGHT_RARITY_PROXY.getValue()).durability(Type.CHESTPLATE.getDurability(45))
      )
   );
   public static final DeferredHolder<Item, Item> JADE_PAGODA_LEGGINGS = ITEMS.register(
      "jade_pagoda_leggings",
      () -> new JadePagodaArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)MFTERarity.JADELIGHT_RARITY_PROXY.getValue()).durability(Type.LEGGINGS.getDurability(45))
      )
   );
   public static final DeferredHolder<Item, Item> JADE_PAGODA_BOOTS = ITEMS.register(
      "jade_pagoda_boots",
      () -> new JadePagodaArmorItem(
         Type.BOOTS, ItemPropertiesHelper.equipment(1).rarity((Rarity)MFTERarity.JADELIGHT_RARITY_PROXY.getValue()).durability(Type.BOOTS.getDurability(45))
      )
   );
   public static final DeferredHolder<Item, Item> JADE_PAGODA_HORSE_ARMOR = ITEMS.register(
      "jade_pagoda_horse_armor",
      () -> new AnimalArmorItem(
         MFTEArmorMaterialRegistries.JADE,
         BodyType.EQUESTRIAN,
         false,
         new Properties().stacksTo(1).rarity((Rarity)MFTERarity.JADELIGHT_RARITY_PROXY.getValue())
      )
   );
   public static final DeferredHolder<Item, Item> JADE_ORE_ITEM = ITEMS.register(
      "jade_ore", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_ORE.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADE_ORE_DEEPSLATE_ITEM = ITEMS.register(
      "deepslate_jade_ore", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_ORE_DEEPSLATE.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADE_BLOCK_ITEM = ITEMS.register(
      "jade_block", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_BLOCK.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADE_SLAB_ITEM = ITEMS.register(
      "jade_slab", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_SLAB.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADE_STAIR_ITEM = ITEMS.register(
      "jade_stair", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_STAIR.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADE_WALL_ITEM = ITEMS.register(
      "jade_wall", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_WALL.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> CHISELED_JADE = ITEMS.register(
      "chiseled_jade", () -> new BlockItem((Block)MFTEBlockRegistries.CHISELED_JADE.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADE_LION_HEAD_BLOCK_ITEMS = ITEMS.register(
      "jade_lion_head_block", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_LION_HEAD_BLOCK.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADESTONE_BRICKS_ITEM = ITEMS.register(
      "jadestone_bricks", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_BRICK_BLOCK.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADESTONE_BRICKS_SLAB_ITEM = ITEMS.register(
      "jadestone_bricks_slab", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_BRICK_SLAB.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADESTONE_BRICKS_STAIR_ITEM = ITEMS.register(
      "jadestone_bricks_stair", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_BRICK_STAIR.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADESTONE_BRICKS_WALL_ITEM = ITEMS.register(
      "jadestone_bricks_wall", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> CHISELED_JADESTONE_BRICKS_ITEMS = ITEMS.register(
      "chiseled_jadestone_bricks", () -> new BlockItem((Block)MFTEBlockRegistries.CHISELED_JADE_BRICK.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> JADESTONE_BRICKS_PILLAR = ITEMS.register(
      "jadestone_bricks_pillar", () -> new BlockItem((Block)MFTEBlockRegistries.JADE_BRICK_PILLAR.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> REFINED_JADE_BLOCK_ITEM = ITEMS.register(
      "refined_jade_block", () -> new BlockItem((Block)MFTEBlockRegistries.REFINED_JADE_BLOCK.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> VASE_OF_RICE_WINE = ITEMS.register(
      "vase_rice_wine", () -> new BlockItem((Block)MFTEBlockRegistries.RICE_WINE_VASE.get(), new Properties().stacksTo(1))
   );
   public static final Supplier<BannerPatternItem> BALANCE_PATTERN = ITEMS.register(
      "balance_pattern", () -> new BannerPatternItem(MFTETags.BALANCE_PATTERN_ITEM, new Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
   );
   public static final Supplier<DeferredSpawnEggItem> TAOIST_SPAWN_EGG = ITEMS.register(
      "taoist_spawn_egg", () -> new DeferredSpawnEggItem(MFTEEntityRegistries.TAOIST, 14679808, 2236449, ItemPropertiesHelper.material().stacksTo(64))
   );
   public static final Supplier<DeferredSpawnEggItem> ONMYOJI_SPAWN_EGG = ITEMS.register(
      "onmyoji_spawn_egg", () -> new DeferredSpawnEggItem(MFTEEntityRegistries.ONMYOJI, 16514041, 131071, ItemPropertiesHelper.material().stacksTo(64))
   );
   public static final Supplier<DeferredSpawnEggItem> JIANGSHI_SPAWN_EGG = ITEMS.register(
      "jiangshi_spawn_egg", () -> new DeferredSpawnEggItem(MFTEEntityRegistries.JIANGSHI, 2629213, 27214, ItemPropertiesHelper.material().stacksTo(64))
   );

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
