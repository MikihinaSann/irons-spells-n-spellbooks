package net.warphan.iss_magicfromtheeast.spells.symmetry;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.warphan.iss_magicfromtheeast.entity.spells.jade_bullet.JadeBulletProjectile;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;

@AutoSpellConfig
public class JadeBulletSpell extends AbstractSpell {
   private final ResourceLocation spellID = ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "jade_bullet");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(5.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamage(spellLevel, caster)}));
   }

   public JadeBulletSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 2;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 10;
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

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      JadeBulletProjectile jadeBullet = new JadeBulletProjectile(level, entity);
      jadeBullet.setDamage(this.getSpellPower(spellLevel, entity));
      jadeBullet.setPos(entity.position().add(0.0, entity.getEyeHeight() - jadeBullet.getBoundingBox().getYsize() * 0.5, 0.0));
      jadeBullet.shoot(entity.getLookAngle());
      level.addFreshEntity(jadeBullet);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
