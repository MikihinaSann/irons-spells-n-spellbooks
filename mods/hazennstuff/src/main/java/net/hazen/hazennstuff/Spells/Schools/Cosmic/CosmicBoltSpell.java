package net.hazen.hazennstuff.Spells.Schools.Cosmic;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.hazen.hazennstuff.Entity.Spells.Astral.CosmicBolt.CosmicBolt;
import net.hazen.hazennstuff.Registries.HnSSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class CosmicBoltSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "cosmic_bolt");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(HnSSchoolRegistry.COSMIC_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(2.5)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public CosmicBoltSpell() {
      this.manaCostPerLevel = 2;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 10;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      CosmicBolt cosmicBolt = new CosmicBolt(world, entity);
      cosmicBolt.setSpellLevel(spellLevel);
      cosmicBolt.setPos(entity.position().add(0.0, entity.getEyeHeight() - cosmicBolt.getBoundingBox().getYsize() * 0.5, 0.0));
      cosmicBolt.shoot(entity.getLookAngle());
      cosmicBolt.setDamage(this.getDamage(spellLevel, entity));
      world.addFreshEntity(cosmicBolt);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 0.5F;
   }
}
