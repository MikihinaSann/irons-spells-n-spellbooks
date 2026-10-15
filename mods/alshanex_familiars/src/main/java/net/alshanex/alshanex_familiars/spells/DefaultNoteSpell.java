package net.alshanex.alshanex_familiars.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.entity.sound.BlackNoteEntity;
import net.alshanex.alshanex_familiars.entity.sound.DefaultNoteEntity;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
import net.alshanex.familiarslib.registry.FSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DefaultNoteSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "default_note");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(FSchoolRegistry.SOUND_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(30.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.blast_count", new Object[]{this.getRecastCount(spellLevel, caster)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{"25s"})
      );
   }

   public DefaultNoteSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 90;
   }

   public boolean allowLooting() {
      return false;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)AFSoundRegistry.HARP_WAVE.get());
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2 + spellLevel;
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 80, castSource, null), playerMagicData);
      }

      int remainingCasts = playerMagicData.getPlayerRecasts().getRemainingRecastsForSpell(this);
      if (remainingCasts == 1) {
         BlackNoteEntity blackNote = new BlackNoteEntity(world, entity);
         blackNote.setPos(entity.position().add(0.0, entity.getEyeHeight() - blackNote.getBoundingBox().getYsize() * 0.5, 0.0));
         blackNote.setRotation(entity.getXRot(), entity.getYRot());
         blackNote.setDamage(this.getDamage(spellLevel, entity));
         blackNote.shoot(entity.getLookAngle());
         world.addFreshEntity(blackNote);
      } else if (remainingCasts > 1) {
         DefaultNoteEntity defaultNote = new DefaultNoteEntity(world, entity);
         defaultNote.setPos(entity.position().add(0.0, entity.getEyeHeight() - defaultNote.getBoundingBox().getYsize() * 0.5, 0.0));
         defaultNote.setRotation(entity.getXRot(), entity.getYRot());
         defaultNote.setDamage(this.getDamage(spellLevel, entity));
         defaultNote.shoot(entity.getLookAngle());
         world.addFreshEntity(defaultNote);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SLASH_ANIMATION;
   }
}
