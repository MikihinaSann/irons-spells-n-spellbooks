package com.gametechbc.traveloptics.spells.fire;

import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.particle.SphereParticleManager;
import com.gametechbc.traveloptics.api.spells.AbstractWeaponSpell;
import com.gametechbc.traveloptics.entity.spells.gyro_slash.GyroSlashProjectile;
import com.gametechbc.traveloptics.entity.spells.gyro_slash.GyroSlashVisual;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOItems;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@AutoSpellConfig
public class GyroSlashSpell extends AbstractWeaponSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "gyro_slash");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(30.0)
      .build();

   public GyroSlashSpell() {
      super();
      this.manaCostPerLevel = 0;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 25;
      this.baseManaCost = 180;
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
      return TOSpellAnimations.GYRO_SLASH_CAST;
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.GYRO_SLASH_FINISH;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)TOSounds.DEVASTATOR_BLADE_TRANSFORM.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.SWORD_STOMP.get());
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      List<Item> allowedWeapons = List.of(
         (Item)TOItems.INFERNAL_DEVASTATOR.get(),
         (Item)TOItems.INFERNAL_DEVASTATOR_LEVEL_ONE.get(),
         (Item)TOItems.INFERNAL_DEVASTATOR_LEVEL_TWO.get(),
         (Item)TOItems.INFERNAL_DEVASTATOR_LEVEL_THREE.get()
      );
      if (entity instanceof Player player) {
         MobEffectInstance overloadedEffect = player.getEffect(TOEffects.OVERLOADED_EFFECT);
         if (overloadedEffect != null) {
            if (!level.isClientSide) {
               player.displayClientMessage(Component.translatable("effect.traveloptics.overloaded.warning").withStyle(ChatFormatting.RED), true);
            }

            return false;
         }
      }

      if (allowedWeapons.contains(entity.getMainHandItem().getItem())) {
         return true;
      } else {
         if (entity instanceof Player playerx && !level.isClientSide) {
            playerx.displayClientMessage(Component.translatable("spell.traveloptics.gyro_slash.warning").withStyle(ChatFormatting.RED), true);
         }

         return false;
      }
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      SphereParticleManager.spawnParticles(level, entity, 2, ParticleTypes.FLAME, ParticleDirection.INWARD, 3.0);
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      entity.addEffect(new MobEffectInstance(TOEffects.OVERLOADED_EFFECT, 55, 0, false, false, false));
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      float radius = 3.5F;
      float distance = 2.5F;
      ScreenShake_Entity.ScreenShake(level, caster.position(), 5.0F, 0.025F, 5, 10);
      Vec3 forward = caster.getForward();
      Vec3 hitLocation = caster.position().add(0.0, caster.getBbHeight() * 0.3F, 0.0).add(forward.scale(distance));
      List<Entity> entities = level.getEntities(caster, AABB.ofSize(hitLocation, radius * 2.0F, radius, radius * 2.0F));
      SpellDamageSource damageSource = this.getDamageSource(caster);

      for (Entity targetEntity : entities) {
         if (targetEntity instanceof LivingEntity
            && targetEntity.isAlive()
            && caster.isPickable()
            && targetEntity.position().subtract(caster.getEyePosition()).dot(forward) >= 0.0
            && caster.distanceToSqr(targetEntity) < radius * radius
            && Utils.hasLineOfSight(level, caster.getEyePosition(), targetEntity.getBoundingBox().getCenter(), true)) {
            Vec3 offsetVector = targetEntity.getBoundingBox().getCenter().subtract(caster.getEyePosition());
            if (offsetVector.dot(forward) >= 0.0 && DamageSources.applyDamage(targetEntity, this.getDamage(spellLevel, caster), damageSource)) {
               MagicManager.spawnParticles(
                  level,
                  ParticleHelper.EMBERS,
                  targetEntity.getX(),
                  targetEntity.getY() + targetEntity.getBbHeight() * 0.5F,
                  targetEntity.getZ(),
                  50,
                  targetEntity.getBbWidth() * 0.5F,
                  targetEntity.getBbHeight() * 0.5F,
                  targetEntity.getBbWidth() * 0.5F,
                  0.03,
                  false
               );
               EnchantmentHelper.doPostAttackEffects((ServerLevel)level, targetEntity, damageSource);
            }
         }
      }

      GyroSlashVisual gyroSlashVisual = new GyroSlashVisual(level, false);
      gyroSlashVisual.moveTo(hitLocation);
      gyroSlashVisual.setYRot(caster.getYRot());
      gyroSlashVisual.setXRot(caster.getXRot());
      level.addFreshEntity(gyroSlashVisual);
      List<Item> canCreateProjectile = List.of((Item)TOItems.INFERNAL_DEVASTATOR_LEVEL_TWO.get(), (Item)TOItems.INFERNAL_DEVASTATOR_LEVEL_THREE.get());
      if (canCreateProjectile.contains(caster.getMainHandItem().getItem())) {
         GyroSlashProjectile gyroSlashProjectile = new GyroSlashProjectile(level, caster);
         gyroSlashProjectile.setPos(caster.getEyePosition());
         gyroSlashProjectile.shoot(caster.getLookAngle());
         gyroSlashProjectile.setDamage(this.getDamage(spellLevel, caster));
         gyroSlashProjectile.setEffectAmplifier(this.getFlameJetDamage(spellLevel, caster));
         level.addFreshEntity(gyroSlashProjectile);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return 20.0F + this.getSpellPower(spellLevel, entity) * 4.0F;
   }

   private int getFlameJetDamage(int spellLevel, LivingEntity entity) {
      return (int)(10.0F + this.getSpellPower(spellLevel, entity) * 2.0F);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.flame_jet_damage", new Object[]{Utils.stringTruncation(this.getFlameJetDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.flame_jet.requirement", new Object[]{Utils.stringTruncation(this.getFlameJetDamage(spellLevel, caster), 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
