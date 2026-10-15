package net.warphan.iss_magicfromtheeast.spells.spirit;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
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
import net.warphan.iss_magicfromtheeast.entity.spells.soul_skull.SoulSkullProjectile;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.spells.MFTESpellAnimations;

@AutoSpellConfig
public class SoulCatalystSpell extends AbstractSpell {
   private final ResourceLocation spellID = new ResourceLocation("iss_magicfromtheeast", "soul_catalyst");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(MFTESchoolRegistries.SPIRIT_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(60.0)
      .build();

   public SoulCatalystSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 2;
      this.spellPowerPerLevel = 1;
      this.castTime = 20;
      this.baseManaCost = 35;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster) / 2.0F, 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{2 + spellLevel / 2})
      );
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellID;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.SPIRIT_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.SOUL_CAST.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int projectileCount = 2 + spellLevel / 2;

      for (int i = 0; i < projectileCount; i++) {
         SoulSkullProjectile soulSkull = new SoulSkullProjectile(level, entity);
         soulSkull.setPos(entity.position().add(0.0, entity.getEyeHeight() - soulSkull.getBoundingBox().getYsize() * 0.5, 0.0));
         soulSkull.shoot(entity.getLookAngle(), 0.12F);
         soulSkull.setDamage(this.getSpellPower(spellLevel, entity) / 2.0F);
         level.addFreshEntity(soulSkull);
         super.onCast(level, spellLevel, entity, castSource, playerMagicData);
      }
   }

   public AnimationHolder getCastStartAnimation() {
      return MFTESpellAnimations.ANIMATION_CIRCLE_DRAW;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }
}
