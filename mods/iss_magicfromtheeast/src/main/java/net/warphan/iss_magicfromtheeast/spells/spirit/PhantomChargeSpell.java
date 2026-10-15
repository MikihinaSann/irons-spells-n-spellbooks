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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.entity.spells.phantom_cavalry.PhantomCavalryVisualEntity;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;

@AutoSpellConfig
public class PhantomChargeSpell extends AbstractSpell {
   private final ResourceLocation spellID = new ResourceLocation("iss_magicfromtheeast", "phantom_charge");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(MFTESchoolRegistries.SPIRIT_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(80.0)
      .build();

   public PhantomChargeSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 45;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster) / 2.0F, 1)}),
         Component.translatable("ui.iss_magicfromtheeast.row_length", new Object[]{Utils.stringTruncation(spellLevel, 1)})
      );
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellID;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.SKELETON_HORSE_DEATH);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 orth = new Vec3(
         -Mth.cos(-entity.getYRot() * (float) (Math.PI / 180.0) - (float) Math.PI),
         0.0,
         Mth.sin(-entity.getYRot() * (float) (Math.PI / 180.0) - (float) Math.PI)
      );

      for (int i = 1; i < spellLevel + 1; i++) {
         PhantomCavalryVisualEntity phantomCavalry = new PhantomCavalryVisualEntity(level, entity);
         if (i % 2 == 0) {
            phantomCavalry.setPos(entity.position().add(orth.scale(i)));
         } else {
            phantomCavalry.setPos(entity.position().add(orth.scale(-i + 1)));
         }

         phantomCavalry.shoot(entity.getLookAngle().multiply(1.0, 0.0, 1.0));
         phantomCavalry.setDamage(this.getSpellPower(spellLevel, entity));
         level.addFreshEntity(phantomCavalry);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
