package com.somake.somakespells.effect.holy;

import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class BlessedEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float HOLY_SPELL_POWER_PER_LEVEL = 0.05F;
   public static final float HEALING_RECEIVED_PER_LEVEL = 0.15F;

   public BlessedEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(
         Attributes.HEALING_RECEIVED, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.blessed.healing_receive"), 0.15F, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         AttributeRegistry.BLOOD_MAGIC_RESIST, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.blessed.blood_resist"), -0.05, Operation.ADD_VALUE
      );
   }
}
