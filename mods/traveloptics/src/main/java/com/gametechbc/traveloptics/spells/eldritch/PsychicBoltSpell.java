package com.gametechbc.traveloptics.spells.eldritch;

import com.gametechbc.traveloptics.entity.spells.psychic_bolt.PsychicBoltProjectile;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MultiTargetEntityCastData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@AutoSpellConfig
public class PsychicBoltSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "psychic_bolt");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(40.0)
      .build();

   public PsychicBoltSpell() {
      super();
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 50;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.WARDEN_HEARTBEAT);
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 5;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 64, 0.15F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
         PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
         if (!recasts.hasRecastForSpell(this.getSpellId())) {
            recasts.addRecast(
               new RecastInstance(
                  this.getSpellId(),
                  spellLevel,
                  this.getRecastCount(spellLevel, entity),
                  80,
                  castSource,
                  new MultiTargetEntityCastData(new Entity[]{targetEntityCastData.getTarget((ServerLevel)level)})
               ),
               playerMagicData
            );
         } else {
            RecastInstance instance = recasts.getRecastInstance(this.getSpellId());
            if (instance != null && instance.getCastData() instanceof MultiTargetEntityCastData targetingData) {
               targetingData.addTarget(targetEntityCastData.getTargetUUID());
            }
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
      Level level = serverPlayer.level();
      Vec3 origin = serverPlayer.getEyePosition().add(serverPlayer.getForward().normalize().scale(0.2F));
      ScreenShake_Entity.ScreenShake(level, serverPlayer.position(), 4.0F, 0.02F, 5, 10);
      level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.WARDEN_ROAR, SoundSource.PLAYERS, 1.5F, 1.0F);
      if (castDataSerializable instanceof MultiTargetEntityCastData targetingData) {
         targetingData.getTargets().forEach(uuid -> {
            LivingEntity target = (LivingEntity)((ServerLevel)serverPlayer.level()).getEntity(uuid);
            if (target != null) {
               PsychicBoltProjectile boltProjectile = new PsychicBoltProjectile(level, serverPlayer);
               boltProjectile.setPos(origin.subtract(0.0, boltProjectile.getBbHeight(), 0.0));
               Vec3 vec = target.getBoundingBox().getCenter().subtract(serverPlayer.getEyePosition()).normalize();
               float inaccuracy = (float)Mth.clampedLerp(0.2F, 1.4F, target.position().distanceToSqr(serverPlayer.position()) / 1024.0);
               boltProjectile.shoot(vec.scale(0.75), inaccuracy);
               boltProjectile.setEffectDuration(this.getEffectDuration(recastInstance.getSpellLevel(), serverPlayer));
               boltProjectile.setHomingTarget(target);
               level.addFreshEntity(boltProjectile);
            }
         });
      }
   }

   private int getEffectDuration(int spellLevel, LivingEntity caster) {
      return (int)(100.0F + this.getSpellPower(spellLevel, caster) * 100.0F);
   }

   public ICastDataSerializable getEmptyCastData() {
      return new MultiTargetEntityCastData(new Entity[0]);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.recast", new Object[]{Utils.stringTruncation(5.0, 2)}),
         Component.translatable("ui.traveloptics.duration", new Object[]{Utils.timeFromTicks(this.getEffectDuration(spellLevel, caster), 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
