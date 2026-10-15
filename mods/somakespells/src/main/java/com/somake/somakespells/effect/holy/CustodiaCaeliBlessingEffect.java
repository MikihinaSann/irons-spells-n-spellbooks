package com.somake.somakespells.effect.holy;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CustodiaCaeliBlessingEffect extends MagicMobEffect implements ISyncedMobEffect {
   private static final ResourceLocation MAX_HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "effect.custodia_caeli_blessing.max_health"
   );
   public static final double MAX_HEALTH_BONUS = 0.2;
   public static final double ARMOR_BONUS = 1.0;
   public static final double ARMOR_TOUGHNESS_BONUS = 0.5;

   public CustodiaCaeliBlessingEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(Attributes.MAX_HEALTH, MAX_HEALTH_MODIFIER_ID, 0.2, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(
         Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.custodia_caeli_blessing.armor"), 1.0, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         Attributes.ARMOR_TOUGHNESS,
         ResourceLocation.fromNamespaceAndPath("somakespells", "effect.custodia_caeli_blessing.armor_toughness"),
         0.5,
         Operation.ADD_VALUE
      );
   }
}
