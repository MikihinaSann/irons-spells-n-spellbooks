package net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal;

import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AbyssalSlashSpell extends AbstractAbyssalSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "abyssal_slash");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(CSSchoolRegistry.ABYSSAL_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(40.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}));
   }

   public AbyssalSlashSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 3;
      this.castTime = 15;
      this.baseManaCost = 35;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.FLAMING_STRIKE_UPSWING.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.ABYSS_BLAST_ONLY_SHOOT.get());
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {
      super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);
      this.spawnParticles(entity);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = 4.1F;
      float distance = 2.0F;
      Vec3 slashLocation = entity.position().add(entity.getForward().multiply(distance, 0.3F, distance));
      this.spawnParticles(entity);

      for (Entity targetEntity : level.getEntities(entity, AABB.ofSize(slashLocation, radius * 2.0F, radius, radius * 2.0F))) {
         if (entity.isPickable()
            && entity.distanceToSqr(targetEntity) < radius * radius
            && Utils.hasLineOfSight(level, entity.getEyePosition(), targetEntity.getBoundingBox().getCenter(), true)
            && DamageSources.applyDamage(targetEntity, this.getDamage(spellLevel, entity), this.getDamageSource(entity))) {
            if (targetEntity instanceof LivingEntity livingTarget) {
               Boolean hasBurn = livingTarget.hasEffect(ModEffect.EFFECTABYSSAL_BURN);
               Boolean hasFear = livingTarget.hasEffect(ModEffect.EFFECTABYSSAL_FEAR);
               Boolean hasCurse = livingTarget.hasEffect(ModEffect.EFFECTABYSSAL_CURSE);
               if (hasCurse || hasBurn || hasFear) {
                  float bonus = 0.2F;
                  float bonusDamage = this.getDamage(spellLevel, entity) * bonus;
                  float totalDamage = this.getDamage(spellLevel, entity) + bonusDamage;
                  DamageSources.applyDamage(livingTarget, totalDamage, this.getDamageSource(entity));
               }
            }

            EnchantmentHelper.doPostAttackEffects((ServerLevel)level, targetEntity, this.getDamageSource(entity));
         }
      }

      CameraShakeManager.addCameraShake(new CameraShakeData(10, entity.position(), 10.0F));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) + Utils.getWeaponDamage(caster);
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

   private void spawnParticles(LivingEntity entity) {
      ServerLevel level = (ServerLevel)entity.level();
      level.sendParticles((SimpleParticleType)ModParticle.SHOCK_WAVE.get(), entity.getX(), entity.getY() + 1.0, entity.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ONE_HANDED_HORIZONTAL_SWING_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }
}
