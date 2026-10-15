package com.somake.somakespells.effect.geo;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import java.util.Optional;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EarthboundEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float GEO_SPELL_POWER_PER_LEVEL = 0.05F;
   public static final float ARMOR_PER_LEVEL = 2.0F;

   public EarthboundEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      Optional<Reference<Attribute>> geoPower = BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("gtbcs_geomancy_plus:geo_spell_power"));
      geoPower.ifPresent(
         attributeReference -> this.addAttributeModifier(
            attributeReference, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.earthbound.geo_power"), 0.05F, Operation.ADD_MULTIPLIED_TOTAL
         )
      );
      this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.earthbound.armor"), 2.0, Operation.ADD_VALUE);
   }
}
