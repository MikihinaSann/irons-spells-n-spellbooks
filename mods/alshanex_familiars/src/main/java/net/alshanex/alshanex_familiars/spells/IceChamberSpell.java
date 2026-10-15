package net.alshanex.alshanex_familiars.spells;

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
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.ray_of_frost.RayOfFrostVisualEntity;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.alshanex.alshanex_familiars.entity.misc.FrozenEntity;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.alshanex.alshanex_familiars.util.FrozenUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class IceChamberSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "ice_chamber");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.freeze_time", new Object[]{Utils.timeFromTicks(this.getFreezeTime(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(getRange(spellLevel, caster), 1)})
      );
   }

   public IceChamberSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
      this.castTime = 10;
      this.baseManaCost = 25;
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

   public boolean allowCrafting() {
      return false;
   }

   public boolean allowLooting() {
      return false;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.RAY_OF_FROST.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      HitResult hitResult = Utils.raycastForEntity(level, entity, getRange(spellLevel, entity), true, 0.15F);
      level.addFreshEntity(new RayOfFrostVisualEntity(level, entity.getEyePosition(), hitResult.getLocation(), entity));
      float radius = 3.0F;
      if (hitResult.getType() == Type.ENTITY) {
         Entity target = ((EntityHitResult)hitResult).getEntity();
         DamageSources.applyDamage(
            target,
            this.getDamage(spellLevel, entity),
            this.getDamageSource(entity).setFreezeTicks(target.getTicksRequiredToFreeze() + this.getFreezeTime(spellLevel, entity))
         );
         if (target instanceof LivingEntity livingEntity
            && !DamageSources.isFriendlyFireBetween(target, entity)
            && (livingEntity.getHealth() < livingEntity.getMaxHealth() * 0.1 || livingEntity.getHealth() <= this.getDamage(spellLevel, entity))
            && !(livingEntity instanceof Player)
            && livingEntity.getType().is(AlshanexFamiliarsTags.FREEZING_WHITELIST)) {
            FrozenEntity frozenEntity = FrozenEntity.buildFrozenEntity(livingEntity, entity);
            frozenEntity.absMoveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.getYRot(), livingEntity.getXRot());
            frozenEntity.yBodyRot = livingEntity.getYRot();
            frozenEntity.setShatterDamage(this.getDamage(spellLevel, entity));
            frozenEntity.setDeathTimer(200);
            livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            livingEntity.kill();
            livingEntity.remove(RemovalReason.KILLED);
            level.addFreshEntity(frozenEntity);
         }

         FrozenUtils.freezeSurroundingBlocks(target.blockPosition(), radius, level);
      } else if (hitResult.getType() == Type.BLOCK) {
         BlockPos center = new BlockPos((int)hitResult.getLocation().x, (int)hitResult.getLocation().y, (int)hitResult.getLocation().z);
         FrozenUtils.freezeSurroundingBlocks(center, radius, level);
      }

      MagicManager.spawnParticles(
         level, ParticleHelper.SNOWFLAKE, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 50, 0.0, 0.0, 0.0, 0.3, false
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public static float getRange(int level, LivingEntity caster) {
      return 30.0F;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 1.5F;
   }

   private int getFreezeTime(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 15.0F);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CHARGED_CAST;
   }
}
