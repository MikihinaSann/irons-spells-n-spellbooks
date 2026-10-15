package net.acetheeldritchking.cataclysm_spellbooks.spells.sand;

import com.github.L_Ender.cataclysm.entity.projectile.Sandstorm_Projectile;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.util.CSUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DesertWindsSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "desert_winds");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(CSSchoolRegistry.SAND_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(5.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   public DesertWindsSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 2;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 20;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      this.summonSandstormProjectile(spellLevel, entity);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void summonSandstormProjectile(int spellLevel, LivingEntity caster) {
      Level level = caster.level();
      double casterX = caster.getX();
      double casterEyeHeight = CSUtils.getEyeHeight(caster);
      double casterZ = caster.getZ();
      float casterYRot = caster.getYRot();
      float casterXRot = caster.getXRot();
      float d1 = -Mth.sin(casterYRot * (float) (Math.PI / 180.0)) * Mth.cos(casterXRot * (float) (Math.PI / 180.0));
      float d2 = -Mth.sin(casterXRot * (float) (Math.PI / 180.0));
      float d3 = Mth.cos(casterYRot * (float) (Math.PI / 180.0)) * Mth.cos(casterXRot * (float) (Math.PI / 180.0));
      double theta = casterYRot * (Math.PI / 180.0);
      double vecX = Math.cos(++theta);
      double vexZ = Math.sin(theta);
      double x = casterX + vecX;
      double z = casterZ + vexZ;
      Sandstorm_Projectile sandstormProjectile = new Sandstorm_Projectile(level, caster, new Vec3(d1, d2, d3), this.getDamage(spellLevel, caster));
      sandstormProjectile.setState(1);
      sandstormProjectile.setPos(x, casterEyeHeight, z);
      level.addFreshEntity(sandstormProjectile);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 2.0F;
   }
}
