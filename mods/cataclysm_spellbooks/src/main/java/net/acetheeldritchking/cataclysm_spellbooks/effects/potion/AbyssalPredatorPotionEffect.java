package net.acetheeldritchking.cataclysm_spellbooks.effects.potion;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.acetheeldritchking.cataclysm_spellbooks.CataclysmSpellbooks;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.common.NeoForgeMod;

public class AbyssalPredatorPotionEffect extends MagicMobEffect {
   public static final float SWIM_SPEED_PER_LEVEL = 0.25F;
   public static final float ATTACK_SPEED_PER_LEVEL = 0.05F;
   public static final float ATTACK_DAMAGE_PER_LEVEL = 0.05F;
   public static final float MOVEMENT_SPEED_BONUS_PER_LEVEL = 0.1F;
   public static final float ATTACK_DAMAGE_BONUS_PER_LEVEL = 0.1F;

   public AbyssalPredatorPotionEffect() {
      super(MobEffectCategory.BENEFICIAL, 5984177);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, CataclysmSpellbooks.id("abyssal_predator_effect"), 0.05F, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, CataclysmSpellbooks.id("abyssal_predator_effect"), 0.05F, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(NeoForgeMod.SWIM_SPEED, CataclysmSpellbooks.id("abyssal_predator_effect"), 0.25, Operation.ADD_MULTIPLIED_BASE);
   }

   public void removeAttributeModifiers(AttributeMap attributeMap) {
      super.removeAttributeModifiers(attributeMap);
   }

   public MobEffect addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double amount, Operation operation) {
      return super.addAttributeModifier(attribute, id, amount, operation);
   }
}
