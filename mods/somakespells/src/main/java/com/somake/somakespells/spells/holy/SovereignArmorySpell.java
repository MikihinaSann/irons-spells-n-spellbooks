package com.somake.somakespells.spells.holy;

import com.somake.somakespells.entity.SovereignArmoryPortalEntity;
import com.somake.somakespells.entity.SovereignArmoryProjectileEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.RaycastBuilder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.entity.PartEntity;

public class SovereignArmorySpell extends AbstractSpell {
   private static final float TARGET_RANGE = 48.0F;
   private static final int FIRST_LAUNCH_DELAY = 18;
   private static final int BASE_LAUNCH_INTERVAL = 7;
   private static final int SECOND_SHOT_DELAY_TICKS = 40;
   private static final int SECOND_PROJECTILE_LAUNCH_DELAY = 1;
   private static final int PORTAL_LINGER_TICKS = 30;
   private static final float BASE_PORTAL_RADIUS = 6.2F;
   private static final float MIN_PORTAL_SPACING = 3.0F;
   private static final int POSITION_ATTEMPTS = 96;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "sovereign_armory");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(24.0)
      .build();

   public SovereignArmorySpell() {
      this.baseManaCost = 50;
      this.manaCostPerLevel = 8;
      this.baseSpellPower = 7;
      this.spellPowerPerLevel = 1;
      this.castTime = 20;
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
      return Optional.of(SoundEvents.BEACON_POWER_SELECT);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.AMETHYST_BLOCK_RESONATE);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable(
            "ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(this.getTotalProjectileCount(spellLevel, caster), 0)}
         ),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getAttackDurationTicks(spellLevel, caster), 1)})
      );
   }

   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setIFrames(0);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         this.spawnArmory(serverLevel, spellLevel, caster);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private void spawnArmory(ServerLevel level, int spellLevel, LivingEntity caster) {
      HitResult hitResult = RaycastBuilder.begin(level, caster)
         .range(48.0F)
         .checkForBlocks(true)
         .bbInflation(0.4F)
         .filter(
            entity -> {
               Entity candidate = normalizeTarget(entity);
               return candidate instanceof LivingEntity living
                  && living.isAlive()
                  && candidate != caster
                  && Utils.canHitWithRaycast(entity)
                  && !DamageSources.isFriendlyFireBetween(caster, candidate);
            }
         )
         .build();
      LivingEntity lockedTarget = null;
      if (hitResult instanceof EntityHitResult entityHitResult
         && normalizeTarget(entityHitResult.getEntity()) instanceof LivingEntity living
         && living.isAlive()
         && !DamageSources.isFriendlyFireBetween(caster, living)) {
         lockedTarget = living;
      }

      Vec3 aimPoint = lockedTarget != null ? lockedTarget.getBoundingBox().getCenter() : hitResult.getLocation();
      int portalCount = this.getPortalCount(spellLevel, caster);
      int launchInterval = this.getLaunchInterval(spellLevel, caster);
      float portalRadius = this.getPortalRadius(spellLevel, caster);
      int lastSecondLaunchTick = 18 + (portalCount - 1) * launchInterval + 40 + 1;
      int portalCloseDelay = lastSecondLaunchTick + 30;
      this.spawnPortalField(
         level, caster, lockedTarget, aimPoint, this.getDamage(spellLevel, caster), portalCount, launchInterval, portalRadius, portalCloseDelay
      );
   }

   private void spawnPortalField(
      ServerLevel level,
      LivingEntity caster,
      LivingEntity lockedTarget,
      Vec3 aimPoint,
      float damage,
      int projectileCount,
      int launchInterval,
      float portalRadius,
      int portalCloseDelay
   ) {
      Vec3 look = caster.getLookAngle();
      Vec3 forward = new Vec3(look.x, 0.0, look.z);
      if (forward.lengthSqr() < 1.0E-4) {
         Vec3 fallback = Vec3.directionFromRotation(0.0F, caster.getYHeadRot());
         forward = new Vec3(fallback.x, 0.0, fallback.z);
      }

      forward = forward.normalize();
      Vec3 backward = forward.scale(-1.0);
      Vec3 right = new Vec3(forward.z, 0.0, -forward.x);
      Vec3 fieldOrigin = caster.position().add(0.0, caster.getBbHeight() + 0.75, 0.0);
      List<Vec3> portalPositions = createRandomPortalPositions(level, caster, fieldOrigin, backward, right, portalRadius, projectileCount);

      for (int index = 0; index < projectileCount; index++) {
         Vec3 spawnPosition = portalPositions.get(index);
         Vec3 direction = aimPoint.subtract(spawnPosition);
         if (direction.lengthSqr() < 1.0E-6) {
            direction = caster.getLookAngle();
         }

         direction = direction.normalize();
         float yaw = (float)(Mth.atan2(direction.x, direction.z) * 180.0F / (float)Math.PI);
         float pitch = (float)(Mth.atan2(direction.y, direction.horizontalDistance()) * 180.0F / (float)Math.PI);
         int launchDelay = 18 + index * launchInterval;
         SovereignArmoryPortalEntity portal = new SovereignArmoryPortalEntity(level, spawnPosition, yaw, pitch, portalCloseDelay);
         portal.configureSecondShot(caster, lockedTarget, aimPoint, damage, launchDelay + 40, 1, (index + 1) % 3);
         SovereignArmoryProjectileEntity projectile = new SovereignArmoryProjectileEntity(
            level, caster, spawnPosition, aimPoint, lockedTarget, damage, launchDelay, index % 3
         );
         level.addFreshEntity(portal);
         level.addFreshEntity(projectile);
      }
   }

   private static List<Vec3> createRandomPortalPositions(
      ServerLevel level, LivingEntity caster, Vec3 fieldOrigin, Vec3 backward, Vec3 right, float portalRadius, int projectileCount
   ) {
      RandomSource random = caster.getRandom();
      Vec3 obstructionOrigin = caster.getBoundingBox().getCenter();
      List<Vec3> positions = new ArrayList<>(projectileCount);
      double verticalSpread = 5.2 + portalRadius * 0.32;

      for (int index = 0; index < projectileCount; index++) {
         Vec3 bestCandidate = null;
         double bestSpacingSqr = -1.0;

         for (int attempt = 0; attempt < 96; attempt++) {
            double expansion = 1.0 + 0.35 * attempt / 95.0;
            double lateralOffset = randomBetween(random, -portalRadius * expansion, portalRadius * expansion);
            double verticalOffset = randomBetween(random, 0.35, verticalSpread * expansion);
            double depthOffset = randomBetween(random, 2.4, 5.2 + portalRadius * 0.12);
            Vec3 desiredPosition = fieldOrigin.add(right.scale(lateralOffset)).add(backward.scale(depthOffset)).add(0.0, verticalOffset, 0.0);
            Vec3 candidate = resolvePortalObstruction(level, obstructionOrigin, desiredPosition, caster);
            double spacingSqr = nearestPortalSpacingSqr(candidate, positions, right);
            if (spacingSqr > bestSpacingSqr) {
               bestSpacingSqr = spacingSqr;
               bestCandidate = candidate;
            }

            if (spacingSqr >= 9.0) {
               bestCandidate = candidate;
               break;
            }
         }

         positions.add(bestCandidate != null ? bestCandidate : fieldOrigin.add(backward.scale(3.0)));
      }

      return positions;
   }

   private static double nearestPortalSpacingSqr(Vec3 candidate, List<Vec3> positions, Vec3 right) {
      if (positions.isEmpty()) {
         return Double.POSITIVE_INFINITY;
      } else {
         double nearestSqr = Double.POSITIVE_INFINITY;

         for (Vec3 existing : positions) {
            Vec3 delta = candidate.subtract(existing);
            double lateralDistance = delta.dot(right);
            double verticalDistance = delta.y;
            nearestSqr = Math.min(nearestSqr, lateralDistance * lateralDistance + verticalDistance * verticalDistance);
         }

         return nearestSqr;
      }
   }

   private static double randomBetween(RandomSource random, double minimum, double maximum) {
      return minimum + random.nextDouble() * (maximum - minimum);
   }

   private static Vec3 resolvePortalObstruction(ServerLevel level, Vec3 origin, Vec3 desiredPosition, Entity context) {
      BlockHitResult hit = level.clip(new ClipContext(origin, desiredPosition, Block.COLLIDER, Fluid.NONE, context));
      if (hit.getType() != Type.BLOCK) {
         return desiredPosition;
      } else {
         Vec3 path = desiredPosition.subtract(origin);
         return path.lengthSqr() < 1.0E-6 ? origin : hit.getLocation().subtract(path.normalize().scale(0.35));
      }
   }

   private static Entity normalizeTarget(Entity entity) {
      return entity instanceof PartEntity<?> partEntity ? partEntity.getParent() : entity;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.75F + 1.0F;
   }

   private int getPortalCount(int spellLevel, LivingEntity caster) {
      int powerBonus = Mth.floor(Math.max(0.0F, this.getSpellPower(spellLevel, caster) - this.getSpellPower(spellLevel, null)) / 4.0F);
      return Mth.clamp(5 + spellLevel + powerBonus, 6, 16);
   }

   private int getTotalProjectileCount(int spellLevel, LivingEntity caster) {
      return this.getPortalCount(spellLevel, caster) * 2;
   }

   private int getLaunchInterval(int spellLevel, LivingEntity caster) {
      float spellPower = this.getSpellPower(spellLevel, caster);
      return 7 + Mth.clamp(Mth.floor(spellPower / 12.0F), 0, 3);
   }

   private float getPortalRadius(int spellLevel, LivingEntity caster) {
      float spellPower = this.getSpellPower(spellLevel, caster);
      return Mth.clamp(6.2F + (spellLevel - 1) * 0.4F + spellPower * 0.04F, 6.2F, 10.5F);
   }

   private int getAttackDurationTicks(int spellLevel, LivingEntity caster) {
      int portalCount = this.getPortalCount(spellLevel, caster);
      return 18 + (portalCount - 1) * this.getLaunchInterval(spellLevel, caster) + 40 + 1 + 30;
   }
}
