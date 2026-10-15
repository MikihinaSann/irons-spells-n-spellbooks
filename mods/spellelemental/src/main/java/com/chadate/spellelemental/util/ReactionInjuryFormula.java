package com.chadate.spellelemental.util;

public class ReactionInjuryFormula {
   public static float QuickenReactionDamage(float originalDamage, float reactionMultiplier, float astralBlessing) {
      return originalDamage * reactionMultiplier * (1.0F + 5.0F * astralBlessing / (astralBlessing + 1200.0F));
   }

   public static float CalculateOverloadDamage(float elementsAmount, float reactionMultiplier, float astralBlessing) {
      return elementsAmount * 0.1F * reactionMultiplier * (1.0F + 16.0F * astralBlessing / (astralBlessing + 2000.0F));
   }

   public static float AmplifiedReactionBonus(float astralBlessing) {
      return (float)(1.0 + 2.78 * astralBlessing / (astralBlessing + 1400.0F));
   }
}
