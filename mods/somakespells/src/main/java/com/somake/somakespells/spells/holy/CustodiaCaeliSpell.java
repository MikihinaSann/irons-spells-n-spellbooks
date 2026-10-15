package com.somake.somakespells.spells.holy;

import com.somake.somakespells.config.Config;
import com.somake.somakespells.entity.CustodiaCaeliSigilEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.RaycastBuilder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CustodiaCaeliSpell extends AbstractSpell {
   private static final float CAST_RANGE = 32.0F;
   private static final float SIGIL_RADIUS = 6.0F;
   private static final int SIGIL_DURATION_TICKS = 360;
   private static final int BLESSING_DURATION_BASE_TICKS = 400;
   private static final int BLESSING_DURATION_PER_LEVEL_TICKS = 200;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "custodia_caeli");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(180.0)
      .build();

   public CustodiaCaeliSpell() {
      this.baseManaCost = 150;
      this.manaCostPerLevel = 0;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 0;
      this.castTime = 45;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.BEACON_POWER_SELECT);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.AMETHYST_BLOCK_RESONATE);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getBlessingDuration(spellLevel), 1)}),
         Component.translatable(
            "attribute.modifier.plus.1",
            new Object[]{
               Utils.stringTruncation((float)this.getMaxHealthBonusPercent(spellLevel), 0),
               Component.translatable(((Attribute)Attributes.MAX_HEALTH.value()).getDescriptionId())
            }
         ),
         Component.translatable(
            "attribute.modifier.plus.0",
            new Object[]{
               Utils.stringTruncation((float)this.getArmorBonus(spellLevel), 0),
               Component.translatable(((Attribute)Attributes.ARMOR.value()).getDescriptionId())
            }
         ),
         Component.translatable(
            "attribute.modifier.plus.0",
            new Object[]{
               Utils.stringTruncation((float)this.getArmorToughnessBonus(spellLevel), 1),
               Component.translatable(((Attribute)Attributes.ARMOR_TOUGHNESS.value()).getDescriptionId())
            }
         )
      );
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
      if (playerMagicData != null) {
         Vec3 targetPos = this.getTargetedSigilPosition(level, entity);
         TargetedAreaEntity targetArea = TargetedAreaEntity.createTargetAreaEntity(level, targetPos, 6.0F, 16773288);
         targetArea.setShouldFade(true);
         targetArea.setDuration(this.castTime + 8);
         playerMagicData.setAdditionalCastData(new TargetAreaCastData(targetPos, targetArea));
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 targetPos = playerMagicData.getAdditionalCastData() instanceof TargetAreaCastData castData
         ? castData.getCenter()
         : this.getTargetedSigilPosition(level, entity);
      CustodiaCaeliSigilEntity custodiaCaeliSigil = new CustodiaCaeliSigilEntity(
         level, targetPos.x, targetPos.y, targetPos.z, entity, 6.0F, 360, this.getBlessingDuration(spellLevel), spellLevel - 1
      );
      level.addFreshEntity(custodiaCaeliSigil);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getBlessingDuration(int spellLevel) {
      return 400 + (spellLevel - 1) * 200;
   }

   private double getMaxHealthBonusPercent(int spellLevel) {
      return 20.0;
   }

   private double getArmorBonus(int spellLevel) {
      return 1.0 * spellLevel;
   }

   private double getArmorToughnessBonus(int spellLevel) {
      return 0.5 * spellLevel;
   }

   private Vec3 getTargetedSigilPosition(Level level, LivingEntity caster) {
      Vec3 hitLocation = RaycastBuilder.begin(level, caster).range(32.0F).checkForBlocks(true).bbInflation(0.2F).build().getLocation();
      return Utils.moveToRelativeGroundLevel(level, hitLocation, 3, 6).add(0.0, 0.05, 0.0);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   public boolean allowLooting() {
      return !(Boolean)Config.DISABLE_CUSTODIA_CAELI_CRAFTING_AND_LOOTING.get();
   }

   public boolean allowCrafting() {
      return !(Boolean)Config.DISABLE_CUSTODIA_CAELI_CRAFTING_AND_LOOTING.get();
   }
}
