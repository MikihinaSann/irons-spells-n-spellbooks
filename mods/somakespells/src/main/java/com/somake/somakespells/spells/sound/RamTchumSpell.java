package com.somake.somakespells.spells.sound;

import com.somake.somakespells.compat.TunesCompat;
import com.somake.somakespells.registries.ModSounds;
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
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RamTchumSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "ram_tchum");
   private final DefaultConfig defaultConfig;

   public RamTchumSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 2;
      this.castTime = 6;
      this.baseManaCost = 30;
      this.defaultConfig = new DefaultConfig()
         .setMinRarity(SpellRarity.RARE)
         .setSchoolResource(TunesCompat.schoolOrEvocation())
         .setMaxLevel(5)
         .setCooldownSeconds(10.0)
         .build();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)})
      );
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
      return Optional.of((SoundEvent)ModSounds.RAM_TCHUM.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel, entity);
      float damage = this.getSpellPower(spellLevel, entity);

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius))) {
         if (target != entity && !DamageSources.isFriendlyFireBetween(entity, target) && target.distanceTo(entity) <= radius) {
            DamageSources.applyDamage(target, damage, this.getDamageSource(entity));
            Vec3 knockback = target.position().subtract(entity.position()).normalize().scale(1.5);
            target.setDeltaMovement(target.getDeltaMovement().add(knockback));
            target.hurtMarked = true;
         }
      }

      if (level instanceof ServerLevel serverLevel) {
         ParticleOptions particleType = TunesCompat.getMusicalParticle();
         int particleCount = 100;
         float speed = radius / 10.0F;

         for (int i = 0; i < particleCount; i++) {
            double angle = i * 2 * Math.PI / particleCount;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            serverLevel.sendParticles(particleType, entity.getX(), entity.getY() + 0.5, entity.getZ(), 0, dx, 0.0, dz, speed);
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getRadius(int spellLevel, LivingEntity entity) {
      return 3.0F + spellLevel / 2.0F;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }
}
