package com.snackpirate.aeromancy.spells;

import com.snackpirate.aeromancy.Aeromancy;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@AutoSpellConfig
public class TelelinkSpell extends AbstractSpell {
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(30.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.max_victims", new Object[]{this.getRecastCount(spellLevel, caster)}));
   }

   public TelelinkSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 80;
   }

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("telelink");
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
      if (castDataSerializable instanceof MultiTargetEntityCastData targetingData) {
         serverPlayer.addEffect(new MobEffectInstance(AASpells.MobEffects.TELELINKED, this.getEffectDuration(recastInstance.getSpellLevel(), serverPlayer)));
         targetingData.getTargets().forEach(uuid -> {
            LivingEntity target = (LivingEntity)((ServerLevel)serverPlayer.level()).getEntity(uuid);
            if (target != null) {
               target.addEffect(new MobEffectInstance(AASpells.MobEffects.TELELINKED, this.getEffectDuration(recastInstance.getSpellLevel(), serverPlayer)));
            }
         });
      }
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 1 + spellLevel;
   }

   public int getEffectDuration(int spellLevel, LivingEntity entity) {
      return (int)((3 * spellLevel * this.getSpellPower(spellLevel, entity) + 12.0F) * 20.0F);
   }

   public ICastDataSerializable getEmptyCastData() {
      return new MultiTargetEntityCastData(new Entity[0]);
   }
}
