package net.acetheeldritchking.discerning_the_eldritch.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell.TeleportData;
import java.util.List;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class OtherworldlyPresenceSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "otherworldly_presence");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(10.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getDistance(spellLevel, caster), 1)}));
   }

   public OtherworldlyPresenceSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 4;
      this.castTime = 0;
      this.baseManaCost = 40;
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
      Vec3 destination = null;
      TeleportData teleportData = (TeleportData)playerMagicData.getAdditionalCastData();
      if (teleportData != null) {
         Vec3 potentialTarget = teleportData.getTeleportTargetPosition();
         if (potentialTarget != null) {
            destination = potentialTarget;
            Utils.handleSpellTeleport(this, entity, potentialTarget);
         }
      }

      if (destination == null) {
         destination = TeleportSpell.findTeleportLocation(level, entity, this.getDistance(spellLevel, entity));
         Utils.handleSpellTeleport(this, entity, destination);
      }

      if (entity.isPassenger()) {
         entity.stopRiding();
      }

      entity.resetFallDistance();
      entity.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.METAPHYSICAL_POTION_EFFECT, 200, 0, false, false, true));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDistance(int spellLevel, LivingEntity entity) {
      return (float)(Utils.softCapFormula(this.getEntityPowerMultiplier(entity)) * this.getSpellPower(spellLevel, null));
   }
}
