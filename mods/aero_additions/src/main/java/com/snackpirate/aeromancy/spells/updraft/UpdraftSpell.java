package com.snackpirate.aeromancy.spells.updraft;

import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
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
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@AutoSpellConfig
public class UpdraftSpell extends AbstractSpell {
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(AASpells.Schools.WIND_RESOURCE)
      .setMaxLevel(7)
      .setCooldownSeconds(30.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.aero_additions.stun_duration", new Object[]{Utils.stringTruncation(getStunDuration(spellLevel) / 20.0F, 2)}),
         Component.translatable("ui.irons_spellbooks.max_victims", new Object[]{this.getRecastCount(spellLevel, caster)})
      );
   }

   public UpdraftSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 80;
   }

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("updraft");
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2 + spellLevel;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      boolean b = Utils.preCastTargetHelper(level, entity, playerMagicData, this, 64, 0.15F);
      if (b && playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
         PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
         if (recasts.hasRecastForSpell(this.getSpellId())) {
            RecastInstance instance = recasts.getRecastInstance(this.getSpellId());
            if (instance != null
               && instance.getCastData() instanceof MultiTargetEntityCastData targetingData
               && targetingData.getTargets().contains(targetEntityCastData.getTargetUUID())) {
               return false;
            }
         }
      }

      return b;
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
      level.playSound(null, origin.x, origin.y, origin.z, SoundRegistry.GUST_CAST, SoundSource.PLAYERS, 2.0F, 1.0F);
      if (castDataSerializable instanceof MultiTargetEntityCastData targetingData) {
         targetingData.getTargets().forEach(uuid -> {
            LivingEntity target = (LivingEntity)((ServerLevel)serverPlayer.level()).getEntity(uuid);
            if (target != null) {
               target.addDeltaMovement(new Vec3(0.0, 0.9, 0.0));
               target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
               UpdraftEntity updraft = new UpdraftEntity((EntityType<?>)AASpells.Entities.UPDRAFT_VISUAL_ENTITY.get(), level);
               updraft.setPos(target.getPosition(0.2F));
               updraft.tick();
               level.addFreshEntity(updraft);
            }
         });
      }
   }

   public ICastDataSerializable getEmptyCastData() {
      return new MultiTargetEntityCastData(new Entity[0]);
   }

   public static int getStunDuration(int spellLevel) {
      return 100 + 20 * spellLevel;
   }
}
