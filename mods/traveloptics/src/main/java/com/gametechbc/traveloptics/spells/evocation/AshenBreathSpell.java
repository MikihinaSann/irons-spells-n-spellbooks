package com.gametechbc.traveloptics.spells.evocation;

import com.gametechbc.traveloptics.entity.spells.AshenBreathProjectile;
import com.gametechbc.traveloptics.init.TOSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.spells.EntityCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class AshenBreathSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "ashen_breath");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(15.0)
      .build();

   public AshenBreathSpell() {
      super();
      this.manaCostPerLevel = 2;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 100;
      this.baseManaCost = 5;
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)TOSounds.REFINED_REVENANT_BREATH.get());
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.isCasting()
         && playerMagicData.getCastingSpellId().equals(this.getSpellId())
         && playerMagicData.getAdditionalCastData() instanceof EntityCastData entityCastData
         && entityCastData.getCastingEntity() instanceof AbstractConeProjectile cone) {
         cone.setDealDamageActive();
      } else {
         AshenBreathProjectile breath = new AshenBreathProjectile(world, entity);
         breath.setPos(entity.position().add(0.0, entity.getEyeHeight() * 0.7, 0.0));
         breath.setDamage(this.getDamage(spellLevel, entity));
         world.addFreshEntity(breath);
         playerMagicData.setAdditionalCastData(new EntityCastData(breath));
         super.onCast(world, spellLevel, entity, castSource, playerMagicData);
      }
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      return mob.distanceToSqr(target) > 120.0;
   }
}
