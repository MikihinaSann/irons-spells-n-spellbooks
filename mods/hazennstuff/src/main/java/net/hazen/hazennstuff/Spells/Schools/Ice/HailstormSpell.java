package net.hazen.hazennstuff.Spells.Schools.Ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
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
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkA.IceChunkA;
import net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkB.IceChunkB;
import net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkC.IceChunkC;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HailstormSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "hailstorm");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(16.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(caster), 1)})
      );
   }

   public HailstormSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
      this.castTime = 160;
      this.baseManaCost = 5;
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.ENDER_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!(playerMagicData.getAdditionalCastData() instanceof HailstormSpell.StarfallCastData)) {
         Vec3 targetArea = Utils.moveToRelativeGroundLevel(world, Utils.raycastForEntity(world, entity, 40.0F, true).getLocation(), 12);
         playerMagicData.setAdditionalCastData(new HailstormSpell.StarfallCastData(targetArea));
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (playerMagicData != null && playerMagicData.getAdditionalCastData() instanceof HailstormSpell.StarfallCastData castData) {
         float var16 = this.getRadius(entity);
         int tick = playerMagicData.getCastDurationRemaining() - 1;
         if (tick % 20 == 0) {
            castData.updateTrackedEntities(
               level.getEntities(
                  entity,
                  AABB.ofSize(castData.center, var16 * 3.0F, var16, var16 * 3.0F),
                  e -> e instanceof LivingEntity && !DamageSources.isFriendlyFireBetween(entity, e)
               )
            );
         }

         if (tick % 4 == 0) {
            for (int i = 0; i < 2; i++) {
               Vec3 center = castData.center;
               Vec3 weightedArea = Vec3.ZERO;

               for (Entity target : castData.trackedEntities) {
                  weightedArea = weightedArea.add(target.position().subtract(center).scale(1.0F / castData.trackedEntities.size()));
               }

               double spawnRadius = Mth.clampedLerp(var16, var16 * 0.5F, weightedArea.length() / var16);
               Vec3 spawnTarget = Utils.moveToRelativeGroundLevel(
                     level,
                     center.add(weightedArea)
                        .add(new Vec3(0.0, 0.0, entity.getRandom().nextFloat() * spawnRadius).yRot(entity.getRandom().nextInt(360) * (float) (Math.PI / 180.0))),
                     3
                  )
                  .add(0.0, 0.5, 0.0);
               Vec3 trajectory = new Vec3(0.15F, -0.85F, 0.0).normalize();
               Vec3 spawn = Utils.raycastForBlock(level, spawnTarget, spawnTarget.add(trajectory.scale(-12.0)), Fluid.NONE).getLocation().add(trajectory);
               int choice = entity.getRandom().nextInt(3);
               switch (choice) {
                  case 0:
                     this.shootIceChunkA(level, spellLevel, entity, spawn, trajectory);
                     break;
                  case 1:
                     this.shootIceChunkB(level, spellLevel, entity, spawn, trajectory);
                     break;
                  case 2:
                     this.shootIceChunkC(level, spellLevel, entity, spawn, trajectory);
               }

               MagicManager.spawnParticles(level, HnSParticleHelper.ICE_CLOUD, spawn.x, spawn.y, spawn.z, 1, 1.0, 1.0, 1.0, 1.0, false);
               MagicManager.spawnParticles(level, HnSParticleHelper.ICE_CLOUD, spawn.x, spawn.y, spawn.z, 1, 1.0, 1.0, 1.0, 1.0, true);
            }
         }
      }
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.5F;
   }

   private float getRadius(LivingEntity caster) {
      return 6.0F;
   }

   public void shootIceChunkA(Level world, int spellLevel, LivingEntity entity, Vec3 spawn, Vec3 trajectory) {
      IceChunkA iceChunkA = new IceChunkA(world, entity);
      iceChunkA.setPos(spawn.add(-1.0, 0.0, 0.0));
      iceChunkA.shoot(trajectory, 0.1F);
      iceChunkA.setDamage(this.getDamage(spellLevel, entity));
      iceChunkA.setExplosionRadius(2.0F);
      world.addFreshEntity(iceChunkA);
      world.playSound(
         (Player)null, spawn.x, spawn.y, spawn.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 3.0F, 0.7F + Utils.random.nextFloat() * 0.3F
      );
   }

   public void shootIceChunkB(Level world, int spellLevel, LivingEntity entity, Vec3 spawn, Vec3 trajectory) {
      IceChunkB iceChunkB = new IceChunkB(world, entity);
      iceChunkB.setPos(spawn.add(-1.0, 0.0, 0.0));
      iceChunkB.shoot(trajectory, 0.15F);
      iceChunkB.setDamage(this.getDamage(spellLevel, entity));
      world.addFreshEntity(iceChunkB);
      world.playSound(
         (Player)null, spawn.x, spawn.y, spawn.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 3.0F, 0.7F + Utils.random.nextFloat() * 0.3F
      );
   }

   public void shootIceChunkC(Level world, int spellLevel, LivingEntity entity, Vec3 spawn, Vec3 trajectory) {
      IceChunkC iceChunkC = new IceChunkC(world, entity);
      iceChunkC.setPos(spawn.add(-1.0, 0.0, 0.0));
      iceChunkC.shoot(trajectory, 0.075F);
      iceChunkC.setDamage(this.getDamage(spellLevel, entity));
      iceChunkC.setExplosionRadius(4.0F);
      world.addFreshEntity(iceChunkC);
      world.playSound(
         (Player)null, spawn.x, spawn.y, spawn.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 3.0F, 0.7F + Utils.random.nextFloat() * 0.3F
      );
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CONTINUOUS_OVERHEAD;
   }

   public SpellDamageSource getDamageSource(@javax.annotation.Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setFreezeTicks(80);
   }

   public static class StarfallCastData implements ICastData {
      Vec3 center;
      final List<Entity> trackedEntities = new ArrayList<>();

      public StarfallCastData(Vec3 center) {
         this.center = center;
      }

      public void reset() {
         this.trackedEntities.clear();
      }

      public void updateTrackedEntities(List<Entity> entities) {
         this.trackedEntities.clear();
         this.trackedEntities.addAll(entities);
      }
   }
}
