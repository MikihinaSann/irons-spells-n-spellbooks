package net.hazen.hazennstuff.Spells.Schools.Nature;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Entity.Spells.Nature.DeathSentence.DeathSentence;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DeathSentenceSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "death_sentence");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 2)})
      );
   }

   public DeathSentenceSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 2;
      this.castTime = 10;
      this.baseManaCost = 30;
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
      return Optional.of((SoundEvent)SoundRegistry.FIREFLY_SPELL_PREPARE.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData magicData) {
      if (!level.isClientSide) {
         if (level instanceof ServerLevel serverLevel) {
            this.fireProjectiles(serverLevel, caster, spellLevel);
         }
      }
   }

   private void fireProjectiles(ServerLevel level, LivingEntity caster, int spellLevel) {
      int count = 8;
      double radius = 1.2;

      for (int i = 0; i < count; i++) {
         double angle = Math.toRadians(360.0 / count * i);
         Vec3 dir = new Vec3(Math.cos(angle), 0.0, Math.sin(angle)).normalize().scale(radius);
         DeathSentence projectile = new DeathSentence(level, caster);
         projectile.setPos(caster.getX(), caster.getY() + 1.0, caster.getZ());
         projectile.setYRot((float)Math.toDegrees(angle));
         projectile.setDeltaMovement(dir);
         projectile.setDamage(this.getDamage(spellLevel, caster));
         level.addFreshEntity(projectile);
      }
   }

   public float getRadius(int spellLevel, LivingEntity caster) {
      return 6.0F + spellLevel * 0.75F;
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 20.0F);
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return 5.0F + this.getSpellPower(spellLevel, caster) * 0.6F;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }
}
