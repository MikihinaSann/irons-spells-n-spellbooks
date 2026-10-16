package com.userofbricks.expanded_combat.item.recipes.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.userofbricks.expanded_combat.ExpandedCombat;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import org.jetbrains.annotations.NotNull;

public class ECConfigBooleanCondition implements ICondition {
   public static MapCodec<ECConfigBooleanCondition> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(Codec.STRING.fieldOf("config_entry").forGetter(ECConfigBooleanCondition::getConfigEntry))
         .apply(builder, ECConfigBooleanCondition::new)
   );
   private final String configBooleanName;

   public ECConfigBooleanCondition(String configBooleanName) {
      this.configBooleanName = configBooleanName;
   }

   public boolean test(@NotNull IContext context) {
      String var2 = this.configBooleanName;

      return switch (var2) {
         case "gauntlet" -> ExpandedCombat.CONFIG.enableGauntlets;
         case "shield" -> ExpandedCombat.CONFIG.enableShields;
         case "bow" -> ExpandedCombat.CONFIG.enableBows;
         case "crossbow" -> ExpandedCombat.CONFIG.enableCrossbows;
         case "quiver" -> ExpandedCombat.CONFIG.enableQuivers;
         case "arrow" -> ExpandedCombat.CONFIG.enableArrows;
         case "weapon" -> ExpandedCombat.CONFIG.enableWeapons;
         case "soul" -> ExpandedCombat.CONFIG.enableSouls;
         default -> false;
      };
   }

   @NotNull
   public MapCodec<? extends ICondition> codec() {
      return CODEC;
   }

   public String getConfigEntry() {
      return this.configBooleanName;
   }

   @Override
   public String toString() {
      return "ec:config_boolean(\"" + this.configBooleanName + "\")";
   }
}
