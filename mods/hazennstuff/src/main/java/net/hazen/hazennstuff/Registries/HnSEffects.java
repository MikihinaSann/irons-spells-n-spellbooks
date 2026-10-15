package net.hazen.hazennstuff.Registries;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.hazen.hazennstuff.HazenNStuff;
import net.hazen.hazennstuff.Registries.Effects.DryadsBlessingEffect;
import net.hazen.hazennstuff.Registries.Effects.ElectrocutedEffect;
import net.hazen.hazennstuff.Registries.Effects.FlameKissedEffect;
import net.hazen.hazennstuff.Registries.Effects.GenericHazenEffect;
import net.hazen.hazennstuff.Registries.Effects.HarmonizedEffect;
import net.hazen.hazennstuff.Registries.Effects.IchorEffect;
import net.hazen.hazennstuff.Registries.Effects.InfestedEffect;
import net.hazen.hazennstuff.Registries.Effects.SporeEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HnSEffects {
   public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, "hazennstuff");
   public static final Holder<MobEffect> TYRANTS_GRACE_EFFECT = MOB_EFFECTS.register(
      "tyrants_grace",
      () -> new GenericHazenEffect(MobEffectCategory.BENEFICIAL, 16496449)
         .addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("hazennstuff", "tyrants_grace"), 1.0, Operation.ADD_VALUE)
         .addAttributeModifier(
            AttributeRegistry.CASTING_MOVESPEED, ResourceLocation.fromNamespaceAndPath("hazennstuff", "tyrants_grace"), 0.2F, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            AttributeRegistry.FIRE_SPELL_POWER, ResourceLocation.fromNamespaceAndPath("hazennstuff", "tyrants_grace"), 0.12F, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("hazennstuff", "tyrants_grace"), 1.0, Operation.ADD_VALUE)
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.ARROW_DAMAGE,
            ResourceLocation.fromNamespaceAndPath("hazennstuff", "tyrants_grace"),
            1.0,
            Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final Holder<MobEffect> FIREBLOSSOM_RULER_EFFECT = MOB_EFFECTS.register(
      "fireblossom_ruler",
      () -> new GenericHazenEffect(MobEffectCategory.BENEFICIAL, 16496449)
         .addAttributeModifier(
            AttributeRegistry.CASTING_MOVESPEED,
            ResourceLocation.fromNamespaceAndPath("hazennstuff", "fireblossom_ruler"),
            0.15F,
            Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            AttributeRegistry.SPELL_RESIST, ResourceLocation.fromNamespaceAndPath("hazennstuff", "fireblossom_ruler"), 0.1F, Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final Holder<MobEffect> FIREBLOSSOM_WARRIOR_EFFECT = MOB_EFFECTS.register(
      "fireblossom_warrior",
      () -> new GenericHazenEffect(MobEffectCategory.BENEFICIAL, 16496449)
         .addAttributeModifier(
            Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("hazennstuff", "fireblossom_warrior"), 2.0, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("hazennstuff", "fireblossom_warrior"), 2.0, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.PROT_SHRED,
            ResourceLocation.fromNamespaceAndPath("hazennstuff", "fireblossom_warrior"),
            0.15F,
            Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final Holder<MobEffect> SCORCH_BLOOMED_EFFECT = MOB_EFFECTS.register(
      "scorch_bloomed",
      () -> new GenericHazenEffect(MobEffectCategory.BENEFICIAL, 16496449)
         .addAttributeModifier(
            AttributeRegistry.CASTING_MOVESPEED, ResourceLocation.fromNamespaceAndPath("hazennstuff", "scorch_bloomed"), 0.15F, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            AttributeRegistry.SPELL_RESIST, ResourceLocation.fromNamespaceAndPath("hazennstuff", "scorch_bloomed"), 0.1F, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("hazennstuff", "scorch_bloomed"), 2.0, Operation.ADD_MULTIPLIED_BASE)
         .addAttributeModifier(
            Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("hazennstuff", "scorch_bloomed"), 2.0, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.PROT_SHRED,
            ResourceLocation.fromNamespaceAndPath("hazennstuff", "scorch_bloomed"),
            0.15F,
            Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final Holder<MobEffect> MAGE_SET_BONUS = MOB_EFFECTS.register(
      "mage_set_bonus",
      () -> new GenericHazenEffect(MobEffectCategory.BENEFICIAL, 16496449)
         .addAttributeModifier(
            AttributeRegistry.CASTING_MOVESPEED, ResourceLocation.fromNamespaceAndPath("hazennstuff", "mage_set_bonus"), 0.15F, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            AttributeRegistry.SPELL_RESIST, ResourceLocation.fromNamespaceAndPath("hazennstuff", "mage_set_bonus"), 0.1F, Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final Holder<MobEffect> SWORDMASTER_SET_BONUS = MOB_EFFECTS.register(
      "swordmaster_set_bonus",
      () -> new GenericHazenEffect(MobEffectCategory.BENEFICIAL, 16496449)
         .addAttributeModifier(
            Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("hazennstuff", "swordmaster_set_bonus"), 0.15F, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.PROT_SHRED,
            ResourceLocation.fromNamespaceAndPath("hazennstuff", "swordmaster_set_bonus"),
            0.1F,
            Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final Holder<MobEffect> ARCHER_SET_BONUS = MOB_EFFECTS.register(
      "archer_set_bonus",
      () -> new GenericHazenEffect(MobEffectCategory.BENEFICIAL, 16496449)
         .addAttributeModifier(
            Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("hazennstuff", "archer_set_bonus"), 0.15F, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.ARROW_DAMAGE,
            ResourceLocation.fromNamespaceAndPath("hazennstuff", "archer_set_bonus"),
            0.1F,
            Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.ARROW_VELOCITY,
            ResourceLocation.fromNamespaceAndPath("hazennstuff", "archer_set_bonus"),
            0.1F,
            Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final Holder<MobEffect> SUMMONER_SET_BONUS = MOB_EFFECTS.register(
      "summoner_set_bonus",
      () -> new GenericHazenEffect(MobEffectCategory.BENEFICIAL, 16496449)
         .addAttributeModifier(
            AttributeRegistry.SUMMON_DAMAGE, ResourceLocation.fromNamespaceAndPath("hazennstuff", "summoner_set_bonus"), 0.15F, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            ASAttributeRegistry.MANA_STEAL, ResourceLocation.fromNamespaceAndPath("hazennstuff", "summoner_set_bonus"), 0.1F, Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> FLAME_KISSED = MOB_EFFECTS.register(
      "flame_kissed",
      () -> new FlameKissedEffect(MobEffectCategory.BENEFICIAL, 3311322)
         .addAttributeModifier(AttributeRegistry.FIRE_SPELL_POWER, HazenNStuff.id("flame_kissed"), 0.03F, Operation.ADD_MULTIPLIED_TOTAL)
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.FIRE_DAMAGE, HazenNStuff.id("flame_kissed"), 0.03F, Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> HARMONIZED = MOB_EFFECTS.register(
      "harmonized",
      () -> new HarmonizedEffect(MobEffectCategory.BENEFICIAL, 3311322)
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.CRIT_DAMAGE, HazenNStuff.id("harmonized"), 0.05F, Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.CRIT_DAMAGE, HazenNStuff.id("harmonized"), 0.05F, Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> ICHOR = MOB_EFFECTS.register(
      "ichor",
      () -> new IchorEffect(MobEffectCategory.HARMFUL, 3311322)
         .addAttributeModifier(Attributes.ARMOR, HazenNStuff.id("ichor"), -0.1F, Operation.ADD_MULTIPLIED_TOTAL)
         .addAttributeModifier(AttributeRegistry.SPELL_RESIST, HazenNStuff.id("ichor"), -0.05F, Operation.ADD_MULTIPLIED_TOTAL)
         .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, HazenNStuff.id("ichor"), -0.1F, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static final DeferredHolder<MobEffect, MobEffect> DRYADS_BLESSING = MOB_EFFECTS.register(
      "dryads_blessing",
      () -> new DryadsBlessingEffect(MobEffectCategory.BENEFICIAL, 3311322)
         .addAttributeModifier(Attributes.ARMOR, HazenNStuff.id("dryads_blessing"), 0.05F, Operation.ADD_MULTIPLIED_TOTAL)
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.OVERHEAL, HazenNStuff.id("dryads_blessing"), 0.05F, Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.HEALING_RECEIVED,
            HazenNStuff.id("dryads_blessing"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final Holder<MobEffect> MANA_SICKNESS = MOB_EFFECTS.register(
      "mana_sickness",
      () -> new GenericHazenEffect(MobEffectCategory.HARMFUL, 16496449)
         .addAttributeModifier(
            AttributeRegistry.MAX_MANA, ResourceLocation.fromNamespaceAndPath("hazennstuff", "mana_sickness"), -0.25, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            AttributeRegistry.MANA_REGEN, ResourceLocation.fromNamespaceAndPath("hazennstuff", "mana_sickness"), -0.15F, Operation.ADD_MULTIPLIED_BASE
         )
         .addAttributeModifier(
            AttributeRegistry.SPELL_RESIST, ResourceLocation.fromNamespaceAndPath("hazennstuff", "mana_sickness"), -0.15F, Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> INFESTED = MOB_EFFECTS.register(
      "infested", () -> new InfestedEffect(MobEffectCategory.HARMFUL, 3026687)
   );
   public static final DeferredHolder<MobEffect, MobEffect> CHAOS_STATE = MOB_EFFECTS.register(
      "chaos_state", () -> new GenericHazenEffect(MobEffectCategory.HARMFUL, 3026687)
   );
   public static final DeferredHolder<MobEffect, MobEffect> SPORES = MOB_EFFECTS.register("spores", () -> new SporeEffect(MobEffectCategory.HARMFUL, 3026687));
   public static final DeferredHolder<MobEffect, MobEffect> ELECTROCUTED = MOB_EFFECTS.register(
      "electrocuted", () -> new ElectrocutedEffect(MobEffectCategory.HARMFUL, 3026687)
   );
   public static final DeferredHolder<MobEffect, MobEffect> HEXED = MOB_EFFECTS.register(
      "hexed", () -> new GenericHazenEffect(MobEffectCategory.HARMFUL, 3026687)
   );

   public static void register(IEventBus eventBus) {
      MOB_EFFECTS.register(eventBus);
   }
}
