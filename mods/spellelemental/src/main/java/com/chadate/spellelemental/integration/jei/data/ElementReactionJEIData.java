package com.chadate.spellelemental.integration.jei.data;

import com.chadate.spellelemental.integration.jei.recipe.ElementReactionRecipe;
import java.util.ArrayList;
import java.util.List;

public class ElementReactionJEIData {
   public static List<ElementReactionRecipe> generateReactionRecipes() {
      List<ElementReactionRecipe> recipes = new ArrayList<>();
      recipes.add(createMeltRecipe());
      recipes.add(createOverloadRecipe());
      recipes.add(createSuperconductRecipe());
      recipes.add(createQuickenRecipe());
      recipes.add(createAggravateRecipe());
      recipes.add(createSpreadRecipe());
      recipes.add(createCoagulationRecipe());
      recipes.add(createBurningRecipe());
      recipes.add(createBoilingRecipe());
      return recipes;
   }

   private static ElementReactionRecipe createMeltRecipe() {
      List<ElementReactionRecipe.EffectInfo> effects = new ArrayList<>();
      effects.add(new ElementReactionRecipe.EffectInfo("damage_amplify", "2.0x / 1.5x", "jei.spellelemental.melt.effect.damage"));
      effects.add(new ElementReactionRecipe.EffectInfo("formula", "AmplifiedReactionBonus", "jei.spellelemental.melt.effect.astral"));
      return new ElementReactionRecipe("melt", "Melt / 融化", "fire", "ice", true, "damage", effects, "");
   }

   private static ElementReactionRecipe createOverloadRecipe() {
      List<ElementReactionRecipe.EffectInfo> effects = new ArrayList<>();
      effects.add(new ElementReactionRecipe.EffectInfo("aoe", "1.5 blocks", "jei.spellelemental.overload.effect.aoe"));
      effects.add(new ElementReactionRecipe.EffectInfo("formula", "CalculateOverloadDamage", "jei.spellelemental.overload.effect.formula"));
      return new ElementReactionRecipe("overload", "Overload / 超载", "fire", "lightning", false, "damage", effects, "");
   }

   private static ElementReactionRecipe createSuperconductRecipe() {
      List<ElementReactionRecipe.EffectInfo> effects = new ArrayList<>();
      effects.add(new ElementReactionRecipe.EffectInfo("aoe", "1.5 blocks", "jei.spellelemental.superconduct.effect.aoe"));
      effects.add(new ElementReactionRecipe.EffectInfo("debuff", "-40% Physical Resist", "jei.spellelemental.superconduct.effect.debuff"));
      return new ElementReactionRecipe("superconduct", "Superconduct / 超导", "ice", "lightning", false, "damage", effects, "");
   }

   private static ElementReactionRecipe createQuickenRecipe() {
      List<ElementReactionRecipe.EffectInfo> effects = new ArrayList<>();
      effects.add(new ElementReactionRecipe.EffectInfo("transformation", "Quicken Element", "jei.spellelemental.quicken.effect.transform"));
      return new ElementReactionRecipe("quicken", "Quicken / 激化", "lightning", "nature", false, "damage", effects, "");
   }

   private static ElementReactionRecipe createAggravateRecipe() {
      List<ElementReactionRecipe.EffectInfo> effects = new ArrayList<>();
      effects.add(new ElementReactionRecipe.EffectInfo("damage_amplify", "1.15x", "jei.spellelemental.aggravate.effect.damage"));
      effects.add(new ElementReactionRecipe.EffectInfo("repeatable", "Non-consuming", "jei.spellelemental.aggravate.effect.repeatable"));
      return new ElementReactionRecipe("aggravate", "Aggravate / 超激化", "lightning", "nature", true, "damage", effects, "");
   }

   private static ElementReactionRecipe createSpreadRecipe() {
      List<ElementReactionRecipe.EffectInfo> effects = new ArrayList<>();
      effects.add(new ElementReactionRecipe.EffectInfo("damage_amplify", "1.25x", "jei.spellelemental.spread.effect.damage"));
      effects.add(new ElementReactionRecipe.EffectInfo("repeatable", "Non-consuming", "jei.spellelemental.spread.effect.repeatable"));
      return new ElementReactionRecipe("spread", "Spread / 蔓延", "nature", "quicken", true, "damage", effects, "");
   }

   private static ElementReactionRecipe createCoagulationRecipe() {
      List<ElementReactionRecipe.EffectInfo> effects = new ArrayList<>();
      effects.add(new ElementReactionRecipe.EffectInfo("damage_amplify", "1.25x", "jei.spellelemental.coagulation.effect.damage"));
      effects.add(new ElementReactionRecipe.EffectInfo("debuff", "-50% Healing", "jei.spellelemental.coagulation.effect.debuff"));
      return new ElementReactionRecipe("coagulation", "Coagulation / 凝血", "blood", "ice", false, "damage", effects, "");
   }

   private static ElementReactionRecipe createBurningRecipe() {
      List<ElementReactionRecipe.EffectInfo> effects = new ArrayList<>();
      effects.add(new ElementReactionRecipe.EffectInfo("dot", "Every 3 ticks", "jei.spellelemental.burning.effect.dot"));
      effects.add(new ElementReactionRecipe.EffectInfo("attachment", "Fire Element", "jei.spellelemental.burning.effect.attachment"));
      return new ElementReactionRecipe("burning", "Burning / 燃烧", "fire", "nature", false, "tick", effects, "");
   }

   private static ElementReactionRecipe createBoilingRecipe() {
      List<ElementReactionRecipe.EffectInfo> effects = new ArrayList<>();
      effects.add(new ElementReactionRecipe.EffectInfo("dot", "Every 5 ticks", "jei.spellelemental.boiling.effect.dot"));
      effects.add(new ElementReactionRecipe.EffectInfo("debuff", "Weakness I", "jei.spellelemental.boiling.effect.debuff"));
      effects.add(new ElementReactionRecipe.EffectInfo("attachment", "Blood Element", "jei.spellelemental.boiling.effect.attachment"));
      return new ElementReactionRecipe("boiling", "Boiling / 沸腾", "fire", "blood", false, "tick", effects, "");
   }
}
