package net.warphan.iss_magicfromtheeast.spells.spirit;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.warphan.iss_magicfromtheeast.entity.spells.splitting_shuriken.SplittingShurikenProjectile;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;

@AutoSpellConfig
public class SplittingShurikenSpell extends AbstractSpell {
   private final ResourceLocation spellID = new ResourceLocation("iss_magicfromtheeast", "splitting_shuriken");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(MFTESchoolRegistries.SPIRIT_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(3.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}),
         Component.translatable("ui.iss_magicfromtheeast.split_projectile_count", new Object[]{Utils.stringTruncation(spellLevel + 2, 1)})
      );
   }

   public SplittingShurikenSpell() {
      this.manaCostPerLevel = 3;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 15;
   }

   public ResourceLocation getSpellResource() {
      return this.spellID;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.MAGIC_PROJECTILE_THROW.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      SplittingShurikenProjectile shuriken = new SplittingShurikenProjectile(level, entity);
      shuriken.setIsPrimary();
      shuriken.setSplitCount(spellLevel + 2);
      shuriken.setDamage(this.getSpellPower(spellLevel, entity));
      shuriken.setPos(entity.position().add(0.0, entity.getEyeHeight() - shuriken.getBoundingBox().getYsize() * 0.5, 0.0));
      shuriken.shoot(entity.getLookAngle());
      level.addFreshEntity(shuriken);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
