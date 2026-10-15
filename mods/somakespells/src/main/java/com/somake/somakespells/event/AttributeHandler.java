package com.somake.somakespells.event;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.compat.TunesCompat;
import com.somake.somakespells.config.ArmorBalanceConfig;
import com.somake.somakespells.config.SpellbookBalanceConfig;
import com.somake.somakespells.config.StaffBalanceConfig;
import com.somake.somakespells.config.WeaponBalanceConfig;
import com.somake.somakespells.config.magic.MagicAttributeOverrideValues;
import com.somake.somakespells.network.WeaponBalanceClientState;
import com.somake.somakespells.registries.ModAttributes;
import com.somake.somakespells.util.StaffHandCompat;
import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class AttributeHandler {
   private static final String TUNES_N_TOMES_MODID = "tunes_n_tomes";
   private static final String GEOMANCY_MODID = "gtbcs_geomancy_plus";
   private static final String IRONS_SPELLBOOKS_MODID = "irons_spellbooks";
   private static final ResourceLocation MELODY_SPELL_POWER_ID = TunesCompat.MELODY_SPELL_POWER_ID;
   private static final ResourceLocation GEO_SPELL_POWER_ID = ResourceLocation.fromNamespaceAndPath("gtbcs_geomancy_plus", "geo_spell_power");
   private static final ResourceLocation SPIRIT_SPELL_POWER_ID = MagicFromTheEastCompat.SPIRIT_SPELL_POWER_ID;
   private static final ResourceLocation SYMMETRY_SPELL_POWER_ID = MagicFromTheEastCompat.SYMMETRY_SPELL_POWER_ID;
   private static final List<AttributeHandler.ArmorSetBinding> ARMOR_SET_BINDINGS = List.of(
      new AttributeHandler.ArmorSetBinding("cataclysm:ignitium", "cataclysm", "ignitium_", false, false),
      new AttributeHandler.ArmorSetBinding("cataclysm:cursium", "cataclysm", "cursium_", false, false),
      new AttributeHandler.ArmorSetBinding("somakespells:ceranium", "somakespells", "ceranium_", false, false),
      new AttributeHandler.ArmorSetBinding("somakespells:abyssium", "somakespells", "abyssium_", false, false),
      new AttributeHandler.ArmorSetBinding("somakespells:dark_metal_battlemage", "somakespells", "dark_metal_battlemage_", false, false),
      new AttributeHandler.ArmorSetBinding("born_in_chaos_v1:dark_metal_armor", "born_in_chaos_v1", "dark_metal_armor_", false, false),
      new AttributeHandler.ArmorSetBinding("mowziesmobs:geomancer", "mowziesmobs", "geomancer_", false, true),
      new AttributeHandler.ArmorSetBinding("irons_spellbooks:cultist", "irons_spellbooks", "cultist_", false, false),
      new AttributeHandler.ArmorSetBinding("irons_spellbooks:cryomancer", "irons_spellbooks", "cryomancer_", false, false),
      new AttributeHandler.ArmorSetBinding("irons_spellbooks:electromancer", "irons_spellbooks", "electromancer_", false, false),
      new AttributeHandler.ArmorSetBinding("irons_spellbooks:pyromancer", "irons_spellbooks", "pyromancer_", false, false),
      new AttributeHandler.ArmorSetBinding("irons_spellbooks:priest", "irons_spellbooks", "priest_", false, false),
      new AttributeHandler.ArmorSetBinding("irons_spellbooks:plagued", "irons_spellbooks", "plagued_", false, false),
      new AttributeHandler.ArmorSetBinding("iss_magicfromtheeast:onmyoji", "iss_magicfromtheeast", "onmyoji_", false, false),
      new AttributeHandler.ArmorSetBinding("iss_magicfromtheeast:taoist", "iss_magicfromtheeast", "taoist_", false, false),
      new AttributeHandler.ArmorSetBinding("irons_spellbooks:archevoker", "irons_spellbooks", "archevoker_", false, false),
      new AttributeHandler.ArmorSetBinding("irons_spellbooks:shadowwalker", "irons_spellbooks", "shadowwalker_", false, false),
      new AttributeHandler.ArmorSetBinding("somakespells:aquamancer", "somakespells", "aquamancer_", false, false),
      new AttributeHandler.ArmorSetBinding("tunes_n_tomes:bard", "tunes_n_tomes", "bard_", true, false)
   );

   @SubscribeEvent
   public static void modifyItemAttributes(ItemAttributeModifierEvent event) {
      Item item = event.getItemStack().getItem();
      ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
      StaffHandCompat.remapStaffModifiers(event);
      if (item instanceof ArmorItem armorItem) {
         ArmorBalanceConfig.DynamicArmorPieceValues customArmorValues = resolveCustomArmorPieceOverride(itemId);
         if (customArmorValues != null && customArmorValues.enabled()) {
            applyCustomArmorPieceOverride(event, itemId, armorItem, customArmorValues);
         }

         for (AttributeHandler.ArmorSetBinding binding : ARMOR_SET_BINDINGS) {
            if (binding.matches(itemId)) {
               if ((!binding.requiresTunes || ModList.get().isLoaded("tunes_n_tomes"))
                  && (!binding.requiresGeomancy || ModList.get().isLoaded("gtbcs_geomancy_plus"))) {
                  ArmorBalanceConfig.ArmorSetOverrideValues values = resolveArmorOverride(binding.armorKey);
                  if (values != null && values.enabled()) {
                     applyArmorOverride(event, itemId, armorItem, values);
                  }
               }
               break;
            }
         }
      }

      WeaponBalanceConfig.WeaponOverrideValues override = resolveWeaponOverride(itemId);
      if (override != null && override.enabled()) {
         applyWeaponOverride(event, override);
      } else {
         applyCompatWeaponDefaults(event, itemId);
      }

      if (StaffHandCompat.isStaff(event.getItemStack())) {
         MagicAttributeOverrideValues staffOverride = resolveStaffOverride(itemId);
         if (staffOverride != null && staffOverride.enabled()) {
            applyStaffOverride(event, staffOverride);
         }
      }
   }

   private static WeaponBalanceConfig.WeaponOverrideValues resolveWeaponOverride(ResourceLocation itemId) {
      WeaponBalanceConfig.WeaponOverrideValues synced = WeaponBalanceClientState.getWeaponForItem(itemId);
      return synced != null ? synced : WeaponBalanceConfig.getLocalWeaponOverride(itemId);
   }

   private static ArmorBalanceConfig.ArmorSetOverrideValues resolveArmorOverride(String armorKey) {
      ArmorBalanceConfig.ArmorSetOverrideValues synced = WeaponBalanceClientState.getArmorForKey(armorKey);
      return synced != null ? synced : ArmorBalanceConfig.getLocalArmorOverride(armorKey);
   }

   private static ArmorBalanceConfig.DynamicArmorPieceValues resolveCustomArmorPieceOverride(ResourceLocation itemId) {
      ArmorBalanceConfig.DynamicArmorPieceValues synced = WeaponBalanceClientState.getCustomArmorForItem(itemId);
      return synced != null ? synced : ArmorBalanceConfig.getLocalCustomArmorPieceOverride(itemId);
   }

   private static MagicAttributeOverrideValues resolveSpellbookOverride(ResourceLocation itemId) {
      MagicAttributeOverrideValues synced = WeaponBalanceClientState.getSpellbookForItem(itemId);
      return synced != null ? synced : SpellbookBalanceConfig.getLocalSpellbookOverride(itemId);
   }

   private static MagicAttributeOverrideValues resolveStaffOverride(ResourceLocation itemId) {
      MagicAttributeOverrideValues synced = WeaponBalanceClientState.getStaffForItem(itemId);
      return synced != null ? synced : StaffBalanceConfig.getLocalStaffOverride(itemId);
   }

   private static void applyArmorOverride(
      ItemAttributeModifierEvent event, ResourceLocation itemId, ArmorItem armorItem, ArmorBalanceConfig.ArmorSetOverrideValues values
   ) {
      EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(armorItem.getType().getSlot());
      String slotName = armorItem.getType().getName();
      String itemPrefix = itemId.getNamespace() + "_" + itemId.getPath();
      if (values.overrideArmor()) {
         event.removeAllModifiersFor(Attributes.ARMOR);

         double armorValue = switch (armorItem.getType()) {
            case HELMET -> values.armorHelmet();
            case CHESTPLATE -> values.armorChestplate();
            case LEGGINGS -> values.armorLeggings();
            case BOOTS -> values.armorBoots();
            default -> 0.0;
         };
         if (Math.abs(armorValue) > 1.0E-6) {
            event.addModifier(
               Attributes.ARMOR,
               new AttributeModifier(
                  ResourceLocation.fromNamespaceAndPath("somakespells", "armor_balance_" + itemPrefix + "_armor_" + slotName), armorValue, Operation.ADD_VALUE
               ),
               slotGroup
            );
         }
      }

      if (values.overrideArmorToughness()) {
         event.removeAllModifiersFor(Attributes.ARMOR_TOUGHNESS);
         if (Math.abs(values.armorToughness()) > 1.0E-6) {
            event.addModifier(
               Attributes.ARMOR_TOUGHNESS,
               new AttributeModifier(
                  ResourceLocation.fromNamespaceAndPath("somakespells", "armor_balance_" + itemPrefix + "_armor_toughness_" + slotName),
                  values.armorToughness(),
                  Operation.ADD_VALUE
               ),
               slotGroup
            );
         }
      }

      applyOptionalAttribute(
         event,
         values.overrideMaxMana(),
         AttributeRegistry.MAX_MANA,
         values.maxMana(),
         "armor_balance_" + itemPrefix + "_max_mana_" + slotName,
         Operation.ADD_VALUE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideSpellPower(),
         AttributeRegistry.SPELL_POWER,
         values.spellPower(),
         "armor_balance_" + itemPrefix + "_spell_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideFireSpellPower(),
         AttributeRegistry.FIRE_SPELL_POWER,
         values.fireSpellPower(),
         "armor_balance_" + itemPrefix + "_fire_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideIceSpellPower(),
         AttributeRegistry.ICE_SPELL_POWER,
         values.iceSpellPower(),
         "armor_balance_" + itemPrefix + "_ice_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideLightningSpellPower(),
         AttributeRegistry.LIGHTNING_SPELL_POWER,
         values.lightningSpellPower(),
         "armor_balance_" + itemPrefix + "_lightning_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideEnderSpellPower(),
         AttributeRegistry.ENDER_SPELL_POWER,
         values.enderSpellPower(),
         "armor_balance_" + itemPrefix + "_ender_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideBloodSpellPower(),
         AttributeRegistry.BLOOD_SPELL_POWER,
         values.bloodSpellPower(),
         "armor_balance_" + itemPrefix + "_blood_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideNatureSpellPower(),
         AttributeRegistry.NATURE_SPELL_POWER,
         values.natureSpellPower(),
         "armor_balance_" + itemPrefix + "_nature_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalRegistryAttribute(
         event,
         values.overrideSpiritSpellPower(),
         SPIRIT_SPELL_POWER_ID,
         values.spiritSpellPower(),
         "armor_balance_" + itemPrefix + "_spirit_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalRegistryAttribute(
         event,
         values.overrideSymmetrySpellPower(),
         SYMMETRY_SPELL_POWER_ID,
         values.symmetrySpellPower(),
         "armor_balance_" + itemPrefix + "_symmetry_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideAquaSpellPower(),
         ModAttributes.AQUA_SPELL_POWER,
         values.aquaSpellPower(),
         "armor_balance_" + itemPrefix + "_aqua_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideHolySpellPower(),
         AttributeRegistry.HOLY_SPELL_POWER,
         values.holySpellPower(),
         "armor_balance_" + itemPrefix + "_holy_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideEvocationSpellPower(),
         AttributeRegistry.EVOCATION_SPELL_POWER,
         values.evocationSpellPower(),
         "armor_balance_" + itemPrefix + "_evocation_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      if (values.overrideSoundSpellPower()) {
         Optional<Reference<Attribute>> soundAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(MELODY_SPELL_POWER_ID);
         soundAttribute.ifPresent(
            attribute -> applyOptionalAttribute(
               event,
               true,
               attribute,
               values.soundSpellPower(),
               "armor_balance_" + itemPrefix + "_sound_power_" + slotName,
               Operation.ADD_MULTIPLIED_BASE,
               slotGroup
            )
         );
      }

      if (values.overrideGeoSpellPower()) {
         Optional<Reference<Attribute>> geoAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(GEO_SPELL_POWER_ID);
         geoAttribute.ifPresent(
            attribute -> applyOptionalAttribute(
               event,
               true,
               attribute,
               values.geoSpellPower(),
               "armor_balance_" + itemPrefix + "_geo_power_" + slotName,
               Operation.ADD_MULTIPLIED_BASE,
               slotGroup
            )
         );
      }
   }

   private static void applyWeaponOverride(ItemAttributeModifierEvent event, WeaponBalanceConfig.WeaponOverrideValues values) {
      removeNonUpgradeModifiersFor(event, Attributes.ATTACK_DAMAGE);
      removeNonUpgradeModifiersFor(event, Attributes.ATTACK_SPEED);
      removeNonUpgradeModifiersFor(event, Attributes.ENTITY_INTERACTION_RANGE);
      event.addModifier(
         Attributes.ATTACK_DAMAGE,
         new AttributeModifier(ResourceLocation.withDefaultNamespace("base_attack_damage"), values.attackDamage(), Operation.ADD_VALUE),
         EquipmentSlotGroup.MAINHAND
      );
      event.addModifier(
         Attributes.ATTACK_SPEED,
         new AttributeModifier(ResourceLocation.withDefaultNamespace("base_attack_speed"), values.attackSpeed() - 4.0, Operation.ADD_VALUE),
         EquipmentSlotGroup.MAINHAND
      );
      double rangeDelta = values.entityInteractionRange() - 3.0;
      if (Math.abs(rangeDelta) > 1.0E-6) {
         event.addModifier(
            Attributes.ENTITY_INTERACTION_RANGE,
            new AttributeModifier(ResourceLocation.fromNamespaceAndPath("somakespells", "weapon_balance_range_mainhand"), rangeDelta, Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         );
      }

      applyOptionalSpellPower(
         event, values.overrideFireSpellPower(), AttributeRegistry.FIRE_SPELL_POWER, values.fireSpellPower(), "weapon_balance_fire_power_mainhand"
      );
      applyOptionalSpellPower(
         event,
         values.overrideInfernalFireSpellPower(),
         ModAttributes.INFERNAL_FIRE_SPELL_POWER,
         values.infernalFireSpellPower(),
         "weapon_balance_infernal_fire_power_mainhand"
      );
      applyOptionalSpellPower(
         event, values.overrideIceSpellPower(), AttributeRegistry.ICE_SPELL_POWER, values.iceSpellPower(), "weapon_balance_ice_power_mainhand"
      );
      applyOptionalSpellPower(
         event,
         values.overrideLightningSpellPower(),
         AttributeRegistry.LIGHTNING_SPELL_POWER,
         values.lightningSpellPower(),
         "weapon_balance_lightning_power_mainhand"
      );
      applyOptionalSpellPower(
         event, values.overrideEnderSpellPower(), AttributeRegistry.ENDER_SPELL_POWER, values.enderSpellPower(), "weapon_balance_ender_power_mainhand"
      );
      applyOptionalSpellPower(
         event, values.overrideBloodSpellPower(), AttributeRegistry.BLOOD_SPELL_POWER, values.bloodSpellPower(), "weapon_balance_blood_power_mainhand"
      );
      applyOptionalSpellPower(
         event, values.overrideNatureSpellPower(), AttributeRegistry.NATURE_SPELL_POWER, values.natureSpellPower(), "weapon_balance_nature_power_mainhand"
      );
      applyOptionalRegistrySpellPower(
         event, values.overrideSpiritSpellPower(), SPIRIT_SPELL_POWER_ID, values.spiritSpellPower(), "weapon_balance_spirit_power_mainhand"
      );
      applyOptionalRegistrySpellPower(
         event, values.overrideSymmetrySpellPower(), SYMMETRY_SPELL_POWER_ID, values.symmetrySpellPower(), "weapon_balance_symmetry_power_mainhand"
      );
      applyOptionalSpellPower(
         event, values.overrideAquaSpellPower(), ModAttributes.AQUA_SPELL_POWER, values.aquaSpellPower(), "weapon_balance_aqua_power_mainhand"
      );
      applyOptionalSpellPower(
         event, values.overrideHolySpellPower(), AttributeRegistry.HOLY_SPELL_POWER, values.holySpellPower(), "weapon_balance_holy_power_mainhand"
      );
      applyOptionalSpellPower(
         event,
         values.overrideEldritchSpellPower(),
         AttributeRegistry.ELDRITCH_SPELL_POWER,
         values.eldritchSpellPower(),
         "weapon_balance_eldritch_power_mainhand"
      );
      applyOptionalSpellPower(
         event,
         values.overrideEvocationSpellPower(),
         AttributeRegistry.EVOCATION_SPELL_POWER,
         values.evocationSpellPower(),
         "weapon_balance_evocation_power_mainhand"
      );
      if (values.overrideSoundSpellPower()) {
         Optional<Reference<Attribute>> soundAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(MELODY_SPELL_POWER_ID);
         soundAttribute.ifPresent(
            attribute -> applyOptionalAttribute(
               event,
               true,
               attribute,
               values.soundSpellPower(),
               "weapon_balance_sound_power_mainhand",
               Operation.ADD_MULTIPLIED_BASE,
               EquipmentSlotGroup.MAINHAND
            )
         );
      }

      if (values.overrideGeoSpellPower()) {
         Optional<Reference<Attribute>> geoAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(GEO_SPELL_POWER_ID);
         geoAttribute.ifPresent(
            attribute -> applyOptionalAttribute(
               event, true, attribute, values.geoSpellPower(), "weapon_balance_geo_power_mainhand", Operation.ADD_MULTIPLIED_BASE, EquipmentSlotGroup.MAINHAND
            )
         );
      }

      applyOptionalSpellPower(event, values.overrideSpellResist(), AttributeRegistry.SPELL_RESIST, values.spellResist(), "weapon_balance_spell_resist_mainhand");
   }

   private static void applyCompatWeaponDefaults(ItemAttributeModifierEvent event, ResourceLocation itemId) {
      if (BornInChaosCompat.isLoaded()) {
         if (BornInChaosCompat.SOULBANE_ID.equals(itemId)) {
            addFixedAttribute(
               event,
               ModAttributes.INFERNAL_FIRE_SPELL_POWER,
               ResourceLocation.fromNamespaceAndPath("somakespells", "compat_weapon_soulbane_infernal_fire_power_mainhand"),
               0.25,
               Operation.ADD_MULTIPLIED_BASE,
               EquipmentSlotGroup.MAINHAND
            );
         }
      }
   }

   private static void applyStaffOverride(ItemAttributeModifierEvent event, MagicAttributeOverrideValues values) {
      EquipmentSlotGroup combatSlotGroup = StaffHandCompat.getBaseCombatSlot();
      EquipmentSlotGroup magicSlotGroup = StaffHandCompat.getPreferredStaffSlot(event.getItemStack());
      if (values.overrideAttackDamage()) {
         removeNonUpgradeModifiersFor(event, Attributes.ATTACK_DAMAGE);
         event.addModifier(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(ResourceLocation.withDefaultNamespace("base_attack_damage"), values.attackDamage(), Operation.ADD_VALUE),
            combatSlotGroup
         );
      }

      if (values.overrideAttackSpeed()) {
         removeNonUpgradeModifiersFor(event, Attributes.ATTACK_SPEED);
         event.addModifier(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(ResourceLocation.withDefaultNamespace("base_attack_speed"), values.attackSpeed(), Operation.ADD_VALUE),
            combatSlotGroup
         );
      }

      if (values.overrideEntityInteractionRange()) {
         removeNonUpgradeModifiersFor(event, Attributes.ENTITY_INTERACTION_RANGE);
         if (Math.abs(values.entityInteractionRange()) > 1.0E-6) {
            event.addModifier(
               Attributes.ENTITY_INTERACTION_RANGE,
               new AttributeModifier(
                  ResourceLocation.fromNamespaceAndPath("somakespells", "staff_balance_range"), values.entityInteractionRange(), Operation.ADD_VALUE
               ),
               combatSlotGroup
            );
         }
      }

      applyMagicAttributeOverrides(event, values, magicSlotGroup, true);
   }

   private static void applyMagicAttributeOverrides(
      ItemAttributeModifierEvent event, MagicAttributeOverrideValues values, EquipmentSlotGroup slotGroup, boolean includeHeldOnly
   ) {
      String modifierPrefix = getMagicModifierPrefix(event.getItemStack(), values);
      applyOptionalAttribute(
         event, values.overrideMaxMana(), AttributeRegistry.MAX_MANA, values.maxMana(), modifierPrefix + "_max_mana", Operation.ADD_VALUE, slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideManaRegen(),
         AttributeRegistry.MANA_REGEN,
         values.manaRegen(),
         modifierPrefix + "_mana_regen",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideSpellPower(),
         AttributeRegistry.SPELL_POWER,
         values.spellPower(),
         modifierPrefix + "_spell_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideCastTimeReduction(),
         AttributeRegistry.CAST_TIME_REDUCTION,
         values.castTimeReduction(),
         modifierPrefix + "_cast_time",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideCooldownReduction(),
         AttributeRegistry.COOLDOWN_REDUCTION,
         values.cooldownReduction(),
         modifierPrefix + "_cooldown",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideSpellResist(),
         AttributeRegistry.SPELL_RESIST,
         values.spellResist(),
         modifierPrefix + "_spell_resist",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideSummonDamage(),
         AttributeRegistry.SUMMON_DAMAGE,
         values.summonDamage(),
         modifierPrefix + "_summon_damage",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideFireSpellPower(),
         AttributeRegistry.FIRE_SPELL_POWER,
         values.fireSpellPower(),
         modifierPrefix + "_fire_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideIceSpellPower(),
         AttributeRegistry.ICE_SPELL_POWER,
         values.iceSpellPower(),
         modifierPrefix + "_ice_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideLightningSpellPower(),
         AttributeRegistry.LIGHTNING_SPELL_POWER,
         values.lightningSpellPower(),
         modifierPrefix + "_lightning_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideEnderSpellPower(),
         AttributeRegistry.ENDER_SPELL_POWER,
         values.enderSpellPower(),
         modifierPrefix + "_ender_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideBloodSpellPower(),
         AttributeRegistry.BLOOD_SPELL_POWER,
         values.bloodSpellPower(),
         modifierPrefix + "_blood_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideNatureSpellPower(),
         AttributeRegistry.NATURE_SPELL_POWER,
         values.natureSpellPower(),
         modifierPrefix + "_nature_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideHolySpellPower(),
         AttributeRegistry.HOLY_SPELL_POWER,
         values.holySpellPower(),
         modifierPrefix + "_holy_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideEvocationSpellPower(),
         AttributeRegistry.EVOCATION_SPELL_POWER,
         values.evocationSpellPower(),
         modifierPrefix + "_evocation_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideEldritchSpellPower(),
         AttributeRegistry.ELDRITCH_SPELL_POWER,
         values.eldritchSpellPower(),
         modifierPrefix + "_eldritch_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideAquaSpellPower(),
         ModAttributes.AQUA_SPELL_POWER,
         values.aquaSpellPower(),
         modifierPrefix + "_aqua_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalRegistryAttribute(
         event,
         values.overrideSpiritSpellPower(),
         SPIRIT_SPELL_POWER_ID,
         values.spiritSpellPower(),
         modifierPrefix + "_spirit_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalRegistryAttribute(
         event,
         values.overrideSymmetrySpellPower(),
         SYMMETRY_SPELL_POWER_ID,
         values.symmetrySpellPower(),
         modifierPrefix + "_symmetry_power",
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      if (values.overrideSoundSpellPower()) {
         Optional<Reference<Attribute>> soundAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(MELODY_SPELL_POWER_ID);
         soundAttribute.ifPresent(
            attribute -> applyOptionalAttribute(
               event, true, attribute, values.soundSpellPower(), modifierPrefix + "_sound_power", Operation.ADD_MULTIPLIED_BASE, slotGroup
            )
         );
      }

      if (values.overrideGeoSpellPower()) {
         Optional<Reference<Attribute>> geoAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(GEO_SPELL_POWER_ID);
         geoAttribute.ifPresent(
            attribute -> applyOptionalAttribute(
               event, true, attribute, values.geoSpellPower(), modifierPrefix + "_geo_power", Operation.ADD_MULTIPLIED_BASE, slotGroup
            )
         );
      }

      applyBornInChaosCompatStaffBonuses(event, slotGroup);
   }

   private static void applyBornInChaosCompatStaffBonuses(ItemAttributeModifierEvent event, EquipmentSlotGroup slotGroup) {
      ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
      if (BornInChaosCompat.isLoaded() && BornInChaosCompat.isCompatStaff(itemId)) {
         ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath("somakespells", "compat_staff_" + itemId.getNamespace() + "_" + itemId.getPath());
         if (itemId.equals(BornInChaosCompat.PUMPKIN_STAFF_ID)) {
            addFixedAttribute(event, ModAttributes.INFERNAL_FIRE_SPELL_POWER, modifierId, 0.15, Operation.ADD_MULTIPLIED_BASE, slotGroup);
         }
      }
   }

   private static void applySpellbookOverride(CurioAttributeModifierEvent event, MagicAttributeOverrideValues values) {
      String modifierPrefix = getMagicModifierPrefix(event.getItemStack(), values);
      applyOptionalAttribute(event, values.overrideMaxMana(), AttributeRegistry.MAX_MANA, values.maxMana(), modifierPrefix + "_max_mana", Operation.ADD_VALUE);
      applyOptionalAttribute(
         event, values.overrideManaRegen(), AttributeRegistry.MANA_REGEN, values.manaRegen(), modifierPrefix + "_mana_regen", Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event, values.overrideSpellPower(), AttributeRegistry.SPELL_POWER, values.spellPower(), modifierPrefix + "_spell_power", Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideCastTimeReduction(),
         AttributeRegistry.CAST_TIME_REDUCTION,
         values.castTimeReduction(),
         modifierPrefix + "_cast_time",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideCooldownReduction(),
         AttributeRegistry.COOLDOWN_REDUCTION,
         values.cooldownReduction(),
         modifierPrefix + "_cooldown",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideSpellResist(),
         AttributeRegistry.SPELL_RESIST,
         values.spellResist(),
         modifierPrefix + "_spell_resist",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideSummonDamage(),
         AttributeRegistry.SUMMON_DAMAGE,
         values.summonDamage(),
         modifierPrefix + "_summon_damage",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideFireSpellPower(),
         AttributeRegistry.FIRE_SPELL_POWER,
         values.fireSpellPower(),
         modifierPrefix + "_fire_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideIceSpellPower(),
         AttributeRegistry.ICE_SPELL_POWER,
         values.iceSpellPower(),
         modifierPrefix + "_ice_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideLightningSpellPower(),
         AttributeRegistry.LIGHTNING_SPELL_POWER,
         values.lightningSpellPower(),
         modifierPrefix + "_lightning_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideEnderSpellPower(),
         AttributeRegistry.ENDER_SPELL_POWER,
         values.enderSpellPower(),
         modifierPrefix + "_ender_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideBloodSpellPower(),
         AttributeRegistry.BLOOD_SPELL_POWER,
         values.bloodSpellPower(),
         modifierPrefix + "_blood_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideNatureSpellPower(),
         AttributeRegistry.NATURE_SPELL_POWER,
         values.natureSpellPower(),
         modifierPrefix + "_nature_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideHolySpellPower(),
         AttributeRegistry.HOLY_SPELL_POWER,
         values.holySpellPower(),
         modifierPrefix + "_holy_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideEvocationSpellPower(),
         AttributeRegistry.EVOCATION_SPELL_POWER,
         values.evocationSpellPower(),
         modifierPrefix + "_evocation_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideEldritchSpellPower(),
         AttributeRegistry.ELDRITCH_SPELL_POWER,
         values.eldritchSpellPower(),
         modifierPrefix + "_eldritch_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalAttribute(
         event,
         values.overrideAquaSpellPower(),
         ModAttributes.AQUA_SPELL_POWER,
         values.aquaSpellPower(),
         modifierPrefix + "_aqua_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalRegistryAttribute(
         event,
         values.overrideSpiritSpellPower(),
         SPIRIT_SPELL_POWER_ID,
         values.spiritSpellPower(),
         modifierPrefix + "_spirit_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      applyOptionalRegistryAttribute(
         event,
         values.overrideSymmetrySpellPower(),
         SYMMETRY_SPELL_POWER_ID,
         values.symmetrySpellPower(),
         modifierPrefix + "_symmetry_power",
         Operation.ADD_MULTIPLIED_BASE
      );
      if (values.overrideSoundSpellPower()) {
         Optional<Reference<Attribute>> soundAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(MELODY_SPELL_POWER_ID);
         soundAttribute.ifPresent(
            attribute -> applyOptionalAttribute(
               event, true, attribute, values.soundSpellPower(), modifierPrefix + "_sound_power", Operation.ADD_MULTIPLIED_BASE
            )
         );
      }

      if (values.overrideGeoSpellPower()) {
         Optional<Reference<Attribute>> geoAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(GEO_SPELL_POWER_ID);
         geoAttribute.ifPresent(
            attribute -> applyOptionalAttribute(event, true, attribute, values.geoSpellPower(), modifierPrefix + "_geo_power", Operation.ADD_MULTIPLIED_BASE)
         );
      }
   }

   private static String getMagicModifierPrefix(ItemStack itemStack, MagicAttributeOverrideValues values) {
      ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
      String profile = values.profile().name().toLowerCase(Locale.ROOT);
      String itemPath = itemId.getPath().replace('/', '_');
      return "magic_balance_" + profile + "_" + itemId.getNamespace() + "_" + itemPath;
   }

   private static void applyCustomArmorPieceOverride(
      ItemAttributeModifierEvent event, ResourceLocation itemId, ArmorItem armorItem, ArmorBalanceConfig.DynamicArmorPieceValues values
   ) {
      EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(armorItem.getType().getSlot());
      String slotName = armorItem.getType().getName();
      String itemPrefix = itemId.getNamespace() + "_" + itemId.getPath();
      if (values.overrideArmor()) {
         event.removeAllModifiersFor(Attributes.ARMOR);
         if (Math.abs(values.armorValue()) > 1.0E-6) {
            event.addModifier(
               Attributes.ARMOR,
               new AttributeModifier(
                  ResourceLocation.fromNamespaceAndPath("somakespells", "armor_balance_custom_" + itemPrefix + "_armor_" + slotName),
                  values.armorValue(),
                  Operation.ADD_VALUE
               ),
               slotGroup
            );
         }
      }

      if (values.overrideArmorToughness()) {
         event.removeAllModifiersFor(Attributes.ARMOR_TOUGHNESS);
         if (Math.abs(values.armorToughness()) > 1.0E-6) {
            event.addModifier(
               Attributes.ARMOR_TOUGHNESS,
               new AttributeModifier(
                  ResourceLocation.fromNamespaceAndPath("somakespells", "armor_balance_custom_" + itemPrefix + "_armor_toughness_" + slotName),
                  values.armorToughness(),
                  Operation.ADD_VALUE
               ),
               slotGroup
            );
         }
      }

      applyOptionalAttribute(
         event,
         values.overrideMaxMana(),
         AttributeRegistry.MAX_MANA,
         values.maxMana(),
         "armor_balance_custom_" + itemPrefix + "_max_mana_" + slotName,
         Operation.ADD_VALUE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideSpellPower(),
         AttributeRegistry.SPELL_POWER,
         values.spellPower(),
         "armor_balance_custom_" + itemPrefix + "_spell_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideFireSpellPower(),
         AttributeRegistry.FIRE_SPELL_POWER,
         values.fireSpellPower(),
         "armor_balance_custom_" + itemPrefix + "_fire_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideIceSpellPower(),
         AttributeRegistry.ICE_SPELL_POWER,
         values.iceSpellPower(),
         "armor_balance_custom_" + itemPrefix + "_ice_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideLightningSpellPower(),
         AttributeRegistry.LIGHTNING_SPELL_POWER,
         values.lightningSpellPower(),
         "armor_balance_custom_" + itemPrefix + "_lightning_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideEnderSpellPower(),
         AttributeRegistry.ENDER_SPELL_POWER,
         values.enderSpellPower(),
         "armor_balance_custom_" + itemPrefix + "_ender_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideBloodSpellPower(),
         AttributeRegistry.BLOOD_SPELL_POWER,
         values.bloodSpellPower(),
         "armor_balance_custom_" + itemPrefix + "_blood_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideNatureSpellPower(),
         AttributeRegistry.NATURE_SPELL_POWER,
         values.natureSpellPower(),
         "armor_balance_custom_" + itemPrefix + "_nature_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalRegistryAttribute(
         event,
         values.overrideSpiritSpellPower(),
         SPIRIT_SPELL_POWER_ID,
         values.spiritSpellPower(),
         "armor_balance_custom_" + itemPrefix + "_spirit_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalRegistryAttribute(
         event,
         values.overrideSymmetrySpellPower(),
         SYMMETRY_SPELL_POWER_ID,
         values.symmetrySpellPower(),
         "armor_balance_custom_" + itemPrefix + "_symmetry_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideAquaSpellPower(),
         ModAttributes.AQUA_SPELL_POWER,
         values.aquaSpellPower(),
         "armor_balance_custom_" + itemPrefix + "_aqua_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideHolySpellPower(),
         AttributeRegistry.HOLY_SPELL_POWER,
         values.holySpellPower(),
         "armor_balance_custom_" + itemPrefix + "_holy_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      applyOptionalAttribute(
         event,
         values.overrideEvocationSpellPower(),
         AttributeRegistry.EVOCATION_SPELL_POWER,
         values.evocationSpellPower(),
         "armor_balance_custom_" + itemPrefix + "_evocation_power_" + slotName,
         Operation.ADD_MULTIPLIED_BASE,
         slotGroup
      );
      if (values.overrideSoundSpellPower()) {
         Optional<Reference<Attribute>> soundAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(MELODY_SPELL_POWER_ID);
         soundAttribute.ifPresent(
            attribute -> applyOptionalAttribute(
               event,
               true,
               attribute,
               values.soundSpellPower(),
               "armor_balance_custom_" + itemPrefix + "_sound_power_" + slotName,
               Operation.ADD_MULTIPLIED_BASE,
               slotGroup
            )
         );
      }

      if (values.overrideGeoSpellPower()) {
         Optional<Reference<Attribute>> geoAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(GEO_SPELL_POWER_ID);
         geoAttribute.ifPresent(
            attribute -> applyOptionalAttribute(
               event,
               true,
               attribute,
               values.geoSpellPower(),
               "armor_balance_custom_" + itemPrefix + "_geo_power_" + slotName,
               Operation.ADD_MULTIPLIED_BASE,
               slotGroup
            )
         );
      }
   }

   private static EquipmentSlotGroup detectPrimarySlotGroup(ItemAttributeModifierEvent event, EquipmentSlotGroup fallback) {
      for (net.minecraft.world.item.component.ItemAttributeModifiers.Entry entry : event.getModifiers()) {
         if (entry.slot() != EquipmentSlotGroup.ANY) {
            return entry.slot();
         }
      }

      return fallback;
   }

   private static void applyOptionalAttribute(
      ItemAttributeModifierEvent event,
      boolean shouldOverride,
      Holder<Attribute> attribute,
      double value,
      String modifierPath,
      Operation operation,
      EquipmentSlotGroup slotGroup
   ) {
      if (shouldOverride) {
         boolean hasUpgradeModifier = hasUpgradeModifier(event, attribute, operation);
         removeNonUpgradeModifiersFor(event, attribute);
         double finalValue = value;
         if (hasUpgradeModifier) {
            removeUpgradeModifiersFor(event, attribute);
            finalValue = value + getUpgradeBonus(event, attribute, operation);
         }

         if (!(Math.abs(finalValue) <= 1.0E-6)) {
            event.addModifier(
               attribute, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("somakespells", modifierPath), finalValue, operation), slotGroup
            );
         }
      }
   }

   private static void applyOptionalRegistryAttribute(
      ItemAttributeModifierEvent event,
      boolean shouldOverride,
      ResourceLocation attributeId,
      double value,
      String modifierPath,
      Operation operation,
      EquipmentSlotGroup slotGroup
   ) {
      if (shouldOverride) {
         BuiltInRegistries.ATTRIBUTE
            .getHolder(attributeId)
            .ifPresent(attribute -> applyOptionalAttribute(event, true, attribute, value, modifierPath, operation, slotGroup));
      }
   }

   private static void addFixedAttribute(
      ItemAttributeModifierEvent event,
      Holder<Attribute> attribute,
      ResourceLocation modifierId,
      double value,
      Operation operation,
      EquipmentSlotGroup slotGroup
   ) {
      event.removeModifier(attribute, modifierId);
      if (!(Math.abs(value) <= 1.0E-6)) {
         event.addModifier(attribute, new AttributeModifier(modifierId, value, operation), slotGroup);
      }
   }

   private static void applyOptionalAttribute(
      CurioAttributeModifierEvent event, boolean shouldOverride, Holder<Attribute> attribute, double value, String modifierPath, Operation operation
   ) {
      if (shouldOverride) {
         boolean hasUpgradeModifier = hasUpgradeModifier(event, attribute, operation);
         removeNonUpgradeModifiersFor(event, attribute);
         double finalValue = value;
         if (hasUpgradeModifier) {
            removeUpgradeModifiersFor(event, attribute);
            finalValue = value + getUpgradeBonus(event.getItemStack(), attribute, operation);
         }

         if (!(Math.abs(finalValue) <= 1.0E-6)) {
            event.addModifier(attribute, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("somakespells", modifierPath), finalValue, operation));
         }
      }
   }

   private static void applyOptionalRegistryAttribute(
      CurioAttributeModifierEvent event, boolean shouldOverride, ResourceLocation attributeId, double value, String modifierPath, Operation operation
   ) {
      if (shouldOverride) {
         BuiltInRegistries.ATTRIBUTE
            .getHolder(attributeId)
            .ifPresent(attribute -> applyOptionalAttribute(event, true, attribute, value, modifierPath, operation));
      }
   }

   private static void removeNonUpgradeModifiersFor(ItemAttributeModifierEvent event, Holder<Attribute> attribute) {
      for (net.minecraft.world.item.component.ItemAttributeModifiers.Entry entry : List.copyOf(event.getModifiers())) {
         if (entry.attribute().equals(attribute) && !isUpgradeModifier(entry.modifier())) {
            event.removeModifier(attribute, entry.modifier().id());
         }
      }
   }

   private static void removeNonUpgradeModifiersFor(CurioAttributeModifierEvent event, Holder<Attribute> attribute) {
      for (Entry<Holder<Attribute>, AttributeModifier> entry : List.copyOf(event.getModifiers().entries())) {
         if (entry.getKey().equals(attribute) && !isUpgradeModifier(entry.getValue())) {
            event.removeModifier(attribute, entry.getValue());
         }
      }
   }

   private static boolean isUpgradeModifier(AttributeModifier modifier) {
      ResourceLocation modifierId = modifier.id();
      return modifierId.getNamespace().equals("irons_spellbooks") && modifierId.getPath().contains("_upgrade_");
   }

   private static boolean hasUpgradeModifier(ItemAttributeModifierEvent event, Holder<Attribute> attribute, Operation operation) {
      for (net.minecraft.world.item.component.ItemAttributeModifiers.Entry entry : event.getModifiers()) {
         if (entry.attribute().equals(attribute) && entry.modifier().operation().equals(operation) && isUpgradeModifier(entry.modifier())) {
            return true;
         }
      }

      return false;
   }

   private static boolean hasUpgradeModifier(CurioAttributeModifierEvent event, Holder<Attribute> attribute, Operation operation) {
      for (Entry<Holder<Attribute>, AttributeModifier> entry : event.getModifiers().entries()) {
         if (entry.getKey().equals(attribute) && entry.getValue().operation().equals(operation) && isUpgradeModifier(entry.getValue())) {
            return true;
         }
      }

      return false;
   }

   private static void removeUpgradeModifiersFor(ItemAttributeModifierEvent event, Holder<Attribute> attribute) {
      for (net.minecraft.world.item.component.ItemAttributeModifiers.Entry entry : List.copyOf(event.getModifiers())) {
         if (entry.attribute().equals(attribute) && isUpgradeModifier(entry.modifier())) {
            event.removeModifier(attribute, entry.modifier().id());
         }
      }
   }

   private static void removeUpgradeModifiersFor(CurioAttributeModifierEvent event, Holder<Attribute> attribute) {
      for (Entry<Holder<Attribute>, AttributeModifier> entry : List.copyOf(event.getModifiers().entries())) {
         if (entry.getKey().equals(attribute) && isUpgradeModifier(entry.getValue())) {
            event.removeModifier(attribute, entry.getValue());
         }
      }
   }

   private static double getUpgradeBonus(ItemAttributeModifierEvent event, Holder<Attribute> attribute, Operation operation) {
      return getUpgradeBonus(event.getItemStack(), attribute, operation);
   }

   private static double getUpgradeBonus(ItemStack itemStack, Holder<Attribute> attribute, Operation operation) {
      UpgradeData upgradeData = UpgradeData.getUpgradeData(itemStack);
      if (upgradeData == UpgradeData.NONE) {
         return 0.0;
      } else {
         double bonus = 0.0;

         for (Entry<Holder<UpgradeOrbType>, Integer> entry : upgradeData.upgrades().entrySet()) {
            UpgradeOrbType upgradeType = (UpgradeOrbType)entry.getKey().value();
            if (upgradeType.attribute().equals(attribute) && upgradeType.operation().equals(operation)) {
               bonus += upgradeType.amount() * entry.getValue().intValue();
            }
         }

         return bonus;
      }
   }

   private static void applyOptionalSpellPower(
      ItemAttributeModifierEvent event, boolean shouldOverride, Holder<Attribute> attribute, double value, String modifierPath
   ) {
      applyOptionalAttribute(event, shouldOverride, attribute, value, modifierPath, Operation.ADD_MULTIPLIED_BASE, EquipmentSlotGroup.MAINHAND);
   }

   private static void applyOptionalRegistrySpellPower(
      ItemAttributeModifierEvent event, boolean shouldOverride, ResourceLocation attributeId, double value, String modifierPath
   ) {
      if (shouldOverride) {
         BuiltInRegistries.ATTRIBUTE.getHolder(attributeId).ifPresent(attribute -> applyOptionalSpellPower(event, true, attribute, value, modifierPath));
      }
   }

   private record ArmorSetBinding(String armorKey, String namespace, String pathPrefix, boolean requiresTunes, boolean requiresGeomancy) {
      private boolean matches(ResourceLocation itemId) {
         return itemId.getNamespace().equals(this.namespace) && itemId.getPath().startsWith(this.pathPrefix);
      }
   }

   @EventBusSubscriber(
      modid = "somakespells",
      value = {Dist.CLIENT},
      bus = Bus.GAME
   )
   public static class ClientEvents {
      @SubscribeEvent
      public static void onClientLogout(LoggingOut event) {
         WeaponBalanceClientState.clear();
      }
   }

   @EventBusSubscriber(
      modid = "somakespells",
      bus = Bus.GAME
   )
   public static class CurioEvents {
      @SubscribeEvent
      public static void modifySpellbookCurioAttributes(CurioAttributeModifierEvent event) {
         if (event.getItemStack().getItem() instanceof SpellBook) {
            if (Curios.SPELLBOOK_SLOT.equals(event.getSlotContext().identifier())) {
               ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
               MagicAttributeOverrideValues spellbookOverride = AttributeHandler.resolveSpellbookOverride(itemId);
               if (spellbookOverride != null && spellbookOverride.enabled()) {
                  AttributeHandler.applySpellbookOverride(event, spellbookOverride);
               }
            }
         }
      }
   }
}
