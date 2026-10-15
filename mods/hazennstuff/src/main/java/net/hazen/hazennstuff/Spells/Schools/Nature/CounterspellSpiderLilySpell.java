package net.hazen.hazennstuff.Spells.Schools.Nature;

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
import net.hazen.hazennstuff.Entity.Spells.Nature.SpiderLily.SpiderLily;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class CounterspellSpiderLilySpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "counterspell_spider_lily");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(4.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public CounterspellSpiderLilySpell() {
      this.manaCostPerLevel = 4;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 20;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.ACID_ORB_CAST.get());
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

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      SpiderLily spiderLily = new SpiderLily(world, entity);
      spiderLily.setPos(entity.position().add(0.0, entity.getEyeHeight() - spiderLily.getBoundingBox().getYsize() * 0.5, 0.0));
      spiderLily.shoot(entity.getLookAngle());
      spiderLily.setDamage(this.getDamage(spellLevel, entity));
      world.addFreshEntity(spiderLily);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 0.5F;
   }
}
