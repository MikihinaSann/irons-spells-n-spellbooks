package com.somake.somakespells.network;

import com.somake.somakespells.config.ArmorBalanceConfig;
import com.somake.somakespells.config.WeaponBalanceConfig;
import com.somake.somakespells.config.magic.MagicAttributeOverrideValues;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class WeaponBalanceClientState {
   private static volatile Map<String, WeaponBalanceConfig.WeaponOverrideValues> serverWeaponSnapshot = Collections.emptyMap();
   private static volatile Map<String, ArmorBalanceConfig.ArmorSetOverrideValues> serverArmorSnapshot = Collections.emptyMap();
   private static volatile Map<String, ArmorBalanceConfig.DynamicArmorPieceValues> serverCustomArmorSnapshot = Collections.emptyMap();
   private static volatile Map<String, MagicAttributeOverrideValues> serverSpellbookSnapshot = Collections.emptyMap();
   private static volatile Map<String, MagicAttributeOverrideValues> serverStaffSnapshot = Collections.emptyMap();

   private WeaponBalanceClientState() {
   }

   public static void setServerWeaponSnapshot(Map<String, WeaponBalanceConfig.WeaponOverrideValues> snapshot) {
      serverWeaponSnapshot = Collections.unmodifiableMap(new HashMap<>(snapshot));
   }

   public static void setServerArmorSnapshot(Map<String, ArmorBalanceConfig.ArmorSetOverrideValues> snapshot) {
      serverArmorSnapshot = Collections.unmodifiableMap(new HashMap<>(snapshot));
   }

   public static void setServerCustomArmorSnapshot(Map<String, ArmorBalanceConfig.DynamicArmorPieceValues> snapshot) {
      serverCustomArmorSnapshot = Collections.unmodifiableMap(new HashMap<>(snapshot));
   }

   public static void setServerSpellbookSnapshot(Map<String, MagicAttributeOverrideValues> snapshot) {
      serverSpellbookSnapshot = Collections.unmodifiableMap(new HashMap<>(snapshot));
   }

   public static void setServerStaffSnapshot(Map<String, MagicAttributeOverrideValues> snapshot) {
      serverStaffSnapshot = Collections.unmodifiableMap(new HashMap<>(snapshot));
   }

   public static void clear() {
      serverWeaponSnapshot = Collections.emptyMap();
      serverArmorSnapshot = Collections.emptyMap();
      serverCustomArmorSnapshot = Collections.emptyMap();
      serverSpellbookSnapshot = Collections.emptyMap();
      serverStaffSnapshot = Collections.emptyMap();
   }

   public static WeaponBalanceConfig.WeaponOverrideValues getWeaponForItem(ResourceLocation itemId) {
      return serverWeaponSnapshot.get(itemId.toString());
   }

   public static ArmorBalanceConfig.ArmorSetOverrideValues getArmorForKey(String armorKey) {
      return serverArmorSnapshot.get(armorKey);
   }

   public static ArmorBalanceConfig.DynamicArmorPieceValues getCustomArmorForItem(ResourceLocation itemId) {
      return serverCustomArmorSnapshot.get(itemId.toString());
   }

   public static MagicAttributeOverrideValues getSpellbookForItem(ResourceLocation itemId) {
      return serverSpellbookSnapshot.get(itemId.toString());
   }

   public static MagicAttributeOverrideValues getStaffForItem(ResourceLocation itemId) {
      return serverStaffSnapshot.get(itemId.toString());
   }
}
