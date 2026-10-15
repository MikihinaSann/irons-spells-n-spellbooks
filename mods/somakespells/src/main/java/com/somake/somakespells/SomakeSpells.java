package com.somake.somakespells;

import com.mojang.logging.LogUtils;
import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.config.ArmorBalanceConfig;
import com.somake.somakespells.config.Config;
import com.somake.somakespells.config.EffectBalanceConfig;
import com.somake.somakespells.config.SpellbookBalanceConfig;
import com.somake.somakespells.config.StaffBalanceConfig;
import com.somake.somakespells.config.WeaponBalanceConfig;
import com.somake.somakespells.config.progression.SpellbookProgressionConfig;
import com.somake.somakespells.item.armor.ModArmorMaterials;
import com.somake.somakespells.registries.ModAttachments;
import com.somake.somakespells.registries.ModAttributes;
import com.somake.somakespells.registries.ModBlockEntities;
import com.somake.somakespells.registries.ModBlocks;
import com.somake.somakespells.registries.ModCompatEntityTypes;
import com.somake.somakespells.registries.ModCompatGhostItems;
import com.somake.somakespells.registries.ModCompatItems;
import com.somake.somakespells.registries.ModCreativeModeTabs;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModItems;
import com.somake.somakespells.registries.ModLootModifiers;
import com.somake.somakespells.registries.ModMenus;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModRecipes;
import com.somake.somakespells.registries.ModSchools;
import com.somake.somakespells.registries.ModSounds;
import com.somake.somakespells.registries.ModSpells;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.slf4j.Logger;

