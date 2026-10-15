package net.alshanex.alshanex_familiars.effects;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalData;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalEntity;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.alshanex.alshanex_familiars.util.FrozenUtils;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EndMayhemEffect extends MagicMobEffect {
   public EndMayhemEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 40 == 0;
   }

   public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
      int entityLimit = getmaxNumberOfEntities(pAmplifier);
      List<LivingEntity> entities = pLivingEntity.level
         .getEntitiesOfClass(
            LivingEntity.class,
            pLivingEntity.getBoundingBox().inflate(10.0, 4.0, 10.0),
            target -> !DamageSources.isFriendlyFireBetween(target, pLivingEntity) && target.distanceToSqr(pLivingEntity) < 100.0
         );
      if (!entities.isEmpty()) {
         for (LivingEntity livingTarget : entities.subList(0, Math.min(entities.size(), entityLimit) - 1)) {
            Random random = new Random();
            double theta = random.nextDouble() * 2.0 * Math.PI;
            double phi = random.nextDouble() * Math.PI / 2.0;
            double radius = 10.0;
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.cos(phi);
            double z = radius * Math.sin(phi) * Math.sin(theta);
            Vec3 portalPosition = livingTarget.position().add(x, y + livingTarget.getBbHeight() / 2.0F, z);
            Vec3 targetCenterPosition = livingTarget.position().add(0.0, livingTarget.getBbHeight() / 2.0F, 0.0);
            Vec3 directionToTarget = targetCenterPosition.subtract(portalPosition).normalize();
            float rotationX = (float)(-Math.toDegrees(Math.asin(directionToTarget.y)));
            float rotationY = (float)Math.toDegrees(Math.atan2(directionToTarget.z, directionToTarget.x)) - 90.0F;
            PortalData portalData = new PortalData();
            portalData.setPortalDuration(15);
            PortalEntity portalEntity = this.setupPortalEntity(pLivingEntity.level, portalData, pLivingEntity, portalPosition, rotationY, rotationX);
            pLivingEntity.level.addFreshEntity(portalEntity);
            AbstractMagicProjectile projectile = FrozenUtils.getMagicProjectile(pLivingEntity);
            projectile.setPos(portalPosition);
            projectile.setXRot(rotationX);
            projectile.setYRot(rotationY);
            projectile.shoot(directionToTarget);
            projectile.setDamage(getDamageFromAmplifier(pAmplifier, pLivingEntity));
            pLivingEntity.level.addFreshEntity(projectile);
         }
      }

      return super.applyEffectTick(pLivingEntity, pAmplifier);
   }

   private PortalEntity setupPortalEntity(Level level, PortalData portalData, LivingEntity owner, Vec3 spawnPos, float rotationY, float rotationX) {
      PortalEntity portalEntity = new PortalEntity(level, portalData);
      portalEntity.setOwnerUUID(owner.getUUID());
      portalEntity.moveTo(spawnPos);
      portalEntity.setYRot(rotationY);
      portalEntity.setXRot(rotationX);
      level.addFreshEntity(portalEntity);
      return portalEntity;
   }

   private static int getmaxNumberOfEntities(int effectAmplifier) {
      return effectAmplifier + 10;
   }

   public static float getDamageFromAmplifier(int effectAmplifier, @Nullable LivingEntity caster) {
      float power = caster == null ? 1.0F : PetSpellRegistry.MAYHEM.get().getEntityPowerMultiplier(caster);
      return (effectAmplifier - 7) * power + 7.0F;
   }
}
