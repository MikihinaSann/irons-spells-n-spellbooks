package net.acetheeldritchking.discerning_the_eldritch.spells.ritual;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one.AscendedOneBoss;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class CallAscendedOneSpell extends AbstractRitualSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "call_ascended_one");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(DTESchoolRegistry.RITUAL_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(1000.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.hp", new Object[]{Utils.stringTruncation(this.getAscendedHealth(spellLevel, null), 2)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getAscendedDamage(spellLevel, null), 2)}),
         Component.translatable("ui.discerning_the_eldritch.spell_power", new Object[]{Utils.stringTruncation(this.getAscendedSpellPower(spellLevel, null), 2)}),
         Component.translatable(
            "ui.discerning_the_eldritch.spell_resist", new Object[]{Utils.stringTruncation(this.getAscendedSpellResist(spellLevel, null), 2)}
         )
      );
   }

   public CallAscendedOneSpell() {
      this.manaCostPerLevel = 100;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 10;
      this.castTime = 100;
      this.baseManaCost = 1000;
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

   @Override
   public boolean isComplex() {
      return true;
   }

   @Override
   public boolean isSuperComplex() {
      return true;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 vec = entity.getEyePosition();
      double randomNearbyX = vec.x + entity.getRandom().nextGaussian() * 3.0;
      double randomNearbyZ = vec.z + entity.getRandom().nextGaussian() * 3.0;
      this.spawnAscendedOneBoss(randomNearbyX, vec.y, randomNearbyZ, entity, level, spellLevel);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnAscendedOneBoss(double x, double y, double z, LivingEntity caster, Level level, int spellLevel) {
      AscendedOneBoss ascendedOneBoss = new AscendedOneBoss(level);
      ascendedOneBoss.setPos(x, y, z);
      ascendedOneBoss.setOldPosAndRot();
      ascendedOneBoss.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAscendedDamage(spellLevel, caster));
      ascendedOneBoss.getAttributes().getInstance(AttributeRegistry.SPELL_POWER).setBaseValue(this.getAscendedSpellPower(spellLevel, caster));
      ascendedOneBoss.getAttributes().getInstance(AttributeRegistry.SPELL_RESIST).setBaseValue(this.getAscendedSpellResist(spellLevel, caster));
      ascendedOneBoss.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getAscendedHealth(spellLevel, caster));
      ascendedOneBoss.setHealth(ascendedOneBoss.getMaxHealth());
      ascendedOneBoss.finalizeSpawn((ServerLevelAccessor)level, level.getCurrentDifficultyAt(ascendedOneBoss.getOnPos()), MobSpawnType.MOB_SUMMONED, null);
      level.addFreshEntity(ascendedOneBoss);
   }

   private float getAscendedDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 2.5F;
   }

   private float getAscendedHealth(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 60.5F;
   }

   private float getAscendedSpellPower(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.75F;
   }

   private float getAscendedSpellResist(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.075F;
   }
}
