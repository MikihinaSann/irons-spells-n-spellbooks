package com.gametechbc.traveloptics.spells.ice;

import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.particle.SphereParticleManager;
import com.gametechbc.traveloptics.api.spells.AbstractUniqueSpell;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.github.L_Ender.cataclysm.entity.projectile.Phantom_Halberd_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class HalberdHorizonSpell extends AbstractUniqueSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "halberd_horizon");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(20.0)
      .build();

   public HalberdHorizonSpell() {
      super();
      this.manaCostPerLevel = 40;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 21;
      this.baseManaCost = 30;
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

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.HALBERD_HORIZON_CAST;
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.HALBERD_HORIZON_FINISH;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.FLAME_BURST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.MALEDICTUS_SHORT_ROAR.get());
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.aoe_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.ring_count", new Object[]{this.getRingCount(spellLevel)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      int rings = this.getRingCount(spellLevel);
      int entitiesInFirstRing = 8;
      float distanceBetweenRings = 1.0F;
      float innerRingDistance = 1.5F;
      float damage = this.getDamage(spellLevel, caster);
      double ringOffset = Math.PI / 8;

      for (int ring = 0; ring < rings; ring++) {
         float currentRingDistance = innerRingDistance + ring * distanceBetweenRings;
         int entitiesInThisRing = entitiesInFirstRing + ring * 4;
         int warmupDelay = ring * 3;
         double currentRingOffset = ring % 2 == 0 ? 0.0 : ringOffset;

         for (int i = 0; i < entitiesInThisRing; i++) {
            double angle = (Math.PI * 2) * i / entitiesInThisRing + currentRingOffset;
            double offsetX = currentRingDistance * Math.cos(angle);
            double offsetZ = currentRingDistance * Math.sin(angle);
            double x = caster.getX() + offsetX;
            double y = caster.getY();
            double z = caster.getZ() + offsetZ;
            Phantom_Halberd_Entity halberdEntity = new Phantom_Halberd_Entity(level, x, y, z, (float)angle, warmupDelay, caster, damage);
            level.addFreshEntity(halberdEntity);
            CameraShakeManager.addCameraShake(new CameraShakeData(25, caster.position(), 12.0F));
            SphereParticleManager.spawnParticles(level, caster, 3, (ParticleOptions)ModParticle.PHANTOM_WING_FLAME.get(), ParticleDirection.OUTWARD, 3.5);
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public int getRingCount(int spellLevel) {
      return 1 + spellLevel;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 18.0F + this.getSpellPower(spellLevel, caster) * 4.0F;
   }
}