@Mod("somakespells")
public class SomakeSpells {
   public static final String MODID = "somakespells";
   public static final Logger LOGGER = LogUtils.getLogger();
   private static final String GEOMANCY_MODID = "gtbcs_geomancy_plus";
   private static final ResourceLocation GEOMANCY_COMPAT_PACK_ID = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "builtin_resource_packs/geomancy_compat_data"
   );
   private static final ResourceLocation ISS_COMPAT_PACK_ID = ResourceLocation.fromNamespaceAndPath("somakespells", "builtin_resource_packs/iss_compat_data");
   private static final ResourceLocation LEGENDARY_MONSTERS_COMPAT_PACK_ID = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "builtin_resource_packs/legendary_monsters_compat_data"
   );
   private static final ResourceLocation LEGENDARY_MFE_COMPAT_PACK_ID = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "builtin_resource_packs/legendary_mfe_compat_data"
   );
   private static final ResourceLocation LEGENDARY_STANDALONE_COMPAT_PACK_ID = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "builtin_resource_packs/legendary_standalone_compat_data"
   );

   public SomakeSpells(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(this::commonSetup);
      modEventBus.addListener(this::addPackFinders);
      ModAttributes.register(modEventBus);
      ModAttachments.register(modEventBus);
      ModSchools.register(modEventBus);
      ModItems.register(modEventBus);
      ModArmorMaterials.register(modEventBus);
      ModBlocks.register(modEventBus);
      ModBlockEntities.register(modEventBus);
      ModLootModifiers.register(modEventBus);
      ModCreativeModeTabs.register(modEventBus);
      ModMenus.register(modEventBus);
      ModEntityTypes.register(modEventBus);
      ModMobEffects.register(modEventBus);
      ModSpells.register(modEventBus);
      ModRecipes.register(modEventBus);
      ModParticles.register(modEventBus);
      ModSounds.register(modEventBus);
      if (BornInChaosCompat.isLoaded()) {
         ModAttributes.registerBornInChaos(modEventBus);
         ModMobEffects.registerBornInChaos(modEventBus);
         ModParticles.registerBornInChaos(modEventBus);
         ModCompatEntityTypes.registerBornInChaos(modEventBus);
      }

      if (ModList.get().isLoaded("gtbcs_geomancy_plus")) {
         LOGGER.info("Geomancy compatibility detected! Registering Geo compat items.");
         ModCompatItems.registerGeo(modEventBus);
         ModCompatEntityTypes.registerGeo(modEventBus);
         ModCompatGhostItems.registerGeo(modEventBus);
      } else {
         LOGGER.info("Geomancy compatibility NOT detected. Geo compat items will not be registered.");
      }

      if (ModList.get().isLoaded("tunes_n_tomes")) {
         LOGGER.info("Tunes 'n Tomes detected! Registering Melody compat items.");
         ModCompatItems.registerSound(modEventBus);
         ModCompatEntityTypes.registerSound(modEventBus);
         ModCompatGhostItems.registerSound(modEventBus);
      } else {
         LOGGER.info("Tunes 'n Tomes NOT detected. Melody compat items will not be registered.");
      }

      if (BornInChaosCompat.isLoaded()) {
         LOGGER.info("Born in Chaos detected! Registering Infernal Fire compat items.");
         ModCompatItems.registerBornInChaos(modEventBus);
      } else {
         LOGGER.info("Born in Chaos NOT detected. Infernal Fire compat items will not be registered.");
      }

      modContainer.registerConfig(Type.COMMON, Config.SPEC, "somakespells/general/common.toml");
      modContainer.registerConfig(Type.COMMON, WeaponBalanceConfig.SPEC, "somakespells/balance/weapons.toml");
      modContainer.registerConfig(Type.COMMON, ArmorBalanceConfig.SPEC, "somakespells/balance/armors.toml");
      modContainer.registerConfig(Type.COMMON, SpellbookBalanceConfig.SPEC, "somakespells/balance/spellbooks.toml");
      modContainer.registerConfig(Type.COMMON, StaffBalanceConfig.SPEC, "somakespells/balance/staffs.toml");
      modContainer.registerConfig(Type.COMMON, EffectBalanceConfig.SPEC, "somakespells/balance/efeitos.toml");
      modContainer.registerConfig(Type.COMMON, SpellbookProgressionConfig.SPEC, "somakespells/progression/spellbooks.toml");
   }

   private void addPackFinders(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.SERVER_DATA) {
         if (ModList.get().isLoaded("gtbcs_geomancy_plus")) {
            event.addPackFinders(
               GEOMANCY_COMPAT_PACK_ID, PackType.SERVER_DATA, Component.literal("Somake Geomancy Compat Data"), PackSource.BUILT_IN, true, Position.TOP
            );
         }

         if (MagicFromTheEastCompat.isLoaded()) {
            event.addPackFinders(ISS_COMPAT_PACK_ID, PackType.SERVER_DATA, Component.literal("Somake ISS Compat Data"), PackSource.BUILT_IN, true, Position.TOP);
         }

         if (LegendaryMonstersCompat.isLoaded()) {
            event.addPackFinders(
               LEGENDARY_MONSTERS_COMPAT_PACK_ID,
               PackType.SERVER_DATA,
               Component.literal("Somake Legendary Monsters Compat Data"),
               PackSource.BUILT_IN,
               true,
               Position.TOP
            );
            ResourceLocation schoolPackId = MagicFromTheEastCompat.isLoaded() ? LEGENDARY_MFE_COMPAT_PACK_ID : LEGENDARY_STANDALONE_COMPAT_PACK_ID;
            String schoolPackName = MagicFromTheEastCompat.isLoaded() ? "Somake Legendary/MFE Compat Data" : "Somake Legendary Standalone Compat Data";
            event.addPackFinders(schoolPackId, PackType.SERVER_DATA, Component.literal(schoolPackName), PackSource.BUILT_IN, true, Position.TOP);
         }
      }
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> {
         SpellbookProgressionConfig.migrateLegacyMobIds();
         SpellbookProgressionConfig.validateMobIds();
         if ((Boolean)Config.REMOVE_CHARGE_SPEED_BOOST.get()) {
            this.patchChargeEffect();
         } else {
            LOGGER.info("Charge speed removal is disabled by config.");
         }

         if ((Boolean)EffectBalanceConfig.MODIFY_VANILLA_SPEED_EFFECT.get()) {
            this.patchVanillaSpeedEffect();
         } else {
            LOGGER.info("Vanilla speed effect patch is disabled by config.");
         }

         if ((Boolean)EffectBalanceConfig.REMOVE_BORN_IN_CHAOS_RAMPAGE_SPEED.get()) {
            this.patchBornInChaosRampageSpeed();
         } else {
            LOGGER.info("Born In Chaos rampage speed patch is disabled by config.");
         }

         this.patchBornInChaosRampageAttackDamage();
         if ((Boolean)EffectBalanceConfig.ADJUST_BORN_IN_CHAOS_RABBIT_AGILITY.get()) {
            this.patchBornInChaosRabbitAgility();
         } else {
            LOGGER.info("Born In Chaos rabbit_agility patch is disabled by config.");
         }

         if ((Boolean)EffectBalanceConfig.REMOVE_BORN_IN_CHAOS_PREDATORS_DESIRE_SPEED.get()) {
            this.patchBornInChaosPredatorsDesireSpeed();
         } else {
            LOGGER.info("Born In Chaos predators_desire speed patch is disabled by config.");
         }

         if ((Boolean)EffectBalanceConfig.MODIFY_BORN_IN_CHAOS_STONE_PERSISTENCE.get()) {
            this.patchBornInChaosStonePersistence();
         } else {
            LOGGER.info("Born In Chaos stone_persistence patch is disabled by config.");
         }

         this.patchBornInChaosStimulation();
         this.patchBornInChaosObsession();
      });
   }

   private void patchBornInChaosRabbitAgility() {
      try {
         ResourceLocation effectId = ResourceLocation.parse("born_in_chaos_v1:rabbit_agility");
         MobEffect effect = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(effectId);
         if (effect == null) {
            LOGGER.warn("Could not find effect in registry: {}", effectId);
            return;
         }

         Field attributeModifiersField = MobEffect.class.getDeclaredField("attributeModifiers");
         attributeModifiersField.setAccessible(true);
         Map<?, ?> modifiers = (Map<?, ?>)attributeModifiersField.get(effect);
         modifiers.remove(Attributes.MOVEMENT_SPEED);
         modifiers.remove(Attributes.ATTACK_DAMAGE);
         effect.addAttributeModifier(
            Attributes.MOVEMENT_SPEED, ResourceLocation.parse("born_in_chaos_v1:effect.rabbit_agility_0"), 0.5, Operation.ADD_MULTIPLIED_BASE
         );
         effect.addAttributeModifier(
            Attributes.ATTACK_DAMAGE, ResourceLocation.parse("born_in_chaos_v1:effect.rabbit_agility_1"), -0.5, Operation.ADD_MULTIPLIED_TOTAL
         );
         LOGGER.info("Patched born_in_chaos_v1:rabbit_agility to speed +50% and attack damage -50%.");
      } catch (Exception var5) {
         LOGGER.error("Failed to patch born_in_chaos_v1:rabbit_agility", var5);
      }
   }

   private void patchBornInChaosPredatorsDesireSpeed() {
      this.patchRemoveMovementSpeedFromEffect(ResourceLocation.parse("born_in_chaos_v1:predators_desire"));
   }

   private void patchBornInChaosStonePersistence() {
      try {
         ResourceLocation effectId = ResourceLocation.parse("born_in_chaos_v1:stone_persistence");
         MobEffect effect = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(effectId);
         if (effect == null) {
            LOGGER.warn("Could not find effect in registry: {}", effectId);
            return;
         }

         Field attributeModifiersField = MobEffect.class.getDeclaredField("attributeModifiers");
         attributeModifiersField.setAccessible(true);
         Map<?, ?> modifiers = (Map<?, ?>)attributeModifiersField.get(effect);
         modifiers.remove(Attributes.ARMOR);
         effect.addAttributeModifier(
            Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.stone_persistence.armor"), 5.0, Operation.ADD_VALUE
         );
         LOGGER.info("Patched born_in_chaos_v1:stone_persistence to add +5 armor.");
      } catch (Exception var5) {
         LOGGER.error("Failed to patch born_in_chaos_v1:stone_persistence", var5);
      }
   }

   private void patchBornInChaosStimulation() {
      try {
         ResourceLocation effectId = ResourceLocation.parse("born_in_chaos_v1:stimulation");
         MobEffect effect = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(effectId);
         if (effect == null) {
            LOGGER.warn("Could not find effect in registry: {}", effectId);
            return;
         }

         Field attributeModifiersField = MobEffect.class.getDeclaredField("attributeModifiers");
         attributeModifiersField.setAccessible(true);
         Map<?, ?> modifiers = (Map<?, ?>)attributeModifiersField.get(effect);
         modifiers.remove(Attributes.MOVEMENT_SPEED);
         modifiers.remove(Attributes.ATTACK_SPEED);
         if (!(Boolean)EffectBalanceConfig.REMOVE_BORN_IN_CHAOS_STIMULATION_SPEED.get()) {
            effect.addAttributeModifier(
               Attributes.MOVEMENT_SPEED, ResourceLocation.parse("born_in_chaos_v1:effect.stimulation_0"), 0.25, Operation.ADD_MULTIPLIED_BASE
            );
         }

         effect.addAttributeModifier(
            Attributes.ATTACK_SPEED,
            ResourceLocation.parse("born_in_chaos_v1:effect.stimulation_1"),
            (Double)EffectBalanceConfig.BORN_IN_CHAOS_STIMULATION_ATTACK_SPEED_PER_LEVEL.get(),
            Operation.ADD_MULTIPLIED_BASE
         );
         LOGGER.info("Patched born_in_chaos_v1:stimulation using config values.");
      } catch (Exception var5) {
         LOGGER.error("Failed to patch born_in_chaos_v1:stimulation", var5);
      }
   }

   private void patchBornInChaosObsession() {
      try {
         ResourceLocation effectId = ResourceLocation.parse("born_in_chaos_v1:obsession");
         MobEffect effect = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(effectId);
         if (effect == null) {
            LOGGER.warn("Could not find effect in registry: {}", effectId);
            return;
         }

         Field attributeModifiersField = MobEffect.class.getDeclaredField("attributeModifiers");
         attributeModifiersField.setAccessible(true);
         Map<?, ?> modifiers = (Map<?, ?>)attributeModifiersField.get(effect);
         modifiers.remove(Attributes.ARMOR);
         modifiers.remove(Attributes.ARMOR_TOUGHNESS);
         modifiers.remove(Attributes.ATTACK_DAMAGE);
         modifiers.remove(Attributes.ATTACK_SPEED);
         modifiers.remove(Attributes.MAX_HEALTH);
         modifiers.remove(Attributes.MOVEMENT_SPEED);
         effect.addAttributeModifier(
            Attributes.ARMOR,
            ResourceLocation.parse("born_in_chaos_v1:effect.obsession_0"),
            (Double)EffectBalanceConfig.BORN_IN_CHAOS_OBSESSION_ARMOR_PER_LEVEL.get(),
            Operation.ADD_VALUE
         );
         effect.addAttributeModifier(
            Attributes.ARMOR_TOUGHNESS,
            ResourceLocation.parse("born_in_chaos_v1:effect.obsession_1"),
            (Double)EffectBalanceConfig.BORN_IN_CHAOS_OBSESSION_ARMOR_TOUGHNESS_PER_LEVEL.get(),
            Operation.ADD_VALUE
         );
         effect.addAttributeModifier(
            Attributes.ATTACK_DAMAGE,
            ResourceLocation.parse("born_in_chaos_v1:effect.obsession_2"),
            (Double)EffectBalanceConfig.BORN_IN_CHAOS_OBSESSION_ATTACK_DAMAGE_PER_LEVEL.get(),
            Operation.ADD_VALUE
         );
         effect.addAttributeModifier(
            Attributes.ATTACK_SPEED,
            ResourceLocation.parse("born_in_chaos_v1:effect.obsession_3"),
            (Double)EffectBalanceConfig.BORN_IN_CHAOS_OBSESSION_ATTACK_SPEED_PER_LEVEL.get(),
            Operation.ADD_MULTIPLIED_BASE
         );
         effect.addAttributeModifier(
            Attributes.MAX_HEALTH,
            ResourceLocation.parse("born_in_chaos_v1:effect.obsession_4"),
            (Double)EffectBalanceConfig.BORN_IN_CHAOS_OBSESSION_MAX_HEALTH_PER_LEVEL.get(),
            Operation.ADD_MULTIPLIED_BASE
         );
         if (!(Boolean)EffectBalanceConfig.REMOVE_BORN_IN_CHAOS_RAMPAGE_SPEED.get()) {
            effect.addAttributeModifier(
               Attributes.MOVEMENT_SPEED,
               ResourceLocation.parse("born_in_chaos_v1:effect.obsession_5"),
               (Double)EffectBalanceConfig.BORN_IN_CHAOS_OBSESSION_MOVEMENT_SPEED_PER_LEVEL.get(),
               Operation.ADD_MULTIPLIED_TOTAL
            );
         }

         LOGGER.info("Patched born_in_chaos_v1:obsession using config values.");
      } catch (Exception var5) {
         LOGGER.error("Failed to patch born_in_chaos_v1:obsession", var5);
      }
   }

   private void patchChargeEffect() {
      try {
         ResourceLocation chargeRl = ResourceLocation.parse("irons_spellbooks:charged");
         MobEffect charge = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(chargeRl);
         if (charge == null) {
            chargeRl = ResourceLocation.parse("irons_spellbooks:charge");
            charge = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(chargeRl);
         }

         if (charge == null) {
            LOGGER.error("Could not find Charge effect in registry!");
            return;
         }

         Field attributeModifiersField = MobEffect.class.getDeclaredField("attributeModifiers");
         attributeModifiersField.setAccessible(true);
         Map<?, ?> modifiers = (Map<?, ?>)attributeModifiersField.get(charge);
         modifiers.remove(Attributes.MOVEMENT_SPEED);
         LOGGER.info("Successfully removed Movement Speed from Charge effect.");
      } catch (Exception var5) {
         LOGGER.error("Failed to remove Movement Speed from Charge effect", var5);
      }
   }

   private void patchVanillaSpeedEffect() {
      try {
         ResourceLocation speedRl = ResourceLocation.withDefaultNamespace("speed");
         MobEffect speed = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(speedRl);
         if (speed == null) {
            LOGGER.error("Could not find vanilla Speed effect in registry!");
            return;
         }

         Field attributeModifiersField = MobEffect.class.getDeclaredField("attributeModifiers");
         attributeModifiersField.setAccessible(true);
         Map<?, ?> modifiers = (Map<?, ?>)attributeModifiersField.get(speed);
         modifiers.remove(Attributes.MOVEMENT_SPEED);
         speed.addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            ResourceLocation.fromNamespaceAndPath("somakespells", "effect.vanilla_speed.movement_speed"),
            0.1,
            Operation.ADD_MULTIPLIED_TOTAL
         );
         LOGGER.info("Successfully modified vanilla Speed effect scaling to 10% per level.");
      } catch (Exception var5) {
         LOGGER.error("Failed to modify vanilla Speed effect", var5);
      }
   }

   private void patchBornInChaosRampageSpeed() {
      this.patchRemoveMovementSpeedFromEffect(ResourceLocation.parse("born_in_chaos_v1:light_rampage"));
      this.patchRemoveMovementSpeedFromEffect(ResourceLocation.parse("born_in_chaos_v1:medium_rampage"));
      this.patchRemoveMovementSpeedFromEffect(ResourceLocation.parse("born_in_chaos_v1:strong_rampage"));
      this.patchRemoveMovementSpeedFromEffect(ResourceLocation.parse("born_in_chaos_v1:furious_rampage"));
      this.patchRemoveMovementSpeedFromEffect(ResourceLocation.parse("born_in_chaos_v1:rampant_rampage"));
      this.patchRemoveMovementSpeedFromEffect(ResourceLocation.parse("born_in_chaos_v1:obsession"));
   }

   private void patchBornInChaosRampageAttackDamage() {
      this.patchRampageAttackDamage(
         ResourceLocation.parse("born_in_chaos_v1:light_rampage"),
         ResourceLocation.parse("born_in_chaos_v1:effect.light_rampage_1"),
         (Double)EffectBalanceConfig.BORN_IN_CHAOS_LIGHT_RAMPAGE_ATTACK_DAMAGE.get()
      );
      this.patchRampageAttackDamage(
         ResourceLocation.parse("born_in_chaos_v1:medium_rampage"),
         ResourceLocation.parse("born_in_chaos_v1:effect.medium_rampage_1"),
         (Double)EffectBalanceConfig.BORN_IN_CHAOS_MEDIUM_RAMPAGE_ATTACK_DAMAGE.get()
      );
      this.patchRampageAttackDamage(
         ResourceLocation.parse("born_in_chaos_v1:strong_rampage"),
         ResourceLocation.parse("born_in_chaos_v1:effect.strong_rampage_1"),
         (Double)EffectBalanceConfig.BORN_IN_CHAOS_STRONG_RAMPAGE_ATTACK_DAMAGE.get()
      );
      this.patchRampageAttackDamage(
         ResourceLocation.parse("born_in_chaos_v1:furious_rampage"),
         ResourceLocation.parse("born_in_chaos_v1:effect.furious_rampage_1"),
         (Double)EffectBalanceConfig.BORN_IN_CHAOS_FURIOUS_RAMPAGE_ATTACK_DAMAGE.get()
      );
      this.patchRampageAttackDamage(
         ResourceLocation.parse("born_in_chaos_v1:rampant_rampage"),
         ResourceLocation.parse("born_in_chaos_v1:effect.rampant_rampage_1"),
         (Double)EffectBalanceConfig.BORN_IN_CHAOS_RAMPANT_RAMPAGE_ATTACK_DAMAGE.get()
      );
   }

   private void patchRampageAttackDamage(ResourceLocation effectId, ResourceLocation modifierId, double attackDamage) {
      try {
         MobEffect effect = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(effectId);
         if (effect == null) {
            LOGGER.warn("Could not find effect in registry: {}", effectId);
            return;
         }

         Field attributeModifiersField = MobEffect.class.getDeclaredField("attributeModifiers");
         attributeModifiersField.setAccessible(true);
         Map<?, ?> modifiers = (Map<?, ?>)attributeModifiersField.get(effect);
         modifiers.remove(Attributes.ATTACK_DAMAGE);
         effect.addAttributeModifier(Attributes.ATTACK_DAMAGE, modifierId, attackDamage, Operation.ADD_VALUE);
         LOGGER.info("Patched {} attack damage to {}.", effectId, attackDamage);
      } catch (Exception var8) {
         LOGGER.error("Failed to patch {} attack damage", effectId, var8);
      }
   }

   private void patchRemoveMovementSpeedFromEffect(ResourceLocation effectId) {
      try {
         MobEffect effect = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(effectId);
         if (effect == null) {
            LOGGER.warn("Could not find effect in registry: {}", effectId);
            return;
         }

         Field attributeModifiersField = MobEffect.class.getDeclaredField("attributeModifiers");
         attributeModifiersField.setAccessible(true);
         Map<?, ?> modifiers = (Map<?, ?>)attributeModifiersField.get(effect);
         modifiers.remove(Attributes.MOVEMENT_SPEED);
         LOGGER.info("Removed MOVEMENT_SPEED from effect {}", effectId);
      } catch (Exception var5) {
         LOGGER.error("Failed to remove MOVEMENT_SPEED from effect {}", effectId, var5);
      }
   }
}
