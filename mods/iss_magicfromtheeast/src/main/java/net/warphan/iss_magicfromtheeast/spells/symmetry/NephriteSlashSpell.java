package net.warphan.iss_magicfromtheeast.spells.symmetry;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.entity.spells.nephrite_slash.NephriteCrystalEntity;
import net.warphan.iss_magicfromtheeast.particle.JadeSlashParticleOptions;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.util.MFTEParticleHelper;

@AutoSpellConfig
public class NephriteSlashSpell extends AbstractSpell {
   private final ResourceLocation spellID = ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "nephrite_slash");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}));
   }

   public NephriteSlashSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 40;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellID;
   }

   public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
      super.onClientCast(level, spellLevel, entity, castData);
      entity.setYBodyRot(entity.getYRot());
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.JADE_SLASH.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = 0.5F;
      Vec3 forward = entity.getForward().multiply(1.0, 0.0, 1.0).normalize();
      Vec3 start = entity.getEyePosition().subtract(0.0, 0.5, 0.0).add(forward.scale(1.5));
      float count = 4.0F;

      for (int i = 0; i < count; i++) {
         Vec3 hitLocation = start.add(forward.scale(i));
         List<Entity> entities = level.getEntities(entity, AABB.ofSize(hitLocation, radius, radius * 5.0F, radius));
         SpellDamageSource damageSource = this.getDamageSource(entity);

         for (Entity targetEntity : entities) {
            if (targetEntity.isAlive()
               && targetEntity.isPickable()
               && Utils.hasLineOfSight(level, hitLocation.add(0.0, 1.0, 0.0), targetEntity.getBoundingBox().getCenter(), true)
               && DamageSources.applyDamage(targetEntity, this.getDamage(spellLevel, entity), damageSource)) {
               targetEntity.invulnerableTime = 0;
               MagicManager.spawnParticles(
                  level,
                  MFTEParticleHelper.JADE_SHATTER,
                  targetEntity.getX(),
                  targetEntity.getY() + targetEntity.getBbHeight() * 0.5F,
                  targetEntity.getZ(),
                  5,
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

      float damage = this.getDamage(spellLevel, entity);
      float minScale = 1.2F;
      float maxScale = 3.0F;
      int crystalCount = 8;
      start = Utils.moveToRelativeGroundLevel(level, start, 1, 3).add(0.0, 0.1, 0.0);
      float distanceCovered = 0.0F;

      for (int i = 0; i < crystalCount; i++) {
         float f = Math.max((float)i / crystalCount, (distanceCovered + 1.0F) / crystalCount);
         float scale = Mth.lerp(f, minScale, maxScale);
         Vec3 spawn = start.add(forward.scale(i * 0.75));
         Vec3 ground = Utils.moveToRelativeGroundLevel(level, spawn, 8);
         spawn = ground.subtract(spawn).scale(Mth.clamp(i / 3.0F, 0.0F, 1.0F)).add(spawn);
         boolean isBiggestCrystal = i == crystalCount - 1 || distanceCovered + 1.0F > crystalCount;
         if (isBiggestCrystal) {
            scale = maxScale * 1.2F;
         }

         distanceCovered += (float)forward.horizontalDistance();
         if (level.getBlockState(BlockPos.containing(spawn).below()).isFaceSturdy(level, BlockPos.containing(spawn).below(), Direction.UP)) {
            NephriteCrystalEntity crystal = new NephriteCrystalEntity(level, entity);
            if (i % 3 == 0) {
               crystal.setSilent(true);
            }

            crystal.setCrystalSize(scale);
            crystal.moveTo(spawn.add(0.0, 0.0, 0.0));
            crystal.setWaitTime(i);
            crystal.setDamage(damage * (isBiggestCrystal ? 0.75F : 0.3F));
            crystal.setYRot(entity.getYRot() - 45.0F + Utils.random.nextIntBetweenInclusive(-20, 20));
            crystal.setXRot(Utils.random.nextIntBetweenInclusive(-15, 15));
            level.addFreshEntity(crystal);
         }

         if (isBiggestCrystal) {
            break;
         }
      }

      Vec3 up = new Vec3(0.0, 1.0, 0.0);
      if (forward.dot(up) > 0.999) {
         up = new Vec3(1.0, 0.0, 0.0);
      }

      Vec3 right = up.cross(forward);
      Vec3 particlePos = start.add(forward.scale(2.5)).subtract(forward.scale(4.0)).add(right.scale(-0.3));
      MagicManager.spawnParticles(
         level,
         new JadeSlashParticleOptions((float)forward.x, (float)forward.y, (float)forward.z, (float)right.x, (float)right.y, (float)right.z, 1.0F),
         particlePos.x,
         particlePos.y + 0.3,
         particlePos.z,
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
      return this.getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity);
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
      return SpellAnimations.ONE_HANDED_VERTICAL_UPSWING_ANIMATION;
   }
}
