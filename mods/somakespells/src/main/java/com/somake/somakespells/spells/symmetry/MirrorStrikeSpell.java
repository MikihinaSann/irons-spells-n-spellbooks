package com.somake.somakespells.spells.symmetry;

import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.MirrorStrikeVisualEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MirrorStrikeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "mirror_strike");
   private final ResourceLocation symmetrySchool = MagicFromTheEastCompat.SYMMETRY_SCHOOL_ID;
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(this.symmetrySchool)
      .setMaxLevel(8)
      .setCooldownSeconds(25.0)
      .build();

   public MirrorStrikeSpell() {
      this.manaCostPerLevel = 10;
      this.baseManaCost = 40;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 2;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.MALEDICTUS_MACE_SWING.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SLASH_ANIMATION;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel);
      float damage = this.getDamage(spellLevel, caster);
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            new BlastwaveParticleOptions(0.33F, 0.92F, 0.8F, radius), caster.getX(), caster.getY() + 0.135F, caster.getZ(), 1, 0.0, 0.0, 0.0, 0.0
         );
         ScreenShake_Entity.ScreenShake(serverLevel, caster.position(), 15.0F, 0.1F, 0, 10);
         MirrorStrikeVisualEntity visualEntity = new MirrorStrikeVisualEntity(level, caster.getX(), caster.getY() + 0.1, caster.getZ(), radius);
         visualEntity.setYRot(caster.getYRot());
         level.addFreshEntity(visualEntity);

         for (Entity target : level.getEntities(caster, caster.getBoundingBox().inflate(radius))) {
            if (target instanceof LivingEntity livingTarget && target != caster && !target.isAlliedTo(caster) && target.distanceToSqr(caster) < radius * radius
               )
             {
               DamageSources.applyDamage(livingTarget, damage, this.getDamageSource(caster));
               serverLevel.sendParticles(
                  MagicFromTheEastCompat.getJadeShatterParticle(),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.5F,
                  target.getZ(),
                  5,
                  target.getBbWidth() * 0.5F,
                  target.getBbHeight() * 0.5F,
                  target.getBbWidth() * 0.5F,
                  0.03
               );
               Vec3 dir = target.position().subtract(caster.position()).normalize();
               target.push(dir.x * 1.5, 0.2, dir.z * 1.5);
            }
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private float getRadius(int spellLevel) {
      return 1.0F + spellLevel * 0.5F;
   }
}
