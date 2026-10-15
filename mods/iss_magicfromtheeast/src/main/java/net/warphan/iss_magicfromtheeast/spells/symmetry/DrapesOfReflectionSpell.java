package net.warphan.iss_magicfromtheeast.spells.symmetry;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.entity.spells.jade_drape.JadeDrapesEntity;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;

@AutoSpellConfig
public class DrapesOfReflectionSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("iss_magicfromtheeast", "drapes_of_reflection");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(30.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.hp", new Object[]{Utils.stringTruncation(this.getDrapesHP(spellLevel, caster), 1)}),
         Component.translatable(
            "ui.iss_magicfromtheeast.reflect_damage_percent",
            new Object[]{Utils.stringTruncation(this.getDrapesReflectionPercent(spellLevel, caster) * 100.0F, 1)}
         )
      );
   }

   public DrapesOfReflectionSpell() {
      this.manaCostPerLevel = 6;
      this.baseSpellPower = 2;
      this.spellPowerPerLevel = 1;
      this.baseManaCost = 35;
      this.castTime = 0;
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
      JadeDrapesEntity drapes = new JadeDrapesEntity(level, this.getDrapesHP(spellLevel, entity), entity, true);
      Vec3 spawn = Utils.raycastForEntity(level, entity, 3.0F, true).getLocation();
      drapes.setPos(spawn);
      drapes.setRotation(entity.getYRot());
      drapes.setPercentReflectDamage(this.getDrapesReflectionPercent(spellLevel, entity));
      level.addFreshEntity(drapes);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDrapesHP(int spellLevel, LivingEntity caster) {
      return 1.0F + this.getSpellPower(spellLevel, caster);
   }

   private float getDrapesReflectionPercent(int spellLevel, LivingEntity caster) {
      return (10 + spellLevel * 10) / 100.0F;
   }
}
