package com.somake.somakespells.spells.ice;

import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.util.ElementalChargeActivation;
import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;

public class PermafrostSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "permafrost");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(40.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getSpellPower(spellLevel, caster) * 20.0F, 1)}),
         Component.translatable(
            "attribute.modifier.plus.1",
            new Object[]{
               Utils.stringTruncation(this.getPercentSpellPower(spellLevel, caster), 0), Component.translatable("attribute.irons_spellbooks.ice_spell_power")
            }
         ),
         Component.translatable(
            "attribute.modifier.plus.0",
            new Object[]{
               Utils.stringTruncation(this.getColdDamage(spellLevel, caster), 0),
               Component.translatable(((Attribute)Attributes.COLD_DAMAGE.value()).getDescriptionId())
            }
         )
      );
   }

   public PermafrostSpell() {
      this.manaCostPerLevel = 25;
      this.baseSpellPower = 30;
      this.spellPowerPerLevel = 8;
      this.castTime = 0;
      this.baseManaCost = 50;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      ElementalChargeActivation.activate(level, this, spellLevel, entity, ModMobEffects.PERMAFROST, 8101293);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getPercentSpellPower(int spellLevel, LivingEntity entity) {
      return spellLevel * 0.05F * 100.0F;
   }

   private float getColdDamage(int spellLevel, LivingEntity entity) {
      return spellLevel * 5.0F;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
