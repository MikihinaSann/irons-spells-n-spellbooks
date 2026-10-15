package net.acetheeldritchking.cataclysm_spellbooks.spells.sand;

import com.github.L_Ender.cataclysm.entity.effect.Sandstorm_Entity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import java.util.List;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SandstormSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "sandstorm");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(CSSchoolRegistry.SAND_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(30.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.sandstorm.lifespan", new Object[]{this.getLifespan(spellLevel, caster) / 20.0F}),
         Component.translatable("ui.cataclysm_spellbooks.sandstorm_amount", new Object[]{spellLevel})
      );
   }

   public SandstormSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 2;
      this.spellPowerPerLevel = 2;
      this.castTime = 25;
      this.baseManaCost = 60;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      this.summonSandstormAroundCaster(spellLevel, level, entity);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void summonSandstormAroundCaster(int spellLevel, Level level, LivingEntity caster) {
      for (int i = 0; i < spellLevel; i++) {
         double casterX = caster.getX();
         double casterY = caster.getY();
         double casterZ = caster.getZ();
         float angle = (float)(i * Math.PI / 1.5);
         double stormX = casterX + Mth.cos(angle) * 4.0F;
         double stormZ = casterZ + Mth.sin(angle) * 4.0F;
         Sandstorm_Entity sandstorm = new Sandstorm_Entity(level, stormX, casterY, stormZ, (int)this.getLifespan(spellLevel, caster), angle, caster);
         level.addFreshEntity(sandstorm);
      }
   }

   private float getLifespan(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 30.0F;
   }
}
