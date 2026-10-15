package net.alshanex.alshanex_familiars.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.network.particles.ShockwaveParticlesPacket;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.alshanex.alshanex_familiars.entity.misc.FrozenEntity;
import net.alshanex.alshanex_familiars.util.ASpellAnimations;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.alshanex.alshanex_familiars.util.FrozenUtils;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class IceAgeSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "ice_age");
   private int initialRadius = 0;
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 2)})
      );
   }

   public IceAgeSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 1;
      this.castTime = 55;
      this.baseManaCost = 5;
   }

   public int getCastTime(int spellLevel) {
      return this.castTime + 5 * spellLevel;
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
      return Optional.of((SoundEvent)SoundRegistry.CONE_OF_COLD_LOOP.get());
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 10 == 0) {
         this.initialRadius += 2;
         if (this.initialRadius <= this.getRadius(spellLevel, entity)) {
            this.freezeAround(level, spellLevel, entity, this.initialRadius);
         }
      }

      if (playerMagicData != null && playerMagicData.getCastDurationRemaining() + 1 > 20 && (playerMagicData.getCastDurationRemaining() + 1) % 20 == 0) {
         Vec3 targetArea = entity.position();
         level.playSound(
            null,
            targetArea.x,
            targetArea.y,
            targetArea.z,
            (SoundEvent)SoundRegistry.ICE_BLOCK_CAST.get(),
            SoundSource.PLAYERS,
            2.0F,
            Utils.random.nextIntBetweenInclusive(8, 12) * 0.1F
         );
      }
   }

   public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
      this.initialRadius = 0;
      super.onServerCastComplete(level, spellLevel, entity, playerMagicData, cancelled);
   }

   public void freezeAround(Level level, int spellLevel, LivingEntity entity, int radius) {
      float particleRadius = entity.getBbWidth() + 1.0F;
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.ICE.get()).getTargetingColor(), particleRadius),
         entity.getX(),
         entity.getY() + 0.165F,
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(
         entity,
         new ShockwaveParticlesPacket(
            new Vec3(entity.getX(), entity.getY() + 0.165F, entity.getZ()), radius, (ParticleType)ParticleRegistry.SNOWFLAKE_PARTICLE.get()
         ),
         new CustomPacketPayload[0]
      );
      level.getEntities(
            entity,
            entity.getBoundingBox().inflate(radius, 4.0, radius),
            target -> !DamageSources.isFriendlyFireBetween(target, entity) && Utils.hasLineOfSight(level, entity, target, true)
         )
         .forEach(
            target -> {
               if (target instanceof LivingEntity livingEntity
                  && livingEntity.distanceToSqr(entity) < radius * radius
                  && !DamageSources.isFriendlyFireBetween(target, entity)
                  && !(target instanceof FrozenEntity)) {
                  DamageSources.applyDamage(livingEntity, this.getDamage(spellLevel, entity), this.getDamageSource(entity));
                  MagicManager.spawnParticles(
                     level,
                     ParticleHelper.SNOWFLAKE,
                     livingEntity.getX(),
                     livingEntity.getY() + livingEntity.getBbHeight() * 0.5F,
                     livingEntity.getZ(),
                     50,
                     livingEntity.getBbWidth() * 0.5F,
                     livingEntity.getBbHeight() * 0.5F,
                     livingEntity.getBbWidth() * 0.5F,
                     0.03,
                     false
                  );
                  if ((livingEntity.getHealth() < livingEntity.getMaxHealth() * 0.1 || livingEntity.getHealth() <= this.getDamage(spellLevel, entity))
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
               }
            }
         );
      FrozenUtils.freezeSurroundingBlocks(entity.blockPosition(), radius, level);
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setFreezeTicks(80);
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return 1.0F + this.getSpellPower(spellLevel, caster) * 0.4F;
   }

   public float getRadius(int spellLevel, LivingEntity caster) {
      return 6.0F + spellLevel * 2 * 0.75F;
   }

   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      return mob.distanceToSqr(target) > 120.0;
   }

   public AnimationHolder getCastStartAnimation() {
      return ASpellAnimations.TOUCH_GROUND_ANIMATION;
   }
}
