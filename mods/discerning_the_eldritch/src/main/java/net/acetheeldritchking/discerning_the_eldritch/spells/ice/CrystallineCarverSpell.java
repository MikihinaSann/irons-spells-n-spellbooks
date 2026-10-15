package net.acetheeldritchking.discerning_the_eldritch.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.crystal_carve.CrystalCarveEntity;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttachmentRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTETags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CrystallineCarverSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "crystalline_carver");
   private boolean isFinalCast;
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(35.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.discerning_the_eldritch.frozen_weapon_bonus"),
         Component.translatable("ui.discerning_the_eldritch.damage_on_last_recast")
      );
   }

   public CrystallineCarverSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
      this.baseManaCost = 50;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return this.isFinalCast ? CastType.LONG : CastType.INSTANT;
   }

   public AnimationHolder getCastStartAnimation() {
      return this.isFinalCast ? ASSpellAnimations.ANIMATION_RIGHT_HORIZONTAL_SWORD_SLASH : SpellAnimations.SLASH_ANIMATION;
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return !this.isFinalCast;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.isFinalCast ? this.getCastTime(spellLevel) : this.getCastTime(spellLevel);
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public Optional<SoundEvent> getCastStartSound() {
      return this.isFinalCast
         ? Optional.of((SoundEvent)DTESoundRegistry.CRYSTALLINE_CARVER_FINISH.get())
         : Optional.of((SoundEvent)DTESoundRegistry.CRYSTALLINE_CARVER_BASE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return spellLevel;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 160, castSource, null), playerMagicData);
      }

      float radius = 3.25F;
      float distance = 1.5F;
      Vec3 hitLocation = entity.position().add(0.0, entity.getBbHeight() * 0.3F, 0.0).add(entity.getForward().multiply(distance, 0.35F, distance));

      for (Entity target : entity.level().getEntities(entity, AABB.ofSize(hitLocation, radius * 2.0F, radius, radius * 2.0F))) {
         if (entity.isPickable()
            && entity.distanceToSqr(target) < radius * radius
            && Utils.hasLineOfSight(level, entity.getEyePosition(), target.getBoundingBox().getCenter(), true)
            && !(target instanceof ItemEntity)
            && target instanceof LivingEntity livingEntity) {
            int frostbiteLevel = (Integer)target.getData(DTEAttachmentRegistry.FROSTBITE_LEVEL);
            DiscerningTheEldritch.LOGGER.debug("Applying chilled");
            target.setData(DTEAttachmentRegistry.FROSTBITE_LEVEL, frostbiteLevel + 1);
            livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.CHILLED, 60, 0, false, false, false));
            DiscerningTheEldritch.LOGGER.debug("Frostbite: " + target.getData(DTEAttachmentRegistry.FROSTBITE_LEVEL));
         }
      }

      boolean mirrored = playerMagicData.getCastingEquipmentSlot().equals(SpellSelectionManager.OFFHAND);
      CrystalCarveEntity swipe = new CrystalCarveEntity(level, mirrored);
      swipe.moveTo(hitLocation);
      swipe.setYRot(entity.getYRot());
      swipe.setIsFinal(false);
      level.addFreshEntity(swipe);
      this.isFinalCast = playerMagicData.getPlayerRecasts().getRemainingRecastsForSpell(this.spellId.toString()) == 2;
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
      if (recastResult.isSuccess()) {
         float radius = 2.15F;
         float distance = 1.2F;
         Vec3 hitLocation = serverPlayer.position()
            .add(0.0, serverPlayer.getBbHeight() * 0.3F, 0.0)
            .add(serverPlayer.getForward().multiply(distance, 0.35F, distance));

         for (Entity target : serverPlayer.level().getEntities(serverPlayer, AABB.ofSize(hitLocation, radius * 2.0F, radius, radius * 2.0F))) {
            if (serverPlayer.isPickable()
               && serverPlayer.distanceToSqr(target) < radius * radius
               && Utils.hasLineOfSight(serverPlayer.level(), serverPlayer.getEyePosition(), target.getBoundingBox().getCenter(), true)
               && !(target instanceof ItemEntity)) {
               float baseDamage = this.getDamage(recastInstance.getSpellLevel(), serverPlayer);
               if (target instanceof LivingEntity livingTarget) {
                  if (livingTarget.hasEffect(MobEffectRegistry.CHILLED)) {
                     int amplifier = (Integer)target.getData(DTEAttachmentRegistry.FROSTBITE_LEVEL);
                     if (serverPlayer.getMainHandItem().is(DTETags.FROZEN_WEAPONS)) {
                        float extraDamage = (baseDamage + amplifier) * 1.5F;
                        if (DamageSources.applyDamage(target, extraDamage, this.getDamageSource(serverPlayer))) {
                           MagicManager.spawnParticles(
                              serverPlayer.level(),
                              ParticleHelper.SNOWFLAKE,
                              target.getX(),
                              target.getY() + target.getBbHeight() * 0.5F,
                              target.getZ(),
                              50,
                              target.getBbWidth() * 0.5F,
                              target.getBbHeight() * 0.5F,
                              target.getBbWidth() * 0.5F,
                              0.03,
                              false
                           );
                           EnchantmentHelper.doPostAttackEffects((ServerLevel)serverPlayer.level(), target, this.getDamageSource(serverPlayer));
                           DiscerningTheEldritch.LOGGER.debug("Bonus damage (Icy): " + extraDamage);
                        }
                     } else {
                        float extraDamage = baseDamage + amplifier;
                        if (DamageSources.applyDamage(target, extraDamage, this.getDamageSource(serverPlayer))) {
                           MagicManager.spawnParticles(
                              serverPlayer.level(),
                              ParticleHelper.SNOWFLAKE,
                              target.getX(),
                              target.getY() + target.getBbHeight() * 0.5F,
                              target.getZ(),
                              50,
                              target.getBbWidth() * 0.5F,
                              target.getBbHeight() * 0.5F,
                              target.getBbWidth() * 0.5F,
                              0.03,
                              false
                           );
                           EnchantmentHelper.doPostAttackEffects((ServerLevel)serverPlayer.level(), target, this.getDamageSource(serverPlayer));
                           DiscerningTheEldritch.LOGGER.debug("Bonus damage: " + extraDamage);
                        }
                     }
                  } else if (serverPlayer.getMainHandItem().is(DTETags.FROZEN_WEAPONS)) {
                     if (DamageSources.applyDamage(target, baseDamage * 1.5F, this.getDamageSource(serverPlayer))) {
                        MagicManager.spawnParticles(
                           serverPlayer.level(),
                           ParticleHelper.SNOWFLAKE,
                           target.getX(),
                           target.getY() + target.getBbHeight() * 0.5F,
                           target.getZ(),
                           50,
                           target.getBbWidth() * 0.5F,
                           target.getBbHeight() * 0.5F,
                           target.getBbWidth() * 0.5F,
                           0.03,
                           false
                        );
                        EnchantmentHelper.doPostAttackEffects((ServerLevel)serverPlayer.level(), target, this.getDamageSource(serverPlayer));
                        DiscerningTheEldritch.LOGGER.debug("Base damage (in living.hasEffect() (Icy)): " + baseDamage);
                     }
                  } else if (DamageSources.applyDamage(target, baseDamage, this.getDamageSource(serverPlayer))) {
                     MagicManager.spawnParticles(
                        serverPlayer.level(),
                        ParticleHelper.SNOWFLAKE,
                        target.getX(),
                        target.getY() + target.getBbHeight() * 0.5F,
                        target.getZ(),
                        50,
                        target.getBbWidth() * 0.5F,
                        target.getBbHeight() * 0.5F,
                        target.getBbWidth() * 0.5F,
                        0.03,
                        false
                     );
                     EnchantmentHelper.doPostAttackEffects((ServerLevel)serverPlayer.level(), target, this.getDamageSource(serverPlayer));
                     DiscerningTheEldritch.LOGGER.debug("Base damage (in living.hasEffect()): " + baseDamage);
                  }
               } else if (DamageSources.applyDamage(target, baseDamage, this.getDamageSource(serverPlayer))) {
                  MagicManager.spawnParticles(
                     serverPlayer.level(),
                     ParticleHelper.SNOWFLAKE,
                     target.getX(),
                     target.getY() + target.getBbHeight() * 0.5F,
                     target.getZ(),
                     50,
                     target.getBbWidth() * 0.5F,
                     target.getBbHeight() * 0.5F,
                     target.getBbWidth() * 0.5F,
                     0.03,
                     false
                  );
                  EnchantmentHelper.doPostAttackEffects((ServerLevel)serverPlayer.level(), target, this.getDamageSource(serverPlayer));
                  DiscerningTheEldritch.LOGGER.debug("Base damage (outside): " + baseDamage);
               }
            }

            target.setData(DTEAttachmentRegistry.FROSTBITE_LEVEL, 0);
         }

         CrystalCarveEntity swipe = new CrystalCarveEntity(serverPlayer.level(), false);
         swipe.moveTo(hitLocation);
         swipe.setYRot(serverPlayer.getYRot());
         swipe.setIsFinal(true);
         serverPlayer.level().addFreshEntity(swipe);
      }
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = this.getSpellPower(spellLevel, caster) * 0.5F;
      return baseDamage + this.getWeaponDamage(caster);
   }

   private float getWeaponDamage(LivingEntity caster) {
      return Utils.getWeaponDamage(caster);
   }

   private String getDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float weaponDamage = Utils.getWeaponDamage(caster);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, caster), 1);
         return damage + plus;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }

   private MobEffectInstance getEffect(ArrayList<MobEffectInstance> effects, MobEffect mobEffect) {
      for (MobEffectInstance effect : effects) {
         if (effect.getEffect().equals(mobEffect)) {
            return effect;
         }
      }

      return null;
   }
}
