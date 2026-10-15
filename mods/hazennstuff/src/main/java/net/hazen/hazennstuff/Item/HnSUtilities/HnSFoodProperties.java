package net.hazen.hazennstuff.Item.HnSUtilities;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;

public class HnSFoodProperties {
   public static final FoodProperties DIVINE_LEMON = new Builder()
      .nutrition(3)
      .saturationModifier(0.25F)
      .effect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400), 1.0F)
      .build();
   public static final FoodProperties LEMON = new Builder().nutrition(3).saturationModifier(0.25F).build();
   public static final FoodProperties FRIED_ICE_SPIDER_EGG = new Builder()
      .nutrition(8)
      .saturationModifier(4.0F)
      .effect(new MobEffectInstance(MobEffectRegistry.FROSTBITTEN_STRIKES, 1200), 1.0F)
      .build();
}
