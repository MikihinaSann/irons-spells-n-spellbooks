package com.chadate.spellelemental.integration.jei.recipe;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class ElementReactionRecipe {
   private final String reactionId;
   private final String reactionName;
   private final String element1;
   private final String element2;
   private final boolean isDirectional;
   private final String triggerType;
   private final List<ElementReactionRecipe.EffectInfo> effects;
   private final String description;

   public ElementReactionRecipe(
      String reactionId,
      String reactionName,
      String element1,
      String element2,
      boolean isDirectional,
      String triggerType,
      List<ElementReactionRecipe.EffectInfo> effects,
      String description
   ) {
      this.reactionId = reactionId;
      this.reactionName = reactionName;
      this.element1 = element1;
      this.element2 = element2;
      this.isDirectional = isDirectional;
      this.triggerType = triggerType;
      this.effects = effects;
      this.description = description;
   }

   public String getReactionId() {
      return this.reactionId;
   }

   public String getReactionName() {
      return this.reactionName;
   }

   public String getElement1() {
      return this.element1;
   }

   public String getElement2() {
      return this.element2;
   }

   public boolean isDirectional() {
      return this.isDirectional;
   }

   public String getTriggerType() {
      return this.triggerType;
   }

   public List<ElementReactionRecipe.EffectInfo> getEffects() {
      return this.effects;
   }

   public String getDescription() {
      return this.description;
   }

   public ResourceLocation getElement1Icon() {
      String iconElement = this.element1.equalsIgnoreCase("quicken") ? "lightning" : this.element1.toLowerCase();
      return ResourceLocation.fromNamespaceAndPath("spellelemental", "textures/elements/" + iconElement + "_element.png");
   }

   public ResourceLocation getElement2Icon() {
      String iconElement = this.element2.equalsIgnoreCase("quicken") ? "lightning" : this.element2.toLowerCase();
      return ResourceLocation.fromNamespaceAndPath("spellelemental", "textures/elements/" + iconElement + "_element.png");
   }

   public static class EffectInfo {
      private final String type;
      private final String value;
      private final String description;

      public EffectInfo(String type, String value, String description) {
         this.type = type;
         this.value = value;
         this.description = description;
      }

      public String getType() {
         return this.type;
      }

      public String getValue() {
         return this.value;
      }

      public String getDescription() {
         return this.description;
      }
   }
}
