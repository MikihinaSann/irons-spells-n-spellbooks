package net.warphan.iss_magicfromtheeast.spells.spirit;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.spells.MFTESpellAnimations;

@AutoSpellConfig
public class CalamityCutSpell extends AbstractSpell {
   private final ResourceLocation spellID = new ResourceLocation("iss_magicfromtheeast", "calamity_cut");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(MFTESchoolRegistries.SPIRIT_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(25.0)
      .build();

   public CalamityCutSpell() {
      this.manaCostPerLevel = 18;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 4;
      this.castTime = 24;
      this.baseManaCost = 30;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}),
         Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getRange(spellLevel, caster), 1)})
      );
   }

   public boolean canBeInterrupted(Player player) {
      return false;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public ResourceLocation getSpellResource() {
      return this.spellID;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.KATANA_WIND_UP.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.SWORD_STRIKE.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = 0.5F;
      Vec3 forward = entity.getForward().multiply(1.0, 0.0, 1.0).normalize();
      Vec3 start = entity.getEyePosition().subtract(0.0, 0.5, 0.0).add(forward.scale(1.5));
      float count = this.getRange(spellLevel, entity);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 10, entity.position(), 10.0F));

      for (int i = 0; i < count; i++) {
         Vec3 strikeLocation = start.add(forward.scale(i));
         Vec3 particleLocation = level.clip(
               new ClipContext(strikeLocation, strikeLocation.add(0.0, -2.0, 0.0), Block.VISUAL, Fluid.NONE, CollisionContext.empty())
            )
            .getLocation()
            .add(0.0, 0.1, 0.0);
         MagicManager.spawnParticles(level, ParticleTypes.SCULK_SOUL, particleLocation.x, particleLocation.y, particleLocation.z, 8, 0.0, 0.0, 0.0, 1.0, false);
         List<Entity> entities = level.getEntities(entity, AABB.ofSize(strikeLocation, radius, radius * 3.0F, radius));
         SpellDamageSource damageSource = this.getDamageSource(entity);

         for (Entity targetEntity : entities) {
            if (targetEntity.isAlive()
               && targetEntity.isPickable()
               && Utils.hasLineOfSight(level, strikeLocation.add(0.0, 1.0, 0.0), targetEntity.getBoundingBox().getCenter(), true)
               && DamageSources.applyDamage(targetEntity, this.getDamage(spellLevel, entity), damageSource)) {
               EnchantmentHelper.doPostAttackEffects((ServerLevel)level, targetEntity, damageSource);
            }
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getRange(int spellLevel, LivingEntity entity) {
      return 9 + spellLevel * 2;
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) + this.getAdditionalDamage(entity);
   }

   private float getAdditionalDamage(LivingEntity entity) {
      return entity == null ? 0.0F : Utils.getWeaponDamage(entity);
   }

   private String getDamageText(int spellLevel, LivingEntity entity) {
      if (entity != null) {
         float weaponDamage = this.getAdditionalDamage(entity);
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
      return MFTESpellAnimations.ANIMATION_KATANA_STRIKE_ACTION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }
}
