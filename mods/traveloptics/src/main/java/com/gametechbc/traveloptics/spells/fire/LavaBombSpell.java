package com.gametechbc.traveloptics.spells.fire;

import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

@AutoSpellConfig
public class LavaBombSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "lava_bomb");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(18.0)
      .build();

   public LavaBombSpell() {
      super();
      this.baseManaCost = 40;
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 50;
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
      return SpellAnimations.CHARGE_SPIT_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SPIT_FINISH_ANIMATION;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.MONSTROSITYGROWL.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.MONSTROSITYSHOOT.get());
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      int projectileCount = (int)this.getProjectileCount(spellLevel, caster);
      double motionScale = this.getMotionScale();
      return List.of(
         Component.translatable("ui.traveloptics.lava_bomb_projectile_count", new Object[]{projectileCount}),
         Component.translatable("ui.traveloptics.motion_scale", new Object[]{motionScale}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
      if (!level.isClientSide()) {
         int projectileCount = (int)this.getProjectileCount(spellLevel, entity);
         double angleBetween = projectileCount > 1 ? (Math.PI / 4) / (projectileCount - 1) : 0.0;

         for (int i = 0; i < projectileCount; i++) {
            double angle = projectileCount > 1 ? (-Math.PI / 8) + angleBetween * i : 0.0;
            Vec3 lookVec = entity.getLookAngle();
            double x = lookVec.x * Math.cos(angle) - lookVec.z * Math.sin(angle);
            double z = lookVec.x * Math.sin(angle) + lookVec.z * Math.cos(angle);
            double motionScale = this.getMotionScale();
            Vec3 motion = new Vec3(x, lookVec.y, z).normalize().scale(motionScale);
            EntityType<?> entityType = (EntityType<?>)ModEntities.LAVA_BOMB.get();
            Projectile projectile = (Projectile)entityType.create(level);
            if (projectile != null) {
               projectile.moveTo(entity.getX(), entity.getY() + 1.5, entity.getZ(), 0.0F, 0.0F);
               projectile.setDeltaMovement(motion.x(), motion.y(), motion.z());
               CameraShakeManager.addCameraShake(new CameraShakeData(18, entity.position(), 25.0F));
               level.addFreshEntity(projectile);
            }
         }

         int radius = 32;
         BlockPos pos = entity.blockPosition();
         int posX = pos.getX();
         int posY = pos.getY();
         int posZ = pos.getZ();
         int absorbedCount = 0;
         int maxAbsorbed = (int)this.getProjectileCount(spellLevel, entity);

         for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
               for (int dz = -radius; dz <= radius && absorbedCount < maxAbsorbed; dz++) {
                  BlockPos blockPos = new BlockPos(posX + dx, posY + dy, posZ + dz);
                  BlockState state = level.getBlockState(blockPos);
                  FluidState fluidState = level.getFluidState(blockPos);
                  Fluid fluid = fluidState.getType();
                  if (fluid == Fluids.LAVA && fluidState.isSource()) {
                     level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                     absorbedCount++;
                  }
               }
            }

            if (absorbedCount >= maxAbsorbed) {
               break;
            }
         }

         Vec3 lookVec = entity.getLookAngle();
         double offsetDistance = 1.0;
         Vec3 handPosition = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight() - 0.5, entity.getZ());
         Vec3 offsetPosition = handPosition.add(lookVec.scale(offsetDistance));
         ParticleOptions particleType = (ParticleOptions)ModParticle.TRAP_FLAME.get();
         MagicManager.spawnParticles(level, particleType, offsetPosition.x(), offsetPosition.y(), offsetPosition.z(), 1, 0.0, 0.0, 0.0, 0.0, true);
      }
   }

   private float getProjectileCount(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private double getMotionScale() {
      return 1.0;
   }
}
