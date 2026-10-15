package com.somake.somakespells.spells.lightning;

import com.somake.somakespells.entity.LightningBallProjectile;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LightningBallSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "lightning_ball");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(25.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{this.getRadius(spellLevel, caster)})
      );
   }

   public LightningBallSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 40;
      this.baseManaCost = 60;
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
      return Optional.of((SoundEvent)SoundRegistry.SHOCKWAVE_PREPARE.get());
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 origin = entity.getEyePosition();
      LightningBallProjectile lightning_ball_projectile = new LightningBallProjectile(world, entity);
      lightning_ball_projectile.setDamage(this.getDamage(spellLevel, entity));
      lightning_ball_projectile.setExplosionRadius(this.getRadius(spellLevel, entity));
      lightning_ball_projectile.setPos(origin.add(entity.getForward()).subtract(0.0, lightning_ball_projectile.getBbHeight() / 2.0F, 0.0));
      lightning_ball_projectile.shoot(entity.getLookAngle());
      world.addFreshEntity(lightning_ball_projectile);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return 5.0F + 5.0F * this.getSpellPower(spellLevel, caster);
   }

   public int getRadius(int spellLevel, LivingEntity caster) {
      return 2 + (int)this.getSpellPower(spellLevel, caster);
   }
}
