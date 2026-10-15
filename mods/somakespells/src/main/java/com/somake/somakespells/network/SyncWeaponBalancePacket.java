package com.somake.somakespells.network;

import com.somake.somakespells.config.ArmorBalanceConfig;
import com.somake.somakespells.config.WeaponBalanceConfig;
import com.somake.somakespells.config.magic.MagicAttributeOverrideValues;
import com.somake.somakespells.config.magic.MagicItemProfile;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncWeaponBalancePacket(
   Map<String, WeaponBalanceConfig.WeaponOverrideValues> weaponOverrides,
   Map<String, ArmorBalanceConfig.ArmorSetOverrideValues> armorOverrides,
   Map<String, ArmorBalanceConfig.DynamicArmorPieceValues> customArmorOverrides,
   Map<String, MagicAttributeOverrideValues> spellbookOverrides,
   Map<String, MagicAttributeOverrideValues> staffOverrides
) implements CustomPacketPayload {
   public static final Type<SyncWeaponBalancePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("somakespells", "sync_weapon_balance"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SyncWeaponBalancePacket> STREAM_CODEC = CustomPacketPayload.codec(
      SyncWeaponBalancePacket::write, SyncWeaponBalancePacket::new
   );

   public SyncWeaponBalancePacket(RegistryFriendlyByteBuf buffer) {
      this(readWeaponMap(buffer), readArmorMap(buffer), readCustomArmorMap(buffer), readMagicMap(buffer), readMagicMap(buffer));
   }

   private static Map<String, WeaponBalanceConfig.WeaponOverrideValues> readWeaponMap(RegistryFriendlyByteBuf buffer) {
      Map<String, WeaponBalanceConfig.WeaponOverrideValues> weaponMap = new HashMap<>();
      int weaponSize = buffer.readInt();

      for (int i = 0; i < weaponSize; i++) {
         String itemId = buffer.readUtf();
         boolean enabled = buffer.readBoolean();
         boolean showInListings = buffer.readBoolean();
         double attackDamage = buffer.readDouble();
         double attackSpeed = buffer.readDouble();
         double entityInteractionRange = buffer.readDouble();
         boolean overrideInfernalFireSpellPower = buffer.readBoolean();
         double infernalFireSpellPower = buffer.readDouble();
         boolean overrideFireSpellPower = buffer.readBoolean();
         double fireSpellPower = buffer.readDouble();
         boolean overrideIceSpellPower = buffer.readBoolean();
         double iceSpellPower = buffer.readDouble();
         boolean overrideLightningSpellPower = buffer.readBoolean();
         double lightningSpellPower = buffer.readDouble();
         boolean overrideEnderSpellPower = buffer.readBoolean();
         double enderSpellPower = buffer.readDouble();
         boolean overrideBloodSpellPower = buffer.readBoolean();
         double bloodSpellPower = buffer.readDouble();
         boolean overrideNatureSpellPower = buffer.readBoolean();
         double natureSpellPower = buffer.readDouble();
         boolean overrideSpiritSpellPower = buffer.readBoolean();
         double spiritSpellPower = buffer.readDouble();
         boolean overrideSymmetrySpellPower = buffer.readBoolean();
         double symmetrySpellPower = buffer.readDouble();
         boolean overrideAquaSpellPower = buffer.readBoolean();
         double aquaSpellPower = buffer.readDouble();
         boolean overrideSpellResist = buffer.readBoolean();
         double spellResist = buffer.readDouble();
         boolean overrideHolySpellPower = buffer.readBoolean();
         double holySpellPower = buffer.readDouble();
         boolean overrideEldritchSpellPower = buffer.readBoolean();
         double eldritchSpellPower = buffer.readDouble();
         boolean overrideSoundSpellPower = buffer.readBoolean();
         double soundSpellPower = buffer.readDouble();
         boolean overrideGeoSpellPower = buffer.readBoolean();
         double geoSpellPower = buffer.readDouble();
         boolean overrideEvocationSpellPower = buffer.readBoolean();
         double evocationSpellPower = buffer.readDouble();
         weaponMap.put(
            itemId,
            new WeaponBalanceConfig.WeaponOverrideValues(
               enabled,
               showInListings,
               attackDamage,
               attackSpeed,
               entityInteractionRange,
               overrideInfernalFireSpellPower,
               infernalFireSpellPower,
               overrideFireSpellPower,
               fireSpellPower,
               overrideIceSpellPower,
               iceSpellPower,
               overrideLightningSpellPower,
               lightningSpellPower,
               overrideEnderSpellPower,
               enderSpellPower,
               overrideBloodSpellPower,
               bloodSpellPower,
               overrideNatureSpellPower,
               natureSpellPower,
               overrideSpiritSpellPower,
               spiritSpellPower,
               overrideSymmetrySpellPower,
               symmetrySpellPower,
               overrideAquaSpellPower,
               aquaSpellPower,
               overrideSpellResist,
               spellResist,
               overrideHolySpellPower,
               holySpellPower,
               overrideEldritchSpellPower,
               eldritchSpellPower,
               overrideSoundSpellPower,
               soundSpellPower,
               overrideGeoSpellPower,
               geoSpellPower,
               overrideEvocationSpellPower,
               evocationSpellPower
            )
         );
      }

      return weaponMap;
   }

   private static Map<String, ArmorBalanceConfig.ArmorSetOverrideValues> readArmorMap(RegistryFriendlyByteBuf buffer) {
      Map<String, ArmorBalanceConfig.ArmorSetOverrideValues> armorMap = new HashMap<>();
      int armorSize = buffer.readInt();

      for (int i = 0; i < armorSize; i++) {
         String armorKey = buffer.readUtf();
         boolean enabled = buffer.readBoolean();
         boolean overrideArmor = buffer.readBoolean();
         double armorHelmet = buffer.readDouble();
         double armorChestplate = buffer.readDouble();
         double armorLeggings = buffer.readDouble();
         double armorBoots = buffer.readDouble();
         boolean overrideArmorToughness = buffer.readBoolean();
         double armorToughness = buffer.readDouble();
         boolean overrideMaxMana = buffer.readBoolean();
         double maxMana = buffer.readDouble();
         boolean overrideSpellPower = buffer.readBoolean();
         double spellPower = buffer.readDouble();
         boolean overrideFireSpellPower = buffer.readBoolean();
         double fireSpellPower = buffer.readDouble();
         boolean overrideIceSpellPower = buffer.readBoolean();
         double iceSpellPower = buffer.readDouble();
         boolean overrideLightningSpellPower = buffer.readBoolean();
         double lightningSpellPower = buffer.readDouble();
         boolean overrideEnderSpellPower = buffer.readBoolean();
         double enderSpellPower = buffer.readDouble();
         boolean overrideBloodSpellPower = buffer.readBoolean();
         double bloodSpellPower = buffer.readDouble();
         boolean overrideNatureSpellPower = buffer.readBoolean();
         double natureSpellPower = buffer.readDouble();
         boolean overrideSpiritSpellPower = buffer.readBoolean();
         double spiritSpellPower = buffer.readDouble();
         boolean overrideSymmetrySpellPower = buffer.readBoolean();
         double symmetrySpellPower = buffer.readDouble();
         boolean overrideAquaSpellPower = buffer.readBoolean();
         double aquaSpellPower = buffer.readDouble();
         boolean overrideHolySpellPower = buffer.readBoolean();
         double holySpellPower = buffer.readDouble();
         boolean overrideEvocationSpellPower = buffer.readBoolean();
         double evocationSpellPower = buffer.readDouble();
         boolean overrideSoundSpellPower = buffer.readBoolean();
         double soundSpellPower = buffer.readDouble();
         boolean overrideGeoSpellPower = buffer.readBoolean();
         double geoSpellPower = buffer.readDouble();
         armorMap.put(
            armorKey,
            new ArmorBalanceConfig.ArmorSetOverrideValues(
               enabled,
               overrideArmor,
               armorHelmet,
               armorChestplate,
               armorLeggings,
               armorBoots,
               overrideArmorToughness,
               armorToughness,
               overrideMaxMana,
               maxMana,
               overrideSpellPower,
               spellPower,
               overrideFireSpellPower,
               fireSpellPower,
               overrideIceSpellPower,
               iceSpellPower,
               overrideLightningSpellPower,
               lightningSpellPower,
               overrideEnderSpellPower,
               enderSpellPower,
               overrideBloodSpellPower,
               bloodSpellPower,
               overrideNatureSpellPower,
               natureSpellPower,
               overrideSpiritSpellPower,
               spiritSpellPower,
               overrideSymmetrySpellPower,
               symmetrySpellPower,
               overrideAquaSpellPower,
               aquaSpellPower,
               overrideHolySpellPower,
               holySpellPower,
               overrideEvocationSpellPower,
               evocationSpellPower,
               overrideSoundSpellPower,
               soundSpellPower,
               overrideGeoSpellPower,
               geoSpellPower
            )
         );
      }

      return armorMap;
   }

   private static Map<String, MagicAttributeOverrideValues> readMagicMap(RegistryFriendlyByteBuf buffer) {
      Map<String, MagicAttributeOverrideValues> magicMap = new HashMap<>();
      int size = buffer.readInt();

      for (int i = 0; i < size; i++) {
         String itemId = buffer.readUtf();
         MagicItemProfile profile = (MagicItemProfile)buffer.readEnum(MagicItemProfile.class);
         boolean enabled = buffer.readBoolean();
         boolean overrideMaxMana = buffer.readBoolean();
         double maxMana = buffer.readDouble();
         boolean overrideManaRegen = buffer.readBoolean();
         double manaRegen = buffer.readDouble();
         boolean overrideSpellPower = buffer.readBoolean();
         double spellPower = buffer.readDouble();
         boolean overrideCastTimeReduction = buffer.readBoolean();
         double castTimeReduction = buffer.readDouble();
         boolean overrideCooldownReduction = buffer.readBoolean();
         double cooldownReduction = buffer.readDouble();
         boolean overrideSpellResist = buffer.readBoolean();
         double spellResist = buffer.readDouble();
         boolean overrideSummonDamage = buffer.readBoolean();
         double summonDamage = buffer.readDouble();
         boolean overrideFireSpellPower = buffer.readBoolean();
         double fireSpellPower = buffer.readDouble();
         boolean overrideIceSpellPower = buffer.readBoolean();
         double iceSpellPower = buffer.readDouble();
         boolean overrideLightningSpellPower = buffer.readBoolean();
         double lightningSpellPower = buffer.readDouble();
         boolean overrideEnderSpellPower = buffer.readBoolean();
         double enderSpellPower = buffer.readDouble();
         boolean overrideBloodSpellPower = buffer.readBoolean();
         double bloodSpellPower = buffer.readDouble();
         boolean overrideNatureSpellPower = buffer.readBoolean();
         double natureSpellPower = buffer.readDouble();
         boolean overrideHolySpellPower = buffer.readBoolean();
         double holySpellPower = buffer.readDouble();
         boolean overrideEvocationSpellPower = buffer.readBoolean();
         double evocationSpellPower = buffer.readDouble();
         boolean overrideEldritchSpellPower = buffer.readBoolean();
         double eldritchSpellPower = buffer.readDouble();
         boolean overrideAquaSpellPower = buffer.readBoolean();
         double aquaSpellPower = buffer.readDouble();
         boolean overrideSpiritSpellPower = buffer.readBoolean();
         double spiritSpellPower = buffer.readDouble();
         boolean overrideSymmetrySpellPower = buffer.readBoolean();
         double symmetrySpellPower = buffer.readDouble();
         boolean overrideSoundSpellPower = buffer.readBoolean();
         double soundSpellPower = buffer.readDouble();
         boolean overrideGeoSpellPower = buffer.readBoolean();
         double geoSpellPower = buffer.readDouble();
         boolean overrideAttackDamage = buffer.readBoolean();
         double attackDamage = buffer.readDouble();
         boolean overrideAttackSpeed = buffer.readBoolean();
         double attackSpeed = buffer.readDouble();
         boolean overrideEntityInteractionRange = buffer.readBoolean();
         double entityInteractionRange = buffer.readDouble();
         magicMap.put(
            itemId,
            new MagicAttributeOverrideValues(
               profile,
               enabled,
               overrideMaxMana,
               maxMana,
               overrideManaRegen,
               manaRegen,
               overrideSpellPower,
               spellPower,
               overrideCastTimeReduction,
               castTimeReduction,
               overrideCooldownReduction,
               cooldownReduction,
               overrideSpellResist,
               spellResist,
               overrideSummonDamage,
               summonDamage,
               overrideFireSpellPower,
               fireSpellPower,
               overrideIceSpellPower,
               iceSpellPower,
               overrideLightningSpellPower,
               lightningSpellPower,
               overrideEnderSpellPower,
               enderSpellPower,
               overrideBloodSpellPower,
               bloodSpellPower,
               overrideNatureSpellPower,
               natureSpellPower,
               overrideHolySpellPower,
               holySpellPower,
               overrideEvocationSpellPower,
               evocationSpellPower,
               overrideEldritchSpellPower,
               eldritchSpellPower,
               overrideAquaSpellPower,
               aquaSpellPower,
               overrideSpiritSpellPower,
               spiritSpellPower,
               overrideSymmetrySpellPower,
               symmetrySpellPower,
               overrideSoundSpellPower,
               soundSpellPower,
               overrideGeoSpellPower,
               geoSpellPower,
               overrideAttackDamage,
               attackDamage,
               overrideAttackSpeed,
               attackSpeed,
               overrideEntityInteractionRange,
               entityInteractionRange
            )
         );
      }

      return magicMap;
   }

   private static Map<String, ArmorBalanceConfig.DynamicArmorPieceValues> readCustomArmorMap(RegistryFriendlyByteBuf buffer) {
      Map<String, ArmorBalanceConfig.DynamicArmorPieceValues> armorMap = new HashMap<>();
      int size = buffer.readInt();

      for (int i = 0; i < size; i++) {
         String itemId = buffer.readUtf();
         boolean enabled = buffer.readBoolean();
         boolean overrideArmor = buffer.readBoolean();
         double armorValue = buffer.readDouble();
         boolean overrideArmorToughness = buffer.readBoolean();
         double armorToughness = buffer.readDouble();
         boolean overrideMaxMana = buffer.readBoolean();
         double maxMana = buffer.readDouble();
         boolean overrideSpellPower = buffer.readBoolean();
         double spellPower = buffer.readDouble();
         boolean overrideFireSpellPower = buffer.readBoolean();
         double fireSpellPower = buffer.readDouble();
         boolean overrideIceSpellPower = buffer.readBoolean();
         double iceSpellPower = buffer.readDouble();
         boolean overrideLightningSpellPower = buffer.readBoolean();
         double lightningSpellPower = buffer.readDouble();
         boolean overrideEnderSpellPower = buffer.readBoolean();
         double enderSpellPower = buffer.readDouble();
         boolean overrideBloodSpellPower = buffer.readBoolean();
         double bloodSpellPower = buffer.readDouble();
         boolean overrideNatureSpellPower = buffer.readBoolean();
         double natureSpellPower = buffer.readDouble();
         boolean overrideSpiritSpellPower = buffer.readBoolean();
         double spiritSpellPower = buffer.readDouble();
         boolean overrideSymmetrySpellPower = buffer.readBoolean();
         double symmetrySpellPower = buffer.readDouble();
         boolean overrideAquaSpellPower = buffer.readBoolean();
         double aquaSpellPower = buffer.readDouble();
         boolean overrideHolySpellPower = buffer.readBoolean();
         double holySpellPower = buffer.readDouble();
         boolean overrideEvocationSpellPower = buffer.readBoolean();
         double evocationSpellPower = buffer.readDouble();
         boolean overrideSoundSpellPower = buffer.readBoolean();
         double soundSpellPower = buffer.readDouble();
         boolean overrideGeoSpellPower = buffer.readBoolean();
         double geoSpellPower = buffer.readDouble();
         armorMap.put(
            itemId,
            new ArmorBalanceConfig.DynamicArmorPieceValues(
               enabled,
               overrideArmor,
               armorValue,
               overrideArmorToughness,
               armorToughness,
               overrideMaxMana,
               maxMana,
               overrideSpellPower,
               spellPower,
               overrideFireSpellPower,
               fireSpellPower,
               overrideIceSpellPower,
               iceSpellPower,
               overrideLightningSpellPower,
               lightningSpellPower,
               overrideEnderSpellPower,
               enderSpellPower,
               overrideBloodSpellPower,
               bloodSpellPower,
               overrideNatureSpellPower,
               natureSpellPower,
               overrideSpiritSpellPower,
               spiritSpellPower,
               overrideSymmetrySpellPower,
               symmetrySpellPower,
               overrideAquaSpellPower,
               aquaSpellPower,
               overrideHolySpellPower,
               holySpellPower,
               overrideEvocationSpellPower,
               evocationSpellPower,
               overrideSoundSpellPower,
               soundSpellPower,
               overrideGeoSpellPower,
               geoSpellPower
            )
         );
      }

      return armorMap;
   }

   public void write(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.weaponOverrides.size());
      this.weaponOverrides.forEach((itemId, values) -> {
         buffer.writeUtf(itemId);
         buffer.writeBoolean(values.enabled());
         buffer.writeBoolean(values.showInListings());
         buffer.writeDouble(values.attackDamage());
         buffer.writeDouble(values.attackSpeed());
         buffer.writeDouble(values.entityInteractionRange());
         buffer.writeBoolean(values.overrideInfernalFireSpellPower());
         buffer.writeDouble(values.infernalFireSpellPower());
         buffer.writeBoolean(values.overrideFireSpellPower());
         buffer.writeDouble(values.fireSpellPower());
         buffer.writeBoolean(values.overrideIceSpellPower());
         buffer.writeDouble(values.iceSpellPower());
         buffer.writeBoolean(values.overrideLightningSpellPower());
         buffer.writeDouble(values.lightningSpellPower());
         buffer.writeBoolean(values.overrideEnderSpellPower());
         buffer.writeDouble(values.enderSpellPower());
         buffer.writeBoolean(values.overrideBloodSpellPower());
         buffer.writeDouble(values.bloodSpellPower());
         buffer.writeBoolean(values.overrideNatureSpellPower());
         buffer.writeDouble(values.natureSpellPower());
         buffer.writeBoolean(values.overrideSpiritSpellPower());
         buffer.writeDouble(values.spiritSpellPower());
         buffer.writeBoolean(values.overrideSymmetrySpellPower());
         buffer.writeDouble(values.symmetrySpellPower());
         buffer.writeBoolean(values.overrideAquaSpellPower());
         buffer.writeDouble(values.aquaSpellPower());
         buffer.writeBoolean(values.overrideSpellResist());
         buffer.writeDouble(values.spellResist());
         buffer.writeBoolean(values.overrideHolySpellPower());
         buffer.writeDouble(values.holySpellPower());
         buffer.writeBoolean(values.overrideEldritchSpellPower());
         buffer.writeDouble(values.eldritchSpellPower());
         buffer.writeBoolean(values.overrideSoundSpellPower());
         buffer.writeDouble(values.soundSpellPower());
         buffer.writeBoolean(values.overrideGeoSpellPower());
         buffer.writeDouble(values.geoSpellPower());
         buffer.writeBoolean(values.overrideEvocationSpellPower());
         buffer.writeDouble(values.evocationSpellPower());
      });
      buffer.writeInt(this.armorOverrides.size());
      this.armorOverrides.forEach((armorKey, values) -> {
         buffer.writeUtf(armorKey);
         buffer.writeBoolean(values.enabled());
         buffer.writeBoolean(values.overrideArmor());
         buffer.writeDouble(values.armorHelmet());
         buffer.writeDouble(values.armorChestplate());
         buffer.writeDouble(values.armorLeggings());
         buffer.writeDouble(values.armorBoots());
         buffer.writeBoolean(values.overrideArmorToughness());
         buffer.writeDouble(values.armorToughness());
         buffer.writeBoolean(values.overrideMaxMana());
         buffer.writeDouble(values.maxMana());
         buffer.writeBoolean(values.overrideSpellPower());
         buffer.writeDouble(values.spellPower());
         buffer.writeBoolean(values.overrideFireSpellPower());
         buffer.writeDouble(values.fireSpellPower());
         buffer.writeBoolean(values.overrideIceSpellPower());
         buffer.writeDouble(values.iceSpellPower());
         buffer.writeBoolean(values.overrideLightningSpellPower());
         buffer.writeDouble(values.lightningSpellPower());
         buffer.writeBoolean(values.overrideEnderSpellPower());
         buffer.writeDouble(values.enderSpellPower());
         buffer.writeBoolean(values.overrideBloodSpellPower());
         buffer.writeDouble(values.bloodSpellPower());
         buffer.writeBoolean(values.overrideNatureSpellPower());
         buffer.writeDouble(values.natureSpellPower());
         buffer.writeBoolean(values.overrideSpiritSpellPower());
         buffer.writeDouble(values.spiritSpellPower());
         buffer.writeBoolean(values.overrideSymmetrySpellPower());
         buffer.writeDouble(values.symmetrySpellPower());
         buffer.writeBoolean(values.overrideAquaSpellPower());
         buffer.writeDouble(values.aquaSpellPower());
         buffer.writeBoolean(values.overrideHolySpellPower());
         buffer.writeDouble(values.holySpellPower());
         buffer.writeBoolean(values.overrideEvocationSpellPower());
         buffer.writeDouble(values.evocationSpellPower());
         buffer.writeBoolean(values.overrideSoundSpellPower());
         buffer.writeDouble(values.soundSpellPower());
         buffer.writeBoolean(values.overrideGeoSpellPower());
         buffer.writeDouble(values.geoSpellPower());
      });
      buffer.writeInt(this.customArmorOverrides.size());
      this.customArmorOverrides.forEach((itemId, values) -> {
         buffer.writeUtf(itemId);
         buffer.writeBoolean(values.enabled());
         buffer.writeBoolean(values.overrideArmor());
         buffer.writeDouble(values.armorValue());
         buffer.writeBoolean(values.overrideArmorToughness());
         buffer.writeDouble(values.armorToughness());
         buffer.writeBoolean(values.overrideMaxMana());
         buffer.writeDouble(values.maxMana());
         buffer.writeBoolean(values.overrideSpellPower());
         buffer.writeDouble(values.spellPower());
         buffer.writeBoolean(values.overrideFireSpellPower());
         buffer.writeDouble(values.fireSpellPower());
         buffer.writeBoolean(values.overrideIceSpellPower());
         buffer.writeDouble(values.iceSpellPower());
         buffer.writeBoolean(values.overrideLightningSpellPower());
         buffer.writeDouble(values.lightningSpellPower());
         buffer.writeBoolean(values.overrideEnderSpellPower());
         buffer.writeDouble(values.enderSpellPower());
         buffer.writeBoolean(values.overrideBloodSpellPower());
         buffer.writeDouble(values.bloodSpellPower());
         buffer.writeBoolean(values.overrideNatureSpellPower());
         buffer.writeDouble(values.natureSpellPower());
         buffer.writeBoolean(values.overrideSpiritSpellPower());
         buffer.writeDouble(values.spiritSpellPower());
         buffer.writeBoolean(values.overrideSymmetrySpellPower());
         buffer.writeDouble(values.symmetrySpellPower());
         buffer.writeBoolean(values.overrideAquaSpellPower());
         buffer.writeDouble(values.aquaSpellPower());
         buffer.writeBoolean(values.overrideHolySpellPower());
         buffer.writeDouble(values.holySpellPower());
         buffer.writeBoolean(values.overrideEvocationSpellPower());
         buffer.writeDouble(values.evocationSpellPower());
         buffer.writeBoolean(values.overrideSoundSpellPower());
         buffer.writeDouble(values.soundSpellPower());
         buffer.writeBoolean(values.overrideGeoSpellPower());
         buffer.writeDouble(values.geoSpellPower());
      });
      writeMagicMap(buffer, this.spellbookOverrides);
      writeMagicMap(buffer, this.staffOverrides);
   }

   private static void writeMagicMap(RegistryFriendlyByteBuf buffer, Map<String, MagicAttributeOverrideValues> overrides) {
      buffer.writeInt(overrides.size());
      overrides.forEach((itemId, values) -> {
         buffer.writeUtf(itemId);
         buffer.writeEnum(values.profile());
         buffer.writeBoolean(values.enabled());
         buffer.writeBoolean(values.overrideMaxMana());
         buffer.writeDouble(values.maxMana());
         buffer.writeBoolean(values.overrideManaRegen());
         buffer.writeDouble(values.manaRegen());
         buffer.writeBoolean(values.overrideSpellPower());
         buffer.writeDouble(values.spellPower());
         buffer.writeBoolean(values.overrideCastTimeReduction());
         buffer.writeDouble(values.castTimeReduction());
         buffer.writeBoolean(values.overrideCooldownReduction());
         buffer.writeDouble(values.cooldownReduction());
         buffer.writeBoolean(values.overrideSpellResist());
         buffer.writeDouble(values.spellResist());
         buffer.writeBoolean(values.overrideSummonDamage());
         buffer.writeDouble(values.summonDamage());
         buffer.writeBoolean(values.overrideFireSpellPower());
         buffer.writeDouble(values.fireSpellPower());
         buffer.writeBoolean(values.overrideIceSpellPower());
         buffer.writeDouble(values.iceSpellPower());
         buffer.writeBoolean(values.overrideLightningSpellPower());
         buffer.writeDouble(values.lightningSpellPower());
         buffer.writeBoolean(values.overrideEnderSpellPower());
         buffer.writeDouble(values.enderSpellPower());
         buffer.writeBoolean(values.overrideBloodSpellPower());
         buffer.writeDouble(values.bloodSpellPower());
         buffer.writeBoolean(values.overrideNatureSpellPower());
         buffer.writeDouble(values.natureSpellPower());
         buffer.writeBoolean(values.overrideHolySpellPower());
         buffer.writeDouble(values.holySpellPower());
         buffer.writeBoolean(values.overrideEvocationSpellPower());
         buffer.writeDouble(values.evocationSpellPower());
         buffer.writeBoolean(values.overrideEldritchSpellPower());
         buffer.writeDouble(values.eldritchSpellPower());
         buffer.writeBoolean(values.overrideAquaSpellPower());
         buffer.writeDouble(values.aquaSpellPower());
         buffer.writeBoolean(values.overrideSpiritSpellPower());
         buffer.writeDouble(values.spiritSpellPower());
         buffer.writeBoolean(values.overrideSymmetrySpellPower());
         buffer.writeDouble(values.symmetrySpellPower());
         buffer.writeBoolean(values.overrideSoundSpellPower());
         buffer.writeDouble(values.soundSpellPower());
         buffer.writeBoolean(values.overrideGeoSpellPower());
         buffer.writeDouble(values.geoSpellPower());
         buffer.writeBoolean(values.overrideAttackDamage());
         buffer.writeDouble(values.attackDamage());
         buffer.writeBoolean(values.overrideAttackSpeed());
         buffer.writeDouble(values.attackSpeed());
         buffer.writeBoolean(values.overrideEntityInteractionRange());
         buffer.writeDouble(values.entityInteractionRange());
      });
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(SyncWeaponBalancePacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         WeaponBalanceClientState.setServerWeaponSnapshot(packet.weaponOverrides());
         WeaponBalanceClientState.setServerArmorSnapshot(packet.armorOverrides());
         WeaponBalanceClientState.setServerCustomArmorSnapshot(packet.customArmorOverrides());
         WeaponBalanceClientState.setServerSpellbookSnapshot(packet.spellbookOverrides());
         WeaponBalanceClientState.setServerStaffSnapshot(packet.staffOverrides());
      });
   }
}
