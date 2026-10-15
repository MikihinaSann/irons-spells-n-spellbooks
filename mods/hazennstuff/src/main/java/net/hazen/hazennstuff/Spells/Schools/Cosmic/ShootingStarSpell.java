package net.hazen.hazennstuff.Spells.Schools.Cosmic;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Entity.Spells.Astral.ShootingStar.ShootingStar;
import net.hazen.hazennstuff.Registries.HnSSchoolRegistry;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class ShootingStarSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "shooting_star");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(HnSSchoolRegistry.COSMIC_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(2.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public ShootingStarSpell() {
      this.manaCostPerLevel = 3;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 40;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)HnSSounds.STAR_CAST_1.get());
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

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.35F, false);
      return true;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 targetPos = null;
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData && level instanceof ServerLevel serverLevel) {
         targetPos = targetData.getTargetPosition(serverLevel);
      }

      if (targetPos == null) {
         HitResult raycast = Utils.raycastForEntity(level, entity, 45.0F, true);
         if (raycast.getType() == Type.ENTITY) {
            targetPos = ((EntityHitResult)raycast).getEntity().position();
         } else {
            targetPos = Utils.moveToRelativeGroundLevel(level, raycast.getLocation(), 5);
         }
      }

      if (targetPos != null) {
         for (int i = 0; i < 2; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * 5.0;
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 5.0;
            Vec3 spawnPos = targetPos.add(offsetX, 8.0, offsetZ);
            Vec3 trajectory = targetPos.subtract(spawnPos).normalize();
            ShootingStar star = new ShootingStar(level, entity);
            star.setPos(spawnPos);
            star.shoot(trajectory);
            star.setDamage(this.getDamage(spellLevel, entity));
            level.addFreshEntity(star);
         }

         super.onCast(level, spellLevel, entity, castSource, playerMagicData);
      }
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setIFrames(0);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 0.4F;
   }
}
