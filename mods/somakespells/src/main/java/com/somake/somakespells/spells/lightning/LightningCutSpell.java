package com.somake.somakespells.spells.lightning;

import com.somake.somakespells.particle.LightningCutParticleOptions;
import com.somake.somakespells.registries.ModMobEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LightningCutSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "lightning_cut");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}));
   }

   public LightningCutSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 30;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.SHOCKWAVE_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.LIGHTNING_LANCE_CAST.get());
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

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      boolean isRecast = playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId());
      if (entity instanceof ServerPlayer serverPlayer && !isRecast) {
         playerMagicData.getPlayerRecasts().addRecast(new RecastInstance(this.getSpellId(), spellLevel, 2, 40, castSource, null), playerMagicData);
      }

      float radius = 3.25F;
      float distance = 1.9F;
      Vec3 forward = entity.getForward();
      Vec3 hitLocation = entity.position().add(0.0, entity.getBbHeight() * 0.3F, 0.0).add(forward.scale(distance));
      List<Entity> entities = level.getEntities(entity, AABB.ofSize(hitLocation, radius * 2.0F, radius, radius * 2.0F));
      SpellDamageSource damageSource = this.getDamageSource(entity);

      for (Entity targetEntity : entities) {
         if (targetEntity instanceof LivingEntity
            && targetEntity.isAlive()
            && entity.isPickable()
            && targetEntity.position().subtract(entity.getEyePosition()).dot(forward) >= 0.0
            && entity.distanceToSqr(targetEntity) < radius * radius
            && Utils.hasLineOfSight(level, entity.getEyePosition(), targetEntity.getBoundingBox().getCenter(), true)) {
            Vec3 offsetVector = targetEntity.getBoundingBox().getCenter().subtract(entity.getEyePosition());
            if (offsetVector.dot(forward) >= 0.0 && DamageSources.applyDamage(targetEntity, this.getDamage(spellLevel, entity), damageSource)) {
               MagicManager.spawnParticles(
                  level,
                  (ParticleOptions)ParticleRegistry.ELECTRICITY_PARTICLE.get(),
                  targetEntity.getX(),
                  targetEntity.getY() + targetEntity.getBbHeight() * 0.5F,
                  targetEntity.getZ(),
                  30,
                  targetEntity.getBbWidth() * 0.5F,
                  targetEntity.getBbHeight() * 0.5F,
                  targetEntity.getBbWidth() * 0.5F,
                  0.03,
                  false
               );
               EnchantmentHelper.doPostAttackEffects((ServerLevel)level, targetEntity, damageSource);
               if (targetEntity instanceof LivingEntity livingTarget) {
                  livingTarget.addEffect(new MobEffectInstance(ModMobEffects.ELECTROCUTED, 100, 0, true, true, true));
               }
            }
         }
      }

      boolean mirrored = playerMagicData.getCastingEquipmentSlot().equals(SpellSelectionManager.OFFHAND);
      if (isRecast) {
         mirrored = !mirrored;
      }

      MagicManager.spawnParticles(
         level,
         new LightningCutParticleOptions((float)forward.x, (float)forward.y, (float)forward.z, mirrored, false, 1.0F),
         hitLocation.x,
         hitLocation.y + 0.3,
         hitLocation.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) + this.getAdditionalDamage(entity);
   }

   private float getAdditionalDamage(LivingEntity entity) {
      return entity == null ? 0.0F : Utils.getWeaponDamage(entity);
   }

   private String getDamageText(int spellLevel, LivingEntity entity) {
      if (entity != null) {
         float weaponDamage = Utils.getWeaponDamage(entity);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, entity), 1);
         return damage + plus;
      } else {
         return this.getSpellPower(spellLevel, entity) + "";
      }
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ONE_HANDED_HORIZONTAL_SWING_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }
}
