package com.grandsigil.registry;

import com.grandsigil.GrandSigil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class GsDamage {
   public static final ResourceKey<DamageType> GRAND_SIGIL = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("grand_sigil"));
   public static final ResourceKey<DamageType> SUNFALL = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("sunfall"));
   public static final ResourceKey<DamageType> SKYBREAK = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("skybreak"));
   public static final ResourceKey<DamageType> WORLD_TREE = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("world_tree"));
   public static final ResourceKey<DamageType> HEAVEN_GATE = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("heaven_gate"));
   public static final ResourceKey<DamageType> GLACIATION = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("glaciation"));
   public static final ResourceKey<DamageType> GAZE = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("gaze"));
   public static final ResourceKey<DamageType> COLOSSEUM = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("colosseum"));
   public static final ResourceKey<DamageType> VOIDGRIP = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("voidgrip"));
   public static final ResourceKey<DamageType> RECKONING = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("reckoning"));
   public static final ResourceKey<DamageType> BLACK_COFFIN = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("black_coffin"));
   public static final ResourceKey<DamageType> THOUSAND_BLADES = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("thousand_blades"));
   public static final ResourceKey<DamageType> PHOENIX = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("phoenix"));
   public static final ResourceKey<DamageType> THUNDER_DRAGON = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("thunder_dragon"));
   public static final ResourceKey<DamageType> FULL_BLOOM = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("full_bloom"));
   public static final ResourceKey<DamageType> ICE_AGE = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("ice_age"));
   public static final ResourceKey<DamageType> CRIMSON_THREADS = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("crimson_threads"));
   public static final ResourceKey<DamageType> DIMENSION_CUT = ResourceKey.create(Registries.DAMAGE_TYPE, GrandSigil.id("dimension_cut"));

   public static DamageSource column(Level level, Entity caster) {
      return of(level, GRAND_SIGIL, caster);
   }

   public static DamageSource sunfall(Level level, Entity caster) {
      return of(level, SUNFALL, caster);
   }

   public static DamageSource skybreak(Level level, Entity caster) {
      return of(level, SKYBREAK, caster);
   }

   public static DamageSource worldTree(Level level, Entity caster) {
      return of(level, WORLD_TREE, caster);
   }

   public static DamageSource heavenGate(Level level, Entity caster) {
      return of(level, HEAVEN_GATE, caster);
   }

   public static DamageSource glaciation(Level level, Entity caster) {
      return of(level, GLACIATION, caster);
   }

   public static DamageSource gaze(Level level, Entity caster) {
      return of(level, GAZE, caster);
   }

   public static DamageSource colosseum(Level level, Entity caster) {
      return of(level, COLOSSEUM, caster);
   }

   public static DamageSource voidgrip(Level level, Entity caster) {
      return of(level, VOIDGRIP, caster);
   }

   public static DamageSource reckoning(Level level, Entity caster) {
      return of(level, RECKONING, caster);
   }

   public static DamageSource blackCoffin(Level level, Entity caster) {
      return of(level, BLACK_COFFIN, caster);
   }

   public static DamageSource thousandBlades(Level level, Entity caster) {
      return of(level, THOUSAND_BLADES, caster);
   }

   public static DamageSource phoenix(Level level, Entity caster) {
      return of(level, PHOENIX, caster);
   }

   public static DamageSource thunderDragon(Level level, Entity caster) {
      return of(level, THUNDER_DRAGON, caster);
   }

   public static DamageSource fullBloom(Level level, Entity caster) {
      return of(level, FULL_BLOOM, caster);
   }

   public static DamageSource iceAge(Level level, Entity caster) {
      return of(level, ICE_AGE, caster);
   }

   public static DamageSource crimsonThreads(Level level, Entity caster) {
      return of(level, CRIMSON_THREADS, caster);
   }

   public static DamageSource dimensionCut(Level level, Entity caster) {
      return of(level, DIMENSION_CUT, caster);
   }

   private static DamageSource of(Level level, ResourceKey<DamageType> key, Entity caster) {
      Holder<DamageType> type = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
      return caster == null ? new DamageSource(type) : new DamageSource(type, caster);
   }

   private GsDamage() {
   }
}
