package net.warphan.iss_magicfromtheeast.spells.symmetry;

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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.entity.spells.sword_dance.JadeSword;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;

@AutoSpellConfig
public class SwordDanceSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("iss_magicfromtheeast", "sword_dance");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(90.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(this.getSwordCount(spellLevel, caster), 1)})
      );
   }

   public SwordDanceSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 0;
      this.castTime = 0;
      this.baseManaCost = 30;
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
      int swordCount = this.getSwordCount(spellLevel, entity);
      int offset = 360 / swordCount;
      Vec3 center = entity.getEyePosition().add(0.0, -0.75, 0.0);

      for (int i = 0; i < swordCount; i++) {
         Vec3 motion = new Vec3(0.0, 0.0, 1.0);
         motion = motion.xRot((float) (Math.PI / 180.0));
         motion = motion.yRot(offset * i * (float) (Math.PI / 180.0));
         JadeSword jadeSword = new JadeSword(level, entity);
         if (i % 3 == 0) {
            jadeSword.setSilent(true);
         }

         jadeSword.setDamage(this.getSpellPower(spellLevel, entity));
         jadeSword.setWaitTimer(120);
         jadeSword.getSpeed();
         jadeSword.setDeltaMovement(motion);
         jadeSword.moveTo(center.x, center.y, center.z);
         level.addFreshEntity(jadeSword);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getSwordCount(int spellLevel, LivingEntity entity) {
      return spellLevel + 2;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
