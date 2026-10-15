package net.alshanex.familiarslib.registry;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;

public class FoodRegistry {
   public static final FoodProperties BLUEBERRY = new Builder().nutrition(2).saturationModifier(0.2F).fast().build();
}
