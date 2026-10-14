package com.gametechbc.traveloptics.spells.ice;

import com.gametechbc.traveloptics.api.particle.ConeInwardParticleManager;
import com.gametechbc.traveloptics.api.spells.AbstractWeaponSpell;
import com.gametechbc.traveloptics.data_manager.SpiritPointsManager;
import com.gametechbc.traveloptics.init.TOItems;
import com.gametechbc.traveloptics.init.TOParticles;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

@AutoSpellConfig
public class CursedBlastSpell extends AbstractWeaponSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "cursed_blast");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(3.0)
      .build();

   public CursedBlastSpell() {
      super();
      this.manaCostPerLevel = 0;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 0;
      this.castTime = 28;
      this.baseManaCost = 200;
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
      return TOSpellAnimations.GUNBLADE_CHARGE;
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.GUNBLADE_SHOOT;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)TOSounds.CURSED_BLAST_CHARGE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      List<Item> allowedWeapons = List.of((Item)TOItems.CURSED_WRAITHBLADE.get());
      if (allowedWeapons.contains(entity.getMainHandItem().getItem())) {
         return true;
      } else {
         if (entity instanceof Player player && !level.isClientSide()) {
            player.displayClientMessage(Component.translatable("spell.traveloptics.cursed_blast.warning").withStyle(ChatFormatting.RED), true);
         }

         return false;
      }
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      ConeInwardParticleManager.spawnInwardConeParticles(level, entity, 2.0, 1, -0.35, 0.5, 0.8, (ParticleOptions)ModParticle.SMALL_CURSED_FLAME.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float soulConversion = this.getSoulConversionRate(spellLevel, entity);
      int spiritPoints = SpiritPointsManager.getSpiritPoints(entity.getMainHandItem());
      HitResult hitResult = Utils.raycastForEntity(level, entity, getRange(), true, 0.15F);
      if (hitResult.getType() == Type.ENTITY) {
         Entity target = ((EntityHitResult)hitResult).getEntity();
         if (target instanceof LivingEntity livingTarget) {
            float scaledDamage = this.getDamage() + spiritPoints * soulConversion;
            DamageSources.applyDamage(livingTarget, scaledDamage, this.getDamageSource(entity));
            AABB area = new AABB(target.blockPosition()).inflate(3.0);

            for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != entity && e != livingTarget)) {
               DamageSources.applyDamage(nearby, scaledDamage, this.getDamageSource(entity));
               MagicManager.spawnParticles(level, ParticleTypes.SCULK_SOUL, nearby.getX(), nearby.getY(), nearby.getZ(), 40, 0.0, 0.0, 0.0, 0.3, false);
            }
         }
      }

      float knockbackStrength = spiritPoints / 100.0F * 0.5F;
      Vec3 knockbackDirection = entity.getLookAngle().reverse().scale(knockbackStrength);
      entity.setDeltaMovement(knockbackDirection.x, 0.15, knockbackDirection.z);
      entity.hurtMarked = true;
      this.doPostEffects(entity, SpiritPointsManager.getSpiritPoints(entity.getMainHandItem()), level);
      SpiritPointsManager.setSpiritPoints(entity.getMainHandItem(), 0);
      if (entity instanceof Player player) {
         player.displayClientMessage(Component.literal("☠ Soul Fragments: 0").withStyle(ChatFormatting.DARK_AQUA), true);
      }

      float distance = (float)hitResult.distanceTo(entity);
      Vec3 vec3 = entity.getLookAngle().normalize();

      for (int i = 0; i < distance; i++) {
         Vec3 vec32 = vec3.scale(i).add(entity.getEyePosition());
         MagicManager.spawnParticles(level, (ParticleOptions)TOParticles.CURSED_BLAST.get(), vec32.x, vec32.y, vec32.z, 1, 0.0, 0.0, 0.0, 0.0, false);
      }

      MagicManager.spawnParticles(
         level,
         (ParticleOptions)ModParticle.PHANTOM_WING_FLAME.get(),
         hitResult.getLocation().x,
         hitResult.getLocation().y,
         hitResult.getLocation().z,
         50,
         0.0,
         0.0,
         0.0,
         0.2,
         false
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void doPostEffects(LivingEntity entity, int spiritPoints, Level level) {
      if (spiritPoints >= 200) {
         level.playSound(null, entity.blockPosition(), (SoundEvent)TOSounds.BLAST_STAGE_THREE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         ScreenShake_Entity.ScreenShake(level, entity.position(), 30.0F, 0.05F, 10, 20);
      } else if (spiritPoints >= 100) {
         level.playSound(null, entity.blockPosition(), (SoundEvent)TOSounds.BLAST_STAGE_TWO.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         ScreenShake_Entity.ScreenShake(level, entity.position(), 20.0F, 0.035F, 10, 20);
      } else if (spiritPoints >= 0) {
         level.playSound(null, entity.blockPosition(), (SoundEvent)TOSounds.BLAST_STAGE_ONE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         ScreenShake_Entity.ScreenShake(level, entity.position(), 15.0F, 0.02F, 10, 20);
      }
   }

   public static float getRange() {
      return 30.0F;
   }

   private float getDamage() {
      return 2.0F;
   }

   private float getSoulConversionRate(int spellLevel, LivingEntity caster) {
      return 0.35F + this.getSpellPower(spellLevel, caster) * 0.15F;
   }

   private String getDamageText(int spellLevel, LivingEntity caster) {
      float soulConversion = this.getSoulConversionRate(spellLevel, caster);
      if (caster != null) {
         int spiritPoints = SpiritPointsManager.getSpiritPoints(caster.getMainHandItem());
         float baseDamage = this.getDamage();
         String spiritBoost = spiritPoints > 0 ? String.format(" (+%s)", Utils.stringTruncation(spiritPoints * soulConversion, 1)) : "";
         return Utils.stringTruncation(baseDamage + spiritPoints * soulConversion, 1) + spiritBoost;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }

   private String getSoulConversionPercentage(int spellLevel, LivingEntity caster) {
      return String.format("%.1f%%", this.getSoulConversionRate(spellLevel, caster) * 100.0F);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.soul_damage", new Object[]{this.getDamageText(spellLevel, caster)}),
         Component.translatable("ui.traveloptics.soul_conversion_rate", new Object[]{this.getSoulConversionPercentage(spellLevel, caster)}),
         Component.translatable("ui.traveloptics.range", new Object[]{Utils.stringTruncation(getRange(), 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
