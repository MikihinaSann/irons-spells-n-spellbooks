package net.warphan.iss_magicfromtheeast.spells.symmetry;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.warphan.iss_magicfromtheeast.entity.spells.dragon_glide.JadeLoong;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.spells.MFTESpellAnimations;

@AutoSpellConfig
public class DragonGlideSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("iss_magicfromtheeast", "dragon_glide");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(5.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}));
   }

   public DragonGlideSpell() {
      this.manaCostPerLevel = 4;
      this.baseSpellPower = 2;
      this.spellPowerPerLevel = 2;
      this.castTime = 30;
      this.baseManaCost = 34;
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
      return Optional.of((SoundEvent)MFTESoundRegistries.SYMMETRY_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.LOONG_CAST.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      JadeLoong loong = new JadeLoong(level, entity);
      loong.setPos(entity.position().add(0.0, entity.getEyeHeight() - loong.getBoundingBox().getYsize() * 0.5, 0.0).add(entity.getForward()));
      loong.shoot(entity.getLookAngle());
      loong.setDamage(this.getSpellPower(spellLevel, entity));
      level.addFreshEntity(loong);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public AnimationHolder getCastStartAnimation() {
      return MFTESpellAnimations.ANIMATION_LOONG_CAST;
   }
}
