package com.somake.somakespells.spells.fire;

import com.github.L_Ender.cataclysm.init.ModParticle;
import com.somake.somakespells.entity.FireBlastVisualEntity;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.util.FireElementContext;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class FireBlastSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "fire_blast");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(1.0)
      .build();

   public FireBlastSpell() {
      this.manaCostPerLevel = 2;
      this.baseManaCost = 10;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.FIRECHARGE_USE);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      int count = 1;
      float damage = this.getDamage(spellLevel, caster);
      FireElementContext context = FireElementContext.resolveForSpell(caster, this);

      for (int i = 0; i < count; i++) {
         float spread = 0.5F;
         Vec3 look = caster.getLookAngle();
         Vec3 spreadVec = new Vec3(
            (caster.getRandom().nextFloat() - 0.5) * spread, (caster.getRandom().nextFloat() - 0.5) * spread, (caster.getRandom().nextFloat() - 0.5) * spread
         );
         double range = 48.0;
         Vec3 start = caster.getEyePosition();
         Vec3 rayVec = look.add(spreadVec.scale(0.1)).normalize().scale(range);
         Vec3 end = start.add(rayVec);
         HitResult hitResult = level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, caster));
         if (hitResult.getType() != Type.MISS) {
            end = hitResult.getLocation();
         }

         AABB aabb = new AABB(start, end).inflate(1.0);
         EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
            level, caster, start, end, aabb, e -> e != caster && !e.isSpectator() && e.isPickable()
         );
         if (entityHitResult != null) {
            hitResult = entityHitResult;
            end = entityHitResult.getLocation();
         }

         FireBlastVisualEntity visual = new FireBlastVisualEntity(level, start.add(0.0, -0.2, 0.0), end, caster);
         visual.setIsSoulFire(context.isSoulFire());
         visual.setIsInfernalFire(context.isInfernalFire());
         level.addFreshEntity(visual);
         if (hitResult.getType() != Type.MISS) {
            level.playSound(null, end.x, end.y, end.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (context.isSoulFire()) {
               MagicManager.spawnParticles(level, ParticleTypes.SOUL_FIRE_FLAME, end.x, end.y, end.z, 5, 0.1, 0.1, 0.1, 0.1, true);
               MagicManager.spawnParticles(level, (ParticleOptions)ModParticle.IGNIS_SOUL_EXPLODE.get(), end.x, end.y, end.z, 1, 0.0, 0.0, 0.0, 0.0, true);
            } else if (context.isInfernalFire()) {
               MagicManager.spawnParticles(
                  level, ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FIRE.get()), end.x, end.y, end.z, 5, 0.1, 0.1, 0.1, 0.1, true
               );
               MagicManager.spawnParticles(
                  level,
                  ModParticles.infernalFireExplodeOr((SimpleParticleType)ModParticle.IGNIS_EXPLODE.get()),
                  end.x,
                  end.y,
                  end.z,
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  true
               );
            } else {
               MagicManager.spawnParticles(level, (ParticleOptions)ModParticle.IGNIS_EXPLODE.get(), end.x, end.y, end.z, 1, 0.0, 0.0, 0.0, 0.0, true);
               MagicManager.spawnParticles(level, ParticleTypes.FLAME, end.x, end.y, end.z, 5, 0.1, 0.1, 0.1, 0.1, true);
            }

            if (hitResult.getType() == Type.ENTITY && hitResult instanceof EntityHitResult entityHit) {
               Entity target = entityHit.getEntity();
               target.hurt(this.getDamageSource(caster), damage);
            }
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return caster == null ? power : FireElementContext.resolveForSpell(caster, this).applySupportedDamage(power, caster);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }
}
