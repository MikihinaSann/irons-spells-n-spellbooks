package com.somake.somakespells.effect.ender;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EnderCorruptionEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float ENDER_SPELL_POWER_PER_LEVEL = 0.05F;
   public static final float MAX_MANA_PER_LEVEL = 50.0F;

   public EnderCorruptionEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(
         AttributeRegistry.MAX_MANA, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.ender_corruption.max_mana"), 50.0, Operation.ADD_VALUE
      );
   }
}
