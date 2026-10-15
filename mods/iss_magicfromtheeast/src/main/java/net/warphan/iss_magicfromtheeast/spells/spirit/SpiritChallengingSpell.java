package net.warphan.iss_magicfromtheeast.spells.spirit;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.warphan.iss_magicfromtheeast.entity.spells.spirit_challenging.ExtractedSoul;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.spells.MFTESpellAnimations;
import net.warphan.iss_magicfromtheeast.util.MFTETags;

@AutoSpellConfig
public class SpiritChallengingSpell extends AbstractSpell {
   private final ResourceLocation spellID = new ResourceLocation("iss_magicfromtheeast", "spirit_challenging");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(MFTESchoolRegistries.SPIRIT_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(120.0)
      .build();

   public SpiritChallengingSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 10;
      this.castTime = 20;
      this.baseManaCost = 65;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getConnectionRange(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.duration", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.percent_damage", new Object[]{Utils.stringTruncation(this.getBonusPercent(spellLevel, caster), 0)})
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
      return Optional.of((SoundEvent)MFTESoundRegistries.SPIRIT_EXTRACTING.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 8, 0.35F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityData) {
         LivingEntity challengedEntity = targetEntityData.getTarget((ServerLevel)level);
         if (challengedEntity != null) {
            if (challengedEntity.getType().is(MFTETags.SPIRIT_CHALLENGING_IMMUNE)) {
               if (entity instanceof ServerPlayer serverPlayer) {
                  serverPlayer.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable("ui.iss_magicfromtheeast.soul_extracting_false").withStyle(ChatFormatting.RED)
                        )
                     );
               }
            } else {
               ExtractedSoul extractedSoul = new ExtractedSoul(level, challengedEntity, entity);
               extractedSoul.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(challengedEntity.getHealth());
               extractedSoul.setHealth(extractedSoul.getMaxHealth());
               extractedSoul.setDuration(this.getDuration(spellLevel, entity));
               extractedSoul.setBonusPercent(this.getBonusPercent(spellLevel, entity));
               extractedSoul.setPos(entity.position().add(entity.getViewVector(5.0F)));
               extractedSoul.enableRadius();
               TargetedAreaEntity visualEntity = TargetedAreaEntity.createTargetAreaEntity(level, challengedEntity.position(), 12.0F, 65535);
               visualEntity.setDuration(extractedSoul.duration);
               visualEntity.setOwner(extractedSoul);
               visualEntity.setShouldFade(true);
               level.addFreshEntity(extractedSoul);
            }
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getConnectionRange(int spellLevel, LivingEntity caster) {
      return 12.0F;
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return 160 + 40 * spellLevel;
   }

   private int getBonusPercent(int spellLevel, LivingEntity caster) {
      int bonusAmount = (int)this.getSpellPower(spellLevel, caster);
      return Math.min(bonusAmount, 80);
   }

   public AnimationHolder getCastStartAnimation() {
      return MFTESpellAnimations.ANIMATION_SOUL_EXTRACT;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }
}
