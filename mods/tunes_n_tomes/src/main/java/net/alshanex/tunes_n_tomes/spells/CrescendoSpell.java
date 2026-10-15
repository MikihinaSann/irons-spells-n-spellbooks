package net.alshanex.tunes_n_tomes.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.StompAoe;
import java.util.List;
import java.util.Optional;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TParticleRegistry;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class CrescendoSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "crescendo");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(TSchoolRegistry.MELODY_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(15.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public CrescendoSpell() {
      this.manaCostPerLevel = 50;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 8;
      this.castTime = 30;
      this.baseManaCost = 110;
   }

   public boolean allowLooting() {
      return false;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)TSoundRegistry.CRESCENDO.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float range = getRange(spellLevel, entity);
      Vec3 start = entity.getEyePosition();
      Vec3 end = start.add(entity.getForward().scale(range));
      AABB boundingBox = entity.getBoundingBox().expandTowards(end.subtract(start));
      int duration = 200;

      for (Entity target : level.getEntities(entity, boundingBox)) {
         HitResult hit = Utils.checkEntityIntersecting(target, start, end, 0.4F);
         if (hit.getType() != Type.MISS) {
            DamageSources.applyDamage(target, this.getDamage(spellLevel, entity), this.getDamageSource(entity));
            if (target instanceof LivingEntity livingEntity) {
               livingEntity.addEffect(new MobEffectInstance(TEffectRegistry.DEAFENED, duration, 0));
            }
         }
      }

      Vec3 vec3 = entity.getLookAngle().normalize();

      for (int i = 0; i < range; i += 3) {
         Vec3 vec32 = vec3.scale(i).add(entity.getEyePosition());
         MagicManager.spawnParticles(
            level, (ParticleOptions)TParticleRegistry.VIBRATION_PARTICLE.get(), vec32.x, vec32.y, vec32.z, 1, 0.0, 0.0, 0.0, 0.0, false
         );
         MagicManager.spawnParticles(
            level, (ParticleOptions)TParticleRegistry.MUSICAL_NOTE_PARTICLE.get(), vec32.x, vec32.y, vec32.z, 25, 0.0, 0.0, 0.0, 0.18, true
         );
      }

      Vec3 spawn = Utils.moveToRelativeGroundLevel(level, entity.getEyePosition().add(entity.getForward().multiply(1.0, 0.0, 1.0)), 1);
      BlockPos bpos = BlockPos.containing(spawn);
      ((ServerLevel)level)
         .sendParticles(
            new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(bpos)).setPos(bpos),
            spawn.x,
            spawn.y,
            spawn.z,
            40,
            0.0,
            0.0,
            0.0,
            0.2 + 0.05F * spellLevel
         );
      StompAoe stomp = new StompAoe(level, (int)getRange(spellLevel, entity), entity.getYRot());
      stomp.moveTo(spawn);
      stomp.setDamage(0.0F);
      stomp.setExplosionRadius(this.getEntityPowerMultiplier(entity));
      stomp.setOwner(entity);
      level.addFreshEntity(stomp);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public static float getRange(int level, LivingEntity caster) {
      return 15 + 5 * level;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.6F;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_SPIT_ANIMATION;
   }
}
