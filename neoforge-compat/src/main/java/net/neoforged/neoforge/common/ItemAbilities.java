package net.neoforged.neoforge.common;

/**
 * Vanilla item ability constants (subset used by this mod).
 */
public class ItemAbilities {
    public static final ItemAbility AXE_DIG = ItemAbility.get("axe_dig");
    public static final ItemAbility AXE_STRIP = ItemAbility.get("axe_strip");
    public static final ItemAbility AXE_SCRAPE = ItemAbility.get("axe_scrape");
    public static final ItemAbility AXE_WAX_OFF = ItemAbility.get("axe_wax_off");
    public static final ItemAbility SHOVEL_DIG = ItemAbility.get("shovel_dig");
    public static final ItemAbility SHOVEL_FLATTEN = ItemAbility.get("shovel_flatten");
    public static final ItemAbility PICKAXE_DIG = ItemAbility.get("pickaxe_dig");
    public static final ItemAbility HOE_DIG = ItemAbility.get("hoe_dig");
    public static final ItemAbility HOE_TILL = ItemAbility.get("hoe_till");
    public static final ItemAbility SWORD_DIG = ItemAbility.get("sword_dig");
    public static final ItemAbility SWORD_SWEEP = ItemAbility.get("sword_sweep");
    public static final ItemAbility SHEARS_DIG = ItemAbility.get("shears_dig");
    public static final ItemAbility SHEARS_HARVEST = ItemAbility.get("shears_harvest");
    public static final ItemAbility SHEARS_REMOVE_ARMOR = ItemAbility.get("shears_remove_armor");
    public static final ItemAbility SHEARS_CARVE = ItemAbility.get("shears_carve");
    public static final ItemAbility SHEARS_DISARM = ItemAbility.get("shears_disarm");
    public static final ItemAbility FLINT_AND_STEEL_FIRE = ItemAbility.get("flint_and_steel_fire");
    public static final ItemAbility SHOVEL_DOUSE = ItemAbility.get("shovel_douse");
    public static final ItemAbility FIRESTARTER_LIGHT = ItemAbility.get("firestarter_light");
    public static final ItemAbility FIRESTARTER_DIM = ItemAbility.get("firestarter_dim");
    public static final ItemAbility AXE_WAX_ON = ItemAbility.get("axe_wax_on");
    public static final ItemAbility BUCKET_EMPTY = ItemAbility.get("bucket_empty");
    public static final ItemAbility BUCKET_FILL = ItemAbility.get("bucket_fill");
    public static final ItemAbility SWORD_BLOCK = ItemAbility.get("sword_block");
    public static final ItemAbility SHIELD_BLOCK = ItemAbility.get("shield_block");
    public static final ItemAbility ARMOR_EQUIP_ELYTRA = ItemAbility.get("armor_equip_elytra");
    public static final ItemAbility TURTLE_HELMET_SHIFT = ItemAbility.get("turtle_helmet_shift");
    public static final ItemAbility TRIDENT_TIDE = ItemAbility.get("trident_tide");
    public static final ItemAbility BUNDLE_EMPTY = ItemAbility.get("bundle_empty");
    public static final ItemAbility BUNDLE_INSERT = ItemAbility.get("bundle_insert");
    public static final ItemAbility ENDER_PEARL_THROW = ItemAbility.get("ender_pearl_throw");
    public static final ItemAbility FIREWORK_ROCKET_ELYTRA_BOOST = ItemAbility.get("firework_rocket_elytra_boost");
    public static final ItemAbility TRIDENT_THROW = ItemAbility.get("trident_throw");
    public static final ItemAbility TRIDENT_CHANNELING = ItemAbility.get("trident_channeling");
    public static final ItemAbility FISHING_ROD_CAST = ItemAbility.get("fishing_rod_cast");
    public static final ItemAbility BRUSH_BRUSH = ItemAbility.get("brush_brush");
    public static final ItemAbility MACE_SMASH = ItemAbility.get("mace_smash");
    public static final ItemAbility SHEARS_TRIM = ItemAbility.get("shears_trim");

    // Default actions supported by each tool type
    public static final java.util.Set<ItemAbility> DEFAULT_AXE_ACTIONS = of(AXE_DIG, AXE_STRIP, AXE_SCRAPE, AXE_WAX_OFF);
    public static final java.util.Set<ItemAbility> DEFAULT_HOE_ACTIONS = of(HOE_DIG, HOE_TILL);
    public static final java.util.Set<ItemAbility> DEFAULT_SHOVEL_ACTIONS = of(SHOVEL_DIG, SHOVEL_FLATTEN, SHOVEL_DOUSE);
    public static final java.util.Set<ItemAbility> DEFAULT_PICKAXE_ACTIONS = of(PICKAXE_DIG);
    public static final java.util.Set<ItemAbility> DEFAULT_SWORD_ACTIONS = of(SWORD_DIG, SWORD_SWEEP);
    public static final java.util.Set<ItemAbility> DEFAULT_SHEARS_ACTIONS = of(SHEARS_DIG, SHEARS_HARVEST,
            SHEARS_REMOVE_ARMOR, SHEARS_CARVE, SHEARS_DISARM, SHEARS_TRIM);
    public static final java.util.Set<ItemAbility> DEFAULT_SHIELD_ACTIONS = of(SHIELD_BLOCK);
    public static final java.util.Set<ItemAbility> DEFAULT_FISHING_ROD_ACTIONS = of(FISHING_ROD_CAST);
    public static final java.util.Set<ItemAbility> DEFAULT_TRIDENT_ACTIONS = of(TRIDENT_THROW);
    public static final java.util.Set<ItemAbility> DEFAULT_BRUSH_ACTIONS = of(BRUSH_BRUSH);
    public static final java.util.Set<ItemAbility> DEFAULT_FLINT_ACTIONS = of(FIRESTARTER_LIGHT);
    public static final java.util.Set<ItemAbility> DEFAULT_FIRECHARGE_ACTIONS = of(FIRESTARTER_LIGHT);

    private static java.util.Set<ItemAbility> of(ItemAbility... actions) {
        return java.util.stream.Stream.of(actions)
                .collect(java.util.stream.Collectors.toCollection(com.google.common.collect.Sets::newIdentityHashSet));
    }
}
