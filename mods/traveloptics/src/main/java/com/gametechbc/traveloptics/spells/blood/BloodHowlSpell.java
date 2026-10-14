package com.gametechbc.traveloptics.spells.blood;

import com.gametechbc.traveloptics.entity.extended.ExtendedWitherHowitzerEntity;
import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.github.L_Ender.cataclysm.entity.projectile.Wither_Howitzer_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@AutoSpellConfig
public class BloodHowlSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "blood_howl");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(20.0)
      .build();

   public BloodHowlSpell() {
      super();
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 70;
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

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.HARBINGER_HURT.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.SHOOT;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 1 + spellLevel;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.aoe_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.shot_count", new Object[]{this.getRecastCount(spellLevel, caster)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 80, castSource, null), playerMagicData);
      }

      this.spawnWitherHowitzerProjectile(level, entity, spellLevel);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 4.0F + this.getSpellPower(spellLevel, caster) * 2.5F;
   }

   private void spawnWitherHowitzerProjectile(Level level, LivingEntity entity, int spellLevel) {
      float damage = this.getDamage(spellLevel, entity);
      Vec3 lookVec = entity.getLookAngle();
      Vec3 spawnPos = entity.getEyePosition().add(lookVec.x * 2.0, lookVec.y * 2.0, lookVec.z * 2.0);
      ExtendedWitherHowitzerEntity projectile = new ExtendedWitherHowitzerEntity(
         (EntityType<? extends Wither_Howitzer_Entity>)TOEntities.EXTENDED_WITHER_HOWITZER.get(), level, entity, damage, damage / 2.0F
      );
      projectile.setOwner(entity);
      projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
      projectile.shoot(lookVec.x, lookVec.y, lookVec.z, 1.8F, 1.0F);
      level.addFreshEntity(projectile);
   }
}
