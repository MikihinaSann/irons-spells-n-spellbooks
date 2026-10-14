package com.gametechbc.traveloptics.spells.ender;

import com.gametechbc.traveloptics.spells.TOSpellAnimations;
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
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

@AutoSpellConfig
public class VoidEruptionSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "void_eruption");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(25.0)
      .build();

   public VoidEruptionSpell() {
      super();
      this.manaCostPerLevel = 55;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 8;
      this.baseManaCost = 70;
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
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.ELDRITCH_BLAST.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.SHOOT_WINDUP;
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.SHOOT;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return spellLevel;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.aoe_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.shot_count", new Object[]{this.getRecastCount(spellLevel, caster)}),
         Component.translatable("ui.traveloptics.falloff_distance", new Object[]{Utils.stringTruncation(getRange(spellLevel, caster), 1)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 150, castSource, null), playerMagicData);
      }

      HitResult hitResult = Utils.raycastForEntity(level, entity, getRange(spellLevel, entity), true, 0.15F);
      this.spawnVoidHowitzerProjectile(level, entity);
      if (hitResult.getType() == Type.ENTITY) {
         Entity target = ((EntityHitResult)hitResult).getEntity();
         if (target instanceof LivingEntity) {
            DamageSources.applyDamage(target, this.getDamage(spellLevel, entity), this.getDamageSource(entity));
            AABB areaOfEffect = new AABB(target.blockPosition()).inflate(5.0);

            for (LivingEntity nearbyEntity : level.getEntitiesOfClass(LivingEntity.class, areaOfEffect, e -> e != entity && e != target)) {
               DamageSources.applyDamage(nearbyEntity, this.getDamage(spellLevel, entity), this.getDamageSource(entity));
            }
         }
      }

      MagicManager.spawnParticles(
         level, ParticleHelper.UNSTABLE_ENDER, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 50, 0.0, 0.0, 0.0, 0.3, false
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnVoidHowitzerProjectile(Level level, LivingEntity entity) {
      Vec3 lookVec = entity.getLookAngle();
      Vec3 spawnPos = entity.getEyePosition().add(lookVec.x * 2.0, lookVec.y * 2.0, lookVec.z * 2.0);
      ResourceLocation projectileType = new ResourceLocation("cataclysm", "void_howitzer");
      EntityType<?> entityType = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(projectileType);
      if (entityType != null) {
         Projectile projectile = (Projectile)entityType.create(level);
         if (projectile != null) {
            projectile.setOwner(entity);
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.shoot(lookVec.x, lookVec.y, lookVec.z, 1.8F, 1.0F);
            level.addFreshEntity(projectile);
         }
      }
   }

   public static float getRange(int level, LivingEntity caster) {
      return 20.0F;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 13.0F + this.getSpellPower(spellLevel, caster) * 3.0F;
   }
}
