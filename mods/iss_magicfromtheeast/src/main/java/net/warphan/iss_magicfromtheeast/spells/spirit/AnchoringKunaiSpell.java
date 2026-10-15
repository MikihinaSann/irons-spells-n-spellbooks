package net.warphan.iss_magicfromtheeast.spells.spirit;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.warphan.iss_magicfromtheeast.entity.spells.anchoring_kunai.AnchoringKunaiProjectile;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;

@AutoSpellConfig
public class AnchoringKunaiSpell extends AbstractSpell {
   private final ResourceLocation spellID = new ResourceLocation("iss_magicfromtheeast", "anchoring_kunai");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(MFTESchoolRegistries.SPIRIT_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(90.0)
      .build();

   public AnchoringKunaiSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 20;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(20 * (spellLevel * 5 + 10), 1)}),
         Component.translatable("ui.irons_spellbooks.percent_damage", new Object[]{Utils.stringTruncation((spellLevel + 1) * 5, 0)})
      );
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.MAGIC_PROJECTILE_THROW.get());
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity caster) {
      return 3;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 100, castSource, null), playerMagicData);
      }

      AnchoringKunaiProjectile kunaiProjectile = new AnchoringKunaiProjectile(level, entity);
      kunaiProjectile.setEffectLevel(spellLevel);
      kunaiProjectile.setEffectTick(spellLevel * 5 + 10);
      kunaiProjectile.setDamage(this.getSpellPower(spellLevel, entity));
      kunaiProjectile.setPos(entity.position().add(0.0, entity.getEyeHeight() - kunaiProjectile.getBoundingBox().getYsize() * 0.5, 0.0));
      kunaiProjectile.shoot(entity.getLookAngle());
      level.addFreshEntity(kunaiProjectile);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
