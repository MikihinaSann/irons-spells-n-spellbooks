package net.alshanex.tunes_n_tomes.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.alshanex.tunes_n_tomes.util.SoundMarkerManager;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DalSegnoSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "dal_segno");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(TSchoolRegistry.MELODY_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(3.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of();
   }

   public DalSegnoSpell() {
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 10;
      this.baseManaCost = 70;
      this.manaCostPerLevel = 5;
      this.castTime = 0;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)TSoundRegistry.DAL_SEGNO.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (entity instanceof ServerPlayer player) {
         Vec3 closestMarker = SoundMarkerManager.getClosestMarkerPosition(player);
         return closestMarker != null;
      } else {
         return false;
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (entity instanceof ServerPlayer player) {
         Vec3 closestMarker = SoundMarkerManager.getClosestMarkerPosition(player);
         if (closestMarker != null) {
            if (entity.isPassenger()) {
               entity.stopRiding();
            }

            Utils.handleSpellTeleport(this, entity, closestMarker);
            entity.resetFallDistance();
            playerMagicData.resetAdditionalCastData();
            entity.playSound(this.getCastFinishSound().get(), 2.0F, 1.0F);
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public AnimationHolder getCastStartAnimation() {
      return AnimationHolder.none();
   }
}
